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
    private int nextSlotId = 1;

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

        addBlockSlots("ALPHA");
        addBlockSlots("BETA");
        addBlockSlots("GAMMA");
        addBlockSlots("DELTA");
        addBlockSlots("EPSILON");
    }

    private void addBlockSlots(String blockName) {
        addSlotGroup(blockName, "A", 30, "Compact");
        addSlotGroup(blockName, "B", 50, "Bike");
        addSlotGroup(blockName, "EV", 10, "EV");
    }

    private void addSlotGroup(String blockName, String slotGroup, int count, String slotType) {
        for (int i = 1; i <= count; i++) {
            String slotNumber = slotGroup + i;
            String status = getDefaultStatus(slotGroup, i);
            boolean available = "Available".equalsIgnoreCase(status);

            ParkingSlot slot = new ParkingSlot(
                    nextSlotId++,
                    blockName,
                    slotGroup,
                    slotNumber,
                    slotType,
                    status,
                    available
            );

            applyDemoData(slot, i);
            addSlot(slot);
        }
    }

    private String getDefaultStatus(String slotGroup, int index) {
        if ("EV".equalsIgnoreCase(slotGroup)) {
            if (index % 4 == 0) {
                return "Charging";
            } else if (index % 3 == 0) {
                return "Occupied";
            } else {
                return "Available";
            }
        } else {
            if (index % 5 == 0 || index % 7 == 0) {
                return "Occupied";
            } else {
                return "Available";
            }
        }
    }

    private void applyDemoData(ParkingSlot slot, int index) {
        if ("Occupied".equalsIgnoreCase(slot.getStatus()) || "Charging".equalsIgnoreCase(slot.getStatus())) {
            String vehicleNumber = generateVehicleNumber(index, slot.getBlockName(), slot.getSlotGroup());
            slot.assignVehicle(vehicleNumber);
            slot.setLastEntryTime(generateEntryTime(index));
            slot.setLastExitTime("--");
            slot.addHistory("Vehicle entered at " + slot.getLastEntryTime());
        } else {
            slot.setLastEntryTime(generatePastEntryTime(index));
            slot.setLastExitTime(generatePastExitTime(index));
            slot.addHistory("Last vehicle exited at " + slot.getLastExitTime());
        }

        slot.setNotes(slot.getBlockName() + " " + slot.getSlotGroup() + " section slot for " + slot.getSlotType() + " vehicles.");
    }

    private String generateVehicleNumber(int index, String blockName, String slotGroup) {
        int blockCode = Math.abs(blockName.hashCode()) % 90 + 10;
        int seriesCode = Math.abs(slotGroup.hashCode()) % 9000 + 1000;
        return "TS" + blockCode + "AB" + seriesCode;
    }

    private String generateEntryTime(int index) {
        int day = 10 + (index % 9);
        int hour = 8 + (index % 6);
        int minute = 10 + ((index * 7) % 50);
        String amPm = hour >= 12 ? "PM" : "AM";
        int displayHour = hour > 12 ? hour - 12 : hour;
        if (displayHour == 0) displayHour = 12;
        return String.format("%02d-05-2026 %02d:%02d %s", day, displayHour, minute, amPm);
    }

    private String generatePastEntryTime(int index) {
        int day = 1 + (index % 20);
        int hour = 7 + (index % 8);
        int minute = 5 + ((index * 3) % 50);
        String amPm = hour >= 12 ? "PM" : "AM";
        int displayHour = hour > 12 ? hour - 12 : hour;
        if (displayHour == 0) displayHour = 12;
        return String.format("%02d-05-2026 %02d:%02d %s", day, displayHour, minute, amPm);
    }

    private String generatePastExitTime(int index) {
        int day = 1 + (index % 20);
        int hour = 9 + (index % 8);
        int minute = 15 + ((index * 5) % 40);
        String amPm = hour >= 12 ? "PM" : "AM";
        int displayHour = hour > 12 ? hour - 12 : hour;
        if (displayHour == 0) displayHour = 12;
        return String.format("%02d-05-2026 %02d:%02d %s", day, displayHour, minute, amPm);
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

    public ParkingSlot getSlotByDisplayId(String displaySlotId) {
        for (ParkingSlot slot : slotList) {
            if (slot.getDisplaySlotId().equalsIgnoreCase(displaySlotId)) {
                return slot;
            }
        }
        return null;
    }

    public List<ParkingSlot> getSlotsByBlockAndGroup(String blockName, String slotGroup) {
        List<ParkingSlot> filtered = new ArrayList<>();
        for (ParkingSlot slot : slotList) {
            if (slot.getBlockName().equalsIgnoreCase(blockName)
                    && slot.getSlotGroup().equalsIgnoreCase(slotGroup)) {
                filtered.add(slot);
            }
        }
        return filtered;
    }

    public boolean parkVehicleInFirstAvailableSlot(String vehicleNumber) {
        for (ParkingSlot slot : slotList) {
            if (slot.isAvailable()) {
                slot.assignVehicle(vehicleNumber);
                slot.setLastEntryTime(generateEntryTime(slot.getSlotId()));
                slot.setLastExitTime("--");
                slot.addHistory("Vehicle " + vehicleNumber + " parked at " + slot.getLastEntryTime());
                return true;
            }
        }
        return false;
    }

    public boolean releaseSlotByVehicleNumber(String vehicleNumber) {
        for (ParkingSlot slot : slotList) {
            if (!slot.isAvailable() && vehicleNumber.equals(slot.getOccupiedVehicleNumber())) {
                slot.releaseSlot();
                slot.setLastExitTime(generatePastExitTime(slot.getSlotId()));
                slot.addHistory("Vehicle " + vehicleNumber + " exited at " + slot.getLastExitTime());
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