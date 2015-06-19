package com.github.drxaos.robocoder.game.actors;

import com.github.drxaos.robocoder.game.equipment.MemoryEquipment;
import com.github.drxaos.robocoder.game.equipment.RadioStationEquipment;
import com.github.drxaos.robocoder.geom.KPoint;

public class InfoStation extends Station {

    public InfoStation(String uid, KPoint position) {
        super(uid, position, 2.5f);
    }

    @Override
    public void addDefaultEquipment() {
        addEquipment(new RadioStationEquipment());
        addEquipment(new MemoryEquipment());
    }
}
