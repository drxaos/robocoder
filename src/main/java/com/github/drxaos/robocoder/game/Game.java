package com.github.drxaos.robocoder.game;

import com.github.drxaos.robocoder.game.actors.Actor;
import com.github.drxaos.robocoder.geom.KPoint;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.*;

public class Game {
    protected List<Actor> actors = new ArrayList<Actor>();
    protected List<Actor> destroy = new ArrayList<Actor>();
    protected List<Actor> create = new ArrayList<Actor>();
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
        if (started) {
            create.add(actor);
        } else {
            doAddActor(actor);
        }
    }

    protected void doAddActor(Actor actor) {
        actors.add(actor);
        actor.setGame(this);
        if (started) {
            actor.getModel().makeBody(world, actor);
            actor.start();
        }
    }

    public void removeActor(Actor actor) {
        destroy.add(actor);
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
        for (Actor actor : destroy) {
            actors.remove(actor);
            actor.setGame(null);
            if (started) {
                world.destroyBody(actor.getModel().body);
                actor.stop();
            }
        }
        destroy.clear();
        for (Actor actor : create) {
            doAddActor(actor);
        }
        create.clear();
    }

    public List<Actor> resolveCircle(KPoint center, double radius) {
        List<Actor> result = new ArrayList<Actor>();
        for (Actor actor : actors) {
            if (actor.getModel().getPosition().distance(center) <= radius) {
                result.add(actor);
            }
        }
        return result;
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
        public boolean permanent = false;
        public boolean polygon = false;
        public boolean background = false;
        public Actor resolvableAsActor;

        public Trace() {
        }

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

        public Trace copy() {
            Trace clone = new Trace();
            clone.startTtl = startTtl;
            clone.ttl = ttl;
            clone.points = new KPoint[points.length];
            for (int i = 0; i < points.length; i++) {
                clone.points[i] = new KPoint(points[i]);
            }
            clone.radius = radius;
            clone.color3f = color3f;
            clone.width = width;
            return clone;
        }

        public Trace width(float width) {
            this.width = width;
            return this;
        }

        public Trace permanent(boolean permanent) {
            this.permanent = permanent;
            return this;
        }

        public Trace polygon(boolean polygon) {
            this.polygon = polygon;
            return this;
        }

        public Trace background(boolean background) {
            this.background = background;
            return this;
        }

        public Trace resolvableAs(Actor actor) {
            this.resolvableAsActor = actor;
            return this;
        }

        public Trace ttl(int startTtl, int ttl) {
            this.startTtl = startTtl;
            this.ttl = ttl;
            return this;
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

    public void addTrace(Trace trace) {
        traces.add(trace);
    }

    private static class RayCastClosestCallback implements RayCastCallback {
        Actor fromActor;
        Vec2 m_point;
        Actor actor;
        boolean scanSensors;
        float fraction = 1;

        private RayCastClosestCallback(Actor fromActor, boolean scanSensors) {
            this.fromActor = fromActor;
            this.scanSensors = scanSensors;
        }

        public void init() {
            actor = null;
            m_point = null;
        }

        public float reportFixture(Fixture fixture, Vec2 point,
                                   Vec2 normal, float fraction) {
            if (fixture.getBody() == fromActor.getModel().body) {
                return -1;
            }
            Actor actor = (Actor) ((Map) fixture.getBody().getUserData()).get("actor");
            if (actor.isSensor() && !scanSensors) {
                return -1;
            }

            this.actor = actor;
            m_point = point.clone();
            this.fraction = fraction;
            return fraction;
        }

        public void reportResolvableTrace(Trace trace, Vec2 point, float fraction) {
            if (trace.resolvableAsActor.isSensor() && !scanSensors) {
                return;
            }
            this.actor = trace.resolvableAsActor;
            m_point = point.clone();
            this.fraction = fraction;
        }

        public void reportStartActor(Actor actor, Vec2 point) {
            if (actor == fromActor) {
                return;
            }
            if (actor.isSensor() && !scanSensors) {
                return;
            }
            this.actor = actor;
            m_point = point.clone();
            fraction = 0;
        }

        public Actor getActor() {
            return actor;
        }

        public Vec2 getPoint() {
            return m_point;
        }

        public float getFraction() {
            return fraction;
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

    public ScanResult resolveDirection(double angle, double scanDistance, Actor fromActor, KPoint fromPoint, boolean scanSensors) {
        Map<Actor, Double> result = new HashMap<Actor, Double>();
        Vec2 fromPointVec2;
        if (fromPoint == null) {
            fromPoint = fromActor.getModel().getPosition();
        }
        fromPointVec2 = fromPoint.toVec2();
        RayCastClosestCallback callback = new RayCastClosestCallback(fromActor, scanSensors);
        world.raycast(callback,
                fromPointVec2,
                fromActor.getModel().getPositionVec2().add(
                        new Vec2((float) (Math.cos(angle) * scanDistance),
                                (float) (Math.sin(angle) * scanDistance))
                ));
        for (Actor actor : resolvePoint(fromPointVec2.x, fromPointVec2.y, fromActor)) {
            callback.reportStartActor(actor, fromPointVec2);
        }
        for (Trace trace : traces) {
            if (trace.resolvableAsActor != null && trace.resolvableAsActor != fromActor) {
                for (int i = 0; i < trace.points.length - 1; i++) {
                    KPoint toPoint = fromPoint.createPointFromAngle(angle, scanDistance * callback.fraction);
                    KPoint intersection = KPoint.segmentaIntersect(trace.points[i], trace.points[i + 1], fromPoint, toPoint);
                    if (intersection != null) {
                        callback.reportResolvableTrace(trace, intersection.toVec2(), (float) (fromPoint.distance(intersection) / scanDistance));
                    }
                }
            }
        }
        Actor actor = callback.getActor();
        if (actor != null) {
            Vec2 point = callback.getPoint();
            KPoint kpoint = new KPoint(point.x, point.y);
            return new ScanResult(actor, KPoint.distance(kpoint, fromActor.getModel().getPosition()), kpoint);
        }
        return null;
    }

    public Set<Actor> resolvePoint(double x, double y, Actor fromActor) {
        Set<Actor> result = new HashSet<Actor>();
        for (Actor actor : actors) {
            if (actor == fromActor) {
                continue;
            }
            if (actor.getModel().hasPoint(new Vec2((float) x, (float) y))) {
                result.add(actor);
            }
        }
        return result;
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

            actorA.beginContact(actorB);
            actorB.beginContact(actorA);
        }

        public void endContact(Contact contact) {
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();

            Actor actorA = (Actor) ((Map) fixtureA.getBody().getUserData()).get("actor");
            Actor actorB = (Actor) ((Map) fixtureB.getBody().getUserData()).get("actor");

            actorA.endContact(actorB);
            actorB.endContact(actorA);
        }

        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }
}
