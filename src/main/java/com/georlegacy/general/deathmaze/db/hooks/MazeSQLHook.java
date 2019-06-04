package com.georlegacy.general.deathmaze.db.hooks;

import com.georlegacy.general.deathmaze.hooks.SQLHook;
import com.georlegacy.general.deathmaze.hooks.SQLHook.ISQLHook;
import com.georlegacy.general.deathmaze.objects.Maze;

import java.sql.SQLException;

public class MazeSQLHook extends ISQLHook<Maze> {

    private final SQLHook dbHook;

    protected MazeSQLHook(SQLHook dbHook) {
        super(dbHook);
        this.dbHook = dbHook;
    }

    @Override
    public Maze decode() {
        return null;
    }

    @Override
    public boolean encode(Maze obj) {
        return false;
    }

    @Override
    public void init() throws SQLException {


        // todo LOTS OF PARSERS

        dbHook.getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS mazes (" +
                " id integer primary key," +
                " spawn text" +
                ")");
        dbHook.getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS mazeContainers (" +
                " id integer primary key," +
                " mazeId integer key," +
                " type text," +
                " refillSeconds integer," +
                " location text," +
                " itemsBase64 text," +
                " hiddenWhenEmpty bool" +
                ")");
        dbHook.getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS mazeRegions (" +
                " id integer primary key," +
                " mazeId integer key," +
                " name text," +
                " pos1 text," +
                " pos2 text," +
                " entrySound text" +
                ")");
        dbHook.getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS mazeRegionSplashes (" +
                " id integer primary key," +
                " regionId integer key," +
                " splash text" +
                ")");
        dbHook.getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS mazeLeaderboards (" +
                " id integer primary key," +
                " mazeId integer key," +
                " type text," +
                " location text," +
                " length integer," +
                " header text," +
                " color text," +
                " name text" +
                ")");
    }

}
