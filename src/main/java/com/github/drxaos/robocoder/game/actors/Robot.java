package com.github.drxaos.robocoder.game.actors;

import com.github.drxaos.robocoder.game.Actor;
import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.box2d.RobotBox;
import com.github.drxaos.robocoder.game.equipment.Equipment;
import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.Bus;
import straightedge.geom.KPoint;

import java.util.ArrayList;
import java.util.List;

public class Robot implements Actor {

    List<Equipment> equipment = new ArrayList<Equipment>();
    Game game;
    RobotBox box;
    String uid;
    boolean logging = false;

    private AbstractProgram program;
    private Thread userProgramThread;
    private Bus bus = new Bus();

    public Robot(Game game, String uid, double x, double y, double angle) {
        this.game = game;
        this.uid = uid;
        box = new RobotBox(new KPoint(x, y), angle);
    }

    public void enableLogging() {
        logging = true;
    }

    private void log(String msg) {
        if (logging) {
            System.out.println(uid + ": " + msg);
        }
    }

    private void log(Throwable t) {
        if (logging) {
            t.printStackTrace();
        }
    }

    public void addEquipment(Equipment equipment) {
        this.equipment.add(equipment);
    }

    @Override
    public RobotBox getBox() {
        return box;
    }

    @Override
    public void start() {
        program.setBus(bus);
        this.userProgramThread = new Thread(new Runnable() {
            @Override
            public void run() {
                program.run();
                log("Program terminated");
            }
        }, "UserProgram: " + uid);
        userProgramThread.setDaemon(true);
        userProgramThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log(e);
                try {
                    log("Trying to recover...");
                    Thread.sleep(3000);
                    setProgram(program.getClass());
                    userProgramThread.start();
                } catch (InterruptedException e1) {
                    log(e1);
                }
            }
        });
        userProgramThread.start();
    }

    public void setProgram(final Class<? extends AbstractProgram> program) {
        try {
            this.program = program.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeStep() {
        for (Equipment eq : equipment) {
            eq.applyPhysics(this, game);
        }
    }

    @Override
    public void afterStep() {
        for (Equipment eq : equipment) {
            eq.communicate(this, game);
        }
        bus.removeRequest();
    }

    public Bus getBus() {
        return bus;
    }
}
