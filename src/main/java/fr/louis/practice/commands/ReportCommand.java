package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerReport;
import fr.louis.practice.models.PlayerReport.ReportReason;
import fr.louis.practice.models.PlayerReport.ReportStatus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReportCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public ReportCommand(PracticeCore plugin) {
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
            case "create":
            case "add":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /report create <joueur> <raison> [description]");
                    showReasons(player);
                    return true;
                }
                handleCreate(player, args);
                break;
                
            case "list":
                if (player.hasPermission("practice.staff.reports")) {
                    handleList(player);
                } else {
                    handleMyReports(player);
                }
                break;
                
            case "handle":
                if (!player.hasPermission("practice.staff.reports")) {
                    player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /report handle <id>");
                    return true;
                }
                handleManage(player, args[1]);
                break;
                
            case "resolve":
                if (!player.hasPermission("practice.staff.reports")) {
                    player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /report resolve <id> [note]");
                    return true;
                }
                String resolveNote = args.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)) : "";
                plugin.getReportManager().handleReport(args[1], player.getUniqueId(), ReportStatus.RESOLVED, resolveNote);
                break;
                
            case "reject":
                if (!player.hasPermission("practice.staff.reports")) {
                    player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /report reject <id> [raison]");
                    return true;
                }
                String rejectNote = args.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)) : "";
                plugin.getReportManager().handleReport(args[1], player.getUniqueId(), ReportStatus.REJECTED, rejectNote);
                break;
                
            default:
                showHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "COMMANDES REPORT" + ChatColor.GOLD + "        ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "/report create <joueur>");
        player.sendMessage(ChatColor.GOLD + "║         " + ChatColor.WHITE + "<raison> [desc]");
        player.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + "Créer un report");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "/report list");
        player.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + "Voir vos reports");
        
        if (player.hasPermission("practice.staff.reports")) {
            player.sendMessage(ChatColor.GOLD + "║                               ║");
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.RED + "STAFF:");
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "/report handle <id>");
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "/report resolve <id>");
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "/report reject <id>");
        }
        
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
    }
    
    private void showReasons(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Raisons disponibles:");
        for (ReportReason reason : ReportReason.values()) {
            player.sendMessage(ChatColor.GRAY + "  • " + reason.name().toLowerCase() + 
                " - " + reason.getDisplayName());
        }
    }
    
    private void handleCreate(Player player, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);
        
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Joueur introuvable!");
            return;
        }
        
        ReportReason reason;
        try {
            reason = ReportReason.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Raison invalide!");
            showReasons(player);
            return;
        }
        
        String description = args.length > 3 ? 
            String.join(" ", java.util.Arrays.copyOfRange(args, 3, args.length)) : "";
        
        plugin.getReportManager().createReport(player.getUniqueId(), target.getUniqueId(), 
            reason, description);
    }
    
    private void handleList(Player player) {
        List<PlayerReport> reports = plugin.getReportManager().getPendingReports();
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "REPORTS PENDING" + 
            ChatColor.GOLD + " (" + reports.size() + ")     ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        
        if (reports.isEmpty()) {
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "Aucun report en attente");
        } else {
            int shown = Math.min(reports.size(), 10);
            for (int i = 0; i < shown; i++) {
                PlayerReport report = reports.get(i);
                Player reported = plugin.getServer().getPlayer(report.getReportedId());
                String name = reported != null ? reported.getName() : "Déconnecté";
                
                player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "[" + 
                    report.getId() + "] " + name);
                player.sendMessage(ChatColor.GOLD + "║    " + report.getReason().getSymbol() + 
                    " " + report.getReason().getDisplayName());
            }
            
            if (reports.size() > 10) {
                player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + 
                    "... et " + (reports.size() - 10) + " autres");
            }
        }
        
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
    }
    
    private void handleMyReports(Player player) {
        List<PlayerReport> reports = plugin.getReportManager().getReportsByReporter(player.getUniqueId());
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "VOS REPORTS" + 
            ChatColor.GOLD + " (" + reports.size() + ")         ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        
        if (reports.isEmpty()) {
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "Aucun report");
        } else {
            int shown = Math.min(reports.size(), 10);
            for (int i = 0; i < shown; i++) {
                PlayerReport report = reports.get(i);
                player.sendMessage(ChatColor.GOLD + "║  " + report.getStatus().getSymbol() + 
                    " " + ChatColor.WHITE + "[" + report.getId() + "]");
                player.sendMessage(ChatColor.GOLD + "║    " + report.getReason().getDisplayName() + 
                    " - " + report.getStatus().getDisplayName());
            }
        }
        
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
    }
    
    private void handleManage(Player player, String reportId) {
        PlayerReport report = plugin.getReportManager().getReport(reportId);
        
        if (report == null) {
            player.sendMessage(ChatColor.RED + "Report introuvable!");
            return;
        }
        
        Player reported = plugin.getServer().getPlayer(report.getReportedId());
        Player reporter = plugin.getServer().getPlayer(report.getReporterId());
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "REPORT " + reportId + 
            ChatColor.GOLD + "              ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "Reporter: " + 
            ChatColor.WHITE + (reporter != null ? reporter.getName() : "Déconnecté"));
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "Reporté: " + 
            ChatColor.WHITE + (reported != null ? reported.getName() : "Déconnecté"));
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "Raison: " + 
            report.getReason().getDisplayName());
        
        if (report.getDescription() != null && !report.getDescription().isEmpty()) {
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "Desc: " + 
                ChatColor.WHITE + report.getDescription());
        }
        
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "Statut: " + 
            report.getStatus().getDisplayName());
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.YELLOW + "/report resolve " + reportId);
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.YELLOW + "/report reject " + reportId);
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
    }
}
