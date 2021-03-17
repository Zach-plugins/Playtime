package me.zachary.playtime.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import me.zachary.playtime.Playtime;
import me.zachary.zachcore.commands.Command;
import me.zachary.zachcore.commands.CommandResult;
import me.zachary.zachcore.guis.ZMenu;
import me.zachary.zachcore.guis.buttons.ZButton;
import me.zachary.zachcore.guis.pagination.ZPaginationButtonBuilder;
import me.zachary.zachcore.utils.MessageUtils;
import me.zachary.zachcore.utils.items.ItemBuilder;
import me.zachary.zachcore.utils.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CommandPlaytimeReward extends Command {
    private Playtime plugin;

    public CommandPlaytimeReward(Playtime plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommand() {
        return "playtimereward";
    }

    @Override
    public CommandResult onPlayerExecute(Player player, String[] strings) {
        if(!player.hasPermission("playtime.reward")){
            MessageUtils.sendMessage(player, plugin.getFileManager().getMessageConfig().getStringList("no permission"));
            return CommandResult.COMPLETED;
        }
        Boolean bool = null;
        try {
            if (!plugin.sql.open()) {
                plugin.sql.open();
            }
            ResultSet result = plugin.sql.query("SELECT EXISTS(SELECT * FROM Playtime_Reward WHERE uuid = '"+ player.getUniqueId() +"');");
            result.next();
            bool = result.getBoolean(1);
            if(!bool){
                plugin.sql.query("INSERT INTO Playtime_Reward (uuid,reward) VALUES ('"+ player.getUniqueId() +"',1);");
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

        ZMenu rewardMenu = Playtime.zachGUI.create(plugin.getFileManager().getRewardGuiConfig().getString("Reward.Menu.Name"), 3);

        int[] TILES_TO_UPDATE = {
                0,  1,  2,  3,  4,  5,  6,  7,  8,
                9,                             17,
                18, 19, 20, 21, 22, 23, 24, 25, 26
        };
        IntStream.range(0, TILES_TO_UPDATE.length).map(i -> TILES_TO_UPDATE.length - i + -1).forEach(
                index -> rewardMenu.setButton(0, TILES_TO_UPDATE[index], new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getFileManager().getRewardGuiConfig().getString("Reward.Menu.Glass")).parseItem()).build()))
        );

        int slot = 10;
        int page = 0;
        for (String item : plugin.getFileManager().getRewardConfig().getConfigurationSection("Reward").getKeys(false)) {
            if(slot == 17){
                slot = 10;
                page++;
                int finalPage = page;
                IntStream.range(0, TILES_TO_UPDATE.length).map(i -> TILES_TO_UPDATE.length - i + -1).forEach(
                        index -> rewardMenu.setButton(finalPage, TILES_TO_UPDATE[index], new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getFileManager().getRewardGuiConfig().getString("Reward.Menu.Glass")).parseItem()).build()))
                );
            }
            ConfigurationSection config = plugin.getFileManager().getRewardConfig().getConfigurationSection("Reward." + item);

            Boolean goodReward = false;
            Boolean rewardTime = false;
            Boolean claim = false;
            int rewardNumber = 0;
            long time = 0L;
            long playtime = 0;
            String[] sTime =  config.getString("Time").split(" ");
            time += Long.parseLong(sTime[0]) * 86400;
            time += Long.parseLong(sTime[1]) * 3600;
            time += Long.parseLong(sTime[2]) * 60;
            time += Long.parseLong(sTime[3]);

            if (!plugin.sql.open()) {
                plugin.sql.open();
            }

            try {
                ResultSet resultSet = plugin.sql.query("SELECT reward FROM Playtime_Reward WHERE uuid = '"+ player.getUniqueId() +"';");
                resultSet.next();
                rewardNumber = resultSet.getInt(1);
                resultSet = plugin.sql.query("SELECT time FROM Playtime WHERE uuid = '"+ player.getUniqueId() +"';");
                resultSet.next();
                playtime = (resultSet.getInt(1) + plugin.time.get(player.getUniqueId()));
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
            Integer finalRewardNumber = rewardNumber;
            if(playtime >= time){
                rewardTime = true;
            }
            if(finalRewardNumber.equals(Integer.valueOf(item))){
                goodReward = true;
            }
            if(finalRewardNumber > Integer.parseInt(item) && playtime >= Integer.parseInt(item)){
                claim = true;
            }

            ItemStack itemButton = new ItemBuilder(Material.valueOf(config.getString("Button.Item")))
                    .name(config.getString("Button.Name"))
                    .lore(getLore(config, goodReward, rewardTime, claim))
                    .build();

            Boolean finalGoodReward = goodReward;
            Boolean finalRewardTime = rewardTime;
            Boolean finalClaim = claim;
            ZButton Button = new ZButton(itemButton)
                    .withListener((InventoryClickEvent event) -> {
                        HumanEntity playerEvent = event.getWhoClicked();
                        if(finalGoodReward && finalRewardTime && !finalClaim){
                            try {
                                if (!plugin.sql.open()) {
                                    plugin.sql.open();
                                }
                                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                                for(String command : config.getStringList("Command")){
                                    Bukkit.dispatchCommand(console, command.replace("%player_name%", player.getName()));
                                }
                                plugin.sql.query("UPDATE Playtime_Reward SET uuid = '"+ player.getUniqueId() +"', reward = '"+ (finalRewardNumber + 1) +"' WHERE uuid = '"+ player.getUniqueId() +"';");
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
                            for(String m : plugin.getFileManager().getMessageConfig().getStringList("player claim reward")){
                                MessageUtils.sendMessage(player, m.replace("{reward_name}", config.getString("Button.Name")));
                            }
                            event.getWhoClicked().closeInventory();
                        }
                    });

            rewardMenu.setButton(page, slot, Button);
            slot++;
        }

        ConfigurationSection config = plugin.getFileManager().getRewardGuiConfig().getConfigurationSection("Reward.Menu");
        ZPaginationButtonBuilder defaultPaginationButtonBuilders = (type, inventory) -> {
            switch (type) {
                case CLOSE_BUTTON:
                    return new ZButton(new ItemBuilder(XMaterial.valueOf(config.getString("Close button.Item")).parseItem())
                            .name(config.getString("Close button.Name"))
                            .build()
                    ).withListener(event -> {
                        event.getWhoClicked().closeInventory();
                    });

                case PREV_BUTTON:
                    List<String> lore_prev = new ArrayList<>();
                    for (String l : config.getStringList("Previous button.Lore")) {
                        String o = l.replace("{current_page}", String.valueOf(inventory.getCurrentPage()));
                        lore_prev.add(o);
                    }
                    if (inventory.getCurrentPage() > 0) return new ZButton(new ItemBuilder(XMaterial.valueOf(config.getString("Previous button.Item")).parseItem())
                            .name(config.getString("Previous button.Name"))
                            .lore(lore_prev)
                            .build()
                    ).withListener(event -> {
                        event.setCancelled(true);
                        inventory.previousPage(event.getWhoClicked());
                    });
                    else return null;

                case CURRENT_BUTTON:
                    String name = config.getString("Current button.Name")
                            .replace("{current_page}", String.valueOf(inventory.getCurrentPage() + 1))
                            .replace("{max_page}", String.valueOf(inventory.getMaxPage()));
                    List<String> lore_current = new ArrayList<>();
                    for (String l : config.getStringList("Current button.Lore")) {
                        String o = l.replace("{current_page}", String.valueOf(inventory.getCurrentPage() + 1));
                        lore_current.add(o);
                    }
                    return new ZButton(new ItemBuilder(XMaterial.valueOf(config.getString("Current button.Item")).parseItem())
                            .name(name)
                            .lore(lore_current)
                            .build()
                    ).withListener(event -> event.setCancelled(true));

                case NEXT_BUTTON:
                    List<String> lore_next = new ArrayList<>();
                    for (String l : config.getStringList("Next button.Lore")) {
                        String o = l.replace("{current_page}", String.valueOf(inventory.getCurrentPage() + 2));
                        lore_next.add(o);
                    }
                    if (inventory.getCurrentPage() < inventory.getMaxPage() - 1) return new ZButton(new ItemBuilder(XMaterial.valueOf(config.getString("Next button.Item")).parseItem())
                            .name(config.getString("Next button.Name"))
                            .lore(lore_next)
                            .build()
                    ).withListener(event -> {
                        event.setCancelled(true);
                        inventory.nextPage(event.getWhoClicked());
                    });
                    else return null;

                case CUSTOM_1:
                    return new ZButton(new ItemBuilder(XMaterial.valueOf(config.getString("Playtime button.Item")).parseItem())
                            .name(config.getString("Playtime button.Name"))
                            .lore(getLorePlaytime(player))
                            .build()
                    );
                case CUSTOM_2:
                case CUSTOM_3:
                case CUSTOM_4:
                case UNASSIGNED:
                default:
                    return null;
            }
        };
        rewardMenu.setPaginationButtonBuilder(defaultPaginationButtonBuilders);

        player.openInventory(rewardMenu.getInventory());
        return CommandResult.COMPLETED;
    }

    @Override
    public CommandResult onConsoleExecute(boolean b, String[] strings) {
        return CommandResult.COMPLETED;
    }

    private String getStatus(Boolean gReward, Boolean tReward, Boolean claim){
        String respond = null;
        if(gReward && tReward)
            return plugin.getFileManager().getRewardGuiConfig().getString("Unlockable");
        if(claim)
            return plugin.getFileManager().getRewardGuiConfig().getString("Unlocked");
        return plugin.getFileManager().getRewardGuiConfig().getString("Not unlockable");
    }

    private List<String> getLore(ConfigurationSection cfg, Boolean gReward, Boolean tReward, Boolean claim){
        List<String> lore = new ArrayList<>();
        for (String l : cfg.getStringList("Button.Lore")) {
            lore.add(l.replace("{unlock}", getStatus(gReward, tReward, claim)));
        }
        return lore;
    }

    private List<String> getLorePlaytime(Player player){
        List<String> lore = new ArrayList<>();
        for (String l : plugin.getFileManager().getRewardGuiConfig().getStringList("Reward.Menu.Playtime button.Lore")) {
            lore.add(PlaceholderAPI.setPlaceholders(player, l));
        }
        return lore;
    }
}
