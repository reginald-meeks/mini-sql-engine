package main;

import executor.Executor;
import parser.Parser;
import planner.Planner;
import storage.Database;
import storage.Row;

import java.util.ArrayList;
import java.util.Scanner;

public class Repl {
    private Database database;
    private Parser parser;
    private Executor executor;
    private Planner planner;

    public Repl(Database database) {
        this.database = database;
        parser = new Parser();
        executor = new Executor(database);
        planner = new Planner(executor);

    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Mini SQL Engine. Type EXIT to quit.");

        while (true) {
            System.out.print("sql> ");
            String input = scanner.nextLine();

            if (input.equals("EXIT")) {
                break;
            }

            ArrayList<Row> results = planner.plan(parser.parse(input));
            for (Row row : results) {
                System.out.println(row.getValues());
            }
        }
    }
}
