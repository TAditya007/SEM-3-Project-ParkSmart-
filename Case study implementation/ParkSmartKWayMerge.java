import java.util.*;

public class ParkSmartKWayMerge {

    // Simple in-memory ChunkReader for demonstration.
    static class ChunkReader {
        private final long[] data;
        private int index = 0;

        ChunkReader(long[] data) {
            this.data = data;
        }

        boolean hasNext() {
            return index < data.length;
        }

        long peek() {
            return data[index];
        }

        long next() {
            return data[index++];
        }
    }

    // Simple OutputWriter that writes to a list and also prints.
    static class OutputWriter {
        private final List<Long> out = new ArrayList<>();

        void write(long value) {
            out.add(value);
        }

        void flush() {
            // nothing special for in-memory writer
        }

        List<Long> getOutput() {
            return out;
        }
    }

    static class HeapEntry {
        long value;
        int chunkId;
        HeapEntry(long v, int c) { value = v; chunkId = c; }

        @Override
        public String toString() {
            return value + "(c" + chunkId + ")";
        }
    }

    /** Merge k sorted chunks into a single sorted output, with logging. */
    static void kWayMerge(List<ChunkReader> chunks, OutputWriter out) {
        PriorityQueue<HeapEntry> heap = new PriorityQueue<>(
                (a, b) -> Long.compare(a.value, b.value));

        // Seed the heap with the first element of each non-empty chunk.
        for (int i = 0; i < chunks.size(); i++) {
            ChunkReader cr = chunks.get(i);
            if (cr.hasNext()) {
                long v = cr.next();
                heap.add(new HeapEntry(v, i));
            }
        }

        int step = 1;
        while (!heap.isEmpty()) {
            System.out.println("Step " + step + ":");
            System.out.println("  Heap before pop : " + heap);

            HeapEntry e = heap.poll();
            out.write(e.value);

            System.out.println("  Popped          : " + e.value + " from chunk " + e.chunkId);

            ChunkReader cr = chunks.get(e.chunkId);
            if (cr.hasNext()) {
                long v = cr.next();
                heap.add(new HeapEntry(v, e.chunkId));
                System.out.println("  Pushed          : " + v + " from chunk " + e.chunkId);
            } else {
                System.out.println("  Chunk " + e.chunkId + " exhausted");
            }

            System.out.println("  Heap after step : " + heap);
            System.out.println();
            step++;
        }

        out.flush();
    }

    public static void main(String[] args) {
        // ParkSmart test data: 6 sorted chunks of timestamps.
        long[] c1 = {10, 25, 47, 89};
        long[] c2 = {12, 30, 51, 75};
        long[] c3 = {8, 19, 41, 92};
        long[] c4 = {15, 33, 60, 80};
        long[] c5 = {22, 38, 55, 88};
        long[] c6 = {11, 28, 49, 85};

        List<ChunkReader> chunks = new ArrayList<>();
        chunks.add(new ChunkReader(c1)); // chunk 0
        chunks.add(new ChunkReader(c2)); // chunk 1
        chunks.add(new ChunkReader(c3)); // chunk 2
        chunks.add(new ChunkReader(c4)); // chunk 3
        chunks.add(new ChunkReader(c5)); // chunk 4
        chunks.add(new ChunkReader(c6)); // chunk 5

        OutputWriter out = new OutputWriter();

        System.out.println("=== ParkSmart k-way merge demo (k = " + chunks.size() + ") ===");
        kWayMerge(chunks, out);

        System.out.println("Final merged output:");
        System.out.println(out.getOutput());
    }
}