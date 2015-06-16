package com.github.drxaos.robocoder.levels.radar;

import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.ArmDriver;
import com.github.drxaos.robocoder.program.api.BasicMovement;
import com.github.drxaos.robocoder.program.api.ChassisDriver;
import com.github.drxaos.robocoder.program.api.RadarDriver;
import straightedge.geom.KPoint;

import java.util.ArrayList;
import java.util.List;


public class RadarProgram extends AbstractProgram {

    public void run() {
        BasicMovement basicMovement = new BasicMovement(bus);
        RadarDriver radarDriver = new RadarDriver(bus);
        ChassisDriver chassisDriver = new ChassisDriver(bus);
        ArmDriver armDriver = new ArmDriver(bus);

        while (true) {
            List<Double> angles = new ArrayList<Double>();
            for (int i = 0; i < 360; i += 2) {
                double angle = Math.PI * i / 180;
                RadarDriver.Result scan = radarDriver.scan(angle, true);
                if (scan.properties.contains("box")) {
                    angles.add(angle);
                }
            }
            double sx = 0, sy = 0;
            for (Double angle : angles) {
                sx += Math.cos(angle);
                sy += Math.sin(angle);
            }
            sx = sx / angles.size();
            sy = sy / angles.size();
            double center = basicMovement.angle(new KPoint(0, 0), new KPoint(sx, sy));
            basicMovement.rotate(center, false, 10000);
        }
    }
}
