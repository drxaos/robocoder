package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.game.AbstractLevel;
import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.Runner;
import com.github.drxaos.robocoder.game.actors.Robot;
import com.github.drxaos.robocoder.game.actors.StartPad;
import com.github.drxaos.robocoder.game.actors.TriggerPad;
import com.github.drxaos.robocoder.game.equipment.ArmEquipment;
import com.github.drxaos.robocoder.game.equipment.ChassisEquipment;
import com.github.drxaos.robocoder.game.equipment.RadarEquipment;
import com.github.drxaos.robocoder.program.AbstractProgram;
import org.jbox2d.common.Color3f;
import com.github.drxaos.robocoder.geom.KPoint;

import javax.swing.*;

public class Tutorial01Chassis extends AbstractLevel {
    public static void run(Class<? extends AbstractProgram> program) {
        Runner.run(Tutorial01Chassis.class, program);
    }

    StartPad startPad;
    TriggerPad[] pads;
    Robot robot;

    @Override
    public void initLevel(Game game) {
        startPad = new StartPad(new KPoint(0, 0), 0d);
        game.addActor(startPad);

        pads = new TriggerPad[]{
                new TriggerPad(new KPoint(-15, 15), 2),
                new TriggerPad(new KPoint(0, 20), 2),
                new TriggerPad(new KPoint(10, 10), 2),
                new TriggerPad(new KPoint(20, 20), 2),
        };
        for (TriggerPad pad : pads) {
            game.addActor(pad);
        }

        robot = new Robot("RC-1", 0, 0, Math.PI / 2);
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
    KPoint[] trace1 = new KPoint[]{new KPoint(-15, 13), new KPoint(-15, 0)};
    KPoint[] trace1a = new KPoint[]{new KPoint(-15, 0), new KPoint(-2, 0)};
    KPoint[] trace2 = new KPoint[]{new KPoint(0, 18), new KPoint(0, 2)};
    KPoint[] trace3 = new KPoint[]{new KPoint(10, 8), new KPoint(10, 1)};
    KPoint[] trace3a = new KPoint[]{new KPoint(10, 1), new KPoint(2, 1)};
    KPoint[] trace4 = new KPoint[]{new KPoint(20, 18), new KPoint(20, -1)};
    KPoint[] trace4a = new KPoint[]{new KPoint(20, -1), new KPoint(2, -1)};

    @Override
    public synchronized void step() {
        super.step();
        traceTriggers();
        if (!success) {
            checkSuccess();
        }
    }

    private void checkSuccess() {
        if (!startPad.getContacts().contains(robot)) {
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
        if (game.getTime() % 150 == 20) {
            if (!pads[0].isTriggered()) {
                game.addTrace(trace1, traceColor, 250);
                game.addTrace(trace1a, traceColor, 250);
            }
            if (!pads[1].isTriggered()) {
                game.addTrace(trace2, traceColor, 250);
            }
            if (!pads[2].isTriggered()) {
                game.addTrace(trace3, traceColor, 250);
                game.addTrace(trace3a, traceColor, 250);
            }
            if (!pads[3].isTriggered()) {
                game.addTrace(trace4, traceColor, 250);
                game.addTrace(trace4a, traceColor, 250);
            }
        }
    }
}
