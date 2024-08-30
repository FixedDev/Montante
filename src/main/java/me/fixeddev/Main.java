package me.fixeddev;

import jas.core.Compiler;
import jas.core.JASException;
import jas.core.Node;
import jas.core.components.RawValue;
import jas.core.components.Variable;
import jas.core.operations.Binary;

import java.util.Map;
import java.util.Scanner;

import static me.fixeddev.MatrixOperations.*;
import static me.fixeddev.Montante.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");

        int dim;

        do {
            try {
                System.out.println("Ingrese las dimensiones del sistema: ");
                dim = scanner.nextInt();

                break;
            } catch (Exception e) {
                System.out.println("La dimension de el sistema debe ser un numero.");
                scanner.nextLine();
            }
        } while (true);

        double[][] systemMatrix = new double[dim][dim];
        double[] constants = new double[dim];
        int[] lastIdx = {0};
        BiMap<String, Integer> varMappings = new BiMap<>();

        readSystem(dim, scanner, lastIdx, varMappings, systemMatrix, constants);

        double[][] matrix = new double[dim][2 * dim];
        generateAugmentedMatrix(dim, matrix, systemMatrix);
        System.out.println("Matriz aumentada original:");
        printMatrix(matrix, dim);

        System.out.println();
        solve(matrix, constants);
        System.out.println("Matriz aumentada resultante: ");
        printMatrix(matrix, dim);

        if (isDetZero(matrix)) {
            System.out.println("Este sistema no tiene solucion por medio del metodo montante (determinante = 0).");

            return;
        }
        double det = matrix[0][0];

        System.out.println("Determinante: " + det);
        System.out.println("Matriz inversa: ");
        double[][] inverse = inverseMatrix(matrix, det, dim);
        printNormalMatrix(inverse);

        double[][] result = multiplyMatrices(inverse, convert(constants));
        if (result == null) {
            System.out.println("No se pueden multiplicar las matrices de inversa y sus constantes (¿el sistema es invalido?)");

            return;
        }

        System.out.println("Resultados: ");
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                String varMapping = varMappings.getKeyByValue(i);

                System.out.print(varMapping + " = " + result[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void generateAugmentedMatrix(int dim, double[][] matrix, double[][] systemMatrix) {
        for (int i = 0; i < dim; i++) {
            System.arraycopy(systemMatrix[i], 0, matrix[i], 0, dim);
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = dim; j < matrix[i].length; j++) {
                if (i == j - dim) {
                    matrix[i][j] = 1;
                }
            }
        }
    }

    private static void readSystem(int dim, Scanner scanner, int[] lastIdx, Map<String, Integer> varMappings, double[][] systemMatrix, double[] constants) {
        for (int i = 0; i < dim; i++) {
            do {
                Node node = null;
                String next = null;
                try {
                    System.out.println("Ingrese la ecuacion " + (i + 1) + " del sistema: ");
                    scanner.nextLine();
                    next = scanner.next().replace(" ", "");

                    String[] array = next.split("=");
                    if (array.length != 2) {
                        System.out.println("Se necesita un valor de la ecuacion del sistema, ingrese la ecuacion nuevamente. ");
                        continue;
                    }

                    node = Compiler.compile(array[0]).simplify().toAdditionOnly();
                    extractVariables(i, lastIdx, varMappings, systemMatrix, node);

                    constants[i] = Double.parseDouble(array[1].trim());
                    System.out.println(node.coloredString() + " = " + constants[i]);
                    break;
                } catch (NumberFormatException | JASException e) {
                    System.out.println("La ecuacion " + next + " no es valida.");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                } catch (IndexOutOfBoundsException e) {
                    if (node == null) {
                        continue;
                    }

                    System.out.println("La ecuacion " + node.coloredString() + " tiene una mayor cantidad de dimensiones que las especificadas.");
                }
            } while (true);

        }
    }

    private static void extractVariables(int dimIdx, int[] lastVarIdx, Map<String, Integer> varMappings, double[][] matrix, Node rootNode) {
        if (rootNode instanceof Binary binary) {
            if (binary.is("*")) {
                throw new IllegalArgumentException(binary.coloredString() + " no es una variable valida (¿quiza incluye multiplicacion entre variables?)");
            }

            for (Node node : binary.flattened()) {
                if (node instanceof Variable variable) {
                    int varIdx = varMappings.computeIfAbsent(variable.getName(), key -> lastVarIdx[0]++);

                    matrix[dimIdx][varIdx] = 1;
                } else if (node instanceof Binary binNode) {
                    if (!binNode.is("*")) {
                        throw new IllegalArgumentException("Solo se pueden resolver sistemas lineales simples, favor de simplificar el sistema (remover parentesis).");
                    }

                    if ((binNode.getLeft() instanceof RawValue coefficient) && (binNode.getRight() instanceof Variable var)) {
                        if (var.getName().length() > 1 && !varMappings.containsKey(var.getName())) {
                            System.out.println("La variable " + var.getName() + " sera tomado como una unica variable...");
                        }

                        int varIdx = varMappings.computeIfAbsent(var.getName(), key -> lastVarIdx[0]++);

                        matrix[dimIdx][varIdx] = coefficient.val();
                    } else if (
                            (binNode.getRight() instanceof RawValue coefficient) && (binNode.getLeft() instanceof Variable var)
                    ) {
                        if (var.getName().length() > 1 && !varMappings.containsKey(var.getName())) {
                            System.out.println("La variable " + var.getName() + " sera tomado como una unica variable...");
                        }

                        int varIdx = varMappings.computeIfAbsent(var.getName(), key -> lastVarIdx[0]++);

                        matrix[dimIdx][varIdx] = coefficient.val();
                    } else {
                        throw new IllegalArgumentException(binNode.coloredString() + " no es una variable valida (¿quiza incluye multiplicacion entre variables?)");
                    }
                }
            }
        }

    }


}