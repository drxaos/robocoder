package com.github.drxaos.robocoder.program.api;

import com.github.drxaos.robocoder.program.Bus;
import straightedge.geom.KPoint;

public class Wheel {

    Bus bus;
    Chassis chassis;
    Radar radar;

    public Wheel(Bus bus) {
        this.bus = bus;
        chassis = new Chassis(bus);
        radar = new Radar(bus);
    }


    public void stop() {
        if (chassis != null) {
            chassis.setLeftAcceleration(0d);
            chassis.setRightAcceleration(0d);
        }
    }

    public boolean rotate(double toAngle, boolean precise, int maxMs) {
        long end = radar.getTime() + maxMs;
        if (chassis == null || radar == null) {
            return false;
        }
        stop();
        while (radar.getTime() < end) {
            double azimuth = differenceAngle(radar.getAngle(), toAngle);
            if (Math.abs(azimuth) < (precise ? 0.0001 : 0.001)) {
                stop();
                return true;
            }
            if (precise) {
                azimuth = Math.max(Math.abs(azimuth) - 0.03, 0.001) * Math.signum(azimuth);
            }
            int force = 500;
            chassis.setLeftAcceleration(-1 * force * azimuth);
            chassis.setRightAcceleration(1 * force * azimuth);
        }
        stop();
        return false;
    }

    public boolean forward(double distance, boolean precise, int maxMs) {
        long end = radar.getTime() + maxMs;
        if (chassis == null || radar == null) {
            return false;
        }
        stop();
        KPoint start = radar.getPosition();
        while (radar.getTime() < end) {
            KPoint current = radar.getPosition();
            double remains = distance - distance(start, current);
            if (Math.abs(remains) < (precise ? 0.001 : 0.01)) {
                stop();
                return true;
            }
            if (precise) {
                remains = Math.max(Math.abs(remains) - 1, 0.01) * Math.signum(remains);
            }
            int force = 100;
            chassis.setLeftAcceleration(1 * force * remains);
            chassis.setRightAcceleration(1 * force * remains);
        }
        stop();
        return false;
    }

    public boolean move(KPoint to, boolean precise, int maxMs) {
        long end = radar.getTime() + maxMs;
        if (chassis == null || radar == null) {
            return false;
        }
        stop();
        double longAng = (precise ? 0.01 : 0.1);
        double shortDist = (precise ? 1.5 : 1.0);
        double shortAng = (precise ? 0.005 : 0.05);
        double enoughDist = (precise ? 0.1 : 0.8);
        while (radar.getTime() < end) {
            KPoint current = radar.getPosition();
            double angleToTarget = angle(current, to);
            double azimuth = differenceAngle(radar.getAngle(), angleToTarget);
            double distance = distance(to, current);
            if (Math.abs(distance) >= shortDist && Math.abs(azimuth) > longAng) {
                rotate(angleToTarget, precise, (int) (end - radar.getTime()));
            } else if (Math.abs(distance) < shortDist && Math.abs(distance) > enoughDist &&
                    (Math.abs(azimuth) > shortAng && Math.abs(azimuth) < Math.PI - shortAng)) {
                if (Math.abs(azimuth) < Math.PI / 2) {
                    rotate(angleToTarget, precise, (int) (end - radar.getTime()));
                } else {
                    rotate(angleToTarget + Math.PI, precise, (int) (end - radar.getTime()));
                }
            } else if (Math.abs(distance) < enoughDist) {
                stop();
                return true;
            } else {
                int force = 80;
                if (precise) {
                    distance = Math.max(Math.abs(distance) - 0.2, 0.01) * Math.signum(distance);
                }
                chassis.setLeftAcceleration(Math.min(1 * force * distance, 100));
                chassis.setRightAcceleration(Math.min(1 * force * distance, 100));
            }
        }
        stop();
        return false;
    }


    public boolean moveSmooth(KPoint to, int maxMs) {
        long end = radar.getTime() + maxMs;
        if (chassis == null || radar == null) {
            return false;
        }
        while (radar.getTime() < end) {
            int force = 80;
            int rotateForce = 120;
            double width = 2;
            KPoint current = radar.getPosition();
            double myAngle = radar.getAngle();
            double angleToTarget = angle(current, to);
            double azimuth = differenceAngle(myAngle, angleToTarget);
            double direction = (Math.abs(azimuth) > Math.PI / 2) ? -1 : 1;
            double distance = distance(to, current);
            if (distance < 1 && Math.abs(azimuth) > 0.2 && Math.abs(azimuth) < Math.PI - 0.2) {
                chassis.setLeftAcceleration(-direction * rotateForce * azimuth);
                chassis.setRightAcceleration(direction * rotateForce * azimuth);
            } else if (distance < 0.2) {
                return true;
            } else {
                double distanceLeft = distance(to, current.translateCopy(Math.cos(myAngle + Math.PI / 2) * width, Math.sin(myAngle + Math.PI / 2) * width));
                double distanceRight = distance(to, current.translateCopy(Math.cos(myAngle - Math.PI / 2) * width, Math.sin(myAngle - Math.PI / 2) * width));
                double distDiff = distanceLeft - distanceRight;
                chassis.setLeftAcceleration(direction * (Math.min(1 * force * distance, 100) - rotateForce * distDiff));
                chassis.setRightAcceleration(direction * (Math.min(1 * force * distance, 100) + rotateForce * distDiff));
            }
        }
        return false;
    }

    public double differenceAngle(double theta1, double theta2) {
        double dif = theta2 - theta1;
        while (dif < -Math.PI) dif += 2 * Math.PI;
        while (dif > Math.PI) dif -= 2 * Math.PI;
        return dif;
    }

    public double angle(KPoint from, KPoint to) {
        return to.findAngle(from);
    }

    public double distance(KPoint from, KPoint to) {
        return from.distance(to);
    }

}
