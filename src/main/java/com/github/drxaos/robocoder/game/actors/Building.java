package com.github.drxaos.robocoder.game.actors;


import com.github.drxaos.robocoder.game.box2d.AbstractModel;
import com.github.drxaos.robocoder.game.box2d.StaticModel;
import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.geom.KPolygon;
import org.jbox2d.common.Color3f;

import java.util.ArrayList;
import java.util.List;

public class Building extends Actor {

    protected StaticModel model;
    protected ArrayList<KPoint> vertices;
    protected Color3f color = new Color3f(0.9f, 0.7f, 0.7f);
    protected final Color3f destroyedColor = new Color3f(0.3f, 0.3f, 0.3f);

    public Building(List<KPoint> vertices, KPoint position, double angle) {
        this.vertices = new ArrayList<KPoint>();
        this.vertices.addAll(vertices);
        model = new StaticModel(new KPolygon(this.vertices), position, angle);
    }

    public Building(KPoint position, float width, float height, double angle) {
        model = new StaticModel(position, width, height, angle);
    }

    public Building(KPoint position, float radius) {
        model = new StaticModel(position, radius, 0d);
    }

    @Override
    public AbstractModel getModel() {
        return model;
    }

    @Override
    public String[] getTags() {
        return new String[]{"building", "static", "breakable"};
    }

    @Override
    public void damage(float points) {
        super.damage(points);
        if (getArmour() == 0) {
            model.body.setActive(false);
        }
    }

    @Override
    public Color3f getColor() {
        if (model.body.isActive()) {
            return color;
        } else {
            return destroyedColor;
        }
    }

    public void setColor(Color3f color) {
        this.color = color;
    }
}
