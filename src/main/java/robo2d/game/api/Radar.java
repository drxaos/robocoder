package robo2d.game.api;

import straightedge.geom.KPoint;

public interface Radar extends Equipment {

    public static enum Type {
        UNKNOWN,
        EMPTY,
        WALL,
        ENEMY_BOT,
        MATE_BOT,
        Type, ME
    }

    public static class LocatorScanData {
        public Type pixel;
        public double distance;
        public double angle;

        public LocatorScanData(Type pixel, double distance, double angle) {
            this.pixel = pixel;
            this.distance = distance;
            this.angle = angle;
        }
    }

    public static class SatelliteScanData {

        public Type[][] image;
        public double accuracy;
        public int centerX, centerY;

        public SatelliteScanData(Type[][] image, double accuracy, int centerX, int centerY) {
            this.image = image;
            this.accuracy = accuracy;
            this.centerX = centerX;
            this.centerY = centerY;
        }
    }

    boolean satelliteRequest(KPoint center, double accuracy);

    SatelliteScanData getSatelliteResponse();

    void clearSatelliteResponse();

    LocatorScanData locate(double angle);

    Double getAngle();

    KPoint getPosition();

}
