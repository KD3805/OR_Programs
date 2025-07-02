import java.util.*;

public class HungarianAssignmentProblem {

    static final int N = 5; // Problem size (5x5 matrix)

    public static void main(String[] args) {
        // Original cost matrix (given in the PPT Problem-2)
        int[][] costMatrix = {
            {2, 9, 2, 7, 1},
            {6, 8, 7, 6, 1},
            {4, 6, 5, 3, 1},
            {4, 2, 7, 3, 1},
            {5, 3, 9, 5, 1}
        };

        // Create a working copy of the matrix
        int[][] matrix = new int[N][N];
        for (int i = 0; i < N; i++)
            matrix[i] = Arrays.copyOf(costMatrix[i], N);

        System.out.println("Original Cost Matrix:");
        printMatrix(matrix);

        // Step 1: Row Reduction
        rowReduction(matrix);

        // Step 2: Column Reduction
        columnReduction(matrix);

        // Step 3: Star initial independent zeros
        int[][] mark = new int[N][N]; // mark[i][j] = 1 (starred), 2 (primed)
        boolean[] rowCover = new boolean[N];
        boolean[] colCover = new boolean[N];

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (matrix[i][j] == 0 && !rowCover[i] && !colCover[j]) {
                    mark[i][j] = 1; // star the zero
                    rowCover[i] = true;
                    colCover[j] = true;
                }

        // Reset covers
        Arrays.fill(rowCover, false);
        Arrays.fill(colCover, false);

        // Step 4: Cover all columns with starred zeros
        coverColumnsWithStars(mark, colCover);

        // Step 5+: Continue adjustment until all columns are covered
        while (!allColumnsCovered(colCover)) {
            int[] zero = findUncoveredZero(matrix, rowCover, colCover);

            while (zero == null) {
                adjustMatrix(matrix, rowCover, colCover); // Adjust the matrix
                zero = findUncoveredZero(matrix, rowCover, colCover);
            }

            int i = zero[0], j = zero[1];
            mark[i][j] = 2; // Prime the zero

            int starCol = findStarInRow(mark, i);
            if (starCol != -1) {
                rowCover[i] = true;       // Cover the row
                colCover[starCol] = false; // Uncover the column
            } else {
                // Found an augmenting path
                augmentPath(mark, i, j);
                clearPrimes(mark); // Reset all primes
                Arrays.fill(rowCover, false);
                Arrays.fill(colCover, false);
                coverColumnsWithStars(mark, colCover);
            }
        }

        // Step 6: Final assignment and cost
        int totalCost = 0;
        System.out.println("Final Assignment:");
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (mark[i][j] == 1) {
                    System.out.printf("  Job %c → Machine %d (Cost: %d)\n", (char)(i + 'A'), j + 1, costMatrix[i][j]);
                    totalCost += costMatrix[i][j];
                }

        System.out.println("Minimum Total Cost (Z min) = " + totalCost + " hours");
    }

    // Step 1: Subtract row-wise min
    static void rowReduction(int[][] matrix) {
        for (int i = 0; i < N; i++) {
            int min = Arrays.stream(matrix[i]).min().getAsInt();
            for (int j = 0; j < N; j++)
                matrix[i][j] -= min;
        }
        System.out.println("After Row Reduction:");
        printMatrix(matrix);
    }

    // Step 2: Subtract column-wise min
    static void columnReduction(int[][] matrix) {
        for (int j = 0; j < N; j++) {
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < N; i++)
                min = Math.min(min, matrix[i][j]);
            for (int i = 0; i < N; i++)
                matrix[i][j] -= min;
        }
        System.out.println("After Column Reduction:");
        printMatrix(matrix);
    }

    // Cover columns with starred zeros
    static void coverColumnsWithStars(int[][] mark, boolean[] colCover) {
        Arrays.fill(colCover, false);
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (mark[i][j] == 1)
                    colCover[j] = true;
    }

    static boolean allColumnsCovered(boolean[] colCover) {
        for (boolean cover : colCover)
            if (!cover) return false;
        return true;
    }

    // Find a 0 in uncovered area
    static int[] findUncoveredZero(int[][] matrix, boolean[] rowCover, boolean[] colCover) {
        for (int i = 0; i < N; i++)
            if (!rowCover[i])
                for (int j = 0; j < N; j++)
                    if (matrix[i][j] == 0 && !colCover[j])
                        return new int[]{i, j};
        return null;
    }

    // Find starred zero in a row
    static int findStarInRow(int[][] mark, int row) {
        for (int j = 0; j < N; j++)
            if (mark[row][j] == 1) return j;
        return -1;
    }

    // Augment path between primes and stars
    static void augmentPath(int[][] mark, int row, int col) {
        List<int[]> path = new ArrayList<>();
        path.add(new int[]{row, col});

        while (true) {
            int starRow = findStarInColumn(mark, col);
            if (starRow == -1) break;
            path.add(new int[]{starRow, col});

            int primeCol = findPrimeInRow(mark, starRow);
            path.add(new int[]{starRow, primeCol});
            col = primeCol;
        }

        // Flip stars and primes along path
        for (int[] p : path)
            mark[p[0]][p[1]] = (mark[p[0]][p[1]] == 1) ? 0 : 1;
    }

    static int findStarInColumn(int[][] mark, int col) {
        for (int i = 0; i < N; i++)
            if (mark[i][col] == 1) return i;
        return -1;
    }

    static int findPrimeInRow(int[][] mark, int row) {
        for (int j = 0; j < N; j++)
            if (mark[row][j] == 2) return j;
        return -1;
    }

    static void clearPrimes(int[][] mark) {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (mark[i][j] == 2) mark[i][j] = 0;
    }

    // Adjustment Step (slide-based logic): subtract uncovered min, add to covered intersections
    static void adjustMatrix(int[][] matrix, boolean[] rowCover, boolean[] colCover) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < N; i++)
            if (!rowCover[i])
                for (int j = 0; j < N; j++)
                    if (!colCover[j])
                        min = Math.min(min, matrix[i][j]);

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                if (rowCover[i] && colCover[j]) matrix[i][j] += min;
                else if (!rowCover[i] && !colCover[j]) matrix[i][j] -= min;
            }

        System.out.println("Matrix after Adjustment Step:");
        printMatrix(matrix);
    }

    // Pretty-print matrix
    static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row)
                System.out.printf("%3d ", val);
            System.out.println();
        }
    }
}
