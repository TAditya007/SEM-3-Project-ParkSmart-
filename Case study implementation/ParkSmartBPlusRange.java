import java.util.ArrayList;
import java.util.List;


public class ParkSmartBPlusRange {

    // ---------------- B+ TREE NODE ----------------

    static class BPlusNode {
        boolean isLeaf;
        int[] keys;
        BPlusNode[] children;   // internal: child pointers
        BPlusNode next;         // leaf-chain pointer

        BPlusNode(boolean isLeaf, int keyCapacity, int childCapacity) {
            this.isLeaf = isLeaf;
            this.keys = new int[keyCapacity];
            if (!isLeaf) {
                this.children = new BPlusNode[childCapacity];
            }
        }
    }

    // For instrumentation: count how many leaf nodes we touched
    static class Counter {
        int leafPages;
    }

    // ---------------- BUGGY RANGecount (FOR COMPARISON) ----------------

    // This version has both bugs:
    // 1) Always goes to leftmost leaf (children[0])
    // 2) Walks to end of leaf chain without stopping after hi
    static int rangeCountBuggy(BPlusNode root, int lo, int hi, Counter c) {
        if (root == null) return 0;

        BPlusNode leaf = root;
        while (!leaf.isLeaf) {
            leaf = leaf.children[0]; // BUG #1
        }

        int count = 0;
        while (leaf != null) {
            c.leafPages++;
            for (int k : leaf.keys) {
                if (k == Integer.MIN_VALUE) continue; // ignore empty
                if (lo <= k && k <= hi) {
                    count++;
                }
            }
            leaf = leaf.next; // BUG #2
        }
        return count;
    }

    // ---------------- CORRECTED FIND-LEAF ----------------

    // Descend using keys to find first leaf that can contain "key"
    static BPlusNode findLeaf(BPlusNode root, int key) {
        BPlusNode node = root;
        while (!node.isLeaf) {
            int i = 0;
            // keys[]: separator keys, sorted ascending; some may be empty (MIN_VALUE)
            while (i < node.keys.length && node.keys[i] != Integer.MIN_VALUE
                    && key > node.keys[i]) {
                i++;
            }
            // go to children[i]
            node = node.children[i];
        }
        return node;
    }

    // ---------------- OPTIMIZED RANGecount ----------------

    static int rangeCountOptimized(BPlusNode root, int lo, int hi, Counter c) {
        if (root == null) return 0;

        BPlusNode leaf = findLeaf(root, lo);
        int count = 0;

        while (leaf != null) {
            c.leafPages++;
            for (int k : leaf.keys) {
                if (k == Integer.MIN_VALUE) continue; // ignore empty slots
                if (k < lo) continue;                 // not yet in range
                if (k > hi) return count;             // beyond range: stop
                count++;
            }
            leaf = leaf.next;                         // next leaf if still needed
        }
        return count;
    }

    // ----------------- BUILD A TOY B+ TREE -----------------
    // Note: this is a very simplified builder just for testing rangeCount logic.
    // It creates:
    //   - one root internal node with many leaf children
    //   - each leaf has "leafCapacity" keys
    //   - leaf keys are global sorted array split across pages
    //
    // This simulates a large B+ tree leaf-level and a shallow internal level.

    static BPlusNode buildToyBPlusTree(int totalKeys, int leafCapacity) {
        // Generate sorted keys 1..totalKeys
        int[] allKeys = new int[totalKeys];
        for (int i = 0; i < totalKeys; i++) {
            allKeys[i] = i + 1;
        }

        int leafCount = (int) Math.ceil(totalKeys / (double) leafCapacity);

        // Create leaf nodes
        List<BPlusNode> leaves = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < leafCount; i++) {
            BPlusNode leaf = new BPlusNode(true, leafCapacity, 0);
            for (int j = 0; j < leafCapacity; j++) {
                if (index < totalKeys) {
                    leaf.keys[j] = allKeys[index++];
                } else {
                    leaf.keys[j] = Integer.MIN_VALUE; // mark empty
                }
            }
            leaves.add(leaf);
        }

        // Link leaf chain via .next
        for (int i = 0; i < leaves.size() - 1; i++) {
            leaves.get(i).next = leaves.get(i + 1);
        }

        // Create a simple root internal node with children = all leaves
        // and separator keys at boundaries
        int childCount = leaves.size();
        BPlusNode root = new BPlusNode(false, childCount - 1, childCount);

        // Children array
        for (int i = 0; i < childCount; i++) {
            root.children[i] = leaves.get(i);
        }

        // Separator keys: maximum key in each child (except last, which doesn't need a separator)
        for (int i = 0; i < childCount - 1; i++) {
            int[] leafKeys = leaves.get(i).keys;
            int max = Integer.MIN_VALUE;
            for (int k : leafKeys) {
                if (k > max) max = k;
            }
            root.keys[i] = max;
        }
        return root;
    }

    // ---------------- MAIN: DEMO FOR PARKSMART ----------------

    public static void main(String[] args) {
        // Simulate ParkSmart-like sizes in SMALLER SCALE so we can run quickly:
        // Instead of 1e7 keys, let's use 200 * 100 = 20,000 keys.
        // Leaf capacity = 200, so we get about 100 leaves.

        int leafCapacity = 200;
        int totalKeys = 200 * 100; // 100 leaf pages
        BPlusNode root = buildToyBPlusTree(totalKeys, leafCapacity);

        // Simulate a fee-range query ~ like ParkSmart's 2800 hits.
        // Here, each key is 1..20000. If we pick [5000, 7800], that's ~2800 keys.
        int lo = 5000;
        int hi = 7800;

        Counter cBuggy = new Counter();
        Counter cOpt   = new Counter();

        int countBuggy = rangeCountBuggy(root, lo, hi, cBuggy);
        int countOpt   = rangeCountOptimized(root, lo, hi, cOpt);

        System.out.println("Range: [" + lo + ", " + hi + "]");
        System.out.println("Buggy  count = " + countBuggy + ", leaf pages read = " + cBuggy.leafPages);
        System.out.println("Fixed  count = " + countOpt   + ", leaf pages read = " + cOpt.leafPages);
        System.out.println("Total leaves in tree = " + (int) Math.ceil(totalKeys / (double) leafCapacity));
    }
}