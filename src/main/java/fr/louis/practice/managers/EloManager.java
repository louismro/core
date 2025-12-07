package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import lombok.Getter;

@Getter
public class EloManager {
    private final PracticeCore plugin;
    private final int kFactor;
    private final int startingElo;
    
    public EloManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.kFactor = plugin.getConfig().getInt("elo.k-factor", 32);
        this.startingElo = plugin.getConfig().getInt("elo.starting-elo", 1000);
    }
    
    /**
     * Calcule le changement d'ELO pour deux joueurs
     * @param elo1 ELO du joueur 1
     * @param elo2 ELO du joueur 2
     * @param player1Won Est-ce que le joueur 1 a gagné
     * @return Un tableau avec [changement joueur 1, changement joueur 2]
     */
    public int[] calculateEloChange(int elo1, int elo2, boolean player1Won) {
        double expected1 = getExpectedScore(elo1, elo2);
        double expected2 = getExpectedScore(elo2, elo1);
        
        double actual1 = player1Won ? 1.0 : 0.0;
        double actual2 = player1Won ? 0.0 : 1.0;
        
        int change1 = (int) Math.round(kFactor * (actual1 - expected1));
        int change2 = (int) Math.round(kFactor * (actual2 - expected2));
        
        return new int[]{change1, change2};
    }
    
    /**
     * Calcule le score attendu (probabilité de victoire)
     */
    private double getExpectedScore(int elo1, int elo2) {
        return 1.0 / (1.0 + Math.pow(10.0, (elo2 - elo1) / 400.0));
    }
    
    /**
     * Obtient le rang basé sur l'ELO
     */
    public String getRank(int elo) {
        if (elo >= 2000) return "Champion";
        if (elo >= 1800) return "Master";
        if (elo >= 1600) return "Diamond";
        if (elo >= 1400) return "Platinum";
        if (elo >= 1200) return "Gold";
        if (elo >= 1000) return "Silver";
        return "Bronze";
    }
    
    /**
     * Obtient le nom du rang (alias pour getRank)
     */
    public String getRankName(int elo) {
        return getRank(elo);
    }
    
    /**
     * Obtient la couleur du rang
     */
    public String getRankColor(int elo) {
        if (elo >= 2000) return "§d";
        if (elo >= 1800) return "§5";
        if (elo >= 1600) return "§b";
        if (elo >= 1400) return "§3";
        if (elo >= 1200) return "§6";
        if (elo >= 1000) return "§7";
        return "§c";
    }
    
    /**
     * Obtient le prefix du rang
     */
    public String getRankPrefix(int elo) {
        return getRankColor(elo) + "[" + getRank(elo) + "]";
    }
}
