package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Particle;
import org.bukkit.Material;

@Getter
@Setter
public class Cosmetic {
    private final String id;
    private final String name;
    private final CosmeticType type;
    private final Material icon;
    private final int price;
    private final String permission;
    private final Particle particle; // Pour kill effects et trails
    private final String particleData; // Données additionnelles
    
    public enum CosmeticType {
        KILL_EFFECT,
        PROJECTILE_TRAIL,
        DEATH_EFFECT,
        WIN_EFFECT,
        TITLE,
        CHAT_COLOR,
        KILLSTREAK_EFFECT
    }
    
    public Cosmetic(String id, String name, CosmeticType type, Material icon, 
                    int price, String permission, Particle particle, String particleData) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.price = price;
        this.permission = permission;
        this.particle = particle;
        this.particleData = particleData;
    }
    
    public Cosmetic(String id, String name, CosmeticType type, Material icon, int price) {
        this(id, name, type, icon, price, null, null, null);
    }
}
