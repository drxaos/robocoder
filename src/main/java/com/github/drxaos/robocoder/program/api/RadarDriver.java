package com.github.drxaos.robocoder.program.api;

import com.github.drxaos.robocoder.program.Bus;
import straightedge.geom.KPoint;

public class RadarDriver {
    Bus bus;

    public RadarDriver(Bus bus) {
        this.bus = bus;
    }

    public Double getAngle() {
        String resp = bus.request("radar::angle");
        return Double.parseDouble(resp);
    }

    public KPoint getPosition() {
        String pos = bus.request("radar::position");
        String[] split = pos.split(":");
        double x = Double.parseDouble(split[0]);
        double y = Double.parseDouble(split[1]);
        return new KPoint(x, y);
    }

    public long getTime() {
        String resp = bus.request("radar::time");
        return Long.parseLong(resp);
    }
}
