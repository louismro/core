package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public PartyCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            if (sender != null) {
                sender.sendMessage("§cCette commande est réservée aux joueurs!");
            }
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create" -> handleCreate(player);
            case "disband" -> handleDisband(player);
            case "invite" -> {
                if (args.length < 2) {
                    player.sendMessage("§cUtilisation: /party invite <joueur>");
                    return true;
                }
                handleInvite(player, args[1]);
            }
            case "accept" -> handleAccept(player);
            case "leave" -> handleLeave(player);
            case "kick" -> {
                if (args.length < 2) {
                    player.sendMessage("§cUtilisation: /party kick <joueur>");
                    return true;
                }
                handleKick(player, args[1]);
            }
            default -> sendHelp(player);
        }
        
        return true;
    }
    
    private void handleCreate(Player player) {
        Party party = plugin.getPartyManager().createParty(player);
        if (party != null) {
            player.sendMessage("§aPartie créée!");
        } else {
            player.sendMessage("§cVous êtes déjà dans une partie!");
        }
    }
    
    private void handleDisband(Player player) {
        Party party = plugin.getPartyManager().getPlayerParty(player.getUniqueId());
        
        if (party == null) {
            player.sendMessage("§cVous n'êtes pas dans une partie!");
            return;
        }
        
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage("§cSeul le chef peut dissoudre la partie!");
            return;
        }
        
        plugin.getPartyManager().disbandParty(party);
    }
    
    private void handleInvite(Player player, String targetName) {
        Party party = plugin.getPartyManager().getPlayerParty(player.getUniqueId());
        
        if (party == null) {
            player.sendMessage("§cVous n'êtes pas dans une partie!");
            return;
        }
        
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage("§cSeul le chef peut inviter des joueurs!");
            return;
        }
        
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            player.sendMessage("§cJoueur introuvable!");
            return;
        }
        
        if (target.equals(player)) {
            player.sendMessage("§cVous ne pouvez pas vous inviter vous-même!");
            return;
        }
        
        if (party.isFull()) {
            player.sendMessage("§cLa partie est pleine!");
            return;
        }
        
        plugin.getPartyManager().invitePlayer(party, target);
        player.sendMessage("§aInvitation envoyée à §e" + target.getName());
    }
    
    private void handleAccept(Player player) {
        // Trouver une invitation en attente
        Party invitedParty = null;
        
        for (Party party : plugin.getPartyManager().getParties().values()) {
            if (party.isInvited(player.getUniqueId())) {
                invitedParty = party;
                break;
            }
        }
        
        if (invitedParty == null) {
            player.sendMessage("§cVous n'avez aucune invitation en attente!");
            return;
        }
        
        plugin.getPartyManager().joinParty(player, invitedParty);
    }
    
    private void handleLeave(Player player) {
        Party party = plugin.getPartyManager().getPlayerParty(player.getUniqueId());
        
        if (party == null) {
            player.sendMessage("§cVous n'êtes pas dans une partie!");
            return;
        }
        
        plugin.getPartyManager().leaveParty(player, party);
    }
    
    private void handleKick(Player player, String targetName) {
        Party party = plugin.getPartyManager().getPlayerParty(player.getUniqueId());
        
        if (party == null) {
            player.sendMessage("§cVous n'êtes pas dans une partie!");
            return;
        }
        
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage("§cSeul le chef peut exclure des joueurs!");
            return;
        }
        
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            player.sendMessage("§cJoueur introuvable!");
            return;
        }
        
        if (!party.isMember(target.getUniqueId())) {
            player.sendMessage("§cCe joueur n'est pas dans votre partie!");
            return;
        }
        
        plugin.getPartyManager().kickPlayer(party, target.getUniqueId());
        player.sendMessage("§e" + target.getName() + " §aa été exclu de la partie");
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§8§m----------------------------");
        player.sendMessage("§b§lCommandes de Partie");
        player.sendMessage("");
        player.sendMessage("§e/party create §7- Créer une partie");
        player.sendMessage("§e/party disband §7- Dissoudre la partie");
        player.sendMessage("§e/party invite <joueur> §7- Inviter un joueur");
        player.sendMessage("§e/party accept §7- Accepter une invitation");
        player.sendMessage("§e/party leave §7- Quitter la partie");
        player.sendMessage("§e/party kick <joueur> §7- Exclure un joueur");
        player.sendMessage("§8§m----------------------------");
    }
}
