package fr.louis.practice.models;


public class PlayerSettings {
    private boolean allowDuels;
    private boolean allowSpectators;
    private boolean showScoreboard;
    private boolean publicMatches;
    private boolean showPing;
    private boolean comboMessages;
    private boolean deathMessages;
    private boolean matchAnnouncements;
    
    public PlayerSettings() {
        this.allowDuels = true;
        this.allowSpectators = true;
        this.showScoreboard = true;
        this.publicMatches = true;
        this.showPing = true;
        this.comboMessages = true;
        this.deathMessages = true;
        this.matchAnnouncements = true;
    }

    // Getters
    public boolean isAllowDuels() { return allowDuels; }
    public boolean isAllowSpectators() { return allowSpectators; }
    public boolean isShowScoreboard() { return showScoreboard; }
    public boolean isPublicMatches() { return publicMatches; }
    public boolean isShowPing() { return showPing; }
    public boolean isComboMessages() { return comboMessages; }
    public boolean isDeathMessages() { return deathMessages; }
    public boolean isMatchAnnouncements() { return matchAnnouncements; }

    // Setters
    public void setAllowDuels(boolean allowDuels) { this.allowDuels = allowDuels; }
    public void setAllowSpectators(boolean allowSpectators) { this.allowSpectators = allowSpectators; }
    public void setShowScoreboard(boolean showScoreboard) { this.showScoreboard = showScoreboard; }
    public void setPublicMatches(boolean publicMatches) { this.publicMatches = publicMatches; }
    public void setShowPing(boolean showPing) { this.showPing = showPing; }
    public void setComboMessages(boolean comboMessages) { this.comboMessages = comboMessages; }
    public void setDeathMessages(boolean deathMessages) { this.deathMessages = deathMessages; }
    public void setMatchAnnouncements(boolean matchAnnouncements) { this.matchAnnouncements = matchAnnouncements; }
}

