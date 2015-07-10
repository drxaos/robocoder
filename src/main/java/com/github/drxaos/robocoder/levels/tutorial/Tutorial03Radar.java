package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.game.AbstractLevel;
import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.Runner;
import com.github.drxaos.robocoder.game.actors.*;
import com.github.drxaos.robocoder.game.equipment.ArmEquipment;
import com.github.drxaos.robocoder.game.equipment.ChassisEquipment;
import com.github.drxaos.robocoder.game.equipment.RadarEquipment;
import com.github.drxaos.robocoder.game.equipment.TurretEquipment;
import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.program.AbstractProgram;
import org.jbox2d.common.Vec2;

import javax.swing.*;

public class Tutorial03Radar extends AbstractLevel {
    public static void run(Class<? extends AbstractProgram> program) {
        Runner.run(Tutorial03Radar.class, program);
    }

    StartPad startPad;
    FinishPad finishPad;
    Robot robot;

    @Override
    public void initLevel(Game game) {
        setCamera(new Vec2(30, 28), 9);

        char[][] maze = Prim.maze(15, 15);

        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[y].length; x++) {
                char cell = maze[y][x];

                switch (cell) {
                    case '*':
                        Wall wall = new Wall(new KPoint(x * 4, y * 4), 2, 2, 0);
                        game.addActor(wall);
                        break;
                    case '.':
                        if (Math.random() > 0.91) {
                            FenceBuilding fence = new FenceBuilding(new KPoint(x * 4, y * 4), 2, 2, 0);
                            game.addActor(fence);
                        }
                        break;
                    case 'E':
                        finishPad = new FinishPad(new KPoint(x * 4, y * 4), 0d);
                        game.addActor(finishPad);
                        break;
                    case 'S':
                        startPad = new StartPad(new KPoint(x * 4, y * 4), 0d);
                        game.addActor(startPad);
                        robot = new Robot("RC-1", x * 4, y * 4, 0);
                        robot.addDefaultEquipment();
                        robot.setProgram(userProgram);
                        robot.enableLogging();
                        break;
                }
            }
        }
        game.addActor(robot);
    }

    long successTimer = 0;
    boolean success = false;

    @Override
    public synchronized void step() {
        super.step();
        if (!success) {
            checkSuccess();
        }
    }

    private void checkSuccess() {
        if (!finishPad.getContacts().contains(robot)) {
            successTimer = 0;
            return;
        }
        if (robot.isActive()) {
            successTimer = 0;
            return;
        }
        if (++successTimer > 100) {
            success = true;
            JOptionPane.showMessageDialog(null, "Level completed!", "Success!", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
