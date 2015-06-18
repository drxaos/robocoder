package com.github.drxaos.robocoder.game.actors;


import com.github.drxaos.robocoder.geom.KPoint;
import org.jbox2d.common.Color3f;

public class FinishPad extends Pad {

    public static final float SIZE = 2;
    public static final Color3f defaultColor = new Color3f(.8f, .3f, .8f);

    public FinishPad(KPoint position, float width, float height, double angle) {
        super(position, width, height, angle);
    }

    public FinishPad(KPoint position, double angle) {
        super(position, SIZE, SIZE, angle);
    }

    @Override
    public Color3f getColor() {
        return defaultColor;
    }

    @Override
    public String[] getTags() {
        return new String[]{"pad", "sensor", "unbreakable", "finishpad"};
    }
}
