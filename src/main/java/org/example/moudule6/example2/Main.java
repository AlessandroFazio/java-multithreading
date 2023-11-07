package org.example.moudule6.example2;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Metrics metrics = new Metrics();

        Thread businessThread1 = new BusinessLogic(metrics);
        Thread businessThread2 = new BusinessLogic(metrics);
        Thread printerThread = new MetricsPrinter(metrics);

        businessThread1.start();
        businessThread2.start();
        printerThread.start();

    }

    public static class MetricsPrinter extends Thread {
        private Metrics metrics;
        public MetricsPrinter(Metrics metrics) {
            this.metrics = metrics;
        }

        public void printMetrics() {
            try {
                Thread.sleep(100);
            } catch (Exception ignored) {
            }
            double currentAverage = metrics.getAverage();
            System.out.println("Current average is " + currentAverage);
        }

        @Override
        public void run() {
            while(true) {
                printMetrics();
            }
        }
    }

    public static class BusinessLogic extends Thread {
        private Metrics metrics;
        private Random random = new Random();

        public BusinessLogic(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while(true) {
                long start = System.currentTimeMillis();
                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                long end = System.currentTimeMillis();
                metrics.addSample(end - start);
            }
        }
    }
    public static class Metrics {
        private long count = 0;
        private volatile double average = 0.0;

        public synchronized void addSample(long sample) {
            double currentSum = average * count;
            average = (currentSum + sample) / ++count;
        }

        public double getAverage() {
            return average;
        }
    }
}
