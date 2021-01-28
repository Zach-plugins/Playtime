package me.zachary.playtime.commands;

import me.zachary.playtime.Playtime;
import me.zachary.zachcore.commands.Command;
import me.zachary.zachcore.commands.CommandResult;
import me.zachary.zachcore.utils.ChatUtils;
import me.zachary.zachcore.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CommandPlaytimeLeaderboard extends Command {
    private Playtime plugin;

    public CommandPlaytimeLeaderboard(Playtime plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommand() {
        return "playtimeleaderboard";
    }

    @Override
    public CommandResult onPlayerExecute(Player player, String[] strings) {
        if(!player.hasPermission("playtime.leaderboard")){
            player.sendMessage(ChatUtils.color(plugin.getConfig().getString("no permission")));
            return CommandResult.COMPLETED;
        }
        if (!plugin.sql.open()) {
            plugin.sql.open();
        }
        MessageUtils.sendMessage(player, "&7============== &6Playtime leaderboard &7==============");
        ResultSet resultSet;
        try {
            resultSet = plugin.sql.query("SELECT * FROM Playtime ORDER BY time DESC");
            resultSet.next();
            int row = 1;
            do{
                UUID uuid = UUID.fromString(resultSet.getString(1));
                float fSeconds = resultSet.getInt(2);
                float fDays = fSeconds / 86400;
                fSeconds = ((int)fDays - fDays) * 86400;
                float fHours = fSeconds / 3600;
                fSeconds = ((int)fHours - fHours) * 3600;
                float fMinutes = fSeconds / 60;
                fSeconds = ((int)fMinutes - fMinutes) * 60;
                float fSeconds2 = fSeconds;
                String respond = "&6Top &e" + row + "&6, &e" + Bukkit.getOfflinePlayer(uuid).getName() + ". &6Playtime: &e" + (int)fDays + " &6Days, &e" + (int)fHours * -1 + " &6Hours, &e" + (int)fMinutes + " &6minutes, &e" + (int)fSeconds2 * -1 + " &6seconds.";
                MessageUtils.sendMessage(player, respond);
                row++;
                if(!resultSet.next()){
                    row = 11;
                }
            }while(row != 11);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(plugin.sql != null){
                try{
                    plugin.sql.close();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        MessageUtils.sendMessage(player, "&7===============================================");
        return CommandResult.COMPLETED;
    }

    @Override
    public CommandResult onConsoleExecute(boolean b, String[] strings) {
        return CommandResult.COMPLETED;
    }
}
