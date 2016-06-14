import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by izban on 12.06.2016.
 */
public class DKVSNode {
    int id;
    int port;
    ServerSocket socket;

    public DKVSNode(int id) {
        this.id = id;
        this.port = DKVSProperties.ports.get(id);
    }

    public void run() throws IOException {
        socket = new ServerSocket(port);

    }
}
