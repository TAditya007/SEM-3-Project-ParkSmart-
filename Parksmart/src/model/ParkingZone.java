package model;

public class ParkingZone {
    private String zoneName;
    private int totalSlots;
    private int occupiedSlots;

    public ParkingZone(String zoneName, int totalSlots, int occupiedSlots) {
        this.zoneName = zoneName;
        this.totalSlots = totalSlots;
        this.occupiedSlots = occupiedSlots;
    }

    public String getZoneName() {
        return zoneName;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public int getOccupiedSlots() {
        return occupiedSlots;
    }

    public int getAvailableSlots() {
        return totalSlots - occupiedSlots;
    }
}