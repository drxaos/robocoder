package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.*;


public class Example03 extends AbstractProgram {

    public static void main(String[] args) {
        Tutorial03Radar.setSkipFramesMax(8);
        Tutorial03Radar.run(Example03.class);
    }

    public void run() {
        BasicMovement basicMovement = new BasicMovement(bus);
        RadarDriver radarDriver = new RadarDriver(bus);
        ChassisDriver chassisDriver = new ChassisDriver(bus);
        ArmDriver armDriver = new ArmDriver(bus);
        TurretDriver turretDriver = new TurretDriver(bus);

        KPoint prev = radarDriver.getPosition();
        while (true) {

            RadarDriver.Result scan = radarDriver.scan(0d, false);
            if (scan != null && scan.properties.contains("finishpad") && scan.distance < 0.3) {
                break;
            }

            double lastMoveAngle = basicMovement.angle(prev, radarDriver.getPosition());
            Double angle = 1f * Math.round(lastMoveAngle / Math.PI * 8) / 8 * Math.PI + Math.PI / 2;
            while (true) {
                scan = radarDriver.scan(angle, true);
                while (scan != null && scan.properties.contains("breakable")) {
                    basicMovement.rotate(angle);
                    turretDriver.fire();
                    scan = radarDriver.scan(angle, true);
                }
                if (scan == null || scan.distance > 4.5) {
                    KPoint position = radarDriver.getPosition();
                    prev = position.copy();
                    position.setCoords(Math.round(position.x / 4 * 4), Math.round(position.y / 4 * 4));
                    position.translate(Math.round(Math.cos(angle) * 4), Math.round(Math.sin(angle) * 4));
                    basicMovement.move(position, 0.5, 10000);
                    break;
                }
                angle -= Math.PI / 2;
            }

        }

    }
}
