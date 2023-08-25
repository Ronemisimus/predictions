package console;

import console.dto.collector.EnvCollector;
import console.dto.presenter.*;
import console.menu.MenuManager;
import console.menu.menu.EntityMenu;
import console.menu.menu.HistoryDisplayMenu;
import console.menu.menu.HistoryMenu;
import console.menu.menu.PropertyMenu;
import console.menu.option.MenuItem;
import dto.*;
import dto.subdto.InitializeDto;
import dto.subdto.SingleRunHistoryDto;
import dto.subdto.show.EntityListDto;
import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.PropertyDto;
import predictions.MainApi;
import predictions.MainApiImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EngineApi  {

    private final MainApi api;
    private static EngineApi instance;

    private EngineApi()
    {
        api = new MainApiImpl();
    }
    public DTOPresenter readFile(String file)
    {
        ReadFileDto res = api.readFile(file);
        return new ReadFilePresenter(res);
    }

    public DTOPresenter showLoadedWorld()
    {
        ShowWorldDto dto = api.showLoadedWorld();
        return new ShowWorldPresenter(dto);
    }

    public DTOPresenter runSimulation()
    {
        setEnv();
        InitializeDto initDto = api.initialize();
        System.out.println(new InitializePresenter(initDto));
        RunSimulationDto dto = api.runSimulation();
        return new RunSimulationPresenter(dto);
    }


    private void setEnv()
    {
        EnvDto dto = api.getEnv();
        EnvCollector collector = new EnvCollector(dto);
        collector.run();
        collector.getEnv().getEnvironment().stream()
                .map(PropertyDto::getName)
                .forEach(name -> api.setEnv(name,collector.getValue(name)));
    }

    public static EngineApi getInstance()
    {
        if(instance == null)
        {
            instance = new EngineApi();
        }
        return instance;
    }

    public DTOPresenter showPreviousRuns() {
        RunHistoryDto hist = api.getRunHistory();
        List<MenuItem> options = new ArrayList<>();
        final Map.Entry<Integer, LocalDateTime>[] chosenRun = new Map.Entry[]{null};
        final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm:ss");
        hist.getRunList().forEach((k,v) -> options.add(new MenuItem() {
            @Override
            public boolean run() {
                chosenRun[0] = new SimpleEntry<>(k,v);
                return true;
            }
            @Override
            public String toString() {
                return "Run unique id: " + k + " ran at time: " + format.format(v);
            }
        }));
        List<Boolean> canCloseMEnu = IntStream.range(0, options.size()).mapToObj(i -> true).collect(Collectors.toList());
        MenuManager historyMenu = new HistoryMenu(options, canCloseMEnu);
        historyMenu.run(false);
        HistoryDisplayMenu menu = new HistoryDisplayMenu();
        ((MenuManager)menu).run(false);
        Integer displayOption = menu.getChoice();
        SingleRunHistoryDto res;
        if (displayOption ==1)
        {
            res = api.getRunEntityCounts(chosenRun[0].getKey());
            return new RunHistoryPresenter(res);
        }
        else
        {
            EntityListDto entList = api.getEntityList(chosenRun[0].getKey());
            EntityMenu entityMenu = EntityMenu.getInstance(entList);
            entityMenu.run(false);
            EntityDto ent = entityMenu.chosenEntity();
            PropertyMenu propertyMenu = PropertyMenu.getInstance(ent.getProps());
            propertyMenu.run(false);
            PropertyDto prop = propertyMenu.chosenProperty();
            res = api.getRunPropertyHistogram(chosenRun[0].getKey(), ent.getName(), prop.getName());
            return new RunHistoryPresenter(res, ent, prop);
        }
    }
}
