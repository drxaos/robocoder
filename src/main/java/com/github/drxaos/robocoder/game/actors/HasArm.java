package com.github.drxaos.robocoder.game.actors;

public interface HasArm {
    boolean tie(boolean back);

    void untie();

    void push(boolean back, float strength);
}
