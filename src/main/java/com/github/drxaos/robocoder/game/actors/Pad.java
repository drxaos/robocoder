package com.github.drxaos.robocoder.game.actors;


import com.github.drxaos.robocoder.game.box2d.AbstractModel;
import com.github.drxaos.robocoder.game.box2d.SensorModel;
import org.jbox2d.common.Color3f;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

import java.util.ArrayList;
import java.util.List;

public class Pad extends Actor {

    public static final Color3f defaultColor = new Color3f(.5f, .5f, .9f);

    SensorModel model;
    ArrayList<KPoint> vertices;

    public Pad(List<KPoint> vertices, KPoint position, double angle) {
        this.vertices = new ArrayList<KPoint>();
        this.vertices.addAll(vertices);
        model = new SensorModel(new KPolygon(this.vertices), position, angle);
    }

    public Pad(KPoint position, float width, float height, double angle) {
        model = new SensorModel(position, width, height, angle);
    }

    public Pad(KPoint position, float radius) {
        model = new SensorModel(position, radius, 0d);
    }

    @Override
    public AbstractModel getModel() {
        return model;
    }

    @Override
    public String[] getTags() {
        return new String[]{"pad", "sensor", "unbreakable"};
    }

    @Override
    public Color3f getColor() {
        return defaultColor;
    }

    @Override
    public boolean isSensor() {
        return true;
    }
}
