package com.archyx.aureliumskills.player;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.stats.StatLeveler;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;

public class PlayerData {

    private final Player player;

    // Skill data
    private Map<Skill, Integer> skillLevels;
    private Map<Skill, Double> skillXp;
    // Stat data
    private Map<Stat, Integer> statLevels;
    private Map<Stat, Integer> baseStats;
    private Map<String, StatModifier> statModifiers;
    // Misc data
    private Locale language;

    public PlayerData(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public int getSkillLevel(Skill skill) {
        return skillLevels.get(skill);
    }

    public void setSkillLevel(Skill skill, int level) {
        this.skillLevels.put(skill, level);
    }

    public double getSkillXp(Skill skill) {
        return skillXp.get(skill);
    }

    public void setSkillXp(Skill skill, double xp) {
        this.skillXp.put(skill, xp);
    }

    public int getStatLevel(Stat stat) {
        return statLevels.get(stat);
    }

    public int getBaseStat(Stat stat) {
        return baseStats.get(stat);
    }

    public Map<String, StatModifier> getStatModifiers() {
        return statModifiers;
    }

    public void setStatLevel(Stat stat, int level) {
        this.statLevels.put(stat, level);
    }

    public void setBaseStat(Stat stat, int level) {
        this.baseStats.put(stat, level);
    }

    public void addStatModifier(StatModifier modifier) {
        StatModifier oldModifier = statModifiers.get(modifier.getName());
        // Remove old modifier
        if (oldModifier != null) {
            if (oldModifier.equals(modifier)) return; // Return if modifier values are the same
            removeStatModifier(modifier);
        }
        // Adds the modifier
        this.statModifiers.put(modifier.getName(), modifier);
        // Changes the stat level and reloads
        Stat stat = modifier.getStat();
        setStatLevel(stat, getStatLevel(stat) + modifier.getValue());
        StatLeveler.reloadStat(player, stat);
    }

    public void removeStatModifier(StatModifier modifier) {
        Stat stat = modifier.getStat();
        // Changes the stat level
        setStatLevel(stat, getStatLevel(stat) - modifier.getValue());
        // Removes the modifier and reloads
        this.statModifiers.remove(modifier.getName());
        StatLeveler.reloadStat(player, stat);
    }

    public Locale getLanguage() {
        if (language != null) {
            return language;
        }
        else {
            return Lang.defaultLanguage;
        }
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

}
