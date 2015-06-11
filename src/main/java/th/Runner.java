package th;

import java.util.Timer;
import java.util.TimerTask;

public class Runner {

    public static void main(String[] args) {
        System.out.println("Start");

        final Th[] workers = {
                new Th("Heavy1", 1000000),
                new Th("Heavy2", 1000000),
                new Th("Heavy3", 1000000),
                new Th("Heavy4", 1000000),
                new Th("Heavy5", 1000000),
                new Th("Heavy6", 1000000),
                new Th("Heavy7", 1000000),
                new Th("Heavy8", 1000000),
                new Th("Heavy9", 1000000),
                new Th("Heavy0", 1000000),
                new Th("Light00", 100000),
                new Th("Light01", 100000),
                new Th("Light02", 100000),
                new Th("Light03", 100000),
                new Th("Light04", 100000),
                new Th("Light05", 100000),
                new Th("Light06", 100000),
                new Th("Light07", 100000),
                new Th("Light08", 100000),
                new Th("Light09", 100000),
                new Th("Light10", 100000),
                new Th("Light11", 100000),
                new Th("Light12", 100000),
                new Th("Light13", 100000),
                new Th("Light14", 100000),
                new Th("Light15", 100000),
                new Th("Light16", 100000),
                new Th("Light17", 100000),
                new Th("Light18", 100000),
                new Th("Light19", 100000)
        };

        for (Th th : workers) {
            th.start();
            th.suspend();
        }

        new Timer().schedule(new TimerTask() {
            int current = 0;

            @Override
            public void run() {
                workers[current].suspend();
                current = (current + 1) % workers.length;
                workers[current].resume();
            }
        }, 0, 1000 / workers.length);

        System.out.println("End");
    }

}
