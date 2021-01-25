package firstAndFollow;

public class RuleSide {
    private String string;
    private Side side;

    public RuleSide(String string, Side side) {
        this.string = string;
        this.side = side;
    }

    public String[] getSymbols() throws NotContextFreeException {
        return side.equals(Side.LEFT)?getSymbolsLeft():getSymbolsRight();
    }
    private String[] getSymbolsLeft() throws NotContextFreeException {
        checkLeftSide();
        return string.split("\\s+");
    }
    private void checkLeftSide() throws NotContextFreeException {
        String [] symbols = string.split("\\s+");
        for(String symbol:symbols){
            if(symbol.matches("^[^A-Z']$")){
                throw new NotContextFreeException("Left side of the grammar contains a non-terminal");
            }
        }
    }
    private String[] getSymbolsRight(){
        return string.split(" ");
    }
}
enum Side {
    LEFT, RIGHT
}