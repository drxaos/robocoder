package robo2d.game.impl;

import robo2d.game.api.Player;
import robo2d.game.api.map.Obj;
import robo2d.game.box2d.Box;
import robo2d.game.box2d.Physical;
import robo2d.game.box2d.StaticBox;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WallImpl implements Obj, Physical {

    StaticBox box;
    List<Point2D> vertices;

    public WallImpl(List<Point2D> vertices, double angle) {
        this.vertices = Collections.unmodifiableList(vertices);

        ArrayList<KPoint> kPoints = new ArrayList<KPoint>();
        for (Point2D vertice : vertices) {
            kPoints.add(new KPoint(vertice.getX(), vertice.getY()));
        }

        box = new StaticBox(new KPolygon(kPoints), new KPoint(0, 0), angle);
    }

    @Override
    public Player getOwner() {
        return null;
    }

    @Override
    public Type getType() {
        return Type.WALL;
    }

    @Override
    public List<Point2D> getVertices() {
        return vertices;
    }

    @Override
    public Box getBox() {
        return box;
    }
}
