package robo2d.game.impl;

import org.jbox2d.common.Vec2;
import robo2d.game.Game;
import robo2d.game.api.Radar;
import straightedge.geom.KPoint;

public class RadarImpl implements Radar, EquipmentImpl, SatelliteScanner {

    Game game;
    RobotImpl robot;
    Double scanDistance = null;

    SatelliteScanData satelliteScanData;

    public RadarImpl(Game game, Double scanDistance) {
        this.game = game;
        this.scanDistance = scanDistance;
    }

    @Override
    public void setup(RobotImpl robot) {
        this.robot = robot;
    }

    @Override
    public boolean satelliteRequest(KPoint center, double accuracy) {
        if (game.getSatelliteResolution() == null) {
            return false;
        }
        if (!robot.consumeEnergy(0.5)) {
            return false;
        }
        game.satelliteRequest(this, new Vec2((float) center.getX(), (float) center.getY()), accuracy, game.getSatelliteResolution());
        satelliteScanData = null;
        return true;
    }

    @Override
    public SatelliteScanData getSatelliteResponse() {
        return satelliteScanData;
    }

    @Override
    public void clearSatelliteResponse() {
        satelliteScanData = null;
    }

    @Override
    public LocatorScanData locate(double angle) {
        if (scanDistance == null) {
            return null;
        }
        if (!robot.consumeEnergy(0.001)) {
            return null;
        }
        synchronized (game.stepSync()) {
            return game.resolveDirection(angle, scanDistance, robot);
        }
    }

    @Override
    public Double getAngle() {
        if (!game.hasGps()) {
            return null;
        }
        return robot.box.getAngle();
    }

    @Override
    public KPoint getPosition() {
        if (!game.hasGps()) {
            return null;
        }
        return robot.box.getPosition();
    }

    @Override
    public RobotImpl getRobot() {
        return robot;
    }

    @Override
    public void setSatResponse(SatelliteScanData response) {
        satelliteScanData = response;
    }
}
