package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.utils.ColorUtils;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

public class TabListManager implements Listener {
    
    private final PracticeCore plugin;
    @SuppressWarnings("unused")
    private Chat vaultChat;
    @SuppressWarnings("unused")
    private final boolean useVault;
    
    @SuppressWarnings("ThisEscapedInObjectConstruction")
    public TabListManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.useVault = setupVault();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        // Mise à jour automatique de la tablist toutes les 2 secondes
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    updateTabList(player);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 40L, 40L);
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
        // Mise à jour de la tablist pour tous les joueurs après 5 ticks
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (Player online : plugin.getServer().getOnlinePlayers()) {
                updateTabList(online);
            }
        }, 5L);
    }
    
    public void updateTabList(Player player) {
        String header = buildHeader(player);
        String footer = buildFooter(player);
        
        // Conversion MiniMessage -> Legacy pour l'API Bukkit
        String headerLegacy = ColorUtils.toLegacy(ColorUtils.colorize(header));
        String footerLegacy = ColorUtils.toLegacy(ColorUtils.colorize(footer));
        
        player.setPlayerListHeaderFooter(headerLegacy, footerLegacy);
    }
    
    @SuppressWarnings({"all", "unused"})
    private String buildHeader(Player player) {
        return """
                
                <gradient:#00d4ff:#0066ff><bold>HYKO PRACTICE</bold></gradient>
                <gray>Joueurs en ligne: <white>""" + plugin.getServer().getOnlinePlayers().size() + "</white></gray>\n";
    }
    
    private String buildFooter(Player player) {
        int ping = getPing(player);
        String pingColor = getPingColor(ping);
        
        return "\n<gray>Votre ping: " + pingColor + ping + "ms</gray>\n";
    }
    
    private int getPing(Player player) {
        try {
            return player.getPing();
        } catch (Exception e) {
            return 0;
        }
    }
    
    private String getPingColor(int ping) {
        if (ping < 50) return "<green>";
        if (ping < 100) return "<yellow>";
        if (ping < 150) return "<gold>";
        return "<red>";
    }
    
    public void removeTabList(Player player) {
        player.setPlayerListHeaderFooter("", "");
    }
}
