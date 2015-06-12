package com.github.drxaos.robocoder.program.api;

import com.github.drxaos.robocoder.program.Bus;

public class Chassis {

    Bus bus;

    public Chassis(Bus bus) {
        this.bus = bus;
    }

    public void setLeftAcceleration(Double percent) {
        bus.writeRequest("chassis::left::" + percent);
    }

    public void setRightAcceleration(Double percent) {
        bus.writeRequest("chassis::right::" + percent);
    }

}
