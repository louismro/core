package fr.louis.practice.models;

import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum Killstreak {
    KILLING_SPREE(3, "&e{player} &7is on a &eKilling Spree! &7(&e3 kills&7)"),
    RAMPAGE(5, "&6{player} &7is on a &6Rampage! &7(&65 kills&7)"),
    DOMINATING(7, "&c{player} &7is &cDominating! &7(&c7 kills&7)"),
    UNSTOPPABLE(10, "&4{player} &7is &4UNSTOPPABLE! &7(&410 kills&7)"),
    GODLIKE(15, "&5{player} &7is &5GODLIKE! &7(&515 kills&7)"),
    LEGENDARY(20, "&d&l{player} &7is &d&lLEGENDARY! &7(&d20 kills&7)");
    
    private final int kills;
    private final String message;
    
    Killstreak(int kills, String message) {
        this.kills = kills;
        this.message = message;
    }
    
    public static Killstreak getByKills(int kills) {
        Killstreak result = null;
        for (Killstreak streak : values()) {
            if (kills >= streak.kills) {
                result = streak;
            }
        }
        return result;
    }
    
    public String getFormattedMessage(String playerName) {
        return ChatColor.translateAlternateColorCodes('&', 
            message.replace("{player}", playerName));
    }
}
