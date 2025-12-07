package fr.louis.practice.models;


import java.util.*;

public class Clan {
    private final UUID clanId;
    private String name;
    private String tag;
    private UUID leader;
    private List<UUID> members;
    private List<UUID> moderators;
    private int elo;
    private int wins;
    private int losses;
    private long createdAt;
    private Map<String, Object> settings;
    
    public Clan(String name, String tag, UUID leader) {
        this.clanId = UUID.randomUUID();
        this.name = name;
        this.tag = tag;
        this.leader = leader;
        this.members = new ArrayList<>();
        this.moderators = new ArrayList<>();
        this.members.add(leader);
        this.elo = 1000;
        this.wins = 0;
        this.losses = 0;
        this.createdAt = System.currentTimeMillis();
        this.settings = new HashMap<>();
        this.settings.put("friendlyFire", false);
        this.settings.put("maxMembers", 20);
        this.settings.put("public", false);
    }
    
    public void addMember(UUID player) {
        if (!members.contains(player)) {
            members.add(player);
        }
    }
    
    public void removeMember(UUID player) {
        members.remove(player);
        moderators.remove(player);
    }
    
    public void addModerator(UUID player) {
        if (members.contains(player) && !moderators.contains(player)) {
            moderators.add(player);
        }
    }
    
    public boolean isMember(UUID player) {
        return members.contains(player);
    }
    
    public boolean isModerator(UUID player) {
        return moderators.contains(player);
    }
    
    public boolean isLeader(UUID player) {
        return leader.equals(player);
    }
    
    public void addWin() {
        this.wins++;
    }
    
    public void addLoss() {
        this.losses++;
    }
    
    public double getWinrate() {
        int total = wins + losses;
        return total == 0 ? 0 : (double) wins / total * 100;
    }

    // Getters
    public UUID getClanId() { return clanId; }
    public String getName() { return name; }
    public String getTag() { return tag; }
    public UUID getLeader() { return leader; }
    public List<UUID> getMembers() { return members; }
    public List<UUID> getModerators() { return moderators; }
    public int getElo() { return elo; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public long getCreatedAt() { return createdAt; }
    public Map<String, Object> getSettings() { return settings; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setTag(String tag) { this.tag = tag; }
    public void setLeader(UUID leader) { this.leader = leader; }
    public void setMembers(List<UUID> members) { this.members = members; }
    public void setModerators(List<UUID> moderators) { this.moderators = moderators; }
    public void setElo(int elo) { this.elo = elo; }
    public void setWins(int wins) { this.wins = wins; }
    public void setLosses(int losses) { this.losses = losses; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setSettings(Map<String, Object> settings) { this.settings = settings; }
}

