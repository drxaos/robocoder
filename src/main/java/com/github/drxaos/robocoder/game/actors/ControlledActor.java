package com.github.drxaos.robocoder.game.actors;

import com.github.drxaos.robocoder.game.equipment.Equipment;
import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.Bus;
import com.github.drxaos.robocoder.program.LoadedProgram;

import java.util.ArrayList;
import java.util.List;

public abstract class ControlledActor extends Actor {
    protected List<Equipment> equipment = new ArrayList<Equipment>();
    protected String uid;
    protected boolean logging = false;
    protected LoadedProgram program;
    protected Bus bus;

    public ControlledActor(String uid) {
        this.uid = uid;
    }

    public Bus getBus() {
        return bus;
    }

    public String getUid() {
        return uid;
    }

    public void enableLogging() {
        logging = true;
    }

    public void log(String msg) {
        if (logging) {
            System.out.println(uid + ": " + msg);
        }
    }

    public void log(Throwable t) {
        if (logging) {
            t.printStackTrace();
        }
    }

    public void addEquipment(Equipment equipment) {
        this.equipment.add(equipment);
    }

    public <T extends Equipment> T getEquipment(Class<T> type) {
        for (Equipment eq : equipment) {
            if (type.isAssignableFrom(eq.getClass())) {
                return (T) eq;
            }
        }
        return null;
    }

    public abstract void addDefaultEquipment();

    @Override
    public void start() {
        program.setBus(bus = new Bus());
        program.start(this);
    }

    @Override
    public void stop() {
        try {
            log("Program aborted");
            program.stop();
            bus.destroy();
            bus = null;
        } catch (RuntimeException e) {
            // nothing
        }
    }


    public void setProgram(final Class<? extends AbstractProgram> program) {
        try {
            this.program = new LoadedProgram(program);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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
        bus.waitRequestFromRunningThread(program.getThread());

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
}
