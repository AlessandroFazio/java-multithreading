package org.example.module8.example1;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

public class MultiThreadedMatrixMultiplier {
    private static final String INPUT_FILE =
            "/home/alessandrofazio/Projects/JavaMultiThreadingCourse/src/main/resources/module8.example1/input-matrices.txt";
    private static final String OUTPUT_FILE =
            "/home/alessandrofazio/Projects/JavaMultiThreadingCourse/src/main/resources/module8.example1/output-matrices.txt";
    private static final int N = 10;

    public static void main(String[] args) throws IOException {
        ThreadSafeQueue queue = new ThreadSafeQueue();
        File inputFile = new File(INPUT_FILE);
        File outputFile = new File(OUTPUT_FILE);

        MatricesReaderProducer reader = new MatricesReaderProducer(new Scanner(inputFile), queue);
        MatrixMultiplierConsumer consumer = new MatrixMultiplierConsumer(queue, new FileWriter(outputFile));

        reader.start();
        consumer.start();
    }

    private static class MatrixMultiplierConsumer extends Thread {
        private ThreadSafeQueue queue;
        private FileWriter writer;

        public MatrixMultiplierConsumer(ThreadSafeQueue queue, FileWriter writer) {
            this.writer = writer;
            this.queue = queue;
        }

        private void saveMatrixToFile(FileWriter writer, int[][] matrix) throws IOException {
            for(int i=0; i < matrix.length; i++) {
                StringJoiner joiner = new StringJoiner(", ");
                for(int j=0; j < matrix[i].length; j++) {
                    joiner.add(String.valueOf(matrix[i][j]));
                }
                writer.write(joiner.toString());
                writer.write("\n");
            }
            writer.write("\n");
        }

        @Override
        public void run() {
            while(true) {
                MatricesPairs matricesPairs = queue.remove();
                if(matricesPairs == null) {
                    System.out.println("No more matrices to read from queue, consumer is terminating");
                    break;
                }

                int[][] result = multiply(matricesPairs.matrix1, matricesPairs.matrix2);

                try {
                    saveMatrixToFile(writer, result);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private int[][] multiply(int[][] matrix1, int[][] matrix2) {
            int[][] result = new int[N][N];
            for(int r=0; r < N; r++) {
                for(int c=0; c < N; c++) {
                    for(int k=0; k < N; k++) {
                        result[r][c] += matrix1[r][k] * matrix2[k][c];
                    }
                }
            }
            return result;
        }

    }

    private static class MatricesReaderProducer extends Thread {
        private Scanner scanner;
        private ThreadSafeQueue queue;

        public MatricesReaderProducer(Scanner reader, ThreadSafeQueue queue) {
            this.scanner = reader;
            this.queue = queue;
        }

        private int[][] readMatrix() {
            int[][] matrix = new int[N][N];
            for(int r=0; r < N; r++) {
                if(!scanner.hasNext()) return null;
                String [] line = scanner.nextLine().split(",");
                for(int c = 0; c < N; c++) {
                    matrix[r][c] = Integer.parseInt(line[c]);
                }
            }
            scanner.nextLine();
            return matrix;
        }

        @Override
        public void run() {
            while(true) {
                int[][] matrix1 = readMatrix();
                int[][] matrix2 = readMatrix();
                if(matrix1 == null || matrix2 == null) {
                    queue.terminate();
                    System.out.println("No more matrices to read. Producer Thread is terminating");
                    return;
                }

                MatricesPairs matricesPairs = new MatricesPairs();
                matricesPairs.matrix1 = matrix1;
                matricesPairs.matrix2 = matrix2;

                queue.add(matricesPairs);
            }
        }
    }

    private static class ThreadSafeQueue {
        private Queue<MatricesPairs> queue = new LinkedList<>();
        private boolean isEmpty = true;
        private boolean isTerminate = false;
        public static final int CAPACITY = 10;

        public synchronized void add(MatricesPairs matricesPairs) {
            while(queue.size() == CAPACITY) {
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
            }
            queue.add(matricesPairs);
            isEmpty = false;
            notify();
        }

        public synchronized MatricesPairs remove() {
            MatricesPairs matricesPairs = null;
            while(isEmpty && !isTerminate) {
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
            }
            if(queue.size() == 1) {
                isEmpty = true;
            }

            if(queue.size() == 0 && isTerminate) {
                return null;
            }

            System.out.println("queue size " + queue.size());

            matricesPairs = queue.remove();
            if(queue.size() == CAPACITY -1)
                notifyAll();
            return matricesPairs;
        }

        public synchronized void terminate() {
            isTerminate = true;
            notifyAll();
        }
    }
    private static class MatricesPairs {
        public int[][] matrix1;
        public int[][] matrix2;
    }
}
