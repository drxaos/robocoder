package com.github.drxaos.robocoder.game.equipment;

import com.github.drxaos.robocoder.game.Game;
import com.github.drxaos.robocoder.game.actors.ControlledActor;
import com.sun.deploy.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RadioEquipment implements Equipment {

    protected long keepTimeout = 5;

    Map<String, Long> messages = new HashMap<String, Long>();

    public void communicate(ControlledActor robot, Game game) {
        String req = robot.getBus().getRequest();
        if (req == null || !req.startsWith("radio::")) {
            return;
        }
        if (req.equals("radio::receive")) {
            robot.getBus().writeResponse(StringUtils.join(messages.keySet(), "::"));
        }
    }

    long time = 0;

    public void applyPhysics(ControlledActor robot, Game game) {
        time = game.getTime();
        Iterator<Map.Entry<String, Long>> i = messages.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, Long> e = i.next();
            if (time - e.getValue() > keepTimeout) {
                i.remove();
            }
        }
    }

    public void addMessage(String msg) {
        messages.put(msg, time);
    }
}
