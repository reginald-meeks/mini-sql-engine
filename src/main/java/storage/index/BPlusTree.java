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

        if (leaf.keys.size() > order) {
            BPlusTreeNode newLeaf = splitLeaf(leaf);
            if (leaf == root) {
                BPlusTreeNode newRoot = new BPlusTreeNode(false);
                newRoot.keys.add(newLeaf.keys.get(0));
                newRoot.children.add(leaf);
                newRoot.children.add(newLeaf);
                root = newRoot;

            }
        }
    }

    public ArrayList<Row> search(Object key) {
        BPlusTreeNode target = findLeaf(key);
        ArrayList<Row> results = new ArrayList<>();
        for (int i = 0; i < target.keys.size(); i++) {
            if (key.equals(target.keys.get(i))) {
                results.add(target.rows.get(i));
            }
        }
        return results;
    }

    public ArrayList<Row> rangeSearch(Object fromKey, Object toKey) {
        ArrayList<Row> results = new ArrayList<>();
        BPlusTreeNode current = findLeaf(fromKey);
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
}



