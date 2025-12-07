package fr.louis.practice.gui;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PracticePlayer;
import fr.louis.practice.models.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShopGUI {
    private final PracticeCore plugin;
    private final Map<UUID, ShopItem.ShopItemType> openCategories = new HashMap<>();
    
    public ShopGUI(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    public void openMainShop(Player player) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        openCategories.remove(player.getUniqueId());
        
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "╔═══ BOUTIQUE ═══╗");
        
        // Coins display
        ItemStack coinsItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta coinsMeta = coinsItem.getItemMeta();
        if (coinsMeta != null) {
            coinsMeta.setDisplayName(ChatColor.YELLOW + "Vos Coins");
            List<String> coinsLore = new ArrayList<>();
            coinsLore.add(ChatColor.GRAY + "Coins: " + ChatColor.GREEN + practicePlayer.getCoins());
            coinsLore.add(ChatColor.GRAY + "Total gagné: " + ChatColor.GREEN + practicePlayer.getTotalCoinsEarned());
            coinsMeta.setLore(coinsLore);
            coinsItem.setItemMeta(coinsMeta);
        }
        inv.setItem(4, coinsItem);
        
        // Titres
        ItemStack titles = new ItemStack(Material.NAME_TAG);
        ItemMeta titlesMeta = titles.getItemMeta();
        if (titlesMeta != null) {
            titlesMeta.setDisplayName(ChatColor.GOLD + "╔ Titres");
            List<String> titlesLore = new ArrayList<>();
            titlesLore.add(ChatColor.GRAY + "Titres cosmétiques");
            titlesLore.add(ChatColor.YELLOW + "3 items disponibles");
            titlesLore.add("");
            titlesLore.add(ChatColor.GREEN + "» Cliquez pour voir");
            titlesMeta.setLore(titlesLore);
            titles.setItemMeta(titlesMeta);
        }
        inv.setItem(10, titles);
        
        // Effets
        ItemStack effects = new ItemStack(Material.FIREWORK_ROCKET);
        ItemMeta effectsMeta = effects.getItemMeta();
        if (effectsMeta != null) {
            effectsMeta.setDisplayName(ChatColor.RED + "╔ Effets de Victoire");
            List<String> effectsLore = new ArrayList<>();
            effectsLore.add(ChatColor.GRAY + "Effets lors des victoires");
            effectsLore.add(ChatColor.YELLOW + "3 items disponibles");
            effectsLore.add("");
            effectsLore.add(ChatColor.GREEN + "» Cliquez pour voir");
            effectsMeta.setLore(effectsLore);
            effects.setItemMeta(effectsMeta);
        }
        inv.setItem(12, effects);
        
        // Killstreak Effects
        ItemStack killstreakEffects = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta ksMeta = killstreakEffects.getItemMeta();
        if (ksMeta != null) {
            ksMeta.setDisplayName(ChatColor.DARK_RED + "╔ Effets Killstreak");
            List<String> ksLore = new ArrayList<>();
            ksLore.add(ChatColor.GRAY + "Effets lors des killstreaks");
            ksLore.add(ChatColor.YELLOW + "3 items disponibles");
            ksLore.add("");
            ksLore.add(ChatColor.GREEN + "» Cliquez pour voir");
            ksMeta.setLore(ksLore);
            killstreakEffects.setItemMeta(ksMeta);
        }
        inv.setItem(14, killstreakEffects);
        
        // Close button
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(ChatColor.RED + "Fermer");
            close.setItemMeta(closeMeta);
        }
        inv.setItem(26, close);
        
        player.openInventory(inv);
    }
    
    public void openCategory(Player player, ShopItem.ShopItemType category) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        
        // Track opened category
        openCategories.put(player.getUniqueId(), category);
        
        List<ShopItem> items = plugin.getShopManager().getItemsByType(category);
        
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GOLD + "╔═══ " + category.getDisplayName() + " ═══╗");
        
        // Coins display
        ItemStack coinsItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta coinsMeta = coinsItem.getItemMeta();
        if (coinsMeta != null) {
            coinsMeta.setDisplayName(ChatColor.YELLOW + "Vos Coins: " + ChatColor.GREEN + practicePlayer.getCoins());
            coinsItem.setItemMeta(coinsMeta);
        }
        inv.setItem(4, coinsItem);
        
        // Items
        int slot = 10;
        for (ShopItem item : items) {
            if (slot >= 43) break;
            
            boolean owned = plugin.getShopManager().hasItem(player.getUniqueId(), item.getId());
            
            Material material = owned ? Material.EMERALD : Material.DIAMOND;
            ItemStack itemStack = new ItemStack(material);
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(item.getName());
            
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + item.getDescription());
                lore.add("");
            
                if (owned) {
                    lore.add(ChatColor.GREEN + "✓ Possédé");
                } else {
                    lore.add(ChatColor.YELLOW + "Prix: " + ChatColor.GOLD + item.getPrice() + " coins");
                    lore.add("");
                    if (practicePlayer.getCoins() >= item.getPrice()) {
                        lore.add(ChatColor.GREEN + "» Clic gauche pour acheter");
                    } else {
                        lore.add(ChatColor.RED + "» Coins insuffisants");
                    }
                }
            
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
            }
            inv.setItem(slot, itemStack);
            
            slot++;
            if ((slot + 1) % 9 == 0) slot += 2;
        }
        
        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.YELLOW + "← Retour");
            back.setItemMeta(backMeta);
        }
        inv.setItem(45, back);
        
        player.openInventory(inv);
    }
    
    public void handleClick(Player player, int slot, Inventory inventory) {
        ShopItem.ShopItemType currentCategory = openCategories.get(player.getUniqueId());
        
        if (currentCategory == null) {
            // Main shop
            switch (slot) {
                case 10 -> openCategory(player, ShopItem.ShopItemType.TITLE);
                case 12 -> openCategory(player, ShopItem.ShopItemType.EFFECT);
                case 14 -> openCategory(player, ShopItem.ShopItemType.KILLSTREAK_EFFECT);
                case 26 -> player.closeInventory();
                default -> {
                }
            }
        } else {
            // Category view
            if (slot == 45) {
                openMainShop(player);
            } else {
                ItemStack clicked = inventory.getItem(slot);
                if (clicked != null && clicked.getType() == Material.DIAMOND) {
                    // Purchase attempt
                    attemptPurchase(player, slot, inventory);
                }
            }
        }
    }
    
    private void attemptPurchase(Player player, int slot, Inventory inventory) {
        ItemStack clicked = inventory.getItem(slot);
        
        if (clicked == null || !clicked.hasItemMeta()) return;
        
        org.bukkit.inventory.meta.ItemMeta clickedMeta = clicked.getItemMeta();
        if (clickedMeta == null) return;
        
        String displayName = clickedMeta.getDisplayName();
        
        // Find item by display name
        for (ShopItem item : plugin.getShopManager().getAllItems()) {
            if (item.getName().equals(displayName)) {
                if (plugin.getShopManager().purchaseItem(player.getUniqueId(), item.getId())) {
                    player.sendMessage(ChatColor.GREEN + "✓ Achat réussi!");
                    player.sendMessage(ChatColor.GRAY + "Item: " + item.getName());
                    org.bukkit.Location loc = player.getLocation();
                    if (loc != null) {
                        player.playSound(loc, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    }
                    
                    // Refresh inventory - use tracked category
                    ShopItem.ShopItemType category = openCategories.get(player.getUniqueId());
                    if (category != null) {
                        openCategory(player, category);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Échec de l'achat (coins insuffisants ou déjà possédé)");
                    org.bukkit.Location loc = player.getLocation();
                    if (loc != null) {
                        player.playSound(loc, org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    }
                }
                break;
            }
        }
    }
}
