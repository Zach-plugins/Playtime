package me.zachary.playtime.leaderboard;

import org.bukkit.OfflinePlayer;

public class LeaderboardPlayer {
    private OfflinePlayer player;
    private int playtime;
    private int topNumber;

    public LeaderboardPlayer(OfflinePlayer player, int playtime, int topNumber){
        this.player = player;
        this.playtime = playtime;
        this.topNumber = topNumber;
    }

    public OfflinePlayer getOfflinePlayer(){
        return player;
    }

    public int getPlaytime(){
        return playtime;
    }

    public int getTopNumber(){
        return topNumber;
    }
}
