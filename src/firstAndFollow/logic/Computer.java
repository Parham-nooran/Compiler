package firstAndFollow.logic;

import java.io.File;
import java.util.*;

public class Computer {
    private HashMap<String, ArrayList<String[]>> rules;
    private HashMap<String, Set<String>> firsts;
    private HashMap<String, Set<String>> follows;

    public Computer(File file) throws NotContextFreeException {
        Analyzer analyzer = new Analyzer();
        analyzer.scan(file);
        rules = analyzer.getRules();
        firsts = new HashMap<>();
        follows = new HashMap<>();
    }

    public void computeFirst() {
        for (String leftSide:rules.keySet()){
            Set<String> temp = new HashSet<>();
            for(String[] strings:rules.get(leftSide)){
                temp.addAll(computeFirst(strings));
            }
            firsts.put(leftSide, temp);
        }
    }
    public void computeFollow(String startingS) {
        for (String leftSide:rules.keySet()){
            Set<String> temp = new HashSet<>();
            temp.addAll(computeFollow(startingS, leftSide));
            temp.remove("epsilon");
            follows.put(leftSide, temp);
        }
    }
    private Set<String> computeFirst(String ... symbols){
        Set<String> temp = new HashSet<>();
        if(isTerminalOrEps(symbols[0])){
            temp.add(symbols[0]);
            return Set.copyOf(temp);
        }
        if(symbols.length==1&&rules.containsKey(symbols[0])){
            for(String[] strings:rules.get(symbols[0])){
                temp.addAll(computeFirst(strings));
            }
            return Set.copyOf(temp);
        }
        if(!isTerminalOrEps(symbols[0])&&rules.containsKey(symbols[0])){
            int i = 0;
            do {
                temp.addAll(computeFirst(symbols[i]));
                i++;
            }while (i<symbols.length&& computeFirst(symbols[i-1]).contains("epsilon"));
            if(symbols[i<symbols.length?i:i-1].matches("^[^A-Z]+$")){
                temp.remove("epsilon");
            }
            return temp;
        }
        return new HashSet<>();
    }
    public Set<String> computeFollow(String startingNT, String... symbols){
        Set<String> temp = new HashSet<>();
        if(symbols.length==1&&!isTerminalOrEps(symbols[0])&&rules.containsKey(symbols[0])){
            for (String leftSide:rules.keySet()){
                for(String[] strings:rules.get(leftSide)){
                    if(Arrays.asList(strings).contains(symbols[0])){
                        temp.addAll(addSuffixFirst(startingNT, symbols[0], strings, leftSide));
                        temp.addAll(checkFollow(startingNT, symbols[0], strings, leftSide));
                    }
                }
            }
            if(symbols[0].equals(startingNT)){
                temp.add("$");
            }
            return temp;
        }
        return new HashSet<>();
    }

    private Set<String> addSuffixFirst(String startingNT, String symbol, String[] strings, String leftSide){
        Set<String> temp = new HashSet<>(getSuffix(strings, symbol).length > 0 ?
                computeFirst(getSuffix(strings, symbol))
                : !leftSide.equals(symbol) ? computeFollow(startingNT, leftSide) : new HashSet<>());
        if(getSuffix(strings, symbol).length ==0&&leftSide.equals(startingNT)){
            temp.add("$");
        }
        return temp;
    }

    private Set<String> checkFollow(String startingNT, String symbol, String[] strings, String leftSide){
        Set<String> temp = new HashSet<>();
        if(getSuffix(strings, symbol).length > 0 &&
                computeFirst(getSuffix(strings, symbol)).contains("epsilon")&&!leftSide.equals(symbol)){
            temp.addAll(computeFollow(startingNT, leftSide));
        }
        return temp;
    }

    private String[] getSuffix(String[] rightSide, String character){
        int index = Arrays.asList(rightSide).indexOf(character);
        String[] temp = new String[rightSide.length-index-1];
        System.arraycopy(rightSide, index+1, temp, 0, rightSide.length-index-1);
        return temp;
    }

    /**
     * Checks if the input is terminal or not. If the input is a terminal or epsilon it returns true else false.
     * @param input is of type String.
     * @return boolean if the input is a terminal or epsilon it returns true else false.
     */
    private boolean isTerminalOrEps(String input){
        return input.matches("^[^A-Z]+$|^epsilon$");//-a-z+=<>(){}\[\]|\\.,?'*&^%$#@!~
    }

    public HashMap<String, ArrayList<String[]>> getRules() {
        return rules;
    }

    public HashMap<String, Set<String>> getFirsts() {
        return firsts;
    }

    public HashMap<String, Set<String>> getFollows() {
        return follows;
    }
}
