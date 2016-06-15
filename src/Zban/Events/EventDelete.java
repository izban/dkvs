package Zban.Events;

import Zban.Constants;
import Zban.DKVSNode;
import Zban.Log.LogEntry;
import Zban.MySocket;
import Zban.NodeType;

/**
 * Created by izban on 15.06.16.
 */
public class EventDelete implements Event {
    String s;
    MySocket client;
    DKVSNode node;

    EventDelete(String s, MySocket client, DKVSNode node) {
        this.s = s;
        this.client = client;
        this.node = node;
    }

    @Override
    public void execute() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (node.leaderId == -1 && node.type != NodeType.LEADER) {
                    node.q.offer(this);
                    return;
                }
                LogEntry entry;
                s = s.substring(Constants.DELETE.length() + 1);
                String key = s;
                int sz = node.responses.size();
                if (node.type == NodeType.LEADER) {
                    node.logger.add(new LogEntry(key, "", node.term));
                    node.responses.put(sz, () -> {
                        if (!client.broken) {
                            client.write(Constants.DELETED + " " + key);
                        }
                    });
                    node.responseId.put(node.logger.a.size() - 1, sz);
                    node.sendHeartBeat();
                    return;
                }

                node.responses.put(sz, () -> {
                    if (!client.broken) {
                        client.write(Constants.DELETED + " " + key);
                    }
                });
                String msg = Constants.MSG_LEADER + " " + sz + " " + key + " ";
                node.clientSockets.get(node.leaderId).write(msg);
            }
        };
        node.q.offer(r);
    }
}
