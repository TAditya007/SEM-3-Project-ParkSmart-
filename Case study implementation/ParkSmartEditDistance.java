import java.util.*;

public class ParkSmartEditDistance {
    // Compute full Levenshtein edit distance DP table.
    static int[][] buildDpTable(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1];

        // Boundary: distance to/from empty string.
        for (int i = 0; i <= m; i++) dp[i][0] = i;      // delete i chars
        for (int j = 0; j <= n; j++) dp[0][j] = j;      // insert j chars

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];        // no cost
                } else {
                    int del = dp[i - 1][j];            // delete from a
                    int ins = dp[i][j - 1];            // insert into a
                    int sub = dp[i - 1][j - 1];        // substitute
                    dp[i][j] = 1 + Math.min(del, Math.min(ins, sub));
                }
            }
        }
        return dp;
    }

    // Convenience wrapper: just distance.
    static int editDistance(String a, String b) {
        int[][] dp = buildDpTable(a, b);
        return dp[a.length()][b.length()];
    }

    // Pretty-print the DP table with row/column labels.
    static void printDpTable(String a, String b, int[][] dp) {
        int m = a.length();
        int n = b.length();

        System.out.println("DP table for \"" + a + "\" vs \"" + b + "\":");
        // Header row
        System.out.print("    "); // space for row label
        System.out.print("  ∅ ");
        for (int j = 0; j < n; j++) {
            System.out.printf("  %c ", b.charAt(j));
        }
        System.out.println();

        for (int i = 0; i <= m; i++) {
            // Row label
            if (i == 0) {
                System.out.print(" ∅ ");
            } else {
                System.out.printf(" %c ", a.charAt(i - 1));
            }
            // Row entries
            for (int j = 0; j <= n; j++) {
                System.out.printf(" %2d", dp[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    // Reconstruct one sequence of edit operations from dp table.
    // Returns operations in forward order (from a to b).
    static List<String> reconstructEdits(String a, String b, int[][] dp) {
        int i = a.length();
        int j = b.length();
        List<String> ops = new ArrayList<>();

        while (i > 0 || j > 0) {
            // When both i and j > 0, we can consider all 3 moves.
            if (i > 0 && j > 0 && a.charAt(i - 1) == b.charAt(j - 1)) {
                // characters match: move diagonally, no op
                i--;
                j--;
            } else if (i > 0 && j > 0) {
                int cur = dp[i][j];
                int del = dp[i - 1][j];
                int ins = dp[i][j - 1];
                int sub = dp[i - 1][j - 1];

                if (cur == sub + 1 && sub <= del && sub <= ins) {
                    // substitution
                    String op = "Substitute '" + a.charAt(i - 1) +
                                "' -> '" + b.charAt(j - 1) + "'";
                    ops.add(op);
                    i--;
                    j--;
                } else if (cur == del + 1 && del <= ins) {
                    // deletion from a
                    String op = "Delete '" + a.charAt(i - 1) + "'";
                    ops.add(op);
                    i--;
                } else {
                    // insertion (into a)
                    String op = "Insert '" + b.charAt(j - 1) + "'";
                    ops.add(op);
                    j--;
                }
            } else if (i > 0) {
                // must be deletions remaining
                String op = "Delete '" + a.charAt(i - 1) + "'";
                ops.add(op);
                i--;
            } else {
                // j > 0: must be insertions remaining
                String op = "Insert '" + b.charAt(j - 1) + "'";
                ops.add(op);
                j--;
            }
        }

        // We built ops from end to start, so reverse to make it forward.
        Collections.reverse(ops);
        return ops;
    }

    public static void main(String[] args) {
        // Example from case study: ParkSmart using edit distance
        String a = "kitten";   // mistyped zone keyword
        String b = "sitting";  // correct zone keyword

        int[][] dp = buildDpTable(a, b);
        printDpTable(a, b, dp);

        int dist = dp[a.length()][b.length()];
        System.out.println("Edit distance between \"" + a +
                           "\" and \"" + b + "\" = " + dist);

        List<String> ops = reconstructEdits(a, b, dp);
        System.out.println("One optimal sequence of edit operations:");
        for (String op : ops) {
            System.out.println(" - " + op);
        }

        // Quick interactive demo: user types two words
        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.print("Enter first string: ");
        String s1 = sc.nextLine();
        System.out.print("Enter second string: ");
        String s2 = sc.nextLine();

        int[][] dp2 = buildDpTable(s1, s2);
        int d2 = dp2[s1.length()][s2.length()];
        System.out.println("Edit distance between \"" + s1 +
                           "\" and \"" + s2 + "\" = " + d2);
        sc.close();
    }
}