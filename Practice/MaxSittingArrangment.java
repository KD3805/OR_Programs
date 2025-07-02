public class MaxSittingArrangment {

  public static void main(String[] args) {
    char[][] seats = {
      { '#', '.', '.', '.', '#' },
      { '.', '#', '.', '#', '.' },
      { '.', '.', '#', '.', '.' },
      { '.', '#', '.', '#', '.' },
      { '#', '.', '.', '.', '#' },
    };

    int maxStudents = maxStudents(seats);
    System.out.println(
      "Maximum number of students that can be seated: " + maxStudents
    );

    System.out.println("Seating arrangement after placement:");
    for (char[] row : seats) {
      for (char seat : row) {
        System.out.print(seat + " ");
      }
      System.out.println();
    }
  }

  public static int maxStudents(char[][] seats) {
    int m = seats.length;
    int n = seats[0].length;
    boolean[][] used = new boolean[m][n];

    int count = 0;

    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        char ch = seats[i][j];

        if (ch == '.' && !used[i][j]) {
          boolean isSafe = true;

          if (
            i - 1 >= 0 &&
            j - 1 >= 0 &&
            seats[i - 1][j - 1] == '.' &&
            used[i - 1][j - 1]
          ) {
            isSafe = false;
          }
          if (
            i - 1 >= 0 &&
            j + 1 < n &&
            seats[i - 1][j + 1] == '.' &&
            used[i - 1][j + 1]
          ) {
            isSafe = false;
          }
          if (j - 1 >= 0 && seats[i][j - 1] == '.' && used[i][j - 1]) {
            isSafe = false;
          }
          if (j + 1 < n && seats[i][j + 1] == '.' && used[i][j + 1]) {
            isSafe = false;
          }

          if (isSafe) {
            count++;
            // seats[i][j] = 'S';
            used[i][j] = true;
          }
        }
      }
    }

    return count;
  }
}
