package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ChatMessage {
    private final UUID senderId;
    private final String senderName;
    private final String message;
    private final LocalDateTime timestamp;
    private final ChatChannel channel;
    
    public enum ChatChannel {
        GLOBAL("§f", "§7[G] "),
        PARTY("§d", "§d[Party] "),
        CLAN("§b", "§b[Clan] "),
        STAFF("§c", "§c[Staff] "),
        TOURNAMENT("§e", "§e[Tournoi] ");
        
        private final String color;
        private final String prefix;
        
        ChatChannel(String color, String prefix) {
            this.color = color;
            this.prefix = prefix;
        }
        
        public String getColor() { return color; }
        public String getPrefix() { return prefix; }
    }
    
    public ChatMessage(UUID senderId, String senderName, String message, ChatChannel channel) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.channel = channel;
        this.timestamp = LocalDateTime.now();
    }
    
    public String format() {
        return channel.getPrefix() + ChatColor.WHITE + senderName + 
               ChatColor.GRAY + ": " + channel.getColor() + message;
    }
    
    public String formatWithRank(String rankPrefix) {
        return channel.getPrefix() + rankPrefix + " " + ChatColor.WHITE + senderName + 
               ChatColor.GRAY + ": " + channel.getColor() + message;
    }
}
