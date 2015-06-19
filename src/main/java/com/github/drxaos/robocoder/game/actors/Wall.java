package com.github.drxaos.robocoder.game.actors;


import com.github.drxaos.robocoder.game.box2d.AbstractModel;
import com.github.drxaos.robocoder.game.box2d.StaticModel;
import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.geom.KPolygon;
import org.jbox2d.common.Color3f;

import java.util.ArrayList;
import java.util.List;

public class Wall extends Actor {

    protected final Color3f color = new Color3f(0.6f, 0.6f, 0.6f);

    protected StaticModel model;
    protected ArrayList<KPoint> vertices;

    public Wall(List<KPoint> vertices, KPoint position, double angle) {
        this.vertices = new ArrayList<KPoint>();
        this.vertices.addAll(vertices);
        model = new StaticModel(new KPolygon(this.vertices), position, angle);
    }


    public Wall(KPoint position, float width, float height, double angle) {
        model = new StaticModel(position, width, height, angle);
    }

    public Wall(KPoint position, float radius) {
        model = new StaticModel(position, radius, 0d);
    }

    public List<KPoint> getVertices() {
        return vertices;
    }

    @Override
    public AbstractModel getModel() {
        return model;
    }

    @Override
    public String[] getTags() {
        return new String[]{"wall", "static", "unbreakable"};
    }

    @Override
    public Color3f getColor() {
        return color;
    }
}
