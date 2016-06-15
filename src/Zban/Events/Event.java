package Zban.Events;

import Zban.Constants;
import Zban.DKVSNode;
import Zban.MySocket;

/**
 * Created by izban on 14.06.16.
 */
public interface Event {
    void execute();

    static Event getEvent(String s, MySocket client, DKVSNode node) {
        if (s.equals(Constants.PING)) {
            return new EventPing(s, client);
        }
        if (s.startsWith(Constants.NODE)) {
            return new EventConnect(s, client, node);
        }
        if (s.equals(Constants.PONG)) {
            return new EventPong(s, client, node);
        }
        if (s.startsWith(Constants.GET)) {
            return new EventGet(s, client, node);
        }
        if (s.startsWith(Constants.REQUEST_VOTE)) {
            return new EventRequestVote(s, client, node);
        }
        if (s.startsWith(Constants.VOTE_RES)) {
            return new EventVoteRes(s, client, node);
        }
        if (s.startsWith(Constants.APPEND_ENTRY)) {
            return new EventAppendEntry(s, client, node);
        }
        if (s.startsWith(Constants.RES_APPEND_ENTRY)) {
            return new EventResAppendEntry(s, client, node);
        }
        if (s.startsWith(Constants.MSG_LEADER)) {
            return new EventMsgLeader(s, client, node);
        }
        if (s.startsWith(Constants.SET)) {
            return new EventSet(s, client, node);
        }
        if (s.startsWith(Constants.DELETE)) {
            return new EventDelete(s, client, node);
        }
        if (s.startsWith(Constants.LEADER_RESPONSE)) {
            return new EventLeaderResponse(s, client, node);
        }
        return null;
    }
}
