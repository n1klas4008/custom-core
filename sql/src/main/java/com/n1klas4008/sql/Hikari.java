package com.n1klas4008.sql;

import com.n1klas4008.runtime.JsonSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Hikari {

    private final HikariDataSource source;

    public Hikari(HikariDataSource source) {
        this.source = source;
    }

    public HikariDataSource getSource() {
        return source;
    }

    public static Hikari setup(JsonSource source) {
        String jdbc = String.format("jdbc:%s://%s:%s/%s",
                source.get("sql.driver"),
                source.get("sql.ip"),
                source.get("sql.port"),
                source.get("sql.database")
        );
        return setup("default", jdbc, source);
    }

    public static Hikari setup(String jdbc, JsonSource source) {
        return setup("default", jdbc, source);
    }

    public static Hikari setup(String name, String jdbc, JsonSource source) {
        if (CONNECTION_MANAGERS.containsKey(name)) {
            return CONNECTION_MANAGERS.get(name);
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbc);
        config.setUsername(source.get("sql.username"));
        config.setPassword(source.get("sql.password"));
        int size = source.containsKey("sql.max") ? Integer.parseInt(source.get("sql.max")) : Runtime.getRuntime().availableProcessors();
        if (source.containsKey("sql.max")) config.setMaximumPoolSize(size);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        Hikari hikari = new Hikari(new HikariDataSource(config));
        CONNECTION_MANAGERS.put(name, hikari);
        return hikari;
    }

    public static Hikari setup(Supplier<HikariConfig> supplier) {
        return setup("default", supplier);
    }

    public static Hikari setup(String name, Supplier<HikariConfig> supplier) {
        Hikari hikari = new Hikari(new HikariDataSource(supplier.get()));
        CONNECTION_MANAGERS.put(name, hikari);
        return hikari;
    }

    public static void shutdown() {
        for (Hikari hikari : CONNECTION_MANAGERS.values()) {
            try {
                if (!hikari.source.isClosed()) hikari.source.close();
            } catch (Exception e) {
                System.err.println("Failed to shutdown a Hikari source");
                System.err.println(e.getMessage());
            }
        }
    }

    public Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    private static final Map<String, Hikari> CONNECTION_MANAGERS = new HashMap<>();

    public static Hikari getManager() {
        return CONNECTION_MANAGERS.get("default");
    }

    public static Hikari getManager(String name) {
        return CONNECTION_MANAGERS.get(name);
    }
}
