package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.*;


public class Example04 extends AbstractProgram {

    public static void main(String[] args) {
        Tutorial04Arm.setSkipFramesMax(20);
        Tutorial04Arm.run(Example04.class);
    }

    public void run() {
        BasicMovement basicMovement = new BasicMovement(bus);
        RadarDriver radarDriver = new RadarDriver(bus);
        ChassisDriver chassisDriver = new ChassisDriver(bus);
        ArmDriver armDriver = new ArmDriver(bus);
        TurretDriver turretDriver = new TurretDriver(bus);


        int col = 1, row = 1;
        KPoint start = radarDriver.getPosition();

        while (true) {
            Float x = null, y = null;
            search:
            {
                for (int i = 0; i < 90; i++) {
                    float angle = (float) (i * Math.PI / 180);
                    RadarDriver.Result scan = radarDriver.scan((double) angle, true);
                    if (scan != null && scan.properties.contains("box")) {
                        x = (float) (start.getX() + Math.cos(angle) * scan.distance - 1f);
                        y = (float) (start.getY() + Math.sin(angle) * scan.distance - 1f);
                        break search;
                    }
                }
                for (int i = 180; i > 90; i--) {
                    float angle = (float) (i * Math.PI / 180);
                    RadarDriver.Result scan = radarDriver.scan((double) angle, true);
                    if (scan != null && scan.properties.contains("box")) {
                        x = (float) (start.getX() + Math.cos(angle) * scan.distance + 1f);
                        y = (float) (start.getY() + Math.sin(angle) * scan.distance - 1f);
                        break search;
                    }
                }
                for (int i = 0; i > -90; i--) {
                    float angle = (float) (i * Math.PI / 180);
                    RadarDriver.Result scan = radarDriver.scan((double) angle, true);
                    if (scan != null && scan.properties.contains("box")) {
                        x = (float) (start.getX() + Math.cos(angle) * scan.distance - 1f);
                        y = (float) (start.getY() + Math.sin(angle) * scan.distance + 1f);
                        break search;
                    }
                }
                for (int i = -180; i < -90; i++) {
                    float angle = (float) (i * Math.PI / 180);
                    RadarDriver.Result scan = radarDriver.scan((double) angle, true);
                    if (scan != null && scan.properties.contains("box")) {
                        x = (float) (start.getX() + Math.cos(angle) * scan.distance + 1f);
                        y = (float) (start.getY() + Math.sin(angle) * scan.distance + 1f);
                        break search;
                    }
                }
            }
            if (x != null & y != null) {
                basicMovement.move(new KPoint(x, start.y), 0.3, 10000);
                basicMovement.rotate(Math.PI / 2 * (y > start.y ? 1 : -1));
                basicMovement.move(new KPoint(x, y), 0.3, 10000);

                armDriver.tieForward();

                chassisDriver.setLeftAcceleration(-50d);
                chassisDriver.setRightAcceleration(-50d);

                float nx = 5 + 20 - 2 - col * 3;
                float ny = 20 - row * 3;

                if (y > start.y) {
                    while (radarDriver.getPosition().getY() > start.getY()) ;
                } else {
                    while (radarDriver.getPosition().getY() < start.getY()) ;
                }

                basicMovement.move(new KPoint(nx - 5, ny), 0.6, 10000);
                basicMovement.rotate(0);
                basicMovement.move(new KPoint(nx, ny), 0.6, 10000);

                armDriver.untie();

                chassisDriver.setLeftAcceleration(-50d);
                chassisDriver.setRightAcceleration(-50d);

                while (radarDriver.getPosition().getX() > nx - 5) ;

                basicMovement.move(start, 0.5, 10000);

                row++;
                if (row > 6) {
                    col++;
                    row = 1;
                }
            } else {
                break;
            }
        }

        float nx = 5 + 20 - 2 - col * 3;
        float ny = 20 - row * 3;

        basicMovement.move(nx - 5, ny);
        basicMovement.move(nx, ny);
    }
}
