package com.github.drxaos.robocoder.program;

public abstract class AbstractProgram implements Runnable {

    protected Bus bus;

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public abstract void run();
}
