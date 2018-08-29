package com.georlegacy.general.deathmaze.commands;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.Leaderboard;
import com.georlegacy.general.deathmaze.objects.PlayerStats;
import com.georlegacy.general.deathmaze.objects.enumeration.LeaderboardType;
import com.georlegacy.general.deathmaze.objects.pagination.EmptyPaginationPage;
import com.georlegacy.general.deathmaze.objects.pagination.PaginationPage;
import com.georlegacy.general.deathmaze.objects.pagination.PaginationSet;
import com.georlegacy.general.deathmaze.objects.util.DelimitedStringBuilder;
import com.georlegacy.general.deathmaze.util.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class LeaderboardCommand {

    @Command(permission = "deathmaze.admin.leaderboards", subCommand = "leaderboard")
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        for (Map.Entry<Player, PlayerStats> entry : DeathMaze.getInstance().stats.entrySet())
            StatsEncoder.encode(entry.getValue());

        if (args.length == 1) {
            player.sendMessage(LangUtil.PREFIX + LangUtil.INCORRECT_ARGS_MESSAGE);
            return true;
        }

        if (args[1].equalsIgnoreCase("update")) {
            DeathMaze.getInstance().getMaze().getLeaderboards().forEach(leaderboard -> {
                if (leaderboard.update() == 0) {
                    Bukkit.getLogger().warning("An unexpected value has been returned, if this happens repeatedly, please report.");
                }
            });
            player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_UPDATE_SUCCESS);
            return true;
        }
        if (args[1].equalsIgnoreCase("view")) {
            if (args.length == 2) {
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_NO_TYPE);
                return true;
            }
            final LeaderboardType type;
            try {
                type = LeaderboardType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException ex) {
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_NOT_TYPE);
                return true;
            }
            List<PlayerStats> allStats = new LinkedList<PlayerStats>();
            for (File file : Objects.requireNonNull(new File(DeathMaze.getInstance().getDataFolder() +
                    File.separator + "players" + File.separator).listFiles())) {
                allStats.add(StatsEncoder.decode(file));
            }
            allStats.sort((o1, o2) -> {
                switch (type) {
                    case KILLS:
                        return Integer.compare(o2.getKills(), o1.getKills());
                    case DEATHS:
                        return Integer.compare(o2.getDeaths(), o1.getDeaths());
                    case LEVEL:
                        return Integer.compare(o2.getCurrentLevel().getLevel(), o1.getCurrentLevel().getLevel());
                    case XP:
                        return Math.round(Double.compare(o2.getCurrentLevel().getPreviousTotal() + o2.getExcessXp(),
                                o1.getCurrentLevel().getPreviousTotal() + o1.getExcessXp()));
                    case DISTANCE:
                        return Math.round(Double.compare(o2.getDistance(), o1.getDistance()));
                    case REGIONS:
                        return Integer.compare(o2.getRegionsExplored().size(), o1.getRegionsExplored().size());
                    case LOOTABLES:
                        return Integer.compare(o2.getContainersLooted().size(), o1.getContainersLooted().size());
                }
                throw new RuntimeException("A fatal error occurred whilst displaying a leaderboard to " + player.getName());
            });
            // Revisit and condense from nearly 500 lines to hopefully around 100
            if (type.equals(LeaderboardType.KILLS)) {
                PaginationSet storedSet = null;
                PaginationSet set;
                if (DeathMaze.getInstance().getPlayerLeaderboardKillsViewLists().containsKey(player.getUniqueId().toString())) {
                    storedSet = DeathMaze.getInstance().getPlayerLeaderboardKillsViewLists().get(player.getUniqueId().toString());
                }
                List<String> leaderboardEntries = new ArrayList<String>();
                allStats.forEach(stats -> leaderboardEntries.add(stats.getName() + " - " + NumberFormatter.format(stats.getKills())));
                if (leaderboardEntries.size() == 0) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_KILLS_NO_ENTRIES);
                    return true;
                }
                set = new PaginationSet(leaderboardEntries, 6);
                PaginationPage page;
                if (args.length == 3) {
                    page = set.getPage(0);
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_KILLS_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    DeathMaze.getInstance().getPlayerLeaderboardKillsViewLists().put(player.getUniqueId().toString(), set);
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                if (args[3].equalsIgnoreCase("next")) {
                    page = storedSet != null ? storedSet.getNextPage() : set.getNextPage();
                    if (page instanceof EmptyPaginationPage) {
                        player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_KILLS_NOT_PAGE);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_KILLS_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                if (args[3].equalsIgnoreCase("previous")) {
                    page = storedSet != null ? storedSet.getPreviousPage() : set.getPreviousPage();
                    if (page instanceof EmptyPaginationPage) {
                        player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_KILLS_NOT_PAGE);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_KILLS_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                int pageNo;
                try {
                    pageNo = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_KILLS_NOT_PAGE);
                    return true;
                }
                if ((pageNo > set.getPages().size()) || (pageNo <= 0)) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_KILLS_NOT_PAGE);
                    return true;
                }
                page = set.getPage(pageNo - 1);
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_KILLS_HEADER);
                for (String item : page.getItems()) {
                    player.sendMessage(ChatColor.GREEN + item);
                }
                DeathMaze.getInstance().getPlayerLeaderboardKillsViewLists().put(player.getUniqueId().toString(), set);
                sendListFooter(player, pageNo, type);
                return true;
            }

            if (type.equals(LeaderboardType.DEATHS)) {
                PaginationSet storedSet = null;
                PaginationSet set;
                if (DeathMaze.getInstance().getPlayerLeaderboardDeathsViewLists().containsKey(player.getUniqueId().toString())) {
                    storedSet = DeathMaze.getInstance().getPlayerLeaderboardDeathsViewLists().get(player.getUniqueId().toString());
                }
                List<String> leaderboardEntries = new ArrayList<String>();
                allStats.forEach(stats -> leaderboardEntries.add(stats.getName() + " - " + NumberFormatter.format(stats.getDeaths())));
                if (leaderboardEntries.size() == 0) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DEATHS_NO_ENTRIES);
                    return true;
                }
                set = new PaginationSet(leaderboardEntries, 6);
                PaginationPage page;
                if (args.length == 3) {
                    page = set.getPage(0);
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DEATHS_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    DeathMaze.getInstance().getPlayerLeaderboardDeathsViewLists().put(player.getUniqueId().toString(), set);
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                if (args[3].equalsIgnoreCase("next")) {
                    page = storedSet != null ? storedSet.getNextPage() : set.getNextPage();
                    if (page instanceof EmptyPaginationPage) {
                        player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DEATHS_NOT_PAGE);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DEATHS_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                if (args[3].equalsIgnoreCase("previous")) {
                    page = storedSet != null ? storedSet.getPreviousPage() : set.getPreviousPage();
                    if (page instanceof EmptyPaginationPage) {
                        player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DEATHS_NOT_PAGE);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DEATHS_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                int pageNo;
                try {
                    pageNo = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DEATHS_NOT_PAGE);
                    return true;
                }
                if ((pageNo > set.getPages().size()) || (pageNo <= 0)) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DEATHS_NOT_PAGE);
                    return true;
                }
                page = set.getPage(pageNo - 1);
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DEATHS_HEADER);
                for (String item : page.getItems()) {
                    player.sendMessage(ChatColor.GREEN + item);
                }
                DeathMaze.getInstance().getPlayerLeaderboardDeathsViewLists().put(player.getUniqueId().toString(), set);
                sendListFooter(player, pageNo, type);
                return true;
            }

            if (type.equals(LeaderboardType.LEVEL)) {
                PaginationSet storedSet = null;
                PaginationSet set;
                if (DeathMaze.getInstance().getPlayerLeaderboardLevelViewLists().containsKey(player.getUniqueId().toString())) {
                    storedSet = DeathMaze.getInstance().getPlayerLeaderboardLevelViewLists().get(player.getUniqueId().toString());
                }
                List<String> leaderboardEntries = new ArrayList<String>();
                allStats.forEach(stats -> leaderboardEntries.add(stats.getName() + " - " + stats.getCurrentLevel().getLevel()));
                if (leaderboardEntries.size() == 0) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LEVEL_NO_ENTRIES);
                    return true;
                }
                set = new PaginationSet(leaderboardEntries, 6);
                PaginationPage page;
                if (args.length == 3) {
                    page = set.getPage(0);
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LEVEL_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    DeathMaze.getInstance().getPlayerLeaderboardLevelViewLists().put(player.getUniqueId().toString(), set);
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                if (args[3].equalsIgnoreCase("next")) {
                    page = storedSet != null ? storedSet.getNextPage() : set.getNextPage();
                    if (page instanceof EmptyPaginationPage) {
                        player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LEVEL_NOT_PAGE);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LEVEL_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                if (args[3].equalsIgnoreCase("previous")) {
                    page = storedSet != null ? storedSet.getPreviousPage() : set.getPreviousPage();
                    if (page instanceof EmptyPaginationPage) {
                        player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LEVEL_NOT_PAGE);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LEVEL_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                int pageNo;
                try {
                    pageNo = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LEVEL_NOT_PAGE);
                    return true;
                }
                if ((pageNo > set.getPages().size()) || (pageNo <= 0)) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LEVEL_NOT_PAGE);
                    return true;
                }
                page = set.getPage(pageNo - 1);
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LEVEL_HEADER);
                for (String item : page.getItems()) {
                    player.sendMessage(ChatColor.GREEN + item);
                }
                DeathMaze.getInstance().getPlayerLeaderboardLevelViewLists().put(player.getUniqueId().toString(), set);
                sendListFooter(player, pageNo, type);
                return true;
            }

            if (type.equals(LeaderboardType.XP)) {
                PaginationSet storedSet = null;
                PaginationSet set;
                if (DeathMaze.getInstance().getPlayerLeaderboardXPViewLists().containsKey(player.getUniqueId().toString())) {
                    storedSet = DeathMaze.getInstance().getPlayerLeaderboardXPViewLists().get(player.getUniqueId().toString());
                }
                List<String> leaderboardEntries = new ArrayList<String>();
                allStats.forEach(stats -> leaderboardEntries.add(stats.getName() + " - " + NumberFormatter.format(stats.getCurrentLevel().getPreviousTotal() + stats.getExcessXp())));
                if (leaderboardEntries.size() == 0) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_XP_NO_ENTRIES);
                    return true;
                }
                set = new PaginationSet(leaderboardEntries, 6);
                PaginationPage page;
                if (args.length == 3) {
                    page = set.getPage(0);
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_XP_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    DeathMaze.getInstance().getPlayerLeaderboardXPViewLists().put(player.getUniqueId().toString(), set);
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                if (args[3].equalsIgnoreCase("next")) {
                    page = storedSet != null ? storedSet.getNextPage() : set.getNextPage();
                    if (page instanceof EmptyPaginationPage) {
                        player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_XP_NOT_PAGE);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_XP_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                if (args[3].equalsIgnoreCase("previous")) {
                    page = storedSet != null ? storedSet.getPreviousPage() : set.getPreviousPage();
                    if (page instanceof EmptyPaginationPage) {
                        player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_XP_NOT_PAGE);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_XP_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                int pageNo;
                try {
                    pageNo = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_XP_NOT_PAGE);
                    return true;
                }
                if ((pageNo > set.getPages().size()) || (pageNo <= 0)) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_XP_NOT_PAGE);
                    return true;
                }
                page = set.getPage(pageNo - 1);
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_XP_HEADER);
                for (String item : page.getItems()) {
                    player.sendMessage(ChatColor.GREEN + item);
                }
                DeathMaze.getInstance().getPlayerLeaderboardXPViewLists().put(player.getUniqueId().toString(), set);
                sendListFooter(player, pageNo, type);
                return true;
            }

            if (type.equals(LeaderboardType.DISTANCE)) {
                PaginationSet storedSet = null;
                PaginationSet set;
                if (DeathMaze.getInstance().getPlayerLeaderboardDistanceViewLists().containsKey(player.getUniqueId().toString())) {
                    storedSet = DeathMaze.getInstance().getPlayerLeaderboardDistanceViewLists().get(player.getUniqueId().toString());
                }
                List<String> leaderboardEntries = new ArrayList<String>();
                allStats.forEach(stats -> leaderboardEntries.add(stats.getName() + " - " + DistanceFormatter.format(stats.getDistance())));
                if (leaderboardEntries.size() == 0) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DISTANCE_NO_ENTRIES);
                    return true;
                }
                set = new PaginationSet(leaderboardEntries, 6);
                PaginationPage page;
                if (args.length == 3) {
                    page = set.getPage(0);
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DISTANCE_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    DeathMaze.getInstance().getPlayerLeaderboardDistanceViewLists().put(player.getUniqueId().toString(), set);
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                if (args[3].equalsIgnoreCase("next")) {
                    page = storedSet != null ? storedSet.getNextPage() : set.getNextPage();
                    if (page instanceof EmptyPaginationPage) {
                        player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DISTANCE_NOT_PAGE);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DISTANCE_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                if (args[3].equalsIgnoreCase("previous")) {
                    page = storedSet != null ? storedSet.getPreviousPage() : set.getPreviousPage();
                    if (page instanceof EmptyPaginationPage) {
                        player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DISTANCE_NOT_PAGE);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DISTANCE_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                int pageNo;
                try {
                    pageNo = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DISTANCE_NOT_PAGE);
                    return true;
                }
                if ((pageNo > set.getPages().size()) || (pageNo <= 0)) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DISTANCE_NOT_PAGE);
                    return true;
                }
                page = set.getPage(pageNo - 1);
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_DISTANCE_HEADER);
                for (String item : page.getItems()) {
                    player.sendMessage(ChatColor.GREEN + item);
                }
                DeathMaze.getInstance().getPlayerLeaderboardDistanceViewLists().put(player.getUniqueId().toString(), set);
                sendListFooter(player, pageNo, type);
                return true;
            }

            if (type.equals(LeaderboardType.REGIONS)) {
                PaginationSet storedSet = null;
                PaginationSet set;
                if (DeathMaze.getInstance().getPlayerLeaderboardRegionsViewLists().containsKey(player.getUniqueId().toString())) {
                    storedSet = DeathMaze.getInstance().getPlayerLeaderboardRegionsViewLists().get(player.getUniqueId().toString());
                }
                List<String> leaderboardEntries = new ArrayList<String>();
                allStats.forEach(stats -> leaderboardEntries.add(stats.getName() + " - " + NumberFormatter.format(stats.getRegionsExplored().size())));
                if (leaderboardEntries.size() == 0) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_REGIONS_NO_ENTRIES);
                    return true;
                }
                set = new PaginationSet(leaderboardEntries, 6);
                PaginationPage page;
                if (args.length == 3) {
                    page = set.getPage(0);
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_REGIONS_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    DeathMaze.getInstance().getPlayerLeaderboardRegionsViewLists().put(player.getUniqueId().toString(), set);
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                if (args[3].equalsIgnoreCase("next")) {
                    page = storedSet != null ? storedSet.getNextPage() : set.getNextPage();
                    if (page instanceof EmptyPaginationPage) {
                        player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_REGIONS_NOT_PAGE);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_REGIONS_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                if (args[3].equalsIgnoreCase("previous")) {
                    page = storedSet != null ? storedSet.getPreviousPage() : set.getPreviousPage();
                    if (page instanceof EmptyPaginationPage) {
                        player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_REGIONS_NOT_PAGE);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_REGIONS_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                int pageNo;
                try {
                    pageNo = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_REGIONS_NOT_PAGE);
                    return true;
                }
                if ((pageNo > set.getPages().size()) || (pageNo <= 0)) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_REGIONS_NOT_PAGE);
                    return true;
                }
                page = set.getPage(pageNo - 1);
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_REGIONS_HEADER);
                for (String item : page.getItems()) {
                    player.sendMessage(ChatColor.GREEN + item);
                }
                DeathMaze.getInstance().getPlayerLeaderboardRegionsViewLists().put(player.getUniqueId().toString(), set);
                sendListFooter(player, pageNo, type);
                return true;
            }

            if (type.equals(LeaderboardType.LOOTABLES)) {
                PaginationSet storedSet = null;
                PaginationSet set;
                if (DeathMaze.getInstance().getPlayerLeaderboardLootablesViewLists().containsKey(player.getUniqueId().toString())) {
                    storedSet = DeathMaze.getInstance().getPlayerLeaderboardLootablesViewLists().get(player.getUniqueId().toString());
                }
                List<String> leaderboardEntries = new ArrayList<String>();
                allStats.forEach(stats -> leaderboardEntries.add(stats.getName() + " - " + NumberFormatter.format(stats.getContainersLooted().size())));
                if (leaderboardEntries.size() == 0) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LOOTABLES_NO_ENTRIES);
                    return true;
                }
                set = new PaginationSet(leaderboardEntries, 6);
                PaginationPage page;
                if (args.length == 3) {
                    page = set.getPage(0);
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LOOTABLES_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    DeathMaze.getInstance().getPlayerLeaderboardLootablesViewLists().put(player.getUniqueId().toString(), set);
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                if (args[3].equalsIgnoreCase("next")) {
                    page = storedSet != null ? storedSet.getNextPage() : set.getNextPage();
                    if (page instanceof EmptyPaginationPage) {
                        player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LOOTABLES_NOT_PAGE);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LOOTABLES_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                if (args[3].equalsIgnoreCase("previous")) {
                    page = storedSet != null ? storedSet.getPreviousPage() : set.getPreviousPage();
                    if (page instanceof EmptyPaginationPage) {
                        player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LOOTABLES_NOT_PAGE);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LOOTABLES_HEADER);
                    for (String item : page.getItems()) {
                        player.sendMessage(ChatColor.GREEN + item);
                    }
                    sendListFooter(player, page.getNumber(), type);
                    return true;
                }
                int pageNo;
                try {
                    pageNo = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LOOTABLES_NOT_PAGE);
                    return true;
                }
                if ((pageNo > set.getPages().size()) || (pageNo <= 0)) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LOOTABLES_NOT_PAGE);
                    return true;
                }
                page = set.getPage(pageNo - 1);
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_LIST_LOOTABLES_HEADER);
                for (String item : page.getItems()) {
                    player.sendMessage(ChatColor.GREEN + item);
                }
                DeathMaze.getInstance().getPlayerLeaderboardLootablesViewLists().put(player.getUniqueId().toString(), set);
                sendListFooter(player, pageNo, type);
                return true;
            }

        }
        if (args[1].equalsIgnoreCase("tp")) {
            if (args.length == 2) {
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_TP_NO_LEADERBOARD);
                return true;
            }
            for (Leaderboard leaderboard : DeathMaze.getInstance().getMaze().getLeaderboards()) {
                Location loc = leaderboard.getLocation().getLocation();
                if (leaderboard.getName().equalsIgnoreCase(args[2])) {
                    player.teleport(loc);
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_TP_SUCCESS);
                    return true;
                }
            }
            player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_TP_NOT_LEADERBOARD);
            return true;
        }
        if (args[1].equalsIgnoreCase("remove")) {
            if (args.length == 2) {
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_REMOVE_NO_LEADERBOARD);
                return true;
            }
            for (Leaderboard board : DeathMaze.getInstance().getMaze().getLeaderboards()) {
                if (args[2].equalsIgnoreCase(board.getName())) {
                    DeathMaze.getInstance().getMaze().getLeaderboards().remove(board);
                    board.delete();
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_REMOVE_SUCCESS);
                    return true;
                }
            }
            player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_REMOVE_NOT_LEADERBOARD);
            return true;
        }
        if (args[1].equalsIgnoreCase("add")) {
            if (args.length < 6) {
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_ADD_NO_ARGS);
                return true;
            }
            for (Leaderboard board : DeathMaze.getInstance().getMaze().getLeaderboards()) {
                if (args[2].equalsIgnoreCase(board.getName())) {
                    player.sendMessage(LangUtil.PREFIX +LangUtil.LEADERBOARD_ADD_EXISTS);
                    return true;
                }
            }
            LeaderboardType typeToUse;
            ChatColor colorToUse;
            int lengthToUse;
            try {
                typeToUse = LeaderboardType.valueOf(args[3].toUpperCase());
                lengthToUse = Integer.parseInt(args[4]);
            } catch (NumberFormatException ex) {
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_ADD_LENGTH_NOT_NUMBER);
                return true;
            } catch (IllegalArgumentException ex) {
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_ADD_TYPE_NOT_TYPE);
                return true;
            }
            try {
                colorToUse = ChatColor.getByChar(args[5]);
            } catch (IllegalArgumentException ex) {
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_ADD_COLOR_NOT_COLOR);
                return true;
            }
            Leaderboard leaderboard = new Leaderboard(
                    typeToUse,
                    player.getLocation(),
                    lengthToUse,
                    ColorUtil.format("&cDeathMaze Leaderboard for &e" + typeToUse.getFriendlyName()),
                    colorToUse,
                    args[2]
            );
            DeathMaze.getInstance().getMaze().getLeaderboards().add(leaderboard);
            leaderboard.update();
            player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARD_ADD_SUCCESS);
            return true;
        }
        if (args[1].equalsIgnoreCase("set")) {
            if (args.length < 4) {
                player.sendMessage(LangUtil.PREFIX + LangUtil.HELP_HEADER);
                player.sendMessage(ColorUtil.format("&c/deathmaze leaderboard set <leaderboard> header <header> - &7Sets the header for the leaderboard."));
                player.sendMessage(ColorUtil.format("&c/deathmaze leaderboard set <leaderboard> color <color code> - &7Sets the color for the leaderboard."));
                player.sendMessage(ColorUtil.format("&c/deathmaze leaderboard set <leaderboard> type <type> - &7Sets the type for the leaderboard."));
                player.sendMessage(ColorUtil.format("&c/deathmaze leaderboard set <leaderboard> length <length> - &7Sets the length of the leaderboard."));
                return true;
            }
            for (Leaderboard leaderboard : DeathMaze.getInstance().getMaze().getLeaderboards()) {
                if (leaderboard.getName().equalsIgnoreCase(args[2])) {
                    if (args[3].equalsIgnoreCase("header")) {
                        if (args.length < 5) {
                            player.sendMessage(LangUtil.PREFIX + LangUtil.SET_LEADERBOARD_HEADER_NO_HEADER);
                            return true;
                        }
                        DelimitedStringBuilder header = new DelimitedStringBuilder();
                        Arrays.asList(args).subList(4, args.length).forEach(header::append);
                        leaderboard.setHeader(header.toString());
                        player.sendMessage(LangUtil.PREFIX + LangUtil.SET_LEADERBOARD_HEADER_SUCCESS);
                        return true;
                    }
                    if (args[3].equalsIgnoreCase("color")) {
                        if (args.length < 5) {
                            player.sendMessage(LangUtil.PREFIX + LangUtil.SET_LEADERBOARD_COLOR_NO_COLOR);
                            return true;
                        }
                        ChatColor color;
                        try {
                            color = ChatColor.getByChar(args[4]);
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(LangUtil.PREFIX + LangUtil.SET_LEADERBOARD_COLOR_NOT_COLOR);
                            return true;
                        }
                        leaderboard.setColor(color);
                        player.sendMessage(LangUtil.PREFIX + LangUtil.SET_LEADERBOARD_COLOR_SUCCESS);
                        return true;
                    }
                    if (args[3].equalsIgnoreCase("type")) {
                        if (args.length < 5) {
                            player.sendMessage(LangUtil.PREFIX + LangUtil.SET_LEADERBOARD_TYPE_NO_TYPE);
                            return true;
                        }
                        LeaderboardType type;
                        try {
                            type = LeaderboardType.valueOf(args[4].toUpperCase());
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(LangUtil.PREFIX + LangUtil.SET_LEADERBOARD_TYPE_NOT_TYPE);
                            return true;
                        }
                        leaderboard.setType(type);
                        player.sendMessage(LangUtil.PREFIX + LangUtil.SET_LEADERBOARD_TYPE_SUCCESS);
                        return true;
                    }
                    if (args[3].equalsIgnoreCase("length")) {
                        if (args.length < 5) {
                            player.sendMessage(LangUtil.PREFIX + LangUtil.SET_LEADERBOARD_LENGTH_NO_LENGTH);
                            return true;
                        }
                        int length;
                        try {
                            length = Integer.parseInt(args[4]);
                        } catch (NumberFormatException ex) {
                            player.sendMessage(LangUtil.PREFIX + LangUtil.SET_LEADERBOARD_LENGTH_NOT_NUMBER);
                            return true;
                        }
                        leaderboard.setLength(length);
                        player.sendMessage(LangUtil.PREFIX + LangUtil.SET_LEADERBOARD_LENGTH_SUCCESS);
                        return true;
                    }
                    player.sendMessage(LangUtil.PREFIX + LangUtil.HELP_HEADER);
                    player.sendMessage(ColorUtil.format("&c/deathmaze leaderboard set <leaderboard> header <header> - &7Sets the header for the leaderboard."));
                    player.sendMessage(ColorUtil.format("&c/deathmaze leaderboard set <leaderboard> color <color code> - &7Sets the color for the leaderboard."));
                    player.sendMessage(ColorUtil.format("&c/deathmaze leaderboard set <leaderboard> type <type> - &7Sets the type for the leaderboard."));
                    player.sendMessage(ColorUtil.format("&c/deathmaze leaderboard set <leaderboard> length <length> - &7Sets the length of the leaderboard."));
                    return true;
                }
            }
            player.sendMessage(LangUtil.PREFIX + LangUtil.SET_LEADERBOARD_NOT_LEADERBOARD);
            return true;
        }
        if (args[1].equalsIgnoreCase("list")) {
            PaginationSet storedSet = null;
            PaginationSet set;
            if (DeathMaze.getInstance().getPlayerLeaderboardLists().containsKey(player.getUniqueId().toString())) {
                storedSet = DeathMaze.getInstance().getPlayerLeaderboardLists().get(player.getUniqueId().toString());
            }
            List<String> leaderboardNames = new ArrayList<String>();
            DeathMaze.getInstance().getMaze().getLeaderboards().forEach(ldbd -> leaderboardNames.add(ldbd.getName()));
            if (leaderboardNames.size() == 0) {
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARDS_LIST_NO_LEADERBOARDS);
                return true;
            }
            set = new PaginationSet(leaderboardNames, 6);
            PaginationPage page;
            if (args.length == 2) {
                page = set.getPage(0);
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARDS_LIST_HEADER);
                for (String item : page.getItems()) {
                    player.sendMessage(ChatColor.GREEN + item);
                }
                DeathMaze.getInstance().getPlayerLeaderboardLists().put(player.getUniqueId().toString(), set);
                sendListFooter(player, page.getNumber());
                return true;
            }
            if (args[2].equalsIgnoreCase("next")) {
                page = storedSet!=null ? storedSet.getNextPage() : set.getNextPage();
                if (page instanceof EmptyPaginationPage) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARDS_LIST_NOT_PAGE);
                    return true;
                }
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARDS_LIST_HEADER);
                for (String item : page.getItems()) {
                    player.sendMessage(ChatColor.GREEN + item);
                }
                sendListFooter(player, page.getNumber());
                return true;
            }
            if (args[2].equalsIgnoreCase("previous")) {
                page = storedSet!=null ? storedSet.getPreviousPage() : set.getPreviousPage();
                if (page instanceof EmptyPaginationPage) {
                    player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARDS_LIST_NOT_PAGE);
                    return true;
                }
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARDS_LIST_HEADER);
                for (String item : page.getItems()) {
                    player.sendMessage(ChatColor.GREEN + item);
                }
                sendListFooter(player, page.getNumber());
                return true;
            }
            int pageNo;
            try {
                pageNo = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARDS_LIST_NOT_PAGE);
                return true;
            }
            if ((pageNo > set.getPages().size()) || (pageNo <= 0)) {
                player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARDS_LIST_NOT_PAGE);
                return true;
            }
            page = set.getPage(pageNo - 1);
            player.sendMessage(LangUtil.PREFIX + LangUtil.LEADERBOARDS_LIST_HEADER);
            for (String item : page.getItems()) {
                player.sendMessage(ChatColor.GREEN + item);
            }
            DeathMaze.getInstance().getPlayerLeaderboardLists().put(player.getUniqueId().toString(), set);
            sendListFooter(player, pageNo);
            return true;
        }
        player.sendMessage(LangUtil.PREFIX + LangUtil.INCORRECT_ARGS_MESSAGE);
        return true;
    }

    private void sendListFooter(Player player, int pageNumber, LeaderboardType type) {
        TextComponent end = new TextComponent("]----[");
        end.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        end.setStrikethrough(true);

        TextComponent previous = new TextComponent("◀◀");
        previous.setColor(net.md_5.bungee.api.ChatColor.DARK_RED);
        previous.setBold(true);
        previous.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Previous")}));
        previous.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathmaze leaderboard view " +
                type.getFriendlyName() +
                " previous"));

        TextComponent splitLeft = new TextComponent("]+");
        splitLeft.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        splitLeft.setStrikethrough(true);

        TextComponent number = new TextComponent(String.valueOf(pageNumber));
        number.setBold(true);
        number.setColor(net.md_5.bungee.api.ChatColor.RED);

        TextComponent splitRight = new TextComponent("+[");
        splitRight.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        splitRight.setStrikethrough(true);

        TextComponent next = new TextComponent("▶▶");
        next.setColor(net.md_5.bungee.api.ChatColor.DARK_RED);
        next.setBold(true);
        next.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Next")}));
        next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathmaze leaderboard view " +
                type.getFriendlyName() +
                " next"));

        player.spigot().sendMessage(end, previous, splitLeft, number, splitRight, next, end);
    }

    private void sendListFooter(Player player, int pageNumber) {
        TextComponent end = new TextComponent("]----[");
        end.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        end.setStrikethrough(true);

        TextComponent previous = new TextComponent("◀◀");
        previous.setColor(net.md_5.bungee.api.ChatColor.DARK_RED);
        previous.setBold(true);
        previous.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Previous")}));
        previous.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathmaze leaderboard list previous"));

        TextComponent splitLeft = new TextComponent("]+");
        splitLeft.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        splitLeft.setStrikethrough(true);

        TextComponent number = new TextComponent(String.valueOf(pageNumber));
        number.setBold(true);
        number.setColor(net.md_5.bungee.api.ChatColor.RED);

        TextComponent splitRight = new TextComponent("+[");
        splitRight.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        splitRight.setStrikethrough(true);

        TextComponent next = new TextComponent("▶▶");
        next.setColor(net.md_5.bungee.api.ChatColor.DARK_RED);
        next.setBold(true);
        next.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Next")}));
        next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathmaze leaderboard list next"));

        player.spigot().sendMessage(end, previous, splitLeft, number, splitRight, next, end);
    }

}
