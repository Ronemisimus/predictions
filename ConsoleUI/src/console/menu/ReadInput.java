package console.menu;

import java.util.Scanner;


public class ReadInput {

    private static final Scanner scanner;
    static {
        scanner = new Scanner(System.in);
    }
    public static String getString()
    {
        return scanner.nextLine();
    }
}
