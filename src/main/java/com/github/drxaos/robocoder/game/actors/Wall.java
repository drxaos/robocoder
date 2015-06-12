package com.github.drxaos.robocoder.game.actors;


import com.github.drxaos.robocoder.game.Actor;
import com.github.drxaos.robocoder.game.box2d.Box;
import com.github.drxaos.robocoder.game.box2d.StaticBox;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Wall implements Actor {

    StaticBox box;
    List<Point2D> vertices;

    public Wall(List<Point2D> vertices, double angle) {
        this.vertices = Collections.unmodifiableList(vertices);

        ArrayList<KPoint> kPoints = new ArrayList<KPoint>();
        for (Point2D vertice : vertices) {
            kPoints.add(new KPoint(vertice.getX(), vertice.getY()));
        }

        box = new StaticBox(new KPolygon(kPoints), new KPoint(0, 0), angle);
    }

    public List<Point2D> getVertices() {
        return vertices;
    }

    @Override
    public Box getBox() {
        return box;
    }

    @Override
    public void start() {
    }

    @Override
    public void beforeStep() {

    }

    @Override
    public void afterStep() {

    }
}
