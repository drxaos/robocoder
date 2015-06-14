package com.github.drxaos.robocoder.game;

import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.ui.*;
import com.github.drxaos.robocoder.ui.j2d.TestPanelJ2D;
import org.jbox2d.common.Vec2;

import javax.swing.*;

public abstract class AbstractLevel extends TestbedTest {
    protected Game game;
    protected Class<? extends AbstractProgram> userProgram;

    @Override
    public void initLevel(boolean deserialized) {
        setTitle(getLevelName());
        getWorld().setGravity(new Vec2());
        game = createGame();
        game.start();
    }

    abstract public Game createGame();


    @Override
    public synchronized void step() {
        game.beforeStep();
        super.step();
        game.afterStep();
    }

    @Override
    public String getLevelName() {
        return this.getClass().getSimpleName();
    }

    public void start() {
        TestbedModel model = new TestbedModel();
        model.addTest(this);
        TestbedPanel panel = new TestPanelJ2D(model);    // create our testbed panel
        JFrame testbed = new TestbedFrame(model, panel, TestbedController.UpdateBehavior.UPDATE_CALLED); // put both into our testbed frame

        testbed.setVisible(true);
        testbed.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void setUserProgram(final Class<? extends AbstractProgram> userProgram) {
        this.userProgram = userProgram;
    }

}
