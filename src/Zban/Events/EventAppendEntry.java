package Zban.Events;

import Zban.Constants;
import Zban.DKVSNode;
import Zban.Log.LogEntry;
import Zban.MySocket;
import sun.rmi.runtime.Log;

/**
 * Created by izban on 15.06.16.
 */
public class EventAppendEntry implements Event {
    String s;
    MySocket client;
    DKVSNode node;

    EventAppendEntry(String s, MySocket client, DKVSNode node) {
        this.s = s;
        this.client = client;
        this.node = node;
    }

    static public String makeEvent(int term, int id, int prevLogId, int prevLogTerm, LogEntry entry, int leaderCommit) {
        String res = Constants.APPEND_ENTRY + ";" + term + ";" + id + ";" + prevLogId + ";" + prevLogTerm + ";" + (entry == null ? "null" : entry.toString()) + ";" + leaderCommit;
        return res;
    }

    @Override
    public void execute() {
        node.lastLeaderHeartbeat.set(System.currentTimeMillis());
        node.q.offer(() -> {
            String a[] = s.split(";");
            int term = Integer.valueOf(a[1]);
            int cid = Integer.valueOf(a[2]);
            int prevLogId = Integer.valueOf(a[3]);
            int prevLogTerm = Integer.valueOf(a[4]);
            String entry = a[5];
            int leaderCommit = Integer.valueOf(a[6]);
            int entryTerm = (entry.equals("null") ? -1 : LogEntry.parseEntry(entry).term);
            if (term > node.term) {
                node.newTerm(term);
            }

            int ok = 0;
            if (term >= node.term) {
                if (node.logger.a.size() > prevLogId && node.logger.a.get(prevLogId).term == prevLogTerm) {
//                    int curEntryTerm = node.logger.a.size() > prevLogId + 1 ? node.logger.a.get(prevLogId + 1).term : -1;
//                    if (curEntryTerm != entryTerm) {
                    while (node.logger.a.size() > prevLogId + 1) {
                        node.logger.a.remove(node.logger.a.size() - 1);
                    }
//                    }
                    if (node.commitIndex >= node.logger.a.size()) throw new AssertionError();
                    if (!entry.equals("null")) {
                        node.logger.a.add(LogEntry.parseEntry(entry));
                    }
                    ok = 1;
                    if (leaderCommit > node.commitIndex) {
                        node.commitIndex = Math.min(leaderCommit, node.logger.a.size() - 1);
                        node.updateStateMachine();
                    }
                }
            }
            client.write(Constants.RES_APPEND_ENTRY + ";" + node.term + ";" + ok + ";" + node.commitIndex);
        });
    }
}
