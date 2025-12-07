package fr.louis.practice.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ColorUtils {
    
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacySection();
    
    /**
     * Convertit un texte avec codes couleur legacy (&) ou MiniMessage vers Component
     */
    public static Component colorize(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        
        // Si le texte contient des balises MiniMessage
        if (text.contains("<") && text.contains(">")) {
            try {
                return miniMessage.deserialize(text);
            } catch (Exception e) {
                // Fallback sur legacy
                return legacySerializer.deserialize(text.replace('&', '§'));
            }
        }
        
        // Sinon traiter comme legacy
        return legacySerializer.deserialize(text.replace('&', '§'));
    }
    
    /**
     * Convertit un texte MiniMessage en Component
     */
    public static Component parse(String miniMessageText) {
        return miniMessage.deserialize(miniMessageText);
    }
    
    /**
     * Supprime tous les codes couleur d'un texte
     */
    public static String stripColor(String text) {
        if (text == null) return "";
        return text.replaceAll("(?i)&[0-9A-FK-OR]", "")
                   .replaceAll("(?i)§[0-9A-FK-OR]", "")
                   .replaceAll("<[^>]+>", "");
    }
    
    /**
     * Convertit un Component en texte legacy (§)
     */
    public static String toLegacy(Component component) {
        return legacySerializer.serialize(component);
    }
    
    /**
     * Codes couleur prédéfinis pour Hyko Practice
     */
    public static class Hyko {
        public static final String PRIMARY = "<gradient:#00d4ff:#0066ff>";
        public static final String PRIMARY_END = "</gradient>";
        public static final String PRIMARY_BOLD = "<gradient:#00d4ff:#0066ff><bold>";
        public static final String PRIMARY_BOLD_END = "</bold></gradient>";
        
        public static final String SUCCESS = "<green>";
        public static final String ERROR = "<red>";
        public static final String WARNING = "<yellow>";
        public static final String INFO = "<gray>";
        public static final String HIGHLIGHT = "<white>";
        public static final String SECONDARY = "<aqua>";
        public static final String ACCENT = "<gold>";
        
        public static Component prefix() {
            return parse(PRIMARY_BOLD + "Hyko" + PRIMARY_BOLD_END + " <dark_gray>»</dark_gray> ");
        }
        
        public static String prefixString() {
            return PRIMARY_BOLD + "Hyko" + PRIMARY_BOLD_END + " <dark_gray>»</dark_gray> ";
        }
    }
}
