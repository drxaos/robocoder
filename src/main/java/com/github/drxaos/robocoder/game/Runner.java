package com.github.drxaos.robocoder.game;

import com.github.drxaos.robocoder.program.AbstractProgram;
import com.github.drxaos.robocoder.program.SandboxingSecurityManager;

public class Runner {
    public static void run(Class<? extends AbstractLevel> level, Class<? extends AbstractProgram> program) {
        System.setSecurityManager(new SandboxingSecurityManager());
        try {
            AbstractLevel lvl = level.newInstance();
            lvl.setUserProgram(program);
            lvl.start();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
