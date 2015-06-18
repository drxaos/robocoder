package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.game.AbstractLevel;
import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.Runner;
import com.github.drxaos.robocoder.game.actors.FinishPad;
import com.github.drxaos.robocoder.game.actors.Robot;
import com.github.drxaos.robocoder.game.actors.StartPad;
import com.github.drxaos.robocoder.game.actors.TriggerPad;
import com.github.drxaos.robocoder.game.equipment.ArmEquipment;
import com.github.drxaos.robocoder.game.equipment.ChassisEquipment;
import com.github.drxaos.robocoder.game.equipment.RadarEquipment;
import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.program.AbstractProgram;
import org.jbox2d.common.Color3f;

import javax.swing.*;

public class Tutorial01Chassis extends AbstractLevel {
    public static void run(Class<? extends AbstractProgram> program) {
        Runner.run(Tutorial01Chassis.class, program);
    }

    StartPad startPad;
    FinishPad finishPad;
    TriggerPad[] pads;
    Robot robot;

    @Override
    public void initLevel(Game game) {
        startPad = new StartPad(new KPoint(-10, 30), 0d);
        game.addActor(startPad);

        finishPad = new FinishPad(new KPoint(0, 0), 0d);
        game.addActor(finishPad);

        pads = new TriggerPad[]{
                new TriggerPad(new KPoint(-15, 15), 2),
                new TriggerPad(new KPoint(0, 20), 2),
                new TriggerPad(new KPoint(10, 10), 2),
                new TriggerPad(new KPoint(20, 20), 2),
        };
        for (TriggerPad pad : pads) {
            game.addActor(pad);
        }
        for (Game.Trace trace : traces) {
            game.addTrace(trace);
        }

        robot = new Robot("RC-1", -10, 30, Math.PI / 2);
        robot.addEquipment(new ChassisEquipment(100d));
        robot.addEquipment(new RadarEquipment());
        robot.addEquipment(new ArmEquipment());
        robot.setProgram(userProgram);
        robot.enableLogging();
        game.addActor(robot);
    }

    long successTimer = 0;
    boolean success = false;

    Color3f traceColor = new Color3f(.2f, .2f, .2f);
    Game.Trace[] traces = new Game.Trace[]{
            new Game.Trace(new KPoint[]{new KPoint(-15, 13), new KPoint(-15, 0), new KPoint(-2, 0)}, traceColor, 250).permanent(true),
            new Game.Trace(new KPoint[]{new KPoint(0, 18), new KPoint(0, 2)}, traceColor, 250).permanent(true),
            new Game.Trace(new KPoint[]{new KPoint(10, 8), new KPoint(10, 1), new KPoint(2, 1)}, traceColor, 250).permanent(true),
            new Game.Trace(new KPoint[]{new KPoint(20, 18), new KPoint(20, -1), new KPoint(2, -1)}, traceColor, 250).permanent(true),
    };

    @Override
    public synchronized void step() {
        super.step();
        traceTriggers();
        if (!success) {
            checkSuccess();
        }
    }

    private void checkSuccess() {
        if (!finishPad.getContacts().contains(robot)) {
            successTimer = 0;
            return;
        }
        for (TriggerPad pad : pads) {
            if (!pad.isTriggered()) {
                successTimer = 0;
                return;
            }
        }
        if (++successTimer > 100) {
            success = true;
            JOptionPane.showMessageDialog(null, "Level completed!", "Success!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void traceTriggers() {
        for (int i = 0; i < 4; i++) {
            if (pads[i].isTriggered()) {
                traces[i].ttl = 0;
            } else {
                traces[i].ttl = (int) (1 + Math.abs(Math.sin(1f * game.getTime() / 40)) * traces[i].startTtl * 2);
            }
        }
    }
}
