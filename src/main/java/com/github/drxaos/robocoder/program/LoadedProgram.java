package com.github.drxaos.robocoder.program;

import com.github.drxaos.robocoder.game.actors.ControlledActor;
import com.github.drxaos.robocoder.geom.KPoint;

import java.net.URL;
import java.net.URLClassLoader;

public class LoadedProgram {
    ProgramLoader loader;
    Class cls;
    AbstractProgram program;
    Thread thread;

    public LoadedProgram(Class<? extends AbstractProgram> programCls) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        this.loader = new ProgramLoader(((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs(), new Class[]{
                AbstractProgram.class,
                Bus.class,
        });
        this.cls = loader.loadClass(programCls.getName());
        this.program = (AbstractProgram) cls.newInstance();
    }

    public ProgramLoader getLoader() {
        return loader;
    }

    public Class getCls() {
        return cls;
    }

    public AbstractProgram getProgram() {
        return program;
    }

    public Thread getThread() {
        return thread;
    }

    public void setBus(Bus bus) {
        program.setBus(bus);
    }

    public void start(final ControlledActor actor) {
        this.thread = new Thread(new Runnable() {
            public void run() {
                program.run();
                actor.log("Program terminated");
            }
        }, "UserProgram: " + actor.getUid());
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                actor.log(e);
            }
        });
        thread.start();
    }

    public void stop() {
        if (thread != null && thread.isAlive()) {
            thread.stop();
        }
    }
}

class ProgramLoader extends URLClassLoader {

    private final Class[] apiClasses;

    public ProgramLoader(URL[] urls, Class[] apiClasses) {
        super(urls, null);
        this.apiClasses = apiClasses;
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        for (Class apiClass : apiClasses) {
            if (apiClass.getName().equals(name)) {
                return apiClass;
            }
        }
        return super.loadClass(name);
    }

}