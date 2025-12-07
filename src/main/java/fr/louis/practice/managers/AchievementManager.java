package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Achievement;
import fr.louis.practice.models.Achievement.AchievementCategory;
import fr.louis.practice.models.PracticePlayer;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AchievementManager {
    private final PracticeCore plugin;
    
    @Getter
    private final Map<String, Achievement> achievements = new HashMap<>();
    
    // Achievements complétés par joueur
    private final Map<UUID, Set<String>> completedAchievements = new ConcurrentHashMap<>();
    
    // Progression vers achievements (pour ceux qui nécessitent tracking)
    private final Map<UUID, Map<String, Integer>> achievementProgress = new ConcurrentHashMap<>();
    
    public AchievementManager(PracticeCore plugin) {
        this.plugin = plugin;
        registerAchievements();
    }
    
    private void registerAchievements() {
        // ═══ KILLS ═══
        register(new Achievement("first_blood", "§cPremier Sang", 
            "Obtenir votre première élimination", 
            AchievementCategory.KILLS, Material.IRON_SWORD, 1, 100));
        
        register(new Achievement("killer_10", "§cTueur Amateur", 
            "Obtenir 10 éliminations", 
            AchievementCategory.KILLS, Material.IRON_SWORD, 10, 250));
        
        register(new Achievement("killer_50", "§cTueur Confirmé", 
            "Obtenir 50 éliminations", 
            AchievementCategory.KILLS, Material.DIAMOND_SWORD, 50, 500));
        
        register(new Achievement("killer_100", "§cTueur Expert", 
            "Obtenir 100 éliminations", 
            AchievementCategory.KILLS, Material.DIAMOND_SWORD, 100, 1000));
        
        register(new Achievement("killer_500", "§cTueur Légendaire", 
            "Obtenir 500 éliminations", 
            AchievementCategory.KILLS, Material.DIAMOND_SWORD, 500, 2500, "blood_explosion"));
        
        register(new Achievement("killer_1000", "§4Massacreur", 
            "Obtenir 1000 éliminations", 
            AchievementCategory.KILLS, Material.DIAMOND_SWORD, 1000, 5000, "lightning_strike"));
        
        // ═══ WINS ═══
        register(new Achievement("first_win", "§aPremière Victoire", 
            "Gagner votre premier match", 
            AchievementCategory.WINS, Material.GOLD_INGOT, 1, 150));
        
        register(new Achievement("winner_10", "§aVainqueur Amateur", 
            "Gagner 10 matchs", 
            AchievementCategory.WINS, Material.GOLD_INGOT, 10, 300));
        
        register(new Achievement("winner_50", "§aVainqueur Confirmé", 
            "Gagner 50 matchs", 
            AchievementCategory.WINS, Material.GOLD_BLOCK, 50, 750));
        
        register(new Achievement("winner_100", "§aVainqueur Expert", 
            "Gagner 100 matchs", 
            AchievementCategory.WINS, Material.GOLD_BLOCK, 100, 1500));
        
        register(new Achievement("winner_500", "§6Champion", 
            "Gagner 500 matchs", 
            AchievementCategory.WINS, Material.GOLD_BLOCK, 500, 3000, "victory_firework"));
        
        // ═══ STREAKS ═══
        register(new Achievement("streak_3", "§eTriple Kill", 
            "Obtenir une série de 3 kills", 
            AchievementCategory.STREAKS, Material.BLAZE_ROD, 3, 200));
        
        register(new Achievement("streak_5", "§eRampage", 
            "Obtenir une série de 5 kills", 
            AchievementCategory.STREAKS, Material.BLAZE_ROD, 5, 400));
        
        register(new Achievement("streak_10", "§6Unstoppable", 
            "Obtenir une série de 10 kills", 
            AchievementCategory.STREAKS, Material.BLAZE_ROD, 10, 1000, "flame_vortex"));
        
        register(new Achievement("streak_15", "§4Godlike", 
            "Obtenir une série de 15 kills", 
            AchievementCategory.STREAKS, Material.BLAZE_ROD, 15, 2000, "enchant_trail"));
        
        // ═══ ELO ═══
        register(new Achievement("elo_1200", "§bRanked Débutant", 
            "Atteindre 1200 ELO", 
            AchievementCategory.ELO, Material.EMERALD, 1200, 500));
        
        register(new Achievement("elo_1400", "§bRanked Avancé", 
            "Atteindre 1400 ELO", 
            AchievementCategory.ELO, Material.EMERALD, 1400, 1000));
        
        register(new Achievement("elo_1600", "§bRanked Expert", 
            "Atteindre 1600 ELO", 
            AchievementCategory.ELO, Material.EMERALD_BLOCK, 1600, 2000));
        
        register(new Achievement("elo_1800", "§5Ranked Master", 
            "Atteindre 1800 ELO", 
            AchievementCategory.ELO, Material.EMERALD_BLOCK, 1800, 3000, "golden_shower"));
        
        // ═══ SPECIAL ═══
        register(new Achievement("daily_streak_7", "§eDédié", 
            "Série de connexion de 7 jours", 
            AchievementCategory.SPECIAL, Material.NETHER_STAR, 7, 500));
        
        register(new Achievement("daily_streak_30", "§6Assidu", 
            "Série de connexion de 30 jours", 
            AchievementCategory.SPECIAL, Material.NETHER_STAR, 30, 2000, "hearts_burst"));
        
        register(new Achievement("tournament_win", "§eTournoi Vainqueur", 
            "Gagner un tournoi", 
            AchievementCategory.SPECIAL, Material.NETHER_STAR, 1, 1500));
        
        register(new Achievement("ffa_winner", "§cDominateur FFA", 
            "Terminer 1er dans un FFA", 
            AchievementCategory.SPECIAL, Material.NETHER_STAR, 1, 1000));
        
        // ═══ SOCIAL ═══
        register(new Achievement("clan_create", "§bFondateur", 
            "Créer un clan", 
            AchievementCategory.SOCIAL, Material.PLAYER_HEAD, 1, 300));
        
        register(new Achievement("clan_top10", "§bClan Élite", 
            "Avoir un clan dans le top 10", 
            AchievementCategory.SOCIAL, Material.PLAYER_HEAD, 1, 1000));
    }
    
    private void register(Achievement achievement) {
        achievements.put(achievement.getId(), achievement);
    }
    
    public Achievement getAchievement(String id) {
        return achievements.get(id);
    }
    
    public List<Achievement> getAchievementsByCategory(AchievementCategory category) {
        List<Achievement> result = new ArrayList<>();
        for (Achievement achievement : achievements.values()) {
            if (achievement.getCategory() == category) {
                result.add(achievement);
            }
        }
        return result;
    }
    
    public boolean hasCompleted(UUID playerId, String achievementId) {
        Set<String> completed = completedAchievements.get(playerId);
        return completed != null && completed.contains(achievementId);
    }
    
    public void checkAndUnlock(Player player, String achievementId) {
        if (hasCompleted(player.getUniqueId(), achievementId)) return;
        
        Achievement achievement = getAchievement(achievementId);
        if (achievement == null) return;
        
        unlockAchievement(player, achievement);
    }
    
    public void checkProgress(Player player, String achievementId, int currentValue) {
        if (hasCompleted(player.getUniqueId(), achievementId)) return;
        
        Achievement achievement = getAchievement(achievementId);
        if (achievement == null) return;
        
        // Update progress
        Map<String, Integer> progress = achievementProgress.computeIfAbsent(
            player.getUniqueId(), k -> new ConcurrentHashMap<>());
        progress.put(achievementId, currentValue);
        
        // Check if completed
        if (currentValue >= achievement.getRequirement()) {
            unlockAchievement(player, achievement);
        }
    }
    
    private void unlockAchievement(Player player, Achievement achievement) {
        Set<String> completed = completedAchievements.computeIfAbsent(
            player.getUniqueId(), k -> ConcurrentHashMap.newKeySet());
        
        if (completed.contains(achievement.getId())) return;
        
        completed.add(achievement.getId());
        
        // Récompenses
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (practicePlayer != null) {
            practicePlayer.addCoins(achievement.getCoinsReward());
        }
        
        // Cosmetic reward
        if (achievement.getCosmeticReward() != null) {
            plugin.getShopManager().unlockItem(player.getUniqueId(), achievement.getCosmeticReward());
        }
        
        // Annonce au joueur
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.YELLOW + "✦ SUCCÈS DÉBLOQUÉ! ✦" + ChatColor.GOLD + "         ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + achievement.getName());
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + achievement.getDescription());
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.YELLOW + "Récompense: " + 
            ChatColor.GOLD + "+" + achievement.getCoinsReward() + " coins");
        
        if (achievement.getCosmeticReward() != null) {
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.LIGHT_PURPLE + 
                "+ Cosmétique débloqué!");
        }
        
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
        
        // Effets
        var location = player.getLocation();
        if (location != null) {
            player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
        }
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            var loc = player.getLocation();
            if (loc != null) {
                player.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
            }
        }, 10L);
        
        // Broadcast pour achievements spéciaux
        if (achievement.getCategory() == AchievementCategory.SPECIAL || 
            achievement.getRequirement() >= 1000) {
            plugin.getServer().broadcastMessage(
                ChatColor.GOLD + "⚡ " + ChatColor.YELLOW + player.getName() + 
                ChatColor.WHITE + " a débloqué " + achievement.getName() + 
                ChatColor.GOLD + " ⚡"
            );
        }
    }
    
    public Set<String> getCompletedAchievements(UUID playerId) {
        return completedAchievements.getOrDefault(playerId, Collections.emptySet());
    }
    
    public int getProgress(UUID playerId, String achievementId) {
        Map<String, Integer> progress = achievementProgress.get(playerId);
        if (progress == null) return 0;
        return progress.getOrDefault(achievementId, 0);
    }
    
    public double getCompletionPercentage(UUID playerId) {
        Set<String> completed = getCompletedAchievements(playerId);
        return (double) completed.size() / achievements.size() * 100.0;
    }
    
    // Auto-check methods (appelés par les managers)
    public void checkKillAchievements(Player player, int totalKills) {
        checkProgress(player, "first_blood", totalKills);
        checkProgress(player, "killer_10", totalKills);
        checkProgress(player, "killer_50", totalKills);
        checkProgress(player, "killer_100", totalKills);
        checkProgress(player, "killer_500", totalKills);
        checkProgress(player, "killer_1000", totalKills);
    }
    
    public void checkWinAchievements(Player player, int totalWins) {
        checkProgress(player, "first_win", totalWins);
        checkProgress(player, "winner_10", totalWins);
        checkProgress(player, "winner_50", totalWins);
        checkProgress(player, "winner_100", totalWins);
        checkProgress(player, "winner_500", totalWins);
    }
    
    public void checkStreakAchievements(Player player, int currentStreak) {
        checkProgress(player, "streak_3", currentStreak);
        checkProgress(player, "streak_5", currentStreak);
        checkProgress(player, "streak_10", currentStreak);
        checkProgress(player, "streak_15", currentStreak);
    }
    
    public void checkEloAchievements(Player player, int currentElo) {
        checkProgress(player, "elo_1200", currentElo);
        checkProgress(player, "elo_1400", currentElo);
        checkProgress(player, "elo_1600", currentElo);
        checkProgress(player, "elo_1800", currentElo);
    }
    
    public void checkDailyStreakAchievements(Player player, int dailyStreak) {
        checkProgress(player, "daily_streak_7", dailyStreak);
        checkProgress(player, "daily_streak_30", dailyStreak);
    }
}
