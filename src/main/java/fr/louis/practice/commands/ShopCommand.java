package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.ShopItem;
import fr.louis.practice.models.PracticePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ShopCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public ShopCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            }
            return true;
        }
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        
        if (args.length == 0) {
            showShop(player, practicePlayer);
            return true;
        }
        
        if (args[0].equalsIgnoreCase("buy") && args.length >= 2) {
            buyItem(player, practicePlayer, args[1]);
            return true;
        }
        
        if (args[0].equalsIgnoreCase("category") && args.length >= 2) {
            showCategory(player, practicePlayer, args[1]);
            return true;
        }
        
        showShop(player, practicePlayer);
        return true;
    }
    
    private void showShop(Player player, PracticePlayer practicePlayer) {
        player.sendMessage(ChatColor.GOLD + "═══════════ BOUTIQUE ═══════════");
        player.sendMessage(ChatColor.YELLOW + "Coins: " + ChatColor.GREEN + practicePlayer.getCoins());
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Catégories:");
        
        for (ShopItem.ShopItemType type : ShopItem.ShopItemType.values()) {
            int itemCount = plugin.getShopManager().getItemsByType(type).size();
            player.sendMessage(ChatColor.GRAY + "  • " + ChatColor.WHITE + type.getDisplayName() +
                ChatColor.GRAY + " (" + String.valueOf(itemCount) + " items) " +
                ChatColor.YELLOW + "/shop category " + type.name().toLowerCase());
        }
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "Usage: /shop category <catégorie>");
        player.sendMessage(ChatColor.GRAY + "       /shop buy <id>");
        player.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
    }
    
    private void showCategory(Player player, PracticePlayer practicePlayer, String categoryName) {
        ShopItem.ShopItemType type;
        try {
            type = ShopItem.ShopItemType.valueOf(categoryName.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Catégorie invalide!");
            return;
        }
        
        List<ShopItem> items = plugin.getShopManager().getItemsByType(type);
        
        player.sendMessage(ChatColor.GOLD + "═══ " + type.getDisplayName() + " ═══");
        player.sendMessage(ChatColor.GRAY + type.getDescription());
        player.sendMessage(ChatColor.YELLOW + "Vos coins: " + ChatColor.GREEN + practicePlayer.getCoins());
        player.sendMessage("");
        
        for (ShopItem item : items) {
            boolean owned = plugin.getShopManager().hasItem(player.getUniqueId(), item.getId());
            String status = owned ? (ChatColor.GREEN + "✓ Possédé") : (ChatColor.YELLOW + String.valueOf(item.getPrice()) + " coins");
            
            player.sendMessage(ChatColor.WHITE + item.getName());
            player.sendMessage(ChatColor.GRAY + "  " + item.getDescription());
            player.sendMessage(ChatColor.GRAY + "  Prix: " + status);
            
            if (!owned) {
                player.sendMessage(ChatColor.YELLOW + "  /shop buy " + item.getId());
            }
            
            player.sendMessage("");
        }
        
        player.sendMessage(ChatColor.GOLD + "═══════════════════════════");
    }
    
    private void buyItem(Player player, PracticePlayer practicePlayer, String itemId) {
        ShopItem item = plugin.getShopManager().getItem(itemId).orElse(null);
        
        if (item == null) {
            player.sendMessage(ChatColor.RED + "Item introuvable!");
            return;
        }
        
        if (plugin.getShopManager().hasItem(player.getUniqueId(), itemId)) {
            player.sendMessage(ChatColor.RED + "Vous possédez déjà cet item!");
            return;
        }
        
        if (practicePlayer.getCoins() < item.getPrice()) {
            player.sendMessage(ChatColor.RED + "Coins insuffisants!");
            player.sendMessage(ChatColor.GRAY + "Prix: " + ChatColor.YELLOW + item.getPrice() + 
                ChatColor.GRAY + " | Vous avez: " + ChatColor.YELLOW + practicePlayer.getCoins());
            return;
        }
        
        if (plugin.getShopManager().purchaseItem(player.getUniqueId(), itemId)) {
            player.sendMessage(ChatColor.GREEN + "✓ Achat réussi!");
            player.sendMessage(ChatColor.GRAY + "Item: " + ChatColor.WHITE + item.getName());
            player.sendMessage(ChatColor.GRAY + "Prix: " + ChatColor.YELLOW + item.getPrice() + " coins");
            player.sendMessage(ChatColor.GRAY + "Coins restants: " + ChatColor.YELLOW + practicePlayer.getCoins());
        } else {
            player.sendMessage(ChatColor.RED + "Erreur lors de l'achat!");
        }
    }
}
