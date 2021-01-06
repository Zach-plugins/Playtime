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
        /*long Tick = 0;
        Boolean bool;
        try {
            ResultSet result = plugin.sql.query("SELECT EXISTS(SELECT * FROM Playtime WHERE uuid = '"+ player.getUniqueId() +"');");
            result.next();
            bool = result.getBoolean(1);
            if(bool){
                ResultSet resultSet = plugin.sql.query("SELECT time FROM Playtime WHERE uuid = '"+ player.getUniqueId() +"';");
                resultSet.next();
                Tick = (resultSet.getInt(1) + plugin.time.get(player.getUniqueId()));
            }
            else
                Tick = plugin.time.get(player.getUniqueId());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }*/
        float fSeconds = (plugin.oldTime.get(player.getUniqueId()) + plugin.time.get(player.getUniqueId()));
        float fDays = fSeconds / 86400;
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
        return null;
    }
}