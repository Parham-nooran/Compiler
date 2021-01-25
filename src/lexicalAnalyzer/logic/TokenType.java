package lexicalAnalyzer.logic;

public enum TokenType {
    IDENTIFIER, KEYWORD, SEPARATOR, OPERATOR, LITERAL, COMMENT, CONSTANT, ANNOTATION, UNDEFINED;

    public String toString(){
        return this.name().substring(0, 1)+this.name().substring(1).toLowerCase();
    }
}
