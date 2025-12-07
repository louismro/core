package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@Getter
public class InventoryManager {
    private final PracticeCore plugin;
    
    public InventoryManager(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    public void giveSpawnItems(Player player) {
        player.getInventory().clear();
        
        // Unranked Queues
        player.getInventory().setItem(0, createItem(Material.IRON_SWORD, 
            "§aQueues Non-Classées", 
            Arrays.asList("§7Cliquez pour rejoindre", "§7une queue non-classée")));
        
        // Ranked Queues
        player.getInventory().setItem(1, createItem(Material.DIAMOND_SWORD, 
            "§cQueues Classées", 
            Arrays.asList("§7Cliquez pour rejoindre", "§7une queue classée")));
        
        // Create Party
        player.getInventory().setItem(4, createItem(Material.NAME_TAG, 
            "§dCréer une Partie", 
            Arrays.asList("§7Cliquez pour créer", "§7une partie")));
        
        // Edit Kit
        player.getInventory().setItem(7, createItem(Material.BOOK, 
            "§eÉditer les Kits", 
            Arrays.asList("§7Cliquez pour éditer", "§7vos kits personnalisés")));
        
        // Statistiques
        player.getInventory().setItem(8, createItem(Material.PAPER, 
            "§bStatistiques", 
            Arrays.asList("§7Cliquez pour voir", "§7vos statistiques")));
        
        player.updateInventory();
    }
    
    public void giveQueueItems(Player player) {
        player.getInventory().clear();
        
        player.getInventory().setItem(8, createItem(Material.POPPY, 
            "§cQuitter la Queue", 
            Arrays.asList("§7Cliquez pour quitter", "§7la queue")));
        
        player.updateInventory();
    }
    
    public void giveSpectatorItems(Player player) {
        player.getInventory().clear();
        
        player.getInventory().setItem(0, createItem(Material.COMPASS, 
            "§aTéléportation", 
            Arrays.asList("§7Cliquez pour vous téléporter", "§7à un joueur")));
        
        player.getInventory().setItem(8, createItem(Material.RED_BED, 
            "§cQuitter", 
            Arrays.asList("§7Cliquez pour arrêter", "§7de spectater")));
        
        player.updateInventory();
    }
    
    public void giveEditorItems(Player player) {
        player.getInventory().clear();
        
        player.getInventory().setItem(0, createItem(Material.CHEST, 
            "§aCharger un Kit", 
            Arrays.asList("§7Cliquez pour charger", "§7un kit existant")));
        
        player.getInventory().setItem(4, createItem(Material.WHITE_WOOL, 
            "§eRenommer le Kit", 
            Arrays.asList("§7Cliquez pour renommer", "§7votre kit")));
        
        player.getInventory().setItem(7, createItem(Material.EMERALD, 
            "§aSauvegarder", 
            Arrays.asList("§7Cliquez pour sauvegarder", "§7vos modifications")));
        
        player.getInventory().setItem(8, createItem(Material.REDSTONE, 
            "§cAnnuler", 
            Arrays.asList("§7Cliquez pour annuler", "§7sans sauvegarder")));
        
        player.updateInventory();
    }
    
    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(name);
        meta.setLore(lore);
        
        item.setItemMeta(meta);
        return item;
    }
    
    // M\u00e9thode publique pour cr\u00e9er des items depuis d'autres classes
    public ItemStack createItemPublic(Material material, String name, List<String> lore) {
        return createItem(material, name, lore);
    }
    
    public boolean isSpawnItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        String name = item.getItemMeta().getDisplayName();
        return name.contains("Queues") || 
               name.contains("Partie") || 
               name.contains("Kits") || 
               name.contains("Statistiques");
    }
    
    public boolean isQueueItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        String name = item.getItemMeta().getDisplayName();
        return name.contains("Quitter la Queue");
    }
    
    public boolean isSpectatorItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        String name = item.getItemMeta().getDisplayName();
        return name.contains("Téléportation") || name.contains("Quitter");
    }
}
