package robo2d.testbed.tests.waypoints;

import robo2d.game.Game;
import robo2d.game.impl.*;
import robo2d.testbed.RobotTest;
import straightedge.geom.KPoint;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class WayTest extends RobotTest {

    @Override
    public Game createGame() {
        Game game = new Game(getWorld(), getDebugDraw());

        ArrayList<Point2D> points = new ArrayList<Point2D>();

        double w = 20;
        double h = 1;
        double sp = 4;

        points.clear();
        points.add(new Point2D.Double(-w / 2, -(h * 2 + sp) / 2));
        points.add(new Point2D.Double(w / 2, -(h * 2 + sp) / 2));
        points.add(new Point2D.Double(w / 2, -(h * 2 + sp) / 2 + h));
        points.add(new Point2D.Double(-w / 2, -(h * 2 + sp) / 2 + h));
        game.addWall(new WallImpl(points, 0));

        points.clear();
        points.add(new Point2D.Double(-w / 2, (h * 2 + sp) / 2));
        points.add(new Point2D.Double(w / 2, (h * 2 + sp) / 2));
        points.add(new Point2D.Double(w / 2, (h * 2 + sp) / 2 - h));
        points.add(new Point2D.Double(-w / 2, (h * 2 + sp) / 2 - h));
        game.addWall(new WallImpl(points, 0));

        PlayerImpl player1 = new PlayerImpl("player1");

        RobotImpl robot = new RobotImpl(game, player1, new KPoint(0, -10), Math.PI * 4 * Math.random());
        ChassisImpl chassis = new ChassisImpl(300d);
        RadarImpl radar = new RadarImpl(game, 100d);
        ComputerImpl computer = new ComputerImpl(WayTestProgram.class);
        robot.addEquipment(chassis);
        robot.addEquipment(radar);
        robot.addEquipment(computer);
        robot.charge(4000);
        game.addRobot(robot);
        game.addGps();
        game.addSatellite(20, 2000);

        return game;
    }

    public static void main(String[] args) {
        new WayTest().start();
    }
}
