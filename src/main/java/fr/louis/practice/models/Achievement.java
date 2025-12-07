package fr.louis.practice.models;

import org.bukkit.Material;

public class Achievement {
    private final String id;
    private final String name;
    private final String description;
    private final AchievementCategory category;
    private final Material icon;
    private final int requirement; // Valeur requise pour compléter
    private final int coinsReward;
    private final String cosmeticReward; // ID cosmétique optionnel
    
    public enum AchievementCategory {
        KILLS("Éliminations", Material.DIAMOND_SWORD),
        WINS("Victoires", Material.GOLD_INGOT),
        STREAKS("Séries", Material.BLAZE_ROD),
        ELO("Classement", Material.EMERALD),
        SPECIAL("Spécial", Material.NETHER_STAR),
        SOCIAL("Social", Material.PLAYER_HEAD);
        
        private final String displayName;
        private final Material icon;
        
        AchievementCategory(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }

        public String getDisplayName() { return displayName; }
        public Material getIcon() { return icon; }
    }
    
    public Achievement(String id, String name, String description, AchievementCategory category,
                      Material icon, int requirement, int coinsReward, String cosmeticReward) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.icon = icon;
        this.requirement = requirement;
        this.coinsReward = coinsReward;
        this.cosmeticReward = cosmeticReward;
    }
    
    public Achievement(String id, String name, String description, AchievementCategory category,
                      Material icon, int requirement, int coinsReward) {
        this(id, name, description, category, icon, requirement, coinsReward, null);
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public AchievementCategory getCategory() { return category; }
    public Material getIcon() { return icon; }
    public int getRequirement() { return requirement; }
    public int getCoinsReward() { return coinsReward; }
    public String getCosmeticReward() { return cosmeticReward; }
}
