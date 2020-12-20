package me.zachary.playtime.listeners;

import me.zachary.playtime.Playtime;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JoinListeners implements Listener {
    private Playtime plugin;
    private int task;


    public JoinListeners(Playtime plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        plugin.time.put(player.getUniqueId(), 0);
        task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                plugin.time.put(player.getUniqueId(), (plugin.time.get(player.getUniqueId()) + 1));
            }
        },0, 20);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Boolean bool = null;
        long time;
        try {
            ResultSet result = plugin.sql.query("SELECT EXISTS(SELECT * FROM Playtime WHERE uuid = '"+ player.getUniqueId() +"');");
            result.next();
            bool = result.getBoolean(1);
            if(bool){
                ResultSet resultSet = plugin.sql.query("SELECT time FROM Playtime WHERE uuid = '"+ player.getUniqueId() +"';");
                resultSet.next();
                time = resultSet.getInt(1);
                plugin.sql.query("UPDATE Playtime SET uuid = '"+ player.getUniqueId() +"', time = '"+ (time + plugin.time.get(player.getUniqueId())) +"' WHERE uuid = '"+ player.getUniqueId() +"';");
            }else{
                plugin.sql.query("INSERT INTO Playtime (uuid,time) VALUES ('"+ player.getUniqueId() +"',"+ plugin.time.get(player.getUniqueId())+");");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
