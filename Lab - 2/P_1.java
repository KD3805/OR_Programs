public class P_1 {
    public static void main(String[] args) {
        int maxZ = Integer.MIN_VALUE;
        int bestX1 = 0, bestX2 = 0;

        for(int x1 = 0; x1 <= 4; x1++) {
            for(int x2 = 0; x2 <= 4; x2++) {
                if(x1 + x2 <= 4) {
                    int Z = 3 * x1 + 2 * x2;
                    if (Z > maxZ) {
                        maxZ = Z;
                        bestX1 = x1;
                        bestX2 = x2;
                    }
                }
            }
        }

        System.out.println("Maximum value of Z: " + maxZ);
        System.out.println("Optimal values: x1 = " + bestX1 + ", x2 = " + bestX2);
    }
}
