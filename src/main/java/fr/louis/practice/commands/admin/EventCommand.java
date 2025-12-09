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
            case "start" -> {
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /event start <type> <durée minutes>");
                    return true;
                }
                
                GlobalEvent.EventType type = switch (args[1].toLowerCase()) {
                    case "doublexp" -> GlobalEvent.EventType.DOUBLE_XP;
                    case "doublecoins" -> GlobalEvent.EventType.DOUBLE_COINS;
                    case "triplexp" -> GlobalEvent.EventType.TRIPLE_XP;
                    case "happyhour" -> GlobalEvent.EventType.HAPPY_HOUR;
                    case "weekend" -> GlobalEvent.EventType.WEEKEND_BOOST;
                    case "special" -> GlobalEvent.EventType.SPECIAL;
                    default -> {
                        sender.sendMessage("§cType d'événement invalide.");
                        yield null;
                    }
                };
                if (type == null) return true;
                
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
            }
                
            case "list" -> {
                var events = plugin.getGlobalEventManager().getActiveEvents();
                
                if (events.isEmpty()) {
                    sender.sendMessage("§cAucun événement actif.");
                    return true;
                }
                
                sender.sendMessage("§6§lÉvénements actifs:");
                for (GlobalEvent e : events) {
                    sender.sendMessage("§7- " + e.getType().getDisplayName() + " §e" + e.getTimeRemainingFormatted());
                }
            }
                
            case "end" -> {
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /event end <type>");
                    return true;
                }
                
                GlobalEvent.EventType endType = switch (args[1].toLowerCase()) {
                    case "doublexp" -> GlobalEvent.EventType.DOUBLE_XP;
                    case "doublecoins" -> GlobalEvent.EventType.DOUBLE_COINS;
                    case "triplexp" -> GlobalEvent.EventType.TRIPLE_XP;
                    case "happyhour" -> GlobalEvent.EventType.HAPPY_HOUR;
                    case "weekend" -> GlobalEvent.EventType.WEEKEND_BOOST;
                    case "special" -> GlobalEvent.EventType.SPECIAL;
                    default -> {
                        sender.sendMessage("§cType d'événement invalide.");
                        yield null;
                    }
                };
                if (endType == null) return true;
                
                var activeEvents = plugin.getGlobalEventManager().getActiveEvents();
                boolean found = false;
                for (GlobalEvent evt : activeEvents) {
                    if (evt.getType() == endType) {
                        plugin.getGlobalEventManager().endEvent(evt.getId());
                        found = true;
                    }
                }
                
                if (found) {
                    sender.sendMessage("§a✓ Événements de type " + endType.getDisplayName() + " terminés");
                } else {
                    sender.sendMessage("§cAucun événement actif de ce type.");
                }
            }
                
            default -> sender.sendMessage("§cAction invalide: start, end, list");
        }
        
        return true;
    }
}
