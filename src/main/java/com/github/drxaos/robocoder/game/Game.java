package com.github.drxaos.robocoder.game;

import com.github.drxaos.robocoder.game.actors.Actor;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.drxaos.robocoder.geom.KPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    protected List<Actor> actors = new ArrayList<Actor>();
    protected List<Trace> traces = new ArrayList<Trace>();

    protected World world;
    protected DebugDraw debugDraw;
    public static final Color3f GRAY = new Color3f(0.3f, 0.3f, 0.3f);
    protected Long time = 0l;
    protected boolean started = false;

    public Game(World world, DebugDraw debugDraw) {
        this.world = world;
        this.world.setContactListener(new Sensors());
        this.debugDraw = debugDraw;
    }

    public void addActor(Actor actor) {
        actors.add(actor);
        actor.setGame(this);
        if (started) {
            actor.getModel().makeBody(world, actor);
            actor.start();
        }
    }

    public void start() {
        for (int i = actors.size() - 1; i >= 0; i--) {
            Actor actor = actors.get(i);
            actor.getModel().makeBody(world, actor);
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

    public static class Trace {
        public KPoint[] points;
        public Float radius;
        public Color3f color3f;
        public int startTtl = 20;
        public int ttl = 20;
        public float width = .2f;

        public Trace(KPoint[] points, Color3f color3f) {
            this.points = points;
            this.color3f = color3f;
        }

        public Trace(KPoint[] points, Color3f color3f, int ttl) {
            this.points = points;
            this.color3f = color3f;
            this.ttl = this.startTtl = ttl;
        }

        public Trace(KPoint point, Float radius, Color3f color3f) {
            this.points = new KPoint[]{point};
            this.radius = radius;
            this.color3f = color3f;
        }

        public Trace(KPoint point, Float radius, Color3f color3f, int ttl) {
            this.points = new KPoint[]{point};
            this.radius = radius;
            this.color3f = color3f;
            this.ttl = this.startTtl = ttl;
        }
    }

    public void addTrace(KPoint[] points, Color3f color3f) {
        traces.add(new Trace(points, color3f));
    }

    public void addTrace(KPoint[] points, Color3f color3f, int ttl) {
        traces.add(new Trace(points, color3f, ttl));
    }

    public void addTrace(KPoint point, Float radius, Color3f color3f) {
        traces.add(new Trace(point, radius, color3f));
    }

    public void addTrace(KPoint point, Float radius, Color3f color3f, int ttl) {
        traces.add(new Trace(point, radius, color3f, ttl));
    }

    private static class RayCastClosestCallback implements RayCastCallback {
        Body fromBody;
        Vec2 m_point;
        Body body;
        boolean scanSensors;

        private RayCastClosestCallback(Body fromBody, boolean scanSensors) {
            this.fromBody = fromBody;
            this.scanSensors = scanSensors;
        }

        public void init() {
            body = null;
            m_point = null;
        }

        public float reportFixture(Fixture fixture, Vec2 point,
                                   Vec2 normal, float fraction) {
            body = fixture.getBody();
            m_point = point;

            Actor actor = (Actor) ((Map) body.getUserData()).get("actor");
            if (actor.isSensor() && !scanSensors) {
                return -1;
            }
            return fraction;
        }

        public Body getBody() {
            return body;
        }

        public Vec2 getPoint() {
            return m_point;
        }
    }

    public static class ScanResult {
        public Actor actor;
        public Double distance;
        public KPoint point;

        public ScanResult(Actor actor, Double distance, KPoint point) {
            this.actor = actor;
            this.distance = distance;
            this.point = point;
        }
    }

    public ScanResult resolveDirection(double angle, double scanDistance, Actor fromActor, boolean scanSensors) {
        Map<Actor, Double> result = new HashMap<Actor, Double>();
        RayCastClosestCallback callback = new RayCastClosestCallback(fromActor.getModel().body, scanSensors);
        world.raycast(callback,
                fromActor.getModel().getPositionVec2(),
                fromActor.getModel().getPositionVec2().add(
                        new Vec2((float) (Math.cos(angle) * scanDistance),
                                (float) (Math.sin(angle) * scanDistance))
                ));
        Body body = callback.getBody();
        if (body != null) {
            Vec2 point = callback.getPoint();
            Actor actor = (Actor) ((Map) body.getUserData()).get("actor");
            KPoint kpoint = new KPoint(point.x, point.y);
            return new ScanResult(actor, KPoint.distance(kpoint, fromActor.getModel().getPosition()), kpoint);
        }
        return null;
    }

    public Long getTime() {
        return time;
    }

    public static class Sensors implements ContactListener {

        public void beginContact(Contact contact) {
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();

            Actor actorA = (Actor) ((Map) fixtureA.getBody().getUserData()).get("actor");
            Actor actorB = (Actor) ((Map) fixtureB.getBody().getUserData()).get("actor");

            if (actorA.isSensor()) {
                actorA.beginContact(actorB);
            }
            if (actorB.isSensor()) {
                actorB.beginContact(actorA);
            }
        }

        public void endContact(Contact contact) {
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();

            Actor actorA = (Actor) ((Map) fixtureA.getBody().getUserData()).get("actor");
            Actor actorB = (Actor) ((Map) fixtureB.getBody().getUserData()).get("actor");

            if (actorA.isSensor()) {
                actorA.endContact(actorB);
            }
            if (actorB.isSensor()) {
                actorB.endContact(actorA);
            }
        }

        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }
}
