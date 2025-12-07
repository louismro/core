package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PracticePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BalTopCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public BalTopCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Collect all online players with their coins
        List<PlayerBalance> balances = new ArrayList<>();
        
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PracticePlayer pp = plugin.getPlayerManager().getPlayer(onlinePlayer);
            if (pp != null) {
                balances.add(new PlayerBalance(onlinePlayer.getName(), pp.getCoins()));
            }
        }
        
        // Sort by coins descending
        balances.sort(Comparator.comparingInt(PlayerBalance::getCoins).reversed());
        
        sender.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§e§l       TOP COINS");
        sender.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        int rank = 1;
        for (int i = 0; i < Math.min(10, balances.size()); i++) {
            PlayerBalance pb = balances.get(i);
            String medal = getMedal(rank);
            sender.sendMessage(" " + medal + " §e" + rank + ". §f" + pb.getName() + " §7- §6" + pb.getCoins() + " coins");
            rank++;
        }
        
        sender.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return true;
    }
    
    private String getMedal(int rank) {
        switch (rank) {
            case 1: return "§6★";
            case 2: return "§7★";
            case 3: return "§c★";
            default: return "§8▪";
        }
    }
    
    private static class PlayerBalance {
        private final String name;
        private final int coins;
        
        public PlayerBalance(String name, int coins) {
            this.name = name;
            this.coins = coins;
        }
        
        public String getName() { return name; }
        public int getCoins() { return coins; }
    }
}
