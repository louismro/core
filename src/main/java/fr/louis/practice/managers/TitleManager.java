package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerTitle;
import fr.louis.practice.models.PlayerTitle.TitleRarity;
import fr.louis.practice.models.PracticePlayer;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TitleManager {
    private final PracticeCore plugin;
    
    @Getter
    private final Map<String, PlayerTitle> availableTitles = new HashMap<>();
    
    @Getter
    private final Map<UUID, Set<String>> unlockedTitles = new ConcurrentHashMap<>();
    
    @Getter
    private final Map<UUID, String> activeTitles = new ConcurrentHashMap<>();
    
    public TitleManager(PracticeCore plugin) {
        this.plugin = plugin;
        registerDefaultTitles();
    }
    
    private void registerDefaultTitles() {
        // ═══ COMMON ═══
        registerTitle(new PlayerTitle("rookie", "§fDébutant", "§7[Débutant]", 
            TitleRarity.COMMON, 500));
        registerTitle(new PlayerTitle("fighter", "§fCombattant", "§7[Combattant]", 
            TitleRarity.COMMON, 1000));
        
        // ═══ RARE ═══
        registerTitle(new PlayerTitle("warrior", "§9Guerrier", "§9[Guerrier]", 
            TitleRarity.RARE, 2500));
        registerTitle(new PlayerTitle("veteran", "§9Vétéran", "§9[Vétéran]", 
            TitleRarity.RARE, 3000));
        registerTitle(new PlayerTitle("assassin", "§9Assassin", "§9[Assassin]", 
            TitleRarity.RARE, 3500));
        
        // ═══ EPIC ═══
        registerTitle(new PlayerTitle("gladiator", "§5Gladiateur", "§5[Gladiateur]", 
            TitleRarity.EPIC, 5000));
        registerTitle(new PlayerTitle("champion", "§5Champion", "§5[Champion]", 
            TitleRarity.EPIC, 6000));
        registerTitle(new PlayerTitle("slayer", "§5Tueur", "§5§l[Tueur]", 
            TitleRarity.EPIC, 7000));
        
        // ═══ LEGENDARY ═══
        registerTitle(new PlayerTitle("master", "§6Maître", "§6§l[Maître]", 
            TitleRarity.LEGENDARY, 10000));
        registerTitle(new PlayerTitle("legend", "§6Légende", "§6§l[Légende]", 
            TitleRarity.LEGENDARY, 12000));
        registerTitle(new PlayerTitle("immortal", "§6Immortel", "§6§l[Immortel]", 
            TitleRarity.LEGENDARY, 15000));
        
        // ═══ MYTHIC ═══
        registerTitle(new PlayerTitle("godlike", "§dDivin", "§d§l[DIVIN]", 
            TitleRarity.MYTHIC, 25000));
        registerTitle(new PlayerTitle("supreme", "§dSuprême", "§d§l[SUPRÊME]", 
            TitleRarity.MYTHIC, 30000));
        
        // ═══ EXCLUSIVE (Achievements requis) ═══
        registerTitle(new PlayerTitle("flawless", "§c§lImpeccable", "§c§l[IMPECCABLE]", 
            TitleRarity.EXCLUSIVE, 0, true, "flawless_victory"));
        registerTitle(new PlayerTitle("unstoppable", "§c§lImparable", "§c§l[IMPARABLE]", 
            TitleRarity.EXCLUSIVE, 0, true, "unstoppable"));
        registerTitle(new PlayerTitle("nightmare", "§4§lCauchemar", "§4§l[CAUCHEMAR]", 
            TitleRarity.EXCLUSIVE, 0, true, "nightmare"));
    }
    
    private void registerTitle(PlayerTitle title) {
        availableTitles.put(title.getId(), title);
    }
    
    public boolean unlockTitle(UUID playerId, String titleId) {
        if (!availableTitles.containsKey(titleId)) {
            return false;
        }
        
        Set<String> titles = unlockedTitles.computeIfAbsent(playerId, k -> ConcurrentHashMap.newKeySet());
        return titles.add(titleId);
    }
    
    public boolean hasTitle(UUID playerId, String titleId) {
        Set<String> titles = unlockedTitles.get(playerId);
        return titles != null && titles.contains(titleId);
    }
    
    public boolean purchaseTitle(Player player, String titleId) {
        PlayerTitle title = availableTitles.get(titleId);
        
        if (title == null) {
            player.sendMessage(ChatColor.RED + "Titre introuvable!");
            return false;
        }
        
        if (hasTitle(player.getUniqueId(), titleId)) {
            player.sendMessage(ChatColor.RED + "Vous possédez déjà ce titre!");
            return false;
        }
        
        if (title.isRequiresAchievement()) {
            player.sendMessage(ChatColor.RED + "Ce titre nécessite un achievement!");
            return false;
        }
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (practicePlayer == null) {
            return false;
        }
        
        if (practicePlayer.getCoins() < title.getCoinsPrice()) {
            player.sendMessage(ChatColor.RED + "Pas assez de coins! (" + title.getCoinsPrice() + " requis)");
            return false;
        }
        
        practicePlayer.removeCoins(title.getCoinsPrice());
        unlockTitle(player.getUniqueId(), titleId);
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "✓ Titre acheté!");
        player.sendMessage(ChatColor.YELLOW + "Titre: " + title.getDisplayName());
        player.sendMessage(ChatColor.GRAY + "Coût: " + ChatColor.GOLD + title.getCoinsPrice() + " coins");
        player.sendMessage("");
        
        return true;
    }
    
    public void setActiveTitle(UUID playerId, String titleId) {
        if (titleId == null || titleId.isEmpty()) {
            activeTitles.remove(playerId);
            return;
        }
        
        if (!hasTitle(playerId, titleId)) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Vous ne possédez pas ce titre!");
            }
            return;
        }
        
        activeTitles.put(playerId, titleId);
        
        Player player = plugin.getServer().getPlayer(playerId);
        if (player != null) {
            PlayerTitle title = availableTitles.get(titleId);
            player.sendMessage(ChatColor.GREEN + "✓ Titre activé: " + title.getDisplayName());
        }
    }
    
    public String getActiveTitle(UUID playerId) {
        return activeTitles.get(playerId);
    }
    
    public String getFormattedTitle(UUID playerId) {
        String titleId = getActiveTitle(playerId);
        if (titleId == null) return "";
        
        PlayerTitle title = availableTitles.get(titleId);
        if (title == null) return "";
        
        return title.getPrefix() + " ";
    }
    
    public Set<String> getUnlockedTitles(UUID playerId) {
        return unlockedTitles.getOrDefault(playerId, new HashSet<>());
    }
    
    public List<PlayerTitle> getAllTitles() {
        return new ArrayList<>(availableTitles.values());
    }
    
    public List<PlayerTitle> getTitlesByRarity(TitleRarity rarity) {
        List<PlayerTitle> titles = new ArrayList<>();
        for (PlayerTitle title : availableTitles.values()) {
            if (title.getRarity() == rarity) {
                titles.add(title);
            }
        }
        return titles;
    }
}
