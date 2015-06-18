package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.game.AbstractLevel;
import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.Runner;
import com.github.drxaos.robocoder.game.actors.Robot;
import com.github.drxaos.robocoder.game.actors.StartPad;
import com.github.drxaos.robocoder.game.actors.TargetBuilding;
import com.github.drxaos.robocoder.game.equipment.ArmEquipment;
import com.github.drxaos.robocoder.game.equipment.ChassisEquipment;
import com.github.drxaos.robocoder.game.equipment.RadarEquipment;
import com.github.drxaos.robocoder.game.equipment.TurretEquipment;
import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.program.AbstractProgram;

import javax.swing.*;

public class Tutorial02Turret extends AbstractLevel {
    public static void run(Class<? extends AbstractProgram> program) {
        Runner.run(Tutorial02Turret.class, program);
    }

    StartPad startPad;
    TargetBuilding[] targets = new TargetBuilding[8];
    Robot robot;

    @Override
    public void initLevel(Game game) {
        startPad = new StartPad(new KPoint(0, 10), 0d);
        game.addActor(startPad);

        for (int i = 0; i < 8; i++) {
            targets[i] = new TargetBuilding(new KPoint(0 + Math.cos(i * Math.PI / 4) * 15, 10 + Math.sin(i * Math.PI / 4) * 15), 1.5f);
            game.addActor(targets[i]);
        }

        robot = new Robot("RC-1", 0, 10, Math.PI / 2);
        robot.addEquipment(new ChassisEquipment(100d));
        robot.addEquipment(new RadarEquipment());
        robot.addEquipment(new ArmEquipment());
        robot.addEquipment(new TurretEquipment());
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
        if (!startPad.getContacts().contains(robot)) {
            successTimer = 0;
            return;
        }
        for (TargetBuilding target : targets) {
            if (!target.isDestroyed()) {
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
