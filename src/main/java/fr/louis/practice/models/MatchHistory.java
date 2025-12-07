package fr.louis.practice.models;


import java.util.UUID;

public class MatchHistory {
    private final UUID matchId;
    private final long timestamp;
    private final String kit;
    private final UUID opponent;
    private final String opponentName;
    private final boolean won;
    private final int eloChange;
    private final int duration; // en secondes
    private final MatchHistoryStats stats;
    
        public static class MatchHistoryStats {
        private final int kills;
        private final int hits;
        private final int maxCombo;
        private final int potionsUsed;
        private final int potionsThrown;
        private final int arrowsShot;
        private final int arrowsHit;
        private final double damageDealt;
        private final double damageTaken;

        public MatchHistoryStats(int kills, int hits, int maxCombo, int potionsUsed, 
                                int potionsThrown, int arrowsShot, int arrowsHit,
                                double damageDealt, double damageTaken) {
            this.kills = kills;
            this.hits = hits;
            this.maxCombo = maxCombo;
            this.potionsUsed = potionsUsed;
            this.potionsThrown = potionsThrown;
            this.arrowsShot = arrowsShot;
            this.arrowsHit = arrowsHit;
            this.damageDealt = damageDealt;
            this.damageTaken = damageTaken;
        }

        // Getters
        public int getKills() { return kills; }
        public int getHits() { return hits; }
        public int getMaxCombo() { return maxCombo; }
        public int getPotionsUsed() { return potionsUsed; }
        public int getPotionsThrown() { return potionsThrown; }
        public int getArrowsShot() { return arrowsShot; }
        public int getArrowsHit() { return arrowsHit; }
        public double getDamageDealt() { return damageDealt; }
        public double getDamageTaken() { return damageTaken; }
    }

    public MatchHistory(UUID matchId, long timestamp, String kit, UUID opponent, 
                       String opponentName, boolean won, int eloChange, int duration, 
                       MatchHistoryStats stats) {
        this.matchId = matchId;
        this.timestamp = timestamp;
        this.kit = kit;
        this.opponent = opponent;
        this.opponentName = opponentName;
        this.won = won;
        this.eloChange = eloChange;
        this.duration = duration;
        this.stats = stats;
    }

    // Getters
    public UUID getMatchId() { return matchId; }
    public long getTimestamp() { return timestamp; }
    public String getKit() { return kit; }
    public UUID getOpponent() { return opponent; }
    public String getOpponentName() { return opponentName; }
    public boolean isWon() { return won; }
    public int getEloChange() { return eloChange; }
    public int getDuration() { return duration; }
    public MatchHistoryStats getStats() { return stats; }
}

