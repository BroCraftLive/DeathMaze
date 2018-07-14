package com.georlegacy.general.deathmaze.commands;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.Leaderboard;
import com.georlegacy.general.deathmaze.objects.enumeration.LeaderboardType;
import com.georlegacy.general.deathmaze.util.ColorUtil;
import com.georlegacy.general.deathmaze.util.LangUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaderboardCommand {

    @Command(permission = "deathmaze.admin.leaderboards", subCommand = "leaderboard")
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        if (args[1].equalsIgnoreCase("update")) {
            DeathMaze.getInstance().getMaze().getLeaderboards().forEach(leaderboard -> {
                if (leaderboard.update() == 0) {
                    Bukkit.getLogger().warning("An unexpected value has been returned, if this happens repeatedly, please report.");
                }
            });
            //TODO success updated DeathMaze.getInstance().getMaze().getLeaderboards().length() leaderboards
            return true;
        }
        if (args[1].equalsIgnoreCase("view")) {
            //TODO pagination yk
            return true;
        }
        if (args[1].equalsIgnoreCase("remove")) {
            if (args.length == 2) {
                //TODO provide name of leaderboard to remove dude
                return true;
            }
            for (Leaderboard board : DeathMaze.getInstance().getMaze().getLeaderboards()) {
                if (args[2].equalsIgnoreCase(board.getName())) {
                    DeathMaze.getInstance().getMaze().getLeaderboards().remove(board);
                    //TODO success
                    return true;
                }
            }
            //TODO not a leaderboard
            return true;
        }
        if (args[1].equalsIgnoreCase("add")) {
            if (args.length < 5) {
                //TODO provide name, type, and length of leaderboard to create
                return true;
            }
            for (Leaderboard board : DeathMaze.getInstance().getMaze().getLeaderboards()) {
                if (args[2].equalsIgnoreCase(board.getName())) {
                    //TODO already exists
                    return true;
                }
            }
            LeaderboardType typeToUse;
            int lengthToUse;
            try {
                typeToUse = LeaderboardType.valueOf(args[3].toUpperCase());
                lengthToUse = Integer.parseInt(args[4]);
            } catch (NumberFormatException ex) {
                //TODO not a number for length
                return true;
            } catch (IllegalArgumentException ex) {
                //TODO invalid type
                return true;
            }
            DeathMaze.getInstance().getMaze().getLeaderboards().add(new Leaderboard(
                    typeToUse,
                    player.getLocation(),
                    lengthToUse,
                    ColorUtil.format("&cDeathMaze Leaderboard for &e" + typeToUse.getFriendlyName()),
                    args[2]
            ));
            //TODO success
            return true;
        }
        if (args[1].equalsIgnoreCase("list")) {
            //TODO pagination structure
            return true;
        }
        player.sendMessage(LangUtil.PREFIX + LangUtil.INCORRECT_ARGS_MESSAGE);
        return true;
    }

}
