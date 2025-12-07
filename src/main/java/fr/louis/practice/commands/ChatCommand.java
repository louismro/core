package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.ChatMessage.ChatChannel;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public ChatCommand(PracticeCore plugin) {
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
            showInfo(player);
            return true;
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "channel":
            case "ch":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /chat channel <global|party|clan|staff>");
                    return true;
                }
                handleChannelSwitch(player, args[1]);
                break;
                
            case "toggle":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /chat toggle <channel>");
                    return true;
                }
                handleToggle(player, args[1]);
                break;
                
            case "clear":
                for (int i = 0; i < 100; i++) {
                    player.sendMessage("");
                }
                player.sendMessage(ChatColor.GREEN + "✓ Chat effacé");
                break;
                
            default:
                showInfo(player);
                break;
        }
        
        return true;
    }
    
    private void handleChannelSwitch(Player player, String channelName) {
        try {
            ChatChannel channel = ChatChannel.valueOf(channelName.toUpperCase());
            
            if (channel == ChatChannel.STAFF && !player.hasPermission("practice.staff")) {
                player.sendMessage(ChatColor.RED + "Vous n'avez pas accès à ce canal!");
                return;
            }
            
            plugin.getChatManager().setActiveChannel(player.getUniqueId(), channel);
            player.sendMessage(ChatColor.GREEN + "✓ Canal changé: " + channel.name());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Canal invalide!");
            player.sendMessage(ChatColor.YELLOW + "Disponibles: GLOBAL, PARTY, CLAN, STAFF");
        }
    }
    
    private void handleToggle(Player player, String channelName) {
        try {
            ChatChannel channel = ChatChannel.valueOf(channelName.toUpperCase());
            plugin.getChatManager().toggleChannelMute(player.getUniqueId(), channel);
            
            boolean muted = plugin.getChatManager().hasChannelMuted(player.getUniqueId(), channel);
            player.sendMessage(muted ? 
                ChatColor.RED + "✗ Canal " + channel.name() + " désactivé" :
                ChatColor.GREEN + "✓ Canal " + channel.name() + " activé");
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Canal invalide!");
        }
    }
    
    private void showInfo(Player player) {
        ChatChannel active = plugin.getChatManager().getActiveChannel(player.getUniqueId());
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══ Paramètres Chat ═══");
        player.sendMessage(ChatColor.YELLOW + "Canal actif: " + ChatColor.WHITE + active.name());
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Commandes:");
        player.sendMessage(ChatColor.GRAY + "  /chat channel <canal>" + ChatColor.WHITE + " - Changer de canal");
        player.sendMessage(ChatColor.GRAY + "  /chat toggle <canal>" + ChatColor.WHITE + " - Activer/Désactiver");
        player.sendMessage(ChatColor.GRAY + "  /chat clear" + ChatColor.WHITE + " - Effacer le chat");
        player.sendMessage("");
    }
}
