package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.*;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PlayerManager {
    private final PracticeCore plugin;
    private final Map<UUID, PracticePlayer> players;
    
    public PlayerManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.players = new ConcurrentHashMap<>();
    }
    
    public PracticePlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }
    
    public PracticePlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }
    
    public PracticePlayer getOrCreate(Player player) {
        return players.computeIfAbsent(player.getUniqueId(), 
            uuid -> new PracticePlayer(uuid, player.getName()));
    }
    
    public void addPlayer(PracticePlayer player) {
        players.put(player.getUuid(), player);
    }
    
    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }
    
    public Collection<PracticePlayer> getAllPlayers() {
        return players.values();
    }
    
    public List<PracticePlayer> getPlayersInState(PlayerState state) {
        List<PracticePlayer> result = new ArrayList<>();
        for (PracticePlayer player : players.values()) {
            if (player.getState() == state) {
                result.add(player);
            }
        }
        return result;
    }
    
    public int getOnlineCount() {
        return players.size();
    }
    
    public int getFightingCount() {
        return getPlayersInState(PlayerState.MATCH).size();
    }
    
    public void saveAll() {
        for (PracticePlayer player : players.values()) {
            plugin.getMongoManager().savePlayer(player);
        }
    }
}
