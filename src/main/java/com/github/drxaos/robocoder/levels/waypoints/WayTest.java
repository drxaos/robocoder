package com.github.drxaos.robocoder.levels.waypoints;

import com.github.drxaos.robocoder.game.AbstractLevel;
import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.Robot;
import com.github.drxaos.robocoder.game.actors.Wall;
import com.github.drxaos.robocoder.game.equipment.ChassisEquipment;
import com.github.drxaos.robocoder.game.equipment.RadarEquipment;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class WayTest extends AbstractLevel {

    @Override
    public void initLevel(Game game) {
        ArrayList<Point2D> points = new ArrayList<Point2D>();

        double w = 20;
        double h = 1;
        double sp = 4;

        points.clear();
        points.add(new Point2D.Double(-w / 2, -(h * 2 + sp) / 2));
        points.add(new Point2D.Double(w / 2, -(h * 2 + sp) / 2));
        points.add(new Point2D.Double(w / 2, -(h * 2 + sp) / 2 + h));
        points.add(new Point2D.Double(-w / 2, -(h * 2 + sp) / 2 + h));
        game.addActor(new Wall(points, 0));

        points.clear();
        points.add(new Point2D.Double(-w / 2, (h * 2 + sp) / 2));
        points.add(new Point2D.Double(w / 2, (h * 2 + sp) / 2));
        points.add(new Point2D.Double(w / 2, (h * 2 + sp) / 2 - h));
        points.add(new Point2D.Double(-w / 2, (h * 2 + sp) / 2 - h));
        game.addActor(new Wall(points, 0));

        Robot robot = new Robot("RC-1", 0, -10, Math.PI * 4 * Math.random());
        robot.addDefaultEquipment();
        robot.setProgram(userProgram);
        robot.enableLogging();
        game.addActor(robot);
    }

    int n = 2;

    @Override
    public synchronized void step() {
        super.step();
        if (game.getTime() % 750 == 0 && game.getTime() < 2000) {
            Robot robot = new Robot("RC-" + n++, 0, -10, Math.PI * 4 * Math.random());
            robot.addDefaultEquipment();
            robot.setProgram(userProgram);
            robot.enableLogging();
            game.addActor(robot);
        }
    }
}
