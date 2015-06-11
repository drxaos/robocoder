package robo2d.game.impl;

import org.jbox2d.common.Vec2;
import robo2d.game.api.Chassis;
import straightedge.geom.KPoint;

import java.util.HashMap;
import java.util.Map;

public class ChassisImpl implements Chassis, EquipmentImpl, HasEffects {

    RobotImpl robot;

    double leftAccel, rightAccel, maxAccel;
    private Map<KPoint, Vec2> effectsMap;
    private final Vec2 LEFT_ENGINE, RIGHT_ENGINE;

    public ChassisImpl(double maxAccel) {
        this.maxAccel = maxAccel;

        LEFT_ENGINE = new Vec2(0, 0);
        RIGHT_ENGINE = new Vec2(0, 0);
        effectsMap = new HashMap<KPoint, Vec2>();
        effectsMap.put(new KPoint(1, 0), RIGHT_ENGINE);
        effectsMap.put(new KPoint(-1, 0), LEFT_ENGINE);
    }

    @Override
    public void setLeftAcceleration(Double percent) {
        percent = Math.max(Math.min(percent, 100), -100);
        leftAccel = maxAccel * percent / 100;
    }

    @Override
    public void setRightAcceleration(Double percent) {
        percent = Math.max(Math.min(percent, 100), -100);
        rightAccel = maxAccel * percent / 100;
    }

    @Override
    public Double getLeftSpeed() {
        return null;
    }

    @Override
    public Double getRightSpeed() {
        return null;
    }


    @Override
    public Map<KPoint, Vec2> getEffects() {
        if (!robot.consumeEnergy((Math.abs(leftAccel) + Math.abs(rightAccel)) / maxAccel / 100)) {
            leftAccel = rightAccel = 0;
        }
        LEFT_ENGINE.set(0, (float) leftAccel);
        RIGHT_ENGINE.set(0, (float) rightAccel);
        return effectsMap;
    }

    @Override
    public void setup(RobotImpl robot) {
        this.robot = robot;
    }
}
