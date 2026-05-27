import java.util.*;

public class ParkSmartBellmanFord{

    static class Edge {
        int u, v, weight;
        Edge(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.weight = w;
        }
    }

    static int[][] bellmanFordWithPath(int n, List<Edge> edges, int source) {
        int[] dist   = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        for (int iter = 0; iter < n - 1; iter++) {
            boolean changed = false;
            for (Edge e : edges) {
                if (dist[e.u] != Integer.MAX_VALUE &&
                    dist[e.u] + e.weight < dist[e.v]) {

                    dist[e.v] = dist[e.u] + e.weight;
                    parent[e.v] = e.u;
                    changed = true;
                }
            }
            System.out.println("Iteration " + (iter + 1) +
                    " dist = " + Arrays.toString(dist));
            if (!changed) break;
        }

        for (Edge e : edges) {
            if (dist[e.u] != Integer.MAX_VALUE &&
                dist[e.u] + e.weight < dist[e.v]) {
                throw new RuntimeException("negative cycle");
            }
        }

        return new int[][] { dist, parent };
    }

    static List<Integer> reconstructPath(int[] parent, int target) {
        LinkedList<Integer> path = new LinkedList<>();
        int cur = target;
        while (cur != -1) {
            path.addFirst(cur);
            cur = parent[cur];
        }
        return path;
    }

    static String nameOf(int idx) {
        switch (idx) {
            case 0: return "G";   // GATE
            case 1: return "R";   // RAMP
            case 2: return "M";   // MALL
            case 3: return "B1";  // BLOCK1
            case 4: return "B2";  // BLOCK2
            case 5: return "F";   // FARLOT
            case 6: return "E";   // EVHUB
            default: return "?";
        }
    }

    public static void main(String[] args) {
        int n = 7; // G,R,M,B1,B2,F,E

        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, 6));   // G -> R
        edges.add(new Edge(0, 2, 10));  // G -> M
        edges.add(new Edge(1, 2, 3));   // R -> M
        edges.add(new Edge(1, 3, 5));   // R -> B1
        edges.add(new Edge(1, 4, -4));  // R -> B2 (negative)
        edges.add(new Edge(2, 3, 4));   // M -> B1
        edges.add(new Edge(3, 5, -2));  // B1 -> F (negative)
        edges.add(new Edge(3, 6, 11));  // B1 -> E
        edges.add(new Edge(4, 6, 6));   // B2 -> E
        edges.add(new Edge(5, 6, 5));   // F -> E

        int source = 0; // G
        int target = 6; // E

        int[][] result = bellmanFordWithPath(n, edges, source);
        int[] dist   = result[0];
        int[] parent = result[1];

        System.out.println("Final dist[] = " + Arrays.toString(dist));
        System.out.println("Shortest distance G->E = " + dist[target]);

        List<Integer> path = reconstructPath(parent, target);
        System.out.print("Shortest path G->E: ");
        for (int i = 0; i < path.size(); i++) {
            System.out.print(nameOf(path.get(i)));
            if (i + 1 < path.size()) System.out.print(" -> ");
        }
        System.out.println();
    }
}