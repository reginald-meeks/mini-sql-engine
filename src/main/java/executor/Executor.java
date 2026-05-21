package executor;

import parser.Query;
import parser.QueryType;
import storage.Database;
import storage.Row;
import storage.Table;
import storage.index.HashIndex;

import java.util.ArrayList;

public class Executor {
    private Database database;

    public Executor(Database database) {
        this.database = database;
    }

    public ArrayList<Row> execute(Query query) {
        Table queryTable = database.getTable(query.getTableName());
        ArrayList<Row> results = new ArrayList<>();

        if (query.getType() == QueryType.INSERT) {
            Table insertTable = database.getTable(query.getTableName());
            Row newRow = new Row();

            for (int i = 0; i < insertTable.getColumns().size(); i++) {
                String columnName = insertTable.getColumns().get(i).getName();
                String value = query.getValues().get(i);
                newRow.addValue(columnName, value);
            }

            insertTable.addRow(newRow);
            return new ArrayList<>();
        } else {

            if (query.getConditionColumn() != null && query.getConditionOperator().equals("=")) {
                HashIndex index = queryTable.getIndex(query.getConditionColumn());
                if (index != null) {
                    ArrayList<Row> indexResults = index.get(query.getConditionValue());
                    if (indexResults != null) {
                        return indexResults;
                    }
                }
            }

            for (Row row : queryTable.getRows()) {
                if (query.getConditionColumn() != null) {
                    Integer rowValue = (Integer) row.getValue(query.getConditionColumn());
                    Integer conditionValue = Integer.parseInt(query.getConditionValue());

                    switch (query.getConditionOperator()) {
                        case ">" -> {
                            if (rowValue > conditionValue) {
                                results.add(row);
                            }
                        }
                        case "=" -> {
                            if (rowValue.equals(conditionValue)) {
                                results.add(row);
                            }
                        }
                        case "<" -> {
                            if (rowValue < conditionValue) {
                                results.add(row);
                            }
                        }
                    }
                } else {
                    results.add(row);
                }
            }
            return results;
        }
    }
}
