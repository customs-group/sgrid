package edwardlol;

/**
 *
 * Created by edwardlol on 16/7/5.
 */
public class MyMath {
    // Suppress default constructor for noninstantiability
    private MyMath() {
        throw new AssertionError();
    }


    /**
     * normalize the input vector so that it's mod equals 1
     * @param input the input vector
     * @return the normalized vector
     */
    public static double[] normalize(double[] input) {
        double sum = 0.0d;
        for (double e : input) {
            sum += e * e;
        }
        sum = Math.sqrt(sum);
        double[] result = new double[input.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = input[i] / sum;
        }
        return result;
    }

    /**
     * calculate the dot product of two vectors
     * the two input vectors must be equal in length
     * @param vector1 the first input vector
     * @param vector2 the second input vector
     * @return the dot product of the input vectors
     */
    public static double dot(double[] vector1, double[] vector2) {
        double result = 0.0d;
        if (vector1.length != vector2.length) {
            System.err.println("two input vectors must me equal in length!");
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < vector1.length; i++) {
            result += vector1[i] * vector2[i];
        }
        return result;
    }

    /**
     * get the euclidean norm (aka. length) of a vector
     * @param vector the input vector
     * @return the euclidean norm (aka. length) of the input vector
     */
    public static double euclideanNorm(double[] vector) {
        return Math.sqrt(dot(vector, vector));
    }

    /**
     * calculate the euclidean distance of two input vectors
     * the two input vectors must be equal in length
     * @param vector1 the first input vector
     * @param vector2 the second input vector
     * @return the euclidean distance of the input vectors
     */
    public static double euclideanDistance(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) {
            System.err.println("two input vectors must me equal in length!");
            throw new IllegalArgumentException();
        }
        double sum = 0.0d;
        for (int i = 0; i < vector1.length; i++) {
            sum += Math.pow(vector1[i] - vector2[i], 2);
        }
        return Math.sqrt(sum);
    }

    /**
     * calculate the cosine similarity of two input vectors
     * the two input vectors must be equal in length
     * @param vector1 the first input vector
     * @param vector2 the second input vector
     * @return the cosine similarity of the input vectors
     */
    public static double cosineSimilarity(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) {
            System.err.println("two input vectors must me equal in length!");
            throw new IllegalArgumentException();
        }
        return dot(vector1, vector2) / (euclideanNorm(vector1) * euclideanNorm(vector2));
    }

    /**
     * update the distance matrix(in double[][])
     * recalculate distances affected by the union of the ith cluster and the jth cluster
     * @param originalMatrix the original distance matrix
     * @param index_i the first index of two clusters to be unioned
     * @param index_j the second index of two clusters to be unioned
     * @param cluster_i_num the number of defects in cluster i
     * @param cluster_j_num the number of defects in cluster j
     * @return the updated distance matrix
     */
    public static double[][] updateDistanceMatrix(double[][] originalMatrix, int index_i, int index_j, int cluster_i_num, int cluster_j_num) {
        double[][] resultMatrix = new double[originalMatrix.length - 1][originalMatrix.length - 1];
        for (int i = 0; i < resultMatrix.length; i++) {
            for (int j = 0; j < resultMatrix.length; j++) {
                if (i < index_i || (index_i < i && i < index_j)) {
                    // 0~index_i 以及 index_i~index_j 列之间
                    if (j < index_i || (index_i < j && j < index_j)) {
                        // 0～index_i 以及 index_i~index_j 行之间, 直接拷贝
                        resultMatrix[i][j] = originalMatrix[i][j];
                    } else if (j == index_i) {
                        // 第index_i行, 合并cluster, 更新距离
                        resultMatrix[i][j] = (originalMatrix[i][j] * cluster_i_num
                                + originalMatrix[i][index_j] * cluster_j_num) / (cluster_i_num + cluster_j_num);
                    } else if (j >= index_j) {
                        // index_j行之后, 上移一行
                        resultMatrix[i][j] = originalMatrix[i][j + 1];
                    }
                } else if (i == index_i) {
                    // 第index_i列
                    if (j < index_j) {
                        // index_j行之前, 合并cluster, 更新距离
                        resultMatrix[i][j] = (originalMatrix[i][j] * cluster_i_num
                                + originalMatrix[index_j][j] * cluster_j_num) / (cluster_i_num + cluster_j_num);
                    } else if (j >= index_j) {
                        // index_j行之后, 更新距离并上移一行
                        resultMatrix[i][j] = (originalMatrix[i][j + 1] * cluster_i_num
                                + originalMatrix[index_j][j + 1] * cluster_j_num) / (cluster_i_num + cluster_j_num);
                    }
                } else if (i >= index_j) {
                    // index_j列及之后, 整体左移一列
                    if (j < index_i || (index_i < j && j < index_j)) {
                        // 0～index_i 以及 index_i~index_j 行之间, 左移一列
                        resultMatrix[i][j] = originalMatrix[i + 1][j];
                    } else if (j == index_i) {
                        // index_i行, 更新距离并左移一行
                        resultMatrix[i][j] = (originalMatrix[i + 1][j] * cluster_i_num
                                + originalMatrix[i + 1][index_j] * cluster_j_num) / (cluster_i_num + cluster_j_num);
                    } else if (j >= index_j) {
                        // index_j列之后, 左移一列, 上移一行
                        resultMatrix[i][j] = originalMatrix[i + 1][j + 1];
                    }
                }
            }
        }
        return resultMatrix;
    }

    /**
     * update the distance matrix(in float[][])
     * recalculate distances affected by the union of the ith cluster and the jth cluster
     * @param originalMatrix the original distance matrix
     * @param index_i the first index of two clusters to be unioned
     * @param index_j the second index of two clusters to be unioned
     * @param cluster_i_num the number of defects in cluster i
     * @param cluster_j_num the number of defects in cluster j
     * @return the updated distance matrix
     */
    public static float[][] updateDistanceMatrix(float[][] originalMatrix, int index_i, int index_j, int cluster_i_num, int cluster_j_num) {
        float[][] resultMatrix = new float[originalMatrix.length - 1][originalMatrix.length - 1];
        for (int i = 0; i < resultMatrix.length; i++) {
            for (int j = 0; j < resultMatrix.length; j++) {
                if (i < index_i || (index_i < i && i < index_j)) {
                    // 0~index_i 以及 index_i~index_j 列之间
                    if (j < index_i || (index_i < j && j < index_j)) {
                        // 0～index_i 以及 index_i~index_j 行之间, 直接拷贝
                        resultMatrix[i][j] = originalMatrix[i][j];
                    } else if (j == index_i) {
                        // 第index_i行, 合并cluster, 更新距离
                        resultMatrix[i][j] = (originalMatrix[i][j] * cluster_i_num
                                + originalMatrix[i][index_j] * cluster_j_num) / (cluster_i_num + cluster_j_num);
                    } else if (j >= index_j) {
                        // index_j行之后, 上移一行
                        resultMatrix[i][j] = originalMatrix[i][j + 1];
                    }
                } else if (i == index_i) {
                    // 第index_i列
                    if (j < index_j) {
                        // index_j行之前, 合并cluster, 更新距离
                        resultMatrix[i][j] = (originalMatrix[i][j] * cluster_i_num
                                + originalMatrix[index_j][j] * cluster_j_num) / (cluster_i_num + cluster_j_num);
                    } else if (j >= index_j) {
                        // index_j行之后, 更新距离并上移一行
                        resultMatrix[i][j] = (originalMatrix[i][j + 1] * cluster_i_num
                                + originalMatrix[index_j][j + 1] * cluster_j_num) / (cluster_i_num + cluster_j_num);
                    }
                } else if (i >= index_j) {
                    // index_j列及之后, 整体左移一列
                    if (j < index_i || (index_i < j && j < index_j)) {
                        // 0～index_i 以及 index_i~index_j 行之间, 左移一列
                        resultMatrix[i][j] = originalMatrix[i + 1][j];
                    } else if (j == index_i) {
                        // index_i行, 更新距离并左移一行
                        resultMatrix[i][j] = (originalMatrix[i + 1][j] * cluster_i_num
                                + originalMatrix[i + 1][index_j] * cluster_j_num) / (cluster_i_num + cluster_j_num);
                    } else if (j >= index_j) {
                        // index_j列之后, 左移一列, 上移一行
                        resultMatrix[i][j] = originalMatrix[i + 1][j + 1];
                    }
                }
            }
        }
        return resultMatrix;
    }

}
