package benchmark;

import executor.Executor;
import main.Repl;
import parser.Parser;
import planner.Planner;
import storage.*;
import storage.index.BPlusTree;
import storage.index.HashIndex;

import java.util.ArrayList;


public class Benchmark {
    public static void main (String[] args) {
        Database database = new Database("database");
        Table table = new Table("USERS");

        Column column1 = new Column("NAME", DataType.STRING);
        Column column2 = new Column("AGE", DataType.INTEGER);
        table.addColumn(column1);
        table.addColumn(column2);

        for (int i = 0; i < 10000; i++) {
            Row row = new Row();
            row.addValue("NAME", "Bob");
            row.addValue("AGE", i%100);
            table.addRow(row);
        }
        database.addTable(table);

        /*
        Parser parser = new Parser();
        Executor executor = new Executor(database);
        Planner planner = new Planner(executor);

        Repl repl = new Repl(database);
        repl.start();
        * */

        System.out.println("Full Scan: " + benchmarkScan(table) + " ns");
        System.out.println("Hash Index: " + benchmarkHashIndex(table) + " ns");
        System.out.println("B+ Tree: " + benchmarkBPlusTree(table) + " ns");
        System.out.println("-----------------------------------------------------");
        System.out.println("Full Scan Range: " + benchmarkRangeScan(table) + " ns");
        System.out.println("B+ Tree Range: " + benchmarkBPlusTreeRange(table) + " ns");
        System.out.println("-----------------------------------------------------");

        BPlusTree tree = new BPlusTree(4);
        for (Row row : table.getRows()) tree.insert(row.getValue("AGE"), row);
        System.out.println("search(50): " + tree.search(50).size() + " rows (should be 100)");
        System.out.println("range(20,50): " + tree.rangeSearch(20, 50).size() + " rows (should be 3100)");
    }

    public static long benchmarkScan(Table table) {
        ArrayList<Row> results = new ArrayList<>();
        long start = System.nanoTime();
        for (Row row : table.getRows()) {
            if (row.getValue("AGE").equals(50)) {
                results.add(row);
            }
        }
        long end = System.nanoTime();
        return end - start;
    }

    public static long benchmarkHashIndex(Table table) {
        HashIndex index = new HashIndex("AGE");
        for (Row row : table.getRows()) {
            index.insert(row.getValue("AGE"), row);
        }
        long start = System.nanoTime();
        index.get(50);
        long end = System.nanoTime();
        return end - start;
    }

    public static long benchmarkBPlusTree(Table table) {
        BPlusTree tree = new BPlusTree(4);
        for (Row row : table.getRows()) {
            tree.insert(row.getValue("AGE"), row);
        }
        long start = System.nanoTime();
        tree.search(50);
        long end = System.nanoTime();
        return end - start;
    }

    public static long benchmarkRangeScan(Table table) {
        ArrayList<Row> results = new ArrayList<>();
        long start = System.nanoTime();
        for (Row row : table.getRows()) {
            Integer age = (Integer) row.getValue("AGE");
            if (age >= 20 && age <= 50) {
                results.add(row);
            }
        }
        long end = System.nanoTime();
        return end - start;
    }

    public static long benchmarkBPlusTreeRange(Table table) {
        BPlusTree tree = new BPlusTree(4);
        for (Row row : table.getRows()) {
            tree.insert(row.getValue("AGE"), row);
        }
        long start = System.nanoTime();
        tree.rangeSearch(20, 50);
        long end = System.nanoTime();
        return end - start;
    }

}
