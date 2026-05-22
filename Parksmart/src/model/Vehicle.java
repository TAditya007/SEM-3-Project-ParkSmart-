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

        this.history.add(entryTime + " - Vehicle entered into slot " + slotId + " [" + status + "]");
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

    public void setSlotId(String slotId) {
        this.slotId = slotId;
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
}