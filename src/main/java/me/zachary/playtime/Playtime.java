package me.zachary.playtime;

import me.zachary.playtime.commands.CommandPlaytime;
import me.zachary.zachcore.ZachCorePlugin;
import me.zachary.zachcore.utils.Metrics;

public final class Playtime extends ZachCorePlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        new CommandPlaytime(this);

        int pluginId = 9153;
        Metrics metrics = new Metrics(this, pluginId);
        preEnable();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
