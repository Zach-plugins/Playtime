package me.zachary.playtime.commands;

import me.zachary.playtime.Playtime;
import me.zachary.playtime.leaderboard.LeaderboardPlayer;
import me.zachary.zachcore.commands.Command;
import me.zachary.zachcore.commands.CommandResult;
import me.zachary.zachcore.guis.ZMenu;
import me.zachary.zachcore.guis.buttons.ZButton;
import me.zachary.zachcore.utils.MessageUtils;
import me.zachary.zachcore.utils.items.ItemBuilder;
import me.zachary.zachcore.utils.xseries.SkullUtils;
import me.zachary.zachcore.utils.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandPlaytimeLeaderboard extends Command {
    private Playtime plugin;
    private List<LeaderboardPlayer> leaderboardPlayer = new ArrayList<>();

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
            MessageUtils.sendMessage(player, plugin.getFileManager().getMessageConfig().getStringList("no permission"));
            return CommandResult.COMPLETED;
        }
        if (!plugin.sql.open()) {
            plugin.sql.open();
        }

        ResultSet resultSet;
        try {
            resultSet = plugin.sql.query("SELECT * FROM Playtime ORDER BY time DESC");
            resultSet.next();
            int row = 1;
            do{
                UUID uuid = UUID.fromString(resultSet.getString(1));
                LeaderboardPlayer leaderboardPlayer = new LeaderboardPlayer(Bukkit.getOfflinePlayer(uuid), resultSet.getInt(2), row);
                this.leaderboardPlayer.add(leaderboardPlayer);
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

        if(plugin.getConfig().getString("Leaderboard.Format").equals("chat")){
            int index = 0;
            for(String m : plugin.getConfig().getStringList("Leaderboard.Chat")){
                if(m.contains("{") || m.contains("}")){
                    if(index >= this.leaderboardPlayer.size() && plugin.getConfig().getBoolean("Leaderboard.Unclaimed position.Enable")){
                        MessageUtils.sendMessage(player, plugin.getConfig().getString("Leaderboard.Unclaimed position.Message").replace("{topnumber}", String.valueOf(index)));
                    }else if(index < this.leaderboardPlayer.size()){
                        LeaderboardPlayer leaderboardPlayer = this.leaderboardPlayer.get(index);
                        float fSeconds = leaderboardPlayer.getPlaytime();
                        float fDays = fSeconds / 86400;
                        fSeconds = ((int)fDays - fDays) * 86400;
                        float fHours = fSeconds / 3600;
                        fSeconds = ((int)fHours - fHours) * 3600;
                        float fMinutes = fSeconds / 60;
                        fSeconds = ((int)fMinutes - fMinutes) * 60;
                        float fSeconds2 = fSeconds;
                        MessageUtils.sendMessage(player, m.replace("{playerrank}", plugin.getPlayerUtils().getPlayerRankPrefix(leaderboardPlayer.getOfflinePlayer()))
                                .replace("{playernickname}", leaderboardPlayer.getOfflinePlayer().getPlayer().getDisplayName())
                                .replace("{playername}", leaderboardPlayer.getOfflinePlayer().getName())
                                .replace("{topnumber}", String.valueOf(leaderboardPlayer.getTopNumber()))
                                .replace("{days}", String.valueOf((int)fDays))
                                .replace("{hours}", String.valueOf((int)fHours * -1))
                                .replace("{minutes}", String.valueOf((int)fMinutes))
                                .replace("{seconds}", String.valueOf((int)fSeconds2 * -1)));
                    }
                    index++;
                }else{
                    MessageUtils.sendMessage(player, m);
                }
            }
        }else{
            ZMenu leaderboardGui = Playtime.getSpiGUI().create(plugin.getFileManager().getLeaderboardGuiConfig().getString("Leaderboard.Name"), 3);
            leaderboardGui.setAutomaticPaginationEnabled(false);
            List<Integer> slot = fillList();

            for(int i = 0; i < slot.size(); i++){
                ZButton playerButton = null;
                if(i >= this.leaderboardPlayer.size()){
                    playerButton = new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getFileManager().getLeaderboardGuiConfig().getString("Leaderboard.Not Claimed.Item")).parseItem())
                            .name(plugin.getFileManager().getLeaderboardGuiConfig().getString("Leaderboard.Not Claimed.Name"))
                            .lore(plugin.getFileManager().getLeaderboardGuiConfig().getStringList("Leaderboard.Not Claimed.Lore"))
                            .build());
                }else{
                    LeaderboardPlayer leaderboardPlayer = this.leaderboardPlayer.get(i);
                    float fSeconds = leaderboardPlayer.getPlaytime();
                    float fDays = fSeconds / 86400;
                    fSeconds = ((int)fDays - fDays) * 86400;
                    float fHours = fSeconds / 3600;
                    fSeconds = ((int)fHours - fHours) * 3600;
                    float fMinutes = fSeconds / 60;
                    fSeconds = ((int)fMinutes - fMinutes) * 60;
                    float fSeconds2 = fSeconds;
                    List<String> replace = new ArrayList<String>();
                    List<String> replacement = new ArrayList<String>();
                    replace.add("{playerrank}");
                    replacement.add(plugin.getPlayerUtils().getPlayerRankPrefix(leaderboardPlayer.getOfflinePlayer()));
                    replace.add("{playernickname}");
                    replacement.add(leaderboardPlayer.getOfflinePlayer().getPlayer().getDisplayName());
                    replace.add("{playername}");
                    replacement.add(leaderboardPlayer.getOfflinePlayer().getName());
                    replace.add("{topnumber}");
                    replacement.add(String.valueOf(leaderboardPlayer.getTopNumber()));
                    replace.add("{days}");
                    replacement.add(String.valueOf((int)fDays));
                    replace.add("{hours}");
                    replacement.add(String.valueOf((int)fHours * -1));
                    replace.add("{minutes}");
                    replacement.add(String.valueOf((int)fMinutes));
                    replace.add("{seconds}");
                    replacement.add(String.valueOf((int)fSeconds2 * -1));
                    playerButton = new ZButton(new ItemBuilder(SkullUtils.getSkull(leaderboardPlayer.getOfflinePlayer().getUniqueId()))
                            .name(plugin.getFileManager().getLeaderboardGuiConfig().getString("Leaderboard.Player button.Name")
                                    .replace("{playerrank}", plugin.getPlayerUtils().getPlayerRankPrefix(leaderboardPlayer.getOfflinePlayer()))
                                    .replace("{playernickname}", leaderboardPlayer.getOfflinePlayer().getPlayer().getDisplayName())
                                    .replace("{playername}", leaderboardPlayer.getOfflinePlayer().getName())
                                    .replace("{topnumber}", String.valueOf(leaderboardPlayer.getTopNumber()))
                                    .replace("{days}", String.valueOf((int)fDays))
                                    .replace("{hours}", String.valueOf((int)fHours * -1))
                                    .replace("{minutes}", String.valueOf((int)fMinutes))
                                    .replace("{seconds}", String.valueOf((int)fSeconds2 * -1)))
                            .lore(getLore("Leaderboard.Player button.Lore", replace, replacement))
                            .build());
                }
                leaderboardGui.setButton(slot.get(i), playerButton);
            }

            player.openInventory(leaderboardGui.getInventory());
        }

        this.leaderboardPlayer.clear();
        return CommandResult.COMPLETED;
    }

    @Override
    public CommandResult onConsoleExecute(boolean b, String[] strings) {
        return CommandResult.COMPLETED;
    }

    public List<Integer> fillList(){
        List<Integer> slot = new ArrayList<Integer>();
        slot.add(4);
        slot.add(12);
        slot.add(14);
        slot.add(19);
        slot.add(20);
        slot.add(21);
        slot.add(22);
        slot.add(23);
        slot.add(24);
        slot.add(25);
        return slot;
    }

    private List<String> getLore(String path, List<String> replace, List<String> replacement) {
        List<String> lore = new ArrayList<>();
        for (String l : plugin.getFileManager().getLeaderboardGuiConfig().getStringList(path)) {
            for (int i = 0; i < replace.size(); i++){
                l = l.replace(replace.get(i), replacement.get(i));
            }
            lore.add(l);
        }
        return lore;
    }
}
