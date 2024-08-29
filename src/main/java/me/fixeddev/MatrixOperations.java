package me.fixeddev;

public class MatrixOperations {

    public static void printNormalMatrix(double[][] matrix) {
        for (double[] doubles : matrix) {
            for (double aDouble : doubles) {
                System.out.printf("%6.3f", aDouble);
            }
            System.out.println();
        }
    }
    public static void printMatrix(double[][] matrix, int dim) {
        for (double[] doubles : matrix) {
            for (int j = 0; j < doubles.length; j++) {
                System.out.printf("%6.2f", doubles[j]);
                if (j == dim - 1) {
                    System.out.print(" |");
                }
            }
            System.out.println();
        }
    }

    public static void crossMult(double[][] matrix, int x, int y, int i, int j, double pivotVal) {
        matrix[j][i] = (matrix[y][x] * matrix[j][i] - matrix[j][x] * matrix[y][i]) / pivotVal;
    }

    public static void swapColumns(double[][] matrix, double[] constants, int y1, int y2) {
        double[] temp = matrix[y1];

        matrix[y1] = matrix[y2];
        matrix[y2] = temp;

        double tempInt = constants[y1];
        constants[y1] = constants[y2];
        constants[y2] = tempInt;
    }

    public static boolean isDetZero(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][i] == 0) {
                return true;
            }
        }

        return false;
    }

    public static double[][] inverseMatrix(double[][] matrix, double determinant, int dim) {
        double[][] inverse = new double[dim][dim];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = dim; j < matrix[i].length; j++) {
                inverse[i][j - dim] = matrix[i][j] / determinant;
            }
        }

        return inverse;
    }

    public static double[][] convert(double[] matrix) {
        double[][] converted = new double[matrix.length][1];
        for (int i = 0; i < matrix.length; i++) {
            converted[i][0] = matrix[i];
        }

        return converted;
    }

    public static double[][] multiplyMatrices(double[][] matrix1, double[][] matrix2) {
        if (matrix1[0].length != matrix2.length) {
            return null;
        }

        double[][] result = new double[matrix1.length][matrix2[0].length];

        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix2[0].length; j++) {
                for (int k = 0; k < matrix1[0].length; k++) {
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }

        return result;
    }

}
