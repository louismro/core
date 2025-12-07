package fr.louis.practice.models;

import org.bukkit.Sound;

public class HitSound {
    private final String id;
    private final String displayName;
    private final Sound sound;
    private final float pitch;
    private final int coinsPrice;
    
    public static final HitSound[] DEFAULT_SOUNDS = {
        new HitSound("classic", "§7Classique", Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 0),
        new HitSound("pop", "§ePop", Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f, 500),
        new HitSound("bass", "§5Bass", Sound.BLOCK_NOTE_BLOCK_BASS, 1.5f, 500),
        new HitSound("click", "§bClick", Sound.UI_BUTTON_CLICK, 1.0f, 1000),
        new HitSound("orb", "§aOrbe", Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.5f, 1000),
        new HitSound("xp", "§2XP", Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 1500),
        new HitSound("piano", "§6Piano", Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 2000),
        new HitSound("anvil", "§8Enclume", Sound.BLOCK_ANVIL_LAND, 2.0f, 2500),
        new HitSound("firework", "§cFeu d'artifice", Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 3000)
    };

    public HitSound(String id, String displayName, Sound sound, float pitch, int coinsPrice) {
        this.id = id;
        this.displayName = displayName;
        this.sound = sound;
        this.pitch = pitch;
        this.coinsPrice = coinsPrice;
    }

    // Getters
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Sound getSound() { return sound; }
    public float getPitch() { return pitch; }
    public int getCoinsPrice() { return coinsPrice; }
}

