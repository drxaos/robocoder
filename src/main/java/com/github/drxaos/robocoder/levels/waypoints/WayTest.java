package com.github.drxaos.robocoder.levels.waypoints;

import com.github.drxaos.robocoder.game.AbstractLevel;
import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.Robot;
import com.github.drxaos.robocoder.game.actors.Wall;
import com.github.drxaos.robocoder.geom.KPoint;

import java.util.ArrayList;

public class WayTest extends AbstractLevel {

    @Override
    public void initLevel(Game game) {
        ArrayList<KPoint> points = new ArrayList<KPoint>();

        double w = 20;
        double h = 1;
        double sp = 4;

        points.clear();
        points.add(new KPoint(-w / 2, -(h * 2 + sp) / 2));
        points.add(new KPoint(w / 2, -(h * 2 + sp) / 2));
        points.add(new KPoint(w / 2, -(h * 2 + sp) / 2 + h));
        points.add(new KPoint(-w / 2, -(h * 2 + sp) / 2 + h));
        game.addActor(new Wall(points, new KPoint(), 0));

        points.clear();
        points.add(new KPoint(-w / 2, (h * 2 + sp) / 2));
        points.add(new KPoint(w / 2, (h * 2 + sp) / 2));
        points.add(new KPoint(w / 2, (h * 2 + sp) / 2 - h));
        points.add(new KPoint(-w / 2, (h * 2 + sp) / 2 - h));
        game.addActor(new Wall(points, new KPoint(), 0));

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
