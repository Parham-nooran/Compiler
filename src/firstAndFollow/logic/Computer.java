package firstAndFollow.logic;

import java.io.File;
import java.util.*;

/**
 * Is a class designed to compute first and follow of a set of given rules and store them in two lists named firsts
 * and follows.
 * <p> The only constructor of the class would initialize firsts and follows sets as empty sets and calls
 * a new instance of an {@code Analyser} on the file passed to the constructor of the class.
 * <p> The rules set would be initialized using the {@code Analyser}.
 */
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

    /**
     * computes firsts of non terminals of the rules' set and stores them in the firsts' set.
     */
    public void computeFirst() {
        for (String leftSide:rules.keySet()){
            Set<String> temp = new HashSet<>();
            for(String[] strings:rules.get(leftSide)){
                temp.addAll(computeFirst(strings));
            }
            firsts.put(leftSide, temp);
        }
    }

    /**
     * computes follows of non terminals of the rules' set and stores them in the follows' set.
     * @param startingS
     * the starting symbol of the grammar.
     */
    public void computeFollow(String startingS) {
        for (String leftSide:rules.keySet()){
            Set<String> temp = new HashSet<>();
            temp.addAll(computeFollow(startingS, leftSide));
            temp.remove("epsilon");
            follows.put(leftSide, temp);
        }
    }

    /**
     * Computes the firsts of the given given array of strings named symbols
     * using the rules and returns the result as a set of strings.
     * @param symbols
     * an array of string that it would determine its firsts.
     * @return
     * returns the firsts of the given array of strings, named symbols, using the grammar determined by the
     * rules' set.
     */
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

    /**
     *
     * @param startingNT
     * the starting symbol of the grammar.
     * @param symbols
     * an array of string that it would determine its follows.
     * @return
     * returns the follows of the given array of strings, named symbols, using the grammar determined by the
     * rules' set.
     */
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

    /**
     * Computes and returns the follow of a given symbol which is the first of its suffix or the follow of the
     * left symbol of the rule.
     * @param startingNT
     * the starting symbol of the grammar.
     * @param symbol
     * the symbol that its follow would be determined
     * @param strings
     * the right side of the rule.
     * @param leftSide
     * the left side non terminal of the rule
     * @return
     * returns a set of strings which is the follow of the given symbol.
     */

    private Set<String> addSuffixFirst(String startingNT, String symbol, String[] strings, String leftSide){
        Set<String> temp = new HashSet<>(getSuffix(strings, symbol).length > 0 ?
                computeFirst(getSuffix(strings, symbol))
                : !leftSide.equals(symbol) ? computeFollow(startingNT, leftSide) : new HashSet<>());
        if(getSuffix(strings, symbol).length ==0&&leftSide.equals(startingNT)){
            temp.add("$");
        }
        return temp;
    }

    /**
     * Checks whether to compute the follow of a symbol (of type string) in an array of strings or not and returns the
     * computed follow or an empty set in case of a problem.
     * <p> the determination is on a single rule.
     * @param startingNT
     * the starting symbol of the grammar.
     * @param symbol
     * the symbol that its follow would be determined
     * @param strings
     * the right side of the rule.
     * @param leftSide
     * the left side non terminal of the rule
     * @return
     * returns a set of strings which is the follow of the given symbol.
     */
    private Set<String> checkFollow(String startingNT, String symbol, String[] strings, String leftSide){
        Set<String> temp = new HashSet<>();
        if(getSuffix(strings, symbol).length > 0 &&
                computeFirst(getSuffix(strings, symbol)).contains("epsilon")&&!leftSide.equals(symbol)){
            temp.addAll(computeFollow(startingNT, leftSide));
        }
        return temp;
    }

    /**
     * Returns the symbols in the right side of a given character.
     * @param rightSide
     * the array of strings (symbols). Some part of this array would be returned.
     * @param character
     * a given character that exists in the given array of strings.
     * @return
     */
    private String[] getSuffix(String[] rightSide, String character){
        int index = Arrays.asList(rightSide).indexOf(character);
        String[] temp = new String[rightSide.length-index-1];
        System.arraycopy(rightSide, index+1, temp, 0, rightSide.length-index-1);
        return temp;
    }

    /**
     * Checks whether the input is a terminal or not. If the input is a terminal or epsilon it returns true else false.
     * @param input
     * the string that would be checked.
     * @return boolean if the input is a terminal or epsilon it returns true else false.
     */
    private boolean isTerminalOrEps(String input){
        return input.matches("^[^A-Z]+$|^epsilon$");//-a-z+=<>(){}\[\]|\\.,?'*&^%$#@!~
    }

    /**
     * Returns a copy of rules' list
     * @return
     */
    public HashMap<String, ArrayList<String[]>> getRules() {
        return new HashMap<>(rules);
    }
    /**
     * Returns a copy of firsts' list
     * @return
     */
    public HashMap<String, Set<String>> getFirsts() {
        return new HashMap<>(firsts);
    }
    /**
     * Returns a copy of follows' list
     * @return
     */
    public HashMap<String, Set<String>> getFollows() {
        return new HashMap<>(follows);
    }
}
