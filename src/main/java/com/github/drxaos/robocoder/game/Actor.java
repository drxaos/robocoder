package com.github.drxaos.robocoder.game;

import com.github.drxaos.robocoder.game.box2d.Box;

public interface Actor {
    Box getBox();

    void start();

    void beforeStep();

    void afterStep();
}
