package robo2d.testbed.tests.move;

import robo2d.game.Game;
import robo2d.game.impl.*;
import robo2d.testbed.RobotTest;
import straightedge.geom.KPoint;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class MoveTest extends RobotTest {

    @Override
    public Game createGame() {
        Game game = new Game(getWorld(), getDebugDraw());

        ArrayList<Point2D> points = new ArrayList<Point2D>();
        points.add(new Point2D.Double(7d, -7d));
        points.add(new Point2D.Double(15d, -15d));
        points.add(new Point2D.Double(-10d, -15d));
        points.add(new Point2D.Double(-10d, -10d));
        points.add(new Point2D.Double(0, 10));
        points.add(new Point2D.Double(5, 5));
        points.add(new Point2D.Double(17, 13));
        points.add(new Point2D.Double(10, 0));

        game.addWall(new WallImpl(points, Math.PI * 0.01));

        PlayerImpl player1 = new PlayerImpl("player1");

        RobotImpl robot = new RobotImpl(game, player1, new KPoint(15, 15), Math.PI * 4 * Math.random());
        ChassisImpl chassis = new ChassisImpl(300d);
        RadarImpl radar = new RadarImpl(game, 100d);
        ComputerImpl computer = new ComputerImpl(EngineTestProgram.class);
        robot.addEquipment(chassis);
        robot.addEquipment(radar);
        robot.addEquipment(computer);
        robot.charge(400);
        game.addRobot(robot);
        game.addSatellite(30, 1);
        game.addGps();

        return game;
    }

    public static void main(String[] args) {
        new MoveTest().start();
    }
}
