import java.util.Scanner;

public class ParkSmartTrees {

    // ---------- BST IMPLEMENTATION ----------

    static class BSTNode {
        int key;
        BSTNode left, right;
        BSTNode(int key) { this.key = key; }
    }

    static class BST {
        BSTNode root;

        public void insert(int key) {
            root = insertRec(root, key);
        }

        private BSTNode insertRec(BSTNode node, int key) {
            if (node == null) return new BSTNode(key);
            if (key < node.key) node.left = insertRec(node.left, key);
            else if (key > node.key) node.right = insertRec(node.right, key);
            // ignore duplicates
            return node;
        }

        public boolean search(int key) {
            return searchRec(root, key) != null;
        }

        private BSTNode searchRec(BSTNode node, int key) {
            if (node == null) return null;
            if (key == node.key) return node;
            if (key < node.key) return searchRec(node.left, key);
            return searchRec(node.right, key);
        }

        public void delete(int key) {
            root = deleteRec(root, key);
        }

        private BSTNode deleteRec(BSTNode node, int key) {
            if (node == null) return null;

            if (key < node.key) {
                node.left = deleteRec(node.left, key);
            } else if (key > node.key) {
                node.right = deleteRec(node.right, key);
            } else {
                // found node
                if (node.left == null) return node.right;
                if (node.right == null) return node.left;
                // two children: inorder successor
                BSTNode succ = minNode(node.right);
                node.key = succ.key;
                node.right = deleteRec(node.right, succ.key);
            }
            return node;
        }

        private BSTNode minNode(BSTNode node) {
            BSTNode cur = node;
            while (cur.left != null) cur = cur.left;
            return cur;
        }

        public void inorder() {
            inorderRec(root);
            System.out.println();
        }

        private void inorderRec(BSTNode node) {
            if (node == null) return;
            inorderRec(node.left);
            System.out.print(node.key + " ");
            inorderRec(node.right);
        }

        public int height() {
            return heightRec(root);
        }

        private int heightRec(BSTNode node) {
            if (node == null) return -1; // height in edges
            return 1 + Math.max(heightRec(node.left), heightRec(node.right));
        }
    }

    // ---------- AVL IMPLEMENTATION ----------

    static class AVLNode {
        int key;
        AVLNode left, right;
        int height = 1;
        AVLNode(int key) { this.key = key; }
    }

    static class AVL {
        AVLNode root;

        private int height(AVLNode n) {
            return n == null ? 0 : n.height;
        }

        private int balance(AVLNode n) {
            return n == null ? 0 : height(n.left) - height(n.right);
        }

        private void updateHeight(AVLNode n) {
            if (n != null) {
                n.height = 1 + Math.max(height(n.left), height(n.right));
            }
        }

        private AVLNode rotateRight(AVLNode y) {
            AVLNode x  = y.left;
            AVLNode T2 = x.right;

            x.right = y;
            y.left  = T2;

            updateHeight(y);
            updateHeight(x);

            return x;
        }

        private AVLNode rotateLeft(AVLNode x) {
            AVLNode y  = x.right;
            AVLNode T2 = y.left;

            y.left  = x;
            x.right = T2;

            updateHeight(x);
            updateHeight(y);

            return y;
        }

        public void insert(int key) {
            root = insertRec(root, key);
        }

        private AVLNode insertRec(AVLNode node, int key) {
            if (node == null) return new AVLNode(key);

            if (key < node.key) {
                node.left = insertRec(node.left, key);
            } else if (key > node.key) {
                node.right = insertRec(node.right, key);
            } else {
                // ignore duplicates
                return node;
            }

            updateHeight(node);
            int bf = balance(node);

            // LL
            if (bf > 1 && key < node.left.key) {
                return rotateRight(node);
            }
            // RR
            if (bf < -1 && key > node.right.key) {
                return rotateLeft(node);
            }
            // LR
            if (bf > 1 && key > node.left.key) {
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
            // RL
            if (bf < -1 && key < node.right.key) {
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }

            return node;
        }

        public boolean search(int key) {
            return searchRec(root, key) != null;
        }

        private AVLNode searchRec(AVLNode node, int key) {
            if (node == null) return null;
            if (key == node.key) return node;
            if (key < node.key) return searchRec(node.left, key);
            return searchRec(node.right, key);
        }

        public void delete(int key) {
            root = deleteRec(root, key);
        }

        private AVLNode deleteRec(AVLNode node, int key) {
            if (node == null) return null;

            if (key < node.key) {
                node.left = deleteRec(node.left, key);
            } else if (key > node.key) {
                node.right = deleteRec(node.right, key);
            } else {
                // node to be deleted
                if (node.left == null) return node.right;
                if (node.right == null) return node.left;

                AVLNode succ = minNode(node.right);
                node.key = succ.key;
                node.right = deleteRec(node.right, succ.key);
            }

            updateHeight(node);
            int bf = balance(node);

            // LL
            if (bf > 1 && balance(node.left) >= 0) {
                return rotateRight(node);
            }
            // LR
            if (bf > 1 && balance(node.left) < 0) {
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
            // RR
            if (bf < -1 && balance(node.right) <= 0) {
                return rotateLeft(node);
            }
            // RL
            if (bf < -1 && balance(node.right) > 0) {
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }

            return node;
        }

        private AVLNode minNode(AVLNode node) {
            AVLNode cur = node;
            while (cur.left != null) cur = cur.left;
            return cur;
        }

        public void inorder() {
            inorderRec(root);
            System.out.println();
        }

        private void inorderRec(AVLNode node) {
            if (node == null) return;
            inorderRec(node.left);
            System.out.print(node.key + " ");
            inorderRec(node.right);
        }

        public int heightEdges() {
            // height in edges = node.height - 1 for non-null
            if (root == null) return -1;
            return root.height - 1;
        }
    }

    // ---------- MAIN: INTERACTIVE MENU FOR TERMINAL ----------

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        BST bst = new BST();
        AVL avl = new AVL();

        int[] initialIds = {20, 30, 35, 40, 45, 50, 60, 65, 70, 75, 80, 85, 90};

        // Load initial morning IDs into both trees
        for (int id : initialIds) {
            bst.insert(id);
            avl.insert(id);
        }

        System.out.println("ParkSmart SlotBooking Trees (BST vs AVL)");
        System.out.println("Initial IDs loaded:");
        printStatus(bst, avl);

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Insert SlotBooking-ID");
            System.out.println("2. Delete SlotBooking-ID");
            System.out.println("3. Search SlotBooking-ID");
            System.out.println("4. Print inorder (BST & AVL)");
            System.out.println("5. Show heights and worst-case SLA time");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid input.");
                continue;
            }

            if (choice == 0) break;

            switch (choice) {
                case 1:
                    System.out.print("Enter ID to insert: ");
                    int ins = Integer.parseInt(sc.nextLine().trim());
                    bst.insert(ins);
                    avl.insert(ins);
                    System.out.println("Inserted in both BST and AVL.");
                    break;

                case 2:
                    System.out.print("Enter ID to delete: ");
                    int del = Integer.parseInt(sc.nextLine().trim());
                    bst.delete(del);
                    avl.delete(del);
                    System.out.println("Deleted from both BST and AVL (if present).");
                    break;

                case 3:
                    System.out.print("Enter ID to search: ");
                    int s = Integer.parseInt(sc.nextLine().trim());
                    boolean inBST = bst.search(s);
                    boolean inAVL = avl.search(s);
                    System.out.println("BST: " + (inBST ? "FOUND" : "NOT FOUND"));
                    System.out.println("AVL: " + (inAVL ? "FOUND" : "NOT FOUND"));
                    break;

                case 4:
                    System.out.println("BST inorder:");
                    bst.inorder();
                    System.out.println("AVL inorder:");
                    avl.inorder();
                    break;

                case 5:
                    printStatus(bst, avl);
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }

        sc.close();
        System.out.println("Exiting ParkSmartTrees.");
    }

    // Helper: print heights and SLA worst-case time estimate
    private static void printStatus(BST bst, AVL avl) {
        int bstH = bst.height();        // edges
        int avlH = avl.heightEdges();   // edges

        // each hop ~200ns, worst case hops ≈ height + 1
        long bstHops = bstH + 1;
        long avlHops = avlH + 1;
        long bstNs = bstHops * 200L;
        long avlNs = avlHops * 200L;

        double bstMs = bstNs / 1_000_000.0;
        double avlMs = avlNs / 1_000_000.0;

        double slaMs = 5.0;
        double bstPercent = (bstMs / slaMs) * 100.0;
        double avlPercent = (avlMs / slaMs) * 100.0;

        System.out.println("\n--- Current Tree Status ---");
        System.out.println("BST height (edges): " + bstH);
        System.out.printf("BST worst-case ≈ %.6f ms (%.4f%% of 5 ms SLA)%n", bstMs, bstPercent);

        System.out.println("AVL height (edges): " + avlH);
        System.out.printf("AVL worst-case ≈ %.6f ms (%.4f%% of 5 ms SLA)%n", avlMs, avlPercent);
    }
}