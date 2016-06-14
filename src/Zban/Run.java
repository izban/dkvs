package Zban;

import java.io.IOException;

public class Run {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: Zban.Run port");
            return;
        }
        int id = Integer.parseInt(args[0]);
        new DKVSNode(id).run();
    }
}