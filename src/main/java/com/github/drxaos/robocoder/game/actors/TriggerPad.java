package com.github.drxaos.robocoder.game.actors;


import com.github.drxaos.robocoder.geom.KPoint;
import org.jbox2d.common.Color3f;

import java.util.List;

public class TriggerPad extends Pad implements Trigger {

    public static final Color3f waitingColor = new Color3f(.5f, .5f, .9f);
    public static final Color3f triggeredColor = new Color3f(.3f, .3f, .3f);

    protected boolean triggered = false;

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public TriggerPad(List<KPoint> vertices, KPoint position, double angle) {
        super(vertices, position, angle);
    }

    public TriggerPad(KPoint position, float width, float height, double angle) {
        super(position, width, height, angle);
    }

    public TriggerPad(KPoint position, float radius) {
        super(position, radius);
    }

    @Override
    public String[] getTags() {
        return new String[]{"pad", "sensor", "unbreakable", "triggerpad"};
    }

    @Override
    public Color3f getColor() {
        return triggered ? triggeredColor : waitingColor;
    }

    @Override
    public void beginContact(Actor actor) {
        if (actor.hasTag("dynamic")) {
            triggered = true;
        }
    }
}
