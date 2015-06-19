package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.MemoryDriver;


public class Transmitter05 extends AbstractProgram {

    public void run() {
        MemoryDriver memoryDriver = new MemoryDriver(bus);
        bus.request("radio-station::transmit::" + memoryDriver.load());
    }
}
