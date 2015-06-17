package com.github.drxaos.robocoder.game.actors;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.box2d.AbstractModel;
import org.jbox2d.common.Color3f;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Actor {
    protected Game game;

    public abstract AbstractModel getModel();

    public void start() {
    }

    public void beforeStep() {
    }

    public void afterStep() {
    }

    public boolean isTowable() {
        return false;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Color3f getColor() {
        return null;
    }

    public abstract String[] getTags();

    public boolean isSensor() {
        return false;
    }

    public Set<Actor> getContacts() {
        if (!isSensor()) {
            return null;
        }
        return ((HashSet<Actor>) ((Map) getModel().body.getUserData()).get("contacts"));
    }
}
