package fr.louis.practice.models;

import org.bukkit.Material;
import java.util.UUID;

public class PlayerTrail {
    private final UUID playerId;
    private TrailType type;
    private boolean enabled;
    
    public enum TrailType {
        RAINBOW("§cA§6r§ec§ad§bi§9e§5n§c-§6c§ei§ae§bl", 
                new Material[]{Material.WHITE_WOOL, Material.CLAY},
                new byte[]{14, 1, 4, 5, 13, 11, 10, 14}), // Red, Orange, Yellow, Green, Cyan, Blue, Purple
        
        FIRE("§c§lFEU", 
                new Material[]{Material.REDSTONE_BLOCK, Material.GOLD_BLOCK, Material.COAL_BLOCK},
                new byte[]{0}),
        
        ICE("§b§lGLACE", 
                new Material[]{Material.ICE, Material.PACKED_ICE, Material.SNOW_BLOCK},
                new byte[]{0}),
        
        TOXIC("§a§lTOXIQUE", 
                new Material[]{Material.SLIME_BLOCK, Material.EMERALD_BLOCK},
                new byte[]{0}),
        
        VOID("§5§lVOIDE", 
                new Material[]{Material.OBSIDIAN, Material.COAL_BLOCK, Material.BEDROCK},
                new byte[]{0}),
        
        GOLD("§6§lOR", 
                new Material[]{Material.GOLD_BLOCK, Material.GLOWSTONE},
                new byte[]{0}),
        
        DIAMOND("§b§lDIAMANT", 
                new Material[]{Material.DIAMOND_BLOCK, Material.BEACON},
                new byte[]{0}),
        
        REDSTONE("§c§lREDSTONE", 
                new Material[]{Material.REDSTONE_BLOCK, Material.REDSTONE_LAMP},
                new byte[]{0}),
        
        NETHER("§4§lNETHER", 
                new Material[]{Material.NETHERRACK, Material.NETHER_BRICK, Material.SOUL_SAND},
                new byte[]{0}),
        
        END("§d§lEND", 
                new Material[]{Material.END_STONE, Material.PURPUR_BLOCK},
                new byte[]{0}),
        
        OCEAN("§3§lOCÉAN", 
                new Material[]{Material.PRISMARINE, Material.SEA_LANTERN},
                new byte[]{0}),
        
        BLOOD("§4§lSANG", 
                new Material[]{Material.REDSTONE_BLOCK, Material.RED_SANDSTONE},
                new byte[]{0}),
        
        LIGHTNING("§e§lÉCLAIR", 
                new Material[]{Material.QUARTZ_BLOCK, Material.GLOWSTONE, Material.GOLD_BLOCK},
                new byte[]{0}),
        
        GALAXY("§5§lGALAXIE", 
                new Material[]{Material.WHITE_WOOL},
                new byte[]{10, 11, 2, 14, 10}), // Purple, Blue, Magenta, Red cycle
        
        ENCHANTED("§d§lENCHANTÉ", 
                new Material[]{Material.ENCHANTING_TABLE, Material.BOOKSHELF},
                new byte[]{0});
        
        private final String displayName;
        private final Material[] blocks;
        private final byte[] data;
        
        TrailType(String displayName, Material[] blocks, byte[] data) {
            this.displayName = displayName;
            this.blocks = blocks;
            this.data = data;
        }
        
        public String getDisplayName() { return displayName; }
        public Material[] getBlocks() { return blocks; }
        public byte[] getData() { return data; }
    }
    
    public PlayerTrail(UUID playerId) {
        this.playerId = playerId;
        this.type = TrailType.RAINBOW;
        this.enabled = false;
    }

    // Getters
    public UUID getPlayerId() { return playerId; }
    public TrailType getType() { return type; }
    public boolean isEnabled() { return enabled; }

    // Setters
    public void setType(TrailType type) { this.type = type; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}

