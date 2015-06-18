package com.github.drxaos.robocoder.program.api;

import com.github.drxaos.robocoder.program.Bus;

public class TurretDriver {
    Bus bus;

    public TurretDriver(Bus bus) {
        this.bus = bus;
    }

    public void fire() {
        bus.request("turret::fire");
    }

}
