package fr.louis.practice.models;


import java.util.*;

public class MatchTeam {
    private final UUID teamId;
    private final String name;
    private final List<UUID> players;
    private final Set<UUID> deadPlayers;
    
    public MatchTeam(String name) {
        this.teamId = UUID.randomUUID();
        this.name = name;
        this.players = new ArrayList<>();
        this.deadPlayers = new HashSet<>();
    }
    
    public void addPlayer(UUID player) {
        players.add(player);
    }
    
    public void removePlayer(UUID player) {
        players.remove(player);
    }
    
    public void killPlayer(UUID player) {
        deadPlayers.add(player);
    }
    
    public boolean containsPlayer(UUID player) {
        return players.contains(player);
    }
    
    public boolean isPlayerAlive(UUID player) {
        return players.contains(player) && !deadPlayers.contains(player);
    }
    
    public List<UUID> getAlivePlayers() {
        List<UUID> alive = new ArrayList<>(players);
        alive.removeAll(deadPlayers);
        return alive;
    }
    
    public int getAliveCount() {
        return players.size() - deadPlayers.size();
    }
    
    public boolean isEliminated() {
        return getAliveCount() == 0;
    }

    // Getters
    public UUID getTeamId() { return teamId; }
    public String getName() { return name; }
    public List<UUID> getPlayers() { return players; }
    public Set<UUID> getDeadPlayers() { return deadPlayers; }
}

