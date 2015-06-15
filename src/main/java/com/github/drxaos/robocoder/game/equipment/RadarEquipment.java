package com.github.drxaos.robocoder.game.equipment;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.Robot;
import straightedge.geom.KPoint;

public class RadarEquipment implements Equipment {

    public Double getAngle(Robot robot, Game game) {
        return robot.getModel().getAngle();
    }

    public KPoint getPosition(Robot robot, Game game) {
        return robot.getModel().getPosition();
    }

    public void communicate(Robot robot, Game game) {
        String req = robot.getBus().peekRequest();
        if ("radar::angle".equals(req)) {
            robot.getBus().writeResponse("" + getAngle(robot, game));
        } else if ("radar::position".equals(req)) {
            KPoint position = getPosition(robot, game);
            robot.getBus().writeResponse("" + position.getX() + ":" + position.getY());
        } else if ("radar::time".equals(req)) {
            Long time = game.getTime();
            robot.getBus().writeResponse("" + time);
        }
    }

    public void applyPhysics(Robot robot, Game game) {

    }
}
