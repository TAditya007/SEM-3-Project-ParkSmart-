package dsa;

public class BSTSlots {
    class Node {
        int slotId;
        Node left, right;

        Node(int slotId) {
            this.slotId = slotId;
        }
    }

    private Node root;

    public void insertSlot(int slotId) {
        root = insert(root, slotId);
    }

    private Node insert(Node node, int slotId) {
        if (node == null) {
            return new Node(slotId);
        }
        if (slotId < node.slotId) {
            node.left = insert(node.left, slotId);
        } else if (slotId > node.slotId) {
            node.right = insert(node.right, slotId);
        }
        return node;
    }

    public boolean searchSlot(int slotId) {
        return search(root, slotId);
    }

    private boolean search(Node node, int slotId) {
        if (node == null) {
            return false;
        }
        if (node.slotId == slotId) {
            return true;
        }
        return slotId < node.slotId ? search(node.left, slotId) : search(node.right, slotId);
    }

    public void inorderDisplay() {
        inorder(root);
        System.out.println();
    }

    private void inorder(Node node) {
        if (node != null) {
            inorder(node.left);
            System.out.print(node.slotId + " ");
            inorder(node.right);
        }
    }
}