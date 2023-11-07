package org.example.module8.example1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class MatrixPairsWriter {
    private static final String OUTPUT_FILE=
            "/home/alessandrofazio/Projects/JavaMultiThreadingCourse/src/main/resources/module8.example1/input-matrices.txt";
    private static final ReentrantLock lock = new ReentrantLock();
    private static final int numberOfPairs = 10000;

    public static void main(String[] args) throws IOException, InterruptedException {

        try(FileWriter writer = new FileWriter(OUTPUT_FILE)) {
            Thread producer1 = new Thread(() -> {
                try {
                    for (int iter = 0; iter < numberOfPairs; iter++)
                        writeMatrix(writer, RandomMatrixGenerator.generate(10));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            Thread producer2 = new Thread(() -> {
                for (int iter = 0; iter < numberOfPairs; iter++) {
                    try {
                        writeMatrix(writer, RandomMatrixGenerator.generate(10));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            producer1.start();
            producer2.start();
            producer1.join();
            producer1.join();

            System.out.println("Matrices has been written");
        }
    }

    public static void writeMatrix(FileWriter writer, int[][] matrix) throws IOException {
        lock.lock();
        try {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                    writer.append(String.valueOf(matrix[i][j]));
                    if (j < matrix[i].length - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }
            writer.append("\n");
        } finally {
            lock.unlock();
        }
    }
}
