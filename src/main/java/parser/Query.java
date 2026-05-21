package parser;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Query {
    private QueryType type;
    private ArrayList<String> selectedColumns;
    private String tableName;
    private String conditionColumn;
    private String conditionOperator;
    private String conditionValue;
    private ArrayList<String> values;


    public Query(QueryType type, ArrayList<String> selectedColumns, String tableName) {
        this.type = type;
        this.selectedColumns = selectedColumns;
        this.tableName = tableName;
    }


    public void setConditionColumn(String conditionColumn) {
        this.conditionColumn = conditionColumn;
    }

    public void setConditionOperator(String conditionOperator) {
        this.conditionOperator = conditionOperator;
    }

    public void setConditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
    }

    public QueryType getType() {
        return type;
    }

    public ArrayList<String> getSelectedColumns() {
        return selectedColumns;
    }

    public String getTableName() {
        return tableName;
    }

    public String getConditionColumn() {
        return conditionColumn;
    }

    public String getConditionOperator() {
        return conditionOperator;
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }
}
