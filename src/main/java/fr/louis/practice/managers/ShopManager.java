package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.ShopItem;
import lombok.Getter;

import java.util.*;

@Getter
public class ShopManager {
    private final PracticeCore plugin;
    private final Map<String, ShopItem> items;
    private final Map<UUID, Set<String>> playerPurchases; // Player -> Owned Items
    
    public ShopManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.items = new HashMap<>();
        this.playerPurchases = new HashMap<>();
        
        loadShopItems();
    }
    
    private void loadShopItems() {
        // Titres
        addItem(new ShopItem("title_legend", "§6§lLégende", "Titre légendaire pour les champions", 
            5000, ShopItem.ShopItemType.TITLE, "practice.title.legend"));
        addItem(new ShopItem("title_warrior", "§c§lGuerrier", "Titre pour les combattants aguerris", 
            2500, ShopItem.ShopItemType.TITLE, "practice.title.warrior"));
        addItem(new ShopItem("title_master", "§5§lMaître", "Titre de maîtrise", 
            3500, ShopItem.ShopItemType.TITLE, "practice.title.master"));
        
        // Effets
        addItem(new ShopItem("effect_lightning", "§e⚡ Éclair de Victoire", "Effet d'éclair lors d'une victoire", 
            1500, ShopItem.ShopItemType.EFFECT, "practice.effect.lightning"));
        addItem(new ShopItem("effect_firework", "§c🎆 Feu d'Artifice", "Feu d'artifice lors d'une victoire", 
            1000, ShopItem.ShopItemType.EFFECT, "practice.effect.firework"));
        addItem(new ShopItem("effect_hearts", "§c❤ Coeurs", "Coeurs de victoire", 
            800, ShopItem.ShopItemType.EFFECT, "practice.effect.hearts"));
        
        // Killstreak Effects
        addItem(new ShopItem("killstreak_flame", "§6🔥 Flammes", "Particules de feu lors des killstreaks", 
            2000, ShopItem.ShopItemType.KILLSTREAK_EFFECT, "practice.killstreak.flame"));
        addItem(new ShopItem("killstreak_lightning", "§e⚡ Éclairs", "Éclairs lors des killstreaks", 
            2500, ShopItem.ShopItemType.KILLSTREAK_EFFECT, "practice.killstreak.lightning"));
        addItem(new ShopItem("killstreak_dragon", "§5🐉 Souffle de Dragon", "Effet de dragon breath", 
            3000, ShopItem.ShopItemType.KILLSTREAK_EFFECT, "practice.killstreak.dragon"));
    }
    
    private void addItem(ShopItem item) {
        items.put(item.getId(), item);
    }
    
    public Optional<ShopItem> getItem(String id) {
        return Optional.ofNullable(items.get(id));
    }
    
    public List<ShopItem> getItemsByType(ShopItem.ShopItemType type) {
        List<ShopItem> result = new ArrayList<>();
        for (ShopItem item : items.values()) {
            if (item.getType() == type) {
                result.add(item);
            }
        }
        return result;
    }
    
    public boolean purchaseItem(UUID player, String itemId) {
        ShopItem item = items.get(itemId);
        if (item == null) return false;
        
        // Check if already owned
        if (hasItem(player, itemId)) return false;
        
        // Check coins
        if (!plugin.getPlayerManager().getPlayer(player).removeCoins(item.getPrice())) {
            return false;
        }
        
        // Add to purchases
        playerPurchases.computeIfAbsent(player, k -> new HashSet<>()).add(itemId);
        
        return true;
    }
    
    public boolean hasItem(UUID player, String itemId) {
        return playerPurchases.getOrDefault(player, Collections.emptySet()).contains(itemId);
    }
    
    public void unlockItem(UUID player, String itemId) {
        playerPurchases.computeIfAbsent(player, k -> new HashSet<>()).add(itemId);
    }
    
    public Set<String> getPlayerItems(UUID player) {
        return new HashSet<>(playerPurchases.getOrDefault(player, Collections.emptySet()));
    }
    
    public List<ShopItem> getAllItems() {
        return new ArrayList<>(items.values());
    }
}
