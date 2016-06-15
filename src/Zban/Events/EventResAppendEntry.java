package Zban.Events;

import Zban.DKVSNode;
import Zban.MySocket;

/**
 * Created by izban on 15.06.16.
 */
public class EventResAppendEntry implements Event {
    String s;
    MySocket client;
    DKVSNode node;

    EventResAppendEntry(String s, MySocket client, DKVSNode node) {
        this.s = s;
        this.client = client;
        this.node = node;
    }

    @Override
    public void execute() {
        node.q.offer(() -> {
            String a[] = s.split(";");
            int cterm = Integer.parseInt(a[1]);
            int ok = Integer.parseInt(a[2]);
            int ccommit = Integer.parseInt(a[3]);

            if (cterm > node.term) {
                node.newTerm(cterm);
                return;
            }

            int cid = client.cid;
            if (ok == 0) {
                node.nextIndex[cid]--;
                if (node.nextIndex[cid] <= node.matchIndex[cid]) throw new AssertionError();
                String msg = node.makeMessage(cid);
                node.clientSockets.get(cid).write(msg);
            } else if (ok == 1) {
                node.matchIndex[cid] = ccommit;
                node.nextIndex[cid] = ccommit + 1;
                if (node.nextIndex[cid] < node.logger.a.size()) {
                    String msg = node.makeMessage(cid);
                    node.clientSockets.get(cid).write(msg);
                }
            } else throw new AssertionError();
            node.updateCommmitIndex();
        });
    }
}
