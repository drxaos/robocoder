package com.github.drxaos.robocoder.game.equipment;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.ControlledActor;
import com.github.drxaos.robocoder.game.actors.HasArm;

public class ArmEquipment implements Equipment {

    boolean tieForward = false;
    boolean tieBack = false;
    boolean untie = false;
    boolean pushForward = false;
    boolean pushBack = false;

    float strength = 2000;

    public void communicate(ControlledActor actor, Game game) {
        String req = actor.getBus().getRequest();
        if ("arm::tie::forward".equals(req)) {
            tieForward = true;
            actor.getBus().writeResponse("arm::accepted");
        } else if ("arm::tie::back".equals(req)) {
            tieBack = true;
            actor.getBus().writeResponse("arm::accepted");
        } else if ("arm::untie".equals(req)) {
            untie = true;
            actor.getBus().writeResponse("arm::accepted");
        } else if ("arm::push::forward".equals(req)) {
            pushForward = true;
            actor.getBus().writeResponse("arm::accepted");
        } else if ("arm::push::back".equals(req)) {
            pushBack = true;
            actor.getBus().writeResponse("arm::accepted");
        }
    }

    public void applyPhysics(ControlledActor actor, Game game) {
        if (actor instanceof HasArm) {
            HasArm actorWithArm = (HasArm) actor;
            if (tieForward) {
                actorWithArm.tie(false);
            } else if (tieBack) {
                actorWithArm.tie(true);
            } else if (untie) {
                actorWithArm.untie();
            } else if (pushForward) {
                actorWithArm.push(false, strength);
            } else if (pushBack) {
                actorWithArm.push(true, strength);
            }
        }
        tieBack = false;
        tieForward = false;
        untie = false;
        pushBack = false;
        pushForward = false;
    }
}
