package com.github.drxaos.robocoder.game.equipment;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.ControlledActor;
import com.github.drxaos.robocoder.game.actors.Robot;

public interface Equipment {

    public void communicate(ControlledActor robot, Game game);

    public void applyPhysics(ControlledActor robot, Game game);
}
