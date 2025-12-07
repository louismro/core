package fr.louis.practice.models;

import org.bukkit.Location;

import java.util.*;

public class FFAEvent {
    private final UUID eventId;
    private final String kit;
    private final Location spawnLocation;
    private final FFAState state;
    private final Set<UUID> participants;
    private final Map<UUID, Integer> kills;
    private final int maxPlayers;
    private final long startedAt;
    private UUID topKiller;
    
    public enum FFAState {
        WAITING,
        ACTIVE,
        ENDING,
        FINISHED
    }
    
    public FFAEvent(String kit, Location spawnLocation, int maxPlayers) {
        this.eventId = UUID.randomUUID();
        this.kit = kit;
        this.spawnLocation = spawnLocation;
        this.state = FFAState.WAITING;
        this.participants = new HashSet<>();
        this.kills = new HashMap<>();
        this.maxPlayers = maxPlayers;
        this.startedAt = System.currentTimeMillis();
    }
    
    public void addParticipant(UUID player) {
        participants.add(player);
        kills.put(player, 0);
    }
    
    public void removeParticipant(UUID player) {
        participants.remove(player);
        kills.remove(player);
    }
    
    public void addKill(UUID player) {
        kills.put(player, kills.getOrDefault(player, 0) + 1);
        updateTopKiller();
    }
    
    private void updateTopKiller() {
        topKiller = kills.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    public int getKills(UUID player) {
        return kills.getOrDefault(player, 0);
    }
    
    public List<Map.Entry<UUID, Integer>> getTopKillers(int limit) {
        List<Map.Entry<UUID, Integer>> sorted = new ArrayList<>(kills.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return sorted.subList(0, Math.min(limit, sorted.size()));
    }
    
    public boolean isFull() {
        return participants.size() >= maxPlayers;
    }
    
    public int getParticipantCount() {
        return participants.size();
    }

    // Getters
    public UUID getEventId() { return eventId; }
    public String getKit() { return kit; }
    public Location getSpawnLocation() { return spawnLocation; }
    public FFAState getState() { return state; }
    public Set<UUID> getParticipants() { return participants; }
    public Map<UUID, Integer> getKills() { return kills; }
    public int getMaxPlayers() { return maxPlayers; }
    public long getStartedAt() { return startedAt; }
    public UUID getTopKiller() { return topKiller; }
}

