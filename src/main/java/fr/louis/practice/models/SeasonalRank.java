package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

@Getter
@Setter
public class SeasonalRank {
    private final String id;
    private final String name;
    private final String prefix;
    private final ChatColor color;
    private final int minElo;
    private final int maxElo;
    private final int seasonCoinsReward;
    
    public SeasonalRank(String id, String name, String prefix, ChatColor color, 
                       int minElo, int maxElo, int seasonCoinsReward) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        this.minElo = minElo;
        this.maxElo = maxElo;
        this.seasonCoinsReward = seasonCoinsReward;
    }
    
    public boolean isInRange(int elo) {
        return elo >= minElo && (maxElo == -1 || elo <= maxElo);
    }
    
    public String getDisplayName() {
        return color + name;
    }
    
    public String getFormattedPrefix() {
        return color + "[" + prefix + "]";
    }
}
