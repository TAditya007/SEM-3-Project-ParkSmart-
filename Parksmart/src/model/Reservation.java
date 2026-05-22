package model;

import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private String reservationId;
    private String vehicleNumber;
    private String slotId;
    private String reservationTime;
    private String status;
    private String notes;
    private List<String> history;

    public Reservation(String reservationId, String vehicleNumber, String slotId, String reservationTime, String status) {
        this.reservationId = reservationId;
        this.vehicleNumber = vehicleNumber;
        this.slotId = slotId;
        this.reservationTime = reservationTime;
        this.status = status;
        this.notes = "";
        this.history = new ArrayList<>();

        this.history.add(reservationTime + " - Reservation created for slot " + slotId + " [" + status + "]");
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getSlotId() {
        return slotId;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public List<String> getHistory() {
        return history;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void addHistory(String event) {
        history.add(event);
    }
}