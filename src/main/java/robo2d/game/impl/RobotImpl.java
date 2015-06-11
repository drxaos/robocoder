package robo2d.game.impl;

import robo2d.game.Game;
import robo2d.game.api.Equipment;
import robo2d.game.api.Player;
import robo2d.game.api.Robot;
import robo2d.game.api.map.Obj;
import robo2d.game.box2d.Box;
import robo2d.game.box2d.Physical;
import robo2d.game.box2d.RobotBox;
import straightedge.geom.KPoint;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RobotImpl implements Robot, Obj, Physical {

    Set<EquipmentImpl> equipment = new HashSet<EquipmentImpl>();
    Set<HasEffects> hasEffects = new HashSet<HasEffects>();
    ComputerImpl computer;

    Game game;
    PlayerImpl owner;
    RobotBox box;
    double energy = 0d;

    final Object sync = new Object();
    String debugMsg;
    KPoint debugPoint;

    public RobotImpl(Game game, PlayerImpl owner, KPoint position, double angle) {
        this.owner = owner;
        this.game = game;
        box = new RobotBox(position, angle);
    }

    public ComputerImpl getComputer() {
        return computer;
    }

    public void addEquipment(EquipmentImpl equipment) {
        this.equipment.add(equipment);
        if (equipment instanceof HasEffects) {
            hasEffects.add((HasEffects) equipment);
        }
        if (equipment instanceof ComputerImpl) {
            computer = (ComputerImpl) equipment;
        }
        equipment.setup(this);
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public Type getType() {
        return Type.BOT;
    }

    @Override
    public List<Point2D> getVertices() {
        return null;
    }


    @Override
    public <T extends Equipment> T getEquipment(Class<T> type) {
        for (EquipmentImpl eq : equipment) {
            if (type.isAssignableFrom(eq.getClass())) {
                return (T) eq;
            }
        }
        return null;
    }

    @Override
    public Double getEnergy() {
        return energy;
    }

    public boolean consumeEnergy(double amount) {
        if (energy > amount) {
            energy -= amount;
            if (energy < 0.01) {
                energy = 0;
            }
            return true;
        } else {
            energy = 0;
            return false;
        }
    }

    public void charge(double energy) {
        this.energy += energy;
    }

    @Override
    public Long getTime() {
        return game.getTime();
    }

    @Override
    public void waitForStep() {
        try {
            synchronized (sync) {
                sync.wait();
            }
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void debug(Object dbg) {
        if (dbg == null) {
            debugPoint = null;
            debugMsg = null;
        } else if (dbg instanceof KPoint) {
            debugPoint = (KPoint) dbg;
        } else {
            debugMsg = dbg.toString();
        }
    }

    public String getDebug() {
        return debugMsg;
    }

    public KPoint getDebugPoint() {
        return debugPoint;
    }

    @Override
    public Box getBox() {
        return box;
    }

    public void applyEffects() {
        for (HasEffects eq : hasEffects) {
            box.applyForces(eq.getEffects());
        }
    }


    public void sync() {
        synchronized (sync) {
            sync.notifyAll();
        }
    }
}
