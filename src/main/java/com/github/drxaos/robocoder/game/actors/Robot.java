package com.github.drxaos.robocoder.game.actors;

import com.github.drxaos.robocoder.game.Actor;
import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.box2d.RobotModel;
import com.github.drxaos.robocoder.game.equipment.Equipment;
import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.Bus;
import org.jbox2d.common.Vec2;
import straightedge.geom.KPoint;

import java.util.ArrayList;
import java.util.List;

public class Robot extends Actor {

    List<Equipment> equipment = new ArrayList<Equipment>();
    RobotModel model;
    String uid;
    boolean logging = false;

    protected AbstractProgram program;
    protected Thread userProgramThread;
    protected Bus bus = new Bus();
    protected boolean active = true;

    public Robot(String uid, double x, double y, double angle) {
        this.uid = uid;
        model = new RobotModel(new KPoint(x, y), angle);
    }

    public boolean isActive() {
        return active;
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
    public RobotModel getModel() {
        return model;
    }

    @Override
    public void start() {
        program.setBus(bus);
        this.userProgramThread = new Thread(new Runnable() {
            public void run() {
                program.run();
                log("Program terminated");
            }
        }, "UserProgram: " + uid);
        userProgramThread.setDaemon(true);
        userProgramThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
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

    protected long lastRequestId = 0;
    protected long freezeDetector = 0;
    protected final long FREEZE_TIMEOUT = 500;

    @Override
    public void afterStep() {
        for (Equipment eq : equipment) {
            eq.communicate(this, game);
        }

        // freeze detector
        long requestId = bus.getRequestId();
        if (lastRequestId == requestId) {
            freezeDetector++;
        } else {
            lastRequestId = requestId;
            freezeDetector = 0;
        }
        if (freezeDetector > FREEZE_TIMEOUT) {
            bus.writeResponse("unknown");
        }
    }

    public Bus getBus() {
        return bus;
    }

    public boolean tie(boolean back) {
        Game.ScanResult scanResult = game.resolveDirection(model.getAngle() + (back ? Math.PI : 0), 2, this);
        if (scanResult == null) {
            return false;
        }
        Actor actor = scanResult.actor;
        if (!actor.isTowable()) {
            return false;
        }
        model.tie(actor.getModel(), back);
        return true;
    }

    public void untie() {
        model.untie();
    }

    public void push(boolean back, float strength) {
        model.untie();
        Game.ScanResult scanResult = game.resolveDirection(model.getAngle() + (back ? Math.PI : 0), 2, this);
        if (scanResult == null) {
            return;
        }
        Actor actor = scanResult.actor;
        double angle = model.getAngle();
        Vec2 force = new Vec2((float) (Math.cos(angle) * strength), (float) (Math.sin(angle) * strength));
        actor.getModel().applyWorldForce(scanResult.point, force);
    }

    @Override
    public boolean isTowable() {
        return !active;
    }
}
