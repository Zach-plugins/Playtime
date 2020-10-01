package me.zachary.playtime.commands;

import me.zachary.playtime.Playtime;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPlaytime implements CommandExecutor {

    private Playtime plugin;

    public CommandPlaytime(Playtime plugin){
        this.plugin = plugin;
        plugin.getCommand("playtime").setExecutor( this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (!(sender instanceof Player)){
            sender.sendMessage("You are not a player");
            return true;
        }
        Player player = (Player) sender;

        if (player.hasPermission("playtime.use")) {
            int iTick = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
            int iSeconds = iTick / 20;
            int iDays = 0;
            int iHours = 0;
            int iMinutes = 0;
            int iSeconds2 = 0;

            player.sendMessage(String.valueOf(iSeconds));
        }else{
            player.sendMessage("You don't have permission");
        }

        return false;
    }

}
