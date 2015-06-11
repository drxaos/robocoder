package robo2d.testbed;

import org.jbox2d.common.Vec2;
import org.jbox2d.testbed.framework.*;
import org.jbox2d.testbed.framework.j2d.TestPanelJ2D;
import robo2d.game.Game;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RobotTest extends TestbedTest {
    Game game;

    @Override
    public void initTest(boolean deserialized) {
        setTitle(getTestName());
        getWorld().setGravity(new Vec2());
        game = createGame();
        game.start();
    }

    abstract public Game createGame();


    @Override
    public synchronized void step(TestbedSettings settings) {
        game.beforeStep();
        synchronized (game.stepSync()) {
            super.step(settings);
        }
        game.afterStep();

    }

    @Override
    public String getTestName() {
        return this.getClass().getSimpleName();
    }

    public void start() {
        TestbedModel model = new TestbedModel() {
            @Override
            public TestbedSettings getSettings() {
                return new TestbedSettings() {
                    private ArrayList<TestbedSetting> settings;

                    {
                        settings = new ArrayList<TestbedSetting>();
                    }

                    @Override
                    public List<TestbedSetting> getSettings() {
                        return Collections.unmodifiableList(settings);
                    }

                    @Override
                    public TestbedSetting getSetting(String argName) {
                        return super.getSetting(argName);
                    }
                };
            }
        };
        model.addCategory("Tests");
        model.addTest(this);
        TestbedPanel panel = new TestPanelJ2D(model);    // create our testbed panel
        JFrame testbed = new TestbedFrame(model, panel, TestbedController.UpdateBehavior.UPDATE_CALLED); // put both into our testbed frame

        testbed.setVisible(true);
        testbed.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
