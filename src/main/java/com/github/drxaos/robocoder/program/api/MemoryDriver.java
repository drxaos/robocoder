package com.github.drxaos.robocoder.program.api;

import com.github.drxaos.robocoder.program.Bus;

public class MemoryDriver {
    Bus bus;

    public MemoryDriver(Bus bus) {
        this.bus = bus;
    }

    public void save(String data) {
        bus.request("memory::save::" + data);
    }

    public String load() {
        return bus.request("memory::load");
    }

}
