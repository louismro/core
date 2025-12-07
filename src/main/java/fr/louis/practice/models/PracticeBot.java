package fr.louis.practice.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class PracticeBot {
    private final UUID botId;
    private final Player target; // Player fighting the bot
    private final BotDifficulty difficulty;
    private final Location spawnLocation;
    private final String kit;
    
    private BukkitTask aiTask;
    private int health;
    private int maxHealth;
    private long startTime;
    private int hits;
    private int misses;
    
    public enum BotDifficulty {
        EASY("§aFacile", 0.6, 10, 80, 1000),
        MEDIUM("§eMoyen", 0.8, 15, 60, 700),
        HARD("§cDifficile", 1.0, 20, 40, 500),
        EXPERT("§4Expert", 1.2, 25, 20, 300);
        
        private final String displayName;
        private final double damageMultiplier;
        private final int reactionSpeed; // Ticks to react
        private final int accuracy; // % chance to hit
        private final int abilityDelay; // Delay between special abilities (ms)
        
        BotDifficulty(String displayName, double damageMultiplier, int reactionSpeed, 
                      int accuracy, int abilityDelay) {
            this.displayName = displayName;
            this.damageMultiplier = damageMultiplier;
            this.reactionSpeed = reactionSpeed;
            this.accuracy = accuracy;
            this.abilityDelay = abilityDelay;
        }
        
        public String getDisplayName() { return displayName; }
        public double getDamageMultiplier() { return damageMultiplier; }
        public int getReactionSpeed() { return reactionSpeed; }
        public int getAccuracy() { return accuracy; }
        public int getAbilityDelay() { return abilityDelay; }
    }
    
    public PracticeBot(Player target, BotDifficulty difficulty, Location spawnLocation, String kit) {
        this.botId = UUID.randomUUID();
        this.target = target;
        this.difficulty = difficulty;
        this.spawnLocation = spawnLocation;
        this.kit = kit;
        this.health = 20;
        this.maxHealth = 20;
        this.startTime = System.currentTimeMillis();
        this.hits = 0;
        this.misses = 0;
    }
    
    public void incrementHits() {
        this.hits++;
    }
    
    public void incrementMisses() {
        this.misses++;
    }
    
    public double getAccuracyPercent() {
        int total = hits + misses;
        if (total == 0) return 0;
        return (hits * 100.0) / total;
    }
    
    public long getDurationSeconds() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }
    
    public boolean isDead() {
        return health <= 0;
    }
    
    public void damage(int amount) {
        this.health = Math.max(0, this.health - amount);
    }
    
    public void heal(int amount) {
        this.health = Math.min(maxHealth, this.health + amount);
    }

    // Getters
    public UUID getBotId() { return botId; }
    public Player getTarget() { return target; }
    public BotDifficulty getDifficulty() { return difficulty; }
    public Location getSpawnLocation() { return spawnLocation; }
    public String getKit() { return kit; }
    public BukkitTask getAiTask() { return aiTask; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public long getStartTime() { return startTime; }
    public int getHits() { return hits; }
    public int getMisses() { return misses; }

    // Setters
    public void setAiTask(BukkitTask aiTask) { this.aiTask = aiTask; }
    public void setHealth(int health) { this.health = health; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public void setHits(int hits) { this.hits = hits; }
    public void setMisses(int misses) { this.misses = misses; }
}

