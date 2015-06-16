package com.github.drxaos.robocoder.program;

import java.util.concurrent.locks.ReentrantLock;

public class Bus {

    private ReentrantLock reqLock = new ReentrantLock();
    private String request;
    private String response;

    private void debug(String msg) {
        //System.out.println(msg);
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
        debug("peekRequest");
        String e = request;
        notifyAll();                // Awaken any waiting write
        debug("peekRequest done: " + e);
        return e;
    }

    public synchronized void removeRequest() {
        debug("removeRequest");
        request = null;
        notifyAll();                // Awaken any waiting write
    }

    public synchronized void writeResponse(String e) {
        debug("writeResponse: " + e);
        response = e;
        notifyAll();                // Awaken any waiting read
    }
}
