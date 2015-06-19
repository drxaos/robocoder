package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.*;


public class Example05 extends AbstractProgram {

    public static void main(String[] args) {
        Tutorial05Bus.setSkipFramesMax(10);
        Tutorial05Bus.run(Example05.class);
    }

    public void run() {
        BasicMovement basicMovement = new BasicMovement(bus);
        RadarDriver radarDriver = new RadarDriver(bus);
        ChassisDriver chassisDriver = new ChassisDriver(bus);
        ArmDriver armDriver = new ArmDriver(bus);
        TurretDriver turretDriver = new TurretDriver(bus);

        double x = 0;
        double y = 0;
        while (true) {
            basicMovement.sleep(30);
            String recv = bus.request("radio::receive");
            if (recv == null || recv.isEmpty()) {
                break;
            }
            String[] split = recv.split("/");
            x = Double.parseDouble(split[0]);
            y = Double.parseDouble(split[1]);
            basicMovement.move(x - 7, y);
            basicMovement.move(x, y + 7);
            basicMovement.move(x + 7, y);
        }

        basicMovement.move(x, y);
    }
}
