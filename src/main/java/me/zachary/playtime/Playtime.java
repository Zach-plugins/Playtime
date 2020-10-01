package me.zachary.playtime;

import me.zachary.playtime.commands.CommandPlaytime;
import org.bukkit.plugin.java.JavaPlugin;

public final class Playtime extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        new CommandPlaytime(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
