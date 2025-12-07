package fr.louis.practice.gui;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerStats;
import fr.louis.practice.models.PracticePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StatsGUI {
    private final PracticeCore plugin;
    
    public StatsGUI(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    public void openStats(Player player) {
        openStats(player, player);
    }
    
    public void openStats(Player viewer, Player target) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getOrCreate(target);
        
        Inventory inv = Bukkit.createInventory(null, 54, "§8§lStats de " + target.getName());
        
        // Global stats (ligne 1)
        ItemStack global = new ItemStack(Material.DIAMOND);
        ItemMeta globalMeta = global.getItemMeta();
        globalMeta.setDisplayName("§6§lSTATISTIQUES GLOBALES");
        
        int totalWins = 0, totalLosses = 0, totalKills = 0, totalDeaths = 0;
        for (PlayerStats stats : practicePlayer.getStatsMap().values()) {
            totalWins += stats.getWins();
            totalLosses += stats.getLosses();
            totalKills += stats.getKills();
            totalDeaths += stats.getDeaths();
        }
        
        double globalKD = totalDeaths > 0 ? (double) totalKills / totalDeaths : totalKills;
        double globalWR = (totalWins + totalLosses) > 0 ? 
            (double) totalWins / (totalWins + totalLosses) * 100 : 0;
        
        List<String> globalLore = new ArrayList<>();
        globalLore.add("");
        globalLore.add("§7Victoires totales: §a" + totalWins);
        globalLore.add("§7Défaites totales: §c" + totalLosses);
        globalLore.add("§7K/D Ratio: §e" + String.format("%.2f", globalKD));
        globalLore.add("§7Winrate: §e" + String.format("%.1f", globalWR) + "%");
        globalLore.add("");
        globalMeta.setLore(globalLore);
        global.setItemMeta(globalMeta);
        inv.setItem(4, global);
        
        // Per-kit stats
        int slot = 19;
        for (String kitName : plugin.getKitManager().getKits().keySet()) {
            int elo = practicePlayer.getElo(kitName);
            PlayerStats stats = practicePlayer.getStats(kitName);
            
            ItemStack kitItem = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta meta = kitItem.getItemMeta();
            meta.setDisplayName("§e§l" + kitName.toUpperCase());
            
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7ELO: §e" + elo);
            lore.add("§7Rank: " + plugin.getEloManager().getRankColor(elo) + 
                     plugin.getEloManager().getRankName(elo));
            lore.add("");
            lore.add("§7Victoires: §a" + stats.getWins());
            lore.add("§7Défaites: §c" + stats.getLosses());
            lore.add("§7Kills: §e" + stats.getKills());
            lore.add("§7Morts: §e" + stats.getDeaths());
            lore.add("");
            lore.add("§7K/D: §e" + String.format("%.2f", stats.getKDRatio()));
            lore.add("§7Winrate: §e" + String.format("%.1f", stats.getWinRate()) + "%");
            lore.add("");
            meta.setLore(lore);
            
            kitItem.setItemMeta(meta);
            inv.setItem(slot, kitItem);
            slot++;
            
            if (slot == 26) slot = 28; // Next row
        }
        
        viewer.openInventory(inv);
    }
}
