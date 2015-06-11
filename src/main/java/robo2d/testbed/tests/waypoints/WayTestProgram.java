package robo2d.testbed.tests.waypoints;

import robo2d.game.api.Chassis;
import robo2d.game.api.Radar;
import robo2d.game.impl.RobotProgram;
import straightedge.geom.KPoint;

import java.util.ArrayList;


public class WayTestProgram extends RobotProgram {

    Chassis chassis;
    Radar radar;

    @Override
    public void program() {
        chassis = robot.getEquipment(Chassis.class);
        radar = robot.getEquipment(Radar.class);

        if (chassis == null || radar == null) {
            robot.debug("fail");
            return;
        }

        ArrayList<KPoint> way = new ArrayList<KPoint>();
        way.add(new KPoint(0, -10));
        way.add(new KPoint(12, -5));
        way.add(new KPoint(12, 0));
        way.add(new KPoint(0, 0));
        way.add(new KPoint(-12, 0));
        way.add(new KPoint(-12, 5));
        way.add(new KPoint(0, 10));
        way.add(new KPoint(15, 5));
//        way.add(new KPoint(15, -5));
        way.add(new KPoint(0, -10));

        for (KPoint wayPoint : way) {
            while (!driver.moveSmooth(wayPoint, 4000)) {
                driver.stop();
                KPoint failPoint = radar.getPosition();
                robot.debug(wayPoint);
                robot.debug("Please drag me to that point!");
                while (true) {
                    // save energy
                    KPoint newPoint = radar.getPosition();
                    if (Utils.distance(newPoint, failPoint) > 0.2) {
                        break;
                    }
                    robot.waitForStep();
                }
            }
            robot.debug(null);

            long waitUntil = robot.getTime() + 100;
            double angle = Math.PI / 2;
            int scans = 0;
            int walls = 0;
            while (robot.getTime() < waitUntil) {
                Radar.LocatorScanData scan = radar.locate(angle += 0.001);
                scans++;
                if (scan.pixel == Radar.Type.WALL) {
                    walls++;
                }
            }
            System.out.println("Time: 100ms, Scans: " + scans + ", walls: " + walls);

            if (Utils.distance(radar.getPosition(), new KPoint(0, 0)) < 0.3) {
                driver.stop();
                radar.satelliteRequest(radar.getPosition(), 0.5);
                waitUntil = robot.getTime() + 10000;
                while (radar.getSatelliteResponse() == null && robot.getTime() < waitUntil) {
                    waitForChanges();
                }
                Radar.SatelliteScanData response = radar.getSatelliteResponse();
                if (response != null) {
                    for (int i = 0; i < response.image.length; i++) {
                        for (int j = 0; j < response.image[i].length; j++) {
                            System.out.print(response.image[j][i].name().charAt(0));
                        }
                        System.out.print("\n");
                    }
                }
            }
        }
    }
}
