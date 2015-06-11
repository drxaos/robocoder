package robo2d.game.api.map;

import java.util.List;

public interface Map {
    Double getWidth();

    Double getHeight();

    List<Obj> getObjects();
}
