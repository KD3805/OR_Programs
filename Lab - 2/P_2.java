import java.util.*;

public class P_2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter No of tasks : ");
        int noOfTask = sc.nextInt();

        System.out.println("Enter No of servers : ");
        int servers = sc.nextInt();

        int[] timeOfTasks = new int[noOfTask];

        System.out.println("Enter time : ");
        for (int i = 0; i < noOfTask; i++) {
            timeOfTasks[i] = sc.nextInt();
        }

        int minMaxLoad = Integer.MAX_VALUE;
        int totalCombinations = (int) Math.pow(servers, noOfTask);

        for (int i = 0; i < totalCombinations; i++) {
            int[] taskToServer = toBaseKArray(i,  noOfTask); 
            int[] serverLoads = new int[servers];

            for (int j = 0; j < noOfTask; j++) {
                int server = taskToServer[j]; 
                serverLoads[server] += timeOfTasks[j];
            }

            int maxLoad = Arrays.stream(serverLoads).max().getAsInt(); 
            minMaxLoad = Math.min(minMaxLoad, maxLoad);
        }

        System.out.println("Minimum max load: " + minMaxLoad);
    }

    public static int[] toBaseKArray(int number, int length) {
        int[] arr = new int[length];
        for (int i = length - 1; i >= 0; i--) {
            arr[i] = number % 2;
            number /= 2;
        }
        return arr;
    }
}

// github: https://github.com/KD3805/OR_Programs