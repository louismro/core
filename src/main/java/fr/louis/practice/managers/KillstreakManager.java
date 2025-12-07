package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Killstreak;
import fr.louis.practice.models.PracticePlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class KillstreakManager {
    private final PracticeCore plugin;
    private final Map<UUID, Killstreak> lastAnnounced;
    
    public KillstreakManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.lastAnnounced = new HashMap<>();
    }
    
    public void handleKill(Player killer) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(killer.getUniqueId());
        if (practicePlayer == null) {
            return;
        }
        int killstreak = practicePlayer.getCurrentStreak();
        
        Killstreak current = Killstreak.getByKills(killstreak);
        Killstreak last = lastAnnounced.get(killer.getUniqueId());
        
        // Announce uniquement si nouveau palier
        if (current != null && current != last) {
            announceKillstreak(killer, current);
            lastAnnounced.put(killer.getUniqueId(), current);
        }
    }
    
    @SuppressWarnings("deprecation")
    private void announceKillstreak(Player player, Killstreak streak) {
        String message = streak.getFormattedMessage(player.getName());
        
        // Broadcast à tous les joueurs
        Bukkit.broadcastMessage(message);
        
        // Son pour le joueur
        var location = player.getLocation();
        if (location != null) {
            player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
        
        // Title pour le joueur
        String title = streak.name().replace("_", " ");
        player.sendTitle("§6§l" + title, "§e" + streak.getKills() + " Kills");
    }
    
    public void resetKillstreak(Player player) {
        lastAnnounced.remove(player.getUniqueId());
    }
}
