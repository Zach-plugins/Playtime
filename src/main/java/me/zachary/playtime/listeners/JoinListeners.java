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
import java.util.HashMap;
import java.util.Map;

public class JoinListeners implements Listener {
    private Playtime plugin;
    private Map<Player, Integer> taskId = new HashMap<Player, Integer>();


    public JoinListeners(Playtime plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        plugin.time.put(player.getUniqueId(), 0);
        int time = 0;
        try {
            if (!plugin.sql.open()) {
                plugin.sql.open();
            }
            ResultSet result = plugin.sql.query("SELECT EXISTS(SELECT * FROM Playtime WHERE uuid = '"+ player.getUniqueId() +"');");
            result.next();
            if(result.getBoolean(1)){
                ResultSet resultTime =  plugin.sql.query("SELECT time FROM Playtime WHERE uuid = '"+ player.getUniqueId() +"';");
                resultTime.next();
                time = (resultTime.getInt(1) + plugin.time.get(player.getUniqueId()));
            }
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
        plugin.oldTime.put(player.getUniqueId(), time);
        int playerTaskId;
        playerTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () ->  {
            plugin.time.put(player.getUniqueId(), (plugin.time.get(player.getUniqueId()) + 1));
        },0, 20);
        taskId.put(player, playerTaskId);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        plugin.setPlayTime(player);
        Bukkit.getScheduler().cancelTask(taskId.get(player));
    }
}
