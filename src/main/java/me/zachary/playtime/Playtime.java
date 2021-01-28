package me.zachary.playtime;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;
import me.zachary.playtime.commands.CommandPlaytime;
import me.zachary.playtime.commands.CommandPlaytimeLeaderboard;
import me.zachary.playtime.commands.CommandPlaytimeReward;
import me.zachary.playtime.listeners.JoinListeners;
import me.zachary.updatechecker.Updatechecker;
import me.zachary.zachcore.ZachCorePlugin;
import me.zachary.zachcore.guis.ZachGUI;
import me.zachary.zachcore.utils.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public final class Playtime extends ZachCorePlugin {
    public Database sql;
    public Map<UUID, Integer> time = new HashMap<UUID, Integer>();
    public Map<UUID, Integer> oldTime = new HashMap<UUID, Integer>();
    public static ZachGUI zachGUI;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Updatechecker.updateSongoda(this, 486);

        if(getConfig().getBoolean("MySQL.Enabled")){
            sql = new MySQL(Logger.getLogger("Minecraft"),
                    "[Playtime] ",
                    getConfig().getString("MySQL.Hostname"),
                    getConfig().getInt("MySQL.Port"),
                    getConfig().getString("MySQL.Database"),
                    getConfig().getString("MySQL.Username"),
                    getConfig().getString("MySQL.Password"));
        }else{
            sql = new SQLite(Logger.getLogger("Minecraft"),
                    "[Playtime] ",
                    this.getDataFolder().getAbsolutePath(),
                    "Playtime",
                    ".sqlite");
        }

        if (!sql.open()) {
            sql.open();
        }
        if(sql.open()){
            try {
                if(!sql.isTable("Playtime"))
                    sql.query("CREATE TABLE `Playtime` (`uuid` TEXT, `time` BIGINT DEFAULT '0');");
                if(!sql.isTable("Playtime_Reward"))
                    sql.query("CREATE TABLE `Playtime_Reward` (`uuid` TEXT, `reward` INT DEFAULT '0');");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                if(sql != null){
                    try{
                        sql.close();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        new JoinListeners(this);
        new CommandPlaytime(this);
        new CommandPlaytimeLeaderboard(this);
        if(getConfig().getBoolean("Reward.Enable"))
            new CommandPlaytimeReward(this);
        zachGUI = new ZachGUI(this);
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new Placeholder(this).register();

        int pluginId = 9153;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("reward", new Callable<String>() {
            @Override
            public String call() throws Exception {
                if(getConfig().getBoolean("Reward.Enable"))
                    return "true";
                else
                    return "false";
            }
        }));
        metrics.addCustomChart(new Metrics.SimplePie("sql", new Callable<String>() {
            @Override
            public String call() throws Exception {
                if(getConfig().getBoolean("MySQL.Enabled"))
                    return "MySQL";
                else
                    return "SQLite";
            }
        }));

        preEnable(this);
    }

    @Override
    public void onDisable() {
        Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
        Bukkit.getServer().getOnlinePlayers().toArray(players);
        for (Player value : players) {
            setPlayTime(value);
        }
        sql.close();
    }

    public static ZachGUI getSpiGUI() {
        return zachGUI;
    }
    
    public long getPlaytime(Player player) {
        if (!sql.open()) {
            sql.open();
        }
        Boolean bool = null;
        long time = 0;
        long timemap = 0;
        if(this.time.get(player.getUniqueId()) != null)
            timemap = this.time.get(player.getUniqueId());
        try {
            ResultSet result = sql.query("SELECT EXISTS(SELECT * FROM Playtime WHERE uuid = '"+ player.getUniqueId() +"');");
            result.next();
            bool = result.getBoolean(1);
            if(bool){
                ResultSet resultTime =  sql.query("SELECT time FROM Playtime WHERE uuid = '"+ player.getUniqueId() +"';");
                resultTime.next();
                time = (resultTime.getInt(1) + timemap);
            }
            else
                time = timemap;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(sql != null){
                try{
                    sql.close();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return time;
    }

    public void setPlayTime(Player player){
        if (!sql.open()) {
            sql.open();
        }
        Boolean bool = null;
        long time;
        long timemap = 0;
        if(this.time.get(player.getUniqueId()) != null)
            timemap = this.time.get(player.getUniqueId());
        try {
            ResultSet result = sql.query("SELECT EXISTS(SELECT * FROM Playtime WHERE uuid = '"+ player.getUniqueId() +"');");
            result.next();
            bool = result.getBoolean(1);
            if(bool){
                ResultSet resultSet = sql.query("SELECT time FROM Playtime WHERE uuid = '"+ player.getUniqueId() +"';");
                resultSet.next();
                time = resultSet.getInt(1);
                sql.query("UPDATE Playtime SET uuid = '"+ player.getUniqueId() +"', time = '"+ (time + timemap) +"' WHERE uuid = '"+ player.getUniqueId() +"';");
            }else{
                sql.query("INSERT INTO Playtime (uuid,time) VALUES ('"+ player.getUniqueId() +"',"+ timemap+");");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(sql != null){
                try{
                    sql.close();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
