package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.*;


public class Example07 extends AbstractProgram {

    public static void main(String[] args) {
        Tutorial07Lasers.setSkipFramesMax(5);
        Tutorial07Lasers.run(Example07.class);
    }

    BasicMovement basicMovement;
    RadarDriver radarDriver;
    ChassisDriver chassisDriver;
    ArmDriver armDriver;
    TurretDriver turretDriver;

    public void run() {
        basicMovement = new BasicMovement(bus);
        radarDriver = new RadarDriver(bus);
        chassisDriver = new ChassisDriver(bus);
        armDriver = new ArmDriver(bus);
        turretDriver = new TurretDriver(bus);

        KPoint position = radarDriver.getPosition();
        basicMovement.move(position.x, 10);
        while (true) {
            position = radarDriver.getPosition();
            RadarDriver.Result scanLaser = radarDriver.scanSensors(0d);
            if (!scanLaser.properties.contains("laser")) {
                basicMovement.move(57, 20);
                return;
            }
            KPoint laser = position.createPointFromAngle(0, scanLaser.distance);

            KPoint box = locateObject("box");
            basicMovement.move(box);

            armDriver.tieForward();
            basicMovement.forward(3, -50);

            basicMovement.move(laser.x - 5, laser.y);
            basicMovement.rotate(0d);

            position = radarDriver.getPosition();
            basicMovement.forward(laser.x - position.x - 2);

            armDriver.untie();

            position = radarDriver.getPosition();

            basicMovement.forward(5, -50);
            basicMovement.move(laser.x - 5, laser.y + 5);
            RadarDriver.Result scan = radarDriver.scanSensors(0d);
            if (scan.distance < 7) {
                basicMovement.move(laser.x - 5, laser.y - 5);
            }
            position = radarDriver.getPosition();
            basicMovement.move(laser.x + 5, position.y);
            basicMovement.move(laser.x + 5, 10);
        }
    }

    private KPoint locateObject(String type) {
        int points = 0;
        double sx = 0, sy = 0;
        for (int i = 0; i < 360; i += 2) {
            double angle = Math.PI * i / 180;
            RadarDriver.Result scan = radarDriver.scan(angle, true);
            if (scan.properties.contains(type)) {
                points++;
                sx += Math.cos(angle) * scan.distance;
                sy += Math.sin(angle) * scan.distance;
            } else if (points > 0) {
                break;
            }
        }
        if (points == 0) {
            return null;
        }
        sx = sx / points;
        sy = sy / points;
        KPoint target = radarDriver.getPosition().translateCopy(sx, sy);

        return target;
    }
}
