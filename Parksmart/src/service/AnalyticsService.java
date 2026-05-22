package service;

import dsa.OptimizationAlgorithms;
import dsa.SortingAlgorithms;
import model.AnalyticsRecord;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsService {
    private OptimizationAlgorithms optimizationAlgorithms;
    private SortingAlgorithms sortingAlgorithms;
    private List<AnalyticsRecord> analyticsRecords;

    public AnalyticsService() {
        optimizationAlgorithms = new OptimizationAlgorithms();
        sortingAlgorithms = new SortingAlgorithms();
        analyticsRecords = new ArrayList<>();
    }

    public void addRecord(AnalyticsRecord record) {
        analyticsRecords.add(record);
    }

    public List<AnalyticsRecord> getAnalyticsRecords() {
        return analyticsRecords;
    }

    public void runOptimization() {
        optimizationAlgorithms.runGreedyAllocation();
        optimizationAlgorithms.runDynamicProgrammingAnalysis();
    }

    public int[] sortUsageData(int[] usageData) {
        return sortingAlgorithms.sortSlotUsage(usageData);
    }
}