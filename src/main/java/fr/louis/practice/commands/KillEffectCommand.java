package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.KillEffect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class KillEffectCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public KillEffectCommand(PracticeCore plugin) {
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
            showEffectMenu(player);
            return true;
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "list":
                showAvailableEffects(player);
                break;
                
            case "set":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /killeffect set <id>");
                    return true;
                }
                
                String effectId = args[1].toLowerCase();
                
                if (!plugin.getKillEffectManager().hasUnlocked(player.getUniqueId(), effectId)) {
                    player.sendMessage("§cVous n'avez pas débloqué cet effet.");
                    return true;
                }
                
                plugin.getKillEffectManager().setActiveEffect(player.getUniqueId(), effectId);
                KillEffect effect = plugin.getKillEffectManager().getEffect(effectId);
                player.sendMessage("§a✓ Effet de kill activé: " + effect.getDisplayName());
                break;
                
            case "buy":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /killeffect buy <id>");
                    return true;
                }
                
                plugin.getKillEffectManager().purchaseEffect(player, args[1].toLowerCase());
                break;
                
            case "remove":
            case "off":
                plugin.getKillEffectManager().setActiveEffect(player.getUniqueId(), null);
                player.sendMessage("§c✗ Effet de kill désactivé");
                break;
                
            default:
                showEffectMenu(player);
                break;
        }
        
        return true;
    }
    
    private void showEffectMenu(Player player) {
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§e§l    EFFETS DE KILL");
        player.sendMessage("");
        player.sendMessage("§e/killeffect list §7- Voir les effets");
        player.sendMessage("§e/killeffect set <id> §7- Activer un effet");
        player.sendMessage("§e/killeffect buy <id> §7- Acheter un effet");
        player.sendMessage("§e/killeffect off §7- Désactiver");
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    private void showAvailableEffects(Player player) {
        List<KillEffect> effects = plugin.getKillEffectManager().getAvailableEffects(player);
        
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§e§l   EFFETS DISPONIBLES");
        player.sendMessage("");
        
        String activeId = plugin.getKillEffectManager().getActiveEffect(player.getUniqueId());
        
        for (KillEffect effect : effects) {
            boolean unlocked = plugin.getKillEffectManager().hasUnlocked(player.getUniqueId(), effect.getId());
            boolean active = effect.getId().equals(activeId);
            
            String status;
            if (active) {
                status = "§a✓ ACTIF";
            } else if (unlocked) {
                status = "§7✓ Possédé";
            } else if (effect.getCoinsPrice() == 0) {
                status = "§eGRATUIT";
            } else {
                status = "§6" + effect.getCoinsPrice() + " coins";
            }
            
            player.sendMessage(" " + effect.getType().getDisplayName() + 
                " §8(§7" + effect.getId() + "§8) §7- " + status);
        }
        
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
