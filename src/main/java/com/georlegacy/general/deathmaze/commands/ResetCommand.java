package com.georlegacy.general.deathmaze.commands;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.PlayerStats;
import com.georlegacy.general.deathmaze.util.LangUtil;
import com.georlegacy.general.deathmaze.util.MazeEncoder;
import com.georlegacy.general.deathmaze.util.ScoreboardUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class ResetCommand {

    @Command(permission = "deathmaze.admin.reset", subCommand = "reset")
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Player player = (Player) sender;

        DeathMaze.getInstance().reloadAll();
        if (args.length == 1) {
            player.sendMessage(LangUtil.PREFIX + LangUtil.PLAYER_RESET_STATS_NO_PLAYER);
            return true;
        }
        if (!DeathMaze.getInstance().getMaze().getUuids().containsKey(args[1])) {
            player.sendMessage(LangUtil.PREFIX + LangUtil.PLAYER_RESET_STATS_NOT_PLAYER);
            return true;
        }
        File file = new File(DeathMaze.getInstance().getDataFolder() + File.separator + "players" + File.separator + DeathMaze.getInstance().getMaze().getUuids().get(args[1]) + ".dat");
        if (!file.exists()) {
            player.sendMessage(LangUtil.PREFIX + LangUtil.PLAYER_RESET_STATS_NOT_PLAYER);
            return true;
        }
        file.delete();
        Player toReset = Bukkit.getPlayerExact(args[1]);
        if (toReset == null) {
            player.sendMessage(LangUtil.PREFIX + LangUtil.PLAYER_RESET_STATS_SUCCESS);
            return true;
        }
        if (DeathMaze.getInstance().stats.containsKey(toReset)) {
            DeathMaze.getInstance().stats.remove(toReset);
            PlayerStats stats = new PlayerStats();
            stats.setName(toReset.getName());
            stats.setUuid(toReset.getUniqueId().toString());
            ScoreboardUtil.send(toReset, stats);
        }
        player.sendMessage(LangUtil.PREFIX + LangUtil.PLAYER_RESET_STATS_SUCCESS);
        return true;
    }

}
