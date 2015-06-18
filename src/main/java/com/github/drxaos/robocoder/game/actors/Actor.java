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
    protected float shield = 5f;
    protected float damage = 0f;

    public abstract AbstractModel getModel();

    public void start() {
    }

    public void stop() {
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
        return Collections.unmodifiableSet(new HashSet<Actor>(contacts));
    }

    public void beginContact(Actor actor) {
        contacts.add(actor);
    }

    public void endContact(Actor actor) {
        contacts.remove(actor);
    }

    public float getShield() {
        return shield;
    }

    public void setShield(float shield) {
        if (shield < 0) {
            this.shield = 0;
        } else {
            this.shield = shield;
        }
        this.damage = 0;
    }

    public float getDamage() {
        return damage;
    }

    public float getArmour() {
        return shield - damage;
    }

    public int getArmourPercent() {
        return Math.round(getArmour() / shield * 100);
    }

    public void setDamage(float damage) {
        if (damage < 0) {
            this.damage = 0;
        } else if (damage > shield) {
            this.damage = shield;
        } else {
            this.damage = damage;
        }
    }

    public void damage(float points) {
        if (points < 0) {
            return;
        }
        this.damage += points;
        if (damage < 0) {
            damage = 0;
        } else if (damage > shield) {
            damage = shield;
        }
    }

    public void repair(float points) {
        if (points < 0) {
            return;
        }
        this.damage -= points;
        if (damage < 0) {
            damage = 0;
        } else if (damage > shield) {
            damage = shield;
        }
    }

    public boolean isDestroyed() {
        return damage >= shield;
    }
}
