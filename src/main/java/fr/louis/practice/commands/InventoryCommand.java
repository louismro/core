package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.managers.InventorySnapshotManager.InventorySnapshot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InventoryCommand implements CommandExecutor {
    private final PracticeCore plugin;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    public InventoryCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande est réservée aux joueurs.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage("§cUtilisation: /inventory <joueur>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            // Chercher par nom dans les snapshots récents
            InventorySnapshot snapshot = findSnapshotByName(args[0]);
            if (snapshot == null) {
                player.sendMessage("§cAucun inventaire trouvé pour ce joueur.");
                return true;
            }
            openSnapshot(player, snapshot);
        } else {
            // Snapshot le plus récent du joueur
            InventorySnapshot snapshot = plugin.getInventorySnapshotManager().getLastSnapshot(target.getUniqueId());
            if (snapshot == null) {
                player.sendMessage("§cAucun inventaire trouvé pour ce joueur.");
                return true;
            }
            openSnapshot(player, snapshot);
        }
        
        return true;
    }
    
    private InventorySnapshot findSnapshotByName(String name) {
        for (var entry : plugin.getInventorySnapshotManager().getSnapshots().entrySet()) {
            for (InventorySnapshot snapshot : entry.getValue()) {
                if (snapshot.getPlayerName().equalsIgnoreCase(name)) {
                    return snapshot;
                }
            }
        }
        return null;
    }
    
    private void openSnapshot(Player viewer, InventorySnapshot snapshot) {
        String time = dateFormat.format(new Date(snapshot.getTimestamp()));
        Inventory inv = Bukkit.createInventory(null, 54, 
            "§8" + snapshot.getPlayerName() + " §7(" + time + ")");
        
        // Inventaire (lignes 0-3)
        for (int i = 0; i < snapshot.getContents().length && i < 36; i++) {
            inv.setItem(i, snapshot.getContents()[i]);
        }
        
        // Armure (ligne 4)
        ItemStack[] armor = snapshot.getArmor();
        if (armor[3] != null) inv.setItem(36, armor[3]); // Casque
        if (armor[2] != null) inv.setItem(37, armor[2]); // Plastron
        if (armor[1] != null) inv.setItem(38, armor[1]); // Jambi\u00e8res
        if (armor[0] != null) inv.setItem(39, armor[0]); // Bottes
        
        // Stats (ligne 5)
        ItemStack stats = new ItemStack(Material.PAPER);
        org.bukkit.inventory.meta.ItemMeta meta = stats.getItemMeta();
        meta.setDisplayName("§6§lSTATISTIQUES");
        meta.setLore(java.util.Arrays.asList(
            "§7Adversaire: §e" + snapshot.getOpponentName(),
            "§7Vie: §c" + String.format("%.1f", snapshot.getHealth()) + "❤",
            "§7Faim: §6" + snapshot.getFood() + "/20",
            "§7Potions restantes: §d" + snapshot.getPotionCount()
        ));
        stats.setItemMeta(meta);
        inv.setItem(49, stats);
        
        viewer.openInventory(inv);
        viewer.sendMessage("§aAffichage de l'inventaire de §e" + snapshot.getPlayerName());
    }
}
