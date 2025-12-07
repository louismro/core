package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.HitSound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HitSoundCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public HitSoundCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCommande réservée aux joueurs.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showSoundMenu(player);
            return true;
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "list":
                showAvailableSounds(player);
                break;
                
            case "set":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /hitsound set <id>");
                    return true;
                }
                
                String soundId = args[1].toLowerCase();
                
                if (!plugin.getHitSoundManager().hasUnlocked(player.getUniqueId(), soundId)) {
                    player.sendMessage("§cVous n'avez pas débloqué ce son.");
                    return true;
                }
                
                plugin.getHitSoundManager().setActiveSound(player.getUniqueId(), soundId);
                HitSound sound = plugin.getHitSoundManager().getSound(soundId);
                player.sendMessage("§a✓ Son de hit activé: " + sound.getDisplayName());
                
                // Play preview
                plugin.getHitSoundManager().playHitSound(player);
                break;
                
            case "buy":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /hitsound buy <id>");
                    return true;
                }
                
                plugin.getHitSoundManager().purchaseSound(player, args[1].toLowerCase());
                break;
                
            default:
                showSoundMenu(player);
                break;
        }
        
        return true;
    }
    
    private void showSoundMenu(Player player) {
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§e§l     SONS DE HIT");
        player.sendMessage("");
        player.sendMessage("§e/hitsound list §7- Voir les sons");
        player.sendMessage("§e/hitsound set <id> §7- Activer un son");
        player.sendMessage("§e/hitsound buy <id> §7- Acheter un son");
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    private void showAvailableSounds(Player player) {
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§e§l    SONS DISPONIBLES");
        player.sendMessage("");
        
        for (HitSound sound : plugin.getHitSoundManager().getAllSounds()) {
            boolean unlocked = plugin.getHitSoundManager().hasUnlocked(player.getUniqueId(), sound.getId());
            
            String status = unlocked ? 
                "§a✓ Possédé" : 
                (sound.getCoinsPrice() == 0 ? "§eGRATUIT" : "§6" + sound.getCoinsPrice() + " coins");
            
            player.sendMessage(" " + sound.getDisplayName() + 
                " §8(§7" + sound.getId() + "§8) §7- " + status);
        }
        
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
