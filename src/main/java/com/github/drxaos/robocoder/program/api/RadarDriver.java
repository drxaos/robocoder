package com.github.drxaos.robocoder.program.api;

import com.github.drxaos.robocoder.program.Bus;
import straightedge.geom.KPoint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

    public static class Result {
        public Double distance;
        public Set<String> properties = new HashSet<String>();
    }

    public Result scan(Double angle, boolean objectsOnly) {
        String resp = bus.request("radar::" + (objectsOnly ? "scan-objects" : "scan") + "::" + angle);
        String[] split = resp.split("::");
        Result result = new Result();
        result.distance = Double.parseDouble(split[0]);
        result.properties.addAll(Arrays.asList(split).subList(1, split.length));
        return result;
    }
}
