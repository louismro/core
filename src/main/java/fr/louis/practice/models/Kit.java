package fr.louis.practice.models;

import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Kit {
    private final String name;
    private final ItemStack[] contents;
    private final ItemStack[] armor;
    private final List<String> effects;
    
    private String displayName;
    private String icon;
    private boolean editable;
    private boolean build;
    private boolean hunger;
    private boolean fall;
    private boolean regeneration;
    
    public Kit(String name) {
        this.name = name;
        this.contents = new ItemStack[36];
        this.armor = new ItemStack[4];
        this.effects = new ArrayList<>();
        this.displayName = name;
        this.icon = "DIAMOND_SWORD";
        this.editable = false;
        this.build = false;
        this.hunger = false;
        this.fall = true;
        this.regeneration = false;
    }
    
    public void setContents(ItemStack[] items) {
        System.arraycopy(items, 0, this.contents, 0, Math.min(items.length, 36));
    }
    
    public void setArmor(ItemStack[] items) {
        System.arraycopy(items, 0, this.armor, 0, Math.min(items.length, 4));
    }
    
    public void addEffect(String effect) {
        effects.add(effect);
    }
    
    public ItemStack[] getContents() {
        return contents.clone();
    }
    
    public ItemStack[] getArmor() {
        return armor.clone();
    }
    
    // Getters supplémentaires
    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public String getIcon() { return icon; }
    public boolean isEditable() { return editable; }
    public boolean isBuild() { return build; }
    public boolean isHunger() { return hunger; }
    public boolean isFall() { return fall; }
    public boolean isRegeneration() { return regeneration; }
    public List<String> getEffects() { return effects; }
    
    // Setters
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setEditable(boolean editable) { this.editable = editable; }
    public void setBuild(boolean build) { this.build = build; }
    public void setHunger(boolean hunger) { this.hunger = hunger; }
    public void setFall(boolean fall) { this.fall = fall; }
    public void setRegeneration(boolean regeneration) { this.regeneration = regeneration; }
}
