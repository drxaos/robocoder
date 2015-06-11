package th;

public class Th extends Thread {

    String name;
    Integer weight;

    int counter = 0;
    double work;

    public Th(String name, Integer weight) {
        this.name = name;
        this.weight = weight;
    }

    @Override
    public void run() {
        while (true) {
            for (int i = 0; i < weight; i++) {
                work += Math.hypot(Math.random(), Math.random());
            }
            System.out.println(name + ": " + counter++);
        }
    }
}
