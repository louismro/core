package fr.louis.practice.models;


public class MatchPlayerData {
    private int hits;
    private int longestCombo;
    private int potionsUsed;
    private int potionsThrown;
    private int potionsMissed;
    private double damageDealt;
    private double damageTaken;
    private int arrowsShot;
    private int arrowsHit;
    
    public MatchPlayerData() {
        this.hits = 0;
        this.longestCombo = 0;
        this.potionsUsed = 0;
        this.potionsThrown = 0;
        this.potionsMissed = 0;
        this.damageDealt = 0;
        this.damageTaken = 0;
        this.arrowsShot = 0;
        this.arrowsHit = 0;
    }
    
    public void addHit() {
        this.hits++;
    }
    
    public void updateCombo(int combo) {
        if (combo > longestCombo) {
            this.longestCombo = combo;
        }
    }
    
    public void addPotionUsed() {
        this.potionsUsed++;
    }
    
    public void addPotionThrown() {
        this.potionsThrown++;
    }
    
    public void addPotionMissed() {
        this.potionsMissed++;
    }
    
    public void addDamageDealt(double damage) {
        this.damageDealt += damage;
    }
    
    public void addDamageTaken(double damage) {
        this.damageTaken += damage;
    }
    
    public void addArrowShot() {
        this.arrowsShot++;
    }
    
    public void addArrowHit() {
        this.arrowsHit++;
    }
    
    public double getPotionAccuracy() {
        if (potionsThrown == 0) return 0;
        return ((double) (potionsThrown - potionsMissed) / potionsThrown) * 100;
    }
    
    public double getArrowAccuracy() {
        if (arrowsShot == 0) return 0;
        return ((double) arrowsHit / arrowsShot) * 100;
    }

    // Getters
    public int getHits() { return hits; }
    public int getLongestCombo() { return longestCombo; }
    public int getPotionsUsed() { return potionsUsed; }
    public int getPotionsThrown() { return potionsThrown; }
    public int getPotionsMissed() { return potionsMissed; }
    public double getDamageDealt() { return damageDealt; }
    public double getDamageTaken() { return damageTaken; }
    public int getArrowsShot() { return arrowsShot; }
    public int getArrowsHit() { return arrowsHit; }

    // Setters
    public void setHits(int hits) { this.hits = hits; }
    public void setLongestCombo(int longestCombo) { this.longestCombo = longestCombo; }
    public void setPotionsUsed(int potionsUsed) { this.potionsUsed = potionsUsed; }
    public void setPotionsThrown(int potionsThrown) { this.potionsThrown = potionsThrown; }
    public void setPotionsMissed(int potionsMissed) { this.potionsMissed = potionsMissed; }
    public void setDamageDealt(double damageDealt) { this.damageDealt = damageDealt; }
    public void setDamageTaken(double damageTaken) { this.damageTaken = damageTaken; }
    public void setArrowsShot(int arrowsShot) { this.arrowsShot = arrowsShot; }
    public void setArrowsHit(int arrowsHit) { this.arrowsHit = arrowsHit; }
}

