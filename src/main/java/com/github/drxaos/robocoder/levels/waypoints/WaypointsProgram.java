package com.github.drxaos.robocoder.levels.waypoints;

import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.BasicMovement;
import com.github.drxaos.robocoder.program.api.RadarDriver;
import com.github.drxaos.robocoder.program.api.DebugDriver;

import java.util.ArrayList;


public class WaypointsProgram extends AbstractProgram {

    public void run() {
        while (true) {

            BasicMovement basicMovement = new BasicMovement(bus);
            DebugDriver robotDriver = new DebugDriver(bus);
            RadarDriver radarDriver = new RadarDriver(bus);

            ArrayList<KPoint> way = new ArrayList<KPoint>();
            way.add(new KPoint(0, -10));
            way.add(new KPoint(12, -5));
            way.add(new KPoint(12, 0));
            way.add(new KPoint(0, 0));
            way.add(new KPoint(-12, 0));
            way.add(new KPoint(-12, 5));
            way.add(new KPoint(0, 10));
            way.add(new KPoint(15, 5));
            way.add(new KPoint(15, -5));
            way.add(new KPoint(0, -10));

            for (KPoint wayPoint : way) {
                while (!basicMovement.move(wayPoint, 1, 10000)) {
                    basicMovement.stop();
                    KPoint failPoint = radarDriver.getPosition();
                    robotDriver.point(wayPoint);
                    robotDriver.say("Please drag me to that point!");
                    while (true) {
                        KPoint newPoint = radarDriver.getPosition();
                        if (newPoint.distance(failPoint) > 0.2) {
                            break;
                        }
                    }
                }
                robotDriver.say(null);

                if (radarDriver.getPosition().distance(new KPoint(0, 0)) < 0.3) {
                    basicMovement.stop();
                }
            }
            basicMovement.stop();
        }
    }
}
