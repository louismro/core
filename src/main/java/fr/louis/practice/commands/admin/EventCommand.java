package fr.louis.practice.commands.admin;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.GlobalEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class EventCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public EventCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.event")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /event <start|end|list> [type] [durée]");
            sender.sendMessage("§7Types: doublexp, doublecoins, triplexp, happyhour, weekend, special");
            return true;
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "start":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /event start <type> <durée minutes>");
                    return true;
                }
                
                GlobalEvent.EventType type;
                try {
                    switch (args[1].toLowerCase()) {
                        case "doublexp": type = GlobalEvent.EventType.DOUBLE_XP; break;
                        case "doublecoins": type = GlobalEvent.EventType.DOUBLE_COINS; break;
                        case "triplexp": type = GlobalEvent.EventType.TRIPLE_XP; break;
                        case "happyhour": type = GlobalEvent.EventType.HAPPY_HOUR; break;
                        case "weekend": type = GlobalEvent.EventType.WEEKEND_BOOST; break;
                        case "special": type = GlobalEvent.EventType.SPECIAL; break;
                        default:
                            sender.sendMessage("§cType d'événement invalide.");
                            return true;
                    }
                } catch (Exception e) {
                    sender.sendMessage("§cType d'événement invalide.");
                    return true;
                }
                
                int duration;
                try {
                    duration = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cDurée invalide.");
                    return true;
                }
                
                GlobalEvent event = new GlobalEvent(
                    UUID.randomUUID().toString(),
                    type,
                    type.getDisplayName(),
                    "Événement spécial du serveur",
                    duration
                );
                
                plugin.getGlobalEventManager().startEvent(event);
                sender.sendMessage("§a✓ Événement démarré pour " + duration + " minutes");
                break;
                
            case "list":
                var events = plugin.getGlobalEventManager().getActiveEvents();
                
                if (events.isEmpty()) {
                    sender.sendMessage("§cAucun événement actif.");
                    return true;
                }
                
                sender.sendMessage("§6§lÉvénements actifs:");
                for (GlobalEvent e : events) {
                    sender.sendMessage("§7- " + e.getType().getDisplayName() + " §e" + e.getTimeRemainingFormatted());
                }
                break;
                
            case "end":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /event end <id>");
                    return true;
                }
                
                // For simplicity, end all events of a type
                GlobalEvent.EventType endType;
                try {
                    switch (args[1].toLowerCase()) {
                        case "doublexp": endType = GlobalEvent.EventType.DOUBLE_XP; break;
                        case "doublecoins": endType = GlobalEvent.EventType.DOUBLE_COINS; break;
                        case "triplexp": endType = GlobalEvent.EventType.TRIPLE_XP; break;
                        case "happyhour": endType = GlobalEvent.EventType.HAPPY_HOUR; break;
                        case "weekend": endType = GlobalEvent.EventType.WEEKEND_BOOST; break;
                        case "special": endType = GlobalEvent.EventType.SPECIAL; break;
                        default:
                            sender.sendMessage("§cType d'événement invalide.");
                            return true;
                    }
                } catch (Exception e) {
                    sender.sendMessage("§cType d'événement invalide.");
                    return true;
                }
                
                plugin.getGlobalEventManager().getActiveEvents().stream()
                    .filter(e -> e.getType() == endType)
                    .forEach(e -> plugin.getGlobalEventManager().endEvent(e.getId()));
                
                sender.sendMessage("§a✓ Événement terminé");
                break;
                
            default:
                sender.sendMessage("§cAction invalide: start, end, list");
                break;
        }
        
        return true;
    }
}
