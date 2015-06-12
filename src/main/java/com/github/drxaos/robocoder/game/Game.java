package com.github.drxaos.robocoder.game;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;

public class Game {
    protected List<Actor> actors = new ArrayList<Actor>();

    protected World worldBox;
    protected DebugDraw debugDraw;
    public static final Color3f GRAY = new Color3f(0.3f, 0.3f, 0.3f);
    private Long time = 0l;

    public Game(World worldBox, DebugDraw debugDraw) {
        this.worldBox = worldBox;
        this.debugDraw = debugDraw;
    }

    public void addActor(Actor actor) {
        actors.add(actor);
    }

    public void start() {
        for (Actor actor : actors) {
            Body body = worldBox.createBody(actor.getBox().bodyDef);
            for (FixtureDef fixtureDef : actor.getBox().fixtureDefs) {
                body.createFixture(fixtureDef);
            }
            actor.getBox().body = body;
        }
        for (Actor actor : actors) {
            actor.start();
        }
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
            if (actor.getBox().hasPoint(new Vec2((float) x, (float) y))) {
                result.add(actor);
            }
        }
        return result;
    }

    public Long getTime() {
        return time;
    }
}
