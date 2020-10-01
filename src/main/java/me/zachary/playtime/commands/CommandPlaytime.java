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
            float fSeconds = iTick / 20;
            float fDays = fSeconds / 86400;
            fSeconds = ((int)fDays - fDays) * 86400;
            float fHours = fSeconds / 3600;
            fSeconds = ((int)fHours - fHours) * 3600;
            float fMinutes = fSeconds / 60;
            fSeconds = ((int)fMinutes - fMinutes) * 60;
            float fSeconds2 = fSeconds;

            player.sendMessage(((int)fSeconds2 * -1) + "S " + (int)fMinutes + "M " + ((int)fHours * -1) + "H " + (int)fDays + "D ");
        }else{
            player.sendMessage("You don't have permission");
        }

        return false;
    }

}
