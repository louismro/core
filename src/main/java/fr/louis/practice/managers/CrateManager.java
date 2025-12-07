package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.CrateReward;
import fr.louis.practice.models.CrateReward.RewardType;
import fr.louis.practice.models.CrateReward.Rarity;
import fr.louis.practice.models.PracticePlayer;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CrateManager {
    private final PracticeCore plugin;
    
    @Getter
    private final Map<String, CrateReward> rewards = new HashMap<>();
    
    // Crates en inventaire par joueur
    private final Map<UUID, Map<Rarity, Integer>> playerCrates = new ConcurrentHashMap<>();
    
    // Animation en cours
    private final Set<UUID> openingCrate = ConcurrentHashMap.newKeySet();
    
    public CrateManager(PracticeCore plugin) {
        this.plugin = plugin;
        registerRewards();
    }
    
    private void registerRewards() {
        // ═══ COMMON ═══
        registerReward(new CrateReward("coins_100", "§f100 Coins", 
            RewardType.COINS, Rarity.COMMON.getBaseWeight(), 100));
        registerReward(new CrateReward("coins_250", "§f250 Coins", 
            RewardType.COINS, Rarity.COMMON.getBaseWeight() * 0.8, 250));
        
        // ═══ UNCOMMON ═══
        registerReward(new CrateReward("coins_500", "§a500 Coins", 
            RewardType.COINS, Rarity.UNCOMMON.getBaseWeight(), 500));
        registerReward(new CrateReward("boost_kills", "§aBoost Kills x2 (1h)", 
            RewardType.BOOST, Rarity.UNCOMMON.getBaseWeight(), 0));
        
        // ═══ RARE ═══
        registerReward(new CrateReward("coins_1000", "§91000 Coins", 
            RewardType.COINS, Rarity.RARE.getBaseWeight(), 1000));
        registerReward(new CrateReward("cosmetic_smoke", "§9Traînée Fumée", 
            RewardType.COSMETIC, Rarity.RARE.getBaseWeight(), 0));
        
        // ═══ EPIC ═══
        registerReward(new CrateReward("coins_2500", "§52500 Coins", 
            RewardType.COINS, Rarity.EPIC.getBaseWeight(), 2500));
        registerReward(new CrateReward("title_warrior", "§5Titre: Guerrier", 
            RewardType.TITLE, Rarity.EPIC.getBaseWeight(), 0));
        registerReward(new CrateReward("cosmetic_blood", "§5Blood Explosion", 
            RewardType.COSMETIC, Rarity.EPIC.getBaseWeight(), 0));
        
        // ═══ LEGENDARY ═══
        registerReward(new CrateReward("coins_5000", "§65000 Coins", 
            RewardType.COINS, Rarity.LEGENDARY.getBaseWeight(), 5000));
        registerReward(new CrateReward("title_legend", "§6Titre: Légende", 
            RewardType.TITLE, Rarity.LEGENDARY.getBaseWeight(), 0));
        registerReward(new CrateReward("cosmetic_lightning", "§6Lightning Strike", 
            RewardType.COSMETIC, Rarity.LEGENDARY.getBaseWeight(), 0));
    }
    
    private void registerReward(CrateReward reward) {
        rewards.put(reward.getId(), reward);
    }
    
    @SuppressWarnings("all")
    public void giveCrate(UUID playerId, Rarity rarity, int amount) {
        playerCrates.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
            .merge(rarity, amount, Integer::sum);
    }
    
    public int getCrateCount(UUID playerId, Rarity rarity) {
        Map<Rarity, Integer> crates = playerCrates.get(playerId);
        if (crates == null) return 0;
        return crates.getOrDefault(rarity, 0);
    }
    
    public boolean hasCrate(UUID playerId, Rarity rarity) {
        return getCrateCount(playerId, rarity) > 0;
    }
    
    public void openCrate(Player player, Rarity rarity) {
        UUID playerId = player.getUniqueId();
        
        if (openingCrate.contains(playerId)) {
            player.sendMessage(ChatColor.RED + "Vous ouvrez déjà une crate!");
            return;
        }
        
        if (!hasCrate(playerId, rarity)) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas de crate " + rarity.getDisplayName() + "!");
            return;
        }
        
        // Retirer la crate
        @SuppressWarnings("all")
        Map<Rarity, Integer> crates = playerCrates.get(playerId);
        crates.merge(rarity, -1, (a, b) -> Math.max(0, a + b));
        crates.remove(rarity, 0);
        
        openingCrate.add(playerId);
        
        // Animation d'ouverture
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "Ouverture de crate..." + ChatColor.GOLD + "    ║");
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
        
        // Sélectionner récompense
        CrateReward reward = selectRandomReward(rarity);
        
        // Animation avec suspense
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks < 40) {
                    // Sons de suspense
                    if (ticks % 5 == 0) {
                        var location = player.getLocation();
                        if (location != null) {
                            player.playSound(location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f + (ticks * 0.05f));
                        }
                    }
                    ticks++;
                } else {
                    // Révéler la récompense
                    revealReward(player, reward, rarity);
                    openingCrate.remove(playerId);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private CrateReward selectRandomReward(Rarity targetRarity) {
        List<CrateReward> possibleRewards = new ArrayList<>();
        double totalWeight = 0.0;
        
        for (CrateReward reward : rewards.values()) {
            // Filtrer par rareté approximative
            if (isRewardForRarity(reward, targetRarity)) {
                possibleRewards.add(reward);
                totalWeight += reward.getWeight();
            }
        }
        
        // Sélection weighted random
        double random = Math.random() * totalWeight;
        double currentWeight = 0.0;
        
        for (CrateReward reward : possibleRewards) {
            currentWeight += reward.getWeight();
            if (random <= currentWeight) {
                return reward;
            }
        }
        
        return possibleRewards.get(0);
    }
    
    private boolean isRewardForRarity(CrateReward reward, Rarity rarity) {
        double minWeight = 0;
        double maxWeight = 100;
        
        switch (rarity) {
            case COMMON -> {
                minWeight = 50;
                maxWeight = 100;
            }
            case UNCOMMON -> {
                minWeight = 15;
                maxWeight = 50;
            }
            case RARE -> {
                minWeight = 5;
                maxWeight = 20;
            }
            case EPIC -> {
                minWeight = 1.5;
                maxWeight = 7;
            }
            case LEGENDARY -> {
                minWeight = 0;
                maxWeight = 2;
            }
        }
        
        return reward.getWeight() >= minWeight && reward.getWeight() <= maxWeight;
    }
    
    private void revealReward(Player player, CrateReward reward, Rarity rarity) {
        // Appliquer la récompense
        applyReward(player, reward);
        
        // Message visuel
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "★ RÉCOMPENSE! ★" + ChatColor.GOLD + "         ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║   " + rarity.getDisplayName());
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.WHITE + reward.getName());
        
        if (reward.getValue() > 0) {
            player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.GRAY + 
                getRewardValueText(reward));
        }
        
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
        
        // Effets sonores selon rareté
        var location = player.getLocation();
        if (location != null) {
            switch (rarity) {
                case LEGENDARY -> player.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 2.0f);
                case EPIC -> player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
                case RARE -> player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                default -> player.playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        }
    }
    
    private void applyReward(Player player, CrateReward reward) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        
        switch (reward.getType()) {
            case COINS -> {
                if (practicePlayer != null) {
                    practicePlayer.addCoins(reward.getValue());
                }
            }
            case COSMETIC -> {
                if (reward.getCosmeticId() != null) {
                    plugin.getShopManager().unlockItem(player.getUniqueId(), reward.getCosmeticId());
                }
            }
            case TITLE -> {
                // Title unlocking will be implemented later
            }
            case BOOST -> {
                // Boost application will be implemented later
            }
            case KIT_VOUCHER, SPECIAL -> {
                // Special rewards handling will be implemented later
            }
        }
    }
    
    private String getRewardValueText(CrateReward reward) {
        return switch (reward.getType()) {
            case COINS -> "+" + reward.getValue() + " coins";
            default -> "";
        };
    }
    
    public Map<Rarity, Integer> getPlayerCrates(UUID playerId) {
        return playerCrates.getOrDefault(playerId, new HashMap<>());
    }
}
