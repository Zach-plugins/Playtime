package me.zachary.playtime.commands;

import me.zachary.playtime.Playtime;
import me.zachary.zachcore.commands.Command;
import me.zachary.zachcore.commands.CommandResult;
import me.zachary.zachcore.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class CommandPlaytime extends Command {

    private Playtime plugin;

    public CommandPlaytime(Playtime plugin){
        this.plugin = plugin;
    }

    @Override
    public String getCommand() {
        return "playtime";
    }

    @Override
    public CommandResult onPlayerExecute(Player player, String[] strings) {

        if (player.hasPermission("playtime.use")) {
            int iTick = 0;
            if(strings.length <= 0){
                iTick = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
            }else{
                Player target = Bukkit.getPlayer(strings[0]);
                iTick = target.getStatistic(Statistic.PLAY_ONE_MINUTE);
            }
            float fSeconds = iTick / 20;
            float fDays = fSeconds / 86400;
            fSeconds = ((int)fDays - fDays) * 86400;
            float fHours = fSeconds / 3600;
            fSeconds = ((int)fHours - fHours) * 3600;
            float fMinutes = fSeconds / 60;
            fSeconds = ((int)fMinutes - fMinutes) * 60;
            float fSeconds2 = fSeconds;

            player.sendMessage(ChatUtils.color(plugin.getConfig().getString("format").replace("{Days}", String.valueOf((int)fDays)).replace("{Hours}", String.valueOf((int)fHours * -1)).replace("{Minutes}", String.valueOf((int)fMinutes)).replace("{Seconds}", String.valueOf((int)fSeconds2 * -1))));
        }else{
            player.sendMessage(ChatUtils.color("&cYou don't have permission to execute this command"));
        }

        return CommandResult.COMPLETED;
    }

    @Override
    public CommandResult onConsoleExecute(boolean b, String[] strings) {
        return CommandResult.COMPLETED;
    }
}
