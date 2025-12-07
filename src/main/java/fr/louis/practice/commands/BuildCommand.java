package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BuildCommand implements CommandExecutor {
    private final Set<UUID> buildMode = new HashSet<>();
    
    public BuildCommand(PracticeCore plugin) {
        // Plugin parameter kept for API compatibility
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (sender != null) sender.sendMessage("§cCette commande ne peut être exécutée que par un joueur!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("practice.build") && !player.isOp()) {
            player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande!");
            return true;
        }
        
        UUID uuid = player.getUniqueId();
        
        if (buildMode.contains(uuid)) {
            buildMode.remove(uuid);
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage("§cMode build désactivé!");
        } else {
            buildMode.add(uuid);
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage("§aMode build activé!");
        }
        
        return true;
    }
    
    public boolean isInBuildMode(Player player) {
        return buildMode.contains(player.getUniqueId());
    }
    
    public void removeBuildMode(Player player) {
        buildMode.remove(player.getUniqueId());
    }
}
