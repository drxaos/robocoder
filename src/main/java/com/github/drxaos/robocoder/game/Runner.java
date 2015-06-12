package com.github.drxaos.robocoder.game;

import com.github.drxaos.robocoder.program.AbstractProgram;

public class Runner {
    public static void run(Class<? extends AbstractLevel> level, Class<? extends AbstractProgram> program) {
        try {
            AbstractLevel lvl = level.newInstance();
            AbstractProgram prg = program.newInstance();
            lvl.setUserProgram(prg);
            lvl.start();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
