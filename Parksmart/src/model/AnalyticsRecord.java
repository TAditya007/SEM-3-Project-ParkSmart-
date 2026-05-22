package model;

public class AnalyticsRecord {
    private String recordId;
    private String date;
    private int totalVehicles;
    private int occupiedSlots;
    private double totalRevenue;

    public AnalyticsRecord(String recordId, String date, int totalVehicles, int occupiedSlots, double totalRevenue) {
        this.recordId = recordId;
        this.date = date;
        this.totalVehicles = totalVehicles;
        this.occupiedSlots = occupiedSlots;
        this.totalRevenue = totalRevenue;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getDate() {
        return date;
    }

    public int getTotalVehicles() {
        return totalVehicles;
    }

    public int getOccupiedSlots() {
        return occupiedSlots;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}