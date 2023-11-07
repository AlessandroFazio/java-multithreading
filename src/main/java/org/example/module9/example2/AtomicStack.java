package org.example.module9.example2;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class AtomicStack {
    public static void main(String[] args) throws InterruptedException {
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        Random random = new Random();

        for(int i=0; i < 10000; i++) {
            stack.push(random.nextInt());
        }

        List<Thread> threads = new ArrayList<>();

        int pushingThreads = 2;
        int poppingThreads = 2;

        for(int i=0; i < poppingThreads + pushingThreads; i++) {
            if(i < poppingThreads) {
                Thread thread = new Thread(() -> {
                    while(true) {
                        stack.push(random.nextInt());
                    }});
                thread.setDaemon(true);
                threads.add(thread);
                } else {
                Thread thread = new Thread(() -> {
                    while(true) {
                        stack.pop();
                    }
                });
                thread.setDaemon(true);
                threads.add(thread);
            }
        }

        for(Thread thread: threads) {
            thread.start();
        }

        Thread.sleep(10000);

        System.out.println(String.format("%,d operations were performed in 10 seconds ", stack.getCounter()));
    }

    public static class LockFreeStack<T> {
        private AtomicReference<StackNode<T>> head = new AtomicReference<>();
        private AtomicInteger counter = new AtomicInteger(0);
        public void push(T value) {
            StackNode<T> newNode = new StackNode<>(value);
            while(true) {
                StackNode<T> currentHead = head.get();
                newNode.next = currentHead;
                if(head.compareAndSet(currentHead, newNode)) {
                    break;
                } else {
                    LockSupport.parkNanos(1);
                }
            }
            counter.incrementAndGet();
        }

        public T pop() {
            StackNode<T> currentHead = head.get();
            while(currentHead != null) {
                if (head.compareAndSet(currentHead, currentHead.next)) {
                    break;
                } else {
                    LockSupport.parkNanos(1);
                    currentHead = head.get();
                }
            }
            counter.incrementAndGet();
            return currentHead != null ? currentHead.value : null;
        }

        public int getCounter() {
            return counter.get();
        }
    }
    public static class StandardStack<T> {
        private StackNode<T> head;
        private int counter = 0;

        public synchronized void push(T value) {
            StackNode<T> newNode = new StackNode<>(value);
            newNode.next = head;
            head = newNode;
            counter++;
        }

        public synchronized T pop() {
            if(head == null) {
                counter++;
                return null;
            }

            T value = head.value;
            head = head.next;
            counter++;
            return value;
        }

        public int getCounter() {
            return counter;
        }
    }
    private static class StackNode<T> {
        public T value;
        public StackNode<T> next;

        public StackNode( T value) {
            this.value = value;
            this.next = next;
        }
    }
}
