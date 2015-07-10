package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.game.AbstractLevel;
import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.Runner;
import com.github.drxaos.robocoder.game.actors.Box;
import com.github.drxaos.robocoder.game.actors.*;
import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.program.AbstractProgram;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.util.Arrays;

public class Tutorial07Lasers extends AbstractLevel {
    public static void run(Class<? extends AbstractProgram> program) {
        Runner.run(Tutorial07Lasers.class, program);
    }

    StartPad startPad;
    FinishPad finishPad;
    Robot robot;
    Box box;

    @Override
    public void initLevel(Game game) {
        setCamera(new Vec2(30, 10), 10);

        startPad = new StartPad(new KPoint(0, 0), 0d);
        game.addActor(startPad);

        finishPad = new FinishPad(new KPoint(57, 20), 0d);
        game.addActor(finishPad);

        {
            Wall wall = new Wall(Arrays.asList(
                    new KPoint(-4, 23),
                    new KPoint(-4, 23 + 2),
                    new KPoint(62, 23 + 2),
                    new KPoint(62, -5),
                    new KPoint(-4, -5),
                    new KPoint(-4, -3),
                    new KPoint(60, -3),
                    new KPoint(60, 23)
            ), new KPoint(), 0);
            game.addActor(wall);
        }

        for (int i = 0; i < 4; i++) {
            if (Math.random() > 0.5) {
                Laser laser = new Laser(new KPoint(10 + i * 12, -3), Math.PI / 2);
                game.addActor(laser);
            } else {
                Laser laser = new Laser(new KPoint(10 + i * 12, 23), -Math.PI / 2);
                game.addActor(laser);
            }
        }

        box = new Box(5, 20, 10);
        game.addActor(box);

        robot = new Robot("RC-1", 0, 0, Math.PI / 2);
        robot.addDefaultEquipment();
        robot.setProgram(userProgram);
        robot.enableLogging();
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
