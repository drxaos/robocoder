package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.game.AbstractLevel;
import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.Runner;
import com.github.drxaos.robocoder.game.actors.FinishPad;
import com.github.drxaos.robocoder.game.actors.InfoStation;
import com.github.drxaos.robocoder.game.actors.Robot;
import com.github.drxaos.robocoder.game.actors.StartPad;
import com.github.drxaos.robocoder.game.equipment.MemoryEquipment;
import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.program.AbstractProgram;

import javax.swing.*;

public class Tutorial05Bus extends AbstractLevel {
    public static void run(Class<? extends AbstractProgram> program) {
        Runner.run(Tutorial05Bus.class, program);
    }

    StartPad startPad;
    FinishPad finishPad;
    Robot robot;

    @Override
    public void initLevel(Game game) {
        startPad = new StartPad(new KPoint(-10, 10), 0d);
        game.addActor(startPad);

        finishPad = new FinishPad(new KPoint(15, 10), 0d);
        game.addActor(finishPad);

        InfoStation infoStation = new InfoStation("IS-01", new KPoint(-6, 10));
        infoStation.addDefaultEquipment();
        infoStation.getEquipment(MemoryEquipment.class).setData("Test 1-2-3...");
        infoStation.setProgram(Transmitter05.class);
        game.addActor(infoStation);

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
