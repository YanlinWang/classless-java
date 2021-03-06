package mumbler.ours.evalprint;

import lombok.Obj;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

interface Node {
    public Object eval(Environment<Node> env);
}

class LongNode implements Node {
    Long value;
    LongNode(Long value) { this.value = value; }
    public Long eval(Environment<Node> env) { return value; }
}

//============= Added operation (begin) ============//
interface Print {
    public String print();
}

interface BuiltinFnP extends Print {
    String name();
    public default String print() { return name(); }
}

@Obj interface BooleanNodeP extends Print {
//    public static final BooleanNodeP TRUE = BooleanNode2.of(Boolean.TRUE);
//    public static final BooleanNodeP FALSE = BooleanNode2.of(Boolean.FALSE);
    Boolean value();
    public default String print() {
        return value().toString();
    }
}

@Obj interface NumberNodeP extends Print {
    long num();
    void num(long n);
    public default String print() {
        return Long.toString(num());
    }
}

@Obj interface SpecialFormP extends Print {
    MumblerListNode<Node> node();
    public default String print() {
        return node().print();
    }
}

@Obj interface SymbolNodeP extends Print {
    String name();

    public default String print() {
        return "'" + name();
    }
}
//============= Added operation (end) ============//


//class MumblerListNode2<T extends Object> extends MumblerListNode<T> implements Eval2 {
//    public final T car;
//    public final MumblerListNode2<T> cdr;
//    public MumblerListNode2() {
//        this.car = null;
//        this.cdr = null;
//    }
//    public MumblerListNode2(T car, MumblerListNode2<T> cdr) {
//        super();
//        this.car = car;
//        this.cdr = cdr;
//    }
//    public String print() {
//        if (this == EMPTY) {
//            return "()";
//        }
//
//        StringBuilder b = new StringBuilder("(" + this.car);
//        MumblerListNode<T> rest = this.cdr;
//        while (rest != null && rest != EMPTY) {
//            b.append(" ");
//            b.append(rest.car);
//            rest = rest.cdr;
//        }
//        b.append(")");
//        return b.toString();
//    }
//}

public interface Function extends Node {
    public default Object eval(Environment<Node> env) {
        return this;
    }

    public abstract Object apply(Object... args);
}

@Obj interface EQUALS extends BuiltinFn {
    public default Object apply(Object... args) {
            Long last = (Long) args[0];
            for (Object arg : args) {
                Long current = (Long) arg;
                if (!last.equals(current)) {
                    return false;
                } else {
                    last = current;
                }
            }
            return true;
    }
}

@Obj interface LESS_THAN  extends BuiltinFn{
        public default Object apply(Object... args) {
            assert args.length > 1;
            long num = (Long) args[args.length - 1];
            for (int i=args.length - 2; i>=0; i--) {
                long n = (Long) args[i];
                if (n >= num) {
                    return false;
                }
                num = n;
            }
            return true;
        }
    }

@Obj interface GREATER_THAN  extends BuiltinFn{// = new BuiltinFn("GREATER-THAN") {
        public default Object apply(Object... args) {
            assert args.length > 1;
            long num = (Long) args[args.length - 1];
            for (int i=args.length - 2; i>=0; i--) {
                long n = (Long) args[i];
                if (n <= num) {
                    return false;
                }
                num = n;
            }
            return true;
        }
    }

@Obj interface DIV extends BuiltinFn {//= new BuiltinFn("DIV") {
        public default Object apply(Object... args) {
            if (args.length == 1) {
                return 1 / (Long) args[0];
            }
            long quotient = (Long) args[0];
            for (int i=1; i<args.length; i++) {
                quotient /= (Long) args[i];
            }
            return quotient;
        }
    }

@Obj interface MULT extends BuiltinFn{// = new BuiltinFn("MULT") {
        public default Object apply(Object... args) {
            long product = 1;
            for (Object arg : args) {
                product *= (Long) arg;
            }
            return product;
        }
    };

@Obj interface MINUS extends BuiltinFn {//= new BuiltinFn("MINUS") {
        public default Object apply(Object... args) {
            if (args.length < 1) {
                throw new RuntimeException(this.name() + " requires an argument");
            }
            switch (args.length) {
            case 1:
                return -((Long) args[0]);
            default:
                long diff = (Long) args[0];
                for (int i=1; i<args.length; i++) {
                    diff -= (Long) args[i];
                }
                return diff;
            }
        }
    };

    @Obj interface PLUS extends BuiltinFn {//= new BuiltinFn("PLUS") {
        @Override
        public default Object apply(Object... args) {
            long sum = 0;
            for (Object arg : args) {
                sum += (Long) arg;
            }
            return sum;
        }
    };

    @Obj interface LIST extends BuiltinFn {//= new BuiltinFn("list") {
        @Override
        public default Object apply(Object... args) {
            return MumblerListNode.list(args);
        }
    };

    @Obj interface CAR extends BuiltinFn {//= new BuiltinFn("car") {
        @Override
        public default Object apply(Object... args) {
            assert args.length == 1;
            return ((MumblerListNode<?>) args[0]).car;
        }
    };

    @Obj interface CDR extends BuiltinFn {//= new BuiltinFn("cdr") {
        @Override
        public default Object apply(Object... args) {
            assert args.length == 1;
            return ((MumblerListNode<?>) args[0]).cdr;
        }
    };

    @Obj interface PRINTLN extends BuiltinFn {//= new BuiltinFn("println") {
        @Override
        public default Object apply(Object... args) {
            for (Object arg : args) {
                System.out.println(arg);
            }
            return MumblerListNode.EMPTY;
        }
    };

    @Obj interface NOW extends BuiltinFn {//= new BuiltinFn("now") {
        @Override
        public default Object apply(Object... args) {
            return System.currentTimeMillis();
        }
    };

interface BuiltinFn extends Function {
    String name();
    public default String toStr() {
        return "<procedure: " + name();
    }
}

@Obj interface BooleanNode extends Node {
    public static BooleanNode TRUE() { return BooleanNode.of(Boolean.TRUE); }
    public static BooleanNode FALSE() { return BooleanNode.of(Boolean.FALSE); }
    Boolean value();
    public default Object eval(Environment<Node> env) {
        return value();
    }
}

@Obj interface NumberNode extends Node {
    long num();
    void num(long n);  
    public default String toStr() {
        return Long.toString(num());
    }

    public default boolean equal(Object other) {
        return other instanceof NumberNode &&
            num() == ((NumberNode) other).num();
    }

    @Override
    public default Object eval(Environment<Node> env) {
        return new LongNode(num());
    }
}
//============= Combine operations (begin) ============//
interface Node2 extends Node, Print {}
interface Function2 extends Node2, Function {}
//@Obj interface BuiltinFn2 extends Eval2, BuiltinFn, BuiltinFnP {}
@Obj interface EQUALS2 extends Node2, EQUALS, BuiltinFnP {}
@Obj interface LESS_THAN2 extends Node2, LESS_THAN, BuiltinFnP {}
@Obj interface GREATER_THAN2 extends Node2, GREATER_THAN, BuiltinFnP {}
@Obj interface DIV2 extends Node2, DIV , BuiltinFnP{
    public default Object apply(Object... args) {
        if (args.length == 1) {
            if ((Long)args[0] == 0) {
                System.out.println("ERROR: divided by zero at: " + this.print());
                try {
                    throw new Exception("ERROR: " + this.print());
                } catch (Exception e) {
                    e.printStackTrace();
                }}
            else return 1 / (Long) args[0];
        }
        long quotient = (Long) args[0];
        for (int i=1; i<args.length; i++) {
            if ((Long) args[i] == 0) {
                System.out.println("ERROR: divided by zero at: " + this.print());
                try {
                    throw new Exception("ERROR: " + this.print());
                } catch (Exception e) {
                    e.printStackTrace();
                } }
            else quotient /= (Long) args[i];
        }
        return quotient;
    }
}
@Obj interface MULT2 extends Node2, MULT , BuiltinFnP{}
@Obj interface MINUS2 extends Node2, MINUS, BuiltinFnP {}
@Obj interface PLUS2 extends Node2, PLUS, BuiltinFnP {}
@Obj interface LIST2 extends Node2, LIST, BuiltinFnP {}
@Obj interface CAR2 extends Node2, CAR, BuiltinFnP {}
@Obj interface CDR2 extends Node2, CDR, BuiltinFnP {}
@Obj interface PRINTLN2 extends Node2, PRINTLN, BuiltinFnP{}
@Obj interface NOW2 extends Node2, NOW, BuiltinFnP {}
@Obj interface BooleanNode2 extends Node2, BooleanNode, BooleanNodeP {}
@Obj interface NumberNode2 extends Node2, NumberNode, NumberNodeP {}
interface SpecialForm2 extends Node2, SpecialForm, SpecialFormP {
//    MumblerListNode<Node> node();
//    static final SymbolNode DEFINE = SymbolNode.of("define");
//    static final SymbolNode LAMBDA = SymbolNode.of("lambda");
//    static final SymbolNode IF = SymbolNode.of("if");
//    static final SymbolNode QUOTE = SymbolNode.of("quote");
    public static Node2 check(MumblerListNode<Node> l) {
        if (l == MumblerListNode.EMPTY) {
            return l;
        } else if (l.car.equals(SymbolNode2.DEFINE)) {
            return DefineSpecialForm2.of(l);
        } else if (l.car.equals(SymbolNode2.LAMBDA)) {
            return LambdaSpecialForm2.of(l);
        } else if (l.car.equals(SymbolNode2.IF)) {
            return IfSpecialForm2.of(l);
        } else if (l.car.equals(SymbolNode2.QUOTE)) {
            return QuoteSpecialForm2.of(l);
        }
        return l;
    }
}
@Obj interface DefineSpecialForm2 extends Node2, DefineSpecialForm, SpecialFormP {}
@Obj interface LambdaSpecialForm2 extends Node2, LambdaSpecialForm, SpecialFormP {}
@Obj interface IfSpecialForm2 extends Node2, IfSpecialForm, SpecialFormP {}
@Obj interface QuoteSpecialForm2 extends Node2, QuoteSpecialForm, SpecialFormP {}
@Obj interface SymbolNode2 extends Node2, SymbolNode, SymbolNodeP {
//    String name();
    public default String print() {
        return "'" + name();
    }
    static SymbolNode2 DEFINE = SymbolNode2.of("define");
    static SymbolNode2 LAMBDA = SymbolNode2.of("lambda");
    static SymbolNode2 IF = SymbolNode2.of("if");
    static SymbolNode2 QUOTE = SymbolNode2.of("quote");
}
//============= Combine operations (end) ============//

class MumblerListNode<T extends Object> implements Node, Iterable<T>, Node2 {
    public static final MumblerListNode<?> EMPTY = 
            new MumblerListNode<>();

    public final T car;
    public final MumblerListNode<T> cdr;

    public MumblerListNode() {
        this.car = null;
        this.cdr = null;
    }

    public MumblerListNode(T car, MumblerListNode<T> cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    @SafeVarargs
    public static <T> MumblerListNode<T> list(T... objs) {
        return list(asList(objs));
    }

    public static <T> MumblerListNode<T> list(List<T> objs) {
        @SuppressWarnings("unchecked")
        MumblerListNode<T> l = (MumblerListNode<T>) EMPTY;
        for (int i=objs.size()-1; i>=0; i--) {
            l = l.cons(objs.get(i));
        }
        return l;
    }

    public MumblerListNode<T> cons(T node) {
        return new MumblerListNode<T>(node, this);
    }

    public long length() {
        if (this == EMPTY) {
            return 0;
        }

        long len = 1;
        MumblerListNode<T> l = this.cdr;
        while (l != EMPTY) {
            len++;
            l = l.cdr;
        }
        return len;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private MumblerListNode<T> l = MumblerListNode.this;

            @Override
            public boolean hasNext() {
                return this.l != EMPTY;
            }

            @Override
            public T next() {
                if (this.l == EMPTY) {
                    throw new IllegalStateException("At end of list");
                }
                T car = this.l.car;
                this.l = this.l.cdr;
                return car;
            }

            @Override
            public void remove() {
                throw new IllegalStateException("Iterator is immutable");
            }
        };
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MumblerListNode)) {
            return false;
        }
        if (this == EMPTY && other == EMPTY) {
            return true;
        }

        MumblerListNode<?> that = (MumblerListNode<?>) other;
        if (this.cdr == EMPTY && that.cdr != EMPTY) {
            return false;
        }
        return this.car.equals(that.car) && this.cdr.equals(that.cdr);
    }

    public String print() {
        if (this == EMPTY) {
            return "()";
        }
        StringBuilder b = new StringBuilder("(" + this.car);
        MumblerListNode<T> rest = this.cdr;
        while (rest != null && rest != EMPTY) {
            b.append(" ");
            b.append(rest.car);
            rest = rest.cdr;
        }
        b.append(")");
        return b.toString();
    }
    @Override
    public String toString() {
        if (this == EMPTY) {
            return "()";
        }
        StringBuilder b = new StringBuilder("(" + this.car);
        MumblerListNode<T> rest = this.cdr;
        while (rest != null && rest != EMPTY) {
            b.append(" ");
            b.append(rest.car);
            rest = rest.cdr;
        }
        b.append(")");
        return b.toString();
    }
    @Override
    public Object eval(Environment<Node> env) {
        Function function = (Function) ((Node) this.car).eval(env);

        @SuppressWarnings("unchecked")
        MumblerListNode<Node> nodes = (MumblerListNode<Node>) this;
        List<Object> args = new ArrayList<Object>();
        for (Node node : nodes.cdr) {
            args.add(node.eval(env));
        }
        return function.apply(args.toArray());
    }
}

@Obj interface DefineSpecialForm extends SpecialForm {

    public default Object eval(Environment<Node> env) {
        SymbolNode sym = (SymbolNode) node().cdr.car; // 2nd element
        env.putValue(sym.name(), (Node)((Node) node().cdr.cdr.car).eval(env)); // 3rd element
        return null;
    }
}

@Obj interface LambdaSpecialForm extends SpecialForm {
    public default Object eval(final Environment<Node> parentEnv) {
        @SuppressWarnings("unchecked")
        final MumblerListNode<Node> formalParams =
        (MumblerListNode<Node>) node().cdr.car;
        final MumblerListNode<Node> body = node().cdr.cdr;
        return new Function() {
            @Override
            public Object apply(Object... args) {
                Environment<Node> lambdaEnv = new Environment<Node>(parentEnv);
                if (args.length != formalParams.length()) {
                    throw new RuntimeException(
                            "Wrong number of arguments. Expected: " +
                                    formalParams.length() + ". Got: " +
                                    args.length);
                }

                // Map parameter values to formal parameter names
                int i = 0;
                for (Node param : formalParams) {
                    SymbolNode paramSymbol = (SymbolNode) param;
                    lambdaEnv.putValue(paramSymbol.name(), (Node)args[i]);
                    i++;
                }

                // Evaluate body
                Object output = null;
                for (Node node : body) {
                    output = node.eval(lambdaEnv);
                }

                return output;
            }
        };
    }
}

@Obj interface IfSpecialForm extends SpecialForm {
    public default Object eval(Environment<Node> env) {
        Node testNode = node().cdr.car;
        Node thenNode = node().cdr.cdr.car;
        Node elseNode = (Node) node().cdr.cdr.cdr.car;

        Object result = testNode.eval(env);
        if (result == MumblerListNode.EMPTY || Boolean.FALSE == result) {
            return elseNode.eval(env);
        } else {
            return thenNode.eval(env);
        }
    }
}

@Obj interface QuoteSpecialForm extends SpecialForm {
    public default Object eval(Environment<Node> env) {
        return node().cdr.car;
    }
}

interface SpecialForm extends Node {
    MumblerListNode<Node> node();

    public static Node check(MumblerListNode<Node> l) {
        if (l == MumblerListNode.EMPTY) {
            return l;
        } else if (l.car.equals(SymbolNode.DEFINE)) {
            return DefineSpecialForm.of(l);
        } else if (l.car.equals(SymbolNode.LAMBDA)) {
            return LambdaSpecialForm.of(l);
        } else if (l.car.equals(SymbolNode.IF)) {
            return IfSpecialForm.of(l);
        } else if (l.car.equals(SymbolNode.QUOTE)) {
            return QuoteSpecialForm.of(l);
        }
        return l;
    }
}

@Obj interface SymbolNode extends Node {
    String name();
    
    public default String toStr() {
        return "'" + name();
    }

    public default boolean equal(Object other) {
        return other instanceof SymbolNode &&
                name().equals(((SymbolNode) other).name());
    }

    public default int hashcode() {
        return name().hashCode();
    }

    public default Object eval(Environment<Node> env) {
        return env.getValue(name());
    }
    static SymbolNode DEFINE = SymbolNode.of("define");
    static SymbolNode LAMBDA = SymbolNode.of("lambda");
    static SymbolNode IF = SymbolNode.of("if");
    static SymbolNode QUOTE = SymbolNode.of("quote");
//    static SymbolNode DEFINE() { return SymbolNode.of("define"); } 
//    static SymbolNode LAMBDA() { return SymbolNode.of("lambda"); }
//    static SymbolNode IF() { return SymbolNode.of("if"); }
//    static SymbolNode QUOTE() { return SymbolNode.of("quote"); }
}