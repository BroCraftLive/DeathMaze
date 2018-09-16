package com.georlegacy.general.deathmaze.util;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.PlayerStats;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Leaderboards {

    public static void updateLeaderboards() {

        DeathMaze.getInstance().stats.values().forEach(StatsEncoder::encode);

        List<PlayerStats> allStats = new LinkedList<PlayerStats>();
        for (File file : Objects.requireNonNull(new File(DeathMaze.getInstance().getDataFolder() + File.separator + "players" + File.separator).listFiles())) {
            allStats.add(StatsEncoder.decode(file));
        }

        DeathMaze.getInstance().getMaze().getLeaderboards().forEach(l -> l.update(allStats));

    }

}
