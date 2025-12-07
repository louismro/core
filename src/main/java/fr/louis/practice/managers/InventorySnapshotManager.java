package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

@Getter
public class InventorySnapshotManager {
    private final PracticeCore plugin;
    private final Map<UUID, List<InventorySnapshot>> snapshots;
    
    public InventorySnapshotManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.snapshots = new HashMap<>();
    }
    
    public InventorySnapshot createSnapshot(Player player, String opponentName) {
        InventorySnapshot snapshot = new InventorySnapshot(
            player.getUniqueId(),
            player.getName(),
            opponentName,
            System.currentTimeMillis(),
            player.getInventory().getContents().clone(),
            player.getInventory().getArmorContents().clone(),
            player.getHealth(),
            player.getFoodLevel(),
            new ArrayList<>(player.getActivePotionEffects())
        );
        
        snapshots.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(snapshot);
        
        // Garder seulement les 10 derniers snapshots
        List<InventorySnapshot> playerSnapshots = snapshots.get(player.getUniqueId());
        if (playerSnapshots.size() > 10) {
            playerSnapshots.remove(0);
        }
        
        return snapshot;
    }
    
    public List<InventorySnapshot> getSnapshots(UUID player) {
        return snapshots.getOrDefault(player, new ArrayList<>());
    }
    
    public InventorySnapshot getLastSnapshot(UUID player) {
        List<InventorySnapshot> playerSnapshots = snapshots.get(player);
        if (playerSnapshots == null || playerSnapshots.isEmpty()) {
            return null;
        }
        return playerSnapshots.get(playerSnapshots.size() - 1);
    }
    
    @Getter
    public static class InventorySnapshot {
        private final UUID player;
        private final String playerName;
        private final String opponentName;
        private final long timestamp;
        private final ItemStack[] contents;
        private final ItemStack[] armor;
        private final double health;
        private final int food;
        private final List<PotionEffect> effects;
        
        public InventorySnapshot(UUID player, String playerName, String opponentName, 
                                long timestamp, ItemStack[] contents, ItemStack[] armor,
                                double health, int food, List<PotionEffect> effects) {
            this.player = player;
            this.playerName = playerName;
            this.opponentName = opponentName;
            this.timestamp = timestamp;
            this.contents = contents;
            this.armor = armor;
            this.health = health;
            this.food = food;
            this.effects = effects;
        }
        
        public int getPotionCount() {
            int count = 0;
            for (ItemStack item : contents) {
                if (item != null && item.getType() == Material.POTION) {
                    @SuppressWarnings("deprecation")
                    short durability = item.getDurability();
                    if (durability == 16421) {
                        count += item.getAmount();
                    }
                }
            }
            return count;
        }
    }
}
