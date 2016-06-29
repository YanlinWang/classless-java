package mumbler.ours.eval;

import java.util.HashMap;

public class Environment<A> {
    private final HashMap<String, A> env = new HashMap<String, A>();

    private final Environment<A> parent;

    public Environment() {
        this(null);
    }

    public Environment(Environment<A> parent) {
        this.parent = parent;
    }

    public Object getValue(String name) {
        if (this.env.containsKey(name)) {
            return this.env.get(name);
        } else if (this.parent != null) {
            return this.parent.getValue(name);
        } else {
            throw new RuntimeException("No variable: " + name);
        }
    }

    public void putValue(String name, A value) {
        this.env.put(name, value);
    }

    public static Environment<Node> getBaseEnvironment() {
        Environment<Node> env = new Environment<Node>();
        env.putValue("+", PLUS.of(""));
        env.putValue("-", MINUS.of(""));
        env.putValue("*", MULT.of(""));
        env.putValue("/", DIV.of(""));
        env.putValue("=", EQUALS.of(""));
        env.putValue("<", LESS_THAN.of(""));
        env.putValue(">", GREATER_THAN.of(""));
        env.putValue("list", LIST.of(""));
        env.putValue("car", CAR.of(""));
        env.putValue("cdr", CDR.of(""));
        env.putValue("println", PRINTLN.of(""));
        env.putValue("now", NOW.of(""));
        return env;
    }
}
