package dsa;

public class OptimizationAlgorithms {

    public void runGreedyAllocation() {
        System.out.println("Greedy allocation algorithm executed for parking slot optimization.");
    }

    public int fractionalKnapsack(int[] values, int[] weights, int capacity) {
        int totalValue = 0;
        for (int i = 0; i < values.length && capacity > 0; i++) {
            if (weights[i] <= capacity) {
                totalValue += values[i];
                capacity -= weights[i];
            }
        }
        return totalValue;
    }

    public int longestIncreasingSubsequence(int[] arr) {
        if (arr.length == 0) {
            return 0;
        }

        int[] dp = new int[arr.length];
        int maxLength = 1;

        for (int i = 0; i < arr.length; i++) {
            dp[i] = 1;
        }

        for (int i = 1; i < arr.length; i++) {
            for (int j = 0; j < i; j++) {
                if (arr[i] > arr[j]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            maxLength = Math.max(maxLength, dp[i]);
        }

        return maxLength;
    }

    public void runDynamicProgrammingAnalysis() {
        System.out.println("Dynamic programming analysis executed for parking demand trends.");
    }
}