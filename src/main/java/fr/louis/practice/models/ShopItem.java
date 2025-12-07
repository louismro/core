package fr.louis.practice.models;


public class ShopItem {
    private final String id;
    private final String name;
    private final String description;
    private final int price;
    private final ShopItemType type;
    private final String permission;
    
    public enum ShopItemType {
        TITLE("Titre", "Titres cosmétiques"),
        EFFECT("Effet", "Effets visuels"),
        KIT_SKIN("Skin de Kit", "Apparences de kits"),
        KILLSTREAK_EFFECT("Effet Killstreak", "Effets lors des killstreaks");
        
        private final String displayName;
        private final String description;
        
        ShopItemType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
    }

    public ShopItem(String id, String name, String description, int price, ShopItemType type, String permission) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.permission = permission;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public ShopItemType getType() { return type; }
    public String getPermission() { return permission; }
}

