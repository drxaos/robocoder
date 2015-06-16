package com.github.drxaos.robocoder.program;

import java.util.concurrent.locks.ReentrantLock;

public class Bus {

    private ReentrantLock reqLock = new ReentrantLock();
    private volatile String request;
    private volatile String response;
    private volatile long reqId = 0;

    private void debug(String msg) {
        System.out.println(Thread.currentThread().getName() + ": " + msg);
    }

    public synchronized String request(String e) {
        try {
            reqLock.lock();

            debug("writeRequest: " + e);
            while (request != null) {
                try {
                    debug("writeRequest wait");
                    wait();    // Block while full
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            reqId++;
            request = e;
            notifyAll();                // Awaken any waiting read
            debug("writeRequest done");

            debug("readResponse");
            while (response == null) {
                try {
                    debug("readResponse wait");
                    wait();    // Block while empty
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            e = response;
            response = null;
            notifyAll();                // Awaken any waiting write
            debug("readResponse done: " + e);
            return e;
        } finally {
            reqLock.unlock();
        }
    }

    public synchronized String getRequest() {
        debug("getRequest");
        String e = request;
        debug("getRequest done: " + e);
        return e;
    }

    public synchronized long getRequestId() {
        debug("getRequestId");
        long e = -1;
        if (request != null) {
            e = reqId;
        }
        debug("getRequestId done: " + e);
        return e;
    }

    public synchronized void writeResponse(String e) {
        if (request != null) {
            debug("removeRequest");
            request = null;
            debug("writeResponse: " + e);
            response = e;
        }
        notifyAll();                // Awaken any waiting read
    }
}
