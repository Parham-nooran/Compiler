package firstAndFollow;

import java.io.File;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws NotContextFreeException {
        File file1 = new File("E:\\F&FExample1.txt");
        File file2 = new File("E:\\F&FExample2.txt");
        File file3 = new File("E:\\F&FExample3.txt");
        File file4 = new File("E:\\F&FExample4.txt");
        Computer computer = new Computer(file4);
        computer.computeFirst();
        System.out.println("Non terminal"+"          First");
        for(String nonTerminal:computer.getFirsts().keySet()){
            System.out.print(nonTerminal + "  :               ");
            for(String terminal:computer.getFirsts().get(nonTerminal)) {
                System.out.print(terminal+"   ");
            }
            System.out.println();
        }
    }
}
