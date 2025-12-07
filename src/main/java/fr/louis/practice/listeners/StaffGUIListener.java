package fr.louis.practice.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StaffGUIListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        // Menu de téléportation
        if (title.equals("§6Téléportation")) {
            event.setCancelled(true);
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() != Material.PLAYER_HEAD) {
                return;
            }
            
            if (!clicked.hasItemMeta()) {
                return;
            }
            
            ItemMeta meta = clicked.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) {
                return;
            }
            
            String targetName = meta.getDisplayName().replace("§e", "");
            Player target = Bukkit.getPlayer(targetName);
            
            if (target == null) {
                player.sendMessage("§cJoueur introuvable!");
                player.closeInventory();
                return;
            }
            
            org.bukkit.Location targetLoc = target.getLocation();
            if (targetLoc != null) {
                player.teleport(targetLoc);
                player.sendMessage("§aTéléportation vers §e" + target.getName());
            } else {
                player.sendMessage("§cImpossible de téléporter!");
            }
            player.closeInventory();
        }
        
        // Menu des joueurs en ligne
        else if (title.startsWith("§eJoueurs en ligne")) {
            event.setCancelled(true);
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() != Material.PLAYER_HEAD) {
                return;
            }
            
            if (!clicked.hasItemMeta()) {
                return;
            }
            
            ItemMeta meta = clicked.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) {
                return;
            }
            
            String targetName = meta.getDisplayName().replace("§e", "");
            Player target = Bukkit.getPlayer(targetName);
            
            if (target == null) {
                player.sendMessage("§cJoueur introuvable!");
                return;
            }
            
            // Ouvrir l'inventaire du joueur
            player.openInventory(target.getInventory());
            player.sendMessage("§aInspection de §e" + target.getName());
        }
    }
}
