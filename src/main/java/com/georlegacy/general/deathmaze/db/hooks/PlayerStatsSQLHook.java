package com.georlegacy.general.deathmaze.db.hooks;

import com.georlegacy.general.deathmaze.hooks.SQLHook;
import com.georlegacy.general.deathmaze.objects.PlayerStats;

public class PlayerStatsSQLHook extends SQLHook.ISQLHook<PlayerStats> {

    //todo fixed to compile
    protected PlayerStatsSQLHook(SQLHook dbHook) {
        super(dbHook);
    }

    @Override
    public PlayerStats decode() {
        return null;
    }

    @Override
    public boolean encode(PlayerStats obj) {
        return false;
    }

}
