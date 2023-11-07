package org.example.module8.example1;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Integer.*;

public class RandomMatrixGenerator {

    private static final int numOfMatrices = 100;

    private static final Random RANDOM = new Random();
    public static void main(String[] args) {
        for(int iter=0; iter < numOfMatrices; iter++) {
            int size = RANDOM.nextInt(5, 100);
            int[][] randomMatrix = generate(size);
        }
    }

    public static int[][] generate(int size) {
        int[][] matrix = new int[size][size];
        for(int i=0; i < size; i++) {
            for(int j=0; j < size; j++) {
                matrix[i][j] = RANDOM.nextInt(
                        MIN_VALUE / 2, MAX_VALUE / 2);
            }
        }
        return matrix;
    }
}
