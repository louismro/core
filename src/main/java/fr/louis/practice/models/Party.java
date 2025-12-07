package fr.louis.practice.models;

import java.util.*;

public class Party {
    private final UUID partyId;
    private final UUID leader;
    private final List<UUID> members;
    private final Set<UUID> invited;
    
    private int maxSize;
    private boolean open;
    
    public Party(UUID leader, int maxSize) {
        this.partyId = UUID.randomUUID();
        this.leader = leader;
        this.members = new ArrayList<>();
        this.invited = new HashSet<>();
        this.maxSize = maxSize;
        this.open = false;
        
        members.add(leader);
    }
    
    public void addMember(UUID player) {
        if (members.size() < maxSize) {
            members.add(player);
            invited.remove(player);
        }
    }
    
    public void removeMember(UUID player) {
        members.remove(player);
    }
    
    public void invite(UUID player) {
        invited.add(player);
    }
    
    public void uninvite(UUID player) {
        invited.remove(player);
    }
    
    public boolean isLeader(UUID player) {
        return leader.equals(player);
    }
    
    public boolean isMember(UUID player) {
        return members.contains(player);
    }
    
    public boolean isInvited(UUID player) {
        return invited.contains(player);
    }
    
    public void broadcast(String message) {
        // Placeholder - will be implemented with Bukkit messaging
    }
    
    public boolean isFull() {
        return members.size() >= maxSize;
    }
    
    public int getSize() {
        return members.size();
    }
    
    public List<UUID> getMembers() {
        return new ArrayList<>(members);
    }
    
    // Getters
    public UUID getPartyId() { return partyId; }
    public UUID getLeader() { return leader; }
    public Set<UUID> getInvited() { return invited; }
    public int getMaxSize() { return maxSize; }
    public boolean isOpen() { return open; }
    
    // Setters
    public void setMaxSize(int maxSize) { this.maxSize = maxSize; }
    public void setOpen(boolean open) { this.open = open; }
}
