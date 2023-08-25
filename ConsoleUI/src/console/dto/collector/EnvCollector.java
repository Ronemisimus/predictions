package console.dto.collector;

import console.menu.MenuManager;
import console.menu.menu.EnvMenu;
import console.menu.option.ExitOption;
import console.menu.option.MenuItem;
import dto.EnvDto;
import dto.subdto.show.world.PropertyDto;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EnvCollector implements Runnable{

    private final EnvDto env;
    private final List<Comparable<?>> userValue;

    private static final Scanner scanner = new Scanner(System.in);
    public EnvCollector(EnvDto env){
        this.env = env;
        this.userValue = IntStream.range(0,env.getEnvironment().size()).mapToObj(i->(Comparable<?>) null).collect(Collectors.toList());
    }

    @Override
    public void run() {
        List<String> names = env.getEnvironment().stream().map(PropertyDto::getName).collect(Collectors.toList());
        List<MenuItem> options = env.getEnvironment().stream().map(dto -> new MenuItem() {
            @Override
            public boolean run() {
                collectProperty(dto, names.indexOf(dto.getName()));
                return true;
            }

            @Override
            public String toString() {
                return "Enter value for property " + dto.getName() + " of type " + dto.getType();
            }
        }).collect(Collectors.toList());
        options.add(new ExitOption("Done entering values"));
        List<Boolean> canCloseMEnu = IntStream.range(0, options.size()).mapToObj(i -> i==options.size()-1).collect(Collectors.toList());
        MenuManager envInsertion = new EnvMenu(options, canCloseMEnu);
        envInsertion.run(false);
    }

    private void collectProperty(PropertyDto propertyDto, int i) {
        switch (propertyDto.getType().toLowerCase()) {
            case "string":
                userValue.set(i, getStr(propertyDto));
                break;
            case "decimal":
                userValue.set(i, getDec(propertyDto));
                break;
            case "float":
                userValue.set(i, getFloat(propertyDto));
                break;
            case "boolean":
                userValue.set(i, getBool(propertyDto));
                break;
        }
    }

    private Comparable<?> getBool(PropertyDto propertyDto) {
        Boolean res = null;
        while (res == null)
        {
            System.out.println("enter value for property " + propertyDto.getName() + ":");
            System.out.println("(enter true or false exactly as written here: [true,false])");
            try {
                res = scanner.nextBoolean();
            }catch (Exception e){
                scanner.nextLine();
            }
        }
        return res;
    }

    private Comparable<?> getFloat(PropertyDto propertyDto) {
        Double res = null;
        Double from = (Double) propertyDto.getFrom();
        Double to = (Double) propertyDto.getTo();
        String rangeStr = (from==null ? "" : " bigger than: " + from) + (to==null ? "" : " smaller than: " + to);
        while(res == null)
        {
            System.out.println("enter value for property " + propertyDto.getName() + ":");
            System.out.println("(enter a number" + rangeStr + ")");
            try {
                res = scanner.nextDouble();
                if (from != null && to != null &&
                        (res.compareTo(to) > 0 || res.compareTo(from) < 0)) {
                    res = null;
                }
            }catch (Exception e){
                scanner.nextLine();
            }
        }
        return res;
    }

    private Comparable<?> getDec(PropertyDto propertyDto) {
        Integer res = null;
        Integer from = (Integer) propertyDto.getFrom();
        Integer to = (Integer) propertyDto.getTo();
        String rangeStr = (from==null ? "" : " bigger than: " + from) + (to==null ? "" : " smaller than: " + to);
        while(res == null)
        {
            System.out.println("enter value for property " + propertyDto.getName() + ":");
            System.out.println("(enter a whole number" + rangeStr + ")");
            try {
                res = scanner.nextInt();
                if (from != null && to != null &&
                        (res.compareTo(to) > 0 || res.compareTo(from) < 0)) {
                    res = null;
                }
            }catch (Exception e){
                scanner.nextLine();
            }
        }
        return res;
    }

    private Comparable<?> getStr(PropertyDto propertyDto) {
        System.out.println("enter value for property " + propertyDto.getName() + ":");
        System.out.println("(you can enter any string)");
        return scanner.nextLine();
    }

    public Optional<Comparable<?>> getValue(String name){
        List<String> propNames = env.getEnvironment().stream()
                .map(PropertyDto::getName)
                .collect(Collectors.toList());
        return Optional.ofNullable(userValue.get(propNames.indexOf(name)));
    }

    public EnvDto getEnv() {
        return env;
    }
}
