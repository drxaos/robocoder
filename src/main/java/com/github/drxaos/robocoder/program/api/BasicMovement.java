package com.github.drxaos.robocoder.program.api;

import com.github.drxaos.robocoder.program.Bus;
import straightedge.geom.KPoint;

import java.awt.geom.Point2D;

public class BasicMovement {

    Bus bus;
    ChassisDriver chassisDriver;
    RadarDriver radarDriver;

    public BasicMovement(Bus bus) {
        this.bus = bus;
        chassisDriver = new ChassisDriver(bus);
        radarDriver = new RadarDriver(bus);
    }

    protected void sleep(int steps) {
        long end = radarDriver.getTime() + steps - 2;
        while (radarDriver.getTime() < end) ;
    }

    public void stop() {
        if (chassisDriver != null) {
            chassisDriver.setLeftAcceleration(0d);
            chassisDriver.setRightAcceleration(0d);
        }
    }

    public boolean rotate(double toAngle, boolean precise, int maxMs) {
        if (chassisDriver == null || radarDriver == null) {
            return false;
        }
        long end = radarDriver.getTime() + maxMs;
        stop();
        while (radarDriver.getTime() < end) {
            double azimuth = differenceAngle(radarDriver.getAngle(), toAngle);
            if (Math.abs(azimuth) < (precise ? 0.0001 : 0.001)) {
                stop();
                return true;
            }
            if (precise) {
                azimuth = Math.max(Math.abs(azimuth) - 0.03, 0.001) * Math.signum(azimuth);
            }
            int force = 500;
            chassisDriver.setLeftAcceleration(-1 * force * azimuth);
            chassisDriver.setRightAcceleration(1 * force * azimuth);
        }
        stop();
        return false;
    }

    public boolean forward(double distance, boolean precise, int maxMs) {
        if (chassisDriver == null || radarDriver == null) {
            return false;
        }
        long end = radarDriver.getTime() + maxMs;
        stop();
        KPoint start = radarDriver.getPosition();
        while (radarDriver.getTime() < end) {
            KPoint current = radarDriver.getPosition();
            double remains = distance - distance(start, current);
            if (Math.abs(remains) < (precise ? 0.001 : 0.01)) {
                stop();
                return true;
            }
            if (precise) {
                remains = Math.max(Math.abs(remains) - 1, 0.01) * Math.signum(remains);
            }
            int force = 100;
            chassisDriver.setLeftAcceleration(1 * force * remains);
            chassisDriver.setRightAcceleration(1 * force * remains);
        }
        stop();
        return false;
    }

    public boolean move(double x, double y) {
        return move(new KPoint(x, y), 0.5d, 100000);
    }

    public boolean move(KPoint to, double accuracy, int timeout) {
        if (Double.isNaN(to.getX()) | Double.isNaN(to.getX())) {
            return false;
        }
        long end = radarDriver.getTime() + timeout;
        while (radarDriver.getTime() < end) {
            int force = 80;
            int rotateForce = 120;
            float width = 2;
            KPoint current = radarDriver.getPosition();
            double myAngle = radarDriver.getAngle();
            //System.out.println(current.toString() + ", a:" + myAngle / Math.PI * 180);
            double angleToTarget = angle(current, to);
            double azimuth = differenceAngle(myAngle, angleToTarget);
            double distance = distance(to, current);
            if (distance < 1 && Math.abs(azimuth) > accuracy && Math.abs(azimuth) < Math.PI - accuracy) {
                chassisDriver.setLeftAcceleration(-rotateForce * azimuth);
                chassisDriver.setRightAcceleration(rotateForce * azimuth);
            } else if (distance > 1 && Math.abs(azimuth) >= Math.PI / 2) {
                chassisDriver.setLeftAcceleration(-rotateForce * azimuth);
                chassisDriver.setRightAcceleration(rotateForce * azimuth);
            } else if (distance < accuracy) {
                stop();
                return true;
            } else {
                double distanceLeft = distance(to, new KPoint(current.getX() + (float) Math.cos(myAngle + Math.PI / 2) * width, current.getY() + (float) Math.sin(myAngle + Math.PI / 2) * width));
                double distanceRight = distance(to, new KPoint(current.getX() + (float) Math.cos(myAngle - Math.PI / 2) * width, current.getY() + (float) Math.sin(myAngle - Math.PI / 2) * width));
                double distDiff = distanceLeft - distanceRight;
                double left = (Math.min(1 * force * distance, 100) + rotateForce * distDiff);
                double right = (Math.min(1 * force * distance, 100) - rotateForce * distDiff);
                chassisDriver.setLeftAcceleration(left);
                chassisDriver.setRightAcceleration(right);
            }
        }
        stop();
        return false;
    }

    public double differenceAngle(double theta1, double theta2) {
        double dif = theta2 - theta1;
        while (dif < -Math.PI) dif += 2 * Math.PI;
        while (dif > Math.PI) dif -= 2 * Math.PI;
        return dif;
    }

    public double angle(KPoint from, KPoint to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        return Math.atan2(dy, dx);
    }

    public double distance(KPoint from, KPoint to) {
        return new Point2D.Float((float) from.getX(), (float) from.getY()).distance(new Point2D.Float((float) to.getX(), (float) to.getY()));
    }
}
