package robo2d.testbed;

import org.jbox2d.testbed.framework.TestbedController;
import org.jbox2d.testbed.framework.TestbedModel;
import org.jbox2d.testbed.framework.TestbedPanel;
import org.jbox2d.testbed.framework.j2d.TestbedSidePanel;

import javax.swing.*;
import java.awt.*;

public class RoboFrame extends JFrame {

    private TestbedSidePanel side;
    private TestbedModel model;
    private TestbedController controller;

    public RoboFrame(final TestbedModel argModel, final TestbedPanel argPanel, TestbedController.UpdateBehavior behavior) {
        super("JBox2D Testbed");
        setLayout(new BorderLayout());

        model = argModel;
        model.setDebugDraw(argPanel.getDebugDraw());
        controller = new TestbedController(model, argPanel, behavior);
        side = new TestbedSidePanel(model, controller);

        add((Component) argPanel, "Center");
        add(new JScrollPane(side), "East");
        pack();

        controller.playTest(0);
        controller.start();
    }
}
