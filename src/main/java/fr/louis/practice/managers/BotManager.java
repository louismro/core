package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PracticeBot;
import fr.louis.practice.models.PracticeBot.BotDifficulty;
import fr.louis.practice.models.PracticePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BotManager {
    private final PracticeCore plugin;
    private final Map<UUID, PracticeBot> activeBots; // Player UUID -> Bot
    private final Map<UUID, ArmorStand> botEntities; // Bot UUID -> ArmorStand (visual representation)
    
    public BotManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.activeBots = new ConcurrentHashMap<>();
        this.botEntities = new ConcurrentHashMap<>();
    }
    
    public boolean spawnBot(Player player, BotDifficulty difficulty, Location location, String kit) {
        if (activeBots.containsKey(player.getUniqueId())) {
            return false; // Already has a bot
        }
        
        PracticeBot bot = new PracticeBot(player, difficulty, location, kit);
        
        // Spawn visual representation (ArmorStand with player appearance)
        var world = location.getWorld();
        if (world == null) {
            player.sendMessage("§c✖ Impossible de spawner le bot dans ce monde!");
            return false;
        }
        ArmorStand stand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setVisible(true);
        stand.setGravity(true);
        stand.setCanPickupItems(false);
        stand.setCustomName("§e" + difficulty.getDisplayName() + " §7Bot");
        stand.setCustomNameVisible(true);
        stand.setBasePlate(false);
        stand.setArms(true);
        
        // Equip bot with armor
        equipBot(stand, kit);
        
        activeBots.put(player.getUniqueId(), bot);
        botEntities.put(bot.getBotId(), stand);
        
        // Start AI
        startBotAI(bot, stand);
        
        player.sendMessage("§a✓ Bot spawné: " + difficulty.getDisplayName());
        player.sendMessage("§7Difficulté: §f" + difficulty.name());
        player.sendMessage("§7Battez le bot pour gagner de l'XP!");
        
        return true;
    }
    
    @SuppressWarnings({"deprecation", "unused", "all"})
    private void equipBot(ArmorStand stand, String kit) {
        // Diamond armor for bots
        stand.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        stand.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        stand.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        stand.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        stand.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
    }
    
    private void startBotAI(PracticeBot bot, ArmorStand stand) {
        BukkitRunnable aiTask = new BukkitRunnable() {
            private int tickCounter = 0;
            private long lastAbility = System.currentTimeMillis();
            
            @Override
            public void run() {
                if (bot.isDead() || !bot.getTarget().isOnline()) {
                    removeBot(bot.getTarget().getUniqueId());
                    cancel();
                    return;
                }
                
                Player target = bot.getTarget();
                tickCounter++;
                
                // Movement AI
                if (tickCounter % 5 == 0) {
                    moveTowardsPlayer(stand, target, bot.getDifficulty());
                }
                
                // Combat AI
                if (tickCounter % bot.getDifficulty().getReactionSpeed() == 0) {
                    attemptAttack(bot, stand, target);
                }
                
                // Special abilities based on difficulty
                long now = System.currentTimeMillis();
                if (now - lastAbility >= bot.getDifficulty().getAbilityDelay()) {
                    useAbility(bot, stand, target);
                    lastAbility = now;
                }
                
                // Update health display
                updateHealthDisplay(stand, bot);
            }
        };
        
        // runTaskTimer returns BukkitTask, not BukkitRunnable
        BukkitTask task = aiTask.runTaskTimer(plugin, 0L, 1L);
        bot.setAiTask(task);
    }
    
    @SuppressWarnings({"unused", "ConstantConditions", "NullableProblems", "all"})
    private void moveTowardsPlayer(ArmorStand stand, Player target, BotDifficulty difficulty) {
        Location botLoc = stand.getLocation();
        Location targetLoc = target.getLocation();
        
        double distance = botLoc.distance(targetLoc);
        
        // Keep optimal combat distance (3-4 blocks)
        if (distance > 4.0) {
            // Move towards player
            var targetVec = targetLoc.toVector();
            var botVec = botLoc.toVector();
            org.bukkit.util.Vector direction = targetVec.subtract(botVec).normalize();
            botLoc.add(direction.multiply(0.2));
            stand.teleport(botLoc);
        } else if (distance < 2.0) {
            // Back away slightly
            var targetVec = targetLoc.toVector();
            var botVec = botLoc.toVector();
            org.bukkit.util.Vector direction = botVec.subtract(targetVec).normalize();
            botLoc.add(direction.multiply(0.1));
            stand.teleport(botLoc);
        }
        
        // Always look at player
        var targetVec = targetLoc.toVector();
        var botVec = botLoc.toVector();
        botLoc.setDirection(targetVec.subtract(botVec));
        stand.teleport(botLoc);
    }
    
    @SuppressWarnings({"ConstantConditions", "NullableProblems"})
    private void attemptAttack(PracticeBot bot, ArmorStand stand, Player target) {
        var standLoc = stand.getLocation();
        var targetLoc = target.getLocation();
        
        double distance = standLoc.distance(targetLoc);
        
        if (distance <= 4.0) {
            Random random = new Random();
            
            // Check accuracy
            if (random.nextInt(100) < bot.getDifficulty().getAccuracy()) {
                // Hit!
                double damage = 4.0 * bot.getDifficulty().getDamageMultiplier();
                target.damage(damage);
                bot.incrementHits();
                
                // Knockback
                var targetVec = targetLoc.toVector();
                var standVec = standLoc.toVector();
                org.bukkit.util.Vector knockback = targetVec.subtract(standVec).normalize().multiply(0.4);
                knockback.setY(0.2);
                target.setVelocity(knockback);
            } else {
                // Miss
                bot.incrementMisses();
            }
        }
    }
    
    private void useAbility(PracticeBot bot, ArmorStand stand, Player target) {
        Random random = new Random();
        int ability = random.nextInt(3);
        
        switch (ability) {
            case 0 -> {
                // Speed boost
                if (bot.getDifficulty() != BotDifficulty.EASY) {
                    // Simulate speed by moving faster for a few ticks
                }
            }
            case 1 -> {
                // Healing
                if (bot.getHealth() < bot.getMaxHealth() / 2) {
                    bot.heal(4);
                }
            }
            case 2 -> {
                // Combo attack
                if (bot.getDifficulty() == BotDifficulty.EXPERT) {
                    // Multiple quick attacks
                    for (int i = 0; i < 3; i++) {
                        attemptAttack(bot, stand, target);
                    }
                }
            }
        }
    }
    
    private void updateHealthDisplay(ArmorStand stand, PracticeBot bot) {
        int healthBars = (bot.getHealth() * 10) / bot.getMaxHealth();
        StringBuilder healthBar = new StringBuilder("§c");
        
        for (int i = 0; i < 10; i++) {
            if (i < healthBars) {
                healthBar.append("❤");
            } else {
                healthBar.append("§7❤");
            }
        }
        
        stand.setCustomName(bot.getDifficulty().getDisplayName() + " §7Bot " + healthBar);
    }
    
    public void damageBot(UUID playerId, int damage) {
        PracticeBot bot = activeBots.get(playerId);
        if (bot == null) return;
        
        bot.damage(damage);
        
        if (bot.isDead()) {
            defeatBot(playerId);
        }
    }
    
    private void defeatBot(UUID playerId) {
        PracticeBot bot = activeBots.get(playerId);
        if (bot == null) return;
        
        Player player = bot.getTarget();
        
        // Reward XP based on difficulty
        PracticePlayer practicePlayer = plugin.getPlayerManager().getOrCreate(player);
        int xpReward = switch (bot.getDifficulty()) {
            case EASY -> 50;
            case MEDIUM -> 100;
            case HARD -> 200;
            case EXPERT -> 400;
        };
        
        practicePlayer.addXP(xpReward);
        
        // Stats
        player.sendMessage("§a§l✓ Bot vaincu!");
        player.sendMessage("§e   Durée: §f" + bot.getDurationSeconds() + "s");
        player.sendMessage("§e   Précision: §f" + String.format("%.1f", bot.getAccuracyPercent()) + "%");
        player.sendMessage("§e   Récompense: §a+" + xpReward + " XP");
        
        removeBot(playerId);
    }
    
    public void removeBot(UUID playerId) {
        PracticeBot bot = activeBots.remove(playerId);
        if (bot == null) return;
        
        if (bot.getAiTask() != null) {
            bot.getAiTask().cancel();
        }
        
        ArmorStand stand = botEntities.remove(bot.getBotId());
        if (stand != null && !stand.isDead()) {
            stand.remove();
        }
    }
    
    public boolean hasActiveBot(UUID playerId) {
        return activeBots.containsKey(playerId);
    }
    
    public PracticeBot getBot(UUID playerId) {
        return activeBots.get(playerId);
    }
}
