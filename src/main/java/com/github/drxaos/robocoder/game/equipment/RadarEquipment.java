package com.github.drxaos.robocoder.game.equipment;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.Robot;
import com.sun.deploy.util.StringUtils;
import org.jbox2d.common.Color3f;
import straightedge.geom.KPoint;

import java.util.Arrays;

public class RadarEquipment implements Equipment {
    public static final String RADAR = "radar::";
    public static final String ANGLE = "angle";
    public static final String POSITION = "position";
    public static final String TIME = "time";
    public static final String SCAN = "scan::";
    public static final String SCAN_OBJECTS = "scan-objects::";

    protected double scanDistance = 20d;

    public void setScanDistance(double scanDistance) {
        this.scanDistance = scanDistance;
    }

    public Double getAngle(Robot robot, Game game) {
        return robot.getModel().getAngle();
    }

    public KPoint getPosition(Robot robot, Game game) {
        return robot.getModel().getPosition();
    }

    public void communicate(Robot robot, Game game) {
        String req = robot.getBus().getRequest();
        if (req == null || !req.startsWith(RADAR)) {
            return;
        }
        if (req.equals(RADAR + ANGLE)) {
            robot.getBus().writeResponse("" + getAngle(robot, game));
        } else if (req.equals(RADAR + POSITION)) {
            KPoint position = getPosition(robot, game);
            robot.getBus().writeResponse("" + position.getX() + ":" + position.getY());
        } else if (req.equals(RADAR + TIME)) {
            Long time = game.getTime();
            robot.getBus().writeResponse("" + time);
        } else if (req.startsWith(RADAR + SCAN)) {
            try {
                String val = req.substring((RADAR + SCAN).length());
                scanAngle = Double.parseDouble(val);
                scanSensors = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (req.startsWith(RADAR + SCAN_OBJECTS)) {
            try {
                String val = req.substring((RADAR + SCAN_OBJECTS).length());
                scanAngle = Double.parseDouble(val);
                scanSensors = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected Double scanAngle;
    protected boolean scanSensors;

    public void applyPhysics(Robot robot, Game game) {
        if (scanAngle != null) {
            if (scanAngle.isNaN()) {
                scanAngle = 0d;
            }
            Game.ScanResult scanResult = game.resolveDirection(scanAngle, scanDistance, robot, scanSensors);
            if (scanResult != null) {
                robot.getBus().writeResponse(scanResult.distance + "::" +
                        StringUtils.join(Arrays.asList(scanResult.actor.getRadarProperties()), "::"));
                game.addTrace(new KPoint[]{robot.getModel().getPosition(), scanResult.point}, new Color3f(.5f, .9f, .5f));
            } else {
                robot.getBus().writeResponse(scanDistance + "::" + "empty");
                game.addTrace(new KPoint[]{
                        robot.getModel().getPosition(),
                        robot.getModel().getPosition().translateCopy(Math.cos(scanAngle) * scanDistance, Math.sin(scanAngle) * scanDistance)
                }, new Color3f(.5f, .9f, .5f));
            }
            scanAngle = null;
        }
    }
}
