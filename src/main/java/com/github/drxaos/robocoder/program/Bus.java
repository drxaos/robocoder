package com.github.drxaos.robocoder.program;

import java.util.concurrent.locks.ReentrantLock;

public class Bus {

    private ReentrantLock reqLock = new ReentrantLock();
    private volatile String request;
    private volatile String response;
    private volatile long reqId = 0;
    private volatile boolean active = true;

    private void debug(String msg) {
        //System.out.println(Thread.currentThread().getName() + ": " + msg);
    }

    public synchronized String request(String e) {
        if (!active) {
            return null;
        }
        try {
            reqLock.lock();

            debug("writeRequest: " + e);
            while (request != null) {
                try {
                    debug("writeRequest wait");
                    wait(1000);    // Block while full
                    if (!active) {
                        throw new RuntimeException("bus destroyed");
                    }
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
                    wait(1000);    // Block while empty
                    if (!active) {
                        throw new RuntimeException("bus destroyed");
                    }
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

    public synchronized void waitRequestFromRunningThread(Thread thread) {
        if (!active) {
            return;
        }
        debug("waitRequest");
        while (request == null && thread.getState() == Thread.State.RUNNABLE) {
            try {
                debug("waitRequest wait");
                wait(1000);    // Block while empty
                if (!active) {
                    throw new RuntimeException("bus destroyed");
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        debug("waitRequest done");
    }

    public synchronized String getRequest() {
        if (!active) {
            return null;
        }
        debug("getRequest");
        String e = request;
        debug("getRequest done: " + e);
        return e;
    }

    public synchronized long getRequestId() {
        if (!active) {
            return -1;
        }
        debug("getRequestId");
        long e = -1;
        if (request != null) {
            e = reqId;
        }
        debug("getRequestId done: " + e);
        return e;
    }

    public synchronized void writeResponse(String e) {
        if (!active) {
            return;
        }
        if (request != null) {
            debug("removeRequest");
            request = null;
            debug("writeResponse: " + e);
            response = e;
        }
        notifyAll();                // Awaken any waiting read
    }

    public void destroy() {
        active = false;
        if (reqLock.isLocked()) {
            reqLock.unlock();
        }
        request = null;
        response = null;
        reqId = -1;
    }
}
