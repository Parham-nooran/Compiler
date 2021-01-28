package firstAndFollow.graphics;

import java.util.ArrayList;
import java.util.Set;

public class NonTerminal {
    private String name;
    private Set<String> firsts;
    private Set<String> follows;

    public NonTerminal(String name, Set<String> firsts, Set<String> follows) {
        this.name = name;
        this.firsts = firsts;
        this.follows = follows;
    }

    public NonTerminal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getFirsts() {
        return new ArrayList<>(firsts);
    }

    public ArrayList<String> getFollows() {
        return new ArrayList<>(follows);
    }

    public void setFirsts(Set<String> firsts) {
        this.firsts = firsts;
    }

    public void setFollows(Set<String> follows) {
        this.follows = follows;
    }
}
