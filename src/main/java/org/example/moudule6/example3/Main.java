package org.example.moudule6.example3;

public class Main {
    private volatile int x, y;
    public static void main(String[] args) {
        Main app = new Main();
        Thread thread1 = new Thread(() -> {
            for(int i=0; i < Integer.MAX_VALUE; i++) {
                app.increment();
            }
        });

        Thread thread2 = new Thread(() -> {
            for(int i=0; i < Integer.MAX_VALUE; i++) {
                app.checkForDataRace();
            }
        });

        thread1.start();
        thread2.start();
    }
    public void increment() {
        x++;
        y++;
    }

    public void checkForDataRace() {
        if(y > x)
            System.out.println("y > x - DataRace is detected");
    }
}
