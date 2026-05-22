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
    }

    public void addReservation(Reservation reservation) {
        reservationList.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationList;
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

    public int getReservationCountInRange(int left, int right) {
        return segmentTree.rangeQuery(left, right);
    }
}