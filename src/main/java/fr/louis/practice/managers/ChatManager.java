package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.ChatMessage;
import fr.louis.practice.models.ChatMessage.ChatChannel;
import fr.louis.practice.models.Clan;
import fr.louis.practice.models.Party;
import fr.louis.practice.models.PracticePlayer;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager {
    private final PracticeCore plugin;
    
    // Chat history (derniers 100 messages par canal)
    @Getter
    private final Map<ChatChannel, LinkedList<ChatMessage>> chatHistory = new ConcurrentHashMap<>();
    
    // Joueurs muted
    private final Map<UUID, Long> mutedPlayers = new ConcurrentHashMap<>();
    
    // Slow mode (délai entre messages)
    private final Map<UUID, Long> lastMessageTime = new ConcurrentHashMap<>();
    private static final long SLOW_MODE_DELAY = 2000; // 2 secondes
    
    // Chat preferences
    private final Map<UUID, ChatChannel> activeChannel = new ConcurrentHashMap<>();
    private final Map<UUID, Set<ChatChannel>> mutedChannels = new ConcurrentHashMap<>();
    
    public ChatManager(PracticeCore plugin) {
        this.plugin = plugin;
        
        // Initialiser les historiques
        for (ChatChannel channel : ChatChannel.values()) {
            chatHistory.put(channel, new LinkedList<>());
        }
    }
    
    public boolean sendMessage(Player player, String message, ChatChannel channel) {
        UUID playerId = player.getUniqueId();
        
        // Vérifier mute
        if (isMuted(playerId)) {
            long remaining = getMuteTimeRemaining(playerId);
            player.sendMessage(ChatColor.RED + "Vous êtes mute pour " + 
                (remaining / 1000) + " secondes!");
            return false;
        }
        
        // Vérifier slow mode
        if (!canSendMessage(playerId)) {
            player.sendMessage(ChatColor.RED + "Attendez avant d'envoyer un autre message!");
            return false;
        }
        
        // Créer le message
        ChatMessage chatMessage = new ChatMessage(playerId, player.getName(), message, channel);
        
        // Ajouter à l'historique
        LinkedList<ChatMessage> history = chatHistory.get(channel);
        history.addFirst(chatMessage);
        if (history.size() > 100) {
            history.removeLast();
        }
        
        // Obtenir le rank prefix
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(playerId);
        String rankPrefix = "";
        if (practicePlayer != null) {
            int elo = practicePlayer.getGlobalElo();
            rankPrefix = plugin.getSeasonManager().getRank(elo).getFormattedPrefix();
        }
        
        // Envoyer selon le canal
        switch (channel) {
            case GLOBAL -> broadcastGlobal(chatMessage, rankPrefix);
            case PARTY -> broadcastParty(player, chatMessage);
            case CLAN -> broadcastClan(player, chatMessage);
            case STAFF -> {
                if (player.hasPermission("practice.staff")) {
                    broadcastStaff(chatMessage, rankPrefix);
                } else {
                    player.sendMessage(ChatColor.RED + "Vous n'avez pas accès à ce canal!");
                    return false;
                }
            }
            case TOURNAMENT -> broadcastTournament(chatMessage);
        }
        
        // Update last message time
        lastMessageTime.put(playerId, System.currentTimeMillis());
        
        return true;
    }
    
    private void broadcastGlobal(ChatMessage message, String rankPrefix) {
        String formatted = message.formatWithRank(rankPrefix);
        
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!hasChannelMuted(player.getUniqueId(), ChatChannel.GLOBAL)) {
                player.sendMessage(formatted);
            }
        }
    }
    
    private void broadcastParty(Player sender, ChatMessage message) {
        Party party = plugin.getPartyManager().getPlayerParty(sender.getUniqueId());
        if (party == null) {
            sender.sendMessage(ChatColor.RED + "Vous n'êtes pas dans une party!");
            return;
        }
        
        String formatted = message.format();
        party.broadcast(formatted);
    }
    
    private void broadcastClan(Player sender, ChatMessage message) {
        Clan clan = plugin.getClanManager().getPlayerClan(sender.getUniqueId());
        if (clan == null) {
            sender.sendMessage(ChatColor.RED + "Vous n'êtes pas dans un clan!");
            return;
        }
        
        String formatted = message.format();
        for (UUID memberId : clan.getMembers()) {
            Player member = plugin.getServer().getPlayer(memberId);
            if (member != null && !hasChannelMuted(memberId, ChatChannel.CLAN)) {
                member.sendMessage(formatted);
            }
        }
    }
    
    private void broadcastStaff(ChatMessage message, String rankPrefix) {
        String formatted = message.formatWithRank(rankPrefix);
        
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission("practice.staff") && 
                !hasChannelMuted(player.getUniqueId(), ChatChannel.STAFF)) {
                player.sendMessage(formatted);
            }
        }
    }
    
    private void broadcastTournament(ChatMessage message) {
        String formatted = message.format();
        
        // Tournament player filtering will be implemented in a future update
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!hasChannelMuted(player.getUniqueId(), ChatChannel.TOURNAMENT)) {
                player.sendMessage(formatted);
            }
        }
    }
    
    public void mutePlayer(UUID playerId, long durationMillis) {
        mutedPlayers.put(playerId, System.currentTimeMillis() + durationMillis);
    }
    
    public void unmutePlayer(UUID playerId) {
        mutedPlayers.remove(playerId);
    }
    
    public boolean isMuted(UUID playerId) {
        Long muteEnd = mutedPlayers.get(playerId);
        if (muteEnd == null) return false;
        
        if (System.currentTimeMillis() > muteEnd) {
            mutedPlayers.remove(playerId);
            return false;
        }
        return true;
    }
    
    public long getMuteTimeRemaining(UUID playerId) {
        Long muteEnd = mutedPlayers.get(playerId);
        if (muteEnd == null) return 0;
        return Math.max(0, muteEnd - System.currentTimeMillis());
    }
    
    private boolean canSendMessage(UUID playerId) {
        Long lastTime = lastMessageTime.get(playerId);
        if (lastTime == null) return true;
        
        return System.currentTimeMillis() - lastTime >= SLOW_MODE_DELAY;
    }
    
    public void setActiveChannel(UUID playerId, ChatChannel channel) {
        activeChannel.put(playerId, channel);
    }
    
    public ChatChannel getActiveChannel(UUID playerId) {
        return activeChannel.getOrDefault(playerId, ChatChannel.GLOBAL);
    }
    
    public void toggleChannelMute(UUID playerId, ChatChannel channel) {
        Set<ChatChannel> muted = mutedChannels.computeIfAbsent(playerId, k -> ConcurrentHashMap.newKeySet());
        if (muted.contains(channel)) {
            muted.remove(channel);
        } else {
            muted.add(channel);
        }
    }
    
    public boolean hasChannelMuted(UUID playerId, ChatChannel channel) {
        Set<ChatChannel> muted = mutedChannels.get(playerId);
        return muted != null && muted.contains(channel);
    }
    
    public List<ChatMessage> getRecentMessages(ChatChannel channel, int count) {
        LinkedList<ChatMessage> history = chatHistory.get(channel);
        if (history == null) return Collections.emptyList();
        
        int size = Math.min(count, history.size());
        return new ArrayList<>(history.subList(0, size));
    }
}
