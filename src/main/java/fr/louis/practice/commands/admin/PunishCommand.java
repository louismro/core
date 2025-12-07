package fr.louis.practice.commands.admin;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Punishment.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PunishCommand implements CommandExecutor {
    private final PracticeCore plugin;
    private final PunishmentType type;
    
    public PunishCommand(PracticeCore plugin, PunishmentType type) {
        this.plugin = plugin;
        this.type = type;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.staff.punish")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <joueur> <raison> [durée]");
            sender.sendMessage(ChatColor.YELLOW + "Durées: 5m, 1h, 3h, 1d, 7d, 30d, perm");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Joueur introuvable!");
            return true;
        }
        
        if (target.hasPermission("practice.staff.punish")) {
            sender.sendMessage(ChatColor.RED + "Vous ne pouvez pas punir un staff!");
            return true;
        }
        
        // Parser la durée si spécifiée
        long durationMinutes = 0;
        String reason;
        
        if (args.length > 2 && isPermanentType()) {
            String durationStr = args[args.length - 1];
            Long parsed = parseDuration(durationStr);
            
            if (parsed != null) {
                durationMinutes = parsed;
                reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length - 1));
            } else {
                reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
            }
        } else {
            reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        }
        
        if (reason.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Vous devez spécifier une raison!");
            return true;
        }
        
        java.util.UUID issuerId = sender instanceof Player ? 
            ((Player) sender).getUniqueId() : 
            new java.util.UUID(0, 0); // Console
        
        plugin.getPunishmentManager().punish(target.getUniqueId(), issuerId, type, reason, durationMinutes);
        
        sender.sendMessage(ChatColor.GREEN + "✓ " + target.getName() + " a été " + 
            type.getDisplayName() + ChatColor.GREEN + "!");
        
        return true;
    }
    
    private boolean isPermanentType() {
        return type == PunishmentType.BAN || type == PunishmentType.TEMP_BAN || 
               type == PunishmentType.MUTE || type == PunishmentType.TEMP_MUTE;
    }
    
    private Long parseDuration(String duration) {
        if (duration.equalsIgnoreCase("perm") || duration.equalsIgnoreCase("permanent")) {
            return 0L;
        }
        
        try {
            String numberPart = duration.substring(0, duration.length() - 1);
            char unit = duration.charAt(duration.length() - 1);
            long value = Long.parseLong(numberPart);
            
            switch (Character.toLowerCase(unit)) {
                case 'm': return value;
                case 'h': return value * 60;
                case 'd': return value * 60 * 24;
                case 'w': return value * 60 * 24 * 7;
                default: return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
