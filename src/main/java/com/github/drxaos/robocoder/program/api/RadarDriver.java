package com.github.drxaos.robocoder.program.api;

import com.github.drxaos.robocoder.program.Bus;
import straightedge.geom.KPoint;

public class RadarDriver {
    Bus bus;

    public RadarDriver(Bus bus) {
        this.bus = bus;
    }

    public Double getAngle() {
        bus.writeRequest("radar::angle");
        String resp = bus.readResponse();
        return Double.parseDouble(resp);
    }

    public KPoint getPosition() {
        bus.writeRequest("radar::position");
        String pos = bus.readResponse();
        String[] split = pos.split(":");
        double x = Double.parseDouble(split[0]);
        double y = Double.parseDouble(split[1]);
        return new KPoint(x, y);
    }


    public long getTime() {
        bus.writeRequest("radar::time");
        String resp = bus.readResponse();
        return Long.parseLong(resp);
    }
}
