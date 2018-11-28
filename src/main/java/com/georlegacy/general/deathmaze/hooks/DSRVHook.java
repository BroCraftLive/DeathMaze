package com.georlegacy.general.deathmaze.hooks;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.core.entities.TextChannel;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class DSRVHook {

    @Getter
    private DiscordCommandManager commandManager;

    public DSRVHook() {
        this.commandManager = new DiscordCommandManager();

        this.commandManager.registerCommand(new DiscordCommand(new String[]{"leaderboard", "lb"},
                "<type>",
                "Displays the DeathMaze leaderboards") {
            @Override
            public void execute(DiscordGuildMessageReceivedEvent event) {
                TextChannel channel = event.getChannel();
                String[] args = event.getMessage().getContentRaw().split(" ");
                if (args.length == 1) {
                    channel.sendMessage("no bitch").queue();
                    return;
                }
                switch (args[1].toUpperCase()) {
                    case ("KILLS"):

                        break;
                    case ("DEATHS"):

                        break;
                    case ("DISTANCE"):

                        break;
                    case ("XP"):

                        break;
                    case ("LEVEL"):

                        break;
                    case ("LOOTABLES"):

                        break;
                    case ("REGIONS"):

                        break;
                    default:
                        channel.sendMessage("hbn").queue();
                        break;
                }
            }
        });
    }

    @Subscribe
    public void discordMessageReceived(DiscordGuildMessageReceivedEvent event) {
        commandManager.proccess(event);
    }

    private class DiscordCommandManager {
        @Getter
        private Set<DiscordCommand> commands;

        public DiscordCommandManager() {
            commands = new HashSet<>();
        }

        public void registerCommand(DiscordCommand command) {
            this.commands.add(command);
        }

        public boolean proccess(DiscordGuildMessageReceivedEvent event) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            System.out.println("proccessing");
            if (!args[0].startsWith("-"))
                return false;
            System.out.println("starts xorrextly");
            for (DiscordCommand command : commands)
                for (String alias : command.getAliases()) {
                    if (("-" + alias).equalsIgnoreCase(args[0])) {
                        System.out.println("exex");
                        command.execute(event);
                        return true;
                    }
                }
            return false;
        }
    }

    abstract class DiscordCommand {
        @Getter
        private String[] aliases;
        @Getter
        private String help;
        @Getter
        private String description;
        @Getter
        private boolean needsAdmin;

        public abstract void execute(DiscordGuildMessageReceivedEvent event);

        public DiscordCommand(String[] aliases,
                              String help,
                              String description,
                              boolean needsAdmin) {
            this.aliases = aliases;
            this.help = help;
            this.description = description;
            this.needsAdmin = needsAdmin;
        }

        public DiscordCommand(String[] aliases,
                              String help,
                              String description) {
            this(aliases,
                    help,
                    description,
                    false);
        }
    }

}
