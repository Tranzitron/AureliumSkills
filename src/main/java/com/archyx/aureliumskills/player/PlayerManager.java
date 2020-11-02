package com.archyx.aureliumskills.player;

import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private ConcurrentHashMap<Player, PlayerData> playerData;

    public void set(PlayerData playerData) {
        this.playerData.put(playerData.getPlayer(), playerData);
    }

    public PlayerData get(Player player) {
        return this.playerData.get(player);
    }

    public void remove(Player player) {
        playerData.remove(player);
    }

}
