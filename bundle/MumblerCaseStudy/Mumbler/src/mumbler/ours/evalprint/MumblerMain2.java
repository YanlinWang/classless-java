package mumbler.ours.evalprint;

import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;

public class MumblerMain2 {
    public static void main(String[] args) throws IOException {
        assert args.length < 2 : "SimpleMumbler only accepts 1 or 0 files";
        if (args.length == 0) {
            startREPL();
        } else {
            runMumbler(args[0]);
        }
    }

    static void startREPL() throws IOException {
        Environment topEnv = Environment.getBaseEnvironment();

        Console console = System.console();
        while (true) {
            // READ
            String data = console.readLine("~> ");
            if (data == null) {
                // EOF sent
                break;
            }
            MumblerListNode<Node2> nodes = Reader2.read(new ByteArrayInputStream(data.getBytes()));

            // EVAL
            Object result = MumblerListNode.EMPTY;
            for (Node2 node : nodes) {
                result = node.eval(topEnv);
            }

            // PRINT
            if (result != MumblerListNode.EMPTY) {
                System.out.println(result);
            }
        }
    }

    static void runMumbler(String filename) throws IOException {
        Environment topEnv = Environment.getBaseEnvironment();

        MumblerListNode<Node2> nodes = Reader2.read(new FileInputStream(filename));
        for (Node2 node : nodes) {
            node.eval(topEnv);
            System.out.println(node.print());
        }
    }
}
