package robo2d.game.impl;

import org.jbox2d.common.Vec2;
import straightedge.geom.KPoint;

import java.util.Map;

public interface HasEffects {
    Map<KPoint, Vec2> getEffects();

}
