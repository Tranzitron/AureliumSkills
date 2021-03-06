package com.archyx.aureliumskills.requirement;

import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.util.LoreUtil;
import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

public class ItemRequirement {

    private final RequirementManager manager;

    public ItemRequirement(RequirementManager manager) {
        this.manager = manager;
    }

    public ItemStack addItemRequirement(ItemStack item, Skill skill, int level) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.getCompound("skillRequirements");
        // If compound does not exist
        if (compound == null) {
            compound = nbtItem.addCompound("skillRequirements");
        }
        NBTCompound itemCompound = compound.getCompound("item");
        if (itemCompound == null) {
            itemCompound = compound.addCompound("item");
        }
        itemCompound.setInteger(skill.name().toLowerCase(), level);
        return nbtItem.getItem();
    }

    public Map<Skill, Integer> getItemRequirements(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        Map<Skill, Integer> requirements = new HashMap<>();
        NBTCompound requirementCompound = nbtItem.getCompound("skillRequirements");
        if (requirementCompound != null) {
            NBTCompound itemCompound = requirementCompound.getCompound("item");
            if (itemCompound != null) {
                for (String key : itemCompound.getKeys()) {
                    try {
                        Skill skill = Skill.valueOf(key.toUpperCase());
                        Integer value = itemCompound.getInteger(key);
                        requirements.put(skill, value);
                    }
                    catch (Exception ignored) { }
                }
            }
        }
        return requirements;
    }

    public ItemStack removeItemRequirement(ItemStack item, Skill skill) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound requirementCompound = nbtItem.getCompound("skillRequirements");
        if (requirementCompound != null) {
            NBTCompound itemCompound = requirementCompound.getCompound("item");
            if (itemCompound != null) {
                for (String key : itemCompound.getKeys()) {
                    if (key.equals(skill.name().toLowerCase())) {
                        itemCompound.removeKey(key);
                    }
                }
                if (itemCompound.getKeys().size() == 0) {
                    requirementCompound.removeKey("item");
                }
            }
            if (requirementCompound.getKeys().size() == 0) {
                nbtItem.removeKey("skillRequirements");
            }
        }
        return nbtItem.getItem();
    }

    public ItemStack removeAllItemRequirements(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound requirementCompound = nbtItem.getCompound("skillRequirements");
        if (requirementCompound != null) {
            NBTCompound itemCompound = requirementCompound.getCompound("item");
            if (itemCompound != null) {
                for (String key : itemCompound.getKeys()) {
                    itemCompound.removeKey(key);
                }
                if (itemCompound.getKeys().size() == 0) {
                    requirementCompound.removeKey("item");
                }
            }
            if (requirementCompound.getKeys().size() == 0) {
                nbtItem.removeKey("skillRequirements");
            }
        }
        return nbtItem.getItem();
    }

    public boolean hasItemRequirement(ItemStack item, Skill skill) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound requirementCompound = nbtItem.getCompound("skillRequirements");
        if (requirementCompound != null) {
            NBTCompound itemCompound = requirementCompound.getCompound("item");
            if (itemCompound != null) {
                for (String key : itemCompound.getKeys()) {
                    if (key.equals(skill.name().toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void addLore(ItemStack item, Skill skill, int level, Locale locale) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String text = LoreUtil.replace(Lang.getMessage(CommandMessage.ITEM_REQUIREMENT_ADD_LORE, locale), "{skill}", skill.getDisplayName(locale), "{level}", String.valueOf(level));
            List<String> lore;
            if (meta.hasLore()) {
                lore = meta.getLore();
            }
            else {
                lore = new ArrayList<>();
            }
            if (lore != null) {
                lore.add(text);
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
    }

    public void removeLore(ItemStack item, Skill skill) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore != null) {
                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i);
                    if (line.contains("Requires") && line.contains(StringUtils.capitalize(skill.name().toLowerCase()))) {
                        lore.remove(line);
                    }
                }
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
    }

    public boolean meetsRequirements(Player player, ItemStack item) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            // Check global requirements
            for (GlobalRequirement global : manager.getGlobalItemRequirements()) {
                if (XMaterial.isNewVersion()) {
                    if (global.getMaterial().parseMaterial() == item.getType()) {
                        for (Map.Entry<Skill, Integer> entry : global.getRequirements().entrySet()) {
                            if (playerSkill.getSkillLevel(entry.getKey()) < entry.getValue()) {
                                return false;
                            }
                        }
                    }
                }
                else {
                    if (global.getMaterial().parseMaterial() == item.getType()) {
                        MaterialData materialData = item.getData();
                        if (materialData != null) {
                            if (materialData.getData() == global.getMaterial().getData()) {
                                for (Map.Entry<Skill, Integer> entry : global.getRequirements().entrySet()) {
                                    if (playerSkill.getSkillLevel(entry.getKey()) < entry.getValue()) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Check item requirements
            for (Map.Entry<Skill, Integer> entry : getItemRequirements(item).entrySet()) {
                if (playerSkill.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
