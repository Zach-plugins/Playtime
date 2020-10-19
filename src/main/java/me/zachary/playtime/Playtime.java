package me.zachary.playtime;

import me.zachary.playtime.commands.CommandPlaytime;
import me.zachary.playtime.utils.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class Playtime extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        new CommandPlaytime(this);

        int pluginId = 9153;
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
