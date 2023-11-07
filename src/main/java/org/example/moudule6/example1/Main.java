package org.example.moudule6.example1;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        InventoryCounter counter = new InventoryCounter();
        Thread incrementingThread = new IncrementingThread(counter);
        Thread decrementingThread = new DecrementingThread(counter);
        incrementingThread.start();
        decrementingThread.start();
        incrementingThread.join();
        decrementingThread.join();

        System.out.println("Number of items: " + counter.getItems());
    }

    public static class IncrementingThread extends Thread {
         private InventoryCounter inventoryCounter;
         public IncrementingThread(InventoryCounter counter) {
             this.inventoryCounter = counter;
         }

         @Override
         public void run() {
             for(int i=0; i < 10000; i++) {
                 inventoryCounter.increment();
             }
         }
    }

    public static class DecrementingThread extends Thread {
        private InventoryCounter inventoryCounter;

        public DecrementingThread(InventoryCounter counter) {
            this.inventoryCounter = counter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.decrement();
            }
        }
    }

    private static class InventoryCounter {
        private int items = 0;
        Object lock = new Object();

        public void increment() {
            synchronized (this.lock) {
                items++;
            }
        }

        public void decrement() {
            synchronized (this.lock) {
                items--;
            }
        }

        public int getItems() {
            synchronized (this.lock) {
                return items;
            }
        }
    }
}
