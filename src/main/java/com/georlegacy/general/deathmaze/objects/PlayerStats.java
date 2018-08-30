package com.georlegacy.general.deathmaze.objects;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.enumeration.Level;
import com.georlegacy.general.deathmaze.util.ColorUtil;
import com.georlegacy.general.deathmaze.util.ScoreboardUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public @Data
class PlayerStats implements Serializable {

    static final long serialVersionUID = 5436645636546343654L;

    public PlayerStats() {
        regionsExplored = new ArrayList<RegionExplorable>();
        containersLooted = new ArrayList<ContainerLootable>();
        currentLevel = Level.ZERO;
    }

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String uuid;

    @Getter
    @Setter
    private Level currentLevel;

    @Getter
    @Setter
    private double excessXp;

    public void addXp(double xpToAdd) {
        excessXp += xpToAdd;
        Player p = Bukkit.getPlayerExact(name);
        if (excessXp >= Level.getNextLevel(currentLevel).getXp()) {
            excessXp = 0;
            currentLevel = Level.getNextLevel(currentLevel);
            DeathMaze.getInstance().getEconomy().depositPlayer(p, 1000);
            p.sendMessage(ColorUtil.format("&aYou have been awarded 1000■ for reaching level " +
                    currentLevel.name().toLowerCase() + "."));
            TextComponent end = new TextComponent("█");
            end.setColor(ChatColor.GREEN);
            TextComponent mid = new TextComponent(" LEVEL " + currentLevel.getLevel() + " ");
            mid.setColor(ChatColor.GREEN);
            mid.setBold(true);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, end, mid, end);
        }
        ScoreboardUtil.send(p, this);
    }

    @Getter
    @Setter
    private double distance;

    @Getter
    @Setter
    private int kills;

    @Getter
    @Setter
    private int deaths;

    @Setter
    private RegionExplorable currentRegion;

    public RegionExplorable getCurrentRegion() {
        if (currentRegion != null)
            return currentRegion;
        else
            return new NoRegion();
    }

    @Getter
    @Setter
    private List<RegionExplorable> regionsExplored;

    @Getter
    @Setter
    private List<ContainerLootable> containersLooted;

}