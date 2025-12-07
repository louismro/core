package fr.louis.practice.models;


public class PartySettings {
    private boolean publicParty;
    private boolean allowJoinRequests;
    private boolean friendsOnly;
    private int maxMembers;
    private boolean mutedChat;
    private boolean autoKickInactive;
    
    public PartySettings() {
        this.publicParty = false;
        this.allowJoinRequests = true;
        this.friendsOnly = false;
        this.maxMembers = 10;
        this.mutedChat = false;
        this.autoKickInactive = false;
    }

    // Getters
    public boolean isPublicParty() { return publicParty; }
    public boolean isAllowJoinRequests() { return allowJoinRequests; }
    public boolean isFriendsOnly() { return friendsOnly; }
    public int getMaxMembers() { return maxMembers; }
    public boolean isMutedChat() { return mutedChat; }
    public boolean isAutoKickInactive() { return autoKickInactive; }

    // Setters
    public void setPublicParty(boolean publicParty) { this.publicParty = publicParty; }
    public void setAllowJoinRequests(boolean allowJoinRequests) { this.allowJoinRequests = allowJoinRequests; }
    public void setFriendsOnly(boolean friendsOnly) { this.friendsOnly = friendsOnly; }
    public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }
    public void setMutedChat(boolean mutedChat) { this.mutedChat = mutedChat; }
    public void setAutoKickInactive(boolean autoKickInactive) { this.autoKickInactive = autoKickInactive; }
}

