package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PracticePlayer;
import fr.louis.practice.models.SeasonalRank;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SeasonManager {
    private final PracticeCore plugin;
    
    @Getter
    private final List<SeasonalRank> ranks = new ArrayList<>();
    
    // Saison actuelle
    @Getter
    private int currentSeason = 1;
    
    @Getter
    private LocalDate seasonStartDate;
    
    @Getter
    private LocalDate seasonEndDate;
    
    // Dernière activité ranked par joueur (pour decay)
    private final Map<UUID, LocalDateTime> lastRankedActivity = new ConcurrentHashMap<>();
    
    // Elo au début de la saison (pour calculer le gain)
    private final Map<UUID, Integer> seasonStartElo = new ConcurrentHashMap<>();
    
    // Peak ELO de la saison
    private final Map<UUID, Integer> seasonPeakElo = new ConcurrentHashMap<>();
    
    // Configuration
    private static final int DECAY_DAYS_THRESHOLD = 14; // Jours avant decay
    private static final int DECAY_AMOUNT = 10; // ELO perdu par jour de decay
    private static final int SEASON_DURATION_DAYS = 90; // 3 mois
    
    public SeasonManager(PracticeCore plugin) {
        this.plugin = plugin;
        loadSeasonData();
        initializeRanks();
        startDecayTask();
        startSeasonCheckTask();
    }
    
    private void initializeRanks() {
        // Rangs basés sur Kohi/Minemen
        ranks.add(new SeasonalRank("unranked", "Unranked", "U", ChatColor.GRAY, 0, 999, 0));
        ranks.add(new SeasonalRank("bronze", "Bronze", "B", ChatColor.RED, 1000, 1199, 100));
        ranks.add(new SeasonalRank("silver", "Silver", "S", ChatColor.WHITE, 1200, 1399, 250));
        ranks.add(new SeasonalRank("gold", "Gold", "G", ChatColor.GOLD, 1400, 1599, 500));
        ranks.add(new SeasonalRank("platinum", "Platinum", "P", ChatColor.AQUA, 1600, 1799, 1000));
        ranks.add(new SeasonalRank("diamond", "Diamond", "D", ChatColor.BLUE, 1800, 1999, 2000));
        ranks.add(new SeasonalRank("master", "Master", "M", ChatColor.DARK_PURPLE, 2000, 2299, 3500));
        ranks.add(new SeasonalRank("grandmaster", "Grand Master", "GM", ChatColor.LIGHT_PURPLE, 2300, 2599, 5000));
        ranks.add(new SeasonalRank("champion", "Champion", "C", ChatColor.YELLOW, 2600, -1, 10000));
    }
    
    private void loadSeasonData() {
        // Configuration loading will be implemented in a future update
        // Pour l'instant, hardcoded
        currentSeason = plugin.getConfig().getInt("season.current", 1);
        
        String startDateStr = plugin.getConfig().getString("season.start-date");
        if (startDateStr != null) {
            seasonStartDate = LocalDate.parse(startDateStr);
        } else {
            seasonStartDate = LocalDate.now();
            plugin.getConfig().set("season.start-date", seasonStartDate.toString());
            plugin.saveConfig();
        }
        
        seasonEndDate = seasonStartDate.plusDays(SEASON_DURATION_DAYS);
    }
    
    public SeasonalRank getRank(int elo) {
        for (SeasonalRank rank : ranks) {
            if (rank.isInRange(elo)) {
                return rank;
            }
        }
        return ranks.get(0); // Unranked fallback
    }
    
    public SeasonalRank getPlayerRank(UUID playerId) {
        PracticePlayer player = plugin.getPlayerManager().getPlayer(playerId);
        if (player == null) return ranks.get(0);
        
        int elo = player.getGlobalElo();
        return getRank(elo);
    }
    
    public void updateActivity(UUID playerId) {
        lastRankedActivity.put(playerId, LocalDateTime.now());
    }
    
    public void updateSeasonStats(Player player, int newElo) {
        UUID playerId = player.getUniqueId();
        
        // Initialiser season start elo si première game
        seasonStartElo.putIfAbsent(playerId, 1000);
        
        // Update peak ELO
        int currentPeak = seasonPeakElo.getOrDefault(playerId, newElo);
        if (newElo > currentPeak) {
            seasonPeakElo.put(playerId, newElo);
            
            // Check si nouveau rank atteint
            SeasonalRank oldRank = getRank(currentPeak);
            SeasonalRank newRank = getRank(newElo);
            
            if (!oldRank.getId().equals(newRank.getId())) {
                announceRankUp(player, newRank);
            }
        }
        
        updateActivity(playerId);
    }
    
    private void announceRankUp(Player player, SeasonalRank rank) {
        // Message au joueur
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "★ RANK UP! ★" + ChatColor.GOLD + "             ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.WHITE + "Nouveau rang: " + rank.getDisplayName());
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.GRAY + rank.getFormattedPrefix());
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
        
        // Broadcast
        plugin.getServer().broadcastMessage(
            ChatColor.GOLD + "⚡ " + ChatColor.YELLOW + player.getName() + 
            ChatColor.WHITE + " a atteint le rang " + rank.getDisplayName() + 
            ChatColor.GOLD + " ⚡"
        );
        
        // Achievement check
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (practicePlayer != null) {
            plugin.getAchievementManager().checkEloAchievements(player, practicePlayer.getGlobalElo());
        }
    }
    
    private void startDecayTask() {
        // Vérifier le decay toutes les heures
        new BukkitRunnable() {
            @Override
            public void run() {
                LocalDateTime now = LocalDateTime.now();
                
                for (Map.Entry<UUID, LocalDateTime> entry : lastRankedActivity.entrySet()) {
                    UUID playerId = entry.getKey();
                    LocalDateTime lastActivity = entry.getValue();
                    
                    long daysInactive = ChronoUnit.DAYS.between(lastActivity, now);
                    
                    if (daysInactive >= DECAY_DAYS_THRESHOLD) {
                        applyDecay(playerId, daysInactive);
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L * 60 * 60, 20L * 60 * 60); // Toutes les heures
    }
    
    private void applyDecay(UUID playerId, long daysInactive) {
        PracticePlayer player = plugin.getPlayerManager().getPlayer(playerId);
        if (player == null) return;
        
        int currentElo = player.getGlobalElo();
        if (currentElo <= 1000) return; // Pas de decay sous 1000
        
        // Calculer decay
        long decayDays = daysInactive - DECAY_DAYS_THRESHOLD;
        int totalDecay = (int) (decayDays * DECAY_AMOUNT);
        
        // Appliquer max 50 ELO par cycle de decay
        int actualDecay = Math.min(totalDecay, 50);
        
        int newElo = Math.max(1000, currentElo - actualDecay);
        player.setGlobalElo(newElo);
        
        // Notifier si en ligne
        Player onlinePlayer = plugin.getServer().getPlayer(playerId);
        if (onlinePlayer != null) {
            onlinePlayer.sendMessage(ChatColor.RED + "⚠ Votre ELO a été réduit de " + 
                actualDecay + " pour inactivité");
            onlinePlayer.sendMessage(ChatColor.YELLOW + "Jouez des matchs ranked pour l'éviter!");
        }
        
        // Reset last activity
        lastRankedActivity.put(playerId, LocalDateTime.now());
    }
    
    private void startSeasonCheckTask() {
        // Vérifier si la saison est terminée tous les jours
        new BukkitRunnable() {
            @Override
            public void run() {
                if (LocalDate.now().isAfter(seasonEndDate) || 
                    LocalDate.now().isEqual(seasonEndDate)) {
                    endSeason();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L * 60 * 60 * 24, 20L * 60 * 60 * 24); // Tous les jours
    }
    
    private void endSeason() {
        plugin.getLogger().info(String.format("Season %d is ending...", currentSeason));
        
        // Broadcast
        plugin.getServer().broadcastMessage("");
        plugin.getServer().broadcastMessage(ChatColor.GOLD + "═══════════════════════════════");
        plugin.getServer().broadcastMessage(ChatColor.YELLOW + "   ⚡ FIN DE LA SAISON " + currentSeason + " ⚡");
        plugin.getServer().broadcastMessage(ChatColor.GOLD + "═══════════════════════════════");
        plugin.getServer().broadcastMessage("");
        
        // Récompenser les joueurs en ligne
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            rewardPlayer(player);
        }
        
        // Nouvelle saison
        currentSeason++;
        seasonStartDate = LocalDate.now();
        seasonEndDate = seasonStartDate.plusDays(SEASON_DURATION_DAYS);
        
        // Save
        plugin.getConfig().set("season.current", currentSeason);
        plugin.getConfig().set("season.start-date", seasonStartDate.toString());
        plugin.saveConfig();
        
        // Reset
        seasonStartElo.clear();
        seasonPeakElo.clear();
        lastRankedActivity.clear();
        
        plugin.getServer().broadcastMessage("");
        plugin.getServer().broadcastMessage(ChatColor.GREEN + "La saison " + currentSeason + " commence maintenant!");
        plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Tous les ELOs ont été soft reset");
        plugin.getServer().broadcastMessage("");
    }
    
    private void rewardPlayer(Player player) {
        SeasonalRank rank = getPlayerRank(player.getUniqueId());
        int peakElo = seasonPeakElo.getOrDefault(player.getUniqueId(), 1000);
        int startElo = seasonStartElo.getOrDefault(player.getUniqueId(), 1000);
        int gain = peakElo - startElo;
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (practicePlayer == null) return;
        
        // Récompenses
        int coins = rank.getSeasonCoinsReward();
        if (gain > 200) coins += 500; // Bonus progression
        if (gain > 400) coins += 1000; // Bonus gros gain
        
        practicePlayer.addCoins(coins);
        
        // Message
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══ Récompenses de Saison ═══");
        player.sendMessage(ChatColor.YELLOW + "Rang final: " + rank.getDisplayName());
        player.sendMessage(ChatColor.YELLOW + "Peak ELO: " + ChatColor.WHITE + peakElo);
        player.sendMessage(ChatColor.YELLOW + "Gain total: " + ChatColor.GREEN + "+" + gain);
        player.sendMessage(ChatColor.YELLOW + "Coins gagnés: " + ChatColor.GOLD + "+" + coins);
        player.sendMessage("");
    }
    
    public int getDaysUntilSeasonEnd() {
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), seasonEndDate);
    }
    
    public int getPeakElo(UUID playerId) {
        return seasonPeakElo.getOrDefault(playerId, 1000);
    }
    
    public int getSeasonGain(UUID playerId) {
        int peak = getPeakElo(playerId);
        int start = seasonStartElo.getOrDefault(playerId, 1000);
        return peak - start;
    }
}
