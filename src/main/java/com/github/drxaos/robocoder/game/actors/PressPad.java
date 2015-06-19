package com.github.drxaos.robocoder.game.actors;


import com.github.drxaos.robocoder.geom.KPoint;
import org.jbox2d.common.Color3f;

import java.util.List;

public class PressPad extends Pad implements Trigger {

    public static final Color3f waitingColor = new Color3f(.7f, .7f, .99f);
    public static final Color3f triggeredColor = new Color3f(.5f, .5f, .8f);

    protected long triggered = 0;

    public boolean isTriggered() {
        return triggered > 0;
    }

    public PressPad(List<KPoint> vertices, KPoint position, double angle) {
        super(vertices, position, angle);
    }

    public PressPad(KPoint position, float width, float height, double angle) {
        super(position, width, height, angle);
    }

    public PressPad(KPoint position, float radius) {
        super(position, radius);
    }

    @Override
    public String[] getTags() {
        return new String[]{"pad", "sensor", "unbreakable", "presspad"};
    }

    @Override
    public Color3f getColor() {
        return triggered > 0 ? triggeredColor : waitingColor;
    }

    @Override
    public void beginContact(Actor actor) {
        if (actor.hasTag("dynamic")) {
            triggered++;
        }
    }

    @Override
    public void endContact(Actor actor) {
        if (actor.hasTag("dynamic")) {
            triggered--;
        }
    }
}
