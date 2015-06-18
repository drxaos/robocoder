package com.github.drxaos.robocoder.ui;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.Actor;
import com.github.drxaos.robocoder.ui.j2d.DebugDrawJ2D;
import com.github.drxaos.robocoder.ui.j2d.TestPanelJ2D;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.PulleyJoint;
import org.jbox2d.pooling.IWorldPool;
import org.jbox2d.pooling.arrays.Vec2Array;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestbedDrawer {

    private DebugDraw m_debugDraw;
    private World world;

    private final Transform xf = new Transform();
    private final Color3f color = new Color3f();
    private final Vec2 center = new Vec2();
    private final Vec2 axis = new Vec2();
    private final Vec2 v1 = new Vec2();
    private final Vec2 v2 = new Vec2();
    private final Vec2Array tlvertices = new Vec2Array();
    private final IWorldPool pool;

    public TestbedDrawer(DebugDraw m_debugDraw, World world) {
        this.m_debugDraw = m_debugDraw;
        this.world = world;
        pool = world.getPool();
    }

    public void drawWorld() {
        if (m_debugDraw == null) {
            return;
        }
        Body m_bodyList = world.getBodyList();
        Joint m_jointList = world.getJointList();

        Vec2 leftTop = m_debugDraw.getScreenToWorld(0, 0);
        leftTop.set((int) leftTop.x, (int) leftTop.y);
        TestPanelJ2D panel = ((DebugDrawJ2D) m_debugDraw).gtPanel();
        Vec2 rightBottom = m_debugDraw.getScreenToWorld(panel.getScreenWidth(), panel.getScreenHeight());
        float factor = 5;
        float scale = m_debugDraw.getScreenToWorld(new Vec2(1, 1)).sub(m_debugDraw.getScreenToWorld(new Vec2(0, 0))).x;
        while (scale > 0.101) {
            factor *= 2;
            scale /= 2;
        }
        while (scale < 0.049) {
            factor /= 2;
            scale *= 2;
        }
        float x0 = factor * (int) (leftTop.x / factor);
        float y0 = factor * (int) (leftTop.y / factor);
        color.set(0.3f, 0.3f, 0.3f);
        for (float x = x0; x < rightBottom.x; x += factor) {
            for (float y = y0; y > rightBottom.y; y -= factor) {
                center.set(x, y);
                m_debugDraw.drawPoint(center, 1, color);
            }
        }

        for (Body b = m_bodyList; b != null; b = b.getNext()) {
            xf.set(b.getTransform());
            for (int i = 3; i >= 0; i--) {
                for (Fixture f = b.getFixtureList(); f != null; f = f.getNext()) {
                    if (i == 0) {
                        Color3f userColor = getUserColor(b);
                        if (!b.isActive()) {
                            if (userColor != null) {
                                color.set(userColor);
                            } else {
                                color.set(0.5f, 0.5f, 0.3f);
                            }
                        } else if (b.getType() == BodyType.STATIC) {
                            if (userColor != null) {
                                color.set(userColor);
                            } else {
                                color.set(0.5f, 0.9f, 0.3f);
                            }
                        } else if (b.getType() == BodyType.KINEMATIC) {
                            if (userColor != null) {
                                color.set(userColor);
                            } else {
                                color.set(0.5f, 0.5f, 0.9f);
                            }
                        } else if (!b.isAwake()) {
                            if (userColor != null) {
                                color.set(userColor);
                            } else {
                                color.set(0.5f, 0.5f, 0.5f);
                            }
                        } else {
                            if (userColor != null) {
                                color.set(userColor);
                            } else {
                                color.set(0.9f, 0.7f, 0.7f);
                            }
                        }
                    } else {
                        color.set(0, 0, 0);
                    }
                    drawShape(f, xf, color);
                }
            }
        }
        for (Joint j = m_jointList; j != null; j = j.getNext()) {
            drawJoint(j);
        }
    }

    public void drawTraces(List<Game.Trace> traces) {
        for (Iterator<Game.Trace> iterator = traces.iterator(); iterator.hasNext(); ) {
            Game.Trace trace = iterator.next();

            if (trace.points.length == 2) {
                ((DebugDrawJ2D) m_debugDraw).drawSegment(
                        new Vec2((float) trace.points[0].x, (float) trace.points[0].y),
                        new Vec2((float) trace.points[1].x, (float) trace.points[1].y),
                        trace.color3f, trace.width, (float) (1d * trace.ttl / trace.startTtl / 2)
                );
            } else if (trace.points.length == 1 && trace.radius != null) {
                ((DebugDrawJ2D) m_debugDraw).drawCircle(
                        new Vec2((float) trace.points[0].x, (float) trace.points[0].y),
                        trace.radius,
                        trace.color3f, trace.width, (float) (1d * trace.ttl / trace.startTtl / 2)
                );
            } else if (trace.points.length == 1) {
                ((DebugDrawJ2D) m_debugDraw).drawCircle(
                        new Vec2((float) trace.points[0].x, (float) trace.points[0].y),
                        .2f,
                        trace.color3f, trace.width, (float) (1d * trace.ttl / trace.startTtl / 2)
                );
            } else {
                Vec2[] vec2s = new Vec2[trace.points.length];
                for (int i = 0; i < trace.points.length; i++) {
                    vec2s[i] = new Vec2((float) trace.points[i].x, (float) trace.points[i].y);
                }
                ((DebugDrawJ2D) m_debugDraw).drawPolygon(
                        vec2s, vec2s.length,
                        trace.color3f, trace.width, (float) (1d * trace.ttl / trace.startTtl / 2)
                );
            }

            trace.ttl--;
            if (trace.ttl <= 0) {
                iterator.remove();
            }
        }
    }

    private Color3f getUserColor(Body b) {
        Object userData = b.getUserData();
        Object actor;
        if (userData != null && userData instanceof Map &&
                (actor = ((Map) userData).get("actor")) != null &&
                actor instanceof Actor) {
            return ((Actor) actor).getColor();
        } else {
            return null;
        }
    }

    private void drawShape(Fixture fixture, Transform xf, Color3f color) {
        switch (fixture.getType()) {
            case CIRCLE: {
                CircleShape circle = (CircleShape) fixture.getShape();

                // Vec2 center = Mul(xf, circle.m_p);
                Transform.mulToOutUnsafe(xf, circle.m_p, center);
                float radius = circle.m_radius;
                xf.q.getXAxis(axis);

                m_debugDraw.drawSolidCircle(center, radius, axis, color);
            }
            break;

            case POLYGON: {
                PolygonShape poly = (PolygonShape) fixture.getShape();
                int vertexCount = poly.m_count;
                assert (vertexCount <= Settings.maxPolygonVertices);
                Vec2[] vertices = tlvertices.get(Settings.maxPolygonVertices);

                for (int i = 0; i < vertexCount; ++i) {
                    // vertices[i] = Mul(xf, poly.m_vertices[i]);
                    Transform.mulToOutUnsafe(xf, poly.m_vertices[i], vertices[i]);
                }

                m_debugDraw.drawSolidPolygon(vertices, vertexCount, color);
            }
            break;
            case EDGE: {
                EdgeShape edge = (EdgeShape) fixture.getShape();
                Transform.mulToOutUnsafe(xf, edge.m_vertex1, v1);
                Transform.mulToOutUnsafe(xf, edge.m_vertex2, v2);
                m_debugDraw.drawSegment(v1, v2, color);
            }
            break;

            case CHAIN: {
                ChainShape chain = (ChainShape) fixture.getShape();
                int count = chain.m_count;
                Vec2[] vertices = chain.m_vertices;

                Transform.mulToOutUnsafe(xf, vertices[0], v1);
                for (int i = 1; i < count; ++i) {
                    Transform.mulToOutUnsafe(xf, vertices[i], v2);
                    m_debugDraw.drawSegment(v1, v2, color);
                    m_debugDraw.drawCircle(v1, 0.05f, color);
                    v1.set(v2);
                }
            }
            break;
            default:
                break;
        }
    }

    private void drawJoint(Joint joint) {
        Body bodyA = joint.getBodyA();
        Body bodyB = joint.getBodyB();
        Transform xf1 = bodyA.getTransform();
        Transform xf2 = bodyB.getTransform();
        Vec2 x1 = xf1.p;
        Vec2 x2 = xf2.p;
        Vec2 p1 = pool.popVec2();
        Vec2 p2 = pool.popVec2();
        joint.getAnchorA(p1);
        joint.getAnchorB(p2);

        color.set(0.5f, 0.8f, 0.8f);

        switch (joint.getType()) {
            // TODO djm write after writing joints
            case DISTANCE:
                m_debugDraw.drawSegment(p1, p2, color);
                break;

            case PULLEY: {
                PulleyJoint pulley = (PulleyJoint) joint;
                Vec2 s1 = pulley.getGroundAnchorA();
                Vec2 s2 = pulley.getGroundAnchorB();
                m_debugDraw.drawSegment(s1, p1, color);
                m_debugDraw.drawSegment(s2, p2, color);
                m_debugDraw.drawSegment(s1, s2, color);
            }
            break;
            case CONSTANT_VOLUME:
            case MOUSE:
                // don't draw this
                break;
            default:
                m_debugDraw.drawSegment(x1, p1, color);
                m_debugDraw.drawSegment(p1, p2, color);
                m_debugDraw.drawSegment(x2, p2, color);
        }
        pool.pushVec2(2);
    }

}
