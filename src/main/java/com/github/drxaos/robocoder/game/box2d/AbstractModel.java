package com.github.drxaos.robocoder.game.box2d;

import com.github.drxaos.robocoder.game.Actor;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import straightedge.geom.KPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractModel {
    public BodyDef bodyDef;
    public List<FixtureDef> fixtureDefs = new ArrayList<FixtureDef>();
    public Body body;
    public World world;
    public Actor actor;

    public void setWorld(World world) {
        this.world = world;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public void makeBody(World world, Actor actor) {
        this.world = world;
        this.actor = actor;
        Body body = this.world.createBody(this.actor.getModel().bodyDef);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("actor", this.actor);
        body.setUserData(data);
        for (FixtureDef fixtureDef : this.actor.getModel().fixtureDefs) {
            body.createFixture(fixtureDef);
        }
        this.actor.getModel().body = body;
    }

    public double getAngle() {
        if (body == null) {
            return 0d;
        }
        return (double) body.getAngle();
    }

    public KPoint getPosition() {
        if (body == null) {
            return new KPoint();
        }
        Vec2 vec2 = body.getPosition();
        return new KPoint(vec2.x, vec2.y);
    }

    public Vec2 getPositionVec2() {
        if (body == null) {
            return new Vec2();
        }
        return body.getPosition();
    }

    public boolean hasPoint(Vec2 point) {
        if (body == null) {
            return false;
        }
        for (Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {
            if (f.testPoint(point)) {
                return true;
            }
        }
        return false;
    }

    public Vec2 getTiePoint() {
        return null;
    }

    public void applyForce(KPoint localPoint, Vec2 localForce) {
        if (body == null) {
            return;
        }
        Vec2 worldVector = body.getWorldVector(localForce);
        Vec2 point = new Vec2((float) localPoint.getX(), (float) localPoint.getY());
        Vec2 worldPoint = body.getWorldPoint(point);
        body.applyForce(worldVector, worldPoint);
    }

    public void applyWorldForce(KPoint worldPoint, Vec2 worldForce) {
        if (body == null) {
            return;
        }
        Vec2 point = new Vec2((float) worldPoint.getX(), (float) worldPoint.getY());
        body.applyForce(worldForce, point);
    }
}
