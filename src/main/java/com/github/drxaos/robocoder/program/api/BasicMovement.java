package com.github.drxaos.robocoder.program.api;

import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.program.Bus;

public class BasicMovement {

    Bus bus;
    ChassisDriver chassisDriver;
    RadarDriver radarDriver;

    public BasicMovement(Bus bus) {
        this.bus = bus;
        chassisDriver = new ChassisDriver(bus);
        radarDriver = new RadarDriver(bus);
    }

    public void sleep(int steps) {
        long end = radarDriver.getTime() + steps - 2;
        while (radarDriver.getTime() < end) ;
    }

    public void stop() {
        if (chassisDriver != null) {
            chassisDriver.setLeftAcceleration(0d);
            chassisDriver.setRightAcceleration(0d);
        }
    }

    public boolean rotate(double toAngle) {
        if (chassisDriver == null || radarDriver == null) {
            return false;
        }
        long end = radarDriver.getTime() + 10000;
        stop();
        while (radarDriver.getTime() < end) {
            double azimuth = differenceAngle(radarDriver.getAngle(), toAngle);
            if (Math.abs(azimuth) < 0.001) {
                stop();
                return true;
            }
            int force = 100;
            chassisDriver.setLeftAcceleration(-1 * force * azimuth);
            chassisDriver.setRightAcceleration(1 * force * azimuth);
        }
        stop();
        return false;
    }

    public boolean forward(double distance) {
        return forward(distance, 100);
    }

    public boolean forward(double distance, float speed) {
        if (chassisDriver == null || radarDriver == null) {
            return false;
        }
        long end = radarDriver.getTime() + 10000;
        stop();
        KPoint start = radarDriver.getPosition();
        while (radarDriver.getTime() < end) {
            KPoint current = radarDriver.getPosition();
            double remains = distance - start.distance(current);
            if (Math.abs(remains) < 0.01) {
                stop();
                return true;
            }
            chassisDriver.setLeftAcceleration(1 * speed * remains);
            chassisDriver.setRightAcceleration(1 * speed * remains);
        }
        stop();
        return false;
    }

    public boolean move(double x, double y) {
        return move(new KPoint(x, y), 0.5d, 10000);
    }

    public boolean move(KPoint to) {
        return move(to, 0.5d, 10000);
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
            double distance = current.distance(to);
            if (distance < 1 && Math.abs(azimuth) > Math.PI / 2) {
                double left = -rotateForce * azimuth;
                double right = rotateForce * azimuth;
                if (left > 0) {
                    left = 0;
                }
                if (right > 0) {
                    right = 0;
                }
                chassisDriver.setLeftAcceleration(left);
                chassisDriver.setRightAcceleration(right);
            } else if (distance > 1 && Math.abs(azimuth) >= Math.PI / 2) {
                chassisDriver.setLeftAcceleration(-rotateForce * azimuth);
                chassisDriver.setRightAcceleration(rotateForce * azimuth);
            } else if (distance < accuracy) {
                stop();
                return true;
            } else {
                KPoint leftPoint = current.translateCopy(Math.cos(myAngle + Math.PI / 2) * width, Math.sin(myAngle + Math.PI / 2) * width);
                KPoint rightPoint = current.translateCopy(Math.cos(myAngle - Math.PI / 2) * width, Math.sin(myAngle - Math.PI / 2) * width);
                double distanceLeft = leftPoint.distance(to);
                double distanceRight = rightPoint.distance(to);
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

    public double differenceAngle(double fromAngle, double toAngle) {
        double dif = toAngle - fromAngle;
        while (dif < -Math.PI) dif += 2 * Math.PI;
        while (dif > Math.PI) dif -= 2 * Math.PI;
        return dif;
    }

    public double angle(KPoint from, KPoint to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        return differenceAngle(0, Math.atan2(dy, dx));
    }
}
