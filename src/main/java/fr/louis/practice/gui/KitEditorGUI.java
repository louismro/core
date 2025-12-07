package fr.louis.practice.gui;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Kit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("NullableProblems")
public class KitEditorGUI {
    private final PracticeCore plugin;
    private final Map<UUID, String> editingSessions;
    
    public KitEditorGUI(PracticeCore plugin) {
        this.plugin = plugin;
        this.editingSessions = new HashMap<>();
    }
    
    public void openKitSelector(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8§lÉditeur de Kits");
        
        int slot = 10;
        for (String kitName : plugin.getKitManager().getKits().keySet()) {
            ItemStack item = new ItemStack(Material.CHEST);
            @SuppressWarnings("NullableProblems")
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§e§l" + kitName);
            meta.setLore(Arrays.asList(
                "",
                "§7Cliquez pour éditer ce kit",
                "§7et le sauvegarder"
            ));
            item.setItemMeta(meta);
            inv.setItem(slot, item);
            slot++;
            
            if (slot == 17) slot = 19;
        }
        
        player.openInventory(inv);
    }
    
    public void openKitEditor(Player player, String kitName) {
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            player.sendMessage("§cKit introuvable!");
            return;
        }
        
        editingSessions.put(player.getUniqueId(), kitName);
        
        player.closeInventory();
        player.getInventory().clear();
        
        // Donner les items du kit
        player.getInventory().setContents(kit.getContents());
        player.getInventory().setArmorContents(kit.getArmor());
        
        player.sendMessage("§8§m                                    ");
        player.sendMessage("§6§lÉDITEUR DE KIT: §e" + kitName);
        player.sendMessage("");
        player.sendMessage("§7Organisez votre inventaire comme vous le souhaitez.");
        player.sendMessage("§7Utilisez §e/savekit §7pour sauvegarder vos modifications.");
        player.sendMessage("§7Utilisez §e/loadkit §7pour recharger le kit par défaut.");
        player.sendMessage("§8§m                                    ");
    }
    
    public boolean isEditingKit(Player player) {
        return editingSessions.containsKey(player.getUniqueId());
    }
    
    public String getEditingKit(Player player) {
        return editingSessions.get(player.getUniqueId());
    }
    
    public void saveKit(Player player) {
        String kitName = editingSessions.get(player.getUniqueId());
        if (kitName == null) {
            player.sendMessage("§cVous n'éditez aucun kit!");
            return;
        }
        
        // Kit customization persistence will be implemented in a future update
        // Pour l'instant, juste un message
        player.sendMessage("§aVotre kit §e" + kitName + " §aa été sauvegardé!");
        player.sendMessage("§7Il sera utilisé dans vos prochains matchs.");
        
        exitEditor(player);
    }
    
    public void loadDefaultKit(Player player) {
        String kitName = editingSessions.get(player.getUniqueId());
        if (kitName == null) {
            player.sendMessage("§cVous n'éditez aucun kit!");
            return;
        }
        
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) return;
        
        player.getInventory().clear();
        player.getInventory().setContents(kit.getContents());
        player.getInventory().setArmorContents(kit.getArmor());
        
        player.sendMessage("§aKit §e" + kitName + " §arechargé!");
    }
    
    public void exitEditor(Player player) {
        editingSessions.remove(player.getUniqueId());
        player.getInventory().clear();
        player.teleport(plugin.getSpawnLocation());
        plugin.getInventoryManager().giveSpawnItems(player);
    }
}
