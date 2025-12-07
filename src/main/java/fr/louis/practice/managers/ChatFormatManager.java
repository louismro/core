package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.utils.ColorUtils;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ChatFormatManager implements Listener {
    
    private final PracticeCore plugin;
    private Chat vaultChat;
    private boolean useVault;
    
    public ChatFormatManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.useVault = setupVault();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        // Format: [Prefix] PlayerName: Message
        String prefix = getPrefix(player);
        String suffix = getSuffix(player);
        String displayName = player.getName();
        
        // Format avec couleurs (buildChatFormat retourne déjà en legacy)
        String format = buildChatFormat(prefix, displayName, suffix, "%2$s");
        
        event.setFormat(format);
    }
    
    private String buildChatFormat(String prefix, String name, String suffix, String message) {
        // Convertir le prefix/suffix legacy (&4&l) en format lisible d'abord
        String prefixConverted = "";
        if (prefix != null && !prefix.isEmpty()) {
            // Convertir &4&l en legacy puis en Component puis back to legacy pour affichage
            prefixConverted = ColorUtils.toLegacy(ColorUtils.colorize(prefix)) + " ";
        }
        
        String suffixConverted = "";
        if (suffix != null && !suffix.isEmpty()) {
            suffixConverted = " " + ColorUtils.toLegacy(ColorUtils.colorize(suffix));
        }
        
        // Format: prefix + nom + suffix + séparateur + message
        // Utiliser %1$s pour le nom du joueur et %2$s pour le message (format Bukkit)
        return prefixConverted + "§f" + name + suffixConverted + " §8» §7" + message;
    }
    
    private String getPrefix(Player player) {
        if (useVault && vaultChat != null) {
            String prefix = vaultChat.getPlayerPrefix(player);
            return prefix != null ? prefix : "";
        }
        
        // Fallback: récupérer depuis LuckPerms directement
        return plugin.getConfig().getString("chat.default-prefix", "");
    }
    
    private String getSuffix(Player player) {
        if (useVault && vaultChat != null) {
            String suffix = vaultChat.getPlayerSuffix(player);
            return suffix != null ? suffix : "";
        }
        return "";
    }
    
    /**
     * Format un message pour le chat global
     */
    public String formatGlobalMessage(Player player, String message) {
        String prefix = getPrefix(player);
        String suffix = getSuffix(player);
        return buildChatFormat(prefix, player.getName(), suffix, message);
    }
    
    /**
     * Format un message système
     */
    public String formatSystemMessage(String message) {
        return ColorUtils.toLegacy(ColorUtils.colorize(ColorUtils.Hyko.prefixString() + message));
    }
}
