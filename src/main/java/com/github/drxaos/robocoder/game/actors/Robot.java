package com.github.drxaos.robocoder.game.actors;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.box2d.RobotModel;
import com.github.drxaos.robocoder.game.equipment.ArmEquipment;
import com.github.drxaos.robocoder.game.equipment.ChassisEquipment;
import com.github.drxaos.robocoder.game.equipment.RadarEquipment;
import com.github.drxaos.robocoder.game.equipment.TurretEquipment;
import com.github.drxaos.robocoder.geom.KPoint;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;

public class Robot extends ControlledActor implements HasArm, HasTurret {

    public static final float ARM_DISTANCE = 2;
    public static final float FIRE_DISTANCE = 30;

    protected RobotModel model;


    protected boolean active = true;

    protected final Color3f destroyedColor = new Color3f(0.3f, 0.3f, 0.3f);
    protected Color3f color = new Color3f(0.9f, 0.7f, 0.7f);

    public Robot(String uid, double x, double y, double angle) {
        super(uid);
        model = new RobotModel(new KPoint(x, y), angle);
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public RobotModel getModel() {
        return model;
    }

    public void addDefaultEquipment() {
        addEquipment(new ChassisEquipment());
        addEquipment(new RadarEquipment());
        addEquipment(new ArmEquipment());
        addEquipment(new TurretEquipment());
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
        if (model.body.isActive()) {
            return color;
        } else {
            return destroyedColor;
        }
    }

    public void setColor(Color3f color) {
        this.color = color;
    }

    @Override
    public void damage(float points) {
        super.damage(points);
        if (getArmour() == 0) {
            model.body.setActive(false);
        }
    }

    long fireDelay = 50;
    long fireWait = 0;

    public boolean fire() {
        if (fireWait > 0) {
            return false;
        }
        double angle = model.getAngle();
        float dist = (float) (RobotModel.SIZE + RobotModel.TURRET_LEDGE + 1f);
        game.addActor(new Bullet(
                model.getPosition().translateCopy(Math.cos(angle) * dist, Math.sin(angle) * dist),
                angle, Bullet.SPEED_NORMAL, Bullet.SIZE_LIGHT, FIRE_DISTANCE));
        fireWait = fireDelay;
        return true;
    }

    @Override
    public void afterStep() {
        super.afterStep();
        if (fireWait > 0) {
            fireWait--;
        }
    }
}
