package robo2d.game.box2d;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import straightedge.geom.KPoint;

import java.util.ArrayList;
import java.util.List;

public class Box {
    public BodyDef bodyDef;
    public List<FixtureDef> fixtureDefs = new ArrayList<FixtureDef>();
    public Body body;

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
}
