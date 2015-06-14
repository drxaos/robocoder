package com.github.drxaos.robocoder.levels.joints;

import com.github.drxaos.robocoder.game.AbstractLevel;
import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.Box;
import com.github.drxaos.robocoder.game.actors.Robot;
import com.github.drxaos.robocoder.game.equipment.ChassisEquipment;
import com.github.drxaos.robocoder.game.equipment.RadarEquipment;

public class JointsTest extends AbstractLevel {
    Robot robot;
    Box box;

    @Override
    public Game createGame() {
        Game game = new Game(getWorld(), getDebugDraw());

        robot = new Robot("RC-1", 0, -10, Math.PI * 4 * Math.random());
        robot.addEquipment(new ChassisEquipment(100d));
        robot.addEquipment(new RadarEquipment());
        robot.setProgram(userProgram);
        game.addActor(robot);

        box = new Box(0, -15, 0);
        game.addActor(box);

        return game;
    }

    @Override
    public synchronized void step() {
        if (game.getTime() == 0) {
            robot.getModel().tieBox(box.getModel());
            robot.getModel().setRopeLength(.1f);
        }
        super.step();
    }
}
