package lexicalAnalyzer;

import lexicalAnalyzer.logic.Analyser;
import lexicalAnalyzer.logic.Token;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Initializes four String ArrayLists and a file and an Analyser to scan and obtain the tokens from the specified file.
 */
public class Main {
    /**
     * Identifiers list
     */
    private static ArrayList<String> identifiers = new ArrayList<>(Arrays.asList("permits", "record", "sealed",
                "var", "yield"));
    /**
     * Keywords list
     */
    private static ArrayList<String> keywords = new ArrayList<>(Arrays.asList("abstract", "assert", "boolean", "break",
                "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double",
                "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements",
                "import", "int", "instanceof", "interface", "long", "native", "new", "non-sealed", "package",
                "private", "protected", "public", "return", "short", "static", "strictfp", "super",
                "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while", "_"));
    /**
     * Keywords list
     */
    private static ArrayList<String> separators = new ArrayList<>(Arrays.asList(";", ",", ".", "(", ")",
            "[", "]", "{", "}"));
    /**
     * Keywords list
     */
    private static ArrayList<String> operators = new ArrayList<>(Arrays.asList("+", "-", "*", "/", "=", "<", ">",
                "^", "<=", ">=", "+=", "-=", "/=", "*=", "%=", "^=", "<<=", ">>=", ">>>=", "", "!=", "==", "|=", "&=",
                "%", "|", "&", "||", "&&", "++", "--", "<<", ">>", "?", "!", ":", "~"));
    /**
     * The file that it uses to search for its tokens.
     */
    private static File file = new File("E:\\test.txt");
    /**
     * The analyser that it calls the scan method on its file.
     */
    private static Analyser analyzer = new Analyser(identifiers, keywords, separators, operators);

    /**
     * The static main method of the class. It first has a function call to the static start method and the a call to
     * the print tokens.
     *
     * <p> the start function call is necessary in order to fill the data list of the analyser instance it is using.
     * The {@code printTokens} then would print the tokens that the analyser found.
     *
     * <p> StackOverflow guy: "it's not worth the trouble to document the main function, especially if you are
     * just going to say "The application's main entry point." If someone doesn't know that Main is the application's
     * main entry point, you don't want them anywhere near your code :-)"
     * @param args
     *
     */
    public static void main(String[] args) {
        start();
        printTokens(analyzer.getTokens());
    }

    /**
     * Checks if the file at the specified path exists and then calls the analyser's scan method passing the file
     * object as its argument.
     * @implNote the method is implemented as a static method so it could be called with out the need to instantiate
     * an instance of type Main.
     */
    public static void start(){
        if(!file.exists()){
            System.out.println("There is no such a file in the path identified");
        }
        analyzer.scan(file);
    }

    /**
     * Prints the tokens of the argument list.
     * @param tokens
     * the list which its tokens would be printed out to the stdout.
     */
    private static void printTokens(ArrayList<Token> tokens){
        for(Token token:tokens){
            String tokenType = token.getTokenType().toString().toLowerCase();
            System.out.println(token.getTokenValue()+" is "+tokenType);
        }
    }

    /**
     * Returns the Analyser specified as the analyser property of the method.
     * @return
     * returns the Analyser specified as the analyser property of the method.
     */
    public static Analyser getAnalyzer() {
        return analyzer;
    }

}
