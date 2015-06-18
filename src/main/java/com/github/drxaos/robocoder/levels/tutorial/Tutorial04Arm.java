package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.game.AbstractLevel;
import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.Runner;
import com.github.drxaos.robocoder.game.actors.Box;
import com.github.drxaos.robocoder.game.actors.FinishPad;
import com.github.drxaos.robocoder.game.actors.Robot;
import com.github.drxaos.robocoder.game.actors.StartPad;
import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.program.AbstractProgram;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Tutorial04Arm extends AbstractLevel {
    public static void run(Class<? extends AbstractProgram> program) {
        Runner.run(Tutorial04Arm.class, program);
    }

    StartPad startPad;
    FinishPad finishPad;
    Robot robot;
    List<Box> boxes = new ArrayList<Box>();

    @Override
    public void initLevel(Game game) {
        drawInterval = 8;

        startPad = new StartPad(new KPoint(-10, 10), 0d);
        game.addActor(startPad);

        finishPad = new FinishPad(new KPoint(15, 10), 10, 10, 0d);
        game.addActor(finishPad);

        for (int s = -1; s <= 1; s += 2) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    Box box = new Box(-10 + x * 3, 10 + s * 10 + y * 3, 0d);
                    game.addActor(box);
                    boxes.add(box);
                }
            }
        }

        robot = new Robot("RC-1", -10, 10, 0);
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
        for (Box box : boxes) {
            if (!finishPad.getContacts().contains(box)) {
                successTimer = 0;
                return;
            }
        }
        if (++successTimer > 100) {
            success = true;
            JOptionPane.showMessageDialog(null, "Level completed!", "Success!", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
