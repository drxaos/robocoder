package robo2d.game.api;

public interface Chassis extends Equipment {

    void setLeftAcceleration(Double percent);

    void setRightAcceleration(Double percent);

    Double getLeftSpeed();

    Double getRightSpeed();
}
