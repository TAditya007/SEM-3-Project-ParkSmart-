import java.util.*;

public class ParkSmartMST {

    // ----- Graph model -----

    static class Edge implements Comparable<Edge> {
        int u, v, w; // u,v are vertex indices, w = weight

        Edge(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }

        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.w, other.w);
        }

        @Override
        public String toString() {
            return "(" + ParkSmartMST.nameOf(u) + "-" + ParkSmartMST.nameOf(v) + ", " + w + ")";
        }
    }

    static String nameOf(int idx) {
        switch (idx) {
            case 0: return "A";
            case 1: return "B";
            case 2: return "C";
            case 3: return "D";
            case 4: return "E";
        }
        return "?";
    }

    // ----- Disjoint Set (Union-Find) for Kruskal -----

    static class DSU {
        int[] parent, rank;

        DSU(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return false;
            if (rank[ra] < rank[rb]) {
                parent[ra] = rb;
            } else if (rank[ra] > rank[rb]) {
                parent[rb] = ra;
            } else {
                parent[rb] = ra;
                rank[ra]++;
            }
            return true;
        }
    }

    // ----- Build the ParkSmart graph -----

    static List<Edge> buildEdges() {
        List<Edge> edges = new ArrayList<>();

        // Vertices: 0:A, 1:B, 2:C, 3:D, 4:E
        // Edges with costs (lakhs):
        edges.add(new Edge(0, 1, 6));  // A-B
        edges.add(new Edge(0, 2, 10)); // A-C
        edges.add(new Edge(0, 3, 4));  // A-D
        edges.add(new Edge(1, 2, 2));  // B-C
        edges.add(new Edge(1, 3, 8));  // B-D
        edges.add(new Edge(1, 4, 5));  // B-E
        edges.add(new Edge(2, 3, 12)); // C-D
        edges.add(new Edge(2, 4, 3));  // C-E
        edges.add(new Edge(3, 4, 7));  // D-E

        return edges;
    }

    static List<List<Edge>> buildAdjacency(int n, List<Edge> edges) {
        List<List<Edge>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (Edge e : edges) {
            adj.get(e.u).add(new Edge(e.u, e.v, e.w));
            adj.get(e.v).add(new Edge(e.v, e.u, e.w));
        }
        return adj;
    }

    // ----- Kruskal MST -----

    static List<Edge> kruskalMST(int n, List<Edge> edges) {
        Collections.sort(edges); // sort by weight
        DSU dsu = new DSU(n);
        List<Edge> mst = new ArrayList<>();
        int total = 0;

        System.out.println("Kruskal's Algorithm steps:");
        for (Edge e : edges) {
            int u = e.u, v = e.v;
            int ru = dsu.find(u), rv = dsu.find(v);
            System.out.print("Consider edge " + e + " ");
            if (ru != rv) {
                dsu.union(u, v);
                mst.add(e);
                total += e.w;
                System.out.println("-> ADDED");
            } else {
                System.out.println("-> SKIPPED (cycle)");
            }
            if (mst.size() == n - 1) break;
        }
        System.out.println("Kruskal MST edges: " + mst);
        System.out.println("Kruskal MST total cost: " + total + " lakhs\n");
        return mst;
    }

    // ----- Prim MST -----

    static List<Edge> primMST(int n, List<List<Edge>> adj, int start) {
        boolean[] inMST = new boolean[n];
        List<Edge> mst = new ArrayList<>();
        int total = 0;

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.w));

        // Add all edges from start
        inMST[start] = true;
        for (Edge e : adj.get(start)) pq.add(e);

        System.out.println("Prim's Algorithm steps (start at " + nameOf(start) + "):");
        while (!pq.isEmpty() && mst.size() < n - 1) {
            Edge e = pq.poll();
            if (inMST[e.v]) continue; // skip if already inside tree

            // e.u is in MST, e.v is new vertex
            inMST[e.v] = true;
            mst.add(e);
            total += e.w;

            System.out.println("Add edge " + e + " -> MST");

            // push all edges from new vertex
            for (Edge ne : adj.get(e.v)) {
                if (!inMST[ne.v]) pq.add(ne);
            }
        }
        System.out.println("Prim MST edges (start " + nameOf(start) + "): " + mst);
        System.out.println("Prim MST total cost (start " + nameOf(start) + "): " + total + " lakhs\n");
        return mst;
    }

    // ----- MAIN -----

    public static void main(String[] args) {
        int n = 5; // A,B,C,D,E

        List<Edge> edges = buildEdges();
        System.out.println("ParkSmart zones: A,B,C,D,E");
        System.out.println("Candidate edges (unsorted):");
        for (Edge e : edges) System.out.println("  " + e);

        // Kruskal
        System.out.println("\n--- Running Kruskal ---");
        kruskalMST(n, edges);
        // Prim
        List<List<Edge>> adj = buildAdjacency(n, edges);
        System.out.println("--- Running Prim from A ---");
        primMST(n, adj, 0);

        System.out.println("--- Running Prim from C ---");
        primMST(n, adj, 2);

        System.out.println("Note: Compare MST total costs from Kruskal and Prim (A/C). They should match.");
    }
}