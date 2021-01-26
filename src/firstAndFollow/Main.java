package firstAndFollow;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        File file = getTheFile();
        String startingSymbol = getTheStartingSymbol(file);
        Computer computer = startComputer(file, startingSymbol);
        System.out.println("Non terminal"+"          First");
        for(String nonTerminal:computer.getFirsts().keySet()){
            System.out.print(nonTerminal + "  :               ");
            for(String terminal:computer.getFirsts().get(nonTerminal)) {
                System.out.print(terminal+"   ");
            }
            System.out.println();
        }
        System.out.println("\n\n");
        System.out.println("Non terminal"+"          follow");
        for(String nonTerminal:computer.getFollows().keySet()){
            System.out.print(nonTerminal + "  :               ");
            for(String terminal:computer.getFollows().get(nonTerminal)) {
                System.out.print(terminal+"   ");
            }
            System.out.println();
        }
    }

    private static File getTheFile(){
        Scanner scanner = new Scanner(System.in);
        String fileNumber;
        System.out.println("Enter the file number [1:4]");
        while (!(fileNumber = scanner.next()).matches("[1-4]")) {
            System.out.println("Enter the file number [1:4]");
        }
        return new File("E:\\F&FExample"+fileNumber+".txt");
    }

    private static String getTheStartingSymbol(File file){
        Scanner newScanner = null;
        try {
            newScanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert newScanner != null;
        return newScanner.next();
    }

    private static Computer startComputer(File file, String startingSymbol){
        Computer computer = null;
        try {
            computer = new Computer(file);
        } catch (NotContextFreeException e) {
            System.out.println(e.getMessage());
        }
        computer.computeFirst();
        computer.computeFollow(startingSymbol);
        return computer;
    }

}
