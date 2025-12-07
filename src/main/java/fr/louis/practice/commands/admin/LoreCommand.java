package fr.louis.practice.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class LoreCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.lore")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCommande réservée aux joueurs.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage("§cUsage: /lore <add|set|clear> <texte>");
            return true;
        }
        
        @SuppressWarnings("deprecation")
        ItemStack item = player.getItemInHand();
        if (item.getType() == org.bukkit.Material.AIR) {
            player.sendMessage("§cVous devez tenir un item.");
            return true;
        }
        
        String action = args[0].toLowerCase();
        ItemMeta meta = item.getItemMeta();
        
        switch (action) {
            case "add" -> {
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /lore add <texte>");
                    return true;
                }
                
                String lineToAdd = buildText(args, 1);
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(lineToAdd);
                meta.setLore(lore);
                item.setItemMeta(meta);
                
                player.sendMessage("§a✓ Ligne ajoutée au lore");
            }
            case "set" -> {
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /lore set <texte>");
                    return true;
                }
                
                String fullLore = buildText(args, 1);
                List<String> newLore = Arrays.asList(fullLore.split("\\|"));
                meta.setLore(newLore);
                item.setItemMeta(meta);
                
                player.sendMessage("§a✓ Lore défini (utilisez | pour les lignes)");
            }
            case "clear" -> {
                meta.setLore(new ArrayList<>());
                item.setItemMeta(meta);
                player.sendMessage("§a✓ Lore effacé");
            }
            default -> {
                player.sendMessage("§cAction invalide: add, set ou clear");
                return true;
            }
        }
        
        return true;
    }
    
    private String buildText(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        return builder.toString().trim().replace("&", "§");
    }
}
