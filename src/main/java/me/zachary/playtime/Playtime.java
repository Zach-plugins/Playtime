package me.zachary.playtime;

import me.zachary.playtime.commands.CommandPlaytime;
import me.zachary.zachcore.ZachCorePlugin;
import me.zachary.zachcore.utils.Metrics;
import org.bukkit.Bukkit;

public final class Playtime extends ZachCorePlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        new CommandPlaytime(this);
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new Placeholder(this).register();

        int pluginId = 9153;
        Metrics metrics = new Metrics(this, pluginId);
        preEnable();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
