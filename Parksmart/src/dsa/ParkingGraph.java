package dsa;

import java.util.*;

public class ParkingGraph {
    private final Map<String, List<Edge>> adjacencyList;

    public ParkingGraph() {
        adjacencyList = new HashMap<>();
    }

    public void addZone(String zone) {
        adjacencyList.putIfAbsent(zone, new ArrayList<>());
    }

    public void connectZones(String source, String destination, int distance) {
        addZone(source);
        addZone(destination);
        adjacencyList.get(source).add(new Edge(destination, distance));
        adjacencyList.get(destination).add(new Edge(source, distance));
    }

    public Map<String, List<Edge>> getGraph() {
        return adjacencyList;
    }

    public Set<String> getZones() {
        return adjacencyList.keySet();
    }

    public static class Edge {
        public String destination;
        public int distance;

        public Edge(String destination, int distance) {
            this.destination = destination;
            this.distance = distance;
        }
    }
}