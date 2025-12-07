package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.DailyQuest;
import fr.louis.practice.models.PracticePlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class QuestManager {
    private final PracticeCore plugin;
    private final Map<UUID, List<DailyQuest>> playerQuests;
    private final Random random;
    
    public QuestManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.playerQuests = new ConcurrentHashMap<>();
        this.random = new Random();
        
        // Daily reset task - 24h
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::resetDailyQuests, 0L, 20L * 60 * 60 * 24);
    }
    
    public void generateDailyQuests(UUID player) {
        List<DailyQuest> quests = new ArrayList<>();
        long expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000); // 24h
        
        // 3 quêtes journalières
        quests.add(createRandomQuest(expiresAt));
        quests.add(createRandomQuest(expiresAt));
        quests.add(createRandomQuest(expiresAt));
        
        playerQuests.put(player, quests);
    }
    
    private DailyQuest createRandomQuest(long expiresAt) {
        DailyQuest.QuestType type = DailyQuest.QuestType.values()[random.nextInt(DailyQuest.QuestType.values().length)];
        String kit = getRandomKit();
        
        int targetAmount;
        int reward;
        
        switch (type) {
            case WIN_MATCHES -> {
                targetAmount = 5 + random.nextInt(6); // 5-10
                reward = targetAmount * 50;
            }
            case KILL_PLAYERS -> {
                targetAmount = 10 + random.nextInt(11); // 10-20
                reward = targetAmount * 25;
            }
            case WIN_STREAK -> {
                targetAmount = 3 + random.nextInt(3); // 3-5
                reward = targetAmount * 100;
            }
            case PLAY_MATCHES -> {
                targetAmount = 10 + random.nextInt(11); // 10-20
                reward = targetAmount * 20;
            }
            case COMBO_ACHIEVEMENT -> {
                targetAmount = 10 + random.nextInt(11); // 10-20
                reward = targetAmount * 30;
            }
            default -> {
                targetAmount = 5;
                reward = 100;
            }
        }
        
        return new DailyQuest(UUID.randomUUID(), type, kit, targetAmount, 0, reward, expiresAt);
    }
    
    private String getRandomKit() {
        List<String> kits = new ArrayList<>(plugin.getKitManager().getKits().keySet());
        return random.nextBoolean() ? null : kits.get(random.nextInt(kits.size())); // 50% chance d'être pour n'importe quel kit
    }
    
    public void updateQuestProgress(UUID player, DailyQuest.QuestType type, String kit, int amount) {
        List<DailyQuest> quests = playerQuests.get(player);
        if (quests == null) return;
        
        for (DailyQuest quest : quests) {
            if (quest.isCompleted() || quest.isExpired()) continue;
            if (quest.getType() != type) continue;
            if (quest.getKit() != null && !quest.getKit().equals(kit)) continue;
            
            int oldProgress = quest.getCurrentProgress();
            quest.incrementProgress(amount);
            
            Player bukPlayer = Bukkit.getPlayer(player);
            if (bukPlayer != null && bukPlayer.isOnline()) {
                if (quest.isCompleted() && oldProgress < quest.getTargetAmount()) {
                    // Quest completed!
                    bukPlayer.sendMessage(ChatColor.GREEN + "✓ Quête complétée: " + ChatColor.YELLOW + quest.getFormattedDescription());
                    bukPlayer.sendMessage(ChatColor.GOLD + "Récompense: " + quest.getRewardCoins() + " coins");
                    
                    // Give reward
                    PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
                    if (practicePlayer != null) {
                        practicePlayer.setCoins(practicePlayer.getCoins() + quest.getRewardCoins());
                    }
                } else if (quest.getCurrentProgress() % 5 == 0) { // Progress notification every 5
                    bukPlayer.sendMessage(ChatColor.GRAY + "Progression quête: " + ChatColor.YELLOW + 
                        quest.getCurrentProgress() + "/" + quest.getTargetAmount() + " " + 
                        ChatColor.GREEN + "(" + String.format("%.1f", quest.getProgressPercentage()) + "%)");
                }
            }
        }
    }
    
    public List<DailyQuest> getPlayerQuests(UUID player) {
        playerQuests.putIfAbsent(player, new ArrayList<>());
        
        List<DailyQuest> quests = playerQuests.get(player);
        if (quests.isEmpty() || quests.stream().allMatch(DailyQuest::isExpired)) {
            generateDailyQuests(player);
            quests = playerQuests.get(player);
        }
        
        return quests;
    }
    
    private void resetDailyQuests() {
        @SuppressWarnings("unused")
        long now = System.currentTimeMillis();
        
        for (Map.Entry<UUID, List<DailyQuest>> entry : playerQuests.entrySet()) {
            entry.getValue().removeIf(quest -> quest.isExpired());
        }
    }
    
    public int getTotalCompletedQuests(UUID player) {
        List<DailyQuest> quests = playerQuests.get(player);
        if (quests == null) return 0;
        
        return (int) quests.stream().filter(DailyQuest::isCompleted).count();
    }
}
