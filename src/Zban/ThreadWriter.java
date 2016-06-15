package Zban;

import Zban.MySocket;

import java.io.PrintWriter;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by izban on 14.06.16.
 */
public class ThreadWriter implements Runnable {
    PrintWriter out;
    LinkedBlockingQueue<String> q = new LinkedBlockingQueue<>();

    ThreadWriter(PrintWriter out) {
        this.out = out;
    }


    public void run() {
        while (true) {
            String s;
            try {
                s = q.take();
            } catch (InterruptedException e) {
                s = null;
            }
            if (s == null) return;
            out.println(s);
            out.flush();
        }
    }
}
