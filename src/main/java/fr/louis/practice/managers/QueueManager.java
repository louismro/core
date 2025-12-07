package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Match;
import fr.louis.practice.models.MatchTeam;
import fr.louis.practice.models.MatchType;
import fr.louis.practice.models.PlayerState;
import fr.louis.practice.models.PracticePlayer;
import fr.louis.practice.models.Queue; // Import explicite pour éviter la confusion avec java.util.Queue
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class QueueManager {
    private final PracticeCore plugin;
    private final Map<String, Queue> queues;
    private BukkitTask matchmakingTask;
    
    public QueueManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.queues = new ConcurrentHashMap<>();
        loadQueues();
        startMatchmaking();
    }
    
    private void loadQueues() {
        org.bukkit.configuration.ConfigurationSection section = plugin.getConfig().getConfigurationSection("queues");
        if (section == null) return;
        
        for (String queueName : section.getKeys(false)) {
            String path = "queues." + queueName;
            String kitName = plugin.getConfig().getString(path + ".kit");
            boolean ranked = plugin.getConfig().getBoolean(path + ".ranked");
            boolean build = plugin.getConfig().getBoolean(path + ".build");
            
            Queue queue = new Queue(queueName, kitName, ranked, build);
            queue.setDisplayName(plugin.getConfig().getString(path + ".display-name", queueName));
            queue.setIcon(plugin.getConfig().getString(path + ".icon", "DIAMOND_SWORD"));
            queue.setEloEnabled(plugin.getConfig().getBoolean(path + ".elo-enabled", ranked));
            
            queues.put(queueName, queue);
        }
    }
    
    public void addToQueue(Player player, String queueName) {
        addToQueue(player, queueName, true);
    }
    
    public void addToQueue(Player player, String kitName, boolean ranked) {
        // Trouver la queue correspondant au kit et au mode ranked
        Queue queue = null;
        for (Queue q : queues.values()) {
            if (q.getKitName().equals(kitName) && q.isRanked() == ranked) {
                queue = q;
                break;
            }
        }
        
        if (queue == null) {
            player.sendMessage("§cCette queue n'existe pas!");
            player.sendMessage("§7Kit: " + kitName + ", Ranked: " + ranked);
            return;
        }
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getOrCreate(player);
        
        if (practicePlayer.isInQueue()) {
            player.sendMessage("§cVous êtes déjà en queue!");
            return;
        }
        
        if (practicePlayer.isInMatch()) {
            player.sendMessage("§cVous êtes déjà en match!");
            return;
        }
        
        int elo = (queue.isEloEnabled() && ranked) ? practicePlayer.getElo(queue.getKitName()) : 1000;
        queue.addPlayer(player.getUniqueId(), elo);
        
        practicePlayer.setState(PlayerState.QUEUE);
        practicePlayer.setQueue(queue);
        
        player.sendMessage("§aVous avez rejoint la queue " + 
            ChatColor.translateAlternateColorCodes('&', queue.getDisplayName()));
        
        plugin.getInventoryManager().giveQueueItems(player);
        plugin.getScoreboardManager().updateQueue(player);
    }
    
    public void removeFromQueue(Player player) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        if (practicePlayer == null || !practicePlayer.isInQueue()) {
            return;
        }
        
        Queue queue = practicePlayer.getQueue();
        queue.removePlayer(player.getUniqueId());
        
        practicePlayer.setState(PlayerState.SPAWN);
        practicePlayer.setQueue(null);
        
        player.sendMessage("§cVous avez quitté la queue");
        
        plugin.getInventoryManager().giveSpawnItems(player);
        plugin.getScoreboardManager().updateSpawn(player);
    }
    
    private void startMatchmaking() {
        matchmakingTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Queue queue : queues.values()) {
                processQueue(queue);
            }
        }, 20L, 20L); // Toutes les secondes
    }
    
    private void processQueue(Queue queue) {
        List<Queue.QueueEntry> entries = queue.getEntries();
        if (entries.size() < 2) {
            return;
        }
        
        // Augmenter le range de recherche pour les joueurs qui attendent longtemps
        for (Queue.QueueEntry entry : entries) {
            if (entry.getSearchTime() >= 10) { // Après 10 secondes
                entry.increaseRange(50);
            }
        }
        
        // Trouver des matchs
        List<Queue.QueueEntry> processed = new ArrayList<>();
        
        for (int i = 0; i < entries.size(); i++) {
            Queue.QueueEntry entry1 = entries.get(i);
            if (processed.contains(entry1)) continue;
            
            for (int j = i + 1; j < entries.size(); j++) {
                Queue.QueueEntry entry2 = entries.get(j);
                if (processed.contains(entry2)) continue;
                
                if (isMatchable(entry1, entry2)) {
                    createMatch(queue, entry1, entry2);
                    processed.add(entry1);
                    processed.add(entry2);
                    break;
                }
            }
        }
    }
    
    private boolean isMatchable(Queue.QueueEntry entry1, Queue.QueueEntry entry2) {
        int eloDiff = Math.abs(entry1.getElo() - entry2.getElo());
        int maxRange = Math.max(entry1.getSearchRange(), entry2.getSearchRange());
        
        return eloDiff <= maxRange;
    }
    
    private void createMatch(Queue queue, Queue.QueueEntry entry1, Queue.QueueEntry entry2) {
        Player player1 = Bukkit.getPlayer(entry1.getPlayer());
        Player player2 = Bukkit.getPlayer(entry2.getPlayer());
        
        if (player1 == null || player2 == null) {
            queue.removePlayer(entry1.getPlayer());
            queue.removePlayer(entry2.getPlayer());
            return;
        }
        
        // Retirer de la queue
        queue.removePlayer(entry1.getPlayer());
        queue.removePlayer(entry2.getPlayer());
        
        PracticePlayer pp1 = plugin.getPlayerManager().getPlayer(entry1.getPlayer());
        PracticePlayer pp2 = plugin.getPlayerManager().getPlayer(entry2.getPlayer());
        
        pp1.setQueue(null);
        pp2.setQueue(null);
        
        // Créer le match
        Match match = plugin.getMatchManager().createMatch(
            MatchType.SOLO, 
            queue.getKitName(), 
            queue.isRanked(), 
            queue.isBuild()
        );
        
        if (match == null) {
            player1.sendMessage("§cErreur: Aucune arène disponible!");
            player2.sendMessage("§cErreur: Aucune arène disponible!");
            pp1.setState(PlayerState.SPAWN);
            pp2.setState(PlayerState.SPAWN);
            return;
        }
        
        // Créer les équipes
        MatchTeam team1 = new MatchTeam(player1.getName());
        team1.addPlayer(entry1.getPlayer());
        
        MatchTeam team2 = new MatchTeam(player2.getName());
        team2.addPlayer(entry2.getPlayer());
        
        match.addTeam(team1);
        match.addTeam(team2);
        
        // Notifier les joueurs
        player1.sendMessage("§aMatch trouvé! §7(vs §e" + player2.getName() + "§7)");
        player2.sendMessage("§aMatch trouvé! §7(vs §e" + player1.getName() + "§7)");
        
        // Démarrer le match immédiatement (sans countdown)
        plugin.getMatchManager().startMatch(match);
    }
    
    public Queue getQueue(String name) {
        return queues.get(name);
    }
    
    public Collection<Queue> getAllQueues() {
        return queues.values();
    }
    
    public int getQueuedCount() {
        int count = 0;
        for (Queue queue : queues.values()) {
            count += queue.getEntries().size();
        }
        return count;
    }
    
    public void shutdown() {
        if (matchmakingTask != null) {
            matchmakingTask.cancel();
        }
    }
}
