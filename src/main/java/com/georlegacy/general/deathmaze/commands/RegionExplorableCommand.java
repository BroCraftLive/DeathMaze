package com.georlegacy.general.deathmaze.commands;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.RegionExplorable;
import com.georlegacy.general.deathmaze.util.LangUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegionExplorableCommand {

    @Command(permission = "deathmaze.admin.region", subCommand = "region")
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (args[1].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                p.sendMessage(LangUtil.PREFIX + LangUtil.INCORRECT_ARGS_MESSAGE);
                return true;
            }
            for (RegionExplorable r : DeathMaze.getInstance().getMaze().getRegions()) {
                if (args[2].equalsIgnoreCase(r.getName())) {
                    p.sendMessage(LangUtil.PREFIX + LangUtil.ADD_REGION_ALREADY_EXISTS);
                    return true;
                }
            }
            Selection s = DeathMaze.getInstance().getWorldedit().getSelection(p);
            if (s == null) {
                p.sendMessage(LangUtil.PREFIX + LangUtil.ADD_REGION_NO_SELECTION);
                return true;
            }
            if (s.getHeight() < 2 || s.getLength() < 2 || s.getWidth() < 2) {
                p.sendMessage(LangUtil.PREFIX + LangUtil.ADD_REGION_SELECTION_TOO_SMALL);
                return true;
            }
            if (!(s instanceof CuboidSelection)) {
                p.sendMessage(LangUtil.PREFIX + LangUtil.ADD_REGION_NON_CUBOID_SELECTION);
                return true;
            }
            CuboidSelection cs = (CuboidSelection) s;
            for (RegionExplorable r : DeathMaze.getInstance().getMaze().getRegions()) {
                if (!r.getPos1().getLocation().getWorld().equals(cs.getWorld())) {
                    continue;
                }
                if (cs.getMinimumPoint().getX() > r.getPos2().getLocation().getX() || cs.getMaximumPoint().getX() > r.getPos1().getLocation().getX()) {
                    p.sendMessage(LangUtil.PREFIX + LangUtil.ADD_REGION_EXISTING_OVERLAP);
                    return true;
                }
                if (cs.getMinimumPoint().getY() > r.getPos2().getLocation().getY() || cs.getMaximumPoint().getY() > r.getPos1().getLocation().getY()) {
                    p.sendMessage(LangUtil.PREFIX + LangUtil.ADD_REGION_EXISTING_OVERLAP);
                    return true;
                }
                if (cs.getMinimumPoint().getZ() > r.getPos2().getLocation().getZ() || cs.getMaximumPoint().getZ() > r.getPos1().getLocation().getZ()) {
                    p.sendMessage(LangUtil.PREFIX + LangUtil.ADD_REGION_EXISTING_OVERLAP);
                    return true;
                }
            }
            DeathMaze.getInstance().getMaze().getRegions().add(new RegionExplorable(
                    args[2],
                    cs.getMinimumPoint(),
                    cs.getMinimumPoint()
            ));
            p.sendMessage(LangUtil.PREFIX + LangUtil.ADD_REGION_SUCCESS);
            DeathMaze.getInstance().reloadAll();
            return true;
        }
        if (args[1].equalsIgnoreCase("remove")) {

        }
        if (args[1].equalsIgnoreCase("set")) {

        }
        if (args[1].equalsIgnoreCase("splash")) {

        }


        return true;
    }

}
