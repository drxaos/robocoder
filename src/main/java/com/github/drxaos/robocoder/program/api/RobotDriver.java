package com.github.drxaos.robocoder.program.api;

import com.github.drxaos.robocoder.program.Bus;
import com.github.drxaos.robocoder.geom.KPoint;

public class RobotDriver {
    Bus bus;

    public RobotDriver(Bus bus) {
        this.bus = bus;
    }

    public void say(String message) {
    }

    public void point(KPoint point) {
    }

}
