package Zban.Events;

import Zban.Constants;
import Zban.DKVSNode;
import Zban.MySocket;

/**
 * Created by izban on 14.06.16.
 */
public class EventRequestVote implements Event {
    String s;
    MySocket client;
    DKVSNode node;

    EventRequestVote(String s, MySocket client, DKVSNode node) {
        this.s = s;
        this.client = client;
        this.node = node;
    }

    @Override
    public void execute() {
        //node.lastLeaderHeartbeat.set(System.currentTimeMillis());
        node.q.offer(() -> {
            String a[] = s.split(" ");
            int cterm = Integer.parseInt(a[1]);
            int cid = Integer.parseInt(a[2]);
            int clogid = Integer.parseInt(a[3]);
            int clogterm = Integer.parseInt(a[4]);

            if (cterm > node.term) {
                node.newTerm(cterm);
            }
            int voted;
            if (node.voteFor != -1) voted = 0;
            else if (cterm < node.term) voted = 0;
            else if (clogterm > node.logger.getLastTerm() || clogterm == node.logger.getLastTerm() && clogid >= node.logger.getLastId()) voted = 1;
            else voted = 0;
            if (voted > 0) node.voteFor = cid;
            String res = Constants.VOTE_RES + " " + node.term + " " + voted;
            client.write(res);
        });
    }
}
