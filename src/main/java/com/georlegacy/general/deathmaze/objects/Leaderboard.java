package com.georlegacy.general.deathmaze.objects;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.enumeration.LeaderboardType;
import com.georlegacy.general.deathmaze.util.ColorUtil;
import com.georlegacy.general.deathmaze.util.SerializableLocation;
import com.georlegacy.general.deathmaze.util.StatsEncoder;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class Leaderboard implements Serializable {

    static final long serialVersionUID = 4389267980455778936L;

    @Getter
    @Setter
    private LeaderboardType type;

    @Getter
    @Setter
    private SerializableLocation location;

    @Getter
    @Setter
    private int length;

    @Getter
    @Setter
    private String header;

    @Getter
    @Setter
    private ChatColor color;

    @Getter
    private String name;

    @Getter
    List<PlayerStats> statsList;

    public Leaderboard(LeaderboardType type, Location loc, int length, String header, ChatColor color, String name) {
        this.type = type;
        this.location = new SerializableLocation(loc);
        this.length = length;
        this.header = header;
        this.name = name;
        this.color = color;

        statsList = new LinkedList<PlayerStats>();
    }

    public void delete() {
        Location current = this.location.getLocation();
        current.getWorld().getNearbyEntities(current, 20, 20, 20).stream().filter(
                entity -> (entity instanceof LivingEntity)
                        && (entity.getScoreboardTags().contains(this.name + "-leaderboard")))
                .forEach(entity -> ((LivingEntity) entity).remove());
    }

    private void render() {
        List<String> lines = new ArrayList<>();
        lines.add(ColorUtil.format(this.header));
        this.statsList.forEach(stats -> lines.add(DeathMaze.getInstance().getConfiguration()
                .getLeaderboardEntryFormat(this, stats)));
        List<String> reversedLines = Lists.reverse(lines);
        Location bottom = this.location.getLocation();
        bottom.setY(bottom.getY() + 0.5D);

        Location current;
        current = bottom;


        current.getWorld().getNearbyEntities(current, 20, 20, 20).stream().filter(
                entity -> (entity instanceof LivingEntity)
                        && (entity.getScoreboardTags().contains(this.name + "-leaderboard")))
                .forEach(entity -> ((LivingEntity) entity).remove());
// ignore below
//        for (Entity entity : current.getWorld().getNearbyEntities(current, 20,20,20)) {
//            if (entity instanceof LivingEntity && entity.getScoreboardTags().contains(this.name + "-leaderboard")) {
//                LivingEntity livingEntity = (LivingEntity) entity;
//                livingEntity.remove();
//            }
//        }

        for (String line : reversedLines) {
            ArmorStand stand = current.getWorld().spawn(current, ArmorStand.class);
            stand.setCustomNameVisible(true);
            stand.setCustomName(line);
            stand.setVisible(false);
            stand.setSmall(true);
            stand.setGravity(false);
            stand.addScoreboardTag(this.name + "-leaderboard");
            current.setY(current.getY() + 0.35D);
        }
    }

    public synchronized int update() {
        DeathMaze.getInstance().stats.values().forEach(StatsEncoder::encode);

        List<PlayerStats> allStats = new LinkedList<PlayerStats>();
        for (File file : Objects.requireNonNull(new File(DeathMaze.getInstance().getDataFolder() + File.separator + "players" + File.separator).listFiles())) {
            allStats.add(StatsEncoder.decode(file));
        }
        allStats.sort((o1, o2) -> {
            switch (type) {
                case KILLS:
                    return Integer.compare(o2.getKills(), o1.getKills());
                case DEATHS:
                    return Integer.compare(o2.getDeaths(), o1.getDeaths());
                case LEVEL:
                    return Integer.compare(o2.getCurrentLevel().getLevel(), o1.getCurrentLevel().getLevel());
                case XP:
                    return Math.round(Double.compare(o2.getCurrentLevel().getPreviousTotal() + o2.getExcessXp(), o1.getCurrentLevel().getPreviousTotal() + o1.getExcessXp()));
                case DISTANCE:
                    return Math.round(Double.compare(o2.getDistance(), o1.getDistance()));
                case REGIONS:
                    return Integer.compare(o1.getRegionsExplored().size(), o2.getRegionsExplored().size());
                case LOOTABLES:
                    return Integer.compare(o1.getContainersLooted().size(), o2.getContainersLooted().size());
            }
            throw new RuntimeException("A fatal error occurred whilst updating leaderboard" + name);
        });
        this.statsList.clear();
        if (length > allStats.size())
            this.statsList.addAll(allStats);
        else
            this.statsList.addAll(allStats.subList(0, length));

        render();
        return statsList.size();
    }

}
