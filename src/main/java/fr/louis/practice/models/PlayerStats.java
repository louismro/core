package fr.louis.practice.models;


public class PlayerStats {
    private int kills;
    private int deaths;
    private int wins;
    private int losses;
    private int matchesPlayed;
    private int highestStreak;
    
    public PlayerStats() {
        this.kills = 0;
        this.deaths = 0;
        this.wins = 0;
        this.losses = 0;
        this.matchesPlayed = 0;
        this.highestStreak = 0;
    }
    
    public double getKDRatio() {
        return deaths == 0 ? kills : (double) kills / deaths;
    }
    
    public double getWinRate() {
        int total = wins + losses;
        return total == 0 ? 0 : (double) wins / total * 100;
    }
    
    public void addKill() {
        this.kills++;
    }
    
    public void addDeath() {
        this.deaths++;
    }
    
    public void addWin() {
        this.wins++;
        this.matchesPlayed++;
    }
    
    public void addLoss() {
        this.losses++;
        this.matchesPlayed++;
    }
    
    public void updateStreak(int streak) {
        if (streak > highestStreak) {
            this.highestStreak = streak;
        }
    }

    // Getters
    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getMatchesPlayed() { return matchesPlayed; }
    public int getHighestStreak() { return highestStreak; }

    // Setters
    public void setKills(int kills) { this.kills = kills; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public void setWins(int wins) { this.wins = wins; }
    public void setLosses(int losses) { this.losses = losses; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }
    public void setHighestStreak(int highestStreak) { this.highestStreak = highestStreak; }
}

