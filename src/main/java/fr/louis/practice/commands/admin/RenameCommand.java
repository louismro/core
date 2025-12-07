package fr.louis.practice.commands.admin;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.rename")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCommande réservée aux joueurs.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage("§cUsage: /rename <nom>");
            return true;
        }
        
        @SuppressWarnings("deprecation")
        ItemStack item = player.getItemInHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage("§cVous devez tenir un item.");
            return true;
        }
        
        // Build name with spaces and color codes
        StringBuilder nameBuilder = new StringBuilder();
        for (String arg : args) {
            nameBuilder.append(arg).append(" ");
        }
        String newName = nameBuilder.toString().trim().replace("&", "§");
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(newName);
            item.setItemMeta(meta);
        }
        
        player.sendMessage("§a✓ Item renommé: " + newName);
        
        return true;
    }
}
