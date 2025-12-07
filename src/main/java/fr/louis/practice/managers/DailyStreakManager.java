package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.DailyStreak;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DailyStreakManager {
    private final PracticeCore plugin;
    private final Map<UUID, DailyStreak> streaks;
    
    public DailyStreakManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.streaks = new ConcurrentHashMap<>();
    }
    
    public DailyStreak getOrCreate(UUID playerId) {
        return streaks.computeIfAbsent(playerId, DailyStreak::new);
    }
    
    public boolean claimDaily(Player player) {
        DailyStreak streak = getOrCreate(player.getUniqueId());
        
        if (!streak.canClaim()) {
            long timeUntilNext = 24 * 60 * 60 * 1000 - (System.currentTimeMillis() - streak.getLastClaimDate());
            long hoursLeft = timeUntilNext / (60 * 60 * 1000);
            player.sendMessage("§cVous avez déjà réclamé votre récompense ! Revenez dans §e" + hoursLeft + "h");
            return false;
        }
        
        int oldStreak = streak.getCurrentStreak();
        streak.claim();
        int newStreak = streak.getCurrentStreak();
        
        // Calculate rewards
        int multiplier = streak.getRewardMultiplier();
        int baseCoins = 100;
        int baseXP = 50;
        
        int coins = baseCoins * multiplier;
        int xp = baseXP * multiplier;
        
        // Give rewards
        var practicePlayer = plugin.getPlayerManager().getOrCreate(player);
        practicePlayer.setCoins(practicePlayer.getCoins() + coins);
        practicePlayer.addExperience(xp);
        
        // Display reward
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§e§l  RÉCOMPENSE QUOTIDIENNE");
        player.sendMessage("");
        player.sendMessage(" §6+" + coins + " Coins");
        player.sendMessage(" §b+" + xp + " XP");
        player.sendMessage("");
        
        if (newStreak > oldStreak) {
            player.sendMessage(" §a🔥 Série: §e" + newStreak + " jours §7(x" + multiplier + ")");
            
            // Milestone rewards
            if (newStreak == 7) {
                player.sendMessage(" §6§l★ BONUS 7 JOURS: +500 Coins!");
                practicePlayer.setCoins(practicePlayer.getCoins() + 500);
            } else if (newStreak == 14) {
                player.sendMessage(" §6§l★ BONUS 14 JOURS: +1000 Coins + Crate Rare!");
                practicePlayer.setCoins(practicePlayer.getCoins() + 1000);
            } else if (newStreak == 30) {
                player.sendMessage(" §6§l★★ BONUS 30 JOURS: +2500 Coins + Crate Légendaire!");
                practicePlayer.setCoins(practicePlayer.getCoins() + 2500);
                Bukkit.broadcastMessage("§6§l★ §e" + player.getName() + " §7a atteint §e30 jours §7de connexion!");
            }
        } else {
            player.sendMessage(" §c⚠ Série perdue! Nouvelle série: §e1 jour");
        }
        
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return true;
    }
    
    public DailyStreak getStreak(UUID playerId) {
        return streaks.get(playerId);
    }
}
