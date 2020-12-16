package me.zachary.playtime.commands;

import me.zachary.playtime.Playtime;
import me.zachary.zachcore.commands.Command;
import me.zachary.zachcore.commands.CommandResult;
import me.zachary.zachcore.utils.ChatUtils;
import me.zachary.zachcore.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        Player target = null;
        Boolean bool = null;
        if (player.hasPermission("playtime.use")) {
            int iTick = 0;
            if(strings.length <= 0){
                try {
                    ResultSet result = plugin.sql.query("SELECT EXISTS(SELECT * FROM Playtime WHERE uuid = '"+ player.getUniqueId() +"');");
                    result.next();
                    bool = result.getBoolean(1);
                    if(bool){
                        ResultSet resultTime =  plugin.sql.query("SELECT time FROM Playtime WHERE uuid = '"+ player.getUniqueId() +"';");
                        resultTime.next();
                        iTick = (resultTime.getInt(1) + plugin.time.get(player.getUniqueId()));
                    }
                    else
                        iTick = plugin.time.get(player.getUniqueId());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }else{
                target = Bukkit.getPlayer(strings[0]);
                if(target != null){
                    try {
                        ResultSet result = plugin.sql.query("SELECT EXISTS(SELECT * FROM Playtime WHERE uuid = '"+ target.getUniqueId() +"');");
                        result.next();
                        bool = result.getBoolean(1);
                        if(bool){
                            ResultSet resultTime =  plugin.sql.query("SELECT time FROM Playtime WHERE uuid = '"+ target.getUniqueId() +"';");
                            resultTime.next();
                            iTick = (resultTime.getInt(1) + plugin.time.get(target.getUniqueId()));
                        }
                        else
                            iTick = plugin.time.get(target.getUniqueId());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }else{
                    MessageUtils.sendMessage(player, plugin.getConfig().getString("player not found"));
                    return CommandResult.COMPLETED;
                }
            }
            float fSeconds = iTick;
            float fDays = fSeconds / 86400;
            fSeconds = ((int)fDays - fDays) * 86400;
            float fHours = fSeconds / 3600;
            fSeconds = ((int)fHours - fHours) * 3600;
            float fMinutes = fSeconds / 60;
            fSeconds = ((int)fMinutes - fMinutes) * 60;
            float fSeconds2 = fSeconds;

            if(target == null)
                player.sendMessage(ChatUtils.color(plugin.getConfig().getString("format own player").replace("{Days}", String.valueOf((int)fDays)).replace("{Hours}", String.valueOf((int)fHours * -1)).replace("{Minutes}", String.valueOf((int)fMinutes)).replace("{Seconds}", String.valueOf((int)fSeconds2 * -1))));
            else
                player.sendMessage(ChatUtils.color(plugin.getConfig().getString("format other player").replace("{Days}", String.valueOf((int)fDays)).replace("{Hours}", String.valueOf((int)fHours * -1)).replace("{Minutes}", String.valueOf((int)fMinutes)).replace("{Seconds}", String.valueOf((int)fSeconds2 * -1)).replace("{PlayerName}", target.getName())));
        }else{
            player.sendMessage(ChatUtils.color("&cYou don't have permission to execute this command"));
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