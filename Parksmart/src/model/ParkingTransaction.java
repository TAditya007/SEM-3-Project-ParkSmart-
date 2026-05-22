package model;

public class ParkingTransaction {
    private String transactionId;
    private String vehicleNumber;
    private int slotId;
    private String entryTime;
    private String exitTime;
    private double amount;

    public ParkingTransaction(String transactionId, String vehicleNumber, int slotId, String entryTime, String exitTime, double amount) {
        this.transactionId = transactionId;
        this.vehicleNumber = vehicleNumber;
        this.slotId = slotId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public int getSlotId() {
        return slotId;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public String getExitTime() {
        return exitTime;
    }

    public double getAmount() {
        return amount;
    }
}
