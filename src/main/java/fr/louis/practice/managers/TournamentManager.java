package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Tournament;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class TournamentManager {
    private final PracticeCore plugin;
    private final Map<UUID, Tournament> activeTournaments;
    private final Map<UUID, UUID> playerToTournament;
    
    public TournamentManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.activeTournaments = new ConcurrentHashMap<>();
        this.playerToTournament = new ConcurrentHashMap<>();
    }
    
    public Tournament createTournament(String kit, int maxPlayers) {
        // Validate power of 2
        if (!isPowerOfTwo(maxPlayers)) {
            return null;
        }
        
        Tournament tournament = new Tournament(kit, maxPlayers);
        activeTournaments.put(tournament.getTournamentId(), tournament);
        
        broadcastMessage(ChatColor.GOLD + "⚔ Tournoi " + kit + " (" + maxPlayers + " joueurs) créé! /tournament join");
        
        return tournament;
    }
    
    public boolean joinTournament(UUID tournamentId, UUID player) {
        Tournament tournament = activeTournaments.get(tournamentId);
        if (tournament == null) return false;
        if (playerToTournament.containsKey(player)) return false;
        
        if (tournament.addParticipant(player)) {
            playerToTournament.put(player, tournamentId);
            
            Player bukPlayer = Bukkit.getPlayer(player);
            if (bukPlayer != null) {
                bukPlayer.sendMessage(ChatColor.GREEN + "Vous avez rejoint le tournoi " + tournament.getKit() + "!");
                bukPlayer.sendMessage(ChatColor.YELLOW + "Joueurs: " + tournament.getParticipants().size() + "/" + tournament.getMaxPlayers());
            }
            
            // Check if full
            if (tournament.isFull()) {
                startTournament(tournamentId);
            }
            
            return true;
        }
        
        return false;
    }
    
    public void leaveTournament(UUID player) {
        UUID tournamentId = playerToTournament.get(player);
        if (tournamentId == null) return;
        
        Tournament tournament = activeTournaments.get(tournamentId);
        if (tournament == null) return;
        
        tournament.removeParticipant(player);
        playerToTournament.remove(player);
        
        Player bukPlayer = Bukkit.getPlayer(player);
        if (bukPlayer != null) {
            bukPlayer.sendMessage(ChatColor.RED + "Vous avez quitté le tournoi!");
        }
    }
    
    private void startTournament(UUID tournamentId) {
        Tournament tournament = activeTournaments.get(tournamentId);
        if (tournament == null) return;
        
        broadcastMessage(ChatColor.GOLD + "⚔ Tournoi " + tournament.getKit() + " commence maintenant!");
        
        // Shuffle participants
        List<UUID> participants = new ArrayList<>(tournament.getParticipants());
        Collections.shuffle(participants);
        
        // Create initial bracket (round 1)
        tournament.getBrackets().put(1, new ArrayList<>(participants));
        
        // Start matches for round 1
        startRoundMatches(tournament, 1);
    }
    
    private void startRoundMatches(Tournament tournament, int round) {
        List<UUID> players = tournament.getRoundPlayers(round);
        
        broadcastMessage(ChatColor.YELLOW + "Round " + round + " du tournoi " + tournament.getKit() + " commence!");
        
        // Pair players and start matches
        for (int i = 0; i < players.size(); i += 2) {
            if (i + 1 < players.size()) {
                UUID player1 = players.get(i);
                UUID player2 = players.get(i + 1);
                
                Player p1 = Bukkit.getPlayer(player1);
                Player p2 = Bukkit.getPlayer(player2);
                
                if (p1 != null && p2 != null) {
                        // Tournament match starting will be implemented when DuelManager is complete
                    p1.sendMessage("§cLe système de duels de tournoi n'est pas encore implémenté");
                    p2.sendMessage("§cLe système de duels de tournoi n'est pas encore implémenté");
                }
            }
        }
    }
    
    public void handleTournamentMatchEnd(UUID tournamentId, UUID winner, UUID loser) {
        Tournament tournament = activeTournaments.get(tournamentId);
        if (tournament == null) return;
        
        int currentRound = tournament.getCurrentRound();
        int nextRound = currentRound + 1;
        
        // Advance winner to next round
        tournament.advancePlayer(winner, nextRound);
        
        // Remove loser
        playerToTournament.remove(loser);
        
        Player winnerPlayer = Bukkit.getPlayer(winner);
        if (winnerPlayer != null) {
            winnerPlayer.sendMessage(ChatColor.GREEN + "Vous passez au round " + nextRound + "!");
        }
        
        // Check if round is complete
        List<UUID> nextRoundPlayers = tournament.getRoundPlayers(nextRound);
        List<UUID> currentRoundPlayers = tournament.getRoundPlayers(currentRound);
        
        if (nextRoundPlayers.size() >= currentRoundPlayers.size() / 2) {
            // Round complete
            if (nextRoundPlayers.size() == 1) {
                // Tournament finished!
                finishTournament(tournament, nextRoundPlayers.get(0));
            } else {
                // Start next round
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    startRoundMatches(tournament, nextRound);
                }, 20L * 10); // 10 seconds delay
            }
        }
    }
    
    private void finishTournament(Tournament tournament, UUID winnerId) {
        tournament.setWinner(winnerId);
        
        Player winner = Bukkit.getPlayer(winnerId);
        String winnerName = winner != null ? winner.getName() : "Unknown";
        
        broadcastMessage(ChatColor.GOLD + "════════════════════════════");
        broadcastMessage(ChatColor.YELLOW + "Tournoi " + tournament.getKit() + " terminé!");
        broadcastMessage(ChatColor.GREEN + "Vainqueur: " + ChatColor.GOLD + winnerName);
        broadcastMessage(ChatColor.GOLD + "════════════════════════════");
        
        // Reward winner
        if (winner != null) {
            int reward = tournament.getMaxPlayers() * 100;
            winner.sendMessage(ChatColor.GOLD + "Récompense: " + reward + " coins");
            
            plugin.getPlayerManager().getPlayer(winnerId).setCoins(
                plugin.getPlayerManager().getPlayer(winnerId).getCoins() + reward
            );
        }
        
        // Cleanup
        for (UUID participant : tournament.getParticipants()) {
            playerToTournament.remove(participant);
        }
        activeTournaments.remove(tournament.getTournamentId());
    }
    
    public Tournament getPlayerTournament(UUID player) {
        UUID tournamentId = playerToTournament.get(player);
        return tournamentId != null ? activeTournaments.get(tournamentId) : null;
    }
    
    public List<Tournament> getActiveTournaments() {
        return new ArrayList<>(activeTournaments.values());
    }
    
    private boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }
    
    private void broadcastMessage(String message) {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }
}
