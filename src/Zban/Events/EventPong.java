package Zban.Events;

import Zban.DKVSNode;
import Zban.MySocket;

/**
 * Created by izban on 14.06.16.
 */
public class EventPong implements Event {
    String s;
    MySocket client;
    DKVSNode node;

    EventPong(String s, MySocket client, DKVSNode node) {
        this.s = s;
        this.client = client;
        this.node = node;
    }

    @Override
    public void execute() {
        node.ponged.put(client.cid, true);
    }
}
