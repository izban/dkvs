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
        return null;
    }
}
