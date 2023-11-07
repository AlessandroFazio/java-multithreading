package org.example.module11.example1;

import java.util.ArrayList;
import java.util.List;

public class VirtualThreadDemo {
    private static final int NUMBER_OF_VTHREADS = 7;
    public static void main(String[] args) throws InterruptedException {

        List<Thread> vThreads = new ArrayList<>();
        for(int i=0; i<NUMBER_OF_VTHREADS; i++) {
            Thread vThread = Thread.ofVirtual().unstarted(new BlockingTask());
            vThreads.add(vThread);
        }

        for(Thread vThread: vThreads) {
            vThread.start();
        }
        for(Thread vThread: vThreads) {
            vThread.join();
        }
    }

    private static class BlockingTask implements Runnable {
        @Override
        public void run() {
            System.out.println("Inside thread: " + Thread.currentThread() + " before blocking call");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Inside thread: " + Thread.currentThread() + " after blocking call");
        }
    }
}
