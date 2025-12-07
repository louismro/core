package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Punishment;
import fr.louis.practice.models.Punishment.PunishmentType;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PunishmentManager {
    private final PracticeCore plugin;
    
    @Getter
    private final Map<String, Punishment> punishments = new ConcurrentHashMap<>();
    
    @Getter
    private final Map<UUID, List<Punishment>> playerPunishments = new ConcurrentHashMap<>();
    
    public PunishmentManager(PracticeCore plugin) {
        this.plugin = plugin;
        startExpirationTask();
    }
    
    public void punish(UUID targetId, UUID issuerId, PunishmentType type, String reason, long durationMinutes) {
        Punishment punishment = new Punishment(targetId, issuerId, type, reason, durationMinutes);
        punishments.put(punishment.getId(), punishment);
        playerPunishments.computeIfAbsent(targetId, k -> new ArrayList<>()).add(punishment);
        
        Player target = plugin.getServer().getPlayer(targetId);
        Player issuer = plugin.getServer().getPlayer(issuerId);
        String issuerName = issuer != null ? issuer.getName() : "Console";
        String targetName = target != null ? target.getName() : "Inconnu";
        
        // Appliquer la punition
        switch (type) {
            case BAN, TEMP_BAN -> {
                if (target != null) {
                    String kickMessage = ChatColor.RED + "═══════════════════════════\n" +
                        ChatColor.DARK_RED + "✖ VOUS ÊTES BANNI ✖\n" +
                        ChatColor.RED + "═══════════════════════════\n\n" +
                        ChatColor.GRAY + "Raison: " + ChatColor.WHITE + reason + "\n" +
                        ChatColor.GRAY + "Durée: " + ChatColor.WHITE + punishment.getFormattedDuration() + "\n" +
                        ChatColor.GRAY + "Par: " + ChatColor.WHITE + issuerName + "\n\n" +
                        ChatColor.YELLOW + "Faire appel sur discord.gg/practice";
                    target.kickPlayer(kickMessage);
                }
                broadcastPunishment(targetName, type, reason, issuerName);
            }
            case MUTE, TEMP_MUTE -> {
                if (target != null) {
                    target.sendMessage("");
                    target.sendMessage(ChatColor.RED + "═══════════════════════════");
                    target.sendMessage(ChatColor.YELLOW + "Vous avez été mute!");
                    target.sendMessage(ChatColor.GRAY + "Raison: " + ChatColor.WHITE + reason);
                    target.sendMessage(ChatColor.GRAY + "Durée: " + ChatColor.WHITE + punishment.getFormattedDuration());
                    target.sendMessage(ChatColor.GRAY + "Par: " + ChatColor.WHITE + issuerName);
                    target.sendMessage(ChatColor.RED + "═══════════════════════════");
                    target.sendMessage("");
                }
                broadcastStaff(targetName + " a été mute par " + issuerName + " (" + reason + ")");
            }
            case KICK -> {
                if (target != null) {
                    String kickMessage = ChatColor.RED + "═══════════════════════════\n" +
                        ChatColor.YELLOW + "⚠ KICK ⚠\n" +
                        ChatColor.RED + "═══════════════════════════\n\n" +
                        ChatColor.GRAY + "Raison: " + ChatColor.WHITE + reason + "\n" +
                        ChatColor.GRAY + "Par: " + ChatColor.WHITE + issuerName;
                    target.kickPlayer(kickMessage);
                }
                broadcastPunishment(targetName, type, reason, issuerName);
            }
            case WARNING -> {
                if (target != null) {
                    target.sendMessage("");
                    target.sendMessage(ChatColor.RED + "═══════════════════════════");
                    target.sendMessage(ChatColor.YELLOW + "⚡ AVERTISSEMENT ⚡");
                    target.sendMessage(ChatColor.RED + "═══════════════════════════");
                    target.sendMessage("");
                    target.sendMessage(ChatColor.GRAY + "Raison: " + ChatColor.WHITE + reason);
                    target.sendMessage(ChatColor.GRAY + "Par: " + ChatColor.WHITE + issuerName);
                    target.sendMessage("");
                    target.sendMessage(ChatColor.YELLOW + "Prochaine infraction = sanction!");
                    target.sendMessage(ChatColor.RED + "═══════════════════════════");
                    target.sendMessage("");
                }
                broadcastStaff(targetName + " a reçu un avertissement de " + issuerName + " (" + reason + ")");
            }
        }
    }
    
    public boolean isBanned(UUID playerId) {
        List<Punishment> bans = getActivePunishments(playerId, PunishmentType.BAN, PunishmentType.TEMP_BAN);
        return !bans.isEmpty();
    }
    
    public boolean isMuted(UUID playerId) {
        List<Punishment> mutes = getActivePunishments(playerId, PunishmentType.MUTE, PunishmentType.TEMP_MUTE);
        return !mutes.isEmpty();
    }
    
    public Punishment getActiveBan(UUID playerId) {
        List<Punishment> bans = getActivePunishments(playerId, PunishmentType.BAN, PunishmentType.TEMP_BAN);
        return bans.isEmpty() ? null : bans.get(0);
    }
    
    public Punishment getActiveMute(UUID playerId) {
        List<Punishment> mutes = getActivePunishments(playerId, PunishmentType.MUTE, PunishmentType.TEMP_MUTE);
        return mutes.isEmpty() ? null : mutes.get(0);
    }
    
    public List<Punishment> getActivePunishments(UUID playerId, PunishmentType... types) {
        List<Punishment> playerPuns = playerPunishments.get(playerId);
        if (playerPuns == null) return Collections.emptyList();
        
        Set<PunishmentType> typeSet = new HashSet<>(Arrays.asList(types));
        
        return playerPuns.stream()
            .filter(p -> p.isActive() && !p.isExpired())
            .filter(p -> typeSet.contains(p.getType()))
            .sorted(Comparator.comparing(Punishment::getIssuedAt).reversed())
            .collect(Collectors.toList());
    }
    
    public List<Punishment> getPunishmentHistory(UUID playerId) {
        List<Punishment> playerPuns = playerPunishments.get(playerId);
        if (playerPuns == null) return Collections.emptyList();
        
        return playerPuns.stream()
            .sorted(Comparator.comparing(Punishment::getIssuedAt).reversed())
            .collect(Collectors.toList());
    }
    
    public void removePunishment(String punishmentId, UUID removedBy) {
        Punishment punishment = punishments.get(punishmentId);
        if (punishment == null) return;
        
        punishment.setActive(false);
        punishment.setUnbannedBy(removedBy);
        punishment.setUnbannedAt(java.time.LocalDateTime.now());
        
        Player target = plugin.getServer().getPlayer(punishment.getTargetId());
        if (target != null) {
            target.sendMessage(ChatColor.GREEN + "✓ Votre " + punishment.getType().getDisplayName() + 
                ChatColor.GREEN + " a été retiré!");
        }
    }
    
    private void broadcastPunishment(String targetName, PunishmentType type, String reason, String issuer) {
        String message = ChatColor.RED + "⚠ " + ChatColor.YELLOW + targetName + 
            ChatColor.GRAY + " a été " + type.getDisplayName() + 
            ChatColor.GRAY + " par " + ChatColor.WHITE + issuer + 
            ChatColor.GRAY + " (" + reason + ")";
        
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }
    
    private void broadcastStaff(String message) {
        String formattedMessage = ChatColor.DARK_RED + "[STAFF] " + ChatColor.GRAY + message;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission("practice.staff")) {
                player.sendMessage(formattedMessage);
            }
        }
    }
    
    private void startExpirationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Punishment punishment : punishments.values()) {
                    if (punishment.isActive() && punishment.isExpired()) {
                        punishment.setActive(false);
                        
                        Player target = plugin.getServer().getPlayer(punishment.getTargetId());
                        if (target != null) {
                            target.sendMessage(ChatColor.GREEN + "✓ Votre " + 
                                punishment.getType().getDisplayName() + 
                                ChatColor.GREEN + " a expiré!");
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 20L); // Chaque seconde
    }
}
