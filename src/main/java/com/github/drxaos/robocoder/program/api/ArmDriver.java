package com.github.drxaos.robocoder.program.api;

import com.github.drxaos.robocoder.program.Bus;

public class ArmDriver {
    Bus bus;

    public ArmDriver(Bus bus) {
        this.bus = bus;
    }

    public void tieForward() {
        bus.writeRequest("arm::tie::forward");
    }

    public void tieBack() {
        bus.writeRequest("arm::tie::back");
    }

    public void pushForward() {
        bus.writeRequest("arm::push::forward");
    }

    public void pushBack() {
        bus.writeRequest("arm::push::back");
    }

    public void untie() {
        bus.writeRequest("arm::untie");
    }

}
