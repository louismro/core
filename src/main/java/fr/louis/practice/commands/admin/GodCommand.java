package fr.louis.practice.commands.admin;

import fr.louis.practice.PracticeCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class GodCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public GodCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.god")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        Player target;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cVous devez spécifier un joueur.");
                return true;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cJoueur introuvable.");
                return true;
            }
        }
        
        // Toggle god mode using metadata (1.8.8 compatible)
        boolean god = !isGodMode(target);
        setGodMode(target, god);
        
        String status = god ? "§aactivé" : "§cdésactivé";
        if (target == sender) {
            target.sendMessage("§eMode dieu " + status + "§e.");
        } else {
            sender.sendMessage("§eMode dieu " + status + " §epour §6" + target.getName() + "§e.");
            target.sendMessage("§eVotre mode dieu a été " + status + "§e.");
        }
        
        return true;
    }
    
    private boolean isGodMode(Player player) {
        return player.hasMetadata("god") && player.getMetadata("god").get(0).asBoolean();
    }
    
    private void setGodMode(Player player, boolean god) {
        if (god) {
            player.setMetadata("god", new FixedMetadataValue(plugin, true));
        } else {
            player.removeMetadata("god", plugin);
        }
    }
}
