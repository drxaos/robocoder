package robo2d.testbed.tests.move;

import robo2d.game.api.Chassis;
import robo2d.game.api.Radar;
import robo2d.game.impl.RobotProgram;
import straightedge.geom.KPoint;


public class EngineTestProgram extends RobotProgram {

    Chassis chassis;
    Radar radar;

    @Override
    public void program() {
        robot.debug("init");

        chassis = robot.getEquipment(Chassis.class);
        radar = robot.getEquipment(Radar.class);

        if (chassis == null || radar == null) {
            robot.debug("fail");
            return;
        }

        robot.debug("Move smooth to 15,40");
        robot.debug(new KPoint(15, 40));
        driver.moveSmooth(new KPoint(15, 40), 20000);
        driver.stop();
        robot.debug("Move to 25,30");
        robot.debug(new KPoint(25, 30));
        driver.move(new KPoint(25, 30), false, 20000);
        robot.debug("Move precise to 5,20");
        robot.debug(new KPoint(5, 20));
        driver.move(new KPoint(5, 20), true, 20000);
        robot.debug(null);

        robot.debug("Rotate to 0");
        driver.rotate(0, false, 10000);
        sleep(1000);
        robot.debug("Forward 10");
        driver.forward(10, false, 10000);
        sleep(1000);

        robot.debug("Test engines");

        chassis.setLeftAcceleration(100d);
        chassis.setRightAcceleration(100d);
        sleep(1000);
        chassis.setLeftAcceleration(0d);
        chassis.setRightAcceleration(100d);
        sleep(1000);
        chassis.setLeftAcceleration(100d);
        chassis.setRightAcceleration(0d);
        sleep(1000);
        chassis.setLeftAcceleration(50d);
        chassis.setRightAcceleration(-100d);
        sleep(1000);
        chassis.setLeftAcceleration(-100d);
        chassis.setRightAcceleration(-50d);
        sleep(1000);
        chassis.setLeftAcceleration(-100d);
        chassis.setRightAcceleration(100d);
        sleep(1000);
        chassis.setLeftAcceleration(0d);
        chassis.setRightAcceleration(0d);
        sleep(1000);
    }
}
