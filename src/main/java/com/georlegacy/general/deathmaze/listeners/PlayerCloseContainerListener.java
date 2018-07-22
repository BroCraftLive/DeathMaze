package com.georlegacy.general.deathmaze.listeners;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.ContainerLootable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Arrays;

public class PlayerCloseContainerListener implements Listener {
    private final DeathMaze plugin;
    public PlayerCloseContainerListener(DeathMaze plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (!plugin.getConfiguration().getEnabledWorlds().contains(player.getWorld())) {
            return;
        }
        for (ContainerLootable container : plugin.getMaze().getContainers()) {
            if (container.getLocation().getLocation().equals(event.getInventory().getLocation())) {
                boolean isEmpty = Arrays.stream(event.getInventory().getContents()).allMatch(
                        itemStack -> itemStack == null || itemStack.getType().equals(Material.AIR));
                if (container.isHiddenWhenEmpty() && isEmpty)
                    event.getInventory().getLocation().getBlock().setType(Material.AIR);
            }
        }
    }

}
