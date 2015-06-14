package com.github.drxaos.robocoder.game;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    protected List<Actor> actors = new ArrayList<Actor>();

    protected World world;
    protected DebugDraw debugDraw;
    public static final Color3f GRAY = new Color3f(0.3f, 0.3f, 0.3f);
    protected Long time = 0l;
    protected boolean started = false;

    public Game(World world, DebugDraw debugDraw) {
        this.world = world;
        this.debugDraw = debugDraw;
    }

    public void addActor(Actor actor) {
        actors.add(actor);
        actor.setGame(this);
        if (started) {
            actor.getModel().makeBody(world,actor);
            actor.start();
        }
    }

    public void start() {
        for (Actor actor : actors) {
            actor.getModel().makeBody(world,actor);
        }
        for (Actor actor : actors) {
            actor.start();
        }
        started = true;
    }

    public void beforeStep() {
        time++;
        for (Actor actor : actors) {
            actor.beforeStep();
        }
    }

    public void afterStep() {
        for (Actor actor : actors) {
            actor.afterStep();
        }
    }

    public List<Actor> resolvePoint(double x, double y) {
        List<Actor> result = new ArrayList<Actor>();
        for (Actor actor : actors) {
            if (actor.getModel().hasPoint(new Vec2((float) x, (float) y))) {
                result.add(actor);
            }
        }
        return result;
    }

    public Long getTime() {
        return time;
    }
}
