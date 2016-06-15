package Zban.Events;

import Zban.*;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static Zban.Constants.ELECTION_TIMEOUT;
import static Zban.Constants.REQUEST_VOTE;
import static Zban.DKVSProperties.n;
import static Zban.NodeType.CANDIDATE;
import static Zban.NodeType.LEADER;

/**
 * Created by izban on 14.06.16.
 */
public class EventVoteRes implements Event {
    String s;
    MySocket client;
    DKVSNode node;

    EventVoteRes(String s, MySocket client, DKVSNode node) {
        this.s = s;
        this.client = client;
        this.node = node;
    }

    @Override
    public void execute() {
        node.q.offer(() -> {
            String a[] = s.split(" ");
            int term = Integer.parseInt(a[1]);
            int voted = Integer.parseInt(a[2]);
            if (term > node.term) {
                node.votes = -n;
                node.term = term;
            }
            node.votes += voted;
            System.err.println(node.votes + " " + n);
            if (node.votes * 2 > n) {
                node.type = NodeType.LEADER;
                System.err.println("I, server " + node.id + ", became the leader! FTW!");
                node.nextIndex = new int[n + 1];
                node.matchIndex = new int[n + 1];
                for (int i = 1; i <= n; i++) {
                    node.nextIndex[i] = node.logger.a.size();
                    node.matchIndex[i] = -1;
                }
                ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
                service.scheduleAtFixedRate(() -> {
                    if (node.type != LEADER) {
                        service.shutdown();
                    } else {
                        node.sendHeartBeat();
                    }
                }, 0, Constants.LEADER_TIMEOUT, TimeUnit.MILLISECONDS);
            }
        });
    }
}
