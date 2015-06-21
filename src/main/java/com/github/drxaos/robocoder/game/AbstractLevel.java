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
        game = new Game(getWorld(), getDebugDraw());
        initLevel(game);
        game.start();
    }

    abstract public void initLevel(Game game);

    @Override
    public synchronized void step() {
        game.beforeStep();
        super.step();
        game.afterStep();
    }

    @Override
    protected void draw() {
        drawer.drawTraces(game.traces, true);
        super.draw();
        drawer.drawTraces(game.traces, false);
    }

    protected static long skipMax = 5;
    protected long skipped = 0;
    protected long lastDraw = 0;

    public static void setSkipFramesMax(long skipMax) {
        AbstractLevel.skipMax = skipMax;
    }

    @Override
    public void update() {
        if (drawer != null) {
            long nano = System.nanoTime();
            if (nano - lastDraw >= 1000000000 / 25 || skipped >= skipMax) {
                drawer.setSkip(false);
                lastDraw = nano;
                skipped = 0;
            } else {
                drawer.setSkip(true);
                skipped++;
            }
        }
        super.update();
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
