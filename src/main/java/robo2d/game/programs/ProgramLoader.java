package robo2d.game.programs;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

public class ProgramLoader {

    Runnable load(String jarPath) throws ProgramLoadException {
        try {
            ClassLoader loader = URLClassLoader.newInstance(
                    new URL[]{new URL(jarPath)},
                    getClass().getClassLoader()
            );
            Class<?> clazz = Class.forName("robo2d.Program", true, loader);
            Class<? extends Runnable> runClass = clazz.asSubclass(Runnable.class);
            Constructor<? extends Runnable> ctor = runClass.getConstructor();
            Runnable doRun = ctor.newInstance();
            return doRun;
        } catch (Exception e) {
            throw new ProgramLoadException(e);
        }
    }
}
