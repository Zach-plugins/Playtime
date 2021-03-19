package me.zachary.playtime;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholder extends PlaceholderExpansion {
    private Playtime plugin;

    public Placeholder(Playtime plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "playtime";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Zach_FR";
    }

    @Override
    public String getRequiredPlugin(){
        return null;
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        float time = (plugin.oldTime.get(player.getUniqueId()) + plugin.time.get(player.getUniqueId()));
        float fSeconds;
        float fDays = time / 86400;
        fSeconds = ((int)fDays - fDays) * 86400;
        float fHours = fSeconds / 3600;
        fSeconds = ((int)fHours - fHours) * 3600;
        float fMinutes = fSeconds / 60;
        fSeconds = ((int)fMinutes - fMinutes) * 60;
        float fSeconds2 = fSeconds;

        // %playtime_days%
        if(identifier.equals("days")){
            return String.valueOf((int)fDays);
        }

        // %playtime_hours%
        if(identifier.equals("hours")){
            return String.valueOf((int)fHours*-1);
        }

        // %playtime_minutes%
        if(identifier.equals("minutes")){
            return String.valueOf((int)fMinutes);
        }

        // %playtime_seconds%
        if(identifier.equals("seconds")){
            return String.valueOf((int)fSeconds2*-1);
        }

        // %playtime_days_all%
        if(identifier.equals("days_all")){
            return String.valueOf((int)time / 86400);
        }

        // %playtime_hours_all%
        if(identifier.equals("hours_all")){
            return String.valueOf((int)time / 3600);
        }

        // %playtime_minutes_all%
        if(identifier.equals("minutes_all")){
            return String.valueOf((int)time / 60);
        }

        // %playtime_seconds_all%
        if(identifier.equals("seconds_all")){
            return String.valueOf((int)time);
        }
        return null;
    }
}