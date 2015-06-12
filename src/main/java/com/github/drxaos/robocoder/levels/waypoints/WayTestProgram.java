package com.github.drxaos.robocoder.levels.waypoints;

import com.github.drxaos.robocoder.game.Runner;
import com.github.drxaos.robocoder.program.AbstractProgram;
import straightedge.geom.KPoint;

import java.util.ArrayList;


public class WayTestProgram extends AbstractProgram {
    public static void main(String[] args) {
        Runner.run(WayTest.class, WayTestProgram.class);
    }

    public void run() {

        if (chassis == null || radar == null) {
            debug.debug("fail");
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
            while (!wheel.moveSmooth(wayPoint, 4000)) {
                wheel.stop();
                KPoint failPoint = radar.getPosition();
                debug.debugPoint(wayPoint);
                debug.debug("Please drag me to that point!");
                while (true) {
                    KPoint newPoint = radar.getPosition();
                    if (wheel.distance(newPoint, failPoint) > 0.2) {
                        break;
                    }
                }
            }
            debug.debug(null);

            if (wheel.distance(radar.getPosition(), new KPoint(0, 0)) < 0.3) {
                wheel.stop();
            }
        }
    }
}
