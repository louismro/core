package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class DailyReward {
    private final LocalDate claimDate;
    private final int day; // Jour dans la streak (1-7, puis reset)
    private final int coinsEarned;
    private final Map<String, Integer> bonusItems; // Items bonus éventuels
    
    public DailyReward(LocalDate claimDate, int day, int coinsEarned) {
        this.claimDate = claimDate;
        this.day = day;
        this.coinsEarned = coinsEarned;
        this.bonusItems = new HashMap<>();
    }
    
    public void addBonusItem(String itemId, int quantity) {
        bonusItems.put(itemId, quantity);
    }
}
