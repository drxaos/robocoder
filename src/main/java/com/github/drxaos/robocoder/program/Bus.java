package com.github.drxaos.robocoder.program;

public class Bus {

    private String request;
    private String response;

    private void debug(String msg) {
        //System.out.println(msg);
    }

    public synchronized void writeRequest(String e) {
        debug("writeRequest: " + e);
        while (request != null) {
            try {
                debug("writeRequest wait");
                wait();    // Block while full
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        request = e;
        notifyAll();                // Awaken any waiting read
        debug("writeRequest done");
    }

    public synchronized String readRequest() {
        debug("readRequest");
        String e = request;
        request = null;
        notifyAll();                // Awaken any waiting write
        debug("readRequest done: " + e);
        return e;
    }

    public synchronized String peekRequest() {
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

    public synchronized String readResponse() {
        debug("readResponse");
        while (response == null) {
            try {
                debug("readResponse wait");
                wait();    // Block while empty
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String e = response;
        response = null;
        notifyAll();                // Awaken any waiting write
        debug("readResponse done: " + e);
        return e;
    }


}
