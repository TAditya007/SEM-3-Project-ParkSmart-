package model;

public class FeeCalculator {
    private double hourlyRate;

    public FeeCalculator(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double calculateFee(int hoursParked) {
        return hoursParked * hourlyRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}