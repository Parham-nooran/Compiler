package Second;

public class Token {
    private TokenType tokenType;
    private String tokenValue;

    public Token(TokenType tokenType, String tokenValue) {
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
    }
    public TokenType getTokenType() {
        return tokenType;
    }
    public String getTokenValue() {
        return tokenValue;
    }
}
