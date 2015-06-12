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

    private AbstractProgram userProgram;
    private Thread userProgramThread;
    private Bus bus = new Bus();

    public Robot(Game game, String uid, double x, double y, double angle) {
        this.game = game;
        box = new RobotBox(new KPoint(x, y), angle);
    }

    public void enableLogging() {
        logging = true;
    }

    private void log(String msg) {
        if (logging) {
            System.out.println(msg);
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
        userProgramThread.start();
    }

    public void setProgram(final AbstractProgram userProgram) {
        this.userProgram = userProgram;
        userProgram.setBus(bus);
        this.userProgramThread = new Thread(new Runnable() {
            @Override
            public void run() {
                userProgram.run();
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
                    setProgram(userProgram);
                    userProgramThread.start();
                } catch (InterruptedException e1) {
                    log(e1);
                }
            }
        });
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
