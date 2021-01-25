package lexicalAnalyzer;

import lexicalAnalyzer.logic.Analyser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    private static File file;
    private static Analyser analyzer;
    public Main(){
        ArrayList<String> identifiers = new ArrayList<>(Arrays.asList("permits", "record", "sealed",
                "var", "yield"));
        ArrayList<String> keywords = new ArrayList<>(Arrays.asList("abstract", "assert", "boolean", "break",
                "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double",
                "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements",
                "import", "int", "instanceof", "interface", "long", "native", "new", "non-sealed", "package",
                "private", "protected", "public", "return", "short", "static", "strictfp", "super",
                "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while", "_"));
        ArrayList<String> separators = new ArrayList<>(Arrays.asList(";", ",", ".", "(", ")", "[", "]", "{", "}"));
        ArrayList<String> operators = new ArrayList<>(Arrays.asList("+", "-", "*", "/", "=", "<", ">",
                "^", "<=", ">=", "+=", "-=", "/=", "*=", "%=", "^=", "<<=", ">>=", ">>>=", "", "!=", "==", "|=", "&=",
                "%", "|", "&", "||", "&&", "++", "--", "<<", ">>", "?", "!", ":", "~"));
        file = new File("E:\\test.txt");
        analyzer = new Analyser(identifiers, keywords, separators, operators, file);
    }
    public static void main(String[] args) {
        start();
    }
    public static void start(){
        if(!file.exists()){
            System.out.println("There is no such a file in the path identified");
        }
        analyzer.scan(file);
    }

    public static Analyser getAnalyzer() {
        return analyzer;
    }

    public File getFile() {
        return file;
    }
}






