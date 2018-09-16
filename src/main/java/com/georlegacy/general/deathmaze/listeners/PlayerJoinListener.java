package com.georlegacy.general.deathmaze.listeners;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.PlayerStats;
import com.georlegacy.general.deathmaze.util.ScoreboardUtil;
import com.georlegacy.general.deathmaze.util.StatsEncoder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.ScoreboardManager;

public class PlayerJoinListener implements Listener {
    private final DeathMaze plugin;
    public PlayerJoinListener(DeathMaze plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        plugin.getMaze().getUuids().put(p.getName(), p.getUniqueId().toString());

        if (plugin.getConfiguration().getEnabledWorlds().contains(p.getWorld())) {
            PlayerStats stats;
            if (plugin.stats.containsKey(p)) {
                stats = plugin.stats.get(p);
            } else {
                if (StatsEncoder.decode(p.getUniqueId().toString()) != null) {
                    plugin.stats.put(p, StatsEncoder.decode(p.getUniqueId().toString()));
                    stats = plugin.stats.get(p);
                } else {
                    stats = new PlayerStats();
                    stats.setName(p.getName());
                    stats.setUuid(p.getUniqueId().toString());
                    plugin.stats.put(p, stats);
                }
            }

            p.sendMessage(plugin.getConfiguration().getVisitMessage(p));
            ScoreboardUtil.send(e.getPlayer(), stats);
            return;
        }

        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

    }

}
