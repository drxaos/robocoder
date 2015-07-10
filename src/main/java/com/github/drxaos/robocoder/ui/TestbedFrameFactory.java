/**
 * Copyright (c) 2013, Daniel Murphy
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * Created at 4:23:48 PM Jul 17, 2010
 * <p>
 * Created at 4:23:48 PM Jul 17, 2010
 * <p>
 * Created at 4:23:48 PM Jul 17, 2010
 * <p>
 * Created at 4:23:48 PM Jul 17, 2010
 */
/**
 * Created at 4:23:48 PM Jul 17, 2010
 */
package com.github.drxaos.robocoder.ui;

import javax.swing.*;
import java.awt.*;

public class TestbedFrameFactory {

    public static void create(final TestbedModel argModel, final TestbedPanel argPanel, TestbedController.UpdateBehavior behavior) {
        if (GraphicsEnvironment.isHeadless()) {
            argModel.setDebugDraw(argPanel.getDebugDraw());
            TestbedController controller = new TestbedController(argModel, argPanel, behavior);
            controller.playTest(0);
            controller.start();
        } else {
            JFrame frame = new JFrame("Robocoder Testbed");
            frame.setLayout(new BorderLayout());

            argModel.setDebugDraw(argPanel.getDebugDraw());
            TestbedController controller = new TestbedController(argModel, argPanel, behavior);

            frame.add((Component) argPanel, "Center");
            frame.pack();

            controller.playTest(0);
            controller.start();

            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }
}
