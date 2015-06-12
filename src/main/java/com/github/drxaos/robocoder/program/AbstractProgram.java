package com.github.drxaos.robocoder.program;

import com.github.drxaos.robocoder.program.api.*;

public abstract class AbstractProgram implements Runnable {

    protected Bus bus;
    protected Chassis chassis;
    protected Radar radar;
    protected Robot robot;
    protected Wheel wheel;

    public void setBus(Bus bus) {
        this.bus = bus;
        chassis = new Chassis(bus);
        radar = new Radar(bus);
        robot = new Robot(bus);
        wheel = new Wheel(bus);
    }

    public abstract void run();
}
