package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.utils.ColorUtils;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class NametagManager implements Listener {
    
    private final PracticeCore plugin;
    private Chat vaultChat;
    private final boolean useVault;
    private final Map<String, Integer> groupWeights = new HashMap<>();
    
    public NametagManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.useVault = setupVault();
        
        // Définir les poids des groupes (pour l'ordre dans la tablist)
        groupWeights.put("owner", 1);
        groupWeights.put("admin", 10);
        groupWeights.put("mod", 20);
        groupWeights.put("helper", 30);
        groupWeights.put("mvp", 40);
        groupWeights.put("vip+", 50);
        groupWeights.put("vip", 60);
        groupWeights.put("default", 70);
        
        // Register events after full initialization
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    private boolean setupVault() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        
        RegisteredServiceProvider<Chat> rsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp != null) {
            vaultChat = rsp.getProvider();
            return true;
        }
        return false;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Mettre à jour le nametag pour tous les joueurs
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (Player online : plugin.getServer().getOnlinePlayers()) {
                updateNametag(online);
                
                // Rendre visible pour le nouveau joueur
                if (!online.equals(player)) {
                    player.showPlayer(plugin, online);
                    online.showPlayer(plugin, player);
                }
            }
        }, 5L);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removeNametag(player);
    }
    
    public void updateNametag(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;
        
        // Si le joueur n'a pas de scoreboard ou utilise celui par défaut, en créer un
        if (scoreboard == manager.getMainScoreboard()) {
            scoreboard = manager.getNewScoreboard();
            player.setScoreboard(scoreboard);
        }
        
        String prefix = getPrefix(player);
        String suffix = getSuffix(player);
        String group = getGroup(player);
        
        // Créer ou récupérer l'équipe
        String teamName = getTeamName(group);
        Team team = scoreboard.getTeam(teamName);
        
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }
        
        // Configurer le prefix/suffix (limité à 16 caractères chacun)
        String prefixLegacy = ColorUtils.toLegacy(ColorUtils.colorize(prefix));
        String suffixLegacy = ColorUtils.toLegacy(ColorUtils.colorize(suffix));
        
        // Tronquer si nécessaire (16 caractères max pour prefix/suffix)
        if (prefixLegacy.length() > 16) {
            prefixLegacy = prefixLegacy.substring(0, 16);
        }
        if (suffixLegacy.length() > 16) {
            suffixLegacy = suffixLegacy.substring(0, 16);
        }
        
        team.setPrefix(prefixLegacy);
        team.setSuffix(suffixLegacy);
        
        // Ajouter le joueur à l'équipe
        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }
        
        // Appliquer le scoreboard à tous les joueurs en ligne
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (!online.equals(player)) {
                Scoreboard otherScoreboard = online.getScoreboard();
                org.bukkit.scoreboard.ScoreboardManager mgr = Bukkit.getScoreboardManager();
                if (mgr != null && otherScoreboard == mgr.getMainScoreboard()) {
                    otherScoreboard = mgr.getNewScoreboard();
                    online.setScoreboard(otherScoreboard);
                }
                
                Team otherTeam = otherScoreboard.getTeam(teamName);
                if (otherTeam == null) {
                    otherTeam = otherScoreboard.registerNewTeam(teamName);
                }
                
                otherTeam.setPrefix(prefixLegacy);
                otherTeam.setSuffix(suffixLegacy);
                
                if (!otherTeam.hasEntry(player.getName())) {
                    otherTeam.addEntry(player.getName());
                }
            }
        }
    }
    
    private String getTeamName(String group) {
        // Utiliser le poids pour l'ordre dans la tablist
        int weight = groupWeights.getOrDefault(group.toLowerCase(), 99);
        return String.format("%02d_%s", weight, group.toLowerCase());
    }
    
    public String getPrefix(Player player) {
        if (useVault && vaultChat != null) {
            String prefix = vaultChat.getPlayerPrefix(player);
            return prefix != null ? prefix : "";
        }
        return "";
    }
    
    private String getSuffix(Player player) {
        if (useVault && vaultChat != null) {
            String suffix = vaultChat.getPlayerSuffix(player);
            return suffix != null ? suffix : "";
        }
        return "";
    }
    
    private String getGroup(Player player) {
        if (useVault && vaultChat != null) {
            String group = vaultChat.getPrimaryGroup(player);
            return group != null ? group : "default";
        }
        return "default";
    }
    
    public void removeNametag(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        
        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
            }
        }
    }
    
    public void updateAllNametags() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            updateNametag(player);
        }
    }
}
