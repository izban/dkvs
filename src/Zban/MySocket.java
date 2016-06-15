package Zban;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Created by izban on 14.06.16.
 */
public class MySocket {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    public boolean broken;
    public int cid;
    ThreadWriter writer;
    Thread thread;

    MySocket(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());
        broken = false;
        cid = -1;
        writer = new ThreadWriter(out);
        thread = new Thread(writer);
        thread.start();
    }

    public void write(String s) {
        writer.q.offer(s);
    }

    String read() {
        try {
            String s = in.readLine();
            if (broken) s = null;
            return s;
        } catch (IOException e) {
            return null;
        }
    }

    String read(int timeout) {
        FutureTask<String> future = new FutureTask<>(() -> {
            String res = in.readLine();
            if (broken) res = null;
            return res;
        });
        try {
            new Thread(future).start();
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            stop();
            System.err.println(e.toString());
            return Constants.FAIL;
        }
    }

    void stop() {
        socket = null;
        in = null;
        out = null;
        broken = true;
        thread.interrupt();
    }
}
