package com.github.drxaos.robocoder.game.box2d;

import com.github.drxaos.robocoder.geom.KPoint;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class BulletModel extends AbstractModel {

    public BulletModel(KPoint position, float angle, float speed, float size) {

        CircleShape shape = new CircleShape();
        shape.setRadius(size);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 1000f;
        fixtureDef.density = 25f * size;
        fixtureDefs.add(fixtureDef);

        bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.bullet = true;
        bodyDef.position.set((float) position.getX(), (float) position.getY());
        bodyDef.angle = (float) (angle - Math.PI / 2);
        bodyDef.allowSleep = false;
        bodyDef.linearDamping = 0f;
        bodyDef.angularDamping = 0f;
        bodyDef.angularVelocity = 10f;
        bodyDef.linearVelocity = new Vec2((float) (Math.cos(angle) * speed), (float) (Math.sin(angle) * speed));
    }
}
