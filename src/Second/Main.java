package Second;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    private static ArrayList<String> identifiers = new ArrayList<>(Arrays.asList("permits", "record", "sealed",
            "var", "yield"));
    private static ArrayList<String> keywords = new ArrayList<>(Arrays.asList("abstract", "assert", "boolean", "break",
            "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum",
            "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "int", "instanceof",
            "interface", "long", "native", "new", "non-sealed", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while", "_"));
    private static ArrayList<String> separators = new ArrayList<>(Arrays.asList(";", ",", ".", "(", ")", "[", "]", "{",
            "}"));//, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
    private static ArrayList<String> operators = new ArrayList<>(Arrays.asList("+", "-", "*", "/", "=", "<", ">",
            "^", "<=", ">=", "+=", "-=", "/=", "*=", "%=", "^=", "<<=", ">>=", ">>>>=", "", "!=", "==", "|=", "&=",
            "%", "|", "&", "||", "&&", "++", "--", "<<", ">>", "?", "!", ":"));
    //private static ArrayList<String> literals = new ArrayList<>(Arrays.asList("", "", "", "", "", "", "", "", ""));
    private static ArrayList<String> comments = new ArrayList<>(Arrays.asList("/*", "*/", "//"));

    public static void main(String[] args) {
        File file = new File("E:\\test.txt");
        if(!file.exists()){
            System.out.println("There is no such a file in the path identified");
        }
        Analyser analyzer = new Analyser(identifiers, keywords, separators, operators,
                comments, file);
        analyzer.analyse();
    }
}






