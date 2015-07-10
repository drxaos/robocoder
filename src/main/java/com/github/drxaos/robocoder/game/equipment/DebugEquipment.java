package com.github.drxaos.robocoder.game.equipment;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.ControlledActor;
import com.github.drxaos.robocoder.game.box2d.AbstractModel;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Color3f;
import org.jbox2d.dynamics.Fixture;

public class DebugEquipment implements Equipment {
    protected final String SAY = "debug::say::";

    protected int ttl = 1000;

    public void communicate(ControlledActor robot, Game game) {
        String req = robot.getBus().getRequest();
        if (req == null || !req.startsWith("debug::")) {
            return;
        }
        if (req.startsWith(SAY)) {
            message = req.substring(SAY.length());
            update = game.getTime();
            robot.getBus().writeResponse("radio-station::accepted");
        }
    }

    protected String message = null;
    protected long update = 0;

    protected Game.Trace textTrace;
    protected Color3f traceColor = new Color3f(.5f, .5f, .99f);

    public void applyPhysics(ControlledActor self, Game game) {
        if (textTrace == null) {
            textTrace = new Game.Trace(self.getModel().getPosition(), "", traceColor);
            textTrace.permanent(true).ttl(20, 15);
            game.addTrace(textTrace);
        }
        if (update + ttl < game.getTime()) {
            message = null;
        }
        if (message != null && !self.isDestroyed()) {
            AbstractModel model = self.getModel();
            AABB aabb = new AABB();
            aabb.lowerBound.set(Float.MAX_VALUE, Float.MAX_VALUE);
            aabb.upperBound.set(-Float.MAX_VALUE, -Float.MAX_VALUE);
            for (Fixture f = model.body.m_fixtureList; f != null; f = f.getNext()) {
                aabb.combine(aabb, f.getAABB(0));
            }
            textTrace.points[0].setCoords(aabb.upperBound.x, aabb.upperBound.y);
            textTrace.text = message;
            textTrace.ttl = 15;
        } else {
            textTrace.text = "";
            textTrace.ttl = 0;
        }
    }
}
