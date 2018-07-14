package com.georlegacy.general.deathmaze.commands;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.util.LangUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class ResetCommand {

    @Command(permission = "deathmaze.admin.reset", subCommand = "reset")
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length == 1) {
            player.sendMessage(LangUtil.PREFIX + LangUtil.PLAYER_RESET_STATS_NO_PLAYER);
            return true;
        }
        if (!DeathMaze.getInstance().getMaze().getUuids().containsKey(args[1])) {
            player.sendMessage(LangUtil.PREFIX + LangUtil.PLAYER_RESET_STATS_NOT_PLAYER);
            return true;
        }
        File file = new File(DeathMaze.getInstance().getDataFolder() + File.separator + "players", DeathMaze.getInstance().getMaze().getUuids().get(args[1]) + ".dat");
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
        }
        player.sendMessage(LangUtil.PREFIX + LangUtil.PLAYER_RESET_STATS_SUCCESS);
        return true;
    }

}
