package com.github.drxaos.robocoder.game.actors;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.box2d.RobotModel;
import com.github.drxaos.robocoder.game.equipment.Equipment;
import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.Bus;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import straightedge.geom.KPoint;

import java.util.ArrayList;
import java.util.List;

public class Robot extends Actor {

    public static final float ARM_DISTANCE = 2;

    List<Equipment> equipment = new ArrayList<Equipment>();
    RobotModel model;
    String uid;
    boolean logging = false;

    protected AbstractProgram program;
    protected Thread userProgramThread;
    protected Bus bus = new Bus();
    protected boolean active = true;

    public Color3f color = new Color3f(0.9f, 0.7f, 0.7f);

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
                    start();
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
        if (lastRequestId == requestId && requestId != -1) {
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
        float angle = (float) (model.getAngle() + (back ? Math.PI : 0));
        traceArm(angle, false);

        Game.ScanResult scanResult = game.resolveDirection(angle, ARM_DISTANCE, this, false);
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

    private void traceArm(float angle, boolean push) {
        Color3f color = push ? new Color3f(.9f, .5f, .5f) : new Color3f(.5f, .9f, .5f);
        KPoint start = getModel().getPosition();
        KPoint end = getModel().getPosition().translateCopy(
                Math.cos(angle) * ARM_DISTANCE,
                Math.sin(angle) * ARM_DISTANCE);
        KPoint left = getModel().getPosition().translateCopy(
                Math.cos(angle) * ARM_DISTANCE * 3 / 4,
                Math.sin(angle) * ARM_DISTANCE * 3 / 4
        ).translateCopy(
                Math.cos(angle + Math.PI / 2) * ARM_DISTANCE * 1 / 4,
                Math.sin(angle + Math.PI / 2) * ARM_DISTANCE * 1 / 4);
        KPoint right = getModel().getPosition().translateCopy(
                Math.cos(angle) * ARM_DISTANCE * 3 / 4,
                Math.sin(angle) * ARM_DISTANCE * 3 / 4
        ).translateCopy(
                Math.cos(angle - Math.PI / 2) * ARM_DISTANCE * 1 / 4,
                Math.sin(angle - Math.PI / 2) * ARM_DISTANCE * 1 / 4);
        game.addTrace(new KPoint[]{start, end}, color);
        game.addTrace(new KPoint[]{left, end}, color);
        game.addTrace(new KPoint[]{right, end}, color);
    }

    public void untie() {
        model.untie();
    }

    public void push(boolean back, float strength) {
        float angle = (float) (model.getAngle() + (back ? Math.PI : 0));
        traceArm(angle, true);

        model.untie();
        Game.ScanResult scanResult = game.resolveDirection(angle, ARM_DISTANCE, this, false);
        if (scanResult == null) {
            return;
        }
        Actor actor = scanResult.actor;
        Vec2 force = new Vec2((float) (Math.cos(angle) * strength), (float) (Math.sin(angle) * strength));
        actor.getModel().applyWorldForce(scanResult.point, force);
    }

    @Override
    public boolean isTowable() {
        return !active;
    }

    @Override
    public String[] getTags() {
        return new String[]{"robot", "dynamic", "breakable", uid};
    }

    @Override
    public Color3f getColor() {
        return color;
    }

    public void setColor(Color3f color) {
        this.color = color;
    }
}
