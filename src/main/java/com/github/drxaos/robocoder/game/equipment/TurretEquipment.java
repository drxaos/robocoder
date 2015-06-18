package com.github.drxaos.robocoder.game.equipment;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.ControlledActor;
import com.github.drxaos.robocoder.game.actors.HasTurret;

public class TurretEquipment implements Equipment {
    boolean fire = false;

    public void communicate(ControlledActor robot, Game game) {
        String req = robot.getBus().getRequest();
        if ("turret::fire".equals(req)) {
            fire = true;
            robot.getBus().writeResponse("turret::accepted");
        }
    }

    public void applyPhysics(ControlledActor robot, Game game) {
        if (robot instanceof HasTurret) {
            if (fire) {
                ((HasTurret) robot).fire();
            }
        }
        fire = false;
    }
}
