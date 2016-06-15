package Zban.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by izban on 14.06.16.
 */
public class Logger {
    int id;
    String filename;
    public ArrayList<LogEntry> a = new ArrayList<>();

    public Logger(int id) throws IOException {
        this.id = id;
        filename = "dkvs_" + id + ".log";
        if (!Files.exists(Paths.get(filename))) {
            Files.createFile(Paths.get(filename));
        }
        BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
        while (true) {
            String s = in.readLine();
            if (s == null) break;
            a.add(LogEntry.parseEntry(s));
        }
        if (a.isEmpty()) {
            a.add(new LogEntry("", "", 0));
        }
        rewrite();
    }

    public int getLastTerm() {
        return a.get(a.size() - 1).term;
    }

    public int getLastId() {
        return a.size() - 1;
    }

    void rewrite() {
        try {
            String file = "dkvs_" + id + ".log";
            PrintWriter out = new PrintWriter(new File(file));
            for (int i = 0; i < a.size(); i++) {
                out.println(a.get(i).toString());
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void add(LogEntry x) {
        a.add(x);
        try {
            PrintWriter out = new PrintWriter(new FileWriter(filename, true));
            out.println(x);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void trim(int len) {
        while (a.size() > len) {
            a.remove(a.size() - 1);
        }
        rewrite();
    }
}
