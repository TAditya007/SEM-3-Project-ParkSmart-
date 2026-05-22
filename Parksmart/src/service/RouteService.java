package service;

import java.util.*;

public class RouteService {

    private final Map<String, List<Edge>> graph = new LinkedHashMap<>();

    public RouteService() {
        buildParkingGraph();
    }

    private void buildParkingGraph() {
        addBidirectional("MAIN_ENTRANCE", "ALPHA", 2);
        addBidirectional("ALPHA", "BETA", 3);
        addBidirectional("BETA", "GAMMA", 3);
        addBidirectional("GAMMA", "DELTA", 3);
        addBidirectional("DELTA", "EPSILON", 3);
        addBidirectional("EPSILON", "ALPHA", 3);
        addBidirectional("DELTA", "MAIN_EXIT", 2);

        buildBlock("Alpha", "ALPHA");
        buildBlock("Beta", "BETA");
        buildBlock("Gamma", "GAMMA");
        buildBlock("Delta", "DELTA");
        buildBlock("Epsilon", "EPSILON");
    }

    private void buildBlock(String blockName, String hubName) {
        for (int i = 1; i <= 50; i++) {
            addBidirectional(hubName, blockName + " A" + i, 1);
        }

        for (int i = 1; i <= 100; i++) {
            addBidirectional(hubName, blockName + " B" + i, 1);
        }

        for (int i = 1; i <= 30; i++) {
            addBidirectional(hubName, blockName + " EV" + i, 1);
        }
    }

    private void addBidirectional(String from, String to, int weight) {
        graph.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, weight));
        graph.computeIfAbsent(to, k -> new ArrayList<>()).add(new Edge(from, weight));
    }

    public List<String> getNodeNames() {
        return new ArrayList<>(graph.keySet());
    }

    public List<String> findShortestPath(String source, String destination) {
        if (!graph.containsKey(source) || !graph.containsKey(destination)) {
            return Collections.emptyList();
        }

        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

        for (String node : graph.keySet()) {
            dist.put(node, Integer.MAX_VALUE);
        }

        dist.put(source, 0);
        pq.add(new Node(source, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            if (current.distance > dist.get(current.name)) continue;
            if (current.name.equals(destination)) break;

            for (Edge edge : graph.getOrDefault(current.name, Collections.emptyList())) {
                int newDist = dist.get(current.name) + edge.weight;
                if (newDist < dist.get(edge.to)) {
                    dist.put(edge.to, newDist);
                    prev.put(edge.to, current.name);
                    pq.add(new Node(edge.to, newDist));
                }
            }
        }

        if (!source.equals(destination) && !prev.containsKey(destination)) {
            return Collections.emptyList();
        }

        LinkedList<String> path = new LinkedList<>();
        String at = destination;
        path.addFirst(at);

        while (prev.containsKey(at)) {
            at = prev.get(at);
            path.addFirst(at);
        }

        return path;
    }

    public int getShortestDistance(String source, String destination) {
        List<String> path = findShortestPath(source, destination);
        if (path.isEmpty()) return -1;

        int total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);
            for (Edge edge : graph.getOrDefault(from, Collections.emptyList())) {
                if (edge.to.equals(to)) {
                    total += edge.weight;
                    break;
                }
            }
        }
        return total;
    }

    private static class Edge {
        String to;
        int weight;

        Edge(String to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    private static class Node {
        String name;
        int distance;

        Node(String name, int distance) {
            this.name = name;
            this.distance = distance;
        }
    }
}