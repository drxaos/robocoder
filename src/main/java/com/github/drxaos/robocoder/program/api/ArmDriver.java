package com.github.drxaos.robocoder.program.api;

import com.github.drxaos.robocoder.program.Bus;

public class ArmDriver {
    Bus bus;

    public ArmDriver(Bus bus) {
        this.bus = bus;
    }

    public void tieForward() {
        bus.request("arm::tie::forward");
    }

    public void tieBack() {
        bus.request("arm::tie::back");
    }

    public void pushForward() {
        bus.request("arm::push::forward");
    }

    public void pushBack() {
        bus.request("arm::push::back");
    }

    public void untie() {
        bus.request("arm::untie");
    }

}
