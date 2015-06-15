package com.github.drxaos.robocoder.game.box2d;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.RopeJoint;
import org.jbox2d.dynamics.joints.RopeJointDef;
import straightedge.geom.KPoint;

public class RobotModel extends AbstractModel {

    public static double SIZE = 1;
    RopeJoint joint;

    public RobotModel(KPoint position, double angle) {
        {
            PolygonShape shape = new PolygonShape();
            shape.setAsBox((float) SIZE, (float) SIZE);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.friction = 0.8f;
            fixtureDef.density = 1f;

            fixtureDefs.add(fixtureDef);
        }

        {
            PolygonShape shape = new PolygonShape();
            shape.set(new Vec2[]{
                    new Vec2(-0.8f * (float) SIZE, 0),
                    new Vec2(0, 1.2f * (float) SIZE),
                    new Vec2(0.8f * (float) SIZE, 0),
            }, 3);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.friction = 0.8f;
            fixtureDef.density = 1f;

            fixtureDefs.add(fixtureDef);
        }

        bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set((float) position.getX(), (float) position.getY());
        bodyDef.angle = (float) (angle - Math.PI / 2);
        bodyDef.allowSleep = false;
        bodyDef.linearDamping = 10f;
        bodyDef.angularDamping = 30f;
    }

    public double getAngle() {
        if (body == null) {
            return 0d;
        }
        return (double) body.getAngle() + Math.PI / 2;
    }


    public void tie(AbstractModel towable, boolean back) {
        if (body == null || joint != null) {
            return;
        }
        RopeJointDef jointDef = new RopeJointDef();
        jointDef.bodyA = body;
        jointDef.bodyB = towable.body;
        jointDef.localAnchorA.set(0.0f, (float) SIZE * (back ? -1 : 1));
        jointDef.localAnchorB.set(towable.getTiePoint());
        jointDef.maxLength = .01f;
        jointDef.collideConnected = true;
        joint = (RopeJoint) world.createJoint(jointDef);
    }

    public void untie() {
        if (body == null || joint == null) {
            return;
        }
        world.destroyJoint(joint);
        joint = null;
    }
}
