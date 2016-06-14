package Zban.Events;

import Zban.Constants;
import Zban.DKVSNode;
import Zban.MySocket;

/**
 * Created by izban on 14.06.16.
 */
public class EventGet implements Event {
    String s;
    MySocket client;
    DKVSNode node;

    EventGet(String s, MySocket client, DKVSNode node) {
        this.s = s;
        this.client = client;
        this.node = node;
    }

    @Override
    public void execute() {
        String key = s.substring(Constants.GET.length() + 1);

        node.q.offer(() -> {
            new Thread(() -> {
                String res;
                if (node.map.containsKey(key)) {
                    res = Constants.VALUE + " " + key + " " + node.map.get(key);
                } else {
                    res = Constants.NOT_FOUND;
                }
                client.write(res);
            }).start();
        });
    }
}
