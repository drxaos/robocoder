package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.ArmDriver;
import com.github.drxaos.robocoder.program.api.BasicMovement;
import com.github.drxaos.robocoder.program.api.ChassisDriver;
import com.github.drxaos.robocoder.program.api.RadarDriver;


public class Example01 extends AbstractProgram {

    public static void main(String[] args) {
        Tutorial01Chassis.run(Example01.class);
    }

    public void run() {
        BasicMovement basicMovement = new BasicMovement(bus);
        RadarDriver radarDriver = new RadarDriver(bus);
        ChassisDriver chassisDriver = new ChassisDriver(bus);
        ArmDriver armDriver = new ArmDriver(bus);


        basicMovement.move(-15, 15);
        basicMovement.move(0, 20);
        basicMovement.move(10, 10);
        basicMovement.move(20, 20);
        basicMovement.move(0, 0);

    }
}
