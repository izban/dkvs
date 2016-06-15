package Zban.Events;

import Zban.Constants;
import Zban.DKVSNode;
import Zban.Log.LogEntry;
import Zban.MySocket;

/**
 * Created by izban on 15.06.16.
 */
public class EventMsgLeader implements Event {
    String s;
    MySocket client;
    DKVSNode node;

    EventMsgLeader(String s, MySocket client, DKVSNode node) {
        this.s = s;
        this.client = client;
        this.node = node;
    }

    @Override
    public void execute() {
        node.q.offer(() -> {
            s = s.substring(s.indexOf(" ") + 1);
            Integer resp = Integer.parseInt(s.substring(0, s.indexOf(" ")));
            s = s.substring(s.indexOf(" ") + 1);
            String key = s.substring(0, s.indexOf(" "));
            String value = s.substring(s.indexOf(" ") + 1);
            node.logger.add(new LogEntry(key, value, node.term));
            String msg = Constants.LEADER_RESPONSE + " " + resp + " " + (node.logger.a.size() - 1);
            client.write(msg);
            node.sendHeartBeat();
        });
    }
}
