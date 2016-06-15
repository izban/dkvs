package Zban.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by izban on 14.06.16.
 */
public class Logger {
    public ArrayList<LogEntry> a = new ArrayList<>();

    public Logger(int id) throws IOException {
        String file = "dkvs_" + id + ".log";
        if (!Files.exists(Paths.get(file))) {
            Files.createFile(Paths.get(file));
        }
        BufferedReader in = new BufferedReader(new FileReader(new File(file)));
        while (true) {
            String s = in.readLine();
            if (s == null) break;
            a.add(LogEntry.parseEntry(s));
        }
    }

    public int getLastTerm() {
        if (a.isEmpty()) return 0;
        return a.get(a.size() - 1).term;
    }

    public int getLastId() {
        return a.size();
    }
}
