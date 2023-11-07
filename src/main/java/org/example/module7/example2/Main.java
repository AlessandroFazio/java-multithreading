package org.example.module7.example2;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    private static final int MAX_PRICE = 1000;

    public static void main(String[] args) throws InterruptedException {
        InventoryDatabase inventory = new InventoryDatabase();

        Random random = new Random();
        for(int i=0; i < 100000; i++) {
            inventory.addItem(random.nextInt(MAX_PRICE));
        }

        Thread writer = new Thread(() -> {
            while(true) {
                inventory.addItem(random.nextInt(MAX_PRICE));
                inventory.removeItem(random.nextInt(MAX_PRICE));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
            }
        });

        writer.setDaemon(true);
        writer.start();

        int numberOfReadingThreads = 7;
        List<Thread> readers = new ArrayList<>();
        for(int i=0; i < numberOfReadingThreads; i++) {
            Thread reader = new Thread(() -> {
                for(int iter=0; iter < 100000; iter++) {
                    int firstBound = random.nextInt(MAX_PRICE);
                    int secondBound = random.nextInt(MAX_PRICE);
                    int numberOfItemsInPriceRange = firstBound < secondBound ?
                            inventory.getNumberOfItemsInPriceRange(firstBound, secondBound) :
                            inventory.getNumberOfItemsInPriceRange(secondBound, firstBound);
                }
            });
            reader.setDaemon(true);
            readers.add(reader);
        }

        long startReadingTime = System.currentTimeMillis();

        for(Thread reader: readers) {
            reader.start();
        }

        for (Thread reader: readers) {
            reader.join();
        }

        long endReadingTime = System.currentTimeMillis();

        System.out.printf("Reading took %d ms%n", endReadingTime - startReadingTime);
    }

    public static class InventoryDatabase {
        private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
        private ReentrantLock lock = new ReentrantLock();
        private ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        private Lock readLock = reentrantReadWriteLock.readLock();
        private Lock writeLock = reentrantReadWriteLock.writeLock();

        public int getNumberOfItemsInPriceRange(int lowerBound, int upperBound) {
            readLock.lock();
            try {
                Integer fromKey = priceToCountMap.ceilingKey(lowerBound);

                Integer toKey = priceToCountMap.floorKey(upperBound);

                if (fromKey == null || toKey == null) {
                    return 0;
                }
                NavigableMap<Integer, Integer> rangeOfPrices =
                        priceToCountMap.subMap(fromKey, true, toKey, true);

                int sum = 0;
                for (int numberOfItemsForPrice : rangeOfPrices.values()) {
                    sum += numberOfItemsForPrice;
                }
                return sum;
            } finally {
                readLock.unlock();
            }
        }

        public void addItem(int price) {
            writeLock.lock();
            try {
                Integer numberOfItemsForPrices = priceToCountMap.get(price);
                if (numberOfItemsForPrices == null) {
                    priceToCountMap.put(price, 1);
                } else {
                    priceToCountMap.put(price, numberOfItemsForPrices + 1);
                }
            } finally {
                writeLock.unlock();
            }
        }

        public void removeItem(int price) {
            writeLock.lock();
            try {
                Integer numberOfItemsForPrices = priceToCountMap.get(price);
                if (numberOfItemsForPrices == null || numberOfItemsForPrices == 1) {
                    priceToCountMap.remove(price);
                } else {
                    priceToCountMap.put(price, numberOfItemsForPrices - 1);
                }
            } finally {
                writeLock.unlock();
            }
        }
    }
}
