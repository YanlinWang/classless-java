package mumbler.ours.eval;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.List;

public interface Reader {
    public static MumblerListNode<Node> read(InputStream istream) throws IOException {
        return read(new PushbackReader(new InputStreamReader(istream)));
    }

    static MumblerListNode<Node> read(PushbackReader pstream)
            throws IOException {
        List<Node> nodes = new ArrayList<Node>();

        readWhitespace(pstream);
        char c = (char) pstream.read();
        while ((byte) c != -1) {
            pstream.unread(c);
            nodes.add(readNode(pstream));
            readWhitespace(pstream);
            c = (char) pstream.read();
        }

        return MumblerListNode.list(nodes);
    }

    public static Node readNode(PushbackReader pstream) throws IOException {
        char c = (char) pstream.read();
        pstream.unread(c);
        if (c == '(') {
            return readList(pstream);
        } else if (Character.isDigit(c)) {
            return readNumber(pstream);
        } else if (c == '#') {
            return readBoolean(pstream);
        } else if (c == ')') {
            throw new IllegalArgumentException("Unmatched close paren");
        } else {
            return readSymbol(pstream);
        }
    }

    static void readWhitespace(PushbackReader pstream)
            throws IOException {
        char c = (char) pstream.read();
        while (Character.isWhitespace(c)) {
            c = (char) pstream.read();
        }
        pstream.unread(c);
    }

    static SymbolNode readSymbol(PushbackReader pstream)
            throws IOException {
        StringBuilder b = new StringBuilder();
        char c = (char) pstream.read();
        while (!(Character.isWhitespace(c) || (byte) c == -1 || c == '(' || c == ')')) {
            b.append(c);
            c = (char) pstream.read();
        }
        pstream.unread(c);
        if (b.toString().equals("define")) return SymbolNode.DEFINE; 
        else if (b.toString().equals("if")) return SymbolNode.IF;
        else if (b.toString().equals("lambda")) return SymbolNode.LAMBDA;
        else if (b.toString().equals("quote")) return SymbolNode.QUOTE;
        return SymbolNode.of(b.toString());
    }

    static Node readList(PushbackReader pstream) throws IOException {
        char paren = (char) pstream.read();
        assert paren == '(' : "Reading a list must start with '('";
        List<Node> list = new ArrayList<Node>();
        do {
            readWhitespace(pstream);
            char c = (char) pstream.read();

            if (c == ')') {
                // end of list
                break;
            } else if ((byte) c == -1) {
                throw new EOFException("EOF reached before closing of list");
            } else {
                pstream.unread(c);
                list.add(readNode(pstream));
            }
        } while (true);
        return SpecialForm.check(MumblerListNode.list(list));
    }

    static NumberNode readNumber(PushbackReader pstream)
            throws IOException {
        StringBuilder b = new StringBuilder();
        char c = (char) pstream.read();
        while (Character.isDigit(c)) {
            b.append(c);
            c = (char) pstream.read();
        }
        pstream.unread(c);
        return NumberNode.of(Long.valueOf(b.toString(), 10));
    }

    static final SymbolNode TRUE_SYM = SymbolNode.of("t");
    static final SymbolNode FALSE_SYM = SymbolNode.of("f");

    static BooleanNode readBoolean(PushbackReader pstream)
            throws IOException {
        char hash = (char) pstream.read();
        assert hash == '#' : "Reading a boolean must start with '#'";

        SymbolNode sym = readSymbol(pstream);
        if (TRUE_SYM.equals(sym)) {
            return BooleanNode.TRUE();
        } else if (FALSE_SYM.equals(sym)) {
            return BooleanNode.FALSE();
        } else {
            throw new IllegalArgumentException("Unknown value: #" + sym.name());
        }
    }
}
