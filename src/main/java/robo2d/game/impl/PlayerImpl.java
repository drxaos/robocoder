package robo2d.game.impl;

import robo2d.game.api.Player;

public class PlayerImpl implements Player {
    String name;

    public PlayerImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
