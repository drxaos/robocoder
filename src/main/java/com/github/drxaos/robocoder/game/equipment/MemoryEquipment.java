package com.github.drxaos.robocoder.game.equipment;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.ControlledActor;

public class MemoryEquipment implements Equipment {

    public void communicate(ControlledActor robot, Game game) {
        String req = robot.getBus().getRequest();
        if (req == null || !req.startsWith("memory::")) {
            return;
        }
        if (req.startsWith("memory::save::")) {
            String[] split = req.split("::");
            data = split[split.length - 1].replace("::", "");
            robot.getBus().writeResponse("memory::accepted");
        } else if (req.equals("memory::load")) {
            robot.getBus().writeResponse(data);
        }
    }

    protected String data = "";

    public void applyPhysics(ControlledActor self, Game game) {
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
