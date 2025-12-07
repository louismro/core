package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@Setter
public class KitLoadout {
    private final UUID ownerId;
    private final String kitName;
    private final int loadoutNumber; // 1, 2, or 3
    private ItemStack[] contents;
    private ItemStack[] armorContents;
    private String customName;
    
    public KitLoadout(UUID ownerId, String kitName, int loadoutNumber) {
        this.ownerId = ownerId;
        this.kitName = kitName;
        this.loadoutNumber = loadoutNumber;
        this.contents = new ItemStack[36];
        this.armorContents = new ItemStack[4];
        this.customName = "Loadout " + loadoutNumber;
    }
    
    public void setCustomName(String name) {
        if (name != null && name.length() <= 16) {
            this.customName = name;
        }
    }
}
