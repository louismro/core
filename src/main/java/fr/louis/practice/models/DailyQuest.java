package fr.louis.practice.models;


import java.util.UUID;

public class DailyQuest {
    private final UUID questId;
    private final QuestType type;
    private final String kit;
    private final int targetAmount;
    private int currentProgress;
    private final int rewardCoins;
    private final long expiresAt;
    
    public enum QuestType {
        WIN_MATCHES("Gagner %d matchs en %s", "win_matches"),
        KILL_PLAYERS("Tuer %d joueurs en %s", "kill_players"),
        WIN_STREAK("Atteindre un %d killstreak en %s", "win_streak"),
        PLAY_MATCHES("Jouer %d matchs en %s", "play_matches"),
        COMBO_ACHIEVEMENT("Faire un combo de %d hits en %s", "combo_achievement");
        
        private final String description;
        private final String id;
        
        QuestType(String description, String id) {
            this.description = description;
            this.id = id;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getId() {
            return id;
        }
    }
    
    public DailyQuest(UUID questId, QuestType type, String kit, int targetAmount, 
                     int currentProgress, int rewardCoins, long expiresAt) {
        this.questId = questId;
        this.type = type;
        this.kit = kit;
        this.targetAmount = targetAmount;
        this.currentProgress = currentProgress;
        this.rewardCoins = rewardCoins;
        this.expiresAt = expiresAt;
    }
    
    public boolean isCompleted() {
        return currentProgress >= targetAmount;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
    
    public void incrementProgress(int amount) {
        this.currentProgress = Math.min(currentProgress + amount, targetAmount);
    }
    
    public double getProgressPercentage() {
        return (double) currentProgress / targetAmount * 100;
    }
    
    public String getFormattedDescription() {
        return String.format(type.getDescription(), targetAmount, kit != null ? kit : "n'importe quel kit");
    }

    // Getters
    public UUID getQuestId() { return questId; }
    public QuestType getType() { return type; }
    public String getKit() { return kit; }
    public int getTargetAmount() { return targetAmount; }
    public int getCurrentProgress() { return currentProgress; }
    public int getRewardCoins() { return rewardCoins; }
    public long getExpiresAt() { return expiresAt; }

    // Setters
    public void setCurrentProgress(int currentProgress) { this.currentProgress = currentProgress; }
}

