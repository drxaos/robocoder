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
package com.github.drxaos.robocoder.ui.j2d;

import com.github.drxaos.robocoder.ui.TestbedController;
import com.github.drxaos.robocoder.ui.TestbedModel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestbedSidePanel extends JPanel {

    final TestbedModel model;
    final TestbedController controller;

    private JButton quitButton = new JButton("Quit");


    public TestbedSidePanel(TestbedModel argModel, TestbedController argController) {
        model = argModel;
        controller = argController;
        initComponents();
        addListeners();
    }

    public void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel top = new JPanel();
        top.setLayout(new GridLayout(0, 1));
        top.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.LOWERED),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        top.add(new JLabel("Level: "));
        add(top, "North");

        JPanel middle = new JPanel();
        middle.setLayout(new GridLayout(0, 1));
        middle.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.LOWERED),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        add(middle, "Center");

        quitButton.setAlignmentX(CENTER_ALIGNMENT);

        Box buttonGroups = Box.createHorizontalBox();

        JPanel buttons3 = new JPanel();
        buttons3.setLayout(new GridLayout(0, 1));
        buttons3.add(quitButton);

        buttonGroups.add(buttons3);

        add(buttonGroups, "South");
    }

    public void addListeners() {
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}
