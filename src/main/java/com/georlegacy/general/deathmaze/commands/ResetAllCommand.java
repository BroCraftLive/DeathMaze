package com.georlegacy.general.deathmaze.commands;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.PlayerStats;
import com.georlegacy.general.deathmaze.util.LangUtil;
import com.georlegacy.general.deathmaze.util.ScoreboardUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class ResetAllCommand {

    @SuppressWarnings({"SuspiciousMethodCalls", "ResultOfMethodCallIgnored", "unchecked"})
    @Command(permission = "deathmaze.admin.reset.all", subCommand = "resetall")
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Player player = (Player) sender;

        DeathMaze.getInstance().reloadAll();

        File file = new File(DeathMaze.getInstance().getDataFolder() + File.separator + "players" + File.separator);
        for (File child : Objects.requireNonNull(file.listFiles(File::isFile)))
            child.delete();
        for (Map.Entry<Player, PlayerStats> entry : (Set<Map.Entry<Player, PlayerStats>>) new HashSet<>(DeathMaze.getInstance().stats.entrySet()).clone()) {
            DeathMaze.getInstance().stats.remove(entry.getKey());
            PlayerStats stats = new PlayerStats();
            stats.setName(entry.getKey().getName());
            stats.setUuid(entry.getKey().getUniqueId().toString());
            ScoreboardUtil.send(entry.getKey(), stats);
        }
        player.sendMessage(LangUtil.PREFIX + LangUtil.RESET_ALL_STATS_SUCCESS);
        return true;
    }

}
