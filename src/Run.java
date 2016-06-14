import java.io.IOException;

public class Run {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: Run port");
            return;
        }
        int port = DKVSProperties.ports.get(Integer.parseInt(args[0]));
        new DKVSNode(port).run();
    }
}