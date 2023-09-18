package gui;

import dto.*;
import dto.subdto.SingleRunHistoryDto;
import dto.subdto.show.EntityListDto;
import dto.subdto.show.instance.RunStateDto;
import dto.subdto.show.interactive.RunProgressDto;
import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.PropertyDto;
import gui.details.tree.WorldDetailsItem;
import gui.execution.environment.EntityAmountGetter;
import gui.execution.environment.EnvironmentVariableGetter;
import gui.history.data.PropertyData;
import gui.history.data.RunState;
import gui.history.display.RunDisplayed;
import gui.readFileError.ReadFileError;
import gui.util.display.RunStateRow;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import predictions.MainApi;
import predictions.MainApiImpl;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EngineApi {

    private final MainApi api;
    private static EngineApi instance;

    private EngineApi()
    {
        api = new MainApiImpl();
    }

    public static EngineApi getInstance()
    {
        if(instance == null)
        {
            instance = new EngineApi();
        }
        return instance;
    }

    public boolean LoadFile(StringProperty fileLabel)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml"));
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.setTitle("Load XML File");
        try {
            String filePath = fileChooser.showOpenDialog(null).getAbsolutePath();
            ReadFileDto res = api.readFile(filePath);
            if (res.isFileLoaded())
            {
                fileLabel.setValue(filePath);
            }
            else
            {
                ReadFileError error = ReadFileError.build(res);
                error.show();
            }
            return res.isFileLoaded();
        }catch (NullPointerException e)
        {
            return false;
        }
    }

    public TreeItem<String> showLoadedWorld()
    {
        ShowWorldDto res = api.showLoadedWorld();
        return new WorldDetailsItem(res);
    }

    public List<EntityAmountGetter> getEntityAmounts()
    {
        List<EntityDto> res = api.getEntityDefinitionCounts();

        return res.stream().map(EntityAmountGetter::new).collect(Collectors.toList());
    }

    public void setEntityAmount(String name, int i) {
        if (i < 0) throw new RuntimeException("Invalid amount");
        api.setEntityAmount(name, i);
    }

    public List<EnvironmentVariableGetter> getEnvironmentVariables() {
        EnvDto res = api.getEnv();
        return res.getEnvironment().stream().map(EnvironmentVariableGetter::new).collect(Collectors.toList());
    }

    public void setEnvironmentVariable(String name, String text) {
        EnvDto env = api.getEnv();
        Optional<PropertyDto> property = env.getEnvironment().stream().filter(e -> e.getName().equals(name)).findFirst();
        if (property.isPresent())
        {
            if (property.get().getFrom()!=null)
            {
                double from, to;
                Comparable<?> value = property.get().getFrom();
                if (value instanceof Integer)
                {
                    from = (double)(int)(Integer)property.get().getFrom();
                    to = (double)(int)(Integer)property.get().getTo();
                }
                else{
                    from = (double)(Double)property.get().getFrom();
                    to = (double)(Double)property.get().getTo();
                }
                double val = Double.parseDouble(text);
                if (val<from || val>to)
                {
                    throw new RuntimeException("Invalid value");
                }
            }
            switch (property.get().getType().toLowerCase())
            {
                case "decimal":
                    Optional.of(Integer.parseInt(text)).ifPresent( val -> api.setEnv(name, val));
                    break;
                case "float":
                    Optional.of(Double.parseDouble(text)).ifPresent( val -> api.setEnv(name, val));
                    break;
                case "boolean":
                    Boolean val = text.equals("true")? Boolean.TRUE : text.equals("false")? Boolean.FALSE : null;
                    if (val==null) throw new RuntimeException("Invalid boolean value");
                    Optional.of(val).ifPresent(v -> api.setEnv(name, v));
                    break;
                case "string":
                    Optional.of(text).ifPresent(v-> api.setEnv(name, v));
                    break;
            }
        }
    }

    public void runSimulation() {
        api.runSimulation();
    }

    public List<RunDisplayed> getRunHistory() {
        RunHistoryDto res = api.getRunHistory();
        return res.getRunList().entrySet().stream()
                .map(e -> new RunDisplayed(e, res.getRunStates().get(e.getKey())))
                .sorted(Comparator.comparing(RunDisplayed::getRunTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public Map<String, Map<Integer, Integer>> getSingleRunHistoryEntityAmount(Integer key) {
        SingleRunHistoryDto res = api.getRunEntityCounts(key);
        Map<String, Map<Integer, Integer>> map = new HashMap<>();
        IntStream.range(0, res.getEntity().size())
                .forEach(i -> {
                    Map<Integer, Integer> entityCounts = res.getCounts().get(i);
                    int max = entityCounts.keySet().stream().max(Comparator.naturalOrder()).orElse(0);
                    entityCounts.put(res.getLatestTick(), entityCounts.get(max));
                    map.put(res.getEntity().get(i), entityCounts);
                });
        return map;
    }

    public void unload() {
        try {
            api.unload();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Map<String, PropertyData>> getSingleRunHistoryPropertyData(Integer runIdentifier) {
        EntityListDto entities = api.getEntityList(runIdentifier);

        return entities.getEntities().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getName(), e.getProps()))
                .map(entry -> new AbstractMap.SimpleEntry<>(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(p-> new AbstractMap.SimpleEntry<>(
                                        p.getName(),
                                        new PropertyData(
                                                entry.getKey(),
                                                p,
                                                api.getRunPropertyHistogram(
                                                        runIdentifier,
                                                        entry.getKey(),
                                                        p.getName()
                                                )
                                        )
                                ))
                                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

    }

    public List<RunStateRow> getRunStates() {
        RunHistoryDto res = api.getRunHistory();
        List<RunStateDto> states = new ArrayList<>(res.getRunStates().values());
        return Arrays.stream(RunState.values())
                .map(run -> new RunStateRow(run.name(), (int) states.stream()
                        .filter(s -> RunState.getRunState(s).equals(run)).count()))
                .collect(Collectors.toList());
    }

    public RunProgressDto getRunProgress(Integer identifier) {
        return api.getRunProgress(identifier);
    }

    public boolean stopSimulation(Integer identifier) {
        return api.stopSimulation(identifier);
    }

    public void pauseSimulation(Integer identifier) {
        api.pauseSimulation(identifier);
    }

    public void resumeSimulation(Integer identifier) {
        api.resumeSimulation(identifier);
    }

    public void reRunSimulation(Integer identifier) {
        api.reRunSimulation(identifier);
    }

    public List<EntityDto> getCurrentEntityAmounts(Integer identifier) {
        EntityListDto res = api.getCurrentEntityAmounts(identifier);
        return res.getEntities();
    }
}
