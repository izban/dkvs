import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by izban on 12.06.2016.
 */
public class DKVSProperties {
    static public Map<Integer, Integer> ports = new HashMap<>();

    static {
        try {
            Scanner in = new Scanner(new File("dkvs.properties"));
            while (in.hasNext()) {
                String s = in.next();
                if (s.startsWith("node")) {
                    int id = Integer.parseInt(s.substring(s.indexOf(".") + 1, s.indexOf("=")));
                    int port = Integer.parseInt(s.substring(s.indexOf("=") + 1));
                    ports.put(id, port);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
