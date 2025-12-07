package fr.louis.practice.models;

import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PracticePlayer {
    private final UUID uuid;
    private final String name;
    
    // État du joueur
    private PlayerState state;
    private Match currentMatch;
    private Party party;
    private Queue queue;
    
    // Statistiques
    private Map<String, Integer> elo;
    private Map<String, PlayerStats> stats;
    
    // Combat
    private int combo;
    private long lastHit;
    private long lastPearl;
    private UUID lastDamager;
    private long combatTagged;
    
    // Préférences
    private PlayerSettings settings;
    private boolean allowDuels;
    private boolean allowSpectators;
    
    // Killstreak
    private int currentStreak;
    private int highestStreak;
    
    // Inventory
    private ItemStack[] inventory;
    private ItemStack[] armor;
    
    // Economy
    private int coins;
    private int totalCoinsEarned;
    
    public PracticePlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.state = PlayerState.SPAWN;
        this.coins = 0;
        this.totalCoinsEarned = 0;
        this.elo = new HashMap<>();
        this.stats = new HashMap<>();
        this.settings = new PlayerSettings();
        this.allowDuels = true;
        this.allowSpectators = true;
        this.combo = 0;
        this.currentStreak = 0;
        this.highestStreak = 0;
        
        // ELO par défaut pour chaque kit
        initializeElo();
    }
    
    private void initializeElo() {
        elo.put("NoDebuff", 1000);
        elo.put("Debuff", 1000);
        elo.put("BuildUHC", 1000);
        elo.put("Combo", 1000);
        elo.put("Sumo", 1000);
        elo.put("Archer", 1000);
    }
    
    public int getElo(String kit) {
        return elo.getOrDefault(kit, 1000);
    }
    
    public void setElo(String kit, int value) {
        elo.put(kit, Math.max(0, Math.min(3000, value)));
    }
    
    public void addElo(String kit, int amount) {
        setElo(kit, getElo(kit) + amount);
    }
    
    public int getWins() {
        return stats.values().stream().mapToInt(PlayerStats::getWins).sum();
    }
    
    public int getLosses() {
        return stats.values().stream().mapToInt(PlayerStats::getLosses).sum();
    }
    
    public double getWinLossRatio() {
        int losses = getLosses();
        if (losses == 0) return getWins();
        return (double) getWins() / losses;
    }
    
    public int getKillstreak() {
        return currentStreak;
    }
    
    public void addExperience(int amount) {
        // XP system placeholder
    }
    
    public void addXP(int amount) {
        addExperience(amount);
    }
    
    public void setGlobalElo(int elo) {
        // Set ELO for all kits
        for (String kit : this.elo.keySet()) {
            this.elo.put(kit, elo);
        }
    }
    
    public PlayerStats getStats(String kit) {
        return stats.computeIfAbsent(kit, k -> new PlayerStats());
    }
    
    public boolean isInMatch() {
        return currentMatch != null;
    }
    
    public boolean isInQueue() {
        return queue != null;
    }
    
    public boolean isInParty() {
        return party != null;
    }
    
    public boolean isCombatTagged() {
        return System.currentTimeMillis() - combatTagged < 15000;
    }
    
    public void resetCombat() {
        this.combo = 0;
        this.lastHit = 0;
        this.lastDamager = null;
        this.combatTagged = 0;
    }
    
    public void incrementCombo() {
        this.combo++;
        this.lastHit = System.currentTimeMillis();
    }
    
    public void resetCombo() {
        this.combo = 0;
    }
    
    public int getGlobalElo() {
        return elo.values().stream().mapToInt(Integer::intValue).sum() / elo.size();
    }
    
    public String getEloRank(int eloValue) {
        if (eloValue >= 2000) return "Champion";
        if (eloValue >= 1800) return "Master";
        if (eloValue >= 1600) return "Diamond";
        if (eloValue >= 1400) return "Platinum";
        if (eloValue >= 1200) return "Gold";
        if (eloValue >= 1000) return "Silver";
        return "Bronze";
    }
    
    public void addCoins(int amount) {
        this.coins += amount;
        this.totalCoinsEarned += amount;
    }
    
    public boolean removeCoins(int amount) {
        if (this.coins >= amount) {
            this.coins -= amount;
            return true;
        }
        return false;
    }
    
    public Map<String, Integer> getEloMap() {
        return elo;
    }
    
    public Map<String, PlayerStats> getStatsMap() {
        return stats;
    }
    
    // Getters
    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public PlayerState getState() { return state; }
    public Match getCurrentMatch() { return currentMatch; }
    public Party getParty() { return party; }
    public Queue getQueue() { return queue; }
    public Map<String, Integer> getElo() { return elo; }
    public Map<String, PlayerStats> getStats() { return stats; }
    public int getCombo() { return combo; }
    public long getLastHit() { return lastHit; }
    public long getLastPearl() { return lastPearl; }
    public UUID getLastDamager() { return lastDamager; }
    public long getCombatTagged() { return combatTagged; }
    public PlayerSettings getSettings() { return settings; }
    public boolean isAllowDuels() { return allowDuels; }
    public boolean isAllowSpectators() { return allowSpectators; }
    public int getCurrentStreak() { return currentStreak; }
    public int getHighestStreak() { return highestStreak; }
    public ItemStack[] getInventory() { return inventory; }
    public ItemStack[] getArmor() { return armor; }
    public int getCoins() { return coins; }
    public int getTotalCoinsEarned() { return totalCoinsEarned; }
    
    // Setters
    public void setState(PlayerState state) { this.state = state; }
    public void setCurrentMatch(Match currentMatch) { this.currentMatch = currentMatch; }
    public void setParty(Party party) { this.party = party; }
    public void setQueue(Queue queue) { this.queue = queue; }
    public void setElo(Map<String, Integer> elo) { this.elo = elo; }
    public void setStats(Map<String, PlayerStats> stats) { this.stats = stats; }
    public void setCombo(int combo) { this.combo = combo; }
    public void setLastHit(long lastHit) { this.lastHit = lastHit; }
    public void setLastPearl(long lastPearl) { this.lastPearl = lastPearl; }
    public void setLastDamager(UUID lastDamager) { this.lastDamager = lastDamager; }
    public void setCombatTagged(long combatTagged) { this.combatTagged = combatTagged; }
    public void setSettings(PlayerSettings settings) { this.settings = settings; }
    public void setAllowDuels(boolean allowDuels) { this.allowDuels = allowDuels; }
    public void setAllowSpectators(boolean allowSpectators) { this.allowSpectators = allowSpectators; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public void setHighestStreak(int highestStreak) { this.highestStreak = highestStreak; }
    public void setInventory(ItemStack[] inventory) { this.inventory = inventory; }
    public void setArmor(ItemStack[] armor) { this.armor = armor; }
    public void setCoins(int coins) { this.coins = coins; }
    public void setTotalCoinsEarned(int totalCoinsEarned) { this.totalCoinsEarned = totalCoinsEarned; }
}
