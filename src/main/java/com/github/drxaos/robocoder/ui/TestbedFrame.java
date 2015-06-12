/*******************************************************************************
 * Copyright (c) 2013, Daniel Murphy
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright notice,
 * 	  this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright notice,
 * 	  this list of conditions and the following disclaimer in the documentation
 * 	  and/or other materials provided with the distribution.
 *
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
 ******************************************************************************/
/**
 * Created at 4:23:48 PM Jul 17, 2010
 */
package com.github.drxaos.robocoder.ui;

import javax.swing.*;
import java.awt.*;

/**
 * The testbed frame. Contains all stuff. Make sure you call {@link #setVisible(boolean)} and
 * {@link #setDefaultCloseOperation(int)}.
 *
 * @author Daniel Murphy
 */
@SuppressWarnings("serial")
public class TestbedFrame extends JFrame {

    private TestbedModel model;
    private TestbedController controller;

    public TestbedFrame(final TestbedModel argModel, final TestbedPanel argPanel, TestbedController.UpdateBehavior behavior) {
        super("JBox2D Testbed");
        setLayout(new BorderLayout());

        model = argModel;
        model.setDebugDraw(argPanel.getDebugDraw());
        controller = new TestbedController(model, argPanel, behavior);

        add((Component) argPanel, "Center");
        pack();

        controller.playTest(0);
        controller.start();
    }
}
