package com.github.drxaos.robocoder.game.equipment;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.Robot;
import org.jbox2d.common.Vec2;
import straightedge.geom.KPoint;

import java.util.HashMap;
import java.util.Map;

public class ChassisEquipment implements Equipment {

    double leftAccel, rightAccel, maxAccel;
    private Map<KPoint, Vec2> effectsMap;
    private final Vec2 LEFT_ENGINE, RIGHT_ENGINE;

    public ChassisEquipment(double maxAccel) {
        this.maxAccel = maxAccel;

        LEFT_ENGINE = new Vec2(0, 0);
        RIGHT_ENGINE = new Vec2(0, 0);
        effectsMap = new HashMap<KPoint, Vec2>();
        effectsMap.put(new KPoint(1, 0), RIGHT_ENGINE);
        effectsMap.put(new KPoint(-1, 0), LEFT_ENGINE);
    }

    public void setLeftAcceleration(Double percent) {
        percent = Math.max(Math.min(percent, 100), -100);
        leftAccel = maxAccel * percent / 100;
    }

    public void setRightAcceleration(Double percent) {
        percent = Math.max(Math.min(percent, 100), -100);
        rightAccel = maxAccel * percent / 100;
    }

    public static final String CHASSIS = "chassis::";
    public static final String RIGHT = "right::";
    public static final String LEFT = "left::";

    public void communicate(Robot robot, Game game) {
        String req = robot.getBus().getRequest();
        if (req != null && req.startsWith(CHASSIS)) {
            if (req.startsWith(CHASSIS + LEFT)) {
                try {
                    String val = req.substring((CHASSIS + LEFT).length());
                    setLeftAcceleration(Double.parseDouble(val));
                    robot.getBus().removeRequest();
                    robot.getBus().writeResponse(CHASSIS + "::accepted");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (req.startsWith(CHASSIS + RIGHT)) {
                try {
                    String val = req.substring((CHASSIS + RIGHT).length());
                    setRightAcceleration(Double.parseDouble(val));
                    robot.getBus().removeRequest();
                    robot.getBus().writeResponse(CHASSIS + "::accepted");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void applyPhysics(Robot robot, Game game) {
        LEFT_ENGINE.set(0, (float) leftAccel);
        RIGHT_ENGINE.set(0, (float) rightAccel);
        for (Map.Entry<KPoint, Vec2> entry : effectsMap.entrySet()) {
            robot.getModel().applyForce(entry.getKey(), entry.getValue());
        }
    }
}
