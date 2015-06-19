package com.github.drxaos.robocoder.levels.tutorial;

import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.api.*;


public class Example06 extends AbstractProgram {

    public static void main(String[] args) {
        Tutorial06Coop.setSkipFramesMax(8);
        Tutorial06Coop.run(Example06.class);
    }

    static final boolean[] flags = new boolean[]{false, false, false, false};

    public void run() {
        BasicMovement basicMovement = new BasicMovement(bus);
        RadarDriver radarDriver = new RadarDriver(bus);
        ChassisDriver chassisDriver = new ChassisDriver(bus);
        ArmDriver armDriver = new ArmDriver(bus);
        TurretDriver turretDriver = new TurretDriver(bus);

        boolean meFirst = false;
        synchronized (flags) {
            if (!flags[0]) {
                meFirst = flags[0] = true;
            }
        }

        if (meFirst) {

            basicMovement.move(0, 10);

            synchronized (flags) {
                while (!flags[1]) {
                    try {
                        flags.wait(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            basicMovement.move(50, 10);

            basicMovement.move(50, 0);

            synchronized (flags) {
                flags[2] = true;
                flags.notifyAll();
            }

            synchronized (flags) {
                while (!flags[3]) {
                    try {
                        flags.wait(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            basicMovement.move(52, 10);

            basicMovement.move(52, 20);

        } else {

            basicMovement.move(5, 10);

            basicMovement.move(27, 10);

            basicMovement.move(27, 20);

            synchronized (flags) {
                flags[1] = true;
                flags.notifyAll();
            }

            synchronized (flags) {
                while (!flags[2]) {
                    try {
                        flags.wait(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            basicMovement.move(27, 10);

            basicMovement.move(48, 10);

            basicMovement.move(48, 20);

            synchronized (flags) {
                flags[3] = true;
                flags.notifyAll();
            }
        }

    }
}
