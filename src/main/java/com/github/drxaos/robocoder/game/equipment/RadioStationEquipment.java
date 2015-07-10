package com.github.drxaos.robocoder.game.equipment;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.Actor;
import com.github.drxaos.robocoder.game.actors.ControlledActor;
import org.jbox2d.common.Color3f;

import java.util.List;

public class RadioStationEquipment implements Equipment {

    protected final String TRANSMIT = "radio-station::transmit::";
    protected double distance = 10d;
    protected int ttl = 3;

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void communicate(ControlledActor robot, Game game) {
        String req = robot.getBus().getRequest();
        if (req == null || !req.startsWith("radio-station::")) {
            return;
        }
        if (req.startsWith(TRANSMIT)) {
            message = req.substring(TRANSMIT.length());
            update = game.getTime();
            robot.getBus().writeResponse("radio-station::accepted");
        }
    }

    protected String message = "";
    protected long update = 0;

    protected Game.Trace trace;
    protected Color3f traceColor = new Color3f(.7f, .7f, .1f);

    public void applyPhysics(ControlledActor self, Game game) {
        if (trace == null) {
            trace = new Game.Trace(self.getModel().getPosition(), (float) distance, traceColor);
            trace.permanent(true).ttl(20, 10).width(.1f);
            game.addTrace(trace);
        }
        if (update + ttl < game.getTime()) {
            message = null;
        }
        if (message != null && !self.isDestroyed()) {
            List<Actor> actors = game.resolveCircle(self.getModel().getPosition(), distance);
            for (Actor actor : actors) {
                if (actor instanceof ControlledActor) {
                    RadioEquipment radio = ((ControlledActor) actor).getEquipment(RadioEquipment.class);
                    if (radio != null) {
                        radio.addMessage(message);
                    }
                }
            }
            trace.ttl = (int) (1 + Math.abs(Math.sin(1f * game.getTime() / 80)) * trace.startTtl / 2);
        } else {
            trace.ttl = 0;
        }
    }
}
