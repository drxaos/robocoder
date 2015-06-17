package com.github.drxaos.robocoder.game.actors;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.box2d.AbstractModel;
import org.jbox2d.common.Color3f;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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

    public Set<String> getTagsSet() {
        return new HashSet<String>(Arrays.asList(getTags()));
    }

    public boolean hasTag(String tag) {
        return getTagsSet().contains(tag);
    }

    protected HashSet<Actor> contacts = new HashSet<Actor>();

    public boolean isSensor() {
        return false;
    }

    public Set<Actor> getContacts() {
        if (!isSensor()) {
            return null;
        }
        return Collections.unmodifiableSet(contacts);
    }

    public void beginContact(Actor actor) {
        contacts.add(actor);
    }

    public void endContact(Actor actor) {
        contacts.remove(actor);
    }
}
