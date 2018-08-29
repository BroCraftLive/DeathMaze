package com.georlegacy.general.deathmaze.listeners;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.NoRegion;
import com.georlegacy.general.deathmaze.objects.RegionExplorable;
import com.georlegacy.general.deathmaze.objects.enumeration.MazeMode;
import com.georlegacy.general.deathmaze.util.PlayerUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final DeathMaze plugin;
    public PlayerDeathListener(DeathMaze plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (plugin.getConfiguration().getEnabledWorlds().contains(e.getEntity().getWorld())) {
            if (plugin.getModes().getOrDefault(e.getEntity(), MazeMode.PLAYING).equals(MazeMode.PLAYING)) {
                PlayerUtil.addDeath(e.getEntity());

                for (RegionExplorable region : DeathMaze.getInstance().getMaze().getRegions()) {
                    if (new CuboidSelection(e.getEntity().getWorld(), region.getPos1().getLocation(),
                            region.getPos2().getLocation())
                            .contains(e.getEntity().getLocation())) {
                        PlayerUtil.setRegion(e.getEntity(), region);
                        return;
                    }
                }
                PlayerUtil.setRegion(e.getEntity(), new NoRegion());
            }
        }

    }
}
