package fr.louis.practice.models;

import java.util.*;

public class HotbarLayout {
    private final UUID playerId;
    private final String layoutName;
    private final Map<Integer, String> slotItems; // Slot -> Item type
    
    public static final String[] AVAILABLE_ITEMS = {
        "POTION_SPEED", "POTION_HEALTH", "ENDER_PEARL", 
        "SWORD", "ROD", "BOW", "FOOD", "BLOCKS"
    };
    
    public HotbarLayout(UUID playerId, String layoutName) {
        this.playerId = playerId;
        this.layoutName = layoutName;
        this.slotItems = new HashMap<>();
        
        // Default layout
        slotItems.put(0, "SWORD");
        slotItems.put(1, "ENDER_PEARL");
        slotItems.put(2, "POTION_SPEED");
        slotItems.put(3, "POTION_HEALTH");
        slotItems.put(4, "FOOD");
        slotItems.put(8, "BLOCKS");
    }
    
    public void setSlot(int slot, String itemType) {
        if (slot >= 0 && slot <= 8) {
            slotItems.put(slot, itemType);
        }
    }
    
    public String getItemAt(int slot) {
        return slotItems.get(slot);
    }

    // Getters
    public UUID getPlayerId() { return playerId; }
    public String getLayoutName() { return layoutName; }
    public Map<Integer, String> getSlotItems() { return slotItems; }
}

