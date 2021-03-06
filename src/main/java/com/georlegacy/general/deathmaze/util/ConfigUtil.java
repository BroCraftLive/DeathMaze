package com.georlegacy.general.deathmaze.util;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.Leaderboard;
import com.georlegacy.general.deathmaze.objects.PlayerStats;
import com.georlegacy.general.deathmaze.objects.RegionExplorable;
import com.georlegacy.general.deathmaze.objects.enumeration.Level;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static com.georlegacy.general.deathmaze.util.NumberFormatter.*;

public class ConfigUtil {
    private YamlConfiguration config;

    private ConfigUtil() {
        try {
            File f = new File(DeathMaze.getInstance().getDataFolder(), "config.yml");
            if (!f.exists())
                Files.copy(DeathMaze.getInstance().getClass().getClassLoader().getResourceAsStream("config.yml"), f.toPath());
            this.config = YamlConfiguration.loadConfiguration(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigUtil get() {
        return new ConfigUtil();
    }

    public List<World> getEnabledWorlds() {
        List<World> worlds = new ArrayList<World>();
        this.config.getStringList("EnabledWorlds").forEach(string -> worlds.add(Bukkit.getWorld(string)));
        return !worlds.isEmpty() ? worlds : null;
    }

    public String getVisitMessage(Player p) {
        return ColorUtil.format(this.config.getString("VisitMessage")
                .replace("%PNAME%", p.getName()));
    }

    public Sound getVisitSound() {
        if (this.config.getString("VisitSound").equalsIgnoreCase("NONE"))
            return null;
        Sound sound;
        try {
            sound = Sound.valueOf(this.config.getString("VisitSound"));
        } catch (IllegalArgumentException ex) {
            sound = Sound.BLOCK_DISPENSER_LAUNCH;
        }
        return sound;
    }

    public String getScoreboardHeader() {
        String header = this.config.getString("ScoreboardHeader");
        return header.length() > 32 ? "HEADER_TOO_LONG" : ColorUtil.format(header);
    }

    public Set<Map.Entry<String, Integer>> getScoreboardFormat(PlayerStats stats) {
        HashMap<String, Integer> scores = new HashMap<String, Integer>();
        List<String> lines = this.config.getStringList("ScoreboardFormat");
        int i = 0;
        for (String line : lines) {
            String formattedLine = ColorUtil.format(line
                    .replace("%UUID%", stats.getUuid())
                    .replace("%NAME%", stats.getName())
                    .replace("%DISTANCE%", DistanceFormatter.format(stats.getDistance()))
                    .replace("%KILLS%", format(stats.getKills()))
                    .replace("%DEATHS%", format(stats.getDeaths()))
                    .replace("%REGIONS%", format(stats.getRegionsExplored().size()))
                    .replace("%CURRENTREGION%", stats.getCurrentRegion().getName().replace("_", " "))
                    .replace("%CONTAINERS%", format(stats.getContainersLooted().size()))
                    .replace("%LEVEL%", format(stats.getCurrentLevel().getLevel()))
                    .replace("%XP%", formatAsInteger(stats.getExcessXp()))
                    .replace("%NEXTXP%", format(Level.getNextLevel(stats.getCurrentLevel()).getXp()))
                    .replace("%TOTALXP%", formatAsInteger(stats.getCurrentLevel().getPreviousTotal() +
                            stats.getExcessXp()))
            );
            if (formattedLine.length() >= 40)
                formattedLine = "LINE_TOO_LONG";
            scores.put(formattedLine, lines.size() - i);
            i++;
        }
        return scores.entrySet();
    }

    public Set<Map.Entry<String, Integer>> getEditingScoreboardFormat(Player p) {
        HashMap<String, Integer> scores = new HashMap<String, Integer>();
        List<String> lines = this.config.getStringList("EditingScoreboardFormat");
        int i = 0;
        for (String line : lines) {
            String formattedLine = ColorUtil.format(line
                    .replace("%NAME%", p.getName())
            );
            if (formattedLine.length() >= 40)
                formattedLine = "LINE_TOO_LONG";
            scores.put(formattedLine, lines.size() - i);
            i++;
        }
        return scores.entrySet();
    }

    public Set<Map.Entry<String, Integer>> getSpectatingScoreboardFormat(Player p) {
        HashMap<String, Integer> scores = new HashMap<String, Integer>();
        List<String> lines = this.config.getStringList("SpectatingScoreboardFormat");
        int i = 0;
        for (String line : lines) {
            String formattedLine = ColorUtil.format(line
                    .replace("%NAME%", p.getName())
            );
            if (formattedLine.length() >= 40)
                formattedLine = "LINE_TOO_LONG";
            scores.put(formattedLine, lines.size() - i);
            i++;
        }
        return scores.entrySet();
    }

    public String getLeaderboardEntryFormat(Leaderboard leaderboard, PlayerStats stats) {
        String initial = this.config.getString("LeaderboardEntryFormat");
        String finish = getNoIndicatorTypesLeaderboard().contains(leaderboard.getType().getFriendlyName().toUpperCase())
                ? initial
                .replaceAll("%TYPE%", "")
                .replaceAll("%TYPEUPPER%", "")
                .replaceAll("%TYPELOWER%", "") : initial;
        return ColorUtil.format(finish)
                .replace("%NUMBER%", String.valueOf(leaderboard.getPosition(stats)))
                .replaceAll("%COLOR%", leaderboard.getColor().toString())
                .replaceAll("%NAME%", stats.getName())
                .replaceAll("%NAMEUPPER%", stats.getName().toUpperCase())
                .replaceAll("%NAMELOWER%", stats.getName().toLowerCase())
                .replaceAll("%VALUE%", TypeUtil.getValue(leaderboard.getType(), stats))
                .replaceAll("%TYPE%", leaderboard.getType().getFriendlyName())
                .replace("%TYPEUPPER%", leaderboard.getType().name().toUpperCase())
                .replace("%TYPELOWER%", leaderboard.getType().name().toLowerCase());
    }

    public Set<String> getNoIndicatorTypesLeaderboard() {
        return new HashSet<String>(this.config.getStringList("NoIndicatorTypesLeaderboard"));
    }

    public int getLeaderboardUpdateInterval() {
        return this.config.getInt("LeaderboardUpdateInterval");
    }

    public Material getPreviewMaterial() {
        Material mat;
        try {
            mat = Material.getMaterial(this.config.getString("PreviewBlock"));
        } catch (IllegalArgumentException e) {
            mat = Material.GLASS;
        }
        return mat;
    }

    public String getRegionEntryHeader(RegionExplorable r) {
        return ColorUtil.format(this.config.getString("RegionEntryHeader").replace("%RNAME%", r.getName().replaceAll("_", " ")));
    }

    public int getRegionEntryFadeIn() {
        return this.config.getInt("RegionEntryTitleFadeIn");
    }

    public int getRegionEntryFadeOut() {
        return this.config.getInt("RegionEntryTitleFadeOut");
    }

    public int getRegionEntryStay() {
        return this.config.getInt("RegionEntryTitleStay");
    }

    public int getRegionEntryBlindness() {
        return this.config.getInt("RegionEntryBlindness");
    }

    public long getDefaultRefillSeconds() {
        return this.config.getLong("DefaultRefillSeconds");
    }

    public int getXPPer100M() {
        return this.config.getInt("XPPer100M");
    }

    public int getXPPerKill() {
        return this.config.getInt("XPPerKill");
    }

    public int getXPPerDeath() {
        return this.config.getInt("XPPerDeath");
    }

    public int getXPPerLootable() {
        return this.config.getInt("XPPerLootable");
    }

    public int getXPPerRegion() {
        return this.config.getInt("XPPerRegion");
    }

}
