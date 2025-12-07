package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class TournamentData {
    private final String id;
    private final String name;
    private final String kitName;
    private final int maxPlayers;
    private final TournamentState state;
    private final List<UUID> participants;
    private final Map<UUID, Integer> scores;
    private final long startTime;
    private final int prizePool;
    
    public enum TournamentState {
        WAITING,
        STARTING,
        IN_PROGRESS,
        ENDED
    }
    
    public TournamentData(String id, String name, String kitName, int maxPlayers, int prizePool) {
        this.id = id;
        this.name = name;
        this.kitName = kitName;
        this.maxPlayers = maxPlayers;
        this.state = TournamentState.WAITING;
        this.participants = new ArrayList<>();
        this.scores = new HashMap<>();
        this.startTime = System.currentTimeMillis();
        this.prizePool = prizePool;
    }
    
    public boolean canJoin() {
        return state == TournamentState.WAITING && participants.size() < maxPlayers;
    }
    
    public void addParticipant(UUID playerId) {
        if (canJoin()) {
            participants.add(playerId);
            scores.put(playerId, 0);
        }
    }
    
    public void removeParticipant(UUID playerId) {
        participants.remove(playerId);
        scores.remove(playerId);
    }
    
    public void addScore(UUID playerId, int points) {
        scores.put(playerId, scores.getOrDefault(playerId, 0) + points);
    }
    
    public List<UUID> getTopPlayers(int count) {
        return scores.entrySet().stream()
            .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
            .limit(count)
            .map(Map.Entry::getKey)
            .collect(java.util.stream.Collectors.toList());
    }
    
    public int getPlayerRank(UUID playerId) {
        List<UUID> sorted = getTopPlayers(participants.size());
        return sorted.indexOf(playerId) + 1;
    }
}
