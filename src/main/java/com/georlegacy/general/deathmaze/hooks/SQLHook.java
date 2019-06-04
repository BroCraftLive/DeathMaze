package com.georlegacy.general.deathmaze.hooks;

import com.georlegacy.general.deathmaze.DeathMaze;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SQLHook {

    private final String url;
    private Connection conn;
    private final Set<ISQLHook> hooks;

    public SQLHook(String url) {
        this.url = url;
        hooks = new HashSet<>();
    }

    public void registerHook(ISQLHook... hooks) {
        this.hooks.addAll(Arrays.asList(hooks));
    }

    public Set<ISQLHook> getHooks() {
        return hooks;
    }

    public Connection getConnection() {
        return conn;
    }

    public void connect() {
        try {
            conn = DriverManager.getConnection(url);
            DeathMaze.getInstance().getLogger().info("Connected to SQL Database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static abstract class ISQLHook<T> {

        protected ISQLHook(SQLHook dbHook) {}

        public void init() throws SQLException {}

        public abstract T decode();

        public abstract boolean encode(T obj);

    }

}