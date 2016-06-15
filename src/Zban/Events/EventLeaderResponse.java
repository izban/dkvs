package Zban.Events;

import Zban.DKVSNode;
import Zban.MySocket;

/**
 * Created by izban on 15.06.16.
 */
public class EventLeaderResponse implements Event {
    String s;
    MySocket client;
    DKVSNode node;

    EventLeaderResponse(String s, MySocket client, DKVSNode node) {
        this.s = s;
        this.client = client;
        this.node = node;
    }

    @Override
    public void execute() {
        node.q.offer(() -> {
            String a[] = s.split(" ");
            node.responseId.put(Integer.parseInt(a[2]), Integer.parseInt(a[1]));
        });
    }
}
