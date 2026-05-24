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
        if (vehicle == null) {
            return;
        }

        vehicleList.add(vehicle);
        avlVehicleTree.insertVehicle(vehicle.getVehicleNumber());
    }

    public void removeVehicle(String vehicleNumber) {
        vehicleList.removeIf(vehicle -> vehicle.getVehicleNumber().equalsIgnoreCase(vehicleNumber));
        avlVehicleTree.deleteVehicle(vehicleNumber);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleList;
    }

    public Vehicle getVehicleByNumber(String vehicleNumber) {
        for (Vehicle vehicle : vehicleList) {
            if (vehicle.getVehicleNumber().equalsIgnoreCase(vehicleNumber)) {
                return vehicle;
            }
        }
        return null;
    }

    public List<Vehicle> searchVehiclesInHistory(String vehicleNumberPart) {
        List<Vehicle> matched = new ArrayList<>();
        if (vehicleNumberPart == null || vehicleNumberPart.trim().isEmpty()) {
            return matched;
        }

        String query = vehicleNumberPart.trim().toUpperCase();
        for (Vehicle vehicle : vehicleList) {
            if (vehicle.getVehicleNumber() != null &&
                    vehicle.getVehicleNumber().toUpperCase().contains(query)) {
                matched.add(vehicle);
            }
        }
        return matched;
    }

    public List<Vehicle> getVehiclesBySlot(String slotDisplayId) {
        List<Vehicle> matched = new ArrayList<>();
        for (Vehicle vehicle : vehicleList) {
            if (vehicle.getSlotId() != null && vehicle.getSlotId().equalsIgnoreCase(slotDisplayId)) {
                matched.add(vehicle);
            } else if (vehicle.getSlotVisitHistory() != null) {
                for (String visitedSlot : vehicle.getSlotVisitHistory()) {
                    if (visitedSlot != null && visitedSlot.equalsIgnoreCase(slotDisplayId)) {
                        matched.add(vehicle);
                        break;
                    }
                }
            }
        }
        return matched;
    }

    public Vehicle getCurrentVehicleInSlot(String slotDisplayId) {
        for (Vehicle vehicle : vehicleList) {
            boolean active = "Parked".equalsIgnoreCase(vehicle.getStatus())
                    || "Charging".equalsIgnoreCase(vehicle.getStatus());

            if (active && vehicle.getSlotId() != null && vehicle.getSlotId().equalsIgnoreCase(slotDisplayId)) {
                return vehicle;
            }
        }
        return null;
    }

    public Vehicle getLastVehicleForSlot(String slotDisplayId) {
        Vehicle latest = null;

        for (Vehicle vehicle : vehicleList) {
            boolean matched = false;

            if (vehicle.getSlotId() != null && vehicle.getSlotId().equalsIgnoreCase(slotDisplayId)) {
                matched = true;
            } else if (vehicle.getSlotVisitHistory() != null) {
                for (String visitedSlot : vehicle.getSlotVisitHistory()) {
                    if (visitedSlot != null && visitedSlot.equalsIgnoreCase(slotDisplayId)) {
                        matched = true;
                        break;
                    }
                }
            }

            if (matched) {
                latest = vehicle;
            }
        }

        return latest;
    }

    public List<String> getVehicleNumbersForSlotHistory(String slotDisplayId) {
        List<String> vehicleNumbers = new ArrayList<>();

        for (Vehicle vehicle : vehicleList) {
            boolean matched = false;

            if (vehicle.getSlotId() != null && vehicle.getSlotId().equalsIgnoreCase(slotDisplayId)) {
                matched = true;
            } else if (vehicle.getSlotVisitHistory() != null) {
                for (String visitedSlot : vehicle.getSlotVisitHistory()) {
                    if (visitedSlot != null && visitedSlot.equalsIgnoreCase(slotDisplayId)) {
                        matched = true;
                        break;
                    }
                }
            }

            if (matched && !vehicleNumbers.contains(vehicle.getVehicleNumber())) {
                vehicleNumbers.add(vehicle.getVehicleNumber());
            }
        }

        return vehicleNumbers;
    }

    public List<String> getSlotHistoryForVehicle(String vehicleNumber) {
        Vehicle vehicle = getVehicleByNumber(vehicleNumber);
        if (vehicle == null || vehicle.getSlotVisitHistory() == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(vehicle.getSlotVisitHistory());
    }

    public List<String> getEventHistoryForVehicle(String vehicleNumber) {
        Vehicle vehicle = getVehicleByNumber(vehicleNumber);
        if (vehicle == null || vehicle.getHistory() == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(vehicle.getHistory());
    }

    public String getCurrentSlotForVehicle(String vehicleNumber) {
        Vehicle vehicle = getVehicleByNumber(vehicleNumber);
        if (vehicle == null) {
            return "-";
        }

        boolean active = "Parked".equalsIgnoreCase(vehicle.getStatus())
                || "Charging".equalsIgnoreCase(vehicle.getStatus());

        return active && vehicle.getSlotId() != null && !vehicle.getSlotId().trim().isEmpty()
                ? vehicle.getSlotId()
                : "-";
    }

    public void markVehicleExit(String vehicleNumber, String exitTime) {
        Vehicle vehicle = getVehicleByNumber(vehicleNumber);
        if (vehicle == null) {
            return;
        }

        vehicle.setExitTime(exitTime);
        vehicle.setStatus("Exited");
        vehicle.addHistory(exitTime + " - Vehicle exited from " + vehicle.getSlotId());
    }

    public void assignVehicleToSlot(String vehicleNumber, String slotDisplayId, String entryTime, String status) {
        Vehicle vehicle = getVehicleByNumber(vehicleNumber);
        if (vehicle == null) {
            return;
        }

        vehicle.setSlotId(slotDisplayId);
        vehicle.setEntryTime(entryTime);
        vehicle.setExitTime("--");
        vehicle.setStatus(status);

        if (vehicle.getSlotVisitHistory() == null || !vehicle.getSlotVisitHistory().contains(slotDisplayId)) {
            vehicle.addSlotVisit(slotDisplayId);
        }

        vehicle.addHistory(entryTime + " - Vehicle assigned to " + slotDisplayId + " [" + status + "]");
    }

    public List<Vehicle> getActiveVehicles() {
        List<Vehicle> activeVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicleList) {
            if ("Parked".equalsIgnoreCase(vehicle.getStatus())
                    || "Charging".equalsIgnoreCase(vehicle.getStatus())) {
                activeVehicles.add(vehicle);
            }
        }
        return activeVehicles;
    }

    public List<Vehicle> getExitedVehicles() {
        List<Vehicle> exitedVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicleList) {
            if ("Exited".equalsIgnoreCase(vehicle.getStatus())) {
                exitedVehicles.add(vehicle);
            }
        }
        return exitedVehicles;
    }

    private void loadSampleVehicles() {
        if (!vehicleList.isEmpty()) {
            return;
        }

        Vehicle v1 = new Vehicle("TS09AB1234", "Rahul Verma", "Car", "ALPHA-A12", "18-05-2026 08:42 AM", "--", "Parked");
        v1.addHistory("17-02-2026 10:15 AM - Earlier visit at ALPHA-A12");
        v1.addHistory("20-03-2026 01:25 PM - Earlier visit at BETA-A07");
        v1.addHistory("18-05-2026 08:42 AM - Vehicle parked at ALPHA-A12");
        v1.addSlotVisit("ALPHA-A12");
        v1.addSlotVisit("BETA-A07");
        addVehicle(v1);

        Vehicle v2 = new Vehicle("TS10CD5678", "Sneha Reddy", "Bike", "BETA-B08", "18-05-2026 08:55 AM", "--", "Parked");
        v2.addHistory("11-01-2026 09:10 AM - Earlier visit at BETA-B08");
        v2.addHistory("06-04-2026 05:35 PM - Earlier visit at EPSILON-B14");
        v2.addHistory("18-05-2026 08:55 AM - Vehicle parked at BETA-B08");
        v2.addSlotVisit("BETA-B08");
        v2.addSlotVisit("EPSILON-B14");
        addVehicle(v2);

        Vehicle v3 = new Vehicle("TS11EF9012", "Arjun Rao", "EV", "GAMMA-EV03", "18-05-2026 09:03 AM", "--", "Charging");
        v3.addHistory("14-02-2026 02:10 PM - Charging session at GAMMA-EV03");
        v3.addHistory("22-04-2026 12:40 PM - Charging session at EPSILON-EV05");
        v3.addHistory("18-05-2026 09:03 AM - Charging started at GAMMA-EV03");
        v3.addSlotVisit("GAMMA-EV03");
        v3.addSlotVisit("EPSILON-EV05");
        addVehicle(v3);

        Vehicle v4 = new Vehicle("TS12GH3456", "Meera Nair", "Car", "DELTA-A21", "18-05-2026 09:11 AM", "--", "Parked");
        v4.addHistory("19-03-2026 08:20 AM - Earlier visit at DELTA-A21");
        v4.addHistory("18-05-2026 09:11 AM - Vehicle parked at DELTA-A21");
        v4.addSlotVisit("DELTA-A21");
        addVehicle(v4);

        Vehicle v5 = new Vehicle("TS13JK7890", "Kiran Patel", "Bike", "EPSILON-B14", "18-05-2026 09:18 AM", "--", "Parked");
        v5.addHistory("15-01-2026 06:00 PM - Earlier visit at EPSILON-B14");
        v5.addHistory("08-05-2026 10:05 AM - Earlier visit at ALPHA-B07");
        v5.addHistory("18-05-2026 09:18 AM - Vehicle parked at EPSILON-B14");
        v5.addSlotVisit("EPSILON-B14");
        v5.addSlotVisit("ALPHA-B07");
        addVehicle(v5);

        Vehicle v6 = new Vehicle("TS14LM2468", "Akhil Sharma", "Car", "ALPHA-B07", "17-05-2026 06:10 PM", "17-05-2026 10:25 PM", "Exited");
        v6.addHistory("17-05-2026 06:10 PM - Vehicle entered ALPHA-B07");
        v6.addHistory("17-05-2026 10:25 PM - Vehicle exited from ALPHA-B07");
        v6.addHistory("02-02-2026 11:45 AM - Earlier visit at ALPHA-B07");
        v6.addSlotVisit("ALPHA-B07");
        addVehicle(v6);

        Vehicle v7 = new Vehicle("TS15NP1357", "Divya Menon", "EV", "EPSILON-EV05", "16-05-2026 01:10 PM", "16-05-2026 03:40 PM", "Exited");
        v7.addHistory("16-05-2026 01:10 PM - Charging started at EPSILON-EV05");
        v7.addHistory("16-05-2026 03:40 PM - Vehicle exited from EPSILON-EV05");
        v7.addHistory("09-03-2026 12:25 PM - Earlier charging session at EPSILON-EV05");
        v7.addSlotVisit("EPSILON-EV05");
        addVehicle(v7);

        Vehicle v8 = new Vehicle("TS16QR2244", "Sandeep Yadav", "SUV", "BETA-A03", "15-05-2026 11:05 AM", "15-05-2026 02:15 PM", "Exited");
        v8.addHistory("15-05-2026 11:05 AM - Vehicle entered BETA-A03");
        v8.addHistory("15-05-2026 02:15 PM - Vehicle exited from BETA-A03");
        v8.addHistory("28-01-2026 09:55 AM - Earlier visit at BETA-A03");
        v8.addSlotVisit("BETA-A03");
        addVehicle(v8);

        Vehicle v9 = new Vehicle("TS17ST7788", "Pooja Singh", "Bike", "DELTA-B11", "14-05-2026 04:30 PM", "14-05-2026 08:00 PM", "Exited");
        v9.addHistory("14-05-2026 04:30 PM - Vehicle entered DELTA-B11");
        v9.addHistory("14-05-2026 08:00 PM - Vehicle exited from DELTA-B11");
        v9.addHistory("10-04-2026 07:20 PM - Earlier visit at DELTA-B11");
        v9.addSlotVisit("DELTA-B11");
        addVehicle(v9);

        Vehicle v10 = new Vehicle("TS18UV9900", "Nikhil Das", "Car", "GAMMA-A09", "13-05-2026 09:20 AM", "13-05-2026 12:55 PM", "Exited");
        v10.addHistory("13-05-2026 09:20 AM - Vehicle entered GAMMA-A09");
        v10.addHistory("13-05-2026 12:55 PM - Vehicle exited from GAMMA-A09");
        v10.addHistory("03-03-2026 08:50 AM - Earlier visit at GAMMA-A09");
        v10.addSlotVisit("GAMMA-A09");
        addVehicle(v10);
    }
}