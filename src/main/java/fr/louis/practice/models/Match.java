package fr.louis.practice.models;

import org.bukkit.Location;

import java.util.*;

public class Match {
    public enum MatchState {
        STARTING,
        FIGHTING,
        ENDING,
        ENDED
    }
    
    private final UUID matchId;
    private final MatchType type;
    private final String kitName;
    private final Arena arena;
    private final List<MatchTeam> teams;
    private final List<UUID> spectators;
    
    private MatchState state;
    private long startTime;
    private long endTime;
    private int countdown;
    
    private final boolean ranked;
    private final boolean build;
    
    // Statistiques du match
    private Map<UUID, MatchPlayerData> playerData;
    
    // Gates (barrières) pour le countdown style WoW
    private List<Location> gateLocations;
    
    public Match(MatchType type, String kitName, Arena arena, boolean ranked, boolean build) {
        this.matchId = UUID.randomUUID();
        this.type = type;
        this.kitName = kitName;
        this.arena = arena;
        this.teams = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.state = MatchState.STARTING;
        this.countdown = 5;
        this.ranked = ranked;
        this.build = build;
        this.playerData = new HashMap<>();
    }
    
    public void addTeam(MatchTeam team) {
        teams.add(team);
    }
    
    public MatchTeam getTeam1() {
        return teams.isEmpty() ? null : teams.get(0);
    }
    
    public MatchTeam getTeam2() {
        return teams.size() < 2 ? null : teams.get(1);
    }
    
    public void addSpectator(UUID uuid) {
        spectators.add(uuid);
    }
    
    public void removeSpectator(UUID uuid) {
        spectators.remove(uuid);
    }
    
    public List<UUID> getAllPlayers() {
        List<UUID> players = new ArrayList<>();
        for (MatchTeam team : teams) {
            players.addAll(team.getPlayers());
        }
        return players;
    }
    
    public List<UUID> getAlivePlayers() {
        List<UUID> alive = new ArrayList<>();
        for (MatchTeam team : teams) {
            alive.addAll(team.getAlivePlayers());
        }
        return alive;
    }
    
    public MatchTeam getTeam(UUID player) {
        for (MatchTeam team : teams) {
            if (team.containsPlayer(player)) {
                return team;
            }
        }
        return null;
    }
    
    public MatchTeam getOpponentTeam(UUID player) {
        for (MatchTeam team : teams) {
            if (!team.containsPlayer(player)) {
                return team;
            }
        }
        return null;
    }
    
    public UUID getOpponent(UUID player) {
        if (type != MatchType.SOLO) return null;
        
        for (UUID uuid : getAllPlayers()) {
            if (!uuid.equals(player)) {
                return uuid;
            }
        }
        return null;
    }
    
    public MatchPlayerData getPlayerData(UUID player) {
        return playerData.computeIfAbsent(player, k -> new MatchPlayerData());
    }
    
    public void start() {
        this.state = MatchState.FIGHTING;
        this.startTime = System.currentTimeMillis();
    }
    
    public void end(MatchTeam winner) {
        this.state = MatchState.ENDING;
        this.endTime = System.currentTimeMillis();
    }
    
    public long getDuration() {
        if (startTime == 0) return 0;
        long end = endTime == 0 ? System.currentTimeMillis() : endTime;
        return (end - startTime) / 1000;
    }
    
    public boolean hasStarted() {
        return state == MatchState.FIGHTING;
    }
    
    public boolean hasEnded() {
        return state == MatchState.ENDING || state == MatchState.ENDED;
    }
    
    public boolean isFFA() {
        return type == MatchType.FFA;
    }
    
    // Getters
    public UUID getMatchId() { return matchId; }
    public MatchType getType() { return type; }
    public String getKitName() { return kitName; }
    public Arena getArena() { return arena; }
    public List<MatchTeam> getTeams() { return teams; }
    public List<UUID> getSpectators() { return spectators; }
    public MatchState getState() { return state; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public int getCountdown() { return countdown; }
    public boolean isRanked() { return ranked; }
    public boolean isBuild() { return build; }
    public Map<UUID, MatchPlayerData> getPlayerData() { return playerData; }
    public List<Location> getGateLocations() { return gateLocations; }
    
    // Setters
    public void setState(MatchState state) { this.state = state; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    public void setCountdown(int countdown) { this.countdown = countdown; }
    public void setPlayerData(Map<UUID, MatchPlayerData> playerData) { this.playerData = playerData; }
    public void setGateLocations(List<Location> gateLocations) { this.gateLocations = gateLocations; }
}
