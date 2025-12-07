package fr.louis.practice.models;

import org.bukkit.Particle;
import org.bukkit.Sound;

public class KillEffect {
    private final String id;
    private final String displayName;
    private final EffectType type;
    private final int coinsPrice;
    private final boolean requiresVIP;
    
    public enum EffectType {
        LIGHTNING("§e⚡ Éclair", Particle.FLAME, Sound.ENTITY_LIGHTNING_BOLT_THUNDER),
        EXPLOSION("§c💥 Explosion", Particle.EXPLOSION, Sound.ENTITY_GENERIC_EXPLODE),
        HEARTS("§d❤ Cœurs", Particle.HEART, Sound.ENTITY_EXPERIENCE_ORB_PICKUP),
        FLAMES("§6🔥 Flammes", Particle.FLAME, Sound.BLOCK_FIRE_AMBIENT),
        BLOOD("§4💧 Sang", Particle.BLOCK, Sound.ENTITY_PLAYER_HURT),
        MAGIC("§5✨ Magie", Particle.ENCHANT, Sound.ENTITY_PLAYER_LEVELUP),
        ENDER("§d🌀 Ender", Particle.PORTAL, Sound.ENTITY_ENDERMAN_TELEPORT),
        SMOKE("§8💨 Fumée", Particle.SMOKE, Sound.ENTITY_GHAST_SHOOT),
        SLIME("§a💚 Slime", Particle.ITEM_SLIME, Sound.ENTITY_SLIME_SQUISH),
        WATER("§b💧 Eau", Particle.SPLASH, Sound.ENTITY_PLAYER_SPLASH),
        CRITICAL("§e⭐ Critiques", Particle.CRIT, Sound.ENTITY_PLAYER_ATTACK_CRIT),
        ENCHANT("§b📘 Enchantement", Particle.ENCHANT, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        
        private final String displayName;
        private final Particle particle;
        private final Sound sound;
        
        EffectType(String displayName, Particle particle, Sound sound) {
            this.displayName = displayName;
            this.particle = particle;
            this.sound = sound;
        }
        
        public String getDisplayName() { return displayName; }
        public Particle getParticle() { return particle; }
        public Sound getSound() { return sound; }
    }
    
    public KillEffect(String id, String displayName, EffectType type, int coinsPrice, boolean requiresVIP) {
        this.id = id;
        this.displayName = displayName;
        this.type = type;
        this.coinsPrice = coinsPrice;
        this.requiresVIP = requiresVIP;
    }

    // Getters
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public EffectType getType() { return type; }
    public int getCoinsPrice() { return coinsPrice; }
    public boolean isRequiresVIP() { return requiresVIP; }
}

