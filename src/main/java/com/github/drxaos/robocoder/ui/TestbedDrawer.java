package com.github.drxaos.robocoder.ui;

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
import org.jbox2d.pooling.arrays.Vec2Array;

import java.util.Map;

public class TestbedDrawer {

    private DebugDraw m_debugDraw;

    private final Transform xf = new Transform();
    private final Color3f color = new Color3f();
    private final Vec2 center = new Vec2();
    private final Vec2 axis = new Vec2();
    private final Vec2 v1 = new Vec2();
    private final Vec2 v2 = new Vec2();
    private final Vec2Array tlvertices = new Vec2Array();

    public TestbedDrawer(DebugDraw m_debugDraw) {
        this.m_debugDraw = m_debugDraw;
    }

    public void drawWorld(Body m_bodyList) {
        if (m_debugDraw == null) {
            return;
        }

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

        int flags = m_debugDraw.getFlags();

        if ((flags & DebugDraw.e_shapeBit) == DebugDraw.e_shapeBit) {
            for (Body b = m_bodyList; b != null; b = b.getNext()) {
                xf.set(b.getTransform());
                for (Fixture f = b.getFixtureList(); f != null; f = f.getNext()) {
                    Color3f userColor = getUserColor(b);
                    if (!b.isActive()) {
                        if (userColor != null) {
                            color.set(userColor);
                        } else {
                            color.set(0.5f, 0.5f, 0.3f);
                        }
                        drawShape(f, xf, color);
                    } else if (b.getType() == BodyType.STATIC) {
                        if (userColor != null) {
                            color.set(userColor);
                        } else {
                            color.set(0.5f, 0.9f, 0.3f);
                        }
                        drawShape(f, xf, color);
                    } else if (b.getType() == BodyType.KINEMATIC) {
                        if (userColor != null) {
                            color.set(userColor);
                        } else {
                            color.set(0.5f, 0.5f, 0.9f);
                        }
                        drawShape(f, xf, color);
                    } else if (!b.isAwake()) {
                        if (userColor != null) {
                            color.set(userColor);
                        } else {
                            color.set(0.5f, 0.5f, 0.5f);
                        }
                        drawShape(f, xf, color);
                    } else {
                        if (userColor != null) {
                            color.set(userColor);
                        } else {
                            color.set(0.9f, 0.7f, 0.7f);
                        }
                        drawShape(f, xf, color);
                    }
                }
            }
        }
    }

    private Color3f getUserColor(Body b) {
        Object userData = b.getUserData();
        Object userColor;
        if (userData != null && userData instanceof Map &&
                (userColor = ((Map) userData).get("color")) != null &&
                userColor instanceof Color3f) {
            return (Color3f) userColor;
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
}
