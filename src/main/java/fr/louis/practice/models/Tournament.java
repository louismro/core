package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;
import java.util.*;

@Getter
@Setter
public class Tournament {
    private final UUID tournamentId;
    private final String kit;
    private final int maxPlayers;
    private final TournamentState state;
    private final List<UUID> participants;
    private final Map<Integer, List<UUID>> brackets; // Round -> Players in that round
    private final Map<UUID, Integer> playerRounds; // Player -> Current round
    private UUID winner;
    private final long startedAt;
    
    public enum TournamentState {
        WAITING,
        STARTING,
        IN_PROGRESS,
        FINISHED
    }
    
    public Tournament(String kit, int maxPlayers) {
        this.tournamentId = UUID.randomUUID();
        this.kit = kit;
        this.maxPlayers = maxPlayers;
        this.state = TournamentState.WAITING;
        this.participants = new ArrayList<>();
        this.brackets = new HashMap<>();
        this.playerRounds = new HashMap<>();
        this.startedAt = System.currentTimeMillis();
    }
    
    public boolean addParticipant(UUID player) {
        if (participants.size() >= maxPlayers) return false;
        if (state != TournamentState.WAITING) return false;
        
        participants.add(player);
        return true;
    }
    
    public void removeParticipant(UUID player) {
        participants.remove(player);
        playerRounds.remove(player);
    }
    
    public boolean isFull() {
        return participants.size() >= maxPlayers;
    }
    
    public int getCurrentRound() {
        return brackets.keySet().stream().max(Integer::compareTo).orElse(0);
    }
    
    public List<UUID> getRoundPlayers(int round) {
        return brackets.getOrDefault(round, new ArrayList<>());
    }
    
    public void advancePlayer(UUID player, int toRound) {
        playerRounds.put(player, toRound);
        brackets.computeIfAbsent(toRound, k -> new ArrayList<>()).add(player);
    }
    
    public int getRemainingPlayers() {
        int currentRound = getCurrentRound();
        return getRoundPlayers(currentRound).size();
    }
}
