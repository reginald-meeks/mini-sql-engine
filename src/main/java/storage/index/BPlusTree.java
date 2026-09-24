package storage.index;

import storage.Row;

import java.util.ArrayList;
import java.util.Stack;

public class BPlusTree {
    private BPlusTreeNode root;
    private int order;

    public BPlusTree(int order) {
        this.order = order;
        root = new BPlusTreeNode(true);
    }

    public void insert(Object key, Row row) {
        Helper helper = findLeaf(key);
        BPlusTreeNode leaf = helper.leaf;
        int i = 0;
        while (i < leaf.keys.size() && ((Comparable) key).compareTo(leaf.keys.get(i)) > 0) {
            i++;
        }
        leaf.keys.add(i, key);
        leaf.rows.add(i, row);

        if (leaf.keys.size() <= order) {
            return;
        }

        BPlusTreeNode newLeaf = splitLeaf(leaf);
        Object promotedKey = newLeaf.keys.get(0); // leaf split: key is COPIED up
        BPlusTreeNode left = leaf;
        BPlusTreeNode right = newLeaf;

        while (true) {
            if (helper.ancestry.isEmpty()) {
                BPlusTreeNode newRoot = new BPlusTreeNode(false);
                newRoot.keys.add(promotedKey);
                newRoot.children.add(left);
                newRoot.children.add(right);
                root = newRoot;
                return;
            }

            BPlusTreeNode parent = helper.ancestry.pop();
            int childIndex = parent.children.indexOf(left);
            parent.keys.add(childIndex, promotedKey);
            parent.children.add(childIndex + 1, right);

            if (parent.keys.size() <= order) {
                return;
            }

            SplitResult split = splitInternal(parent);
            promotedKey = split.promotedKey;
            left = parent;
            right = split.node;
        }
    }

    public ArrayList<Row> search(Object key) {
        BPlusTreeNode current = findLeftmostLeaf(key);
        ArrayList<Row> results = new ArrayList<>();
        while (current != null) {
            for (int i = 0; i < current.keys.size(); i++) {
                int cmp = ((Comparable) current.keys.get(i)).compareTo(key);
                if (cmp == 0) {
                    results.add(current.rows.get(i));
                } else if (cmp > 0) {
                    return results;
                }
            }
            current = current.nextLeaf;
        }
        return results;
    }

    public ArrayList<Row> rangeSearch(Object fromKey, Object toKey) {
        ArrayList<Row> results = new ArrayList<>();
        BPlusTreeNode current = findLeftmostLeaf(fromKey);
        while (current != null) {
            for (int i = 0; i < current.keys.size(); i++) {
                if (((Comparable) current.keys.get(i)).compareTo(fromKey) >= 0 && ((Comparable) current.keys.get(i)).compareTo(toKey) <= 0) {
                    results.add(current.rows.get(i));
                } else if (((Comparable) current.keys.get(i)).compareTo(toKey) > 0) {
                    return results;
                }
            }
            current = current.nextLeaf;
        }
        return results;
    }

    private Helper findLeaf(Object key) {
        Stack<BPlusTreeNode> ancestry = new Stack<>();
        BPlusTreeNode current = root;
        while (!current.leaf) {
            int i = 0;
            while (i < current.keys.size() && ((Comparable) key).compareTo(current.keys.get(i)) >= 0) {
                i++;
            }
            ancestry.push(current);
            current = current.children.get(i);
        }
        return new Helper(current, ancestry);
    }

    private BPlusTreeNode findLeftmostLeaf(Object key) {
        BPlusTreeNode current = root;
        while (!current.leaf) {
            int i = 0;
            while (i < current.keys.size() && ((Comparable) key).compareTo(current.keys.get(i)) > 0) {
                i++;
            }
            current = current.children.get(i);
        }
        return current;
    }

    private BPlusTreeNode splitLeaf(BPlusTreeNode leaf) {
        BPlusTreeNode newLeaf = new BPlusTreeNode(true);
        int mid = (order + 1) / 2;

        newLeaf.keys.addAll(leaf.keys.subList(mid, leaf.keys.size()));
        newLeaf.rows.addAll(leaf.rows.subList(mid, leaf.rows.size()));

        leaf.keys.subList(mid, leaf.keys.size()).clear();
        leaf.rows.subList(mid, leaf.rows.size()).clear();

        newLeaf.nextLeaf = leaf.nextLeaf;
        leaf.nextLeaf = newLeaf;

        return newLeaf;
    }

    private SplitResult splitInternal(BPlusTreeNode node) {
        BPlusTreeNode newNode = new BPlusTreeNode(false);
        int mid = node.keys.size() / 2;
        Object promotedKey = node.keys.get(mid);

        newNode.keys.addAll(node.keys.subList(mid + 1, node.keys.size()));
        newNode.children.addAll(node.children.subList(mid + 1, node.children.size()));

        node.keys.subList(mid, node.keys.size()).clear();
        node.children.subList(mid + 1, node.children.size()).clear();

        return new SplitResult(newNode, promotedKey);
    }

    public class BPlusTreeNode {
        private boolean leaf;
        private ArrayList<Object> keys;
        private ArrayList<BPlusTreeNode> children;
        private ArrayList<Row> rows;
        private BPlusTreeNode nextLeaf;

        public BPlusTreeNode(boolean isLeaf) {
            this.leaf = isLeaf;
            keys = new ArrayList<>();
            children = new ArrayList<>();
            rows = new ArrayList<>();
        }
    }

    private class Helper {
        private BPlusTreeNode leaf;
        private Stack<BPlusTreeNode> ancestry;

        Helper(BPlusTreeNode leaf, Stack<BPlusTreeNode> ancestry) {
            this.leaf = leaf;
            this.ancestry = ancestry;
        }
    }

    private class SplitResult {
        private BPlusTreeNode node;
        private Object promotedKey;

        SplitResult(BPlusTreeNode node, Object promotedKey) {
            this.node = node;
            this.promotedKey = promotedKey;
        }
    }
}
