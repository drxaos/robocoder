package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.*;


public class Example02 extends AbstractProgram {

    public static void main(String[] args) {
        Tutorial02Turret.run(Example02.class);
    }

    public void run() {
        BasicMovement basicMovement = new BasicMovement(bus);
        RadarDriver radarDriver = new RadarDriver(bus);
        ChassisDriver chassisDriver = new ChassisDriver(bus);
        ArmDriver armDriver = new ArmDriver(bus);
        TurretDriver turretDriver = new TurretDriver(bus);

        while (true) {
            basicMovement.rotate(radarDriver.getAngle() + Math.PI / 4, false, 10000);
            turretDriver.fire();
        }
    }
}
