package dsa;

import java.util.*;

public class ShortestPathAlgorithms {

    public Map<String, Integer> dijkstra(Map<String, List<ParkingGraph.Edge>> graph, String source) {
        Map<String, Integer> distance = new HashMap<>();
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (String node : graph.keySet()) {
            distance.put(node, Integer.MAX_VALUE);
        }

        distance.put(source, 0);
        pq.add(new AbstractMap.SimpleEntry<>(source, 0));

        while (!pq.isEmpty()) {
            Map.Entry<String, Integer> current = pq.poll();
            String currentNode = current.getKey();
            int currentDistance = current.getValue();

            if (currentDistance > distance.get(currentNode)) continue;

            for (ParkingGraph.Edge edge : graph.getOrDefault(currentNode, new ArrayList<>())) {
                int newDistance = currentDistance + edge.distance;
                if (newDistance < distance.get(edge.destination)) {
                    distance.put(edge.destination, newDistance);
                    pq.add(new AbstractMap.SimpleEntry<>(edge.destination, newDistance));
                }
            }
        }

        return distance;
    }

    public List<String> getShortestPath(ParkingGraph parkingGraph, String source, String destination) {
        Map<String, List<ParkingGraph.Edge>> graph = parkingGraph.getGraph();
        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (String node : graph.keySet()) {
            distance.put(node, Integer.MAX_VALUE);
        }

        distance.put(source, 0);
        pq.add(new AbstractMap.SimpleEntry<>(source, 0));

        while (!pq.isEmpty()) {
            Map.Entry<String, Integer> current = pq.poll();
            String currentNode = current.getKey();
            int currentDistance = current.getValue();

            if (currentDistance > distance.get(currentNode)) continue;

            for (ParkingGraph.Edge edge : graph.getOrDefault(currentNode, new ArrayList<>())) {
                int newDistance = currentDistance + edge.distance;
                if (newDistance < distance.get(edge.destination)) {
                    distance.put(edge.destination, newDistance);
                    previous.put(edge.destination, currentNode);
                    pq.add(new AbstractMap.SimpleEntry<>(edge.destination, newDistance));
                }
            }
        }

        List<String> path = new ArrayList<>();
        if (!distance.containsKey(destination) || distance.get(destination) == Integer.MAX_VALUE) {
            return path;
        }

        String step = destination;
        while (step != null) {
            path.add(step);
            step = previous.get(step);
        }

        Collections.reverse(path);
        return path;
    }

    public int getShortestDistance(ParkingGraph parkingGraph, String source, String destination) {
        Map<String, Integer> distance = dijkstra(parkingGraph.getGraph(), source);
        return distance.getOrDefault(destination, Integer.MAX_VALUE);
    }
}