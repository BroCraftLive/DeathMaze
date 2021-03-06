package com.georlegacy.general.deathmaze.listeners;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.enumeration.MazeMode;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangeWorldListener implements Listener {
    private final DeathMaze plugin;
    public PlayerChangeWorldListener(DeathMaze plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onChange(PlayerChangedWorldEvent e) {

        if (!plugin.getConfiguration().getEnabledWorlds().contains(e.getPlayer().getWorld())) {
            e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        } else {
            e.getPlayer().sendMessage(plugin.getConfiguration().getVisitMessage(e.getPlayer()));
        }

        plugin.getModes().put(e.getPlayer(), MazeMode.getByGameMode(e.getPlayer().getGameMode()));

    }

}
