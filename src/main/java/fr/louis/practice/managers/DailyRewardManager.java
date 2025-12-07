package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.DailyReward;
import fr.louis.practice.models.PracticePlayer;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DailyRewardManager {
    private final PracticeCore plugin;
    
    // Dernier claim par joueur
    private final Map<UUID, LocalDate> lastClaim = new ConcurrentHashMap<>();
    
    // Streak actuelle par joueur (jours consécutifs)
    private final Map<UUID, Integer> streaks = new ConcurrentHashMap<>();
    
    // Historique des récompenses
    @Getter
    private final Map<UUID, List<DailyReward>> rewardHistory = new ConcurrentHashMap<>();
    
    // Configuration des récompenses par jour
    private final int[] dailyCoins = {100, 150, 200, 300, 400, 600, 1000}; // Jour 1-7
    
    public DailyRewardManager(PracticeCore plugin) {
        this.plugin = plugin;
        startDailyNotificationTask();
    }
    
    public boolean canClaim(UUID playerId) {
        LocalDate last = lastClaim.get(playerId);
        if (last == null) return true;
        
        LocalDate today = LocalDate.now();
        return last.isBefore(today);
    }
    
    public DailyReward claimReward(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (!canClaim(playerId)) {
            player.sendMessage(ChatColor.RED + "Vous avez déjà réclamé votre récompense aujourd'hui!");
            player.sendMessage(ChatColor.YELLOW + "Revenez demain pour continuer votre série!");
            return null;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate last = lastClaim.get(playerId);
        
        int currentStreak = streaks.getOrDefault(playerId, 0);
        
        // Vérifier si la streak continue
        if (last != null) {
            long daysBetween = ChronoUnit.DAYS.between(last, today);
            
            if (daysBetween == 1) {
                // Streak continue
                currentStreak++;
            } else if (daysBetween > 1) {
                // Streak cassée
                currentStreak = 1;
                player.sendMessage(ChatColor.RED + "⚠ Votre série a été cassée!");
            }
        } else {
            // Première fois
            currentStreak = 1;
        }
        
        // Cycle de 7 jours
        int rewardDay = ((currentStreak - 1) % 7) + 1;
        int coins = dailyCoins[rewardDay - 1];
        
        // Bonus streak si > 7 jours
        int streakBonus = (currentStreak / 7) * 50;
        coins += streakBonus;
        
        // Créer la récompense
        DailyReward reward = new DailyReward(today, rewardDay, coins);
        
        // Bonus spéciaux certains jours
        if (rewardDay == 7) {
            reward.addBonusItem("cosmetic_random", 1);
        }
        
        // Donner les coins
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(playerId);
        if (practicePlayer != null) {
            practicePlayer.addCoins(coins);
        }
        
        // Sauvegarder
        lastClaim.put(playerId, today);
        streaks.put(playerId, currentStreak);
        
        rewardHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(reward);
        
        // Message et effets
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        player.sendMessage(ChatColor.YELLOW + "    ✦ RÉCOMPENSE QUOTIDIENNE ✦");
        player.sendMessage("");
        player.sendMessage(ChatColor.WHITE + "  Jour: " + ChatColor.GREEN + rewardDay + "/7");
        player.sendMessage(ChatColor.WHITE + "  Série: " + ChatColor.AQUA + currentStreak + " jours");
        player.sendMessage(ChatColor.WHITE + "  Coins: " + ChatColor.GOLD + "+" + coins);
        
        if (streakBonus > 0) {
            player.sendMessage(ChatColor.GRAY + "  (bonus série: +" + streakBonus + ")");
        }
        
        if (rewardDay == 7) {
            player.sendMessage("");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "  ★ BONUS: Cosmétique aléatoire!");
        }
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        player.sendMessage("");
        
        // Effets sonores
        var location = player.getLocation();
        if (location != null) {
            player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        }
        
        if (rewardDay == 7) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                var loc = player.getLocation();
                if (loc != null) {
                    player.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
                }
            }, 5L);
        }
        
        // Broadcast pour grosses séries
        if (currentStreak % 30 == 0) {
            plugin.getServer().broadcastMessage(
                ChatColor.GOLD + "⚡ " + ChatColor.YELLOW + player.getName() + 
                ChatColor.WHITE + " a atteint une série de " + 
                ChatColor.AQUA + currentStreak + " jours" + 
                ChatColor.GOLD + " ⚡"
            );
        }
        
        return reward;
    }
    
    public int getStreak(UUID playerId) {
        return streaks.getOrDefault(playerId, 0);
    }
    
    public LocalDate getLastClaim(UUID playerId) {
        return lastClaim.get(playerId);
    }
    
    public int getNextRewardCoins(UUID playerId) {
        int currentStreak = streaks.getOrDefault(playerId, 0);
        int nextDay = ((currentStreak) % 7) + 1;
        int coins = dailyCoins[nextDay - 1];
        
        // Bonus streak
        int streakBonus = ((currentStreak + 1) / 7) * 50;
        return coins + streakBonus;
    }
    
    public String getStreakStatus(UUID playerId) {
        if (!canClaim(playerId)) {
            return ChatColor.GREEN + "✓ Réclamé aujourd'hui";
        }
        
        LocalDate last = lastClaim.get(playerId);
        if (last == null) {
            return ChatColor.YELLOW + "Disponible!";
        }
        
        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(last, today);
        
        if (daysBetween == 1) {
            return ChatColor.YELLOW + "Disponible! (série continue)";
        } else if (daysBetween > 1) {
            return ChatColor.RED + "Disponible (série cassée)";
        }
        
        return ChatColor.GRAY + "Déjà réclamé";
    }
    
    private void startDailyNotificationTask() {
        // Vérifier toutes les heures si des joueurs peuvent claim
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (canClaim(player.getUniqueId())) {
                    // Notification discrète
                    player.sendMessage(ChatColor.GOLD + "✦ " + 
                        ChatColor.YELLOW + "Votre récompense quotidienne est disponible! " +
                        ChatColor.GRAY + "(/daily)");
                }
            }
        }, 20L * 60 * 60, 20L * 60 * 60); // Toutes les heures
    }
    
    public List<String> getRewardPreview(UUID playerId) {
        List<String> preview = new ArrayList<>();
        int currentStreak = streaks.getOrDefault(playerId, 0);
        int startDay = ((currentStreak) % 7) + 1;
        
        preview.add(ChatColor.GOLD + "Récompenses à venir:");
        preview.add("");
        
        for (int i = 0; i < 7; i++) {
            int day = ((startDay + i - 1) % 7) + 1;
            int coins = dailyCoins[day - 1];
            
            String line = ChatColor.YELLOW + "Jour " + day + ": " + 
                ChatColor.GOLD + coins + " coins";
            
            if (day == 7) {
                line += ChatColor.LIGHT_PURPLE + " + Cosmétique";
            }
            
            if (i == 0) {
                line += ChatColor.GREEN + " ← Prochaine";
            }
            
            preview.add(line);
        }
        
        return preview;
    }
    
    // Sauvegarde/Chargement
    public void savePlayerData(UUID playerId) {
        // MongoDB persistence will be implemented in a future update
        // lastClaim, streaks, rewardHistory
    }
    
    public void loadPlayerData(UUID playerId) {
        // MongoDB data loading will be implemented in a future update
    }
}
