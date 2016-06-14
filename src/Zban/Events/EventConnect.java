package Zban.Events;

import Zban.Constants;
import Zban.DKVSNode;
import Zban.DKVSProperties;
import Zban.MySocket;

/**
 * Created by izban on 14.06.16.
 */
public class EventConnect implements Event {
    String s;
    MySocket client;
    DKVSNode node;

    EventConnect(String s, MySocket client, DKVSNode node) {
        this.s = s;
        this.client = client;
        this.node = node;
    }

    @Override
    public void execute() {
        int cid = Integer.parseInt(s.substring(5));
        if (DKVSProperties.ports.containsKey(cid) && cid != node.id) {
            node.clientSockets.remove(cid);
            client.write(Constants.ACCEPTED);
            System.err.println("Send " + Constants.ACCEPTED + " to " + cid);
            node.makeConnection(cid, client);
            return;
        } else {
            client.write(Constants.FAIL);
        }
    }
}
