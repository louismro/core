package fr.louis.practice.commands.admin;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public ArenaCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande est réservée aux joueurs.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("practice.admin.arena")) {
            player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage("§cUtilisation: /arena create <nom>");
                    return true;
                }
                createArena(player, args[1]);
                break;
                
            case "addspawn":
                if (args.length < 2) {
                    player.sendMessage("§cUtilisation: /arena addspawn <nom>");
                    return true;
                }
                addSpawn(player, args[1]);
                break;
                
            case "setpos1":
                if (args.length < 2) {
                    player.sendMessage("§cUtilisation: /arena setpos1 <nom>");
                    return true;
                }
                setPos(player, args[1], 1);
                break;
                
            case "setpos2":
                if (args.length < 2) {
                    player.sendMessage("§cUtilisation: /arena setpos2 <nom>");
                    return true;
                }
                setPos(player, args[1], 2);
                break;
                
            case "save":
                plugin.getArenaManager().saveArenas();
                player.sendMessage("§aToutes les arènes ont été sauvegardées!");
                break;
                
            case "list":
                listArenas(player);
                break;
                
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§8§m                                    ");
        player.sendMessage("§6§lCOMMANDES ARENA");
        player.sendMessage("");
        player.sendMessage("§e/arena create <nom> §7- Créer une arène");
        player.sendMessage("§e/arena setpos1 <nom> §7- Définir le coin 1");
        player.sendMessage("§e/arena setpos2 <nom> §7- Définir le coin 2");
        player.sendMessage("§e/arena addspawn <nom> §7- Ajouter un spawn");
        player.sendMessage("§e/arena save §7- Sauvegarder les arènes");
        player.sendMessage("§e/arena list §7- Liste des arènes");
        player.sendMessage("§8§m                                    ");
    }
    
    private void createArena(Player player, String name) {
        if (plugin.getArenaManager().getArena(name) != null) {
            player.sendMessage("§cUne arène avec ce nom existe déjà!");
            return;
        }
        
        Location loc = player.getLocation();
        Arena arena = new Arena(name, loc, loc);
        plugin.getArenaManager().getArenas().put(name, arena);
        
        player.sendMessage("§aArène §e" + name + " §acréée!");
        player.sendMessage("§7Définissez maintenant pos1 et pos2, puis ajoutez des spawns.");
    }
    
    private void setPos(Player player, String name, int pos) {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) {
            player.sendMessage("§cArène introuvable!");
            return;
        }
        
        Location loc = player.getLocation();
        if (pos == 1) {
            arena.setPos1(loc);
            player.sendMessage("§aPosition 1 définie pour §e" + name + "§a!");
        } else {
            arena.setPos2(loc);
            player.sendMessage("§aPosition 2 définie pour §e" + name + "§a!");
        }
    }
    
    private void addSpawn(Player player, String name) {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) {
            player.sendMessage("§cArène introuvable!");
            return;
        }
        
        arena.addSpawn(player.getLocation());
        player.sendMessage("§aSpawn ajouté à §e" + name + " §7(" + arena.getSpawns().size() + " spawns)");
    }
    
    private void listArenas(Player player) {
        player.sendMessage("§8§m                                    ");
        player.sendMessage("§6§lARÈNES §7(" + plugin.getArenaManager().getArenas().size() + ")");
        player.sendMessage("");
        
        for (Arena arena : plugin.getArenaManager().getArenas().values()) {
            String status = arena.isInUse() ? "§c✗ En cours" : "§a✓ Libre";
            player.sendMessage("§e" + arena.getName() + " §7- " + status + 
                             " §7(" + arena.getSpawns().size() + " spawns)");
        }
        
        player.sendMessage("§8§m                                    ");
    }
}
