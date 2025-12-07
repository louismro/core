package fr.louis.practice.gui;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerState;
import fr.louis.practice.models.PracticePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class QueueSelectorGUI {
    private final PracticeCore plugin;
    
    public QueueSelectorGUI(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    public void openRankedQueues(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8§lQueues Ranked");
        
        int slot = 10;
        for (String kitName : plugin.getKitManager().getKits().keySet()) {
            PracticePlayer practicePlayer = plugin.getPlayerManager().getOrCreate(player);
            int elo = practicePlayer.getElo(kitName);
            
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;
            meta.setDisplayName("§e§l" + kitName);
            
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7Votre ELO: §e" + elo);
            lore.add("§7Rank: " + plugin.getEloManager().getRankColor(elo) + 
                     plugin.getEloManager().getRankName(elo));
            lore.add("");
            lore.add("§aCliquez pour rejoindre la queue!");
            meta.setLore(lore);
            
            item.setItemMeta(meta);
            inv.setItem(slot, item);
            slot++;
            
            if (slot == 17) slot = 19; // Skip to next row
        }
        
        player.openInventory(inv);
    }
    
    public void openUnrankedQueues(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8§lQueues Unranked");
        
        int slot = 10;
        for (String kitName : plugin.getKitManager().getKits().keySet()) {
            ItemStack item = new ItemStack(Material.IRON_SWORD);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;
            meta.setDisplayName("§e§l" + kitName);
            
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7Mode: §fUnranked");
            lore.add("§7Pas d'impact sur l'ELO");
            lore.add("");
            lore.add("§aCliquez pour rejoindre la queue!");
            meta.setLore(lore);
            
            item.setItemMeta(meta);
            inv.setItem(slot, item);
            slot++;
            
            if (slot == 17) slot = 19;
        }
        
        player.openInventory(inv);
    }
    
    public void handleClick(Player player, ItemStack item, boolean ranked) {
        if (item == null) return;
        
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;
        
        String displayName = ChatColor.stripColor(meta.getDisplayName());
        // Le displayName est maintenant sans couleurs ni format (ex: "NoDebuff")
        String kitName = displayName;
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getOrCreate(player);
        if (practicePlayer.getState() != PlayerState.SPAWN) {
            player.sendMessage("§cVous devez être au spawn pour rejoindre une queue!");
            player.closeInventory();
            return;
        }
        
        if (!plugin.getKitManager().getKits().containsKey(kitName)) {
            player.sendMessage("§cKit introuvable: " + kitName);
            player.sendMessage("§7Kits disponibles: " + plugin.getKitManager().getKits().keySet());
            player.closeInventory();
            return;
        }
        
        player.closeInventory();
        plugin.getQueueManager().addToQueue(player, kitName, ranked);
    }
}
