package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.KitLoadout;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LoadoutCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public LoadoutCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "save":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /loadout save <kit> <1-3>");
                    return true;
                }
                handleSave(player, args[1], args[2]);
                break;
                
            case "load":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /loadout load <kit> <1-3>");
                    return true;
                }
                handleLoad(player, args[1], args[2]);
                break;
                
            case "delete":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /loadout delete <kit> <1-3>");
                    return true;
                }
                handleDelete(player, args[1], args[2]);
                break;
                
            case "rename":
                if (args.length < 4) {
                    player.sendMessage(ChatColor.RED + "Usage: /loadout rename <kit> <1-3> <nom>");
                    return true;
                }
                String newName = String.join(" ", java.util.Arrays.copyOfRange(args, 3, args.length));
                handleRename(player, args[1], args[2], newName);
                break;
                
            case "list":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /loadout list <kit>");
                    return true;
                }
                handleList(player, args[1]);
                break;
                
            default:
                showHelp(player);
                break;
        }
        
        return true;
    }
    
    private void handleSave(Player player, String kitName, String numStr) {
        try {
            int loadoutNum = Integer.parseInt(numStr);
            if (loadoutNum < 1 || loadoutNum > 3) {
                player.sendMessage(ChatColor.RED + "Le numéro doit être entre 1 et 3!");
                return;
            }
            
            plugin.getLoadoutManager().saveLoadout(player, kitName.toLowerCase(), loadoutNum);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Numéro invalide!");
        }
    }
    
    private void handleLoad(Player player, String kitName, String numStr) {
        try {
            int loadoutNum = Integer.parseInt(numStr);
            if (loadoutNum < 1 || loadoutNum > 3) {
                player.sendMessage(ChatColor.RED + "Le numéro doit être entre 1 et 3!");
                return;
            }
            
            plugin.getLoadoutManager().loadLoadout(player, kitName.toLowerCase(), loadoutNum);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Numéro invalide!");
        }
    }
    
    private void handleDelete(Player player, String kitName, String numStr) {
        try {
            int loadoutNum = Integer.parseInt(numStr);
            if (loadoutNum < 1 || loadoutNum > 3) {
                player.sendMessage(ChatColor.RED + "Le numéro doit être entre 1 et 3!");
                return;
            }
            
            plugin.getLoadoutManager().deleteLoadout(player, kitName.toLowerCase(), loadoutNum);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Numéro invalide!");
        }
    }
    
    private void handleRename(Player player, String kitName, String numStr, String newName) {
        try {
            int loadoutNum = Integer.parseInt(numStr);
            if (loadoutNum < 1 || loadoutNum > 3) {
                player.sendMessage(ChatColor.RED + "Le numéro doit être entre 1 et 3!");
                return;
            }
            
            plugin.getLoadoutManager().renameLoadout(player, kitName.toLowerCase(), loadoutNum, newName);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Numéro invalide!");
        }
    }
    
    private void handleList(Player player, String kitName) {
        List<KitLoadout> loadouts = plugin.getLoadoutManager()
            .getPlayerLoadouts(player.getUniqueId(), kitName.toLowerCase());
        
        if (loadouts.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Aucun loadout pour " + kitName);
            return;
        }
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══ Loadouts " + kitName + " ═══");
        for (KitLoadout loadout : loadouts) {
            int active = plugin.getLoadoutManager().getActiveLoadout(player.getUniqueId(), kitName.toLowerCase());
            String activeMarker = loadout.getLoadoutNumber() == active ? 
                ChatColor.GREEN + " ✓ Actif" : "";
            
            player.sendMessage(ChatColor.YELLOW + "#" + loadout.getLoadoutNumber() + 
                ChatColor.WHITE + " - " + loadout.getCustomName() + activeMarker);
        }
        player.sendMessage("");
    }
    
    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══ Commandes Loadout ═══");
        player.sendMessage(ChatColor.YELLOW + "/loadout save <kit> <1-3>" + ChatColor.GRAY + " - Sauvegarder");
        player.sendMessage(ChatColor.YELLOW + "/loadout load <kit> <1-3>" + ChatColor.GRAY + " - Charger");
        player.sendMessage(ChatColor.YELLOW + "/loadout delete <kit> <1-3>" + ChatColor.GRAY + " - Supprimer");
        player.sendMessage(ChatColor.YELLOW + "/loadout rename <kit> <1-3> <nom>" + ChatColor.GRAY + " - Renommer");
        player.sendMessage(ChatColor.YELLOW + "/loadout list <kit>" + ChatColor.GRAY + " - Liste");
        player.sendMessage("");
    }
}
