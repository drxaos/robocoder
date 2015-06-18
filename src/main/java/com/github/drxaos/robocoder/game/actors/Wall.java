package com.github.drxaos.robocoder.game.actors;


import com.github.drxaos.robocoder.game.box2d.AbstractModel;
import com.github.drxaos.robocoder.game.box2d.StaticModel;
import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.geom.KPolygon;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Wall extends Actor {

    protected StaticModel model;
    protected List<Point2D> vertices;

    public Wall(List<Point2D> vertices, double angle) {
        this.vertices = Collections.unmodifiableList(vertices);

        ArrayList<KPoint> kPoints = new ArrayList<KPoint>();
        for (Point2D vertice : vertices) {
            kPoints.add(new KPoint(vertice.getX(), vertice.getY()));
        }

        model = new StaticModel(new KPolygon(kPoints), new KPoint(0, 0), angle);
    }

    public List<Point2D> getVertices() {
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
}
