import java.util.Arrays;

/**
 * Implements Best Feasible Solutions (BSF) for the Transportation Problem.
 * Methods:
 * 1. Northwest Corner Method
 * 2. Least Cost Method
 * 3. Vogel's Approximation Method (stub)
 */
public class TransportationProblem {
    private int[] supply;
    private int[] demand;
    private int[][] cost;

    // Allocation matrix to hold shipments
    private int[][] allocation;

    public TransportationProblem(int[] supply, int[] demand, int[][] cost) {
        this.supply = Arrays.copyOf(supply, supply.length);
        this.demand = Arrays.copyOf(demand, demand.length);
        this.cost = cost;
        this.allocation = new int[supply.length][demand.length];
    }

    /**
     * Northwest Corner Method:
     * Start at (0,0). Allocate as much as possible = min(supply[i], demand[j]).
     * Reduce supply[i] and demand[j]. Move right if supply exhausted, else move down if demand exhausted.
     * Repeat until all supplies and demands are zero.
     * @return allocation matrix
     */
    public int[][] northwestCornerSolution() {
        int i = 0, j = 0;
        int[] s = Arrays.copyOf(supply, supply.length);
        int[] d = Arrays.copyOf(demand, demand.length);
        allocation = new int[supply.length][demand.length];

        while (i < s.length && j < d.length) {
            int alloc = Math.min(s[i], d[j]);
            allocation[i][j] = alloc;
            s[i] -= alloc;
            d[j] -= alloc;

            // Move to next cell
            if (s[i] == 0 && i < s.length - 1) {
                i++; // row exhausted, move down
            } 
            if (d[j] == 0 && j < d.length - 1) {
                j++; // column exhausted, move right
            } 
        }
        return allocation;
    }

    /**
     * Least Cost Method:
     * Choose the cell with the lowest cost, allocate as much as possible, adjust supply/demand, mark row/column exhausted.
     * @return allocation matrix
     */
    public int[][] leastCostSolution() {
        int[] s = Arrays.copyOf(supply, supply.length);
        int[] d = Arrays.copyOf(demand, demand.length);
        allocation = new int[supply.length][demand.length];

        boolean[][] used = new boolean[supply.length][demand.length];

        while (true) {
            int minCost = Integer.MAX_VALUE;
            int minI = -1, minJ = -1;

            // Find the least cost cell not used
            for (int i = 0; i < supply.length; i++) {
                if (s[i] == 0) continue; // we can't allocate more to a row exhausted
                for (int j = 0; j < demand.length; j++) {
                    if (d[j] == 0 || used[i][j]) continue; // we can't allocate more to a column exhausted or used cell
                    if (cost[i][j] < minCost) {
                        minCost = cost[i][j];
                        minI = i;
                        minJ = j;
                    }
                }
            }

            if (minI == -1 || minJ == -1) break; // No valid cell left

            int alloc = Math.min(s[minI], d[minJ]);
            allocation[minI][minJ] = alloc;
            s[minI] -= alloc;
            d[minJ] -= alloc;

            // Mark row or column as used
            if (s[minI] == 0) {
                for (int j = 0; j < demand.length; j++) {
                    used[minI][j] = true;
                }
            }
            if (d[minJ] == 0) {
                for (int i = 0; i < supply.length; i++) {
                    used[i][minJ] = true;
                }
            }
        }
        return allocation;
    }

    /**
     * Calculates total transportation cost using the allocation and cost matrices.
     * @return total transportation cost
     */
    public int calculateTotalCost() {
        int totalCost = 0;
        for (int i = 0; i < allocation.length; i++) {
            for (int j = 0; j < allocation[0].length; j++) {
                totalCost += allocation[i][j] * cost[i][j];
            }
        }
        return totalCost;
    }

    /**
     * Vogel's Approximation Method (VAM):
     * For each row/column, find penalty = difference between two lowest costs.
     * Select row/column with highest penalty, allocate in its lowest cost cell. Repeat.
     */
    public int[][] vogelApproximationSolution() {
        int[] s = Arrays.copyOf(supply, supply.length);
        int[] d = Arrays.copyOf(demand, demand.length);
        allocation = new int[supply.length][demand.length];

        boolean[] rowDone = new boolean[supply.length];
        boolean[] colDone = new boolean[demand.length];

        while (true) {
            int maxPenalty = -1;
            boolean isRow = true;
            int index = -1;

            // Row penalties
            for (int i = 0; i < supply.length; i++) {
                if (rowDone[i] || s[i] == 0) continue;
                int[] row = cost[i];
                int first = Integer.MAX_VALUE, second = Integer.MAX_VALUE;
                for (int j = 0; j < demand.length; j++) {
                    if (colDone[j] || d[j] == 0) continue;
                    if (row[j] < first) {
                        second = first;
                        first = row[j];
                    } else if (row[j] < second) {
                        second = row[j];
                    }
                }
                int penalty = second - first;
                if (penalty > maxPenalty) {
                    maxPenalty = penalty;
                    isRow = true;
                    index = i;
                }
            }

            // Column penalties
            for (int j = 0; j < demand.length; j++) {
                if (colDone[j] || d[j] == 0) continue;
                int first = Integer.MAX_VALUE, second = Integer.MAX_VALUE;
                for (int i = 0; i < supply.length; i++) {
                    if (rowDone[i] || s[i] == 0) continue;
                    if (cost[i][j] < first) {
                        second = first;
                        first = cost[i][j];
                    } else if (cost[i][j] < second) {
                        second = cost[i][j];
                    }
                }
                int penalty = second - first;
                if (penalty > maxPenalty) {
                    maxPenalty = penalty;
                    isRow = false;
                    index = j;
                }
            }

            if (index == -1) break;

            int minCost = Integer.MAX_VALUE;
            int minI = -1, minJ = -1;

            if (isRow) {
                for (int j = 0; j < demand.length; j++) {
                    if (!colDone[j] && d[j] > 0 && cost[index][j] < minCost) {
                        minCost = cost[index][j];
                        minI = index;
                        minJ = j;
                    }
                }
            } else {
                for (int i = 0; i < supply.length; i++) {
                    if (!rowDone[i] && s[i] > 0 && cost[i][index] < minCost) {
                        minCost = cost[i][index];
                        minI = i;
                        minJ = index;
                    }
                }
            }

            int alloc = Math.min(s[minI], d[minJ]);
            allocation[minI][minJ] = alloc;
            s[minI] -= alloc;
            d[minJ] -= alloc;

            if (s[minI] == 0) rowDone[minI] = true;
            if (d[minJ] == 0) colDone[minJ] = true;
        }

        return allocation;
    }

    // Example usage and testing
    public static void main(String[] args) {
        int[] supply = {500, 300, 200};
        int[] demand = {180, 150, 350, 320};
        int[][] cost = {
            {12, 10, 12, 13},
            {7, 11, 8, 14},
            {6, 16, 11, 7}
        };

        TransportationProblem tp = new TransportationProblem(supply, demand, cost);

        System.out.println("--- Northwest Corner Method ---");
        int[][] nwAlloc = tp.northwestCornerSolution();
        for (int[] row : nwAlloc) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println("Total Cost: " + tp.calculateTotalCost());

        System.out.println("\n--- Least Cost Method ---");
        int[][] lcAlloc = tp.leastCostSolution();
        for (int[] row : lcAlloc) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println("Total Cost: " + tp.calculateTotalCost());

        System.out.println("\n--- Vogel's Approximation Method (VAM) ---");
        int[][] vamAlloc = tp.vogelApproximationSolution();
        for (int[] row : vamAlloc) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println("Total Cost: " + tp.calculateTotalCost());
    }
}
