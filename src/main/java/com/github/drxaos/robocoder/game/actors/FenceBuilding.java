package com.github.drxaos.robocoder.game.actors;


import com.github.drxaos.robocoder.geom.KPoint;

public class FenceBuilding extends Building {

    public FenceBuilding(KPoint position, float radius) {
        super(position, radius);
        setShield(0.9f);
    }

    public FenceBuilding(KPoint position, float width, float height, double angle) {
        super(position, width, height, angle);
    }

    @Override
    public String[] getTags() {
        return new String[]{"building", "static", "breakable", "fencebuilding"};
    }

    @Override
    public void damage(float points) {
        super.damage(points);
        if (getArmour() == 0) {
            game.removeActor(this);
        }
    }
}
