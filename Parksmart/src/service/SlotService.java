package service;

import dsa.BSTSlots;
import dsa.FenwickTree;
import model.ParkingSlot;

import java.util.ArrayList;
import java.util.List;

public class SlotService {
    private BSTSlots bstSlots;
    private FenwickTree fenwickTree;
    private List<ParkingSlot> slotList;

    public SlotService() {
        bstSlots = new BSTSlots();
        fenwickTree = new FenwickTree();
        slotList = new ArrayList<>();
        loadDefaultSlots();
    }

    private void loadDefaultSlots() {
        if (!slotList.isEmpty()) {
            return;
        }

        ParkingSlot s1 = new ParkingSlot(1, "ALPHA-A12", "ALPHA", "A", "Compact", "Occupied", false);
        s1.assignVehicle("TS09AB1234");
        s1.setLastEntryTime("18-05-2026 08:42 AM");
        s1.setLastExitTime("--");
        addSlot(s1);

        ParkingSlot s2 = new ParkingSlot(2, "ALPHA-B07", "ALPHA", "B", "SUV", "Occupied", false);
        s2.assignVehicle("TS09CD5678");
        s2.setLastEntryTime("18-05-2026 09:05 AM");
        s2.setLastExitTime("--");
        addSlot(s2);

        ParkingSlot s3 = new ParkingSlot(3, "BETA-A03", "BETA", "A", "Compact", "Available", true);
        s3.setLastEntryTime("17-05-2026 06:20 PM");
        s3.setLastExitTime("17-05-2026 10:10 PM");
        addSlot(s3);

        ParkingSlot s4 = new ParkingSlot(4, "BETA-B08", "BETA", "B", "Bike", "Occupied", false);
        s4.assignVehicle("TS10KL5678");
        s4.setLastEntryTime("18-05-2026 08:55 AM");
        s4.setLastExitTime("--");
        addSlot(s4);

        ParkingSlot s5 = new ParkingSlot(5, "GAMMA-EV03", "GAMMA", "EV", "EV", "Charging", false);
        s5.assignVehicle("TS11MN9012");
        s5.setLastEntryTime("18-05-2026 09:03 AM");
        s5.setLastExitTime("--");
        addSlot(s5);

        ParkingSlot s6 = new ParkingSlot(6, "DELTA-A21", "DELTA", "A", "Compact", "Occupied", false);
        s6.assignVehicle("TS12PQ3456");
        s6.setLastEntryTime("18-05-2026 09:11 AM");
        s6.setLastExitTime("--");
        addSlot(s6);

        ParkingSlot s7 = new ParkingSlot(7, "EPSILON-B14", "EPSILON", "B", "Bike", "Occupied", false);
        s7.assignVehicle("TS13RS7890");
        s7.setLastEntryTime("18-05-2026 09:18 AM");
        s7.setLastExitTime("--");
        addSlot(s7);

        ParkingSlot s8 = new ParkingSlot(8, "EPSILON-EV05", "EPSILON", "EV", "EV", "Available", true);
        s8.setLastEntryTime("16-05-2026 01:10 PM");
        s8.setLastExitTime("16-05-2026 03:40 PM");
        addSlot(s8);
    }

    public void addSlot(ParkingSlot slot) {
        slotList.add(slot);
        bstSlots.insertSlot(slot.getSlotId());
    }

    public List<ParkingSlot> getAllSlots() {
        return slotList;
    }

    public int getAvailableSlotCount() {
        int count = 0;
        for (ParkingSlot slot : slotList) {
            if (slot.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    public boolean parkVehicleInFirstAvailableSlot(String vehicleNumber) {
        for (ParkingSlot slot : slotList) {
            if (slot.isAvailable()) {
                slot.assignVehicle(vehicleNumber);
                return true;
            }
        }
        return false;
    }

    public boolean releaseSlotByVehicleNumber(String vehicleNumber) {
        for (ParkingSlot slot : slotList) {
            if (!slot.isAvailable() && vehicleNumber.equals(slot.getOccupiedVehicleNumber())) {
                slot.releaseSlot();
                return true;
            }
        }
        return false;
    }

    public void updateRevenue(int index, int value) {
        fenwickTree.update(index, value);
    }

    public int getRevenueTillIndex(int index) {
        return fenwickTree.query(index);
    }
}