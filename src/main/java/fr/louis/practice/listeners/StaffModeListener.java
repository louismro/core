package fr.louis.practice.listeners;

import fr.louis.practice.PracticeCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StaffModeListener implements Listener {
    private final PracticeCore plugin;
    
    public StaffModeListener(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getStaffModeManager().isInStaffMode(player.getUniqueId())) {
            return;
        }
        
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        
        if (event.getAction() != Action.RIGHT_CLICK_AIR && 
            event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        event.setCancelled(true);
        
        switch (item.getType()) {
            case COMPASS -> handleRandomInspect(player);
            case PACKED_ICE -> handleFreeze(player);
            case ENDER_PEARL -> handleTeleportMenu(player);
            case LIGHT_GRAY_DYE, GRAY_DYE -> handleVanishToggle(player);
            case BOOK -> handleOnlinePlayers(player);
            case STICK -> event.setCancelled(false);
            default -> {
            }
        }
    }
    
    private void handleRandomInspect(Player player) {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        onlinePlayers.remove(player);
        
        if (onlinePlayers.isEmpty()) {
            player.sendMessage("§cAucun joueur à inspecter!");
            return;
        }
        
        Player target = onlinePlayers.get((int) (Math.random() * onlinePlayers.size()));
        player.openInventory(target.getInventory());
        player.sendMessage("§aInspection de §e" + target.getName());
    }
    
    private void handleFreeze(Player player) {
        Player nearest = getNearestPlayer(player, 10.0);
        
        if (nearest == null) {
            player.sendMessage("§cAucun joueur à proximité (10 blocs)!");
            return;
        }
        
        boolean frozen = nearest.hasMetadata("frozen") && nearest.getMetadata("frozen").get(0).asBoolean();
        
        if (frozen) {
            nearest.removeMetadata("frozen", plugin);
            nearest.setWalkSpeed(0.2f);
            nearest.setFlySpeed(0.1f);
            
            nearest.sendMessage("");
            nearest.sendMessage("§a§l✓ Vous avez été défreeze");
            nearest.sendMessage("");
            
            player.sendMessage("§a✓ " + nearest.getName() + " a été défreeze");
        } else {
            nearest.setMetadata("frozen", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
            nearest.setWalkSpeed(0.0f);
            nearest.setFlySpeed(0.0f);
            
            nearest.sendMessage("");
            nearest.sendMessage("§c§l⚠ VOUS AVEZ ÉTÉ FREEZE");
            nearest.sendMessage("§7Vous ne pouvez plus bouger.");
            nearest.sendMessage("§7Contactez le staff sur Discord.");
            nearest.sendMessage("§c§lDéconnexion = BAN PERMANENT");
            nearest.sendMessage("");
            
            player.sendMessage("§a✓ " + nearest.getName() + " a été freeze");
        }
    }
    
    private void handleTeleportMenu(Player player) {
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, 54, "§6Téléportation");
        
        int slot = 0;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (slot >= 54) break;
            if (online.equals(player)) continue;
            
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            org.bukkit.inventory.meta.SkullMeta meta = (org.bukkit.inventory.meta.SkullMeta) skull.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e" + online.getName());
                List<String> lore = new ArrayList<>();
                lore.add("§7Monde: §f" + online.getWorld().getName());
                org.bukkit.Location onlineLoc = online.getLocation();
                if (onlineLoc != null) {
                    lore.add("§7Position: §f" + (int)onlineLoc.getX() + ", " +
                        (int)onlineLoc.getY() + ", " + (int)onlineLoc.getZ());
                }
                lore.add("");
                lore.add("§aClic pour se téléporter");
                meta.setLore(lore);
                meta.setOwningPlayer(online);
                skull.setItemMeta(meta);
            }
            
            inv.setItem(slot++, skull);
        }
        
        player.openInventory(inv);
    }
    
    private void handleVanishToggle(Player player) {
        // Simple toggle vanish
        boolean vanished = player.hasMetadata("vanished");
        
        if (vanished) {
            // Unvanish
            player.removeMetadata("vanished", plugin);
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(plugin, player);
            }
            player.sendMessage("§7Vanish §c✗ Désactivé");
        } else {
            // Vanish
            player.setMetadata("vanished", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.hasPermission("practice.staff")) {
                    online.hidePlayer(plugin, player);
                }
            }
            player.sendMessage("§7Vanish §a✓ Activé");
        }
        
        plugin.getStaffModeManager().updateVanishItem(player, !vanished);
    }
    
    private void handleOnlinePlayers(Player player) {
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, 54, "§eJoueurs en ligne §8(§e" + Bukkit.getOnlinePlayers().size() + "§8)");
        
        int slot = 0;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (slot >= 54) break;
            
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            org.bukkit.inventory.meta.SkullMeta meta = (org.bukkit.inventory.meta.SkullMeta) skull.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e" + online.getName());
                List<String> lore = new ArrayList<>();
                
                if (plugin.getPlayerManager().getPlayer(online) != null) {
                    lore.add("§7État: §f" + plugin.getPlayerManager().getPlayer(online).getState());
                }
                
                lore.add("§7Monde: §f" + online.getWorld().getName());
                lore.add("§7Vie: §c" + String.format("%.1f", online.getHealth()) + "/20");
                lore.add("§7Ping: §e" + getPing(online) + "ms");
                
                if (plugin.getSpectatorManager().isVanished(online.getUniqueId())) {
                    lore.add("");
                    lore.add("§7§o(En vanish)");
                }
                
                meta.setLore(lore);
                meta.setOwningPlayer(online);
                skull.setItemMeta(meta);
            }
            
            inv.setItem(slot++, skull);
        }
        
        player.openInventory(inv);
    }
    
    private Player getNearestPlayer(Player player, double maxDistance) {
        Player nearest = null;
        double nearestDist = maxDistance;
        
        org.bukkit.Location playerLoc = player.getLocation();
        if (playerLoc == null) return null;
        
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(player)) continue;
            if (!online.getWorld().equals(player.getWorld())) continue;
            
            org.bukkit.Location onlineLoc = online.getLocation();
            if (onlineLoc == null) continue;
            
            double dist = onlineLoc.distance(playerLoc);
            if (dist < nearestDist) {
                nearest = online;
                nearestDist = dist;
            }
        }
        
        return nearest;
    }
    
    private String getPing(Player player) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            return String.valueOf((int) handle.getClass().getField("ping").get(handle));
        } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
            return "?";
        }
    }
    
    // Empêcher les dégâts en staff mode
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        
        if (plugin.getStaffModeManager().isInStaffMode(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager) {
            if (plugin.getStaffModeManager().isInStaffMode(damager.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
    
    // Empêcher de drop des items
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (plugin.getStaffModeManager().isInStaffMode(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
    
    // Empêcher de ramasser des items
    @EventHandler
    public void onPlayerPickupItem(org.bukkit.event.entity.EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (plugin.getStaffModeManager().isInStaffMode(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
    
    // Permettre de casser des blocs en staff mode
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Les staffs peuvent casser des blocs
    }
    
    // Permettre de placer des blocs en staff mode
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Les staffs peuvent placer des blocs
    }
}
