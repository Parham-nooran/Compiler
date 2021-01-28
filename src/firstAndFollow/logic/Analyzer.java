package firstAndFollow.logic;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Analyzer {
    private HashMap<RuleSide, ArrayList<RuleSide>> rules;

    public Analyzer() {
        this.rules = new HashMap<>();
    }

    public void scan(File file) {
        try (
                Scanner scanner = new Scanner(file)
        ) {
            while (scanner.hasNext()){
                addRules(scanner);
            }
        } catch (IOException e){
            System.out.println("Something went wrong while initializing the fileReader");
            e.printStackTrace();
        }
    }
    private void addRules(Scanner scanner) {
        String rule = scanner.nextLine();
        RuleSide left = getLeftSide(rule);
        RuleSide[] rightRules = getRightRules(rule);
        ArrayList<RuleSide> temp = new ArrayList<>();
        temp.addAll(Arrays.asList(rightRules));
        rules.put(left, temp);
    }
    private RuleSide getLeftSide(String rule) {
        String symbols = parseRule(rule)[0];
        return new RuleSide(symbols, Side.LEFT);
    }
    private RuleSide[] getRightRules(String rule){
        String[] symbols = parseRule(rule)[1].split("\\|");
        RuleSide[] rules = new RuleSide[symbols.length];
        for(int i=0;i<symbols.length;i++){
            symbols[i] = symbols[i].trim();
            rules[i] = new RuleSide(symbols[i], Side.RIGHT);
        }
        return rules;
    }
    private String[] parseRule(String rule){
        String [] ruleSides = rule.split("->", 2);
        for(int i=0;i<ruleSides.length;i++){
            ruleSides[i] = ruleSides[i].trim();
        }
        return ruleSides;
    }

    public HashMap<String, ArrayList<String[]>> getRules() throws NotContextFreeException {
        HashMap<String, ArrayList<String[]>> stringRules = new HashMap<>();
        for (RuleSide leftSide:rules.keySet()){
            //System.out.println(Arrays.toString(leftSide.getSymbols()));
            ArrayList<String[]> temp = new ArrayList<>();
            for(int i=0;i<rules.get(leftSide).size();i++){
                //System.out.println(Arrays.toString(rules.get(leftSide).get(i).getSymbols()));
                temp.add(rules.get(leftSide).get(i).getSymbols());
            }
            stringRules.put(leftSide.getSymbols()[0], temp);
        }
        return stringRules;
    }
}
