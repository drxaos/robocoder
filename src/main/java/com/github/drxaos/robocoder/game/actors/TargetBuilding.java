package com.github.drxaos.robocoder.game.actors;


import com.github.drxaos.robocoder.geom.KPoint;

public class TargetBuilding extends Building {

    public TargetBuilding(KPoint position, float radius) {
        super(position, radius);
        setShield(0.9f);
    }

    @Override
    public String[] getTags() {
        return new String[]{"building", "static", "breakable", "targetbuilding"};
    }

}
