package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.game.AbstractLevel;
import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.Runner;
import com.github.drxaos.robocoder.game.actors.*;
import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.program.AbstractProgram;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.util.Arrays;

public class Tutorial06Coop extends AbstractLevel {
    public static void run(Class<? extends AbstractProgram> program) {
        Runner.run(Tutorial06Coop.class, program);
    }

    StartPad startPad1;
    StartPad startPad2;
    FinishPad finishPad;
    Robot robot1;
    Robot robot2;
    PressPad[] pressPads = new PressPad[3];

    @Override
    public void initLevel(Game game) {
        setCamera(new Vec2(30, 10), 10);

        startPad1 = new StartPad(new KPoint(0, 0), 0d);
        game.addActor(startPad1);

        startPad2 = new StartPad(new KPoint(0, 20), 0d);
        game.addActor(startPad2);

        finishPad = new FinishPad(new KPoint(50, 20), 4, 2, 0d);
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
        {
            Wall wall = new Wall(Arrays.asList(
                    new KPoint(15 - 1, 10 + 3 + 0.5),
                    new KPoint(15 - 1 + 2, 10 + 3 + 0.5),
                    new KPoint(15 - 1 + 2, 10 + 3 + 9.5),
                    new KPoint(15 - 1, 10 + 3 + 9.5)
            ), new KPoint(), 0);
            game.addActor(wall);
        }
        {
            Wall wall = new Wall(Arrays.asList(
                    new KPoint(15 - 1, 10 - 3 - 0.5),
                    new KPoint(15 - 1 + 2, 10 - 3 - 0.5),
                    new KPoint(15 - 1 + 2, 10 - 3 - 9.5),
                    new KPoint(15 - 1, 10 - 3 - 9.5)
            ), new KPoint(), 0);
            game.addActor(wall);
        }
        {
            Wall wall = new Wall(Arrays.asList(
                    new KPoint(40 - 1, 10 + 3 + 0.5),
                    new KPoint(40 - 1 + 2, 10 + 3 + 0.5),
                    new KPoint(40 - 1 + 2, 10 + 3 + 9.5),
                    new KPoint(40 - 1, 10 + 3 + 9.5)
            ), new KPoint(), 0);
            game.addActor(wall);
        }
        {
            Wall wall = new Wall(Arrays.asList(
                    new KPoint(40 - 1, 10 - 3 - 0.5),
                    new KPoint(40 - 1 + 2, 10 - 3 - 0.5),
                    new KPoint(40 - 1 + 2, 10 - 3 - 9.5),
                    new KPoint(40 - 1, 10 - 3 - 9.5)
            ), new KPoint(), 0);
            game.addActor(wall);
        }

        pressPads[0] = new PressPad(new KPoint(0, 10), 1.5f, 1.5f, 0);
        game.addActor(pressPads[0]);

        pressPads[1] = new PressPad(new KPoint(27, 20), 1.5f, 1.5f, 0);
        game.addActor(pressPads[1]);

        pressPads[2] = new PressPad(new KPoint(50, 0), 1.5f, 1.5f, 0);
        game.addActor(pressPads[2]);

        {
            Door door = new Door(new KPoint(15, 10), 1, 3, 0);
            door.addTrigger(pressPads[0]);
            door.addTrigger(pressPads[1]);
            game.addActor(door);
        }
        {
            Door door = new Door(new KPoint(40, 10), 1, 3, 0);
            door.addTrigger(pressPads[1]);
            door.addTrigger(pressPads[2]);
            game.addActor(door);
        }

        robot1 = new Robot("RC-1", 0, 0, Math.PI / 2);
        robot1.addDefaultEquipment();
        robot1.setProgram(userProgram);
        robot1.enableLogging();
        game.addActor(robot1);

        robot2 = new Robot("RC-2", 0, 20, -Math.PI / 2);
        robot2.addDefaultEquipment();
        robot2.setProgram(userProgram);
        robot2.enableLogging();
        game.addActor(robot2);
    }

    long successTimer = 0;
    boolean success = false;

    @Override
    public synchronized void step() {
        super.step();
        trace();
        if (!success) {
            checkSuccess();
        }
    }

    private void checkSuccess() {
        if (!finishPad.getContacts().contains(robot1)) {
            successTimer = 0;
            return;
        }
        if (!finishPad.getContacts().contains(robot2)) {
            successTimer = 0;
            return;
        }
        if (robot1.isActive()) {
            successTimer = 0;
            return;
        }
        if (robot2.isActive()) {
            successTimer = 0;
            return;
        }
        if (++successTimer > 100) {
            success = true;
            JOptionPane.showMessageDialog(null, "Level completed!", "Success!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    protected Game.Trace[] traces;
    protected Color3f idleTraceColor = new Color3f(.4f, .4f, .4f);
    protected Color3f activeTraceColor = new Color3f(.4f, .8f, .4f);

    private void trace() {
        if (traces == null) {
            traces = new Game.Trace[3];
            traces[0] = new Game.Trace(new KPoint[]{
                    new KPoint(1.5f, 10),
                    new KPoint(14f, 10),
            }, idleTraceColor).permanent(true).width(.5f).background(true);
            game.addTrace(traces[0]);
            traces[1] = new Game.Trace(new KPoint[]{
                    new KPoint(16f, 10),
                    new KPoint(27f, 10),
                    new KPoint(27f, 20 - 1.5f),
                    new KPoint(27f, 10),
                    new KPoint(39f, 10),
            }, idleTraceColor).permanent(true).width(.5f).background(true);
            game.addTrace(traces[1]);
            traces[2] = new Game.Trace(new KPoint[]{
                    new KPoint(41f, 10),
                    new KPoint(50f, 10),
                    new KPoint(50f, 1.5f),
            }, idleTraceColor).permanent(true).width(.5f).background(true);
            game.addTrace(traces[2]);
        }
        for (int i = 0; i < pressPads.length; i++) {
            if (pressPads[i].isTriggered()) {
                traces[i].color3f = activeTraceColor;
            } else {
                traces[i].color3f = idleTraceColor;
            }
        }
    }
}
