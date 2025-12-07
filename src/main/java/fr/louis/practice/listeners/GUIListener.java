package fr.louis.practice.listeners;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.gui.KitEditorGUI;
import fr.louis.practice.gui.QueueSelectorGUI;
import fr.louis.practice.gui.StatsGUI;
import fr.louis.practice.gui.ShopGUI;
import fr.louis.practice.gui.QuestsGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("NullableProblems")
public class GUIListener implements Listener {
    private final PracticeCore plugin;
    private final QueueSelectorGUI queueGUI;
    private final StatsGUI statsGUI;
    private final KitEditorGUI kitEditorGUI;
    private final ShopGUI shopGUI;
    private final QuestsGUI questsGUI;
    
    public GUIListener(PracticeCore plugin) {
        this.plugin = plugin;
        this.queueGUI = new QueueSelectorGUI(plugin);
        this.statsGUI = new StatsGUI(plugin);
        this.kitEditorGUI = new KitEditorGUI(plugin);
        this.shopGUI = new ShopGUI(plugin);
        this.questsGUI = new QuestsGUI(plugin);
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }
        
        String displayName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        
        // Queue Items (depuis InventoryManager)
        if (displayName.equals("Queues Ranked")) {
            event.setCancelled(true);
            queueGUI.openRankedQueues(player);
        } else if (displayName.equals("Queues Unranked")) {
            event.setCancelled(true);
            queueGUI.openUnrankedQueues(player);
        } else if (displayName.equals("Mes Statistiques")) {
            event.setCancelled(true);
            statsGUI.openStats(player);
        } else if (displayName.equals("Editeur de Kits")) {
            event.setCancelled(true);
            kitEditorGUI.openKitSelector(player);
        } else if (displayName.equals("Quitter le spectateur")) {
            event.setCancelled(true);
            plugin.getSpectatorManager().stopSpectating(player);
        } else if (displayName.equals("Boutique") || item.getType() == Material.EMERALD) {
            event.setCancelled(true);
            shopGUI.openMainShop(player);
        } else if (displayName.contains("Quêtes") || (item.getType() == Material.PAPER && displayName.contains("Quête"))) {
            event.setCancelled(true);
            questsGUI.openQuests(player);
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        String title = event.getView().getTitle();
        ItemStack item = event.getCurrentItem();
        
        if (item == null || item.getType() == Material.AIR) return;
        
        // Queue GUIs
        if (title.contains("Queues Ranked")) {
            event.setCancelled(true);
            queueGUI.handleClick(player, item, true);
        } else if (title.contains("Queues Unranked")) {
            event.setCancelled(true);
            queueGUI.handleClick(player, item, false);
        } else if (title.contains("Stats de")) {
            event.setCancelled(true);
        } else if (title.contains("Éditeur de Kits")) {
            event.setCancelled(true);
            String kitName = ChatColor.stripColor(item.getItemMeta().getDisplayName()).toLowerCase();
            kitEditorGUI.openKitEditor(player, kitName);
        } else if (title.contains("BOUTIQUE") || title.contains("Titres") || 
                   title.contains("Effets") || title.contains("Killstreak")) {
            event.setCancelled(true);
            shopGUI.handleClick(player, event.getSlot(), event.getInventory());
        } else if (title.contains("QUÊTES")) {
            event.setCancelled(true);
            questsGUI.handleClick(player, event.getSlot(), event.getInventory());
        }
    }
}
