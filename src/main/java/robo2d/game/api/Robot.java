package robo2d.game.api;

public interface Robot {

    <T extends Equipment> T getEquipment(Class<T> type);

    Double getEnergy();

    Long getTime();

    void waitForStep();

    void debug(Object dbg);
}
