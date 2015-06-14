package com.github.drxaos.robocoder.game;

import com.github.drxaos.robocoder.game.box2d.AbstractModel;
import org.jbox2d.common.Color3f;

public abstract class Actor {
    protected Game game;

    public abstract AbstractModel getModel();

    public void start() {
    }

    public void beforeStep() {
    }

    public void afterStep() {
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Color3f getColor() {
        return null;
    }
}
