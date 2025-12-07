package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class StaffModeManager {
    private final PracticeCore plugin;
    private final Set<UUID> staffMode = ConcurrentHashMap.newKeySet();
    private final Map<UUID, StaffModeData> savedData = new ConcurrentHashMap<>();
    
    public StaffModeManager(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    public boolean isInStaffMode(UUID playerId) {
        return staffMode.contains(playerId);
    }
    
    public void enableStaffMode(Player player) {
        if (staffMode.contains(player.getUniqueId())) {
            return;
        }
        
        // Sauvegarder l'état actuel
        StaffModeData data = new StaffModeData();
        data.inventory = player.getInventory().getContents();
        data.armor = player.getInventory().getArmorContents();
        data.gameMode = player.getGameMode();
        data.allowFlight = player.getAllowFlight();
        data.flying = player.isFlying();
        savedData.put(player.getUniqueId(), data);
        
        // Activer le staff mode
        staffMode.add(player.getUniqueId());
        
        // Configurer le joueur
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.CREATIVE);
        player.setAllowFlight(true);
        player.setFlying(true);
        
        // Donner les items de staff
        giveStaffItems(player);
        
        // Activer le vanish automatiquement
        if (!plugin.getSpectatorManager().isVanished(player.getUniqueId())) {
            plugin.getSpectatorManager().addVanish(player.getUniqueId());
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.hasPermission("practice.staff")) {
                    online.hidePlayer(plugin, player);
                }
            }
        }
        
        player.sendMessage("§a✓ Mode staff activé");
    }
    
    public void disableStaffMode(Player player) {
        if (!staffMode.contains(player.getUniqueId())) {
            return;
        }
        
        // Désactiver le staff mode
        staffMode.remove(player.getUniqueId());
        
        // Restaurer l'état sauvegardé
        StaffModeData data = savedData.remove(player.getUniqueId());
        if (data != null) {
            player.getInventory().setContents(data.inventory);
            player.getInventory().setArmorContents(data.armor);
            player.setGameMode(data.gameMode);
            player.setAllowFlight(data.allowFlight);
            player.setFlying(data.flying);
        } else {
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(false);
            player.setFlying(false);
        }
        
        // Désactiver le vanish
        if (plugin.getSpectatorManager().isVanished(player.getUniqueId())) {
            plugin.getSpectatorManager().removeVanish(player.getUniqueId());
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(plugin, player);
            }
        }
        
        player.sendMessage("§c✗ Mode staff désactivé");
    }
    
    private void giveStaffItems(Player player) {
        // Slot 0: Random Inspect (Compass)
        ItemStack compass = createItem(Material.COMPASS, "§aInspecter Aléatoire", 
            "§7Clic droit pour inspecter", "§7un joueur aléatoire");
        player.getInventory().setItem(0, compass);
        
        // Slot 1: Freeze (Packed Ice)
        ItemStack freeze = createItem(Material.PACKED_ICE, "§bFreeze", 
            "§7Clic droit pour freeze", "§7le joueur le plus proche");
        player.getInventory().setItem(1, freeze);
        
        // Slot 2: Teleport (Ender Pearl)
        ItemStack teleport = createItem(Material.ENDER_PEARL, "§dTéléportation", 
            "§7Clic droit pour ouvrir", "§7le menu de téléportation");
        player.getInventory().setItem(2, teleport);
        
        // Slot 4: World Edit (Stick)
        ItemStack worldEdit = createItem(Material.STICK, "§6World Edit", 
            "§7Utilisez les commandes", "§7//wand, //set, etc.");
        player.getInventory().setItem(4, worldEdit);
        
        // Slot 7: Vanish Toggle (Light Gray Dye)
        ItemStack vanish = createItem(Material.LIGHT_GRAY_DYE, "§7Vanish §8[§a✓§8]", 
            "§7Clic droit pour toggle", "§7Actuellement: §aActivé");
        player.getInventory().setItem(7, vanish);
        
        // Slot 8: Online Players (Book)
        ItemStack book = createItem(Material.BOOK, "§eJoueurs en ligne §8(§e" + Bukkit.getOnlinePlayers().size() + "§8)", 
            "§7Clic droit pour voir", "§7la liste des joueurs");
        player.getInventory().setItem(8, book);
    }
    
    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }
    
    public void updateVanishItem(Player player, boolean vanished) {
        if (!isInStaffMode(player.getUniqueId())) {
            return;
        }
        
        ItemStack vanish;
        if (vanished) {
            vanish = createItem(Material.LIGHT_GRAY_DYE, "§7Vanish §8[§a✓§8]", 
                "§7Clic droit pour toggle", "§7Actuellement: §aActivé");
        } else {
            vanish = createItem(Material.GRAY_DYE, "§7Vanish §8[§c✗§8]", 
                "§7Clic droit pour toggle", "§7Actuellement: §cDésactivé");
        }
        player.getInventory().setItem(7, vanish);
    }
    
    private static class StaffModeData {
        ItemStack[] inventory;
        ItemStack[] armor;
        GameMode gameMode;
        boolean allowFlight;
        boolean flying;
    }
}
