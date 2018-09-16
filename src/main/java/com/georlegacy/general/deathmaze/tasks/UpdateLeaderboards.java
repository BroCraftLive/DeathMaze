package com.georlegacy.general.deathmaze.tasks;

import com.georlegacy.general.deathmaze.util.Leaderboards;

public class UpdateLeaderboards implements Runnable {

    @Override
    public void run() {
        Leaderboards.updateLeaderboards();
    }

}
