package parser;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Parser {
    public Query parse(String sql) {
        sql = sql.toUpperCase();
       String[] tokens = sql.split(" ");
       if (tokens[0].equals("SELECT")) {
           ArrayList<String> columns = new ArrayList<>();
           String tableName = null;
           boolean collectingColumns = false;
           String column = null;
           String operator = null;
           String value = null;

           for (int i = 0; i < tokens.length; i++) {
               if (tokens[i].equals("SELECT")) {
                   collectingColumns = true;
               } else if (tokens[i].equals("FROM")) {
                   collectingColumns = false;
                   tableName = tokens[i + 1];
               } else if (collectingColumns) {
                   columns.add(tokens[i].replace(",", ""));
               } else if (tokens[i].equals("WHERE")) {
                   column = tokens[i + 1];
                   operator = tokens[i + 2];
                   value = tokens[i + 3];
               }
           }
           Query query = new Query(QueryType.SELECT, columns, tableName);
           query.setConditionColumn(column);
           query.setConditionOperator(operator);
           query.setConditionValue(value);
           return query;

       } else if (tokens[0].equals("INSERT")) {
           String tableName = null;
           ArrayList<String> values = new ArrayList<>();
           tableName = tokens[2];
           for (int j = 4; j < tokens.length; j++) {
               String clean = tokens[j].replace("(", "").replace(")",
                       "").replace(",", "");
               values.add(clean);
           }
           Query query = new Query(QueryType.INSERT, null, tableName);
           query.setValues(values);
           return query;
       }
       return null;
    }
}
