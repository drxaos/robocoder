package com.github.drxaos.robocoder.game.actors;


import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.box2d.AbstractModel;
import com.github.drxaos.robocoder.game.box2d.StaticModel;
import com.github.drxaos.robocoder.geom.KPoint;
import com.github.drxaos.robocoder.geom.KPolygon;
import org.jbox2d.common.Color3f;

import java.util.ArrayList;

public class Laser extends Actor {

    protected final Color3f color = new Color3f(0.99f, 0.88f, 0.88f);
    protected final Color3f rayColor = new Color3f(1f, 0.1f, 0.1f);
    protected final float distance = 100;

    protected StaticModel model;
    protected ArrayList<KPoint> vertices = new ArrayList<KPoint>();

    public Laser(KPoint position, double angle) {
        this.vertices.add(new KPoint(0, -0.5f));
        this.vertices.add(new KPoint(0, 0.5f));
        this.vertices.add(new KPoint(1, 0));
        model = new StaticModel(new KPolygon(this.vertices), position, angle);
    }

    @Override
    public AbstractModel getModel() {
        return model;
    }

    @Override
    public String[] getTags() {
        return new String[]{"laser", "static", "unbreakable"};
    }

    @Override
    public Color3f getColor() {
        return color;
    }

    @Override
    public void afterStep() {
        super.afterStep();
        KPoint peak = model.getPosition().translateCopyToAngle(1, model.getAngle());
        Game.ScanResult scan = game.resolveDirection(model.getAngle(), distance, this, peak, false);
        scan.actor.damage(0.3f);

        game.addTrace(new Game.Trace(new KPoint[]{peak, scan.point}, rayColor, 2).resolvableAs(this));
        if (Math.random() > 0.8) {
            double sparkAngle = model.getAngle() + Math.PI + (Math.random() - 0.5) * Math.PI * 6 / 7;
            game.addTrace(
                    new Game.Trace(new KPoint[]{scan.point.translateCopyToAngle(Math.random() * 0.5 + 0.1, sparkAngle), scan.point.translateCopyToAngle(Math.random() * 0.5 + 0.5, sparkAngle)}, rayColor, 3).width(0.1f));
        }
    }
}
