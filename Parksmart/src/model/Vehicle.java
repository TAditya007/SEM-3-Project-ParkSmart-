package model;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {
    private String vehicleNumber;
    private String ownerName;
    private String vehicleType;
    private String slotId;
    private String entryTime;
    private String exitTime;
    private String status;
    private List<String> history;
    private List<String> slotVisitHistory;

    public Vehicle(String vehicleNumber, String ownerName, String vehicleType,
                   String slotId, String entryTime, String exitTime, String status) {
        this.vehicleNumber = vehicleNumber;
        this.ownerName = ownerName;
        this.vehicleType = vehicleType;
        this.slotId = slotId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.status = status;
        this.history = new ArrayList<>();
        this.slotVisitHistory = new ArrayList<>();

        this.history.add(entryTime + " - Vehicle entered into slot " + slotId + " [" + status + "]");
        this.slotVisitHistory.add(slotId);
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getSlotId() {
        return slotId;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public String getExitTime() {
        return exitTime;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getHistory() {
        return history;
    }

    public List<String> getSlotVisitHistory() {
        return slotVisitHistory;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
        if (slotId != null && !slotId.trim().isEmpty() && !"Not Assigned".equalsIgnoreCase(slotId)) {
            this.slotVisitHistory.add(slotId);
        }
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addHistory(String event) {
        history.add(event);
    }

    public void addSlotVisit(String slotDisplayId) {
        if (slotDisplayId != null && !slotDisplayId.trim().isEmpty()) {
            slotVisitHistory.add(slotDisplayId);
        }
    }

    public String getLastVisitedSlot() {
        if (slotVisitHistory == null || slotVisitHistory.isEmpty()) {
            return "Not Assigned";
        }
        return slotVisitHistory.get(slotVisitHistory.size() - 1);
    }
}