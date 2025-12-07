package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerReport;
import fr.louis.practice.models.PlayerReport.ReportReason;
import fr.louis.practice.models.PlayerReport.ReportStatus;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ReportManager {
    private final PracticeCore plugin;
    
    @Getter
    private final Map<String, PlayerReport> reports = new ConcurrentHashMap<>();
    
    // Cooldown pour éviter le spam de reports
    private final Map<UUID, Long> reportCooldowns = new ConcurrentHashMap<>();
    private static final long REPORT_COOLDOWN = 30000; // 30 secondes
    
    public ReportManager(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    public void createReport(UUID reporterId, UUID reportedId, ReportReason reason, String description) {
        Player reporter = plugin.getServer().getPlayer(reporterId);
        Player reported = plugin.getServer().getPlayer(reportedId);
        
        if (reporter == null) return;
        
        // Vérifier le cooldown
        if (hasCooldown(reporterId)) {
            long remaining = getRemainingCooldown(reporterId);
            reporter.sendMessage(ChatColor.RED + "Attendez " + (remaining / 1000) + 
                " secondes avant de faire un autre report!");
            return;
        }
        
        if (reporterId.equals(reportedId)) {
            reporter.sendMessage(ChatColor.RED + "Vous ne pouvez pas vous report vous-même!");
            return;
        }
        
        if (reported == null) {
            reporter.sendMessage(ChatColor.RED + "Le joueur n'est pas en ligne!");
            return;
        }
        
        PlayerReport report = new PlayerReport(reporterId, reportedId, reason, description);
        reports.put(report.getId(), report);
        reportCooldowns.put(reporterId, System.currentTimeMillis());
        
        // Confirmer au reporter
        reporter.sendMessage("");
        reporter.sendMessage(ChatColor.GREEN + "✓ Report envoyé!");
        reporter.sendMessage(ChatColor.GRAY + "ID: " + ChatColor.WHITE + report.getId());
        reporter.sendMessage(ChatColor.GRAY + "Joueur: " + ChatColor.WHITE + reported.getName());
        reporter.sendMessage(ChatColor.GRAY + "Raison: " + reason.getDisplayName());
        reporter.sendMessage("");
        
        // Notifier les staffs en ligne
        notifyStaff(report, reported.getName());
    }
    
    private void notifyStaff(PlayerReport report, String reportedName) {
        String message = ChatColor.RED + "⚠ NOUVEAU REPORT [" + report.getId() + "]" + "\n" +
            ChatColor.GRAY + "Joueur: " + ChatColor.WHITE + reportedName + "\n" +
            ChatColor.GRAY + "Raison: " + report.getReason().getDisplayName() + "\n" +
            ChatColor.YELLOW + "Cliquez pour gérer: /report handle " + report.getId();
        
        for (Player staff : plugin.getServer().getOnlinePlayers()) {
            if (staff.hasPermission("practice.staff.reports")) {
                staff.sendMessage("");
                staff.sendMessage(message);
                staff.sendMessage("");
            }
        }
    }
    
    public void handleReport(String reportId, UUID handlerId, ReportStatus newStatus, String note) {
        PlayerReport report = reports.get(reportId);
        
        if (report == null) {
            Player handler = plugin.getServer().getPlayer(handlerId);
            if (handler != null) {
                handler.sendMessage(ChatColor.RED + "Report introuvable!");
            }
            return;
        }
        
        report.setStatus(newStatus);
        report.setHandledBy(handlerId);
        report.setHandledAt(java.time.LocalDateTime.now());
        report.setHandlerNote(note);
        
        Player handler = plugin.getServer().getPlayer(handlerId);
        if (handler != null) {
            handler.sendMessage(ChatColor.GREEN + "✓ Report " + reportId + " marqué comme: " + 
                newStatus.getDisplayName());
        }
        
        // Notifier le reporter
        Player reporter = plugin.getServer().getPlayer(report.getReporterId());
        if (reporter != null) {
            reporter.sendMessage("");
            reporter.sendMessage(ChatColor.YELLOW + "Votre report [" + reportId + "] a été traité:");
            reporter.sendMessage(ChatColor.GRAY + "Statut: " + newStatus.getDisplayName());
            if (note != null && !note.isEmpty()) {
                reporter.sendMessage(ChatColor.GRAY + "Note: " + ChatColor.WHITE + note);
            }
            reporter.sendMessage(ChatColor.GRAY + "Merci pour votre contribution!");
            reporter.sendMessage("");
        }
    }
    
    public List<PlayerReport> getPendingReports() {
        return reports.values().stream()
            .filter(r -> r.getStatus() == ReportStatus.PENDING || r.getStatus() == ReportStatus.INVESTIGATING)
            .sorted(Comparator.comparing(PlayerReport::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }
    
    public List<PlayerReport> getReportsByPlayer(UUID playerId) {
        return reports.values().stream()
            .filter(r -> r.getReportedId().equals(playerId))
            .sorted(Comparator.comparing(PlayerReport::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }
    
    public List<PlayerReport> getReportsByReporter(UUID reporterId) {
        return reports.values().stream()
            .filter(r -> r.getReporterId().equals(reporterId))
            .sorted(Comparator.comparing(PlayerReport::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }
    
    public PlayerReport getReport(String id) {
        return reports.get(id);
    }
    
    private boolean hasCooldown(UUID playerId) {
        Long lastReport = reportCooldowns.get(playerId);
        if (lastReport == null) return false;
        return System.currentTimeMillis() - lastReport < REPORT_COOLDOWN;
    }
    
    private long getRemainingCooldown(UUID playerId) {
        Long lastReport = reportCooldowns.get(playerId);
        if (lastReport == null) return 0;
        long elapsed = System.currentTimeMillis() - lastReport;
        return Math.max(0, REPORT_COOLDOWN - elapsed);
    }
}
