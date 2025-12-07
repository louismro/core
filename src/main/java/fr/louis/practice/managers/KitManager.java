package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Kit;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class KitManager {
    private final PracticeCore plugin;
    private final Map<String, Kit> kits;
    private final File kitsFile;
    private FileConfiguration kitsConfig;
    
    public KitManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.kits = new ConcurrentHashMap<>();
        this.kitsFile = new File(plugin.getDataFolder(), "kits.yml");
        loadKits();
    }
    
    private void loadKits() {
        if (!kitsFile.exists()) {
            plugin.saveResource("kits.yml", false);
        }
        
        kitsConfig = YamlConfiguration.loadConfiguration(kitsFile);
        
        // TOUJOURS créer les kits par défaut car le YML ne contient pas les items
        createDefaultKits();
        plugin.getLogger().info("Loaded kits with items from code (not YML)");
        
        plugin.getLogger().info(String.format("Loaded %d kits", kits.size()));
    }
    
    private void createDefaultKits() {
        // NoDebuff Kit
        Kit noDebuff = createNoDebuffKit();
        kits.put("NoDebuff", noDebuff);
        
        // Debuff Kit
        Kit debuff = createDebuffKit();
        kits.put("Debuff", debuff);
        
        // Gapple Kit
        Kit gapple = createGappleKit();
        kits.put("Gapple", gapple);
        
        // BuildUHC Kit
        Kit buildUHC = createBuildUHCKit();
        kits.put("BuildUHC", buildUHC);
        
        // Combo Kit
        Kit combo = createComboKit();
        kits.put("Combo", combo);
        
        // Sumo Kit
        Kit sumo = createSumoKit();
        kits.put("Sumo", sumo);
        
        // Boxing Kit
        Kit boxing = createBoxingKit();
        kits.put("Boxing", boxing);
        
        // Archer Kit
        Kit archer = createArcherKit();
        kits.put("Archer", archer);
        
        // Axe Kit
        Kit axe = createAxeKit();
        kits.put("Axe", axe);
        
        // Soup Kit
        Kit soup = createSoupKit();
        kits.put("Soup", soup);
        
        // Classic Kit
        Kit classic = createClassicKit();
        kits.put("Classic", classic);
        
        // Stick Kit
        Kit stick = createStickKit();
        kits.put("Stick", stick);
        
        // SpleefTNT Kit
        Kit spleef = createSpleefKit();
        kits.put("SpleefTNT", spleef);
        
        saveKits();
    }
    
    private Kit createNoDebuffKit() {
        Kit kit = new Kit("NoDebuff");
        kit.setDisplayName("§eNoDebuff");
        kit.setIcon("POTION:16421");
        
        ItemStack[] items = new ItemStack[36];
        items[0] = createItem(Material.DIAMOND_SWORD, 1, "Sharpness:1");
        items[1] = createItem(Material.ENDER_PEARL, 16);
        
        // Health II splash potions (slots 2-34)
        for (int i = 2; i <= 34; i++) {
            items[i] = createSplashPotion(16421, 1); // Heal II Splash
        }
        
        // Speed II drinkable potions (slots 35, 8, 17, 26) - tout à droite
        items[35] = createPotion(8226, 1); // Speed II buvable
        items[8] = createPotion(8226, 1);
        items[17] = createPotion(8226, 1);
        items[26] = createPotion(8226, 1);
        
        kit.setContents(items);
        
        ItemStack[] armor = new ItemStack[4];
        armor[3] = createItem(Material.DIAMOND_HELMET, 1, "Protection:1,Unbreaking:1");
        armor[2] = createItem(Material.DIAMOND_CHESTPLATE, 1, "Protection:1,Unbreaking:1");
        armor[1] = createItem(Material.DIAMOND_LEGGINGS, 1, "Protection:1,Unbreaking:1");
        armor[0] = createItem(Material.DIAMOND_BOOTS, 1, "Protection:1,Unbreaking:1");
        
        kit.setArmor(armor);
        
        return kit;
    }
    
    private Kit createDebuffKit() {
        Kit kit = new Kit("Debuff");
        kit.setDisplayName("§cDebuff");
        kit.setIcon("POTION:16388");
        
        ItemStack[] items = new ItemStack[36];
        items[0] = createItem(Material.DIAMOND_SWORD, 1, "Sharpness:1");
        items[1] = createItem(Material.ENDER_PEARL, 16);
        
        // Debuff splash potions
        items[2] = createSplashPotion(16388, 1); // Poison splash
        items[3] = createSplashPotion(16392, 1); // Slowness splash
        items[4] = createSplashPotion(16424, 1); // Weakness splash
        
        // Health II splash potions (slots 5-34)
        for (int i = 5; i <= 34; i++) {
            items[i] = createSplashPotion(16421, 1);
        }
        
        // Speed II drinkable potions (slots 35, 8, 17, 26)
        items[35] = createPotion(8226, 1);
        items[8] = createPotion(8226, 1);
        items[17] = createPotion(8226, 1);
        items[26] = createPotion(8226, 1);
        
        kit.setContents(items);
        
        ItemStack[] armor = new ItemStack[4];
        armor[3] = createItem(Material.DIAMOND_HELMET, 1, "Protection:1,Unbreaking:1");
        armor[2] = createItem(Material.DIAMOND_CHESTPLATE, 1, "Protection:1,Unbreaking:1");
        armor[1] = createItem(Material.DIAMOND_LEGGINGS, 1, "Protection:1,Unbreaking:1");
        armor[0] = createItem(Material.DIAMOND_BOOTS, 1, "Protection:1,Unbreaking:1");
        
        kit.setArmor(armor);
        
        return kit;
    }
    
    private Kit createBuildUHCKit() {
        Kit kit = new Kit("BuildUHC");
        kit.setDisplayName("§6BuildUHC");
        kit.setIcon("GOLDEN_APPLE");
        kit.setBuild(true);
        
        ItemStack[] items = new ItemStack[36];
        items[0] = createItem(Material.DIAMOND_SWORD, 1, "Sharpness:1");
        items[1] = createItem(Material.BOW, 1, "Power:1");
        items[2] = createItem(Material.GOLDEN_APPLE, 8);
        items[3] = createItem(Material.COOKED_BEEF, 64);
        items[4] = createItem(Material.OAK_PLANKS, 64);
        items[5] = createItem(Material.COBBLESTONE, 64);
        items[6] = createItem(Material.LAVA_BUCKET, 1);
        items[7] = createItem(Material.WATER_BUCKET, 1);
        items[8] = createItem(Material.FISHING_ROD, 1);
        items[9] = createItem(Material.ARROW, 64);
        
        kit.setContents(items);
        
        ItemStack[] armor = new ItemStack[4];
        armor[3] = createItem(Material.DIAMOND_HELMET, 1, "Protection:1");
        armor[2] = createItem(Material.DIAMOND_CHESTPLATE, 1, "Protection:1");
        armor[1] = createItem(Material.DIAMOND_LEGGINGS, 1, "Protection:1");
        armor[0] = createItem(Material.DIAMOND_BOOTS, 1, "Protection:1,Feather_Falling:1");
        
        kit.setArmor(armor);
        
        return kit;
    }
    
    private Kit createComboKit() {
        Kit kit = new Kit("Combo");
        kit.setDisplayName("§aCombo");
        kit.setIcon("BLAZE_ROD");
        kit.setRegeneration(true);
        
        ItemStack[] items = new ItemStack[36];
        items[0] = createItem(Material.DIAMOND_SWORD, 1);
        items[1] = createItem(Material.COOKED_BEEF, 64);
        
        kit.setContents(items);
        
        ItemStack[] armor = new ItemStack[4];
        armor[3] = createItem(Material.IRON_HELMET, 1);
        armor[2] = createItem(Material.IRON_CHESTPLATE, 1);
        armor[1] = createItem(Material.IRON_LEGGINGS, 1);
        armor[0] = createItem(Material.IRON_BOOTS, 1);
        
        kit.setArmor(armor);
        
        return kit;
    }
    
    private Kit createGappleKit() {
        Kit kit = new Kit("Gapple");
        kit.setDisplayName("§6Gapple");
        kit.setIcon("GOLDEN_APPLE");
        
        ItemStack[] items = new ItemStack[36];
        items[0] = createItem(Material.DIAMOND_SWORD, 1, "Sharpness:2");
        items[1] = createItem(Material.FISHING_ROD, 1);
        items[2] = createItem(Material.BOW, 1, "Power:1");
        items[3] = createItem(Material.GOLDEN_APPLE, 64);
        items[4] = createItem(Material.COOKED_BEEF, 64);
        items[9] = createItem(Material.ARROW, 64);
        
        kit.setContents(items);
        
        ItemStack[] armor = new ItemStack[4];
        armor[3] = createItem(Material.DIAMOND_HELMET, 1, "Protection:2");
        armor[2] = createItem(Material.DIAMOND_CHESTPLATE, 1, "Protection:2");
        armor[1] = createItem(Material.DIAMOND_LEGGINGS, 1, "Protection:2");
        armor[0] = createItem(Material.DIAMOND_BOOTS, 1, "Protection:2,Feather_Falling:2");
        
        kit.setArmor(armor);
        
        return kit;
    }
    
    private Kit createBoxingKit() {
        Kit kit = new Kit("Boxing");
        kit.setDisplayName("§cBoxing");
        kit.setIcon("IRON_SWORD");
        kit.setRegeneration(true);
        
        ItemStack[] items = new ItemStack[36];
        items[1] = createItem(Material.COOKED_BEEF, 64);
        
        kit.setContents(items);
        
        ItemStack[] armor = new ItemStack[4];
        // Pas d'armure pour boxing
        
        kit.setArmor(armor);
        
        return kit;
    }
    
    private Kit createArcherKit() {
        Kit kit = new Kit("Archer");
        kit.setDisplayName("§2Archer");
        kit.setIcon("BOW");
        
        ItemStack[] items = new ItemStack[36];
        items[0] = createItem(Material.BOW, 1, "Power:3,Flame:1");
        items[1] = createItem(Material.STONE_SWORD, 1);
        items[2] = createItem(Material.COOKED_BEEF, 64);
        items[9] = createItem(Material.ARROW, 64);
        items[10] = createItem(Material.ARROW, 64);
        
        kit.setContents(items);
        
        ItemStack[] armor = new ItemStack[4];
        armor[3] = createItem(Material.LEATHER_HELMET, 1);
        armor[2] = createItem(Material.LEATHER_CHESTPLATE, 1);
        armor[1] = createItem(Material.LEATHER_LEGGINGS, 1);
        armor[0] = createItem(Material.LEATHER_BOOTS, 1);
        
        kit.setArmor(armor);
        
        return kit;
    }
    
    private Kit createAxeKit() {
        Kit kit = new Kit("Axe");
        kit.setDisplayName("§4Axe");
        kit.setIcon("DIAMOND_AXE");
        
        ItemStack[] items = new ItemStack[36];
        items[0] = createItem(Material.DIAMOND_AXE, 1, "Sharpness:3");
        items[1] = createItem(Material.COOKED_BEEF, 64);
        
        kit.setContents(items);
        
        ItemStack[] armor = new ItemStack[4];
        armor[3] = createItem(Material.IRON_HELMET, 1, "Protection:2");
        armor[2] = createItem(Material.IRON_CHESTPLATE, 1, "Protection:2");
        armor[1] = createItem(Material.IRON_LEGGINGS, 1, "Protection:2");
        armor[0] = createItem(Material.IRON_BOOTS, 1, "Protection:2");
        
        kit.setArmor(armor);
        
        return kit;
    }
    
    private Kit createSoupKit() {
        Kit kit = new Kit("Soup");
        kit.setDisplayName("§dSoup");
        kit.setIcon("MUSHROOM_STEW");
        
        ItemStack[] items = new ItemStack[36];
        items[0] = createItem(Material.DIAMOND_SWORD, 1, "Sharpness:1");
        
        // Remplir de soupe (slots 1-35)
        for (int i = 1; i <= 35; i++) {
            items[i] = createItem(Material.MUSHROOM_STEW, 1);
        }
        
        kit.setContents(items);
        
        ItemStack[] armor = new ItemStack[4];
        armor[3] = createItem(Material.IRON_HELMET, 1);
        armor[2] = createItem(Material.IRON_CHESTPLATE, 1);
        armor[1] = createItem(Material.IRON_LEGGINGS, 1);
        armor[0] = createItem(Material.IRON_BOOTS, 1);
        
        kit.setArmor(armor);
        
        return kit;
    }
    
    private Kit createClassicKit() {
        Kit kit = new Kit("Classic");
        kit.setDisplayName("§7Classic");
        kit.setIcon("DIAMOND_SWORD");
        
        ItemStack[] items = new ItemStack[36];
        items[0] = createItem(Material.DIAMOND_SWORD, 1, "Sharpness:1");
        items[1] = createItem(Material.FISHING_ROD, 1);
        items[2] = createItem(Material.BOW, 1, "Power:1");
        items[3] = createItem(Material.COOKED_BEEF, 64);
        items[9] = createItem(Material.ARROW, 64);
        
        kit.setContents(items);
        
        ItemStack[] armor = new ItemStack[4];
        armor[3] = createItem(Material.DIAMOND_HELMET, 1, "Protection:1");
        armor[2] = createItem(Material.DIAMOND_CHESTPLATE, 1, "Protection:1");
        armor[1] = createItem(Material.DIAMOND_LEGGINGS, 1, "Protection:1");
        armor[0] = createItem(Material.DIAMOND_BOOTS, 1, "Protection:1,Feather_Falling:1");
        
        kit.setArmor(armor);
        
        return kit;
    }
    
    private Kit createStickKit() {
        Kit kit = new Kit("Stick");
        kit.setDisplayName("§eStick");
        kit.setIcon("STICK");
        kit.setRegeneration(true);
        
        ItemStack[] items = new ItemStack[36];
        items[0] = createItem(Material.STICK, 1, "Knockback:2");
        items[1] = createItem(Material.COOKED_BEEF, 64);
        
        kit.setContents(items);
        
        ItemStack[] armor = new ItemStack[4];
        armor[3] = createItem(Material.LEATHER_HELMET, 1);
        armor[2] = createItem(Material.LEATHER_CHESTPLATE, 1);
        armor[1] = createItem(Material.LEATHER_LEGGINGS, 1);
        armor[0] = createItem(Material.LEATHER_BOOTS, 1);
        
        kit.setArmor(armor);
        
        return kit;
    }
    
    private Kit createSpleefKit() {
        Kit kit = new Kit("SpleefTNT");
        kit.setDisplayName("§cSpleef TNT");
        kit.setIcon("TNT");
        kit.setFall(false);
        
        ItemStack[] items = new ItemStack[36];
        items[0] = createItem(Material.DIAMOND_SHOVEL, 1, "Efficiency:3");
        
        kit.setContents(items);
        
        ItemStack[] armor = new ItemStack[4];
        // Pas d'armure pour spleef
        
        kit.setArmor(armor);
        
        kit.addEffect("SPEED:2");
        
        return kit;
    }
    
    private Kit createSumoKit() {
        Kit kit = new Kit("Sumo");
        kit.setDisplayName("§9Sumo");
        kit.setIcon("LEASH");
        kit.setFall(false);
        
        ItemStack[] items = new ItemStack[36];
        // Pas d'items pour sumo
        
        kit.setContents(items);
        
        ItemStack[] armor = new ItemStack[4];
        // Pas d'armure pour sumo
        
        kit.setArmor(armor);
        
        kit.addEffect("SPEED:1");
        
        return kit;
    }
    
    @SuppressWarnings("deprecation")
    private ItemStack createItem(Material material, int amount, String enchants) {
        ItemStack item = new ItemStack(material, amount);
        
        if (enchants != null && !enchants.isEmpty()) {
            String[] enchantArray = enchants.split(",");
            for (String enchant : enchantArray) {
                String[] parts = enchant.split(":");
                Enchantment e = Enchantment.getByName(parts[0].toUpperCase());
                int level = Integer.parseInt(parts[1]);
                if (e != null) {
                    item.addUnsafeEnchantment(e, level);
                }
            }
        }
        
        return item;
    }
    
    private ItemStack createItem(Material material, int amount) {
        return createItem(material, amount, null);
    }
    
    private ItemStack createPotion(int damageValue, int amount) {
        ItemStack potion = new ItemStack(Material.POTION, amount);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        
        if (meta != null) {
            // Convertir les anciennes valeurs de durabilité en effets modernes
            switch (damageValue) {
                case 16421 -> // Heal II
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1), true);
                case 8226 -> // Speed II (1:30)
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1800, 1), true);
                case 16388 -> // Poison
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 660, 0), true);
                case 16392 -> // Slowness
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1800, 0), true);
                case 16424 -> // Weakness
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1800, 0), true);
                default -> {
                }
            }
            potion.setItemMeta(meta);
        }
        
        return potion;
    }
    
    private ItemStack createSplashPotion(int damageValue, int amount) {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION, amount);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        
        if (meta != null) {
            // Convertir les anciennes valeurs en effets splash
            switch (damageValue) {
                case 16421 -> // Heal II Splash
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1), true);
                case 16388 -> // Poison Splash
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 660, 0), true);
                case 16392 -> // Slowness Splash
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1800, 0), true);
                case 16424 -> // Weakness Splash
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1800, 0), true);
                default -> {
                }
            }
            potion.setItemMeta(meta);
        }
        
        return potion;
    }
    
    public Kit getKit(String name) {
        return kits.get(name);
    }
    
    public Collection<Kit> getAllKits() {
        return kits.values();
    }
    
    public void saveKits() {
        // Sauvegarder les kits dans le fichier
        try {
            kitsConfig.save(kitsFile);
        } catch (java.io.IOException e) {
            plugin.getLogger().severe(String.format("Failed to save kits: %s", e.getMessage()));
        }
    }
}
