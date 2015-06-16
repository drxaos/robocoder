package com.github.drxaos.robocoder.program.api;

import com.github.drxaos.robocoder.program.Bus;

public class ChassisDriver {

    Bus bus;

    public ChassisDriver(Bus bus) {
        this.bus = bus;
    }

    public void setLeftAcceleration(Double percent) {
        bus.request("chassis::left::" + percent);
    }

    public void setRightAcceleration(Double percent) {
        bus.request("chassis::right::" + percent);
    }

}
