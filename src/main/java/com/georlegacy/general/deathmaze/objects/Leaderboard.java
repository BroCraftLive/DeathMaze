package com.georlegacy.general.deathmaze.objects;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.enumeration.LeaderboardType;
import com.georlegacy.general.deathmaze.util.SerializableLocation;
import com.georlegacy.general.deathmaze.util.StatsEncoder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import sun.awt.image.ImageWatched;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.*;

public class Leaderboard implements Serializable {

    @Getter @Setter
    private LeaderboardType type;

    @Getter @Setter
    private SerializableLocation location;

    @Getter @Setter
    private int length;

    @Getter @Setter
    private String header;

    @Getter
    private String name;

    @Getter
    List<PlayerStats> statsList;

    public Leaderboard(LeaderboardType type, Location loc, int length, String header, String name) {
        this.type = type;
        this.location = new SerializableLocation(loc);
        this.length = length;
        this.header = header;
        this.name = name;

        statsList = new LinkedList<PlayerStats>();
    }

    public synchronized int update() {
        List<PlayerStats> allStats = new LinkedList<PlayerStats>();
        for (File file : Objects.requireNonNull(new File(DeathMaze.getInstance().getDataFolder() + File.separator + "players" + File.separator).listFiles())) {
            allStats.add(StatsEncoder.decode(file));
        }
        Collections.sort(allStats, new Comparator<PlayerStats>() {
            @Override
            public int compare(PlayerStats o1, PlayerStats o2) {
                switch (type) {
                    case KILLS:
                        return Integer.compare(o1.getKills(), o2.getKills());
                    case DEATHS:
                        return Integer.compare(o1.getDeaths(), o2.getDeaths());
                    case LEVEL:
                        return Integer.compare(o1.getCurrentLevel().getLevel(), o2.getCurrentLevel().getLevel());
                    case XP:
                        return Math.round(Double.compare(o1.getCurrentLevel().getPreviousTotal() + o1.getExcessXp(), o2.getCurrentLevel().getPreviousTotal() + o2.getExcessXp()));
                    case DISTANCE:
                        return Math.round(Double.compare(o1.getDistance(), o2.getDistance()));
                    case REGIONS:
                        return Integer.compare(o1.getRegionsExplored().size(), o2.getRegionsExplored().size());
                    case LOOTABLES:
                        return Integer.compare(o1.getContainersLooted().size(), o2.getContainersLooted().size());
                }
                throw new RuntimeException("A fatal error occurred whilst updating leaderboard" + name);
            }
        });
        this.statsList.clear();
        this.statsList.addAll(allStats.subList(0, length - 1));
        return statsList.size();
    }

}
