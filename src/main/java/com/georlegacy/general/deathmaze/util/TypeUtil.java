package com.georlegacy.general.deathmaze.util;

import com.georlegacy.general.deathmaze.objects.PlayerStats;
import com.georlegacy.general.deathmaze.objects.enumeration.LeaderboardType;

import static com.georlegacy.general.deathmaze.objects.enumeration.LeaderboardType.*;

public class TypeUtil {

    public static String getValue(LeaderboardType type, PlayerStats stats) throws IllegalArgumentException {
        if (type == null || stats == null)
            throw new IllegalArgumentException("Neither of the parameters can be null.");
        return type == KILLS ? NumberFormatter.format(stats.getKills()) :
                type == DEATHS ? NumberFormatter.format(stats.getDeaths()) :
                        type == LEVEL ? String.valueOf(stats.getCurrentLevel().getLevel()) :
                                type == XP ? NumberFormatter.format(stats.getCurrentLevel().getPreviousTotal() + stats.getExcessXp()) :
                                        type == DISTANCE ? DistanceFormatter.format(stats.getDistance()) :
                                                type == REGIONS ? NumberFormatter.format(stats.getRegionsExplored().size()) :
                                                        type == LOOTABLES ? NumberFormatter.format(stats.getContainersLooted().size()) :
                                                                "Invalid Type";
    }

}
