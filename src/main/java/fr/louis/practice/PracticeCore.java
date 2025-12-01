package fr.louis.practice;

import fr.louis.practice.commands.*;
import fr.louis.practice.listeners.*;
import fr.louis.practice.managers.*;
import fr.louis.practice.utils.ConfigManager;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PracticeCore extends JavaPlugin {

    @Getter
    private static PracticeCore instance;

    // Managers
    private ConfigManager configManager;
    private KnockbackManager knockbackManager;
    private MatchManager matchManager;
    private QueueManager queueManager;
    private PartyManager partyManager;
    private EloManager eloManager;
    private StatisticsManager statisticsManager;
    private SpectatorManager spectatorManager;
    private EventManager eventManager;
    private ArenaManager arenaManager;
    private KitManager kitManager;
    private CombatManager combatManager;
    private ScoreboardManager scoreboardManager;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Chargement de la configuration
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        
        // Initialisation des managers
        initializeManagers();
        
        // Enregistrement des commandes
        registerCommands();
        
        // Enregistrement des listeners
        registerListeners();
        
        getLogger().info("PracticeCore a été activé avec succès!");
        getLogger().info("Profil de knockback: " + getConfig().getString("knockback.profile"));
    }

    @Override
    public void onDisable() {
        // Sauvegarde des données
        if (databaseManager != null) {
            databaseManager.saveAll();
            databaseManager.close();
        }
        
        // Fin de tous les matchs en cours
        if (matchManager != null) {
            matchManager.endAllMatches();
        }
        
        getLogger().info("PracticeCore a été désactivé!");
    }

    private void initializeManagers() {
        this.databaseManager = new DatabaseManager(this);
        this.knockbackManager = new KnockbackManager(this);
        this.arenaManager = new ArenaManager(this);
        this.kitManager = new KitManager(this);
        this.eloManager = new EloManager(this);
        this.statisticsManager = new StatisticsManager(this);
        this.combatManager = new CombatManager(this);
        this.matchManager = new MatchManager(this);
        this.queueManager = new QueueManager(this);
        this.partyManager = new PartyManager(this);
        this.spectatorManager = new SpectatorManager(this);
        this.eventManager = new EventManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
    }

    private void registerCommands() {
        getCommand("party").setExecutor(new PartyCommand(this));
        getCommand("duel").setExecutor(new DuelCommand(this));
        getCommand("queue").setExecutor(new QueueCommand(this));
        getCommand("event").setExecutor(new EventCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("leaderboard").setExecutor(new LeaderboardCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("spectate").setExecutor(new SpectateCommand(this));
        getCommand("settings").setExecutor(new SettingsCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this));
        getCommand("elo").setExecutor(new EloCommand(this));
        getCommand("ping").setExecutor(new PingCommand(this));
        getCommand("match").setExecutor(new MatchCommand(this));
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerQuitListener(this), this);
        pm.registerEvents(new EntityDamageListener(this), this);
        pm.registerEvents(new EntityDamageByEntityListener(this), this);
        pm.registerEvents(new PlayerDeathListener(this), this);
        pm.registerEvents(new PlayerRespawnListener(this), this);
        pm.registerEvents(new PlayerMoveListener(this), this);
        pm.registerEvents(new PlayerInteractListener(this), this);
        pm.registerEvents(new PlayerDropItemListener(this), this);
        pm.registerEvents(new PlayerPickupItemListener(this), this);
        pm.registerEvents(new FoodLevelChangeListener(this), this);
        pm.registerEvents(new PlayerCommandPreprocessListener(this), this);
        pm.registerEvents(new BlockBreakListener(this), this);
        pm.registerEvents(new BlockPlaceListener(this), this);
        pm.registerEvents(new ProjectileLaunchListener(this), this);
        pm.registerEvents(new ProjectileHitListener(this), this);
        pm.registerEvents(new PotionSplashListener(this), this);
        pm.registerEvents(new InventoryClickListener(this), this);
    }
}
