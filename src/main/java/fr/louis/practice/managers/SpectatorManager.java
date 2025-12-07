package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Match;
import fr.louis.practice.models.PlayerState;
import fr.louis.practice.models.PracticePlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@SuppressWarnings("deprecation")
public class SpectatorManager {
    private final PracticeCore plugin;
    private final Map<UUID, ItemStack[]> savedInventories;
    private final Map<UUID, ItemStack[]> savedArmor;
    private final Map<UUID, UUID> spectating = new HashMap<>();
    private final Set<UUID> vanished = ConcurrentHashMap.newKeySet();
    
    public SpectatorManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.savedInventories = new HashMap<>();
        this.savedArmor = new HashMap<>();
    }
    
    @SuppressWarnings("NullableProblems")
    public void startSpectating(Player spectator, Match match) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getOrCreate(spectator);
        
        // Sauvegarder l'inventaire
        savedInventories.put(spectator.getUniqueId(), spectator.getInventory().getContents().clone());
        savedArmor.put(spectator.getUniqueId(), spectator.getInventory().getArmorContents().clone());
        
        // Clear inventaire et effets
        spectator.getInventory().clear();
        spectator.getInventory().setArmorContents(null);
        for (PotionEffect effect : spectator.getActivePotionEffects()) {
            spectator.removePotionEffect(effect.getType());
        }
        
        // Mode spectateur
        spectator.setGameMode(GameMode.ADVENTURE);
        spectator.setAllowFlight(true);
        spectator.setFlying(true);
        
        // Items de spectateur
        giveSpectatorItems(spectator);
        
        // TP au match
        if (!match.getTeam1().getAlivePlayers().isEmpty()) {
            Player target = Bukkit.getPlayer(match.getTeam1().getAlivePlayers().get(0));
            if (target != null) {
                @SuppressWarnings("NullableProblems")
                var location = target.getLocation();
                spectator.teleport(location);
            }
        }
        
        // Mettre à jour le state
        practicePlayer.setState(PlayerState.SPECTATING);
        practicePlayer.setCurrentMatch(match);
        match.addSpectator(spectator.getUniqueId());
        
        // Messages
        spectator.sendMessage("§aVous êtes maintenant spectateur du match!");
        spectator.sendMessage("§7Utilisez §e/stopspec §7pour arrêter.");
        
        // Cacher aux joueurs
        for (UUID playerId : match.getAllPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.hidePlayer(spectator);
            }
        }
    }
    
    public void stopSpectating(Player spectator) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getOrCreate(spectator);
        Match match = practicePlayer.getCurrentMatch();
        
        if (match != null) {
            match.removeSpectator(spectator.getUniqueId());
            
            // Montrer aux joueurs
            for (UUID playerId : match.getAllPlayers()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.showPlayer(spectator);
                }
            }
        }
        
        // Restaurer l'inventaire
        if (savedInventories.containsKey(spectator.getUniqueId())) {
            spectator.getInventory().setContents(savedInventories.get(spectator.getUniqueId()));
            spectator.getInventory().setArmorContents(savedArmor.get(spectator.getUniqueId()));
            savedInventories.remove(spectator.getUniqueId());
            savedArmor.remove(spectator.getUniqueId());
        }
        
        // Restaurer le mode
        spectator.setGameMode(GameMode.SURVIVAL);
        spectator.setAllowFlight(false);
        spectator.setFlying(false);
        
        // TP au spawn
        spectator.teleport(plugin.getSpawnLocation());
        
        // Donner items spawn
        plugin.getInventoryManager().giveSpawnItems(spectator);
        
        // Update state
        practicePlayer.setState(PlayerState.SPAWN);
        practicePlayer.setCurrentMatch(null);
        
        spectator.sendMessage("§cVous ne spectateur plus le match.");
    }
    
    private void giveSpectatorItems(Player player) {
        PlayerInventory inv = player.getInventory();
        
        // Téléporteur de joueurs (compass)
        ItemStack compass = plugin.getInventoryManager().createItemPublic(
            org.bukkit.Material.COMPASS,
            "§aTéléporter aux joueurs",
            Arrays.asList("§7Clic droit pour voir la liste")
        );
        inv.setItem(0, compass);
        
        // Quitter (redstone)
        ItemStack quit = plugin.getInventoryManager().createItemPublic(
            org.bukkit.Material.REDSTONE,
            "§cQuitter le spectateur",
            Arrays.asList("§7Clic pour arrêter de spectateur")
        );
        inv.setItem(8, quit);
    }
    
    public boolean isSpectating(Player player) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getOrCreate(player);
        return practicePlayer.getState() == PlayerState.SPECTATING;
    }
    
    public boolean isVanished(UUID playerId) {
        return vanished.contains(playerId);
    }
    
    public void addVanish(UUID playerId) {
        vanished.add(playerId);
    }
    
    public void removeVanish(UUID playerId) {
        vanished.remove(playerId);
    }
}
