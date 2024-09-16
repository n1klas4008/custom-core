package com.n1klas4008.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.function.Function;

public class ResultSetTransformer {

    public static <T> T singleton(ResultSet set, Function<Object[], T> function) throws SQLException {
        ResultSetMetaData meta = set.getMetaData();
        final int columns = meta.getColumnCount();
        if (set.next()) {
            Object[] values = new Object[columns];
            for (int i = 1; i <= columns; i++) {
                values[i - 1] = set.getObject(i);
            }
            return function.apply(values);
        }
        return function.apply(new Object[0]);
    }

    public static <T> LinkedList<T> parse(ResultSet set, Function<Object[], T> function) throws SQLException {
        ResultSetMetaData meta = set.getMetaData();
        final int columns = meta.getColumnCount();
        LinkedList<T> list = new LinkedList<>();
        while (set.next()) {
            Object[] values = new Object[columns];
            for (int i = 1; i <= columns; i++) {
                values[i - 1] = set.getObject(i);
            }
            list.add(function.apply(values));
        }
        return list;
    }
}
