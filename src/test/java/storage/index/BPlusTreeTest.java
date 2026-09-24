package storage.index;

import org.junit.jupiter.api.Test;
import storage.Row;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BPlusTreeTest {

    @Test
    void findsAllDuplicatesAcrossDeepSplits() {
        BPlusTree tree = new BPlusTree(4);
        for (int i = 0; i < 10000; i++) {
            Row row = new Row();
            row.addValue("AGE", i % 100);
            tree.insert(i % 100, row);
        }
        assertEquals(100, tree.search(50).size());
        assertEquals(3100, tree.rangeSearch(20, 50).size());
    }

    @Test
    void sequentialKeysForceMultiLevelSplits() {
        BPlusTree tree = new BPlusTree(3);
        for (int i = 0; i < 10000; i++) {
            Row row = new Row();
            row.addValue("ID", i);
            tree.insert(i, row);
        }
        assertEquals(1, tree.search(1234).size());
        assertEquals(100, tree.rangeSearch(100, 199).size());
        assertEquals(0, tree.search(20000).size());
    }
}