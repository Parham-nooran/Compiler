package lexicalAnalyzer.logic;

/**
 * Determines a token object with two properties, token type and token value.
 */
public class Token {
    private TokenType tokenType;
    private String tokenValue;

    /**
     * Creates a token with the specified token type and token value.
     * @param tokenType
     * Token type could be one of the following:
     * IDENTIFIER, KEYWORD, SEPARATOR, OPERATOR, LITERAL, COMMENT, CONSTANT, ANNOTATION, UNDEFINED
     * @see TokenType
     * @param tokenValue
     * Token value is a string.
     */
    public Token(TokenType tokenType, String tokenValue) {
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
    }

    /**
     * Returns the token type.
     * @return
     * Returns the token type.
     */
    public TokenType getTokenType() {
        return tokenType;
    }

    /**
     * Returns the token value.
     * @return
     * returns the token value.
     */
    public String getTokenValue() {
        return tokenValue;
    }
}
