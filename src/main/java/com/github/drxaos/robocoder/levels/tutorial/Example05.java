package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.*;


public class Example05 extends AbstractProgram {

    public static void main(String[] args) {
        Tutorial05Bus.setSkipFramesMax(0);
        Tutorial05Bus.run(Example05.class);
    }

    public void run() {
        BasicMovement basicMovement = new BasicMovement(bus);
        RadarDriver radarDriver = new RadarDriver(bus);
        ChassisDriver chassisDriver = new ChassisDriver(bus);
        ArmDriver armDriver = new ArmDriver(bus);
        TurretDriver turretDriver = new TurretDriver(bus);

        while (true) {
            basicMovement.sleep(30);
            String recv = bus.request("radio::receive");
            if (recv != null && !recv.isEmpty()) {
                System.out.println(recv);
            } else {
                System.out.println("------");
            }
        }

    }
}
