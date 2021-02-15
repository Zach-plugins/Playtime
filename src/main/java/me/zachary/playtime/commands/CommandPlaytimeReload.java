package me.zachary.playtime.commands;

import me.zachary.playtime.Playtime;
import me.zachary.zachcore.commands.Command;
import me.zachary.zachcore.commands.CommandResult;
import me.zachary.zachcore.utils.MessageUtils;
import org.bukkit.entity.Player;

public class CommandPlaytimeReload extends Command {
    private Playtime plugin;

    public CommandPlaytimeReload(Playtime plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommand() {
        return "playtimereload";
    }

    @Override
    public CommandResult onPlayerExecute(Player player, String[] strings) {
        if(!player.hasPermission("playtime.reload")){
            MessageUtils.sendMessage(player, plugin.getFileManager().getMessageConfig().getStringList("no permission"));
            return CommandResult.COMPLETED;
        }
        plugin.getFileManager().reloadMessageConfig();
        plugin.getFileManager().reloadRewardConfig();
        plugin.getFileManager().reloadRewardGuiConfig();
        plugin.getFileManager().reloadLeaderboardGuiConfig();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        MessageUtils.sendMessage(player, "&cYou have successfully reload file of plugin.");
        return CommandResult.COMPLETED;
    }

    @Override
    public CommandResult onConsoleExecute(boolean b, String[] strings) {
        return CommandResult.COMPLETED;
    }
}
