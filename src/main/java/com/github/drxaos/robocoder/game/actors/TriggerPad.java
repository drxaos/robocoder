package com.github.drxaos.robocoder.game.actors;


import org.jbox2d.common.Color3f;
import straightedge.geom.KPoint;

import java.util.List;

public class TriggerPad extends Pad {

    public static final Color3f activeColor = new Color3f(.5f, .5f, .9f);
    public static final Color3f triggeredColor = new Color3f(.3f, .3f, .3f);

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
        return model.body.isActive() ? activeColor : triggeredColor;
    }
}
