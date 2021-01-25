package firstAndFollow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Computer {
    private HashMap<String, ArrayList<String[]>> rules;
    private HashMap<String, Set<String>> firsts;

    public Computer(File file) throws NotContextFreeException {
        Analyzer analyzer = new Analyzer();
        analyzer.scan(file);
        rules = analyzer.getRules();
        firsts = new HashMap<>();
    }

    public void computeFirst() {
        for (String leftSide:rules.keySet()){
            Set<String> temp = new HashSet<>();
            for(String[] strings:rules.get(leftSide)){
                temp.addAll(getFirst(strings));
            }
            firsts.put(leftSide, temp);
        }
    }
    public Set<String> getFirst(String ... symbols){
        Set<String> temp = new HashSet<>();
        if(isTerminalOrEps(symbols[0])){
            temp.add(symbols[0]);
            return Set.copyOf(temp);
        }
        if(symbols.length==1&&rules.containsKey(symbols[0])){
            for(String[] strings:rules.get(symbols[0])){
                temp.addAll(getFirst(strings));
            }
            return Set.copyOf(temp);
        }
        if(!isTerminalOrEps(symbols[0])&&rules.containsKey(symbols[0])){
            int i = 0;
            do {
                temp.addAll(getFirst(symbols[i]));
                i++;
            }while (i<symbols.length&&getFirst(symbols[i-1]).contains("epsilon"));
            if(symbols[i<symbols.length?i:i-1].matches("^[^A-Z]+$")){
                temp.remove("epsilon");
            }
            return temp;
        }
        return new HashSet<>();
    }
    public void computeFollow(){

    }
    private boolean isTerminalOrEps(String input){
        return input.matches("^[^A-Z]+$|^epsilon$");//-a-z+=<>(){}\[\]|\\.,?'*&^%$#@!~
    }

    public HashMap<String, ArrayList<String[]>> getRules() {
        return rules;
    }

    public HashMap<String, Set<String>> getFirsts() {
        return firsts;
    }
}
