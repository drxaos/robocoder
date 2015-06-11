package robo2d.game.impl;

import robo2d.game.api.Computer;

import java.util.HashMap;
import java.util.Map;

public class ComputerImpl implements Computer, EquipmentImpl {

    RobotImpl robot;
    Thread program;
    Map<String, Object> memory = new HashMap<String, Object>();

    public ComputerImpl(Class<? extends RobotProgram> code) {
        memory.put("boot", code);
    }

    public void setup(RobotImpl robot) {
        this.robot = robot;
    }

    public void startProgram() {
        try {
            if (program == null) {
                Class code = (Class) memory.get("boot");
                if (code == null || !RobotProgram.class.isAssignableFrom(code)) {
                    return;
                }
                RobotProgram robotProgram = (RobotProgram) code.getConstructor().newInstance();
                robotProgram.init(robot);
                program = new Thread(robotProgram);
                program.setDaemon(true);
                program.setPriority(Thread.MIN_PRIORITY);
                program.start();
            } else {
                double consumption = 0.01;
                if (program.getState() == Thread.State.RUNNABLE) {
                    consumption = 0.05;
                }
                if (!robot.consumeEnergy(consumption)) {
                    stopProgram();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopProgram() {
        try {
            if (program != null) {
                program.interrupt();
                program.stop();
                program = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return program != null;
    }
}
