package storage;

import java.util.HashMap;
import java.util.Map;

public class Row {
    private Map<String, Object> values;

    public Row() {
        this.values = new HashMap<String, Object>();
    }

    public void addValue(String columnName, Object value) {
        values.put(columnName, value);
    }

    public Object getValue(String columnName) {
        return values.get(columnName);
    }

    public Map<String, Object> getValues() {
        return values;
    }
}

