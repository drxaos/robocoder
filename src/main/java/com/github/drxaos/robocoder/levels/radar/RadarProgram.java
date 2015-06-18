package com.github.drxaos.robocoder.levels.radar;

import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.ArmDriver;
import com.github.drxaos.robocoder.program.api.BasicMovement;
import com.github.drxaos.robocoder.program.api.ChassisDriver;
import com.github.drxaos.robocoder.program.api.RadarDriver;
import com.github.drxaos.robocoder.geom.KPoint;


public class RadarProgram extends AbstractProgram {

    public void run() {
        BasicMovement basicMovement = new BasicMovement(bus);
        RadarDriver radarDriver = new RadarDriver(bus);
        ChassisDriver chassisDriver = new ChassisDriver(bus);
        ArmDriver armDriver = new ArmDriver(bus);

        while (true) {
            int points = 0;
            double sx = 0, sy = 0;
            for (int i = 0; i < 360; i += 2) {
                double angle = Math.PI * i / 180;
                RadarDriver.Result scan = radarDriver.scan(angle, true);
                if (scan.properties.contains("box")) {
                    points++;
                    sx += Math.cos(angle) * scan.distance;
                    sy += Math.sin(angle) * scan.distance;
                } else if(points > 0) {
                    break;
                }
            }
            if (points == 0) {
                continue;
            }
            sx = sx / points;
            sy = sy / points;
            KPoint target = radarDriver.getPosition().translateCopy(sx, sy);
            if (Math.abs(target.getX()) < 1 && Math.abs(target.getY()) < 1) {
                continue;
            }

            basicMovement.move(target, 0.5, 10000);

            RadarDriver.Result scan = radarDriver.scan(radarDriver.getAngle(), true);
            if (scan != null && scan.distance < 2 && scan.properties.contains("box")) {
                armDriver.tieForward();
                basicMovement.move(new KPoint(0, 0), 0.5, 10000);
                armDriver.untie();

                while (true) {
                    RadarDriver.Result scan2 = radarDriver.scan(radarDriver.getAngle(), true);
                    if (scan2 == null || scan2.distance >= 2 || !scan2.properties.contains("box")) {
                        break;
                    }
                }
                long wait = radarDriver.getTime() + 150;
                while (radarDriver.getTime() < wait) {
                    // wait
                }
            }
        }
    }
}
