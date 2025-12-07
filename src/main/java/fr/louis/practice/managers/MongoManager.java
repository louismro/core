package fr.louis.practice.managers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.*;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Getter
public class MongoManager {
    private final PracticeCore plugin;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> playersCollection;
    private MongoCollection<Document> matchHistoryCollection;
    private MongoCollection<Document> leaderboardsCollection;
    private MongoCollection<Document> clansCollection;
    
    private boolean connected = false;
    
    public MongoManager(PracticeCore plugin) {
        this.plugin = plugin;
        connect();
    }
    
    private void connect() {
        try {
            String connectionString = plugin.getConfig().getString("mongodb.connection-string", 
                "mongodb://localhost:27017");
            String databaseName = plugin.getConfig().getString("mongodb.database", "practicecore");
            
            mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase(databaseName);
            
            // Collections
            playersCollection = database.getCollection("players");
            matchHistoryCollection = database.getCollection("match_history");
            leaderboardsCollection = database.getCollection("leaderboards");
            clansCollection = database.getCollection("clans");
            
            // Indexes
            playersCollection.createIndex(new Document("uuid", 1));
            matchHistoryCollection.createIndex(new Document("player", 1).append("timestamp", -1));
            leaderboardsCollection.createIndex(new Document("kit", 1).append("elo", -1));
            clansCollection.createIndex(new Document("name", 1));
            
            connected = true;
            plugin.getLogger().info("MongoDB connecté avec succès!");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Erreur de connexion MongoDB", e);
            connected = false;
        }
    }
    
    public CompletableFuture<Void> savePlayerAsync(PracticePlayer player) {
        return CompletableFuture.runAsync(() -> {
            if (!connected) return;
            
            try {
                Document doc = new Document()
                    .append("uuid", player.getUuid().toString())
                    .append("name", player.getName())
                    .append("lastSeen", System.currentTimeMillis());
                
                // ELO map
                Document eloDoc = new Document();
                player.getEloMap().forEach((kit, elo) -> eloDoc.append(kit, elo));
                doc.append("elo", eloDoc);
                
                // Stats map
                Document statsDoc = new Document();
                player.getStatsMap().forEach((kit, stats) -> {
                    Document statDoc = new Document()
                        .append("kills", stats.getKills())
                        .append("deaths", stats.getDeaths())
                        .append("wins", stats.getWins())
                        .append("losses", stats.getLosses())
                        .append("highestStreak", stats.getHighestStreak());
                    statsDoc.append(kit, statDoc);
                });
                doc.append("stats", statsDoc);
                
                // Settings
                PlayerSettings settings = player.getSettings();
                Document settingsDoc = new Document()
                    .append("allowDuels", settings.isAllowDuels())
                    .append("allowSpectators", settings.isAllowSpectators())
                    .append("scoreboardVisible", settings.isShowScoreboard());
                doc.append("settings", settingsDoc);
                
                playersCollection.replaceOne(
                    Filters.eq("uuid", player.getUuid().toString()),
                    doc,
                    new com.mongodb.client.model.ReplaceOptions().upsert(true)
                );
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Erreur sauvegarde joueur", e);
            }
        });
    }
    
    // Méthode synchrone pour compatibilité
    public void savePlayer(PracticePlayer player) {
        savePlayerAsync(player).join();
    }
    
    // Méthode synchrone pour compatibilité
    public void loadPlayer(PracticePlayer player) {
        if (!connected) return;
        
        try {
            Document doc = playersCollection.find(Filters.eq("uuid", player.getUuid().toString())).first();
            
            if (doc == null) {
                // Init ELO for all kits
                for (String kit : plugin.getKitManager().getKits().keySet()) {
                    player.setElo(kit, 1000);
                    player.getStatsMap().put(kit, new PlayerStats());
                }
                return;
            }
            
            // Load ELO
            Document eloDoc = doc.get("elo", Document.class);
            if (eloDoc != null) {
                eloDoc.forEach((kit, elo) -> {
                    player.getEloMap().put(kit, ((Number) elo).intValue());
                });
            }
            
            // Load Stats
            Document statsDoc = doc.get("stats", Document.class);
            if (statsDoc != null) {
                statsDoc.forEach((kit, statObj) -> {
                    Document statDoc = (Document) statObj;
                    PlayerStats stats = new PlayerStats();
                    stats.setKills(statDoc.getInteger("kills", 0));
                    stats.setDeaths(statDoc.getInteger("deaths", 0));
                    stats.setWins(statDoc.getInteger("wins", 0));
                    stats.setLosses(statDoc.getInteger("losses", 0));
                    stats.setHighestStreak(statDoc.getInteger("highestStreak", 0));
                    player.getStatsMap().put(kit, stats);
                });
            }
            
            // Load Settings
            Document settingsDoc = doc.get("settings", Document.class);
            if (settingsDoc != null) {
                PlayerSettings settings = player.getSettings();
                settings.setAllowDuels(settingsDoc.getBoolean("allowDuels", true));
                settings.setAllowSpectators(settingsDoc.getBoolean("allowSpectators", true));
                settings.setShowScoreboard(settingsDoc.getBoolean("scoreboardVisible", true));
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Erreur chargement joueur", e);
        }
    }
    
    public CompletableFuture<PracticePlayer> loadPlayerAsync(UUID uuid, String name) {
        return CompletableFuture.supplyAsync(() -> {
            if (!connected) return createNewPlayer(uuid, name);
            
            try {
                Document doc = playersCollection.find(Filters.eq("uuid", uuid.toString())).first();
                
                if (doc == null) {
                    return createNewPlayer(uuid, name);
                }
                
                PracticePlayer player = new PracticePlayer(uuid, name);
                
                // Load ELO
                Document eloDoc = doc.get("elo", Document.class);
                if (eloDoc != null) {
                    eloDoc.forEach((kit, elo) -> {
                        player.getEloMap().put(kit, ((Number) elo).intValue());
                    });
                }
                
                // Load Stats
                Document statsDoc = doc.get("stats", Document.class);
                if (statsDoc != null) {
                    statsDoc.forEach((kit, statObj) -> {
                        Document statDoc = (Document) statObj;
                        PlayerStats stats = new PlayerStats();
                        stats.setKills(statDoc.getInteger("kills", 0));
                        stats.setDeaths(statDoc.getInteger("deaths", 0));
                        stats.setWins(statDoc.getInteger("wins", 0));
                        stats.setLosses(statDoc.getInteger("losses", 0));
                        stats.setHighestStreak(statDoc.getInteger("highestStreak", 0));
                        player.getStatsMap().put(kit, stats);
                    });
                }
                
                // Load Settings
                Document settingsDoc = doc.get("settings", Document.class);
                if (settingsDoc != null) {
                    PlayerSettings settings = player.getSettings();
                    settings.setAllowDuels(settingsDoc.getBoolean("allowDuels", true));
                    settings.setAllowSpectators(settingsDoc.getBoolean("allowSpectators", true));
                    settings.setShowScoreboard(settingsDoc.getBoolean("scoreboardVisible", true));
                }
                
                return player;
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Erreur chargement joueur", e);
                return createNewPlayer(uuid, name);
            }
        });
    }
    
    private PracticePlayer createNewPlayer(UUID uuid, String name) {
        PracticePlayer player = new PracticePlayer(uuid, name);
        // Init ELO for all kits
        for (String kit : plugin.getKitManager().getKits().keySet()) {
            player.setElo(kit, 1000);
            player.getStatsMap().put(kit, new PlayerStats());
        }
        return player;
    }
    
    public CompletableFuture<Void> saveMatchHistory(MatchHistory history) {
        return CompletableFuture.runAsync(() -> {
            if (!connected) return;
            
            try {
                Document doc = new Document()
                    .append("matchId", history.getMatchId().toString())
                    .append("player", history.getOpponent().toString())
                    .append("timestamp", history.getTimestamp())
                    .append("kit", history.getKit())
                    .append("opponent", history.getOpponent().toString())
                    .append("opponentName", history.getOpponentName())
                    .append("won", history.isWon())
                    .append("eloChange", history.getEloChange())
                    .append("duration", history.getDuration());
                
                // Stats
                MatchHistory.MatchHistoryStats stats = history.getStats();
                Document statsDoc = new Document()
                    .append("kills", stats.getKills())
                    .append("hits", stats.getHits())
                    .append("maxCombo", stats.getMaxCombo())
                    .append("potionsUsed", stats.getPotionsUsed())
                    .append("potionsThrown", stats.getPotionsThrown())
                    .append("arrowsShot", stats.getArrowsShot())
                    .append("arrowsHit", stats.getArrowsHit())
                    .append("damageDealt", stats.getDamageDealt())
                    .append("damageTaken", stats.getDamageTaken());
                doc.append("stats", statsDoc);
                
                matchHistoryCollection.insertOne(doc);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Erreur sauvegarde match history", e);
            }
        });
    }
    
    public void saveAll() {
        if (!connected) return;
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (PracticePlayer player : plugin.getPlayerManager().getAllPlayers()) {
                savePlayerAsync(player).join();
            }
        });
    }
    
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            plugin.getLogger().info("MongoDB déconnecté");
        }
    }
}
