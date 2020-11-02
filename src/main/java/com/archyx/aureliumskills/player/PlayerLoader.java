package com.archyx.aureliumskills.player;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Locale;

public class PlayerLoader {

    private final AureliumSkills plugin;
    private final PlayerManager manager;

    public PlayerLoader(AureliumSkills plugin, PlayerManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    /**
     * Loads player data
     * @param player - The player to load
     */
    public void loadPlayerData(Player player) {
        if (OptionL.getBoolean(Option.MYSQL_ENABLED)) {
            loadFromDatabase(player);
        }
        else {
            File file = new File(plugin.getDataFolder(), player.getUniqueId().toString() + ".yml");
            if (file.exists()) {
                loadFromFile(player, file);
            }
            else {
                loadNewPlayer(player);
            }
        }
    }

    /**
     * Unloads player data
     * @param player - The player to unload
     */
    public void unloadPlayerData(Player player) {
        manager.remove(player);
    }

    private void loadNewPlayer(Player player) {
        PlayerData playerData = new PlayerData(player);
        for (Skill skill : Skill.values()) {
            playerData.setSkillLevel(skill, 1);
            playerData.setSkillXp(skill, 0.0);
        }
        manager.set(playerData);
    }

    private void loadFromFile(Player player, File file) {
        new BukkitRunnable() {
            @Override
            public void run() {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                PlayerData playerData = new PlayerData(player);
                for (Skill skill : Skill.values()) {
                    String skillName = skill.toString().toLowerCase();
                    playerData.setSkillLevel(skill, config.getInt("skills." + skillName + ".lvl", 1));
                    playerData.setSkillXp(skill, config.getDouble("skills." + skillName + ".xp", 0.0));
                }
                playerData.setLanguage(loadLanguage(config.getString("lang")));
                manager.set(playerData);
            }
        }.runTaskAsynchronously(plugin);
    }

    private void loadFromDatabase(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.mySqlSupport.hasData()) {
                    plugin.mySqlSupport.loadPlayerData(player);
                }
                else {
                    loadNewPlayer(player);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private Locale loadLanguage(String input) {
        Locale language;
        if (input != null) {
            language = new Locale(input);
        }
        else {
            language = Lang.defaultLanguage;
        }
        if (Lang.hasLocale(language)) {
            return language;
        }
        else {
            return Lang.defaultLanguage;
        }
    }


}
