package me.zachary.playtime;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FileManager {
    private Playtime plugin;
    // Reward file
    private File rewardFile;
    private YamlConfiguration rewardConfig;
    // Message file
    private File messageFile;
    private YamlConfiguration messageConfig;
    // Reward gui file
    private File rewardGuiFile;
    private YamlConfiguration rewardGuiConfig;
    // Leaderboard gui File
    private File leaderboardGuiFile;
    private YamlConfiguration leaderboardGuiConfig;

    public FileManager(Playtime plugin) {
        this.plugin = plugin;
    }

    public void loadFile(){
        loadRewardFile();
        loadMessageFile();
        loadRewardGuiFile();
        loadLeaderboardGuiFile();
    }

    private void loadMessageFile() {
        messageFile = new File(plugin.getDataFolder() + File.separator + "messages.yml");
        if (!messageFile.exists()) {
            plugin.saveResource("messages.yml", false);
            messageFile = new File(plugin.getDataFolder() + File.separator + "messages.yml");
        }
        messageConfig = YamlConfiguration.loadConfiguration(messageFile);
    }

    private void loadRewardFile() {
        rewardFile = new File(plugin.getDataFolder() + File.separator + "reward.yml");
        if (!rewardFile.exists()) {
            plugin.saveResource("reward.yml", false);
            rewardFile = new File(plugin.getDataFolder() + File.separator + "reward.yml");
        }
        rewardConfig = YamlConfiguration.loadConfiguration(rewardFile);
    }

    private void loadRewardGuiFile(){
        rewardGuiFile = new File(plugin.getDataFolder() + File.separator + "gui" + File.separator + "rewardgui.yml");
        if(!rewardGuiFile.exists()){
            plugin.saveResource("gui/rewardgui.yml", false);
            rewardGuiFile = new File(plugin.getDataFolder() + File.separator + "gui" + File.separator + "rewardgui.yml");
        }
        rewardGuiConfig = YamlConfiguration.loadConfiguration(rewardGuiFile);
    }

    private void loadLeaderboardGuiFile(){
        leaderboardGuiFile = new File(plugin.getDataFolder() + File.separator + "gui" + File.separator + "leaderboardgui.yml");
        if(!leaderboardGuiFile.exists()){
            plugin.saveResource("gui/leaderboardgui.yml", false);
            leaderboardGuiFile = new File(plugin.getDataFolder() + File.separator + "gui" + File.separator + "leaderboardgui.yml");
        }
        leaderboardGuiConfig = YamlConfiguration.loadConfiguration(leaderboardGuiFile);
    }

    public void reloadRewardConfig() {
        rewardFile = new File(plugin.getDataFolder() + File.separator + "reward.yml");
        rewardConfig = YamlConfiguration.loadConfiguration(rewardFile);
    }

    public void reloadMessageConfig() {
        messageFile = new File(plugin.getDataFolder() + File.separator + "messages.yml");
        messageConfig = YamlConfiguration.loadConfiguration(messageFile);
    }

    public void reloadRewardGuiConfig(){
        rewardGuiFile = new File(plugin.getDataFolder() + File.separator + "gui" + File.separator + "rewardgui.yml");
        rewardGuiConfig = YamlConfiguration.loadConfiguration(rewardGuiFile);
    }

    public void reloadLeaderboardGuiConfig(){
        leaderboardGuiFile = new File(plugin.getDataFolder() + File.separator + "gui" + File.separator + "leaderboardgui.yml");
        leaderboardGuiConfig = YamlConfiguration.loadConfiguration(leaderboardGuiFile);
    }

    public YamlConfiguration getRewardConfig(){
        return rewardConfig;
    }

    public YamlConfiguration getMessageConfig(){
        return messageConfig;
    }

    public YamlConfiguration getRewardGuiConfig(){
        return rewardGuiConfig;
    }

    public YamlConfiguration getLeaderboardGuiConfig(){
        return leaderboardGuiConfig;
    }
}
