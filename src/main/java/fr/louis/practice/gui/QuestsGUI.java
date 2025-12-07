package fr.louis.practice.gui;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.DailyQuest;
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
import java.util.concurrent.TimeUnit;

public class QuestsGUI {
    private final PracticeCore plugin;
    
    public QuestsGUI(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    public void openQuests(Player player) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        List<DailyQuest> quests = plugin.getQuestManager().getPlayerQuests(player.getUniqueId());
        
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "╔═ QUÊTES JOURNALIÈRES ═╗");
        
        // Player stats
        ItemStack stats = new ItemStack(Material.PAPER);
        ItemMeta statsMeta = stats.getItemMeta();
        statsMeta.setDisplayName(ChatColor.YELLOW + "Vos Statistiques");
        List<String> statsLore = new ArrayList<>();
        statsLore.add(ChatColor.GRAY + "Coins: " + ChatColor.GREEN + practicePlayer.getCoins());
        statsLore.add(ChatColor.GRAY + "Quêtes complétées: " + ChatColor.GREEN + 
            plugin.getQuestManager().getTotalCompletedQuests(player.getUniqueId()));
        statsMeta.setLore(statsLore);
        stats.setItemMeta(statsMeta);
        inv.setItem(4, stats);
        
        // Quests
        int[] questSlots = {10, 13, 16};
        for (int i = 0; i < quests.size() && i < 3; i++) {
            DailyQuest quest = quests.get(i);
            ItemStack questItem = createQuestItem(quest);
            inv.setItem(questSlots[i], questItem);
        }
        
        // Close button
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Fermer");
        close.setItemMeta(closeMeta);
        inv.setItem(26, close);
        
        player.openInventory(inv);
    }
    
    private ItemStack createQuestItem(DailyQuest quest) {
        Material material;
        ChatColor color;
        
        if (quest.isCompleted()) {
            material = Material.EMERALD;
            color = ChatColor.GREEN;
        } else if (quest.isExpired()) {
            material = Material.BARRIER;
            color = ChatColor.GRAY;
        } else {
            material = Material.DIAMOND;
            color = ChatColor.YELLOW;
        }
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color + quest.getFormattedDescription());
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        
        if (quest.isCompleted()) {
            lore.add(ChatColor.GREEN + "✓ COMPLÉTÉE");
            lore.add(ChatColor.GOLD + "Récompense obtenue: " + quest.getRewardCoins() + " coins");
        } else if (quest.isExpired()) {
            lore.add(ChatColor.RED + "✗ EXPIRÉE");
        } else {
            // Progress bar
            double progress = quest.getProgressPercentage();
            int bars = (int) (progress / 10);
            StringBuilder progressBar = new StringBuilder(ChatColor.GREEN + "[");
            for (int i = 0; i < 10; i++) {
                if (i < bars) {
                    progressBar.append("█");
                } else {
                    progressBar.append(ChatColor.GRAY).append("█").append(ChatColor.GREEN);
                }
            }
            progressBar.append(ChatColor.GREEN).append("]");
            
            lore.add(ChatColor.GRAY + "Progression: " + ChatColor.YELLOW + 
                quest.getCurrentProgress() + "/" + quest.getTargetAmount());
            lore.add(progressBar.toString());
            lore.add(ChatColor.YELLOW + String.format("%.1f%%", progress) + " complété");
            lore.add("");
            lore.add(ChatColor.GOLD + "Récompense: " + ChatColor.YELLOW + quest.getRewardCoins() + " coins");
            lore.add("");
            
            // Time left
            long timeLeft = quest.getExpiresAt() - System.currentTimeMillis();
            long hours = TimeUnit.MILLISECONDS.toHours(timeLeft);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60;
            lore.add(ChatColor.GRAY + "Expire dans: " + ChatColor.WHITE + hours + "h " + minutes + "m");
        }
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    public void handleClick(Player player, int slot, Inventory inventory) {
        if (slot == 26) {
            player.closeInventory();
        }
    }
}
