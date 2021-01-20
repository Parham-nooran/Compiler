package Second;

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
    //private ArrayList<String> literals;
    private ArrayList<String> comments;
    private File file;
    private boolean slashCommentOn = false;
    private boolean starCommentOn = false;
    private boolean stringOn = false;
    private boolean charOn = false;

    public Analyser(ArrayList<String> identifiers, ArrayList<String> keywords, ArrayList<String> separators,
                    ArrayList<String> operator, ArrayList<String> comments, File file) {
        this.identifiers = identifiers;
        this.keywords = keywords;
        this.separators = separators;
        this.operators = operator;
        this.comments = comments;
        this.file = file;
    }

    public void analyse(){
        scan(this.file);
    }
    private void scan(File file){
        try (
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            while(true) {
                bufferedReader.mark(1);
                if(bufferedReader.read()==-1){
                    break;
                }
                bufferedReader.reset();
                Token token = getNextToken(bufferedReader);
                String tokenType = token.getTokenType().toString().toLowerCase();
                System.out.println(token.getTokenValue()+" is "+(tokenType.substring(0, 1).
                        matches("[iIoOaAuUeE]")?"an ":"a ") +tokenType);
            }
        } catch (IOException e){
            System.out.println("Something went wrong while initializing the fileReader");
            e.printStackTrace();
        }
    }
    private Token getNextToken(BufferedReader bufferedReader) {
        int input;
        String temp = "";
        Token token;
        try{
            while (true) {
                if(slashCommentOn||starCommentOn||stringOn||charOn) {
                    token = resetFlags(bufferedReader, temp);
                    return token;
                }

                bufferedReader.mark(4);
                if((input = bufferedReader.read()) == -1){
                    break;
                }
                if(((char)input+"").matches("\\s+")){
                    if(temp.equals("")) {
                        continue;
                    } else{
                        break;
                    }
                }
                temp += (char) input;
                //System.out.println("Temp is "+ temp);
                if (separators.contains(temp)) {
                    return new Token(TokenType.SEPARATOR, (char)input+"");
                }

                if (!temp.equals(setFlags(temp))) {
                    temp = setFlags(temp);
                    continue;
                }
                if((!temp.equals(""))&&separators.contains((char)input+"")){
                    bufferedReader.reset();
                    temp = temp.substring(0, temp.length()-1);
                    break;
                }
                if(temp.matches("^[-+]?\\d*\\.?\\d+([eE][-+]\\d+)?$")){
                    temp = getTheNumber(bufferedReader, temp);
                    return new Token(TokenType.LITERAL, temp);
                }
                if((temp).matches("[-!%*^<>+/|&]")){// */;
                    bufferedReader.mark(4);
                    temp+=((char)bufferedReader.read());
                    if (!temp.equals(setFlags(temp))) {
                        temp = setFlags(temp);
                        continue;
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
                    temp = temp.substring(0, temp.length()-1);
                    bufferedReader.reset();
                }

                if(operators.contains(temp)){
                    return new Token(TokenType.OPERATOR, temp);
                }

                if((!temp.equals(""))&&operators.contains((char)input+"")){
                    bufferedReader.reset();
                    temp = temp.substring(0, temp.length()-1);
                    break;
                }

            }
        } catch (IOException e){
            System.out.println("Something went wrong while scanning the file");
            e.printStackTrace();
        }
        return getToken(temp);
    }
    private Token getToken(String input){
        if(keywords.contains(input)){
            return new Token(TokenType.KEYWORD, input);
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
        String temp;
        try {
            if (slashCommentOn) {
                slashCommentOn = !(temp = getTheText(bufferedReader, "\n")).endsWith("\n");
                return "//"+temp;
            }
            if (starCommentOn) {
                starCommentOn = !(temp = getTheText(bufferedReader, "*/")).endsWith("*/");
                return "/*"+temp;
            }
            if (stringOn) {
                temp = getTheText(bufferedReader, "\"");
                stringOn = false;//!(temp = getTheText(fileReader, "\"")).endsWith("\"");
                return "\""+temp;
            }
            if(charOn){
                temp = getTheText(bufferedReader, "'");
                charOn = false;//!((temp = getTheText(fileReader, "'")).endsWith("'"));
                return "'"+temp;
            }
        } catch (IOException e) {
            System.out.println();
        }
        return null;
    }
    private String getTheNumber(BufferedReader bufferedReader, String temp) throws IOException {
        int input;
        bufferedReader.mark(1);
        while((temp+=((char)bufferedReader.read())).matches("^[-+]?\\d*\\.?\\d*")){
            //System.out.println(temp+"why1");
            bufferedReader.mark(1);
        }
        temp = temp.substring(0, temp.length()-1);
        bufferedReader.reset();
        bufferedReader.mark(5);
        int count = 0;
        String test = "";
        while((count<=4)&&!(test+=((char)bufferedReader.read())).
                matches("^[eE][-+]?\\d+$")){
            //System.out.println(test+" test why3");
            count++;
        }
        if(test.matches("^[eE][-+]?\\d+$")){
            //System.out.println(test+" test why4");
            temp+=test;
            bufferedReader.mark(1);
            while((((char)(input = bufferedReader.read()))+"").matches("\\d")){
                temp+= input;
                bufferedReader.mark(1);
            }
            bufferedReader.reset();
        } else{
            bufferedReader.reset();
        }
        return temp;
    }
    private String getTheText(BufferedReader bufferedReader, String end) throws IOException {
        int input;
        String temp = "";
        bufferedReader.mark(2);
        while (((input = bufferedReader.read())!=-1) &&
                !(temp.endsWith(end)||(temp.endsWith("\n")&&(stringOn||charOn)))) {
            temp+=(char)input;
            bufferedReader.mark(2);
        }
        bufferedReader.reset();
        return temp;
    }
    private Token resetFlags(BufferedReader bufferedReader, String input){
        String temp;
        if(!(temp = getCommentOrString(bufferedReader)).endsWith(stringOn?"\"":starCommentOn?"*/":
                slashCommentOn?"\n":charOn?"'":"")) {
            return new Token(TokenType.UNDEFINED, input+temp);
        }
        return new Token(input.startsWith("/")?TokenType.COMMENT:TokenType.LITERAL, input+temp);
    }
    private String setFlags(String input){
        stringOn = input.startsWith("\"");
        slashCommentOn = input.startsWith("//");
        starCommentOn = input.startsWith("/*");
        charOn = input.startsWith("'");
        return (stringOn||slashCommentOn||starCommentOn||charOn)?
                input.replaceAll("(//)|(/\\*)|\"|'",""):input;
    }
}
/*

!input.trim().equals(input)?input.trim():
 input.endsWith(";")
 */