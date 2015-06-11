package robo2d.game.impl;

import org.jbox2d.common.Vec2;
import robo2d.game.api.Radar;

public interface SatelliteScanner {

    public static class Request {
        public Vec2 point;
        public double accuracy;
        public int resolution;
        public long waitUntil;

        public Request(Vec2 point, double accuracy, int resolution, long waitUntil) {
            this.point = point;
            this.accuracy = accuracy;
            this.resolution = resolution;
            this.waitUntil = waitUntil;
        }
    }

    void setSatResponse(Radar.SatelliteScanData response);

    RobotImpl getRobot();

}
