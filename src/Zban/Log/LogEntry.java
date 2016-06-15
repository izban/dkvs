package Zban.Log;

/**
 * Created by izban on 14.06.16.
 */
public class LogEntry {
    public String key;
    public String value;
    public int term;

    public LogEntry(String key, String value, int term) {
        this.key = key;
        this.value = value;
        this.term = term;
    }

    static public LogEntry parseEntry(String s) {
        String a[] = s.split(":");
        return new LogEntry(a[0], a[1], Integer.parseInt(a[2]));
    }


    @Override
    public String toString() {
        return key + ":" + value + ":" + term;
    }
}
