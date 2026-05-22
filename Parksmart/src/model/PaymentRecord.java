package model;

public class PaymentRecord {
    private String paymentId;
    private String vehicleNumber;
    private double amount;
    private String paymentTime;
    private String paymentStatus;

    public PaymentRecord(String paymentId, String vehicleNumber, double amount, String paymentTime, String paymentStatus) {
        this.paymentId = paymentId;
        this.vehicleNumber = vehicleNumber;
        this.amount = amount;
        this.paymentTime = paymentTime;
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
}