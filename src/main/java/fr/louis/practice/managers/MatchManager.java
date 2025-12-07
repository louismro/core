package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class MatchManager {
    private final PracticeCore plugin;
    private final Map<UUID, Match> matches;
    
    public MatchManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.matches = new ConcurrentHashMap<>();
    }
    
    public Match createMatch(MatchType type, String kitName, boolean ranked, boolean build) {
        Arena arena = plugin.getArenaManager().getAvailableArena();
        if (arena == null) {
            return null;
        }
        
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            return null;
        }
        
        Match match = new Match(type, kitName, arena, ranked, build);
        matches.put(match.getMatchId(), match);
        arena.setInUse(true);
        
        return match;
    }
    
    public void startMatch(Match match) {
        if (match.getState() != Match.MatchState.STARTING) {
            return;
        }
        
        // Téléporter les joueurs immédiatement
        List<UUID> players = match.getAllPlayers();
        for (int i = 0; i < players.size(); i++) {
            UUID uuid = players.get(i);
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.teleport(match.getArena().getSpawnPoint(i));
                
                // Nettoyer l'inventaire
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                
                // Donner le kit
                Kit kit = plugin.getKitManager().getKit(match.getKitName());
                if (kit != null) {
                    plugin.getLogger().log(java.util.logging.Level.INFO, 
                        () -> String.format("Giving kit %s to %s", match.getKitName(), player.getName()));
                    player.getInventory().setContents(kit.getContents());
                    player.getInventory().setArmorContents(kit.getArmor());
                    player.updateInventory();
                } else {
                    plugin.getLogger().log(java.util.logging.Level.WARNING,
                        () -> String.format("Kit %s not found!", match.getKitName()));
                }
                
                PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(uuid);
                if (practicePlayer != null) {
                    practicePlayer.setState(PlayerState.MATCH);
                    practicePlayer.setCurrentMatch(match);
                    practicePlayer.resetCombat();
                }
            }
        }
        
        // Créer des gates (barrières) devant les joueurs
        createGates(match);
        
        // Countdown de 3 secondes avec gates
        new org.bukkit.scheduler.BukkitRunnable() {
            int countdown = 3;
            
            @Override
            public void run() {
                if (countdown <= 0) {
                    // Retirer les gates et démarrer le match
                    removeGates(match);
                    match.start();
                    
                    // Message de début
                    for (UUID uuid : match.getAllPlayers()) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            player.sendMessage("§a§lGO!");
                            player.sendTitle("§a§lGO!", "", 10, 20, 10);
                            org.bukkit.Location loc = player.getLocation();
                            if (loc != null) {
                                player.playSound(loc, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            }
                        }
                    }
                    
                    // Mettre à jour le scoreboard
                    plugin.getScoreboardManager().updateMatch(match);
                    
                    cancel();
                    return;
                }
                
                // Afficher le countdown
                for (UUID uuid : match.getAllPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.sendTitle("§e§l" + countdown, "§7Préparez-vous...", 0, 25, 5);
                        org.bukkit.Location loc = player.getLocation();
                        if (loc != null) {
                            player.playSound(loc, org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                        }
                    }
                }
                
                countdown--;
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
    
    private void createGates(Match match) {
        List<org.bukkit.Location> gateLocations = new ArrayList<>();
        List<UUID> players = match.getAllPlayers();
        
        for (int i = 0; i < players.size(); i++) {
            org.bukkit.Location spawn = match.getArena().getSpawnPoint(i);
            org.bukkit.util.Vector direction = spawn.getDirection();
            
            // Créer une barrière 2 blocs devant le joueur
            for (int x = -1; x <= 1; x++) {
                for (int y = 0; y <= 2; y++) {
                    org.bukkit.Location gateBlock = spawn.clone().add(
                        direction.getX() * 2 + (direction.getZ() * x),
                        y,
                        direction.getZ() * 2 + (direction.getX() * x)
                    );
                    
                    if (gateBlock.getBlock().getType() == org.bukkit.Material.AIR) {
                        gateBlock.getBlock().setType(org.bukkit.Material.BARRIER);
                        gateLocations.add(gateBlock);
                    }
                }
            }
        }
        
        match.setGateLocations(gateLocations);
    }
    
    private void removeGates(Match match) {
        if (match.getGateLocations() != null) {
            for (org.bukkit.Location loc : match.getGateLocations()) {
                loc.getBlock().setType(org.bukkit.Material.AIR);
            }
            match.getGateLocations().clear();
        }
    }
    
    public void endMatch(Match match, MatchTeam winner) {
        if (match.hasEnded()) {
            return;
        }
        
        match.end(winner);
        
        // Calculer et appliquer les changements d'ELO
        if (match.isRanked()) {
            calculateEloChanges(match, winner);
        }
        
        // Mettre à jour les statistiques
        updateStatistics(match, winner);
        
        // Téléporter les joueurs au spawn
        for (UUID uuid : match.getAllPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                teleportToSpawn(player);
                
                PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(uuid);
                if (practicePlayer != null) {
                    practicePlayer.setState(PlayerState.SPAWN);
                    practicePlayer.setCurrentMatch(null);
                    practicePlayer.resetCombat();
                }
            }
        }
        
        // Libérer l'arène
        match.getArena().setInUse(false);
        
        // Supprimer le match après un délai
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            matches.remove(match.getMatchId());
        }, 100L);
    }
    
    private void calculateEloChanges(Match match, MatchTeam winner) {
        if (match.getType() != MatchType.SOLO) {
            return;
        }
        
        List<UUID> players = match.getAllPlayers();
        if (players.size() != 2) {
            return;
        }
        
        UUID player1 = players.get(0);
        UUID player2 = players.get(1);
        
        PracticePlayer pp1 = plugin.getPlayerManager().getPlayer(player1);
        PracticePlayer pp2 = plugin.getPlayerManager().getPlayer(player2);
        
        if (pp1 == null || pp2 == null) {
            return;
        }
        
        int elo1 = pp1.getElo(match.getKitName());
        int elo2 = pp2.getElo(match.getKitName());
        
        boolean player1Won = winner.containsPlayer(player1);
        
        int[] changes = plugin.getEloManager().calculateEloChange(elo1, elo2, player1Won);
        
        pp1.addElo(match.getKitName(), changes[0]);
        pp2.addElo(match.getKitName(), changes[1]);
        
        // Envoyer les messages
        Player p1 = Bukkit.getPlayer(player1);
        Player p2 = Bukkit.getPlayer(player2);
        
        if (p1 != null) {
            String msg = player1Won ? 
                "§aVous avez gagné! §7(+" + changes[0] + " ELO)" :
                "§cVous avez perdu! §7(" + changes[0] + " ELO)";
            p1.sendMessage(msg);
        }
        
        if (p2 != null) {
            String msg = !player1Won ? 
                "§aVous avez gagné! §7(+" + changes[1] + " ELO)" :
                "§cVous avez perdu! §7(" + changes[1] + " ELO)";
            p2.sendMessage(msg);
        }
    }
    
    private void updateStatistics(Match match, MatchTeam winner) {
        for (UUID uuid : match.getAllPlayers()) {
            PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(uuid);
            if (practicePlayer == null) continue;
            
            PlayerStats stats = practicePlayer.getStats(match.getKitName());
            
            if (winner.containsPlayer(uuid)) {
                stats.addWin();
                practicePlayer.setCurrentStreak(practicePlayer.getCurrentStreak() + 1);
                stats.updateStreak(practicePlayer.getCurrentStreak());
            } else {
                stats.addLoss();
                practicePlayer.setCurrentStreak(0);
            }
        }
    }
    
    private void teleportToSpawn(Player player) {
        player.teleport(plugin.getSpawnLocation());
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.getActivePotionEffects().clear();
        
        plugin.getInventoryManager().giveSpawnItems(player);
    }
    
    public Match getMatch(UUID matchId) {
        return matches.get(matchId);
    }
    
    public Match getMatchByPlayer(org.bukkit.entity.Player player) {
        return getPlayerMatch(player.getUniqueId());
    }
    
    public Match getPlayerMatch(UUID player) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        if (practicePlayer != null) {
            return practicePlayer.getCurrentMatch();
        }
        return null;
    }
    
    public Collection<Match> getAllMatches() {
        return matches.values();
    }
    
    public void endAllMatches() {
        for (Match match : new ArrayList<>(matches.values())) {
            if (!match.hasEnded()) {
                endMatch(match, null);
            }
        }
    }
}
