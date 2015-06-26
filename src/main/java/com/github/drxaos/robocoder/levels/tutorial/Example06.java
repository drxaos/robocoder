package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.*;


public class Example06 extends AbstractProgram {

    public static void main(String[] args) {
        Tutorial06Coop.setSkipFramesMax(5);
        Tutorial06Coop.run(Example06.class);
    }

    public void run() {
        BasicMovement basicMovement = new BasicMovement(bus);
        RadarDriver radarDriver = new RadarDriver(bus);
        ChassisDriver chassisDriver = new ChassisDriver(bus);
        ArmDriver armDriver = new ArmDriver(bus);
        TurretDriver turretDriver = new TurretDriver(bus);

        boolean meFirst = radarDriver.getPosition().y > 10;

        if (meFirst) {

            basicMovement.move(0, 10);

            boolean ready = false;
            while (true) {
                RadarDriver.Result scanObjects = radarDriver.scanObjects(0d);
                if (scanObjects.properties.contains("robot")) {
                    ready = true;
                } else if (ready) {
                    break;
                }
            }

            basicMovement.move(50, 10);

            basicMovement.move(50, 0);

            ready = false;
            while (true) {
                RadarDriver.Result scanObjects = radarDriver.scanObjects(Math.PI * 0.6);
                if (scanObjects.properties.contains("robot")) {
                    ready = true;
                } else if (ready) {
                    break;
                }
            }

            basicMovement.move(52, 10);

            basicMovement.move(52, 20);

        } else {

            basicMovement.move(5, 10);

            basicMovement.move(27, 10);

            basicMovement.move(27, 20);

            boolean ready = false;
            while (true) {
                RadarDriver.Result scanObjects = radarDriver.scanObjects(Math.PI * -0.25);
                if (scanObjects.properties.contains("robot")) {
                    ready = true;
                } else if (ready) {
                    break;
                }
            }

            basicMovement.move(27, 10);

            basicMovement.move(48, 10);

            basicMovement.move(48, 20);
        }

    }
}
