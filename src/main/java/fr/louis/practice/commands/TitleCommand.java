package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerTitle;
import fr.louis.practice.models.PlayerTitle.TitleRarity;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class TitleCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public TitleCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        
        if (args.length == 0) {
            showTitles(player);
            return true;
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "list" -> showTitles(player);
            case "equip", "set" -> {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /title equip <id>");
                    return true;
                }
                handleEquip(player, args[1]);
            }
            case "unequip", "remove" -> {
                plugin.getTitleManager().setActiveTitle(player.getUniqueId(), null);
                player.sendMessage(ChatColor.YELLOW + "Titre retiré.");
            }
            case "buy", "purchase" -> {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /title buy <id>");
                    return true;
                }
                handleBuy(player, args[1]);
            }
            case "shop" -> showShop(player);
            default -> showHelp(player);
        }
        
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "COMMANDES TITRES" + ChatColor.GOLD + "        ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "/title list");
        player.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + "Voir vos titres");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "/title equip <id>");
        player.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + "Équiper un titre");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "/title unequip");
        player.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + "Retirer votre titre");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "/title shop");
        player.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + "Acheter des titres");
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
    }
    
    private void showTitles(Player player) {
        Set<String> unlocked = plugin.getTitleManager().getUnlockedTitles(player.getUniqueId());
        String activeId = plugin.getTitleManager().getActiveTitle(player.getUniqueId());
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "VOS TITRES" + 
            ChatColor.GOLD + " (" + unlocked.size() + ")           ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        
        if (unlocked.isEmpty()) {
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + 
                "Aucun titre débloqué");
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + 
                "Utilisez /title shop");
        } else {
            for (String titleId : unlocked) {
                PlayerTitle title = plugin.getTitleManager().getAvailableTitles().get(titleId);
                if (title == null) continue;
                
                String active = titleId.equals(activeId) ? ChatColor.GREEN + " ✓" : "";
                player.sendMessage(ChatColor.GOLD + "║  " + title.getPrefix() + active);
                player.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + "ID: " + 
                    ChatColor.WHITE + titleId);
            }
        }
        
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.YELLOW + "/title equip <id>");
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
    }
    
    private void showShop(Player player) {
        Set<String> unlocked = plugin.getTitleManager().getUnlockedTitles(player.getUniqueId());
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╬═══════════════════════════════╬");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "SHOP TITRES" + ChatColor.GOLD + "             ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        
        for (TitleRarity rarity : TitleRarity.values()) {
            List<PlayerTitle> titles = plugin.getTitleManager().getTitlesByRarity(rarity);
            if (titles.isEmpty()) continue;
            
            player.sendMessage(ChatColor.GOLD + "║  " + rarity.getDisplayName() + ":");
            
            for (PlayerTitle title : titles) {
                if (unlocked.contains(title.getId())) {
                    player.sendMessage(ChatColor.GOLD + "║    " + title.getPrefix() + 
                        ChatColor.GREEN + " ✓ Débloqué");
                } else if (title.isRequiresAchievement()) {
                    player.sendMessage(ChatColor.GOLD + "║    " + title.getPrefix() + 
                        ChatColor.GRAY + " (Achievement)");
                } else {
                    player.sendMessage(ChatColor.GOLD + "║    " + title.getPrefix() + 
                        ChatColor.GOLD + " - " + title.getCoinsPrice() + " coins");
                    player.sendMessage(ChatColor.GOLD + "║      " + ChatColor.DARK_GRAY + 
                        "/title buy " + title.getId());
                }
            }
            
            player.sendMessage(ChatColor.GOLD + "║                               ║");
        }
        
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
    }
    
    private void handleEquip(Player player, String titleId) {
        plugin.getTitleManager().setActiveTitle(player.getUniqueId(), titleId);
    }
    
    private void handleBuy(Player player, String titleId) {
        plugin.getTitleManager().purchaseTitle(player, titleId);
    }
}
