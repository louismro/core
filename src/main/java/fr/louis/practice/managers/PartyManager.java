package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PartyManager {
    private final PracticeCore plugin;
    private final Map<UUID, Party> parties;
    private final Map<UUID, UUID> playerParty; // UUID du joueur -> UUID de la partie
    
    public PartyManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.parties = new ConcurrentHashMap<>();
        this.playerParty = new ConcurrentHashMap<>();
    }
    
    public Party createParty(Player leader) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getOrCreate(leader);
        
        if (practicePlayer.isInParty()) {
            return null;
        }
        
        int maxSize = plugin.getConfig().getInt("general.max-party-size", 10);
        Party party = new Party(leader.getUniqueId(), maxSize);
        
        parties.put(party.getPartyId(), party);
        playerParty.put(leader.getUniqueId(), party.getPartyId());
        practicePlayer.setParty(party);
        
        return party;
    }
    
    public void disbandParty(Party party) {
        for (UUID member : party.getMembers()) {
            Player player = Bukkit.getPlayer(member);
            if (player != null) {
                player.sendMessage("§cLa partie a été dissoute");
            }
            
            PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(member);
            if (practicePlayer != null) {
                practicePlayer.setParty(null);
            }
            
            playerParty.remove(member);
        }
        
        parties.remove(party.getPartyId());
    }
    
    public void invitePlayer(Party party, Player player) {
        party.invite(player.getUniqueId());
        
        player.sendMessage("§aVous avez été invité dans une partie par §e" + 
            Bukkit.getPlayer(party.getLeader()).getName());
        player.sendMessage("§a/party accept §7pour accepter");
    }
    
    public void joinParty(Player player, Party party) {
        if (party.isFull()) {
            player.sendMessage("§cCette partie est pleine!");
            return;
        }
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getOrCreate(player);
        
        party.addMember(player.getUniqueId());
        playerParty.put(player.getUniqueId(), party.getPartyId());
        practicePlayer.setParty(party);
        
        // Notifier les membres
        for (UUID member : party.getMembers()) {
            Player p = Bukkit.getPlayer(member);
            if (p != null) {
                p.sendMessage("§a" + player.getName() + " §7a rejoint la partie §7(" + 
                    party.getSize() + "/" + party.getMaxSize() + ")");
            }
        }
    }
    
    public void leaveParty(Player player, Party party) {
        if (party.isLeader(player.getUniqueId())) {
            disbandParty(party);
            return;
        }
        
        party.removeMember(player.getUniqueId());
        playerParty.remove(player.getUniqueId());
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        if (practicePlayer != null) {
            practicePlayer.setParty(null);
        }
        
        player.sendMessage("§cVous avez quitté la partie");
        
        // Notifier les membres
        for (UUID member : party.getMembers()) {
            Player p = Bukkit.getPlayer(member);
            if (p != null) {
                p.sendMessage("§c" + player.getName() + " §7a quitté la partie §7(" + 
                    party.getSize() + "/" + party.getMaxSize() + ")");
            }
        }
    }
    
    public void kickPlayer(Party party, UUID player) {
        Player kicked = Bukkit.getPlayer(player);
        
        party.removeMember(player);
        playerParty.remove(player);
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        if (practicePlayer != null) {
            practicePlayer.setParty(null);
        }
        
        if (kicked != null) {
            kicked.sendMessage("§cVous avez été exclu de la partie");
        }
        
        // Notifier les membres
        for (UUID member : party.getMembers()) {
            Player p = Bukkit.getPlayer(member);
            if (p != null) {
                p.sendMessage("§c" + (kicked != null ? kicked.getName() : "Un joueur") + 
                    " §7a été exclu de la partie");
            }
        }
    }
    
    public Party getParty(UUID partyId) {
        return parties.get(partyId);
    }
    
    public Party getPlayerParty(UUID player) {
        UUID partyId = playerParty.get(player);
        return partyId != null ? parties.get(partyId) : null;
    }
    
    public boolean isInParty(UUID player) {
        return playerParty.containsKey(player);
    }
}
