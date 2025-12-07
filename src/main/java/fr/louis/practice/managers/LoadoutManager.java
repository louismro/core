package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.KitLoadout;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LoadoutManager {
    
    // Map: PlayerId -> KitName -> LoadoutNumber -> Loadout
    @Getter
    private final Map<UUID, Map<String, Map<Integer, KitLoadout>>> loadouts = new ConcurrentHashMap<>();
    
    // Active loadout per player per kit
    private final Map<UUID, Map<String, Integer>> activeLoadouts = new ConcurrentHashMap<>();
    
    public LoadoutManager(PracticeCore plugin) {
    }
    
    public KitLoadout getLoadout(UUID playerId, String kitName, int loadoutNumber) {
        return loadouts
            .computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(kitName, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(loadoutNumber, k -> new KitLoadout(playerId, kitName, loadoutNumber));
    }
    
    public void saveLoadout(Player player, String kitName, int loadoutNumber) {
        KitLoadout loadout = getLoadout(player.getUniqueId(), kitName, loadoutNumber);
        
        // Sauvegarder l'inventaire actuel
        ItemStack[] contents = new ItemStack[36];
        System.arraycopy(player.getInventory().getContents(), 0, contents, 0, 36);
        loadout.setContents(contents);
        
        ItemStack[] armor = player.getInventory().getArmorContents();
        loadout.setArmorContents(armor);
        
        player.sendMessage(ChatColor.GREEN + "✓ Loadout " + loadoutNumber + " sauvegardé pour " + kitName);
    }
    
    public void loadLoadout(Player player, String kitName, int loadoutNumber) {
        KitLoadout loadout = getLoadout(player.getUniqueId(), kitName, loadoutNumber);
        
        if (loadout.getContents() == null || loadout.getContents()[0] == null) {
            player.sendMessage(ChatColor.RED + "Ce loadout est vide!");
            return;
        }
        
        // Appliquer le loadout
        player.getInventory().setContents(loadout.getContents());
        player.getInventory().setArmorContents(loadout.getArmorContents());
        player.updateInventory();
        
        // Définir comme actif
        setActiveLoadout(player.getUniqueId(), kitName, loadoutNumber);
        
        player.sendMessage(ChatColor.GREEN + "✓ Loadout " + loadoutNumber + " chargé!");
    }
    
    public void setActiveLoadout(UUID playerId, String kitName, int loadoutNumber) {
        activeLoadouts
            .computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
            .put(kitName, loadoutNumber);
    }
    
    public int getActiveLoadout(UUID playerId, String kitName) {
        Map<String, Integer> playerLoadouts = activeLoadouts.get(playerId);
        if (playerLoadouts == null) return 1;
        return playerLoadouts.getOrDefault(kitName, 1);
    }
    
    public void deleteLoadout(Player player, String kitName, int loadoutNumber) {
        Map<String, Map<Integer, KitLoadout>> playerLoadouts = loadouts.get(player.getUniqueId());
        if (playerLoadouts != null) {
            Map<Integer, KitLoadout> kitLoadouts = playerLoadouts.get(kitName);
            if (kitLoadouts != null) {
                kitLoadouts.remove(loadoutNumber);
                player.sendMessage(ChatColor.GREEN + "✓ Loadout " + loadoutNumber + " supprimé");
                return;
            }
        }
        player.sendMessage(ChatColor.RED + "Ce loadout n'existe pas!");
    }
    
    public void renameLoadout(Player player, String kitName, int loadoutNumber, String newName) {
        KitLoadout loadout = getLoadout(player.getUniqueId(), kitName, loadoutNumber);
        loadout.setCustomName(newName);
        player.sendMessage(ChatColor.GREEN + "✓ Loadout renommé en: " + ChatColor.WHITE + newName);
    }
    
    public List<KitLoadout> getPlayerLoadouts(UUID playerId, String kitName) {
        Map<String, Map<Integer, KitLoadout>> playerLoadouts = loadouts.get(playerId);
        if (playerLoadouts == null) return Collections.emptyList();
        
        Map<Integer, KitLoadout> kitLoadouts = playerLoadouts.get(kitName);
        if (kitLoadouts == null) return Collections.emptyList();
        
        return new ArrayList<>(kitLoadouts.values());
    }
}
