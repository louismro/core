package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.FFAEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FFACommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public FFACommand(PracticeCore plugin) {
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
            sendHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "create":
                if (!player.hasPermission("practice.ffa.create")) {
                    player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
                    return true;
                }
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /ffa create <kit> <max_joueurs>");
                    return true;
                }
                createFFA(player, args[1], args[2]);
                break;
                
            case "join":
                if (args.length < 2) {
                    // Join first available
                    joinFirstAvailable(player);
                } else {
                    joinFFA(player, args[1]);
                }
                break;
                
            case "leave":
                leaveFFA(player);
                break;
                
            case "list":
                listFFAs(player);
                break;
                
            case "stats":
                showStats(player);
                break;
                
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void createFFA(Player player, String kit, String maxStr) {
        int maxPlayers;
        try {
            maxPlayers = Integer.parseInt(maxStr);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Nombre invalide!");
            return;
        }
        
        if (maxPlayers < 2 || maxPlayers > 100) {
            player.sendMessage(ChatColor.RED + "Le nombre de joueurs doit être entre 2 et 100!");
            return;
        }
        
        FFAEvent event = plugin.getFFAManager().createFFA(kit, player.getLocation(), maxPlayers);
        
        if (event == null) {
            player.sendMessage(ChatColor.RED + "Kit inexistant!");
            return;
        }
        
        player.sendMessage(ChatColor.GREEN + "FFA créé avec succès!");
        player.sendMessage(ChatColor.YELLOW + "ID: " + ChatColor.WHITE + event.getEventId().toString().substring(0, 8));
    }
    
    private void joinFFA(Player player, String eventIdStr) {
        List<FFAEvent> events = plugin.getFFAManager().getActiveEvents();
        
        FFAEvent event = events.stream()
            .filter(e -> e.getEventId().toString().startsWith(eventIdStr))
            .findFirst()
            .orElse(null);
        
        if (event == null) {
            player.sendMessage(ChatColor.RED + "Event FFA introuvable!");
            return;
        }
        
        if (!plugin.getFFAManager().joinFFA(player, event.getEventId())) {
            player.sendMessage(ChatColor.RED + "Impossible de rejoindre le FFA!");
        }
    }
    
    private void joinFirstAvailable(Player player) {
        List<FFAEvent> events = plugin.getFFAManager().getActiveEvents();
        
        FFAEvent available = events.stream()
            .filter(e -> !e.isFull() && 
                   (e.getState() == FFAEvent.FFAState.WAITING || 
                    e.getState() == FFAEvent.FFAState.ACTIVE))
            .findFirst()
            .orElse(null);
        
        if (available == null) {
            player.sendMessage(ChatColor.RED + "Aucun FFA disponible!");
            player.sendMessage(ChatColor.YELLOW + "Utilisez /ffa list pour voir les events actifs");
            return;
        }
        
        plugin.getFFAManager().joinFFA(player, available.getEventId());
    }
    
    private void leaveFFA(Player player) {
        FFAEvent event = plugin.getFFAManager().getPlayerEvent(player.getUniqueId());
        
        if (event == null) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans un FFA!");
            return;
        }
        
        plugin.getFFAManager().leaveFFA(player);
    }
    
    private void listFFAs(Player player) {
        List<FFAEvent> events = plugin.getFFAManager().getActiveEvents();
        
        if (events.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Aucun FFA actif!");
            return;
        }
        
        player.sendMessage(ChatColor.GOLD + "═══ FFA Actifs ═══");
        
        for (FFAEvent event : events) {
            String id = event.getEventId().toString().substring(0, 8);
            String state = getStateColor(event.getState()) + event.getState().toString();
            
            player.sendMessage(ChatColor.YELLOW + "[" + id + "] " + 
                ChatColor.WHITE + event.getKit() + " " +
                ChatColor.GRAY + "(" + event.getParticipantCount() + "/" + event.getMaxPlayers() + ") " +
                state);
        }
    }
    
    private void showStats(Player player) {
        FFAEvent event = plugin.getFFAManager().getPlayerEvent(player.getUniqueId());
        
        if (event == null) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans un FFA!");
            return;
        }
        
        player.sendMessage(ChatColor.GOLD + "═══ Statistiques FFA ═══");
        player.sendMessage(ChatColor.YELLOW + "Kit: " + ChatColor.WHITE + event.getKit());
        player.sendMessage(ChatColor.YELLOW + "Vos kills: " + ChatColor.WHITE + event.getKills(player.getUniqueId()));
        player.sendMessage(ChatColor.YELLOW + "Participants: " + ChatColor.WHITE + 
            event.getParticipantCount() + "/" + event.getMaxPlayers());
        
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Top 5 Kills:");
        
        List<java.util.Map.Entry<java.util.UUID, Integer>> top = event.getTopKillers(5);
        for (int i = 0; i < top.size(); i++) {
            org.bukkit.entity.Player p = plugin.getServer().getPlayer(top.get(i).getKey());
            if (p != null) {
                String position = "#" + (i + 1);
                player.sendMessage(ChatColor.GRAY + "  " + position + " " + 
                    ChatColor.WHITE + p.getName() + ChatColor.GRAY + " - " + 
                    ChatColor.YELLOW + top.get(i).getValue() + " kills");
            }
        }
    }
    
    private ChatColor getStateColor(FFAEvent.FFAState state) {
        switch (state) {
            case WAITING:
                return ChatColor.YELLOW;
            case ACTIVE:
                return ChatColor.GREEN;
            case ENDING:
            case FINISHED:
                return ChatColor.RED;
            default:
                return ChatColor.GRAY;
        }
    }
    
    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "═══ Commandes FFA ═══");
        player.sendMessage(ChatColor.YELLOW + "/ffa create <kit> <max>" + ChatColor.GRAY + " - Créer un FFA");
        player.sendMessage(ChatColor.YELLOW + "/ffa join [id]" + ChatColor.GRAY + " - Rejoindre un FFA");
        player.sendMessage(ChatColor.YELLOW + "/ffa leave" + ChatColor.GRAY + " - Quitter le FFA");
        player.sendMessage(ChatColor.YELLOW + "/ffa list" + ChatColor.GRAY + " - Liste des FFAs");
        player.sendMessage(ChatColor.YELLOW + "/ffa stats" + ChatColor.GRAY + " - Vos stats FFA");
    }
}
