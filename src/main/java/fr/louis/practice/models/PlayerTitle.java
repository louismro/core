package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerTitle {
    private final String id;
    private final String displayName;
    private final String prefix;
    private final TitleRarity rarity;
    private final int coinsPrice;
    private final boolean requiresAchievement;
    private final String achievementId;
    
    public enum TitleRarity {
        COMMON("§fCommun", 1000),
        RARE("§9Rare", 2500),
        EPIC("§5Épique", 5000),
        LEGENDARY("§6Légendaire", 10000),
        MYTHIC("§dMythique", 25000),
        EXCLUSIVE("§c§lExclusif", -1); // Non achetable
        
        private final String displayName;
        private final int basePrice;
        
        TitleRarity(String displayName, int basePrice) {
            this.displayName = displayName;
            this.basePrice = basePrice;
        }
        
        public String getDisplayName() { return displayName; }
        public int getBasePrice() { return basePrice; }
    }
    
    public PlayerTitle(String id, String displayName, String prefix, TitleRarity rarity, 
                      int coinsPrice, boolean requiresAchievement, String achievementId) {
        this.id = id;
        this.displayName = displayName;
        this.prefix = prefix;
        this.rarity = rarity;
        this.coinsPrice = coinsPrice;
        this.requiresAchievement = requiresAchievement;
        this.achievementId = achievementId;
    }
    
    public PlayerTitle(String id, String displayName, String prefix, TitleRarity rarity, int coinsPrice) {
        this(id, displayName, prefix, rarity, coinsPrice, false, null);
    }
}
