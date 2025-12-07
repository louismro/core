package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Clan;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ClanCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public ClanCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /clan create <nom> <tag>");
                    return true;
                }
                createClan(player, args[1], args[2]);
            }
            case "disband" -> disbandClan(player);
            case "invite" -> {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /clan invite <joueur>");
                    return true;
                }
                invitePlayer(player, args[1]);
            }
            case "kick" -> {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /clan kick <joueur>");
                    return true;
                }
                kickPlayer(player, args[1]);
            }
            case "leave" -> leaveClan(player);
            case "info" -> {
                if (args.length > 1) {
                    showClanInfo(player, args[1]);
                } else {
                    showOwnClanInfo(player);
                }
            }
            case "top" -> showTopClans(player);
            default -> sendHelp(player);
        }
        
        return true;
    }
    
    private void createClan(Player player, String name, String tag) {
        if (tag.length() > 6) {
            player.sendMessage(ChatColor.RED + "Le tag doit faire 6 caractères maximum!");
            return;
        }
        
        plugin.getClanManager().createClan(name, tag, player.getUniqueId()).thenAccept(success -> {
            if (success) {
                player.sendMessage(ChatColor.GREEN + "Clan " + ChatColor.GOLD + name + ChatColor.GREEN + " créé avec succès!");
                player.sendMessage(ChatColor.GRAY + "Tag: " + ChatColor.WHITE + "[" + tag + "]");
            } else {
                player.sendMessage(ChatColor.RED + "Impossible de créer le clan (nom/tag déjà pris ou vous êtes déjà dans un clan)");
            }
        });
    }
    
    private void disbandClan(Player player) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans un clan!");
            return;
        }
        
        if (!clan.isLeader(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Seul le chef peut dissoudre le clan!");
            return;
        }
        
        plugin.getClanManager().disbandClan(clan.getClanId());
        player.sendMessage(ChatColor.GREEN + "Clan dissous!");
    }
    
    private void invitePlayer(Player player, String targetName) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans un clan!");
            return;
        }
        
        if (!clan.isLeader(player.getUniqueId()) && !clan.isModerator(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Seuls les modérateurs et le chef peuvent inviter!");
            return;
        }
        
        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "Joueur introuvable!");
            return;
        }
        
        if (plugin.getClanManager().inviteToClan(clan.getClanId(), target.getUniqueId())) {
            player.sendMessage(ChatColor.GREEN + target.getName() + " a rejoint le clan!");
            target.sendMessage(ChatColor.GREEN + "Vous avez rejoint le clan " + ChatColor.GOLD + clan.getName());
        } else {
            player.sendMessage(ChatColor.RED + "Impossible d'inviter ce joueur (déjà dans un clan ou clan plein)");
        }
    }
    
    private void kickPlayer(Player player, String targetName) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans un clan!");
            return;
        }
        
        if (!clan.isLeader(player.getUniqueId()) && !clan.isModerator(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Seuls les modérateurs et le chef peuvent kick!");
            return;
        }
        
        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Joueur introuvable!");
            return;
        }
        
        if (!clan.isMember(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Ce joueur n'est pas dans votre clan!");
            return;
        }
        
        if (clan.isLeader(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas kick le chef!");
            return;
        }
        
        plugin.getClanManager().kickFromClan(clan.getClanId(), target.getUniqueId());
        player.sendMessage(ChatColor.GREEN + target.getName() + " a été exclu du clan!");
        target.sendMessage(ChatColor.RED + "Vous avez été exclu du clan " + clan.getName());
    }
    
    private void leaveClan(Player player) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans un clan!");
            return;
        }
        
        if (clan.isLeader(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Le chef ne peut pas quitter le clan! Utilisez /clan disband");
            return;
        }
        
        plugin.getClanManager().kickFromClan(clan.getClanId(), player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Vous avez quitté le clan!");
    }
    
    private void showOwnClanInfo(Player player) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans un clan!");
            return;
        }
        
        displayClanInfo(player, clan);
    }
    
    private void showClanInfo(Player player, String clanName) {
        Clan clan = plugin.getClanManager().getClanByName(clanName);
        
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Clan introuvable!");
            return;
        }
        
        displayClanInfo(player, clan);
    }
    
    private void displayClanInfo(Player player, Clan clan) {
        player.sendMessage(ChatColor.GOLD + "═══ Clan " + clan.getName() + " ═══");
        player.sendMessage(ChatColor.YELLOW + "Tag: " + ChatColor.WHITE + "[" + clan.getTag() + "]");
        player.sendMessage(ChatColor.YELLOW + "ELO: " + ChatColor.WHITE + clan.getElo());
        player.sendMessage(ChatColor.YELLOW + "W/L: " + ChatColor.GREEN + clan.getWins() + ChatColor.GRAY + "/" + ChatColor.RED + clan.getLosses());
        player.sendMessage(ChatColor.YELLOW + "Winrate: " + ChatColor.WHITE + String.format("%.1f%%", clan.getWinrate()));
        player.sendMessage(ChatColor.YELLOW + "Membres: " + ChatColor.WHITE + clan.getMembers().size() + "/" + clan.getSettings().get("maxMembers"));
    }
    
    private void showTopClans(Player player) {
        List<Clan> topClans = plugin.getClanManager().getTopClans(10);
        
        player.sendMessage(ChatColor.GOLD + "═══ Top 10 Clans ═══");
        
        for (int i = 0; i < topClans.size(); i++) {
            Clan clan = topClans.get(i);
            String color = i == 0 ? ChatColor.GOLD.toString() : i == 1 ? ChatColor.YELLOW.toString() : i == 2 ? ChatColor.RED.toString() : ChatColor.GRAY.toString();
            
            player.sendMessage(color + "#" + (i + 1) + " " + ChatColor.WHITE + clan.getName() + 
                ChatColor.GRAY + " [" + clan.getTag() + "] " + 
                ChatColor.YELLOW + clan.getElo() + " ELO");
        }
    }
    
    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "═══ Commandes Clan ═══");
        player.sendMessage(ChatColor.YELLOW + "/clan create <nom> <tag>" + ChatColor.GRAY + " - Créer un clan");
        player.sendMessage(ChatColor.YELLOW + "/clan disband" + ChatColor.GRAY + " - Dissoudre le clan");
        player.sendMessage(ChatColor.YELLOW + "/clan invite <joueur>" + ChatColor.GRAY + " - Inviter un joueur");
        player.sendMessage(ChatColor.YELLOW + "/clan kick <joueur>" + ChatColor.GRAY + " - Exclure un membre");
        player.sendMessage(ChatColor.YELLOW + "/clan leave" + ChatColor.GRAY + " - Quitter le clan");
        player.sendMessage(ChatColor.YELLOW + "/clan info [nom]" + ChatColor.GRAY + " - Info clan");
        player.sendMessage(ChatColor.YELLOW + "/clan top" + ChatColor.GRAY + " - Top 10 clans");
    }
}
