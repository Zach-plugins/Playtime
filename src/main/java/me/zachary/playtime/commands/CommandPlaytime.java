package me.zachary.playtime.commands;

import me.zachary.playtime.Playtime;
import me.zachary.zachcore.commands.Command;
import me.zachary.zachcore.commands.CommandResult;
import me.zachary.zachcore.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
        OfflinePlayer target = null;
        if (player.hasPermission("playtime.use")) {
            long time = 0;
            if(strings.length <= 0){
                time = plugin.getPlaytime(player);
                target = Bukkit.getOfflinePlayer(player.getUniqueId());
            }else{
                target = Bukkit.getOfflinePlayer(strings[0]);
                time = plugin.getPlaytime(target);
                if(time == 0){
                    MessageUtils.sendMessage(player, plugin.getFileManager().getMessageConfig().getStringList("player not found"));
                    return CommandResult.COMPLETED;
                }
            }
            float fSeconds = time;
            float fDays = fSeconds / 86400;
            fSeconds = ((int)fDays - fDays) * 86400;
            float fHours = fSeconds / 3600;
            fSeconds = ((int)fHours - fHours) * 3600;
            float fMinutes = fSeconds / 60;
            fSeconds = ((int)fMinutes - fMinutes) * 60;
            float fSeconds2 = fSeconds;

            if(target.getPlayer() != null && player.getUniqueId() == target.getPlayer().getUniqueId()){
                for(String m : plugin.getFileManager().getMessageConfig().getStringList("format own player")){
                    MessageUtils.sendMessage(player, m.replace("{Days}", String.valueOf((int)fDays))
                            .replace("{Hours}", String.valueOf((int)fHours * -1))
                            .replace("{Minutes}", String.valueOf((int)fMinutes))
                            .replace("{Seconds}", String.valueOf((int)fSeconds2 * -1)));
                }
            }
            else{
                for(String m : plugin.getFileManager().getMessageConfig().getStringList("format other player")){
                    MessageUtils.sendMessage(player, m.replace("{Days}", String.valueOf((int)fDays))
                            .replace("{Hours}", String.valueOf((int)fHours * -1))
                            .replace("{Minutes}", String.valueOf((int)fMinutes))
                            .replace("{Seconds}", String.valueOf((int)fSeconds2 * -1))
                            .replace("{PlayerName}", target.getName()));
                }
            }
        }else{
            MessageUtils.sendMessage(player, plugin.getFileManager().getMessageConfig().getStringList("no permission"));
        }

        return CommandResult.COMPLETED;
    }

    @Override
    public CommandResult onConsoleExecute(boolean b, String[] strings) {
        return CommandResult.COMPLETED;
    }

    @Override
    public List<String> getCommandComplete(Player player, String alias, String[] args) {
        List<String> args1 = new ArrayList<>();
        Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
        Bukkit.getServer().getOnlinePlayers().toArray(players);
        for (Player value : players) {
            args1.add(value.getName());
        }
        return args1;
    }
}