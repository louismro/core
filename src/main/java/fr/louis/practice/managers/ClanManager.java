package fr.louis.practice.managers;

import com.mongodb.client.model.Filters;
import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Clan;
import lombok.Getter;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter
public class ClanManager {
    private final PracticeCore plugin;
    private final Map<UUID, Clan> clans;
    private final Map<UUID, UUID> playerToClan; // PlayerUUID -> ClanUUID
    
    public ClanManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.clans = new HashMap<>();
        this.playerToClan = new HashMap<>();
        loadAllClans();
    }
    
    private void loadAllClans() {
        if (!plugin.getMongoManager().isConnected()) return;
        
        CompletableFuture.runAsync(() -> {
            try {
                for (Document doc : plugin.getMongoManager().getClansCollection().find()) {
                    Clan clan = documentToClan(doc);
                    clans.put(clan.getClanId(), clan);
                    
                    // Map members
                    for (UUID member : clan.getMembers()) {
                        playerToClan.put(member, clan.getClanId());
                    }
                }
                plugin.getLogger().log(java.util.logging.Level.INFO,
                    () -> String.format("Chargé %d clans depuis MongoDB", clans.size()));
            } catch (Exception e) {
                plugin.getLogger().log(java.util.logging.Level.SEVERE,
                    () -> String.format("Erreur chargement clans: %s", e.getMessage()));
            }
        });
    }
    
    public CompletableFuture<Boolean> createClan(String name, String tag, UUID leader) {
        return CompletableFuture.supplyAsync(() -> {
            // Vérifications
            if (getClanByName(name) != null) return false;
            if (getClanByTag(tag) != null) return false;
            if (playerToClan.containsKey(leader)) return false;
            
            Clan clan = new Clan(name, tag, leader);
            clans.put(clan.getClanId(), clan);
            playerToClan.put(leader, clan.getClanId());
            
            saveClan(clan);
            return true;
        });
    }
    
    public void disbandClan(UUID clanId) {
        Clan clan = clans.get(clanId);
        if (clan == null) return;
        
        // Remove player mappings
        for (UUID member : clan.getMembers()) {
            playerToClan.remove(member);
        }
        
        clans.remove(clanId);
        
        // Delete from MongoDB
        if (plugin.getMongoManager().isConnected()) {
            CompletableFuture.runAsync(() -> {
                plugin.getMongoManager().getClansCollection()
                    .deleteOne(Filters.eq("clanId", clanId.toString()));
            });
        }
    }
    
    public boolean inviteToClan(UUID clanId, UUID player) {
        Clan clan = clans.get(clanId);
        if (clan == null) return false;
        if (playerToClan.containsKey(player)) return false;
        if (clan.getMembers().size() >= (int) clan.getSettings().get("maxMembers")) return false;
        
        clan.addMember(player);
        playerToClan.put(player, clanId);
        saveClan(clan);
        
        return true;
    }
    
    public void kickFromClan(UUID clanId, UUID player) {
        Clan clan = clans.get(clanId);
        if (clan == null) return;
        
        clan.removeMember(player);
        playerToClan.remove(player);
        saveClan(clan);
    }
    
    public Clan getPlayerClan(UUID player) {
        UUID clanId = playerToClan.get(player);
        return clanId != null ? clans.get(clanId) : null;
    }
    
    public Clan getClanByName(String name) {
        return clans.values().stream()
            .filter(c -> c.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }
    
    public Clan getClanByTag(String tag) {
        return clans.values().stream()
            .filter(c -> c.getTag().equalsIgnoreCase(tag))
            .findFirst()
            .orElse(null);
    }
    
    public List<Clan> getTopClans(int limit) {
        return clans.values().stream()
            .sorted(Comparator.comparingInt(Clan::getElo).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    private void saveClan(Clan clan) {
        if (!plugin.getMongoManager().isConnected()) return;
        
        CompletableFuture.runAsync(() -> {
            try {
                Document doc = new Document()
                    .append("clanId", clan.getClanId().toString())
                    .append("name", clan.getName())
                    .append("tag", clan.getTag())
                    .append("leader", clan.getLeader().toString())
                    .append("members", clan.getMembers().stream()
                        .map(UUID::toString).collect(Collectors.toList()))
                    .append("moderators", clan.getModerators().stream()
                        .map(UUID::toString).collect(Collectors.toList()))
                    .append("elo", clan.getElo())
                    .append("wins", clan.getWins())
                    .append("losses", clan.getLosses())
                    .append("createdAt", clan.getCreatedAt())
                    .append("settings", new Document(clan.getSettings()));
                
                plugin.getMongoManager().getClansCollection().replaceOne(
                    Filters.eq("clanId", clan.getClanId().toString()),
                    doc,
                    new com.mongodb.client.model.ReplaceOptions().upsert(true)
                );
            } catch (Exception e) {
                plugin.getLogger().log(java.util.logging.Level.SEVERE,
                    () -> String.format("Erreur sauvegarde clan: %s", e.getMessage()));
            }
        });
    }
    
    private Clan documentToClan(Document doc) {
        String name = doc.getString("name");
        String tag = doc.getString("tag");
        UUID leader = UUID.fromString(doc.getString("leader"));
        
        Clan clan = new Clan(name, tag, leader);
        
        // Members
        List<String> memberStrs = doc.getList("members", String.class);
        clan.setMembers(memberStrs.stream()
            .map(UUID::fromString).collect(Collectors.toList()));
        
        // Moderators
        List<String> modStrs = doc.getList("moderators", String.class);
        clan.setModerators(modStrs.stream()
            .map(UUID::fromString).collect(Collectors.toList()));
        
        clan.setElo(doc.getInteger("elo", 1000));
        clan.setWins(doc.getInteger("wins", 0));
        clan.setLosses(doc.getInteger("losses", 0));
        clan.setCreatedAt(doc.getLong("createdAt"));
        
        Document settingsDoc = doc.get("settings", Document.class);
        if (settingsDoc != null) {
            clan.getSettings().putAll(settingsDoc);
        }
        
        return clan;
    }
}
