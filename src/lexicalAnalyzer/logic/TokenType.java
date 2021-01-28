package lexicalAnalyzer.logic;

/**
 * Token types that can be used as the token type part of a token.
 * @see Token
 */
public enum TokenType {
    /**
     * Identifier token type
     */
    IDENTIFIER,
    /**
     * Keyword token type
     */
    KEYWORD,
    /**
     * Separator token type
     */
    SEPARATOR,
    /**
     * Operator token type
     */
    OPERATOR,
    /**
     * Literal token type
     */
    LITERAL,
    /**
     * Comment token type
     */
    COMMENT,
    /**
     * Constant token type
     */
    CONSTANT,
    /**
     * Annotation token type
     */
    ANNOTATION,
    /**
     * Undefined token type
     */
    UNDEFINED;

    /**
     * Returns a name representing the token type.
     * @return
     * Returns a name representing the token type.
     */
    public String toString(){
        return this.name().substring(0, 1)+this.name().substring(1).toLowerCase();
    }
}
