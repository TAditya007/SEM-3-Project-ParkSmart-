package service;

import dsa.AVLVehicleTree;
import model.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class VehicleService {
    private AVLVehicleTree avlVehicleTree;
    private List<Vehicle> vehicleList;

    public VehicleService() {
        avlVehicleTree = new AVLVehicleTree();
        vehicleList = new ArrayList<>();
        loadSampleVehicles();
    }

    public void addVehicle(Vehicle vehicle) {
        vehicleList.add(vehicle);
        avlVehicleTree.insertVehicle(vehicle.getVehicleNumber());
    }

    public void removeVehicle(String vehicleNumber) {
        vehicleList.removeIf(vehicle -> vehicle.getVehicleNumber().equals(vehicleNumber));
        avlVehicleTree.deleteVehicle(vehicleNumber);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleList;
    }

    private void loadSampleVehicles() {
        if (!vehicleList.isEmpty()) {
            return;
        }
    
        addVehicle(new Vehicle("TS09AB1234", "Rahul Verma", "Car", "ALPHA-A12", "18-05-2026 08:42 AM", "--", "Parked"));
        addVehicle(new Vehicle("TS10CD5678", "Sneha Reddy", "Bike", "BETA-B08", "18-05-2026 08:55 AM", "--", "Parked"));
        addVehicle(new Vehicle("TS11EF9012", "Arjun Rao", "EV", "GAMMA-EV03", "18-05-2026 09:03 AM", "--", "Charging"));
        addVehicle(new Vehicle("TS12GH3456", "Meera Nair", "Car", "DELTA-A21", "18-05-2026 09:11 AM", "--", "Parked"));
        addVehicle(new Vehicle("TS13JK7890", "Kiran Patel", "Bike", "EPSILON-B14", "18-05-2026 09:18 AM", "--", "Parked"));
    
        addVehicle(new Vehicle("TS14LM2468", "Akhil Sharma", "Car", "ALPHA-B07", "17-05-2026 06:10 PM", "17-05-2026 10:25 PM", "Exited"));
        addVehicle(new Vehicle("TS15NP1357", "Divya Menon", "EV", "EPSILON-EV05", "16-05-2026 01:10 PM", "16-05-2026 03:40 PM", "Exited"));
        addVehicle(new Vehicle("TS16QR2244", "Sandeep Yadav", "SUV", "BETA-A03", "15-05-2026 11:05 AM", "15-05-2026 02:15 PM", "Exited"));
        addVehicle(new Vehicle("TS17ST7788", "Pooja Singh", "Bike", "DELTA-B11", "14-05-2026 04:30 PM", "14-05-2026 08:00 PM", "Exited"));
        addVehicle(new Vehicle("TS18UV9900", "Nikhil Das", "Car", "GAMMA-A09", "13-05-2026 09:20 AM", "13-05-2026 12:55 PM", "Exited"));
    }
}