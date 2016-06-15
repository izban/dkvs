package Zban;

import Zban.Events.Event;
import Zban.Log.LogEntry;
import Zban.Log.Logger;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static Zban.DKVSProperties.n;
import static Zban.NodeType.CANDIDATE;
import static Zban.NodeType.FOLLOWER;
import static Zban.Constants.*;
import static Zban.NodeType.LEADER;

/**
 * Created by izban on 12.06.2016.
 */
public class DKVSNode {
    public int id;
    int port;
    ServerSocket socket;
    public ConcurrentHashMap<Integer, MySocket> clientSockets = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Integer, Boolean> ponged = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
    public LinkedBlockingQueue<Runnable> q = new LinkedBlockingQueue<>();
    public AtomicLong lastLeaderHeartbeat = new AtomicLong(-1);
    public int term = 1;
    public NodeType type = FOLLOWER;
    public Logger logger;
    public int voteFor = -1;
    public int votes = 0;
    public int nextIndex[], matchIndex[];
    public int commitIndex = 0;

    public DKVSNode(int id) {
        this.id = id;
        this.port = DKVSProperties.ports.get(id);
    }

    public void makeConnection(int cid, MySocket client) {
        client.cid = cid;
        clientSockets.remove(cid);
        clientSockets.put(cid, client);
        new Thread(() -> {
            while (true) {
                String msg = client.read();
                if (msg == null) break;
                System.err.println("Get message " + msg + " from " + cid);

                Event e = Event.getEvent(msg, client, DKVSNode.this);
                if (e == null) {
                    System.err.println("Can't parse message: " + msg + " from server " + cid);
                } else {
                    e.execute();
                }
            }
        }).start();
        System.err.println("Connection between servers " + id + " and " + cid + " is established");
    }

    public void newTerm(int nterm) {
        type = FOLLOWER;
        term = nterm;
        voteFor = -1;
        votes = 0;
    }

    public void run() throws IOException {
        try {
            socket = new ServerSocket(port);
        } catch (BindException e) {
            System.err.println("there is already working server at this port");
            e.printStackTrace();
            return;
        }
        logger = new Logger(id);
        for (int i = 0; i < logger.a.size(); i++) {
            LogEntry b = logger.a.get(i);
            if (b.value.isEmpty()) map.remove(b.key);
            else map.put(b.key, b.value);
        }
        System.err.println("Server " + id + " at port " + port + " started");

        new Thread(() -> {
            while (true) {
                final Socket cur;
                try {
                    cur = socket.accept();
                } catch (IOException e) {
                    return;
                }
                new Thread(() -> {
                    MySocket client = null;
                    try {
                        client = new MySocket(cur);
                    } catch (IOException e) {
                        System.err.println("Can't connect to client");
                        return;
                    }
                    while (true) {
                        String s = client.read();
                        if (s == null) return;
                        System.err.println("Message received: " + s);

                        Event e = Event.getEvent(s, client, DKVSNode.this);
                        if (e == null) {
                            System.err.println("Can't parse message: " + s);
                        } else {
                            e.execute();
                            if (s.startsWith(Constants.NODE)) {
                                return;
                            }
                        }
                    }
                }).start();
            }
        }).start();

        DKVSProperties.ports.forEach((cid, cport) -> {
            if (cid == id) return;
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(Constants.TIMEOUT_RECONNECT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (clientSockets.containsKey(cid)) {
                        ponged.put(cid, false);
                        clientSockets.get(cid).write(Constants.PING);
                        System.err.println("Send ping " + Constants.PING + " to server " + cid);
                        try {
                            Thread.sleep(Constants.TIMEOUT);
                        } catch (InterruptedException e) {
                            return;
                        }
                        String s = ponged.get(cid) ? Constants.PONG : Constants.FAIL;
                        System.err.println("Get pong " + s + " from server " + cid);
                        if (s.equals(Constants.PONG)) { // Server is alive, everything is ok
                            System.err.println("Connection between servers " + id + " and " + cid + " is OK");
                            try {
                                Thread.sleep(Constants.TIMEOUT_RECONNECT);
                                continue;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                return;
                            }
                        } else {
                            System.err.println("Connection between servers " + id + " and " + cid + " is broken");
                            clientSockets.get(cid).broken = true;
                        }
                    }
                    clientSockets.remove(cid);
                    if (id > cid) continue;
                    try {
                        MySocket socket1 = new MySocket(new Socket("localhost", cport));
                        socket1.write(Constants.NODE + " " + id);
                        System.err.println("tried to connect to " + cid + " server ");
                        String res = socket1.read(Constants.TIMEOUT);
                        System.err.println("answer to connecting is " + res);
                        if (res.equals(Constants.ACCEPTED)) {
                            makeConnection(cid, socket1);
                        }
                    } catch (IOException e) {
                        System.err.println("Connection between servers " + id + " and " + cid + " is not established");
                    }
                }
            }).start();
        });

        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.scheduleAtFixedRate(() -> {
            long cur = System.currentTimeMillis();
            if (cur - lastLeaderHeartbeat.get() > Constants.ELECTION_TIMEOUT) {
                q.offer(() -> {
                    System.err.println("Starting new electory, new term is " + (term + 1));
                    newTerm(term + 1);
                    type = CANDIDATE;
                    votes++;
                    for (int cid = 1; cid <= n; cid++) if (cid != id) {
                        if (clientSockets.containsKey(cid)) {
                            String res = REQUEST_VOTE + " " + term + " " + id + " " + logger.getLastId() + " " + logger.getLastTerm();
                            clientSockets.get(cid).write(res);
                        }
                    }
                });
            }
        }, Constants.ELECTION_TIMEOUT, Constants.ELECTION_TIMEOUT + new Random(id).nextInt(ELECTION_TIMEOUT / 4), TimeUnit.MILLISECONDS);

        while (true) {
            Runnable o;
            try {
                o = q.take();
            } catch (InterruptedException e) {
                return;
            }
            new Thread(o).run();
        }
    }

    public void sendHeartBeat() {
        if (type != LEADER) {
            throw new AssertionError();
        }
        for (int cid = 1; cid <= n; cid++) if (cid != id) {
            if (clientSockets.containsKey(cid)) {

            }
        }
    }
}
