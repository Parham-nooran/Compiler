package lexicalAnalyzer.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Analyser {
    private ArrayList<String> identifiers;
    private ArrayList<String> keywords;
    private ArrayList<String> separators;
    private ArrayList<String> operators;
    private File file;
    private boolean slashCommentOn = false;
    private boolean starCommentOn = false;
    private boolean stringOn = false;
    private boolean charOn = false;

    public Analyser(ArrayList<String> identifiers, ArrayList<String> keywords, ArrayList<String> separators,
                    ArrayList<String> operator, File file) {
        this.identifiers = identifiers;
        this.keywords = keywords;
        this.separators = separators;
        this.operators = operator;
        this.file = file;
    }

    public void scan(File file){
        try (
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            bufferedReader.mark(1);
            while (bufferedReader.read() != -1) {
                bufferedReader.reset();
                Token token = getNextToken(bufferedReader);
                assert token != null;
                String tokenType = token.getTokenType().toString().toLowerCase();
                System.out.println(token.getTokenValue()+" is "+tokenType);//(tokenType.substring(0, 1).matches("[iIoOaAuUeE]")?"an ":"a ") +
                bufferedReader.mark(1);
            }
        } catch (IOException e){
            System.out.println("Something went wrong while initializing the fileReader");
            e.printStackTrace();
        }
    }
    public Token getNextToken(BufferedReader bufferedReader) {
        try{
            bufferedReader.mark(4);
            return searchForToken(bufferedReader);
        } catch (IOException e){
            System.out.println("Something went wrong while scanning the file");
            e.printStackTrace();
        }
        return null;
    }
    private Token searchForToken(BufferedReader bufferedReader) throws IOException {
        int input;
        String tokenValue = "";
        Token token;
        while ((input = bufferedReader.read()) != -1) {
            if(slashCommentOn||starCommentOn||stringOn||charOn)
                return resetFlags(bufferedReader, tokenValue);
            if(((char)input+"").matches("\\s+")){
                if(tokenValue.equals(""))
                    continue;
                break;
            }
            if((token = checkConditions(bufferedReader, tokenValue, input))!=null&&!(token.getTokenType().
                    equals(TokenType.UNDEFINED)))
                return token;
            if(token==null)
                break;
            tokenValue = token.getTokenValue();
            bufferedReader.mark(4);
        }
        return getToken(tokenValue);
    }
    private Token getToken(String input){
        if(keywords.contains(input)){
            return new Token(TokenType.KEYWORD, input);
        }
        if(input.matches("^[A-Z]+$")){
            return new Token(TokenType.CONSTANT, input);
        }
        if(identifiers.contains(input)||input.matches("^[a-zA-Z$_]+[a-zA-Z$_]*$")){
            return new Token(TokenType.IDENTIFIER, input);
        }
        if(input.matches("null|true|false|^[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?$")){
            return new Token(TokenType.LITERAL, input);
        }
        return new Token(TokenType.UNDEFINED, input);
    }

    private String getCommentOrString(BufferedReader bufferedReader){
        String temp = "";
        try {
            if (slashCommentOn) {
                slashCommentOn = !(temp = getTheText(bufferedReader, "\n")).endsWith("\n");
            }
            if (starCommentOn) {
                starCommentOn = !(temp = getTheText(bufferedReader, "*/")).endsWith("*/");
            }
            if (stringOn) {
                stringOn = !(temp = getTheText(bufferedReader, "\"")).endsWith("\"");
            }
            if(charOn){
                charOn = !((temp = getTheText(bufferedReader, "'")).endsWith("'"));
            }
        } catch (IOException e) {
            System.out.println();
        }
        return temp;
    }
    private String getTheNumber(BufferedReader bufferedReader, String temp) throws IOException {
        bufferedReader.mark(1);
        while((temp+=((char)bufferedReader.read())).matches("^[-+]?\\d*(\\.)?\\d*$")){
            bufferedReader.mark(1);
        }
        temp = temp.substring(0, temp.length()-1);
        bufferedReader.reset();
        temp+=getThePowerPart(bufferedReader);
        return temp;
    }
    private String getThePowerPart(BufferedReader bufferedReader) throws IOException {
        int input;
        String temp = "";
        bufferedReader.mark(5);
        int count = 0;
        String test = "";
        while((count<=4)&&!(test+=((char)bufferedReader.read())).
                matches("^[eE][-+]?\\d+$")){
            count++;
        }
        if(test.matches("^[eE][-+]?\\d+$")){
            temp+=test;
            bufferedReader.mark(1);
            while((((char)(input = bufferedReader.read()))+"").matches("\\d")){
                temp+= (char)input;
                bufferedReader.mark(1);
            }
        }
        bufferedReader.reset();
        return temp;
    }
    private String getTheText(BufferedReader bufferedReader, String end) throws IOException {
        int input;
        String temp = "";
        bufferedReader.mark(2);
        while (((input = bufferedReader.read())!=-1) &&
                !((temp.endsWith(end)&&!(temp.endsWith("\\"+end)))||temp.endsWith("\\\\"+end)
                        ||(temp.endsWith("\n")&&(stringOn||charOn)))) {
            temp+=(char)input;
            bufferedReader.mark(2);
        }
        bufferedReader.reset();
        return temp;
    }
    private Token resetFlags(BufferedReader bufferedReader, String input){
        String temp;
        return new Token((temp = getCommentOrString(bufferedReader)).endsWith(stringOn?"\"":starCommentOn?"*/":
                slashCommentOn?"\n":charOn?"'":"")?input.startsWith("/")?TokenType.COMMENT:TokenType.LITERAL:
                TokenType.UNDEFINED, input+temp);
    }
    private boolean setFlags(String input){
        stringOn = input.startsWith("\"");
        slashCommentOn = input.startsWith("//");
        starCommentOn = input.startsWith("/*");
        charOn = input.startsWith("'");
        return (stringOn||slashCommentOn||starCommentOn||charOn);
    }
    private Token multiCharOperator(BufferedReader bufferedReader, String temp) throws IOException {
        bufferedReader.mark(4);
        temp+=((char)bufferedReader.read());
        if (setFlags(temp)) {
            return resetFlags(bufferedReader, temp);
        }
        if(temp.matches("<<|>>")){
            bufferedReader.mark(2);
            temp+=((char)bufferedReader.read());
            if(operators.contains(temp)){
                return new Token(TokenType.OPERATOR, temp);
            }
            temp = temp.substring(0, temp.length()-1);
            bufferedReader.reset();
        }
        if(operators.contains(temp)){
            return new Token(TokenType.OPERATOR, temp);
        }
        bufferedReader.reset();
        return null;
    }
    private Token checkConditions(BufferedReader bufferedReader, String temp, int input) throws IOException {
        Token token;
        temp += (char) input;
        if(temp.equals("@")){
            return new Token(TokenType.ANNOTATION, getAnnotation(bufferedReader));
        }
        if(temp.matches("^\\d+$")||(temp.matches("^[-+]?\\d*\\.?$")&&checkNumber(bufferedReader))){
            return new Token(TokenType.LITERAL, getTheNumber(bufferedReader, temp));
        }
        if((token = checkSeparator(bufferedReader, temp, input))!=null){
            return token;
        }
        if((!temp.equals(""))&&separators.contains((char)input+"")){
            bufferedReader.reset();
            return null;
        }
        if((token = checkOperator(bufferedReader, temp))!=null){
            return token;
        }
        if((!temp.equals(""))&&operators.contains((char)input+"")){
            bufferedReader.reset();
            return null;
        }
        return new Token(TokenType.UNDEFINED, temp);
    }
    private String getAnnotation(BufferedReader bufferedReader) throws IOException {
        String temp = "";
        int input;
        bufferedReader.mark(1);
        while ((input = bufferedReader.read())!=-1&&!((char)input+"").matches("\\s")){
            temp+=(char)input;
            bufferedReader.mark(1);
        }
        bufferedReader.reset();
        return "@"+temp;
    }
    private boolean checkNumber(BufferedReader bufferedReader) throws IOException {
        boolean result;
        bufferedReader.mark(2);
        int input = (char)bufferedReader.read();
        result = ((char)input+"").matches("\\d")||((char)input =='.'&&((char)bufferedReader.read()+"")
                .matches("\\d"));
        bufferedReader.reset();
        return result;
    }
    private Token checkSeparator(BufferedReader bufferedReader, String temp, int input){
        if (setFlags(temp)) {
            return resetFlags(bufferedReader, temp);
        }
        if (separators.contains(temp)) {
            return new Token(TokenType.SEPARATOR, (char)input+"");
        }
        return null;
    }
    private Token checkOperator(BufferedReader bufferedReader, String temp) throws IOException {
        Token token;
        if((temp).matches("[-!%*^<>+/|&]")&&
                (token = multiCharOperator(bufferedReader, temp))!=null){// */;
            return token;
        }
        if(operators.contains(temp)){
            return new Token(TokenType.OPERATOR, temp);
        }
        return null;
    }
}
/*

!input.trim().equals(input)?input.trim():
 input.endsWith(";")
 */