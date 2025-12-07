package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.give")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /give <joueur> <item> [quantité]");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }
        
        Material material = Material.matchMaterial(args[1].toUpperCase());
        if (material == null) {
            sender.sendMessage("§cItem invalide: " + args[1]);
            return true;
        }
        
        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount < 1 || amount > 64) {
                    sender.sendMessage("§cLa quantité doit être entre 1 et 64.");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cQuantité invalide.");
                return true;
            }
        }
        
        ItemStack item = new ItemStack(material, amount);
        target.getInventory().addItem(item);
        
        sender.sendMessage("§a✓ " + amount + "x " + material.name() + " donné à " + target.getName());
        target.sendMessage("§eVous avez reçu §f" + amount + "x " + material.name());
        
        return true;
    }
}
