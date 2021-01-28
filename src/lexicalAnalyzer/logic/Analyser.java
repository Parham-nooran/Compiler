package lexicalAnalyzer.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Designed to analyse a given file and add its tokens to the tokens list which accepts objects of type Token,
 * determining each objects token type and token value.
 *
 * <p> to use the tokens list we must first call the scan function passing the desired file as its argument. This
 * function would search the provided file and adds its tokens to the tokens list, as it finds the tokens.
 *
 * <p>To identify each token type there are 4 separate lists provided as the instance of the class is initialized.
 * In some cases it just matches the found token with a predefined regex to check its type.
 *
 * <p> Eventually calling the {@code getTokens} function may cause two case to happen. It could return a new copy of
 * the tokens list which might be filled with the tokens identified or only an empty {@code ArrayList}.
 *
 * @see Token
 * @see TokenType
 *
 * @author Parham Nooranbackt
 */
public class Analyser {
    private ArrayList<String> identifiers;
    private ArrayList<String> keywords;
    private ArrayList<String> separators;
    private ArrayList<String> operators;
    private ArrayList<Token> tokens;
    /** Causes a function call to resetFlags after the next call to the {@code} search for token
     * and is set when substring {@code "//"} is read from the output*/
    private boolean slashCommentOn = false;
    /** Causes a function call to resetFlags after the next call to the {@code} search for token
     * and is set when substring {@code "/*"} is read from the output*/
    private boolean starCommentOn = false;
    /** Causes a function call to resetFlags after the next call to the {@code} search for token
     * and is set when char {@code "} is read from the output*/
    private boolean stringOn = false;
    /** Causes a function call to resetFlags after the next call to the {@code} search for token
     * and is set when character {@code '} is read from the output*/
    private boolean charOn = false;

    /**
     * creates an analyser that determines token types as it is defined using the four input lists.
     *
     * @param identifiers
     * The list of identifiers. Determines the predefined identifiers in the origin input language.
     * @param keywords
     * List of predefined keywords in the origin input language.
     * @param separators
     * List of predefined separators in the origin input language.
     * @param operators
     * List of predefined operators in the origin input language.
     */
    public Analyser(ArrayList<String> identifiers, ArrayList<String> keywords, ArrayList<String> separators,
                    ArrayList<String> operators) {
        this.identifiers = identifiers;
        this.keywords = keywords;
        this.separators = separators;
        this.operators = operators;
        this.tokens = new ArrayList<>();
    }

    /**
     * Scans the specified file to the end and adds the found tokens to the list of analyser tokens.
     * In case of successfully scanning the whole file the read-head of the bufferedReader stops on the
     * end character and finally it closes it's opened resources.
     * @param file
     * the file that the method scans and adds its tokens to the tokens list.
     */
    public void scan(File file){
        try (
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            bufferedReader.mark(1);
            while (bufferedReader.read() != -1) {
                bufferedReader.reset();
                tokens.add(getNextToken(bufferedReader));
                bufferedReader.mark(1);
            }
        } catch (IOException e){
            System.out.println("Something went wrong while initializing the fileReader");
            e.printStackTrace();
        }
    }

    /**
     * Starts reading the file character by character from where the bufferedReader read-head
     * points to and returns the next token specified. In order to specify the next token
     * it calls the searchForToken method.
     * In case of a failure it returns null.
     * @param bufferedReader
     * the reader that is passed to the {@code searchForToken} method. as specified above the search
     * operation continues from where ever the BufferReader read-head points to.
     * @return returns the next token
     */
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

    /**
     * Searches for the next token starting from the point that the  BufferReader points to.
     * @param bufferedReader
     * the search operation continues from where ever the BufferReader read-head points to and the input characters
     * would be read from the specified BufferedReader.
     * @return
     * it returns the next token found in the file.
     * @throws IOException
     * If an I/O error occurs
     *
     */
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

    /**
     * Specifies the token type of the input String. It should be called after making sure that the input string
     * is not a separator or an operator. The token returned could only be one of the types keyword, constants,
     * identifiers, literals or undefined.
     * @implNote Separators and operators could not be specified using this function.
     * @param input
     * the String that that the method determines its type.
     * @return
     * returns the token corresponding to the input string as specified above.
     */
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

    /**
     * It starts from the BufferReader read-head and reads the input buffet and stores the read characters
     * int a temporary string until it reaches one of the possible set of characters. Depending on that which flag
     * is on it may stop scanning the input buffer reaching the new line character, "&#42;&#47;", "&#47;&#47;", " or '.
     * <p> If the input buffer ends before reaching any of the specified set of characters the read string would be
     * returned
     * If it reaches the escape character it escapes the next input character though it might be the end string
     * for the specified flag and continues scanning the input buffer.
     * @param bufferedReader
     * the search operation continues from where ever the BufferReader read-head points to and the input characters
     * would be read from the specified BufferedReader.
     * @returnit returns the whole text of a comment, String or character (which is a single character)
     * including the ending set of characters.
     */
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

    /**
     * It first checks the input string specified as temp. If the input string is the starting substring of a numeric
     * literal it reads from the buffer until the end of the number and returns the recognized number.
     * <p>After the execution of this method the read-head of the BufferReader would point to
     * the first character after the specified number.
     * @param bufferedReader
     * the search operation continues from where ever the BufferReader read-head points to and the input characters
     * would be read from the specified BufferedReader.
     * @param temp
     * a string which is a numeric literal that has no power symbols(E or e) in it and could be specified as the
     * starting substring of another numeric literal.
     * @return
     * returns the number literal which temp is the starting substring of it or the temp itself if the next characters
     * won't make a numeric literal with temp.
     * @throws IOException
     * If an I/O error occurs
     */
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

    /**
     * Assuming that bufferedReader is pointing to the starting character of the power part of a numeric literal, the
     * method would return this part otherwise it returns empty string("").
     * <p> After the execution of this function the read-head would point to the end of the specified numeric literal.
     * @param bufferedReader
     * the search operation continues from where ever the BufferReader read-head points to and the input characters
     * would be read from the specified BufferedReader.
     * @return
     * returns empty string if the 4 ahead characters do not match the regex of the power part of a numeric literal
     * otherwise it returns the specified substring.
     * @throws IOException
     * If an I/O error occurs
     */
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

    /**
     * Scans the input buffer starting from its read-head until it reaches the substring specified as the end string.
     * It determined the ending substring depending on the input and the boolean flags of the class.
     * @param bufferedReader
     * the search operation continues from where ever the BufferReader read-head points to and the input characters
     * would be read from the specified BufferedReader.
     * @implNote if characters of the end substring are escaped the returning string would not end there as expected.
     * @param end
     * the substring specified as the end string.
     * @return
     * returns the read string as specified above including the ending substring.
     * @throws IOException
     * If an I/O error occurs
     */
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

    /**
     * Depending on which flag is set, it may return a comment, string, character or an undefined literal.
     * call to this method must be after insuring that the input String is the starting substring of one of the
     * literal types specified and the read-head of the BufferReader is pointing to the next character of the
     * input string.
     * @param bufferedReader
     * the search operation continues from where ever the BufferReader read-head points to and the input characters
     * would be read from the specified BufferedReader.
     * @param input
     * the read substring which is assured to be the starting substring of one of the literal types specified.
     * @return
     * returns a token with literal or undefined token type depending on the input string.
     */
    private Token resetFlags(BufferedReader bufferedReader, String input){
        String temp;
        return new Token((temp = getCommentOrString(bufferedReader)).endsWith(stringOn?"\"":starCommentOn?"*/":
                slashCommentOn?"\n":charOn?"'":"")?input.startsWith("/")?TokenType.COMMENT:TokenType.LITERAL:
                TokenType.UNDEFINED, input+temp);
    }

    /**
     * Checks the input string to see if it is the starting substring of a String, comment or character.
     * @param input
     * the string that is checked as specified.
     * @return
     * if any of the flags is set it returns true and false if non of the flags is set.
     */
    private boolean setFlags(String input){
        stringOn = input.startsWith("\"");
        slashCommentOn = input.startsWith("//");
        starCommentOn = input.startsWith("/*");
        charOn = input.startsWith("'");
        return (stringOn||slashCommentOn||starCommentOn||charOn);
    }

    /**
     * The input string, temp, could be the starting substring of an operator, string or a comment literal. It finds
     * the temp token type and reads from the buffer as much as it's needed.
     * @param bufferedReader
     * the search operation continues from where ever the BufferReader read-head points to and the input characters
     * would be read from the specified BufferedReader.
     * @param temp
     *the string to be checked and added to in the case of necessity.
     * @return
     *returns a multi character operator or a comment or null if non of the conditions stands.
     * @throws IOException
     * If an I/O error occurs
     */
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

    /**
     * Checks different conditions on string temp and uses the BufferedReader in case of necessity. The
     * input string temp might be the starting substring of a numeric literal, an operator or a separator.
     * @param bufferedReader
     * the search operation continues from where ever the BufferReader read-head points to and the input characters
     * would be read from the specified BufferedReader.
     * @param temp
     * the string to be checked and added to in case of necessity.
     * @param input
     * the first character before the read-head of the bufferedReader.
     * @return
     * returns an annotation, a numeric literal, a separator, an operator or an undefined token as described above.
     * @throws IOException
     * If an I/O error occurs
     */
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

    /**
     * Assuming that bufferedReader's head is pointing to the starting character of an annotation after the '@'
     * character it returns the annotation including the annotation mark.
     * IF the annotation string heats a separator, comment, string or character starting character it stops adding it
     * to the result string and puts the read-head back to where it started.
     * @param bufferedReader
     * the search operation continues from where ever the BufferReader read-head points to and the input characters
     * would be read from the specified BufferedReader.
     * @return
     * returns the annotation string after the '@' mark.
     * @throws IOException
     * If an I/O error occurs
     */
    private String getAnnotation(BufferedReader bufferedReader) throws IOException {
        String temp = "";
        int input;
        bufferedReader.mark(1);
        while ((input = bufferedReader.read())!=-1&& !(separators.contains((char)input+"")||
                operators.contains((char)input+"")||((char)input+"").matches("\\s")||
                ((char)input+"").matches("[\"']")/*||!(temp+(char)input).matches("^[A-Z]+")*/)){
            temp+=(char)input;
            bufferedReader.mark(1);
        }
        bufferedReader.reset();
        return "@"+temp;
    }

    /**
     * Checks whether the bufferedReader's head is pointing to the start of a numeric literal or not and returns true if
     * it is and false otherwise.
     * @param bufferedReader
     * the search operation continues from where ever the BufferReader read-head points to and the input characters
     * would be read from the specified BufferedReader.
     * @return
     * returns true if the bufferedReader's head is pointing to the start of a numeric literal and false otherwise.
     * @throws IOException
     * If an I/O error occurs
     */
    private boolean checkNumber(BufferedReader bufferedReader) throws IOException {
        boolean result;
        bufferedReader.mark(2);
        int input = (char)bufferedReader.read();
        result = ((char)input+"").matches("\\d")||((char)input =='.'&&((char)bufferedReader.read()+"")
                .matches("\\d"));
        bufferedReader.reset();
        return result;
    }

    /**
     * Checks whether temp is the starting substring of a character, string, comment literal or part of a separator
     * and returns null in case it is non of the above.
     * @param bufferedReader
     * the search operation continues from where ever the BufferReader read-head points to and the input characters
     * would be read from the specified BufferedReader.
     * @param temp
     *the string to be checked and added to in the case of necessity.
     * @param input
     * the character before the point that BufferedReader's head points to.
     * @return
     * returns a comment, separator or null as described above.
     */
    private Token checkSeparator(BufferedReader bufferedReader, String temp, int input){
        if (setFlags(temp)) {
            return resetFlags(bufferedReader, temp);
        }
        if (separators.contains(temp)) {
            return new Token(TokenType.SEPARATOR, (char)input+"");
        }
        return null;
    }

    /**
     * It checks if the input string specified as temp matches any characters that could be the first character of
     * a multi-character operator and in this case calls a method to return the multi-char operator. Otherwise it
     * checks whether or not the input string is an operator or not and in case of accordance it returns the
     * corresponding token otherwise it returns null.
     * @param bufferedReader
     * the reader which in case of a multi-character operator it reads from to specify the operator.
     * @param temp
     * the string that it analyses to find out whether is an operator or a multi-char operator.
     * @return
     * returns a Token in case of accordance as specified or null otherwise.
     * @throws IOException
     * If an I/O error occurs
     */
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

    /**
     * Returns a copy of the token list.
     * @return
     * returns a copy of the token list.
     */
    public ArrayList<Token> getTokens() {
        return new ArrayList<>(tokens);
    }
}
/*

!input.trim().equals(input)?input.trim():
 input.endsWith(";")
 */