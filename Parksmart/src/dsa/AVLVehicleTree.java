package dsa;

public class AVLVehicleTree {
    class Node {
        String vehicleNumber;
        int height;
        Node left, right;

        Node(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
            this.height = 1;
        }
    }

    private Node root;

    public void insertVehicle(String vehicleNumber) {
        root = insert(root, vehicleNumber);
    }

    public void deleteVehicle(String vehicleNumber) {
        root = delete(root, vehicleNumber);
    }

    private Node insert(Node node, String vehicleNumber) {
        if (node == null) {
            return new Node(vehicleNumber);
        }

        if (vehicleNumber.compareTo(node.vehicleNumber) < 0) {
            node.left = insert(node.left, vehicleNumber);
        } else if (vehicleNumber.compareTo(node.vehicleNumber) > 0) {
            node.right = insert(node.right, vehicleNumber);
        } else {
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        if (balance > 1 && vehicleNumber.compareTo(node.left.vehicleNumber) < 0) {
            return rotateRight(node);
        }
        if (balance < -1 && vehicleNumber.compareTo(node.right.vehicleNumber) > 0) {
            return rotateLeft(node);
        }
        if (balance > 1 && vehicleNumber.compareTo(node.left.vehicleNumber) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1 && vehicleNumber.compareTo(node.right.vehicleNumber) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private Node delete(Node node, String vehicleNumber) {
        if (node == null) return null;

        if (vehicleNumber.compareTo(node.vehicleNumber) < 0) {
            node.left = delete(node.left, vehicleNumber);
        } else if (vehicleNumber.compareTo(node.vehicleNumber) > 0) {
            node.right = delete(node.right, vehicleNumber);
        } else {
            if (node.left == null || node.right == null) {
                node = (node.left != null) ? node.left : node.right;
            } else {
                Node minNode = getMinValueNode(node.right);
                node.vehicleNumber = minNode.vehicleNumber;
                node.right = delete(node.right, minNode.vehicleNumber);
            }
        }

        if (node == null) return null;

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0) return rotateRight(node);
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1 && getBalance(node.right) <= 0) return rotateLeft(node);
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private Node getMinValueNode(Node node) {
        Node current = node;
        while (current.left != null) current = current.left;
        return current;
    }

    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node t2 = x.right;

        x.right = y;
        y.left = t2;

        y.height = 1 + Math.max(height(y.left), height(y.right));
        x.height = 1 + Math.max(height(x.left), height(x.right));

        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node t2 = y.left;

        y.left = x;
        x.right = t2;

        x.height = 1 + Math.max(height(x.left), height(x.right));
        y.height = 1 + Math.max(height(y.left), height(y.right));

        return y;
    }
}