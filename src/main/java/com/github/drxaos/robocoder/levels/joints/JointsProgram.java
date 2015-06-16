package com.github.drxaos.robocoder.levels.joints;

import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.ArmDriver;
import com.github.drxaos.robocoder.program.api.BasicMovement;
import com.github.drxaos.robocoder.program.api.ChassisDriver;
import com.github.drxaos.robocoder.program.api.RadarDriver;
import straightedge.geom.KPoint;


public class JointsProgram extends AbstractProgram {

    public void run() {
        BasicMovement basicMovement = new BasicMovement(bus);
        RadarDriver radarDriver = new RadarDriver(bus);
        ChassisDriver chassisDriver = new ChassisDriver(bus);
        ArmDriver armDriver = new ArmDriver(bus);

        basicMovement.move(new KPoint(0, 0), 1, 10000);
        basicMovement.stop();

        while (true) {
            armDriver.tieForward();

            basicMovement.move(new KPoint(10, 0), 1, 10000);
            basicMovement.stop();

            armDriver.pushForward();
            armDriver.pushForward();
            armDriver.pushForward();
            armDriver.pushForward();
            armDriver.pushForward();

            chassisDriver.setLeftAcceleration(100d);
            chassisDriver.setRightAcceleration(100d);

            long end = radarDriver.getTime() + 150;
            while (radarDriver.getTime() < end) {
                armDriver.pushForward();
            }
            while (radarDriver.getTime() < end + 25) {
            }
        }
    }
}
