package com.github.drxaos.robocoder.levels.joints;

import com.github.drxaos.robocoder.game.AbstractLevel;
import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.Box;
import com.github.drxaos.robocoder.game.actors.Robot;
import com.github.drxaos.robocoder.game.equipment.ArmEquipment;
import com.github.drxaos.robocoder.game.equipment.ChassisEquipment;
import com.github.drxaos.robocoder.game.equipment.RadarEquipment;

public class JointsTest extends AbstractLevel {
    Robot robot;
    Box box;

    @Override
    public void initLevel(Game game) {
        robot = new Robot("RC-1", 0, -10, -Math.PI / 2);
        robot.addDefaultEquipment();
        robot.setProgram(userProgram);
        robot.enableLogging();
        game.addActor(robot);

        box = new Box(0, 0, 10);
        game.addActor(box);
    }

}
