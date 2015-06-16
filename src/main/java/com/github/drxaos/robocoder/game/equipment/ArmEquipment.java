package com.github.drxaos.robocoder.game.equipment;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.Robot;

public class ArmEquipment implements Equipment {

    boolean tieForward = false;
    boolean tieBack = false;
    boolean untie = false;
    boolean pushForward = false;
    boolean pushBack = false;

    float strength = 2000;

    public void communicate(Robot robot, Game game) {
        String req = robot.getBus().getRequest();
        if ("arm::tie::forward".equals(req)) {
            tieForward = true;
            robot.getBus().removeRequest();
            robot.getBus().writeResponse("arm::accepted");
        } else if ("arm::tie::back".equals(req)) {
            tieBack = true;
            robot.getBus().removeRequest();
            robot.getBus().writeResponse("arm::accepted");
        } else if ("arm::untie".equals(req)) {
            untie = true;
            robot.getBus().removeRequest();
            robot.getBus().writeResponse("arm::accepted");
        } else if ("arm::push::forward".equals(req)) {
            pushForward = true;
            robot.getBus().removeRequest();
            robot.getBus().writeResponse("arm::accepted");
        } else if ("arm::push::back".equals(req)) {
            pushBack = true;
            robot.getBus().removeRequest();
            robot.getBus().writeResponse("arm::accepted");
        }
    }

    public void applyPhysics(Robot robot, Game game) {
        if (tieForward) {
            robot.tie(false);
        } else if (tieBack) {
            robot.tie(true);
        } else if (untie) {
            robot.untie();
        } else if (pushForward) {
            robot.push(false, strength);
        } else if (pushBack) {
            robot.push(true, strength);
        }
        tieBack = false;
        tieForward = false;
        untie = false;
        pushBack = false;
        pushForward = false;
    }
}
