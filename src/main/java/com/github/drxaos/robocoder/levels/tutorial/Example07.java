package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.*;


public class Example07 extends AbstractProgram {

    public static void main(String[] args) {
        Tutorial07Lasers.setSkipFramesMax(5);
        Tutorial07Lasers.run(Example07.class);
    }

    static final boolean[] flags = new boolean[]{false, false, false, false};

    public void run() {
        BasicMovement basicMovement = new BasicMovement(bus);
        RadarDriver radarDriver = new RadarDriver(bus);
        ChassisDriver chassisDriver = new ChassisDriver(bus);
        ArmDriver armDriver = new ArmDriver(bus);
        TurretDriver turretDriver = new TurretDriver(bus);

        while (true) {
            radarDriver.scan(radarDriver.getAngle(), false);
        }
    }
}
