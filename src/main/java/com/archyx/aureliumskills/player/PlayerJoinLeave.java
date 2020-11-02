package com.archyx.aureliumskills.player;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.util.UpdateChecker;
import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Locale;

public class PlayerJoinLeave implements Listener {

    private final AureliumSkills plugin;
    private final PlayerLoader playerLoader;

    public PlayerJoinLeave(AureliumSkills plugin, PlayerLoader playerLoader) {
        this.plugin = plugin;
        this.playerLoader = playerLoader;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerLoader.loadPlayerData(player); // Load player data
        loadSkull(player); // Load skull into client cache
        // Check for updates if op
        if (player.isOp()) {
            checkForUpdates(player);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerLoader.unloadPlayerData(player);
    }

    private void loadSkull(Player player) {
        Location playerLocation = player.getLocation();
        Location skullLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), 0, playerLocation.getZ());
        Block block = skullLocation.getBlock();
        BlockState blockState = block.getState();
        SkullCreator.blockWithUuid(block, player.getUniqueId());
        blockState.update(true);
    }

    private void checkForUpdates(Player player) {
        new UpdateChecker(plugin, 81069).getVersion(version -> {
            if (!plugin.getDescription().getVersion().contains("Pre-Release")) {
                if (!plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
                    Locale locale = Lang.getLanguage(player);
                    player.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.WHITE + "New update available! You are on version " + ChatColor.AQUA + plugin.getDescription().getVersion() + ChatColor.WHITE + ", latest version is " + ChatColor.AQUA + version);
                    player.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.WHITE + "Download it on Spigot: " + ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "http://spigotmc.org/resources/81069");
                }
            }
        });
    }


}
