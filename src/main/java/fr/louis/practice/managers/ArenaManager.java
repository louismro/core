package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Arena;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ArenaManager {
    private final PracticeCore plugin;
    private final Map<String, Arena> arenas;
    private final File arenasFile;
    private FileConfiguration arenasConfig;
    
    public ArenaManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.arenas = new ConcurrentHashMap<>();
        this.arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
        loadArenas();
    }
    
    private void loadArenas() {
        if (!arenasFile.exists()) {
            plugin.saveResource("arenas.yml", false);
        }
        
        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        
        ConfigurationSection arenasSection = arenasConfig.getConfigurationSection("arenas");
        if (arenasSection == null) {
            return;
        }
        
        for (String arenaName : arenasSection.getKeys(false)) {
            String path = "arenas." + arenaName;
            
            Location pos1 = getLocation(path + ".pos1");
            Location pos2 = getLocation(path + ".pos2");
            
            if (pos1 == null || pos2 == null) {
                continue;
            }
            
            Arena arena = new Arena(arenaName, pos1, pos2);
            arena.setDisplayName(arenasConfig.getString(path + ".display-name", arenaName));
            arena.setIcon(arenasConfig.getString(path + ".icon", "GRASS"));
            
            // Charger les spawn points
            List<?> spawns = arenasConfig.getList(path + ".spawns");
            if (spawns != null) {
                for (Object obj : spawns) {
                    if (obj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> spawnMap = (Map<String, Object>) obj;
                        Location spawn = deserializeLocation(spawnMap);
                        if (spawn != null) {
                            arena.addSpawnPoint(spawn);
                        }
                    }
                }
            }
            
            arenas.put(arenaName, arena);
        }
        
        plugin.getLogger().info(String.format("Loaded %d arenas", arenas.size()));
    }
    
    private Location getLocation(String path) {
        if (!arenasConfig.contains(path)) {
            return null;
        }
        
        return deserializeLocation(arenasConfig.getConfigurationSection(path).getValues(false));
    }
    
    private Location deserializeLocation(Map<String, Object> map) {
        try {
            String world = (String) map.get("world");
            double x = getDouble(map.get("x"));
            double y = getDouble(map.get("y"));
            double z = getDouble(map.get("z"));
            float yaw = getFloat(map.get("yaw"));
            float pitch = getFloat(map.get("pitch"));
            
            return new Location(plugin.getServer().getWorld(world), x, y, z, yaw, pitch);
        } catch (Exception e) {
            return null;
        }
    }
    
    private double getDouble(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        return 0.0;
    }
    
    private float getFloat(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).floatValue();
        }
        return 0.0f;
    }
    
    public Arena getArena(String name) {
        return arenas.get(name);
    }
    
    public Arena getAvailableArena() {
        for (Arena arena : arenas.values()) {
            if (!arena.isInUse()) {
                return arena;
            }
        }
        return null;
    }
    
    public Collection<Arena> getAllArenas() {
        return arenas.values();
    }
    
    public List<Arena> getAvailableArenas() {
        List<Arena> available = new ArrayList<>();
        for (Arena arena : arenas.values()) {
            if (!arena.isInUse()) {
                available.add(arena);
            }
        }
        return available;
    }
    
    public void addArena(Arena arena) {
        arenas.put(arena.getName(), arena);
        saveArenas();
    }
    
    public void removeArena(String name) {
        arenas.remove(name);
        saveArenas();
    }
    
    public void saveArenas() {
        for (Arena arena : arenas.values()) {
            String path = "arenas." + arena.getName();
            
            arenasConfig.set(path + ".display-name", arena.getDisplayName());
            arenasConfig.set(path + ".icon", arena.getIcon());
            arenasConfig.set(path + ".pos1", serializeLocation(arena.getPos1()));
            arenasConfig.set(path + ".pos2", serializeLocation(arena.getPos2()));
            
            List<Map<String, Object>> spawns = new ArrayList<>();
            for (Location spawn : arena.getSpawnPoints()) {
                spawns.add(serializeLocation(spawn));
            }
            arenasConfig.set(path + ".spawns", spawns);
        }
        
        try {
            arenasConfig.save(arenasFile);
        } catch (Exception e) {
            plugin.getLogger().severe(String.format("Failed to save arenas: %s", e.getMessage()));
        }
    }
    
    private Map<String, Object> serializeLocation(Location loc) {
        Map<String, Object> map = new HashMap<>();
        map.put("world", loc.getWorld().getName());
        map.put("x", loc.getX());
        map.put("y", loc.getY());
        map.put("z", loc.getZ());
        map.put("yaw", loc.getYaw());
        map.put("pitch", loc.getPitch());
        return map;
    }
}
