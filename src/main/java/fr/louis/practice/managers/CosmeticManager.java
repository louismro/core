package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Cosmetic;
import fr.louis.practice.models.Cosmetic.CosmeticType;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CosmeticManager {
    private final PracticeCore plugin;
    
    @Getter
    private final Map<String, Cosmetic> cosmetics = new HashMap<>();
    
    // Active cosmetics par joueur
    private final Map<UUID, String> activeKillEffects = new ConcurrentHashMap<>();
    private final Map<UUID, String> activeProjectileTrails = new ConcurrentHashMap<>();
    private final Map<UUID, String> activeDeathEffects = new ConcurrentHashMap<>();
    private final Map<UUID, String> activeWinEffects = new ConcurrentHashMap<>();
    
    // Tracking des projectiles pour trails
    private final Map<Projectile, UUID> projectileOwners = new ConcurrentHashMap<>();
    
    public CosmeticManager(PracticeCore plugin) {
        this.plugin = plugin;
        loadCosmetics();
        startProjectileTrailTask();
    }
    
    private void loadCosmetics() {
        // Kill Effects
        registerCosmetic(new Cosmetic("blood_explosion", "§cExplosion Sanglante", 
            CosmeticType.KILL_EFFECT, Material.REDSTONE, 1000, null, Particle.BLOCK, "REDSTONE_BLOCK"));
        
        registerCosmetic(new Cosmetic("lightning_strike", "§eÉclair Divin", 
            CosmeticType.KILL_EFFECT, Material.GOLD_BLOCK, 2500, null, Particle.FLAME, null));
        
        registerCosmetic(new Cosmetic("hearts_burst", "§dExplosion de Cœurs", 
            CosmeticType.KILL_EFFECT, Material.POPPY, 1500, null, Particle.HEART, null));
        
        registerCosmetic(new Cosmetic("flame_vortex", "§6Vortex de Flammes", 
            CosmeticType.KILL_EFFECT, Material.BLAZE_POWDER, 3000, null, Particle.FLAME, null));
        
        // Projectile Trails
        registerCosmetic(new Cosmetic("smoke_trail", "§7Traînée de Fumée", 
            CosmeticType.PROJECTILE_TRAIL, Material.COAL, 500, null, Particle.SMOKE, null));
        
        registerCosmetic(new Cosmetic("fire_trail", "§cTraînée de Feu", 
            CosmeticType.PROJECTILE_TRAIL, Material.BLAZE_POWDER, 1000, null, Particle.FLAME, null));
        
        registerCosmetic(new Cosmetic("water_trail", "§bTraînée Aquatique", 
            CosmeticType.PROJECTILE_TRAIL, Material.WATER_BUCKET, 1000, null, Particle.SPLASH, null));
        
        registerCosmetic(new Cosmetic("redstone_trail", "§4Traînée Redstone", 
            CosmeticType.PROJECTILE_TRAIL, Material.REDSTONE, 750, null, Particle.DUST, "REDSTONE_BLOCK"));
        
        registerCosmetic(new Cosmetic("enchant_trail", "§5Traînée Enchantée", 
            CosmeticType.PROJECTILE_TRAIL, Material.ENCHANTING_TABLE, 2000, null, Particle.PORTAL, null));
        
        // Death Effects
        registerCosmetic(new Cosmetic("ghost_rise", "§7Âme Ascendante", 
            CosmeticType.DEATH_EFFECT, Material.GHAST_TEAR, 2000, null, Particle.PORTAL, null));
        
        registerCosmetic(new Cosmetic("explosion_death", "§cExplosion Fatale", 
            CosmeticType.DEATH_EFFECT, Material.TNT, 1500, null, Particle.EXPLOSION, null));
        
        // Win Effects
        registerCosmetic(new Cosmetic("victory_firework", "§eFeu d'Artifice", 
            CosmeticType.WIN_EFFECT, Material.FIREWORK_ROCKET, 3000, null, Particle.FIREWORK, null));
        
        registerCosmetic(new Cosmetic("golden_shower", "§6Pluie Dorée", 
            CosmeticType.WIN_EFFECT, Material.GOLD_NUGGET, 2500, null, Particle.CLOUD, null));
    }
    
    private void registerCosmetic(Cosmetic cosmetic) {
        cosmetics.put(cosmetic.getId(), cosmetic);
    }
    
    public List<Cosmetic> getCosmeticsByType(CosmeticType type) {
        List<Cosmetic> result = new ArrayList<>();
        for (Cosmetic cosmetic : cosmetics.values()) {
            if (cosmetic.getType() == type) {
                result.add(cosmetic);
            }
        }
        return result;
    }
    
    public Cosmetic getCosmetic(String id) {
        return cosmetics.get(id);
    }
    
    // Kill Effects
    public void setKillEffect(UUID playerId, String cosmeticId) {
        activeKillEffects.put(playerId, cosmeticId);
    }
    
    public void playKillEffect(Location location, UUID killer) {
        String effectId = activeKillEffects.get(killer);
        if (effectId == null) return;
        
        Cosmetic cosmetic = getCosmetic(effectId);
        if (cosmetic == null || cosmetic.getParticle() == null) return;
        
        // Effet spécial pour lightning
        if (effectId.equals("lightning_strike")) {
            location.getWorld().strikeLightningEffect(location);
        }
        
        // Effet de particules en cercle
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 20) {
                    cancel();
                    return;
                }
                
                for (int i = 0; i < 8; i++) {
                    double angle = (Math.PI * 2 * i) / 8 + (ticks * 0.2);
                    double x = Math.cos(angle);
                    double z = Math.sin(angle);
                    
                    Location particleLoc = location.clone().add(x, 1, z);
                    
                    particleLoc.getWorld().spawnParticle(cosmetic.getParticle(), particleLoc, 1);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        // Son
        location.getWorld().playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }
    
    // Projectile Trails
    public void setProjectileTrail(UUID playerId, String cosmeticId) {
        activeProjectileTrails.put(playerId, cosmeticId);
    }
    
    public void trackProjectile(Projectile projectile, UUID shooter) {
        if (activeProjectileTrails.containsKey(shooter)) {
            projectileOwners.put(projectile, shooter);
        }
    }
    
    public void untrackProjectile(Projectile projectile) {
        projectileOwners.remove(projectile);
    }
    
    private void startProjectileTrailTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Projectile, UUID> entry : projectileOwners.entrySet()) {
                    Projectile projectile = entry.getKey();
                    UUID shooter = entry.getValue();
                    
                    if (projectile.isDead() || !projectile.isValid()) {
                        projectileOwners.remove(projectile);
                        continue;
                    }
                    
                    String trailId = activeProjectileTrails.get(shooter);
                    if (trailId == null) continue;
                    
                    Cosmetic cosmetic = getCosmetic(trailId);
                    if (cosmetic == null || cosmetic.getParticle() == null) continue;
                    
                    Location loc = projectile.getLocation();
                    
                    loc.getWorld().spawnParticle(cosmetic.getParticle(), loc, 1);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L); // Every tick
    }
    
    // Death Effects
    public void setDeathEffect(UUID playerId, String cosmeticId) {
        activeDeathEffects.put(playerId, cosmeticId);
    }
    
    public void playDeathEffect(Location location, UUID deceased) {
        String effectId = activeDeathEffects.get(deceased);
        if (effectId == null) return;
        
        Cosmetic cosmetic = getCosmetic(effectId);
        if (cosmetic == null || cosmetic.getParticle() == null) return;
        
        if (effectId.equals("explosion_death")) {
            location.getWorld().createExplosion(location, 0.0f, false);
        }
        
        // Particules ascendantes
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 40) {
                    cancel();
                    return;
                }
                
                double y = ticks * 0.1;
                
                for (int i = 0; i < 5; i++) {
                    double angle = (Math.PI * 2 * i) / 5 + (ticks * 0.3);
                    double radius = 0.5;
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    
                    Location particleLoc = location.clone().add(x, y, z);
                    particleLoc.getWorld().spawnParticle(cosmetic.getParticle(), particleLoc, 1);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        location.getWorld().playSound(location, Sound.ENTITY_GHAST_AMBIENT, 0.5f, 2.0f);
    }
    
    // Win Effects
    public void setWinEffect(UUID playerId, String cosmeticId) {
        activeWinEffects.put(playerId, cosmeticId);
    }
    
    public void playWinEffect(Player winner) {
        String effectId = activeWinEffects.get(winner.getUniqueId());
        if (effectId == null) return;
        
        Cosmetic cosmetic = getCosmetic(effectId);
        if (cosmetic == null || cosmetic.getParticle() == null) return;
        
        Location location = winner.getLocation();
        
        if (effectId.equals("victory_firework")) {
            // Firework
            for (int i = 0; i < 3; i++) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    location.getWorld().spawnParticle(Particle.FIREWORK, 
                        location.clone().add(0, 2, 0), 10);
                    location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
                }, i * 10L);
            }
        }
        
        // Effet circulaire autour du gagnant
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 60) {
                    cancel();
                    return;
                }
                
                for (int i = 0; i < 12; i++) {
                    double angle = (Math.PI * 2 * i) / 12 + (ticks * 0.15);
                    double radius = 1.5;
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    
                    Location particleLoc = winner.getLocation().add(x, 1.5, z);
                    particleLoc.getWorld().spawnParticle(cosmetic.getParticle(), particleLoc, 1);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
    
    // Getters pour les effets actifs
    public String getActiveKillEffect(UUID playerId) {
        return activeKillEffects.get(playerId);
    }
    
    public String getActiveProjectileTrail(UUID playerId) {
        return activeProjectileTrails.get(playerId);
    }
    
    public String getActiveDeathEffect(UUID playerId) {
        return activeDeathEffects.get(playerId);
    }
    
    public String getActiveWinEffect(UUID playerId) {
        return activeWinEffects.get(playerId);
    }
}
