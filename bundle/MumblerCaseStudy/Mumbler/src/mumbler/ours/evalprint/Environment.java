package mumbler.ours.evalprint;

import java.util.HashMap;

import mumbler.simple.node.Node;

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

    public static Environment<Node2> getBaseEnvironment() {
        Environment<Node2> env = new Environment<Node2>();
        env.putValue("+", PLUS2.of(""));
        env.putValue("-", MINUS2.of(""));
        env.putValue("*", MULT2.of(""));
        env.putValue("/", DIV2.of(""));
        env.putValue("=", EQUALS2.of(""));
        env.putValue("<", LESS_THAN2.of(""));
        env.putValue(">", GREATER_THAN2.of(""));
        env.putValue("list", LIST2.of(""));
        env.putValue("car", CAR2.of(""));
        env.putValue("cdr", CDR2.of(""));
        env.putValue("println", PRINTLN2.of(""));
        env.putValue("now", NOW2.of(""));
        return env;
    }
}
