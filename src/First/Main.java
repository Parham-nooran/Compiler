package First;

import java.io.*;
import java.util.ArrayList;

public class Main {
    private static ArrayList<String> identifiers = new ArrayList<>();
    private static ArrayList<String> keywords = new ArrayList<>();
    private static ArrayList<String> separators = new ArrayList<>();
    private static ArrayList<String> operators = new ArrayList<>();
    private static ArrayList<String> literals = new ArrayList<>();
    //private ArrayList<String> comments;

    public static void main(String[] args) {
        File file = new File("E:\\test.txt");
        if(!file.exists()){
            System.out.println("There is no such a file in the path identified");
        }
        LexicalAnalyser lexicalAnalyzer = new LexicalAnalyser(identifiers, keywords, separators, operators,
                literals, file);
        lexicalAnalyzer.analyse();
    }
}
