package com.georlegacy.general.deathmaze.tasks;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.Leaderboard;

public class UpdateLeaderboards implements Runnable {

    @Override
    public void run() {
        DeathMaze.getInstance().getMaze().getLeaderboards().forEach(Leaderboard::update);
    }

}
