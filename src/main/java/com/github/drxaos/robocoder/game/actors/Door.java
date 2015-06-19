package com.github.drxaos.robocoder.game.actors;


import com.github.drxaos.robocoder.game.box2d.AbstractModel;
import com.github.drxaos.robocoder.game.box2d.StaticModel;
import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.geom.KPolygon;
import org.jbox2d.common.Color3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Door extends Actor {

    protected final Color3f color = new Color3f(0.99f, 0.88f, 0.88f);
    protected final Color3f openedColor = new Color3f(0.3f, 0.3f, 0.3f);

    protected boolean opened = false;

    protected StaticModel model;
    protected List<Point2D> vertices;

    public Door(List<Point2D> vertices, double angle) {
        this.vertices = Collections.unmodifiableList(vertices);

        ArrayList<KPoint> kPoints = new ArrayList<KPoint>();
        for (Point2D vertice : vertices) {
            kPoints.add(new KPoint(vertice.getX(), vertice.getY()));
        }

        model = new StaticModel(new KPolygon(kPoints), new KPoint(0, 0), angle);
    }

    public Door(KPoint position, float width, float height, double angle) {
        model = new StaticModel(position, width, height, angle);
    }

    public Door(KPoint position, float radius) {
        model = new StaticModel(position, radius, 0d);
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
        return new String[]{"door", "static", "unbreakable"};
    }

    @Override
    public Color3f getColor() {
        return opened ? openedColor : color;
    }

    protected List<Trigger> triggers = new ArrayList<Trigger>();

    public void addTrigger(Trigger trigger) {
        triggers.add(trigger);
    }

    @Override
    public void afterStep() {
        super.afterStep();
        for (Trigger trigger : triggers) {
            if (trigger.isTriggered()) {
                model.body.setActive(false);
                opened = true;
                return;
            }
        }
        model.body.setActive(true);
        opened = false;
    }
}
