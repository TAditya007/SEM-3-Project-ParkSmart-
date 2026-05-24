package service;

import dsa.SegmentTree;
import model.Reservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private SegmentTree segmentTree;
    private List<Reservation> reservationList;

    public ReservationService() {
        segmentTree = new SegmentTree();
        reservationList = new ArrayList<>();
        loadDefaultReservations();
    }

    private void loadDefaultReservations() {
        if (!reservationList.isEmpty()) {
            return;
        }

        addReservation(new Reservation("R001", "TS09AB1234", "ALPHA-A12", "18-05-2026 08:45 AM", "Confirmed"));
        addReservation(new Reservation("R002", "TS10CD5678", "BETA-B08", "18-05-2026 09:10 AM", "Pending"));
        addReservation(new Reservation("R003", "TS11EF9012", "GAMMA-EV03", "18-05-2026 09:20 AM", "Confirmed"));
        addReservation(new Reservation("R004", "TS12GH3456", "DELTA-A21", "17-05-2026 06:35 PM", "Confirmed"));
        addReservation(new Reservation("R005", "TS13JK7890", "EPSILON-B14", "17-05-2026 07:05 PM", "Cancelled"));
        addReservation(new Reservation("R006", "TS14LM2468", "ALPHA-B07", "16-05-2026 01:15 PM", "Pending"));
        addReservation(new Reservation("R007", "TS15NP1357", "EPSILON-EV05", "16-05-2026 02:00 PM", "Confirmed"));
        addReservation(new Reservation("R008", "TS16QR2244", "BETA-A03", "15-05-2026 11:25 AM", "Cancelled"));

        addReservation(new Reservation("R009", "TS18UV9900", "GAMMA-A09", "14-04-2026 10:05 AM", "Confirmed"));
        addReservation(new Reservation("R010", "TS17ST7788", "DELTA-B11", "28-03-2026 05:40 PM", "Cancelled"));
        addReservation(new Reservation("R011", "TS09AB1234", "BETA-A07", "12-02-2026 09:25 AM", "Confirmed"));
        addReservation(new Reservation("R012", "TS15NP1357", "GAMMA-EV03", "06-01-2026 12:10 PM", "Confirmed"));
    }

    public void addReservation(Reservation reservation) {
        reservationList.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationList;
    }

    public Reservation getReservationById(String reservationId) {
        for (Reservation reservation : reservationList) {
            if (reservation.getReservationId().equalsIgnoreCase(reservationId)) {
                return reservation;
            }
        }
        return null;
    }

    public List<Reservation> getReservationsByVehicle(String vehicleNumber) {
        List<Reservation> matched = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            if (reservation.getVehicleNumber() != null &&
                    reservation.getVehicleNumber().equalsIgnoreCase(vehicleNumber)) {
                matched.add(reservation);
            }
        }
        return matched;
    }

    public List<Reservation> getReservationsBySlot(String slotId) {
        List<Reservation> matched = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            if (reservation.getSlotId() != null &&
                    reservation.getSlotId().equalsIgnoreCase(slotId)) {
                matched.add(reservation);
            }
        }
        return matched;
    }

    public List<Reservation> getReservationsByVehicleAndSlot(String vehicleNumber, String slotId) {
        List<Reservation> matched = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            boolean vehicleMatch = reservation.getVehicleNumber() != null &&
                    reservation.getVehicleNumber().equalsIgnoreCase(vehicleNumber);
            boolean slotMatch = reservation.getSlotId() != null &&
                    reservation.getSlotId().equalsIgnoreCase(slotId);

            if (vehicleMatch && slotMatch) {
                matched.add(reservation);
            }
        }
        return matched;
    }

    public void cancelReservation(String reservationId) {
        for (Reservation reservation : reservationList) {
            if (reservation.getReservationId().equalsIgnoreCase(reservationId)) {
                reservation.setStatus("Cancelled");
                break;
            }
        }
    }

    public int getConfirmedReservationCount() {
        int count = 0;
        for (Reservation reservation : reservationList) {
            if ("Confirmed".equalsIgnoreCase(reservation.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public int getPendingReservationCount() {
        int count = 0;
        for (Reservation reservation : reservationList) {
            if ("Pending".equalsIgnoreCase(reservation.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public int getCancelledReservationCount() {
        int count = 0;
        for (Reservation reservation : reservationList) {
            if ("Cancelled".equalsIgnoreCase(reservation.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public int getReservationCountInRange(int left, int right) {
        return segmentTree.rangeQuery(left, right);
    }
}