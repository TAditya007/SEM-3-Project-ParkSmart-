package model;

import java.util.ArrayList;
import java.util.List;

public class ParkingSlot {
    private int slotId;
    private String displaySlotId;
    private String blockName;
    private String slotGroup;
    private String slotType;
    private String status;
    private boolean available;
    private String occupiedVehicleNumber;
    private String lastEntryTime;
    private String lastExitTime;
    private String notes;
    private List<String> history;

    public ParkingSlot(int slotId,
                       String displaySlotId,
                       String blockName,
                       String slotGroup,
                       String slotType,
                       String status,
                       boolean available) {
        this.slotId = slotId;
        this.displaySlotId = displaySlotId;
        this.blockName = blockName;
        this.slotGroup = slotGroup;
        this.slotType = slotType;
        this.status = status;
        this.available = available;
        this.occupiedVehicleNumber = "";
        this.lastEntryTime = "--";
        this.lastExitTime = "--";
        this.notes = "";
        this.history = new ArrayList<>();

        this.history.add("Slot created with status [" + status + "]");
    }

    public int getSlotId() {
        return slotId;
    }

    public String getDisplaySlotId() {
        return displaySlotId;
    }

    public String getBlockName() {
        return blockName;
    }

    public String getSlotGroup() {
        return slotGroup;
    }

    public String getSlotType() {
        return slotType;
    }

    public String getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getOccupiedVehicleNumber() {
        return occupiedVehicleNumber;
    }

    public String getLastEntryTime() {
        return lastEntryTime;
    }

    public String getLastExitTime() {
        return lastExitTime;
    }

    public String getNotes() {
        return notes;
    }

    public List<String> getHistory() {
        return history;
    }

    public void setDisplaySlotId(String displaySlotId) {
        this.displaySlotId = displaySlotId;
    }

    public void setStatus(String status) {
        this.status = status;
        this.available = "Available".equalsIgnoreCase(status);
    }

    public void setLastEntryTime(String lastEntryTime) {
        this.lastEntryTime = lastEntryTime;
    }

    public void setLastExitTime(String lastExitTime) {
        this.lastExitTime = lastExitTime;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void addHistory(String event) {
        history.add(event);
    }

    public void assignVehicle(String vehicleNumber) {
        this.occupiedVehicleNumber = vehicleNumber;
        this.available = false;

        if ("EV".equalsIgnoreCase(slotGroup)) {
            this.status = "Charging";
        } else {
            this.status = "Occupied";
        }

        this.history.add("Vehicle " + vehicleNumber + " assigned to slot [" + this.status + "]");
    }

    public void releaseSlot() {
        String previousVehicle = this.occupiedVehicleNumber;

        this.occupiedVehicleNumber = "";
        this.available = true;
        this.status = "Available";

        if (previousVehicle != null && !previousVehicle.isEmpty()) {
            this.history.add("Vehicle " + previousVehicle + " released from slot [Available]");
        } else {
            this.history.add("Slot released and marked [Available]");
        }
    }
}