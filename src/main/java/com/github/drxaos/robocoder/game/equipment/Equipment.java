package com.github.drxaos.robocoder.game.equipment;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.Robot;

public interface Equipment {

    public void communicate(Robot robot, Game game);

    public void applyPhysics(Robot robot, Game game);
}
