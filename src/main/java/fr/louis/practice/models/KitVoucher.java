package fr.louis.practice.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KitVoucher {
    private final String id;
    private final String kitName;
    private final String displayName;
    private final Rarity rarity;
    private final long expiresAt; // 0 = permanent
    
    public enum Rarity {
        COMMON("§aCommun", 70),
        UNCOMMON("§2Peu Commun", 20),
        RARE("§9Rare", 7),
        EPIC("§5Épique", 2),
        LEGENDARY("§6Légendaire", 1);
        
        private final String displayName;
        private final int weight;
        
        Rarity(String displayName, int weight) {
            this.displayName = displayName;
            this.weight = weight;
        }
        
        public String getDisplayName() { return displayName; }
        public int getWeight() { return weight; }
    }
    
    public KitVoucher(String id, String kitName, String displayName, Rarity rarity, long durationDays) {
        this.id = id;
        this.kitName = kitName;
        this.displayName = displayName;
        this.rarity = rarity;
        this.expiresAt = durationDays == 0 ? 0 : System.currentTimeMillis() + (durationDays * 24 * 60 * 60 * 1000);
    }
    
    public boolean isExpired() {
        return expiresAt > 0 && System.currentTimeMillis() > expiresAt;
    }
    
    public String getTimeRemaining() {
        if (expiresAt == 0) return "§aPermanent";
        
        long remaining = expiresAt - System.currentTimeMillis();
        if (remaining <= 0) return "§cExpiré";
        
        long days = remaining / (24 * 60 * 60 * 1000);
        long hours = (remaining % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        
        if (days > 0) return "§e" + days + "j " + hours + "h";
        return "§e" + hours + "h";
    }
    
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(Material.PAPER);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(rarity.getDisplayName() + " §l" + displayName);
        List<String> lore = new ArrayList<>();
        lore.add("§7Kit: §f" + kitName);
        lore.add("§7Rareté: " + rarity.getDisplayName());
        lore.add("§7Expire dans: " + getTimeRemaining());
        lore.add("");
        lore.add("§e▸ Clic droit pour utiliser");
        meta.setLore(lore);
        
        item.setItemMeta(meta);
        return item;
    }

    // Getters
    public String getId() { return id; }
    public String getKitName() { return kitName; }
    public String getDisplayName() { return displayName; }
    public Rarity getRarity() { return rarity; }
    public long getExpiresAt() { return expiresAt; }
}

