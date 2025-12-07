package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Tournament;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TournamentCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public TournamentCommand(PracticeCore plugin) {
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
                if (!player.hasPermission("practice.tournament.create")) {
                    player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
                    return true;
                }
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /tournament create <kit> <joueurs>");
                    player.sendMessage(ChatColor.YELLOW + "Joueurs doit être une puissance de 2 (4, 8, 16, 32, 64)");
                    return true;
                }
                createTournament(player, args[1], args[2]);
            }
            case "join" -> {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /tournament join <id>");
                    return true;
                }
                joinTournament(player, args[1]);
            }
            case "leave" -> leaveTournament(player);
            case "list" -> listTournaments(player);
            case "info" -> showTournamentInfo(player);
            default -> sendHelp(player);
        }
        
        return true;
    }
    
    private void createTournament(Player player, String kit, String playersStr) {
        int maxPlayers;
        try {
            maxPlayers = Integer.parseInt(playersStr);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Nombre invalide!");
            return;
        }
        
        if (plugin.getKitManager().getKit(kit) == null) {
            player.sendMessage(ChatColor.RED + "Kit inexistant!");
            return;
        }
        
        Tournament tournament = plugin.getTournamentManager().createTournament(kit, maxPlayers);
        
        if (tournament == null) {
            player.sendMessage(ChatColor.RED + "Le nombre de joueurs doit être une puissance de 2!");
            return;
        }
        
        player.sendMessage(ChatColor.GREEN + "Tournoi créé avec succès!");
        player.sendMessage(ChatColor.YELLOW + "ID: " + ChatColor.WHITE + tournament.getTournamentId().toString().substring(0, 8));
    }
    
    private void joinTournament(Player player, String tournamentIdStr) {
        List<Tournament> tournaments = plugin.getTournamentManager().getActiveTournaments();
        
        Tournament tournament = tournaments.stream()
            .filter(t -> t.getTournamentId().toString().startsWith(tournamentIdStr))
            .findFirst()
            .orElse(null);
        
        if (tournament == null) {
            player.sendMessage(ChatColor.RED + "Tournoi introuvable!");
            return;
        }
        
        if (!plugin.getTournamentManager().joinTournament(tournament.getTournamentId(), player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Impossible de rejoindre le tournoi (plein ou déjà inscrit)");
        }
    }
    
    private void leaveTournament(Player player) {
        Tournament tournament = plugin.getTournamentManager().getPlayerTournament(player.getUniqueId());
        
        if (tournament == null) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans un tournoi!");
            return;
        }
        
        plugin.getTournamentManager().leaveTournament(player.getUniqueId());
    }
    
    private void listTournaments(Player player) {
        List<Tournament> tournaments = plugin.getTournamentManager().getActiveTournaments();
        
        if (tournaments.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Aucun tournoi actif!");
            return;
        }
        
        player.sendMessage(ChatColor.GOLD + "═══ Tournois Actifs ═══");
        
        for (Tournament tournament : tournaments) {
            String id = tournament.getTournamentId().toString().substring(0, 8);
            player.sendMessage(ChatColor.YELLOW + "[" + id + "] " + 
                ChatColor.WHITE + tournament.getKit() + " " +
                ChatColor.GRAY + "(" + tournament.getParticipants().size() + "/" + tournament.getMaxPlayers() + ") " +
                getStateColor(tournament.getState()) + tournament.getState());
        }
    }
    
    private void showTournamentInfo(Player player) {
        Tournament tournament = plugin.getTournamentManager().getPlayerTournament(player.getUniqueId());
        
        if (tournament == null) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans un tournoi!");
            return;
        }
        
        player.sendMessage(ChatColor.GOLD + "═══ Tournoi " + tournament.getKit() + " ═══");
        player.sendMessage(ChatColor.YELLOW + "État: " + getStateColor(tournament.getState()) + tournament.getState());
        player.sendMessage(ChatColor.YELLOW + "Participants: " + ChatColor.WHITE + tournament.getParticipants().size() + "/" + tournament.getMaxPlayers());
        
        if (tournament.getState() == Tournament.TournamentState.IN_PROGRESS) {
            player.sendMessage(ChatColor.YELLOW + "Round actuel: " + ChatColor.WHITE + tournament.getCurrentRound());
            player.sendMessage(ChatColor.YELLOW + "Joueurs restants: " + ChatColor.WHITE + tournament.getRemainingPlayers());
        }
    }
    
    private ChatColor getStateColor(Tournament.TournamentState state) {
        return switch (state) {
            case WAITING -> ChatColor.YELLOW;
            case STARTING -> ChatColor.GREEN;
            case IN_PROGRESS -> ChatColor.RED;
            case FINISHED -> ChatColor.GRAY;
            default -> ChatColor.WHITE;
        };
    }
    
    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "═══ Commandes Tournoi ═══");
        player.sendMessage(ChatColor.YELLOW + "/tournament create <kit> <joueurs>" + ChatColor.GRAY + " - Créer un tournoi");
        player.sendMessage(ChatColor.YELLOW + "/tournament join <id>" + ChatColor.GRAY + " - Rejoindre un tournoi");
        player.sendMessage(ChatColor.YELLOW + "/tournament leave" + ChatColor.GRAY + " - Quitter un tournoi");
        player.sendMessage(ChatColor.YELLOW + "/tournament list" + ChatColor.GRAY + " - Liste des tournois");
        player.sendMessage(ChatColor.YELLOW + "/tournament info" + ChatColor.GRAY + " - Info de votre tournoi");
    }
}
