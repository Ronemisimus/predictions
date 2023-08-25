package console.menu;

import console.menu.option.MenuItem;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class MenuManagerImpl implements MenuManager{

    private final List<MenuItem> options;
    private final List<Boolean> canCloseMEnu;

    private boolean exited;
    private boolean menuOpen;
    private final Scanner scanner;

    protected MenuManagerImpl(List<MenuItem> options, List<Boolean> canCloseMEnu){
        this.options = options;
        this.canCloseMEnu = canCloseMEnu;
        this.menuOpen = true;
        this.exited = false;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void runOption(int option) {
        option = option - 1;
        if(option >= 0 && option < options.size()){
            boolean completed = options.get(option).run();
            menuOpen = !canCloseMEnu.get(option) || !completed;
        }
        exited = exited || option == options.size()-1;
    }

    @Override
    public boolean stayOpen() {
        return menuOpen;
    }

    @Override
    public int getOption() {
        int res = -1;
        while (res < 0 && res < options.size()){
            System.out.print(this);
            try {
                res = scanner.nextInt();
            } catch (Exception e) {
                scanner.nextLine();
            }
        }
        return res;
    }

    @Override
    public boolean run(boolean exited) {
        this.exited = exited || this.exited;
        if(!this.exited) {
            while (stayOpen()) {
                runOption(getOption());
            }
        }
        return this.exited;
    }

    @Override
    public String toString() {
        StringBuilder prompt = new StringBuilder("Please enter a number to the left of the option you want:\n");
        prompt.append("(a number from this list: [").append(IntStream.range(1, options.size() + 1).mapToObj(String::valueOf).collect(Collectors.joining(", "))).append("])\n");
        for (int i = 0; i < options.size(); i++) {
            prompt.append((i + 1)).append("\t: ").append(options.get(i).toString()).append("\n");
        }
        return prompt.toString();
    }

    public List<MenuItem> getOptions() {
        return options;
    }
}
