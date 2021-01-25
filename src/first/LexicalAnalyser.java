package first;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LexicalAnalyser {
    private ArrayList<String> identifiers;
    private ArrayList<String> keywords;
    private ArrayList<String> separators;
    private ArrayList<String> operators;
    private ArrayList<String> literals;
    private File file;
    private boolean commentOn = false;

    public LexicalAnalyser(ArrayList<String> identifiers, ArrayList<String> keywords, ArrayList<String> separators,
                           ArrayList<String> operator, ArrayList<String> literals, File file) {
        this.identifiers = identifiers;
        this.keywords = keywords;
        this.separators = separators;
        this.operators = operator;
        this.literals = literals;
        this.file = file;
    }

    public void analyse(){
        scan(file);
    }
    public void scan(File file){
        String token;
        try (
                Scanner scanner = new Scanner(file)
        ) {
            while(scanner.hasNext()){
                token = scanner.next();
                check(token, scanner);
                System.out.print(token);
                System.out.println(" is a ");

                System.out.println(commentOn?"Comment":identify(token));
            }
        } catch (IOException e){
            System.out.println("Something went wrong while trying to read from file");
        }
    }
    private void check(String token, Scanner scanner){
        if(token.contains("/*")) {
            token.replaceFirst("/*", "");
            System.out.println(slashStar(scanner));
        } else if (token.equals("//")){

        }
    }
    private String slashStar(Scanner scanner){
        String comment = "";
        while (scanner.hasNext()){
            String token;
            token = scanner.nextLine();
            comment+=(token+"\n");
            if(token.equals("*/")){
                break;
            }
        }
        return comment;
    }
    private void doubleSlash(){

    }
    private  String identify(String input){
        if(identifiers.contains(input)){
            return "identifier";
        }
        if(keywords.contains(input)){
            return "keyword";
        }
        if(separators.contains(input)){
            return "separator";
        }
        if(operators.contains(input)){
            return "operator";
        }
        if(literals.contains(input)){
            return "literal";
        }
        return null;
    }
}
