package com.georlegacy.general.deathmaze.commands;

import com.georlegacy.general.deathmaze.DeathMaze;
import com.georlegacy.general.deathmaze.objects.BossFight;
import com.georlegacy.general.deathmaze.objects.NoRegion;
import com.georlegacy.general.deathmaze.objects.RegionExplorable;
import com.georlegacy.general.deathmaze.objects.annotations.Setable;
import com.georlegacy.general.deathmaze.util.ColorUtil;
import com.georlegacy.general.deathmaze.util.FormatCheckUtil;
import com.georlegacy.general.deathmaze.util.LangUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BossfightCommand {

    @Command(permission = "deathmaze.admin.boss", subCommand = "boss")
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        // todo player sender only lmao
        Player player = (Player) sender;
        RegionExplorable currentPlayerRegion = new NoRegion();
        for (RegionExplorable r : DeathMaze.getInstance().getMaze().getRegions()) {
            CuboidSelection cs = new CuboidSelection(r.getPos1().getLocation().getWorld(), r.getPos1().getLocation(),
                    r.getPos2().getLocation());
            if (cs.contains(player.getLocation())) {
                currentPlayerRegion = r;
                break;
            }
        }

        if (args.length == 1) {
            player.sendMessage(LangUtil.PREFIX + LangUtil.INCORRECT_ARGS_MESSAGE);
            return true;
        }

        if (args[1].equalsIgnoreCase("set")) {
            if (args.length == 2) {
                player.sendMessage(LangUtil.PREFIX + "todo provide boss"); //todo provide boss
                return true;
            }
            BossFight bossFight = null; //TODO: remove null assignment
            //TODO: for loop through bosses if they dont exist then tell the user and return true, if it does exist
            // todo    ( p 2 )    then continue and assign
            Set<String> availableParameters = new HashSet<>(Arrays.asList("unlockedRegion",
                    "holding",
                    "offhand",
                    "head",
                    "body",
                    "leg",
                    "boot"));
            Class clazz = bossFight.getClass();
            Method[] methods = (Method[]) Arrays.stream(clazz.getMethods()).filter(
                    m -> m.isAnnotationPresent(Setable.class)).toArray();
            Arrays.stream(methods).forEachOrdered(m -> availableParameters.add(m.getAnnotation(Setable.class)
                    .setDisplayName()));
            if (args.length == 3) {
                //todo has not provided a parameter to set
                player.sendMessage(LangUtil.HELP_HEADER);
                availableParameters.forEach(p -> player.sendMessage(ColorUtil.format("&c" + p)));
                return true;
            }
            for (String parameter : availableParameters) {
                if (args[2].equalsIgnoreCase(parameter)) {
                    Optional<Method> optionalMethod = Arrays.stream(methods).filter(m ->
                            m.getAnnotation(Setable.class).setDisplayName().equals(parameter)).findFirst();
                    if (!optionalMethod.isPresent()) {
                        //todo tell the user
                        throw new RuntimeException("A fatal exception in the reflection parameter model system has occurred. " +
                                "Please report this event and discontinue use of the initial runtime jar until further information " +
                                "is found from developers.");
                    }
                    if (args.length == 4) {
                        player.sendMessage(ColorUtil.format("&eYou need to specify a &c" +
                                optionalMethod.get().getReturnType().toString().toLowerCase() +
                                " &etype parameter."));
                        return true;
                    }
                    FormatCheckUtil.FormatCheckResult formatCheckResult =
                            FormatCheckUtil.checkFormat(optionalMethod.get().getReturnType(), args[3]);
                    if (formatCheckResult.equals(FormatCheckUtil.FormatCheckResult.FAIL)) {
                        player.sendMessage(ColorUtil.format("&eYou need to specify a &c" +
                                optionalMethod.get().getReturnType().toString().toLowerCase() +
                                " &etype parameter."));
                        return true;
                    }
                    if (formatCheckResult.equals(FormatCheckUtil.FormatCheckResult.INCOMPATIBLE_TYPE)) {
                        //todo tell the user
                        throw new RuntimeException("A minor exception in the reflection parameter model system has occurred. " +
                                "Please discontinue use of the last executed command until further notice.");
                    }
                    Object newParam = null;
                    if (formatCheckResult.equals(FormatCheckUtil.FormatCheckResult.STRING)) {
                        newParam = args[3];
                    }
                    if (formatCheckResult.equals(FormatCheckUtil.FormatCheckResult.BOOLEAN)) {
                        newParam = Boolean.parseBoolean(args[3]);
                    }
                    if (formatCheckResult.equals(FormatCheckUtil.FormatCheckResult.INT)) {
                        newParam = Integer.parseInt(args[3]);
                    }
                    try {
                        optionalMethod.get().invoke(null, newParam);
                        // todo tell the user that they succeeded
                        // todo reload (maybe just bossfights but maybe all of dm)
                        return true;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        // todo tell the user
                        e.printStackTrace();
                        return true;
                    }
                }
            }
            //todo has provided an invalid parameter
            player.sendMessage(LangUtil.HELP_HEADER);
            availableParameters.forEach(p -> player.sendMessage(ColorUtil.format("&c" + p)));
            return true;
        }
        player.sendMessage(LangUtil.PREFIX + LangUtil.INCORRECT_ARGS_MESSAGE);
        return true;
    }

}
