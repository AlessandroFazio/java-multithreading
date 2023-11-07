package org.example.module4.example1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        long[] inputNumbers = {0L, 3L, 5L, 13L, 9L, 12L, 10L};
        // We want to calculate the factorial for each number in the List

        List<FactorialThread> threads = new ArrayList<>();
        for(long num: inputNumbers) {
            threads.add(new FactorialThread(num));
        }
        for(Thread thread: threads)
            thread.start();

        // Join thread in order to wait for all FactorialThreads to complete
        // Set a maximum duration we are willing to wait in order to let a worker Thread executing
        // if this time is surpassed, the join method return
        // This will not interrupt per se the blocking thread
        // The join method calls indeed the interrupt method, but we need to
        // setDaemon property to true in order to really stop the blocking Thread programmatically
        for(Thread thread: threads) {
            thread.setDaemon(true);
            thread.join(2000);
        }

        for(int i=0; i < inputNumbers.length; i++) {
            FactorialThread thread = threads.get(i);
            if(thread.isFinished)
                System.out.println("Factorial of: " + inputNumbers[i] + " is " + thread.getResult());
            else
                System.out.println("The calculation for " + inputNumbers[i] + " is still in progress.");
        }
    }

    public static class FactorialThread extends Thread {
        private long inputNumber;
        private long result = 0L;
        private boolean isFinished = false;

        public FactorialThread(long inputNumber) {
            this.inputNumber = inputNumber;
        }

        @Override
        public void run() {
            this.result = factorial(inputNumber);
            this.isFinished = true;
        }

        public long factorial(long num) {
            if(num == 0L)
                return 1L;
            return num * factorial(num - 1L);
        }

        public long getResult() {
            return result;
        }
    }
}
