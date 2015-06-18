package com.github.drxaos.robocoder.game.box2d;

import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.geom.KPolygon;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.util.ArrayList;
import java.util.List;

public class StaticModel extends AbstractModel {

    public StaticModel(KPolygon kPolygon, KPoint position, double angle) {
        List<KPolygon> triangulated = PolygonUtil.triangulate(kPolygon);
        for (KPolygon polygon : triangulated) {
            PolygonShape shape = new PolygonShape();
            ArrayList<KPoint> points = polygon.getPoints();
            Vec2[] vec2s = new Vec2[points.size()];
            int i = 0;
            for (KPoint point : points) {
                vec2s[i++] = new Vec2((float) point.getX(), (float) point.getY());
            }
            shape.set(vec2s, vec2s.length);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.friction = 0.8f;

            fixtureDefs.add(fixtureDef);
        }

        bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        bodyDef.position.set((float) position.getX(), (float) position.getY());
        bodyDef.angle = (float) angle;
        bodyDef.allowSleep = true;
    }


    public StaticModel(KPoint position, float width, float height, double angle) {

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.8f;

        fixtureDefs.add(fixtureDef);

        bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        bodyDef.position.set((float) position.getX(), (float) position.getY());
        bodyDef.angle = (float) angle;
        bodyDef.allowSleep = true;
    }

    public StaticModel(KPoint position, float radius, double angle) {

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.8f;

        fixtureDefs.add(fixtureDef);

        bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        bodyDef.position.set((float) position.getX(), (float) position.getY());
        bodyDef.angle = (float) angle;
        bodyDef.allowSleep = true;
    }
}
