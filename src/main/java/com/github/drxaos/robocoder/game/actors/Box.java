package com.github.drxaos.robocoder.game.actors;

import com.github.drxaos.robocoder.game.box2d.BoxModel;
import org.jbox2d.common.Color3f;
import straightedge.geom.KPoint;

public class Box extends Actor {
    BoxModel model;

    public Box(double x, double y, double angle) {
        model = new BoxModel(new KPoint(x, y), angle);
    }

    @Override
    public BoxModel getModel() {
        return model;
    }

    public static final Color3f COLOR = new Color3f(.85f, .64f, .125f);

    @Override
    public Color3f getColor() {
        return COLOR;
    }

    @Override
    public String[] getTags() {
        return new String[]{"box", "dynamic", "breakable"};
    }

    @Override
    public boolean isTowable() {
        return true;
    }
}
