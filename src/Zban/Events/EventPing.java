package Zban.Events;

import Zban.Constants;
import Zban.MySocket;

/**
 * Created by izban on 14.06.16.
 */
public class EventPing implements Event {
    String s;
    MySocket client;

    EventPing(String s, MySocket client) {
        this.s = s;
        this.client = client;
    }

    @Override
    public void execute() {
        client.write(Constants.PONG);
    }
}
