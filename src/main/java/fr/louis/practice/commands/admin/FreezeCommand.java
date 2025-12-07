package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.freeze")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /freeze <joueur>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }
        
        // Toggle freeze using metadata
        boolean frozen = target.hasMetadata("frozen") && target.getMetadata("frozen").get(0).asBoolean();
        
        if (frozen) {
            target.removeMetadata("frozen", Bukkit.getPluginManager().getPlugin("PracticeCore"));
            target.setWalkSpeed(0.2f);
            target.setFlySpeed(0.1f);
            
            target.sendMessage("");
            target.sendMessage("§a§l✓ Vous avez été défreeze");
            target.sendMessage("");
            
            sender.sendMessage("§a✓ " + target.getName() + " a été défreeze");
        } else {
            target.setMetadata("frozen", new org.bukkit.metadata.FixedMetadataValue(
                Bukkit.getPluginManager().getPlugin("PracticeCore"), true));
            target.setWalkSpeed(0.0f);
            target.setFlySpeed(0.0f);
            
            target.sendMessage("");
            target.sendMessage("§c§l⚠ VOUS AVEZ ÉTÉ FREEZE");
            target.sendMessage("§7Vous ne pouvez plus bouger.");
            target.sendMessage("§7Contactez le staff sur Discord.");
            target.sendMessage("§c§lDéconnexion = BAN PERMANENT");
            target.sendMessage("");
            
            sender.sendMessage("§a✓ " + target.getName() + " a été freeze");
        }
        
        return true;
    }
}
