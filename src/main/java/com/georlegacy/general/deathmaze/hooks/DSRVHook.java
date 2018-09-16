package com.georlegacy.general.deathmaze.hooks;

import com.google.common.eventbus.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import lombok.Getter;

import java.util.Set;

public class DSRVHook {

    @Getter private DiscordCommandManager commandManager;

    public DSRVHook() {
        this.commandManager = new DiscordCommandManager();

        this.commandManager.registerCommand(new DiscordCommand(new String[]{"leaderboard", "lb"},
                "<type>",
                "Displays the DeathMaze leaderboards") {
            @Override
            public void execute(DiscordGuildMessageReceivedEvent event) {

            }
        });
    }

    @Subscribe
    public void discordMessageReceived(DiscordGuildMessageReceivedEvent event) {

    }

    private class DiscordCommandManager {
        @Getter
        private Set<DiscordCommand> commands;

        public void registerCommand(DiscordCommand command) {
            this.commands.add(command);
        }

        public boolean proccess(DiscordGuildMessageReceivedEvent event) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            if (!args[0].startsWith("-"))
                return false;
            for (DiscordCommand command : commands)
                for (String alias : command.getAliases()) {
                    if (alias.equalsIgnoreCase(args[0])) {
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
