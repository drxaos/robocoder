package com.github.drxaos.robocoder.game.actors;


import org.jbox2d.common.Color3f;
import straightedge.geom.KPoint;

public class StartPad extends Pad {

    public static final float SIZE = 2;
    public static final Color3f defaultColor = new Color3f(.3f, .8f, .3f);

    public StartPad(KPoint position, float width, float height, double angle) {
        super(position, width, height, angle);
    }

    public StartPad(KPoint position, double angle) {
        super(position, SIZE, SIZE, angle);
    }

    @Override
    public Color3f getColor() {
        return defaultColor;
    }

    @Override
    public String[] getTags() {
        return new String[]{"pad", "sensor", "unbreakable", "startpad"};
    }
}
