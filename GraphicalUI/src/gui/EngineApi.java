package gui;

import dto.*;
import dto.subdto.InitializeDto;
import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.PropertyDto;
import gui.details.tree.WorldDetailsItem;
import gui.execution.environment.EntityAmountGetter;
import gui.execution.environment.EnvironmentVariableGetter;
import gui.history.display.RunDisplayed;
import gui.readFileError.ReadFileError;
import javafx.beans.property.StringProperty;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import predictions.MainApi;
import predictions.MainApiImpl;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            switch (property.get().getType().toLowerCase())
            {
                case "decimal":
                    api.setEnv(name, Optional.of(Integer.parseInt(text)));
                    break;
                case "float":
                    api.setEnv(name, Optional.of(Double.parseDouble(text)));
                    break;
                case "boolean":
                    Boolean val = text.equals("true")? true : text.equals("false")? false : null;
                    if (val==null) throw new RuntimeException("Invalid boolean value");
                    api.setEnv(name, Optional.of(val));
                    break;
                case "string":
                    api.setEnv(name, Optional.of(text));
                    break;
            }
        }
    }

    public void runSimulation() {
        InitializeDto init = api.initialize();
        RunSimulationDto run = api.runSimulation();
    }

    public List<RunDisplayed> getRunHistory() {
        RunHistoryDto res = api.getRunHistory();
        return res.getRunList().entrySet().stream()
                .map(RunDisplayed::new)
                .collect(Collectors.toList());
    }
}
