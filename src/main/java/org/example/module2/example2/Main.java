package org.example.module2.example2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static final int MAX_PASSWORD = 9999;

    public static void main(String[] args) {
        Random random = new Random();

        Vautl vautl = new Vautl(random.nextInt(MAX_PASSWORD));

        List<Thread> threads = new ArrayList<>();
        threads.add(new AscendingHackerThread(vautl));
        threads.add(new DiscendingHackerThread(vautl));
        threads.add(new PoliceThread());

        for(Thread thread: threads) {
            thread.start();
        }
    }

    private static class Vautl {
        private int password;
        public Vautl(int password) {
            this.password = password;
        }

        public boolean isCorrectPassword(int guess) throws InterruptedException {
            Thread.sleep(5);
            return this.password == guess;
        }
    }

    private static abstract class HackerThread extends Thread {
        protected Vautl vautl;

        public HackerThread(Vautl vautl) {
            this.vautl = vautl;
            this.setName(this.getClass().getSimpleName());
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void start() {
            System.out.println("Starting thread: " + this.getName());
            super.start();
        }
    }

    private static class AscendingHackerThread extends HackerThread{
        public AscendingHackerThread(Vautl vautl) {
            super(vautl);
        }

        @Override
        public void run() {
            for(int guess = 0; guess < MAX_PASSWORD; guess++) {
                try {
                    if(vautl.isCorrectPassword(guess)) {
                        System.out.println(this.getName() + " guessed the password " + guess);
                        System.exit(0);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static class DiscendingHackerThread extends HackerThread {
        public DiscendingHackerThread(Vautl vautl) {
            super(vautl);
        }

        @Override
        public void run() {
            for(int guess = MAX_PASSWORD; guess > 0; guess--) {
                try {
                    if(vautl.isCorrectPassword(guess)) {
                        System.out.println(this.getName() + " guessed the password " + guess);
                        System.exit(0);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static class PoliceThread extends Thread {
        @Override
        public void run() {
            for(int i=10; i > 0; i--) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(i);
            }
            System.out.println("Game over for you hackers");
            System.exit(0);
        }
    }
}
