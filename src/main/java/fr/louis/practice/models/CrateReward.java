package fr.louis.practice.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CrateReward {
    private final String id;
    private final String name;
    private final RewardType type;
    private final double weight; // Rareté (plus haut = plus rare)
    private final int value; // Coins, XP, etc.
    private ItemStack item; // Pour items physiques
    private String cosmeticId; // Pour cosmétiques
    
    public enum RewardType {
        COINS("§6Coins", Material.GOLD_INGOT),
        COSMETIC("§dCosmétique", Material.FIREWORK_ROCKET),
        TITLE("§eTitre", Material.NAME_TAG),
        KIT_VOUCHER("§aKit Voucher", Material.CHEST),
        BOOST("§bBoost", Material.POTION),
        SPECIAL("§5Spécial", Material.NETHER_STAR);
        
        private final String displayName;
        private final Material icon;
        
        RewardType(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
        
        public String getDisplayName() { return displayName; }
        public Material getIcon() { return icon; }
    }
    
    public enum Rarity {
        COMMON("§fCommun", 70.0),
        UNCOMMON("§aInhabituel", 20.0),
        RARE("§9Rare", 7.0),
        EPIC("§5Épique", 2.0),
        LEGENDARY("§6Légendaire", 1.0);
        
        private final String displayName;
        private final double baseWeight;
        
        Rarity(String displayName, double baseWeight) {
            this.displayName = displayName;
            this.baseWeight = baseWeight;
        }
        
        public String getDisplayName() { return displayName; }
        public double getBaseWeight() { return baseWeight; }
    }
    
    public CrateReward(String id, String name, RewardType type, double weight, int value) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.weight = weight;
        this.value = value;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public RewardType getType() { return type; }
    public double getWeight() { return weight; }
    public int getValue() { return value; }
    public ItemStack getItem() { return item; }
    public String getCosmeticId() { return cosmeticId; }

    // Setters
    public void setItem(ItemStack item) { this.item = item; }
    public void setCosmeticId(String cosmeticId) { this.cosmeticId = cosmeticId; }
}
