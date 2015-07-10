package com.github.drxaos.robocoder.program.api;

import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.program.Bus;

public class DebugDriver {
    Bus bus;

    public DebugDriver(Bus bus) {
        this.bus = bus;
    }

    public void say(String message) {
        bus.request("debug::say::" + message);
    }

    public void point(KPoint point) {
        bus.request("debug::point::" + point.x + "::" + point.y);
    }
}
