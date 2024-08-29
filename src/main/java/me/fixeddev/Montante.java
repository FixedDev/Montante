package me.fixeddev;

import static me.fixeddev.MatrixOperations.crossMult;
import static me.fixeddev.MatrixOperations.swapColumns;

public class Montante {

    public static void solve(double[][] matrix, double[] constants) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return;
        }

        int n = matrix.length;
        int m = matrix[0].length;

        if (m != 2 * n) {
            return;
        }

        double pivot = 1;
        for (int i = 0; i < n; i++) {
            pivot = iteration(matrix, n, m, i, i, pivot);

            if (pivot == 0) {
                for (int l = i + 1; l < n; l++) {
                    if (matrix[i][i] ==
                            matrix[l][l]) {
                        continue;
                    }
                    swapColumns(matrix, constants, i, l);
                }
            }
        }
    }

    public static double iteration(double[][] matrix, int n, int m, int x, int y, double lastPivot) {
        backPropagation(matrix, x);

        for (int i = x + 1; i < m; i++) {
            for (int j = y + 1; j < n; j++) {
                crossMult(matrix, x, y, i, j, lastPivot);
            }
        }

        for (int j = y - 1; j >= 0; j--) {
            for (int i = x + 1; i < m; i++) {
                crossMult(matrix, x, y, i, j, lastPivot);
            }
        }


        lastPivot = matrix[y][x];
        cleanColumn(matrix, x, y);

        return lastPivot;
    }

    public static void cleanColumn(double[][] matrix, int x, int y) {
        for (int i = 0; i < matrix.length; i++) {
            if (i == y) {
                continue;
            }

            matrix[i][x] = 0;
        }
    }

    public static void backPropagation(double[][] matrix, int x) {
        for (int i = 0; i < x; i++) {
            matrix[i][i] = matrix[x][x];
        }
    }

}
