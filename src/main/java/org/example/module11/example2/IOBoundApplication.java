package org.example.module11.example2;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IOBoundApplication {
    private static final int NUMBER_OF_TASK = 100_000;
    private static final CountDownLatch latch = new CountDownLatch(NUMBER_OF_TASK);

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press enter to start");
        scanner.nextLine();
        System.out.printf("Running %d tasks\n", NUMBER_OF_TASK);

        long start = System.currentTimeMillis();

        performTask();

        latch.await();
        System.out.printf("Tasks took %dms to complete\n", System.currentTimeMillis() - start);
    }

    private static void performTask() {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        try {
            for (int i=0; i < NUMBER_OF_TASK; i++) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0; i < 100; i++) {
                            blockingIoOperation();
                        }
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    private static void blockingIoOperation() {
        System.out.println("Executing a block task from thread " + Thread.currentThread());
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }
    }
}
