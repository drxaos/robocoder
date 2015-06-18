package com.github.drxaos.robocoder.game.actors;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.box2d.BulletModel;
import com.github.drxaos.robocoder.geom.KPoint;
import org.jbox2d.common.Color3f;

public class Bullet extends Actor {
    public static final Color3f defaultColor = new Color3f(.9f, .7f, .2f);

    public static final float SIZE_LIGHT = 0.1f; // 1 damage point
    public static final float SIZE_HEAVY = 0.4f; // 4 damage points

    BulletModel model;
    Game.Trace trace;
    float damage;

    public Bullet(double x, double y, double angle, float speed, float size) {
        model = new BulletModel(new KPoint(x, y), (float) angle, speed, size);
        trace = new Game.Trace(new KPoint[]{new KPoint(), new KPoint()}, defaultColor, 4);
        trace.width = size / 2;
        damage = size * 10;
    }

    @Override
    public BulletModel getModel() {
        return model;
    }

    @Override
    public Color3f getColor() {
        return Color3f.BLACK;
    }

    @Override
    public String[] getTags() {
        return new String[]{"bullet", "dynamic", "breakable"};
    }

    KPoint prevPosition;

    @Override
    public void beforeStep() {
        prevPosition = model.getPosition();
    }

    @Override
    public void afterStep() {
        Game.Trace t = trace.copy();
        t.points[0].setCoords(prevPosition);
        t.points[1].setCoords(model.getPosition());
        game.addTrace(t);

        boolean collision = false;
        for (Actor actor : getContacts()) {
            if (!actor.isSensor()) {
                actor.damage(damage);
                collision = true;
            }
        }
        if (collision) {
            game.removeActor(this);
        }
    }
}
