package fr.louis.practice;

import fr.louis.practice.commands.BuildCommand;
import fr.louis.practice.listeners.*;
import fr.louis.practice.managers.*;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PracticeCore extends JavaPlugin {

    @Getter
    private static PracticeCore instance;

    // Managers
    private PlayerManager playerManager;
    private MatchManager matchManager;
    private QueueManager queueManager;
    private PartyManager partyManager;
    private EloManager eloManager;
    private ArenaManager arenaManager;
    private KitManager kitManager;
    private CombatManager combatManager;
    private DuelManager duelManager;
    private CustomScoreboardManager customScoreboardManager;
    private InventoryManager inventoryManager;
    private KillstreakManager killstreakManager;
    private LeaderboardManager leaderboardManager;
    private RematchManager rematchManager;
    private InventorySnapshotManager inventorySnapshotManager;
    private SpectatorManager spectatorManager;
    private MongoManager mongoManager;
    private ClanManager clanManager;
    private QuestManager questManager;
    private TournamentManager tournamentManager;
    private ShopManager shopManager;
    private FFAManager ffaManager;
    private CosmeticManager cosmeticManager;
    private DailyRewardManager dailyRewardManager;
    private AchievementManager achievementManager;
    private SeasonManager seasonManager;
    private LoadoutManager loadoutManager;
    private ChatManager chatManager;
    private ChatFormatManager chatFormatManager;
    private TabListManager tabListManager;
    private NametagManager nametagManager;
    private ProfileManager profileManager;
    private CrateManager crateManager;
    private BoostManager boostManager;
    private FriendManager friendManager;
    private ReportManager reportManager;
    private PunishmentManager punishmentManager;
    private TitleManager titleManager;
    private WarpManager warpManager;
    private HomeManager homeManager;
    private TeleportManager teleportManager;
    private TrailManager trailManager;
    private BuildCommand buildCommand;
    private BotManager botManager;
    private ComboManager comboManager;
    private KillEffectManager killEffectManager;
    private HitSoundManager hitSoundManager;
    private DailyStreakManager dailyStreakManager;
    private StatisticsManager statisticsManager;
    private GlobalEventManager globalEventManager;
    private StaffModeManager staffModeManager;
    
    private Location spawnLocation;

    // Getters explicites pour nouveaux managers (compatibilité commandes)
    public MongoManager getMongoManager() { return mongoManager; }
    public ClanManager getClanManager() { return clanManager; }
    public QuestManager getQuestManager() { return questManager; }
    public TournamentManager getTournamentManager() { return tournamentManager; }
    public ShopManager getShopManager() { return shopManager; }
    public FFAManager getFFAManager() { return ffaManager; }
    public CosmeticManager getCosmeticManager() { return cosmeticManager; }
    public DailyRewardManager getDailyRewardManager() { return dailyRewardManager; }
    public AchievementManager getAchievementManager() { return achievementManager; }
    public SeasonManager getSeasonManager() { return seasonManager; }
    public LoadoutManager getLoadoutManager() { return loadoutManager; }
    public ChatManager getChatManager() { return chatManager; }
    public ChatFormatManager getChatFormatManager() { return chatFormatManager; }
    public TabListManager getTabListManager() { return tabListManager; }
    public NametagManager getNametagManager() { return nametagManager; }
    public ProfileManager getProfileManager() { return profileManager; }
    public CrateManager getCrateManager() { return crateManager; }
    public BoostManager getBoostManager() { return boostManager; }
    public FriendManager getFriendManager() { return friendManager; }
    public ReportManager getReportManager() { return reportManager; }
    public PunishmentManager getPunishmentManager() { return punishmentManager; }
    public TitleManager getTitleManager() { return titleManager; }
    public WarpManager getWarpManager() { return warpManager; }
    public HomeManager getHomeManager() { return homeManager; }
    public TeleportManager getTeleportManager() { return teleportManager; }
    public TrailManager getTrailManager() { return trailManager; }
    public BotManager getBotManager() { return botManager; }
    public ComboManager getComboManager() { return comboManager; }
    public KillEffectManager getKillEffectManager() { return killEffectManager; }
    public HitSoundManager getHitSoundManager() { return hitSoundManager; }
    public DailyStreakManager getDailyStreakManager() { return dailyStreakManager; }
    public StatisticsManager getStatisticsManager() { return statisticsManager; }
    public GlobalEventManager getGlobalEventManager() { return globalEventManager; }
    public StaffModeManager getStaffModeManager() { return staffModeManager; }
    
    // Getters pour les managers core
    public ArenaManager getArenaManager() { return arenaManager; }
    public KitManager getKitManager() { return kitManager; }
    public QueueManager getQueueManager() { return queueManager; }
    public MatchManager getMatchManager() { return matchManager; }
    public PartyManager getPartyManager() { return partyManager; }
    public EloManager getEloManager() { return eloManager; }
    public CustomScoreboardManager getCustomScoreboardManager() { return customScoreboardManager; }
    
    // Alias pour compatibilité
    public CustomScoreboardManager getScoreboardManager() {
        return customScoreboardManager;
    }

    @Override
    public void onEnable() {
        instance = this;
        
        // Chargement de la configuration
        saveDefaultConfig();
        
        // Initialisation des managers
        initializeManagers();
        
        // Enregistrement des listeners
        registerListeners();
        
        // Enregistrement des commandes
        registerCommands();
        
        // Charger le spawn
        loadSpawnLocation();
        
        getLogger().info("PracticeCore a été activé avec succès!");
        getLogger().info(String.format("Loaded %d arenas", arenaManager.getAllArenas().size()));
        getLogger().info(String.format("Loaded %d kits", kitManager.getAllKits().size()));
        getLogger().info(String.format("Loaded %d queues", queueManager.getAllQueues().size()));
    }

    @Override
    public void onDisable() {
        // MongoDB save
        if (mongoManager != null) {
            mongoManager.saveAll();
            mongoManager.close();
        }
        
        // Fin de tous les matchs en cours
        if (matchManager != null) {
            matchManager.endAllMatches();
        }
        
        // Arrêter le matchmaking
        if (queueManager != null) {
            queueManager.shutdown();
        }
        
        // Arrêter les leaderboards
        if (leaderboardManager != null) {
            leaderboardManager.shutdown();
        }
        
        getLogger().info("PracticeCore a été désactivé!");
    }

    private void initializeManagers() {
        // MongoDB first (others may depend on it)
        this.mongoManager = new MongoManager(this);
        
        this.playerManager = new PlayerManager(this);
        this.arenaManager = new ArenaManager(this);
        this.kitManager = new KitManager(this);
        this.eloManager = new EloManager(this);
        this.combatManager = new CombatManager(this);
        this.duelManager = new DuelManager(this);
        this.inventoryManager = new InventoryManager(this);
        this.killstreakManager = new KillstreakManager(this);
        this.leaderboardManager = new LeaderboardManager(this);
        this.rematchManager = new RematchManager(this);
        this.inventorySnapshotManager = new InventorySnapshotManager(this);
        this.spectatorManager = new SpectatorManager(this);
        this.clanManager = new ClanManager(this);
        this.questManager = new QuestManager(this);
        this.tournamentManager = new TournamentManager(this);
        this.shopManager = new ShopManager(this);
        this.ffaManager = new FFAManager(this);
        this.cosmeticManager = new CosmeticManager(this);
        this.dailyRewardManager = new DailyRewardManager(this);
        this.achievementManager = new AchievementManager(this);
        this.seasonManager = new SeasonManager(this);
        this.loadoutManager = new LoadoutManager(this);
        this.chatManager = new ChatManager(this);
        this.chatFormatManager = new ChatFormatManager(this);
        this.tabListManager = new TabListManager(this);
        this.nametagManager = new NametagManager(this);
        this.profileManager = new ProfileManager(this);
        this.crateManager = new CrateManager(this);
        this.boostManager = new BoostManager(this);
        this.friendManager = new FriendManager(this);
        this.reportManager = new ReportManager(this);
        this.punishmentManager = new PunishmentManager(this);
        this.titleManager = new TitleManager(this);
        this.warpManager = new WarpManager(this);
        this.homeManager = new HomeManager(this);
        this.teleportManager = new TeleportManager(this);
        this.trailManager = new TrailManager(this);
        this.buildCommand = new BuildCommand(this);
        this.botManager = new BotManager(this);
        this.comboManager = new ComboManager(this);
        this.killEffectManager = new KillEffectManager(this);
        this.hitSoundManager = new HitSoundManager(this);
        this.dailyStreakManager = new DailyStreakManager(this);
        this.statisticsManager = new StatisticsManager(this);
        this.globalEventManager = new GlobalEventManager(this);
        this.staffModeManager = new StaffModeManager(this);
        
        // Queue et Match managers doivent être initialisés après les autres
        this.killstreakManager = new KillstreakManager(this);
        this.matchManager = new MatchManager(this);
        this.queueManager = new QueueManager(this);
        this.partyManager = new PartyManager(this);
        this.customScoreboardManager = new CustomScoreboardManager(this);
        
        // Update leaderboards après 30 secondes
        getServer().getScheduler().runTaskLater(this, () -> {
            leaderboardManager.updateLeaderboards();
        }, 20L * 30);
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvents(new PlayerConnectionListener(this), this);
        pm.registerEvents(new CombatListener(this), this);
        pm.registerEvents(new DeathListener(this), this);
        pm.registerEvents(new InteractionListener(this), this);
        pm.registerEvents(new MiscListener(this), this);
        pm.registerEvents(new BuildListener(this), this);
        pm.registerEvents(new GUIListener(this), this);
        pm.registerEvents(new CosmeticListener(this), this);
        pm.registerEvents(new FreezeListener(), this);
        pm.registerEvents(new StatisticsListener(this), this);
        pm.registerEvents(new StaffModeListener(this), this);
        pm.registerEvents(new StaffGUIListener(), this);
    }
    
    private void registerCommands() {
        java.util.Objects.requireNonNull(getCommand("queue")).setExecutor(new fr.louis.practice.commands.QueueCommand(this));
        java.util.Objects.requireNonNull(getCommand("party")).setExecutor(new fr.louis.practice.commands.PartyCommand(this));
        java.util.Objects.requireNonNull(getCommand("duel")).setExecutor(new fr.louis.practice.commands.DuelCommand(this));
        java.util.Objects.requireNonNull(getCommand("stats")).setExecutor(new fr.louis.practice.commands.StatsCommand(this));
        java.util.Objects.requireNonNull(getCommand("spawn")).setExecutor(new fr.louis.practice.commands.SpawnCommand(this));
        java.util.Objects.requireNonNull(getCommand("ping")).setExecutor(new fr.louis.practice.commands.PingCommand());
        java.util.Objects.requireNonNull(getCommand("leaderboard")).setExecutor(new fr.louis.practice.commands.LeaderboardCommand(this));
        java.util.Objects.requireNonNull(getCommand("rematch")).setExecutor(new fr.louis.practice.commands.RematchCommand(this));
        java.util.Objects.requireNonNull(getCommand("spectate")).setExecutor(new fr.louis.practice.commands.SpectateCommand(this));
        java.util.Objects.requireNonNull(getCommand("stopspec")).setExecutor(new fr.louis.practice.commands.StopSpectateCommand(this));
        java.util.Objects.requireNonNull(getCommand("inventory")).setExecutor(new fr.louis.practice.commands.InventoryCommand(this));
        java.util.Objects.requireNonNull(getCommand("settings")).setExecutor(new fr.louis.practice.commands.SettingsCommand(this));
        java.util.Objects.requireNonNull(getCommand("clan")).setExecutor(new fr.louis.practice.commands.ClanCommand(this));
        java.util.Objects.requireNonNull(getCommand("tournament")).setExecutor(new fr.louis.practice.commands.TournamentCommand(this));
        java.util.Objects.requireNonNull(getCommand("quests")).setExecutor(new fr.louis.practice.commands.QuestsCommand(this));
        java.util.Objects.requireNonNull(getCommand("shop")).setExecutor(new fr.louis.practice.commands.ShopCommand(this));
        java.util.Objects.requireNonNull(getCommand("coins")).setExecutor(new fr.louis.practice.commands.CoinsCommand(this));
        java.util.Objects.requireNonNull(getCommand("ffa")).setExecutor(new fr.louis.practice.commands.FFACommand(this));
        java.util.Objects.requireNonNull(getCommand("daily")).setExecutor(new fr.louis.practice.commands.DailyCommand(this));
        java.util.Objects.requireNonNull(getCommand("achievements")).setExecutor(new fr.louis.practice.commands.AchievementsCommand(this));
        java.util.Objects.requireNonNull(getCommand("season")).setExecutor(new fr.louis.practice.commands.SeasonCommand(this));
        java.util.Objects.requireNonNull(getCommand("loadout")).setExecutor(new fr.louis.practice.commands.LoadoutCommand(this));
        java.util.Objects.requireNonNull(getCommand("chat")).setExecutor(new fr.louis.practice.commands.ChatCommand(this));
        java.util.Objects.requireNonNull(getCommand("profile")).setExecutor(new fr.louis.practice.commands.ProfileCommand(this));
        java.util.Objects.requireNonNull(getCommand("crate")).setExecutor(new fr.louis.practice.commands.CrateCommand(this));
        java.util.Objects.requireNonNull(getCommand("boost")).setExecutor(new fr.louis.practice.commands.BoostCommand(this));
        java.util.Objects.requireNonNull(getCommand("friend")).setExecutor(new fr.louis.practice.commands.FriendCommand(this));
        java.util.Objects.requireNonNull(getCommand("report")).setExecutor(new fr.louis.practice.commands.ReportCommand(this));
        java.util.Objects.requireNonNull(getCommand("build")).setExecutor(buildCommand);
        java.util.Objects.requireNonNull(getCommand("ban")).setExecutor(new fr.louis.practice.commands.admin.PunishCommand(this, fr.louis.practice.models.Punishment.PunishmentType.BAN));
        java.util.Objects.requireNonNull(getCommand("kick")).setExecutor(new fr.louis.practice.commands.admin.PunishCommand(this, fr.louis.practice.models.Punishment.PunishmentType.KICK));
        java.util.Objects.requireNonNull(getCommand("mute")).setExecutor(new fr.louis.practice.commands.admin.PunishCommand(this, fr.louis.practice.models.Punishment.PunishmentType.MUTE));
        java.util.Objects.requireNonNull(getCommand("warn")).setExecutor(new fr.louis.practice.commands.admin.PunishCommand(this, fr.louis.practice.models.Punishment.PunishmentType.WARNING));
        java.util.Objects.requireNonNull(getCommand("punishhistory")).setExecutor(new fr.louis.practice.commands.admin.PunishHistoryCommand(this));
        java.util.Objects.requireNonNull(getCommand("title")).setExecutor(new fr.louis.practice.commands.TitleCommand(this));
        
        // Teleport commands
        java.util.Objects.requireNonNull(getCommand("warp")).setExecutor(new fr.louis.practice.commands.WarpCommand(this));
        java.util.Objects.requireNonNull(getCommand("home")).setExecutor(new fr.louis.practice.commands.HomeCommand(this));
        java.util.Objects.requireNonNull(getCommand("sethome")).setExecutor(new fr.louis.practice.commands.SetHomeCommand(this));
        java.util.Objects.requireNonNull(getCommand("delhome")).setExecutor(new fr.louis.practice.commands.DelHomeCommand(this));
        java.util.Objects.requireNonNull(getCommand("homes")).setExecutor(new fr.louis.practice.commands.HomesCommand(this));
        java.util.Objects.requireNonNull(getCommand("tpa")).setExecutor(new fr.louis.practice.commands.TpaCommand(this));
        java.util.Objects.requireNonNull(getCommand("tpahere")).setExecutor(new fr.louis.practice.commands.TpaHereCommand(this));
        java.util.Objects.requireNonNull(getCommand("tpaccept")).setExecutor(new fr.louis.practice.commands.TpAcceptCommand(this));
        java.util.Objects.requireNonNull(getCommand("tpdeny")).setExecutor(new fr.louis.practice.commands.TpDenyCommand(this));
        java.util.Objects.requireNonNull(getCommand("back")).setExecutor(new fr.louis.practice.commands.BackCommand(this));
        
        // Admin commands
        java.util.Objects.requireNonNull(getCommand("gamemode")).setExecutor(new fr.louis.practice.commands.admin.GamemodeCommand());
        java.util.Objects.requireNonNull(getCommand("fly")).setExecutor(new fr.louis.practice.commands.admin.FlyCommand(this));
        java.util.Objects.requireNonNull(getCommand("god")).setExecutor(new fr.louis.practice.commands.admin.GodCommand(this));
        java.util.Objects.requireNonNull(getCommand("heal")).setExecutor(new fr.louis.practice.commands.admin.HealCommand());
        java.util.Objects.requireNonNull(getCommand("feed")).setExecutor(new fr.louis.practice.commands.admin.FeedCommand());
        java.util.Objects.requireNonNull(getCommand("tp")).setExecutor(new fr.louis.practice.commands.admin.TpCommand());
        java.util.Objects.requireNonNull(getCommand("tphere")).setExecutor(new fr.louis.practice.commands.admin.TpHereCommand());
        java.util.Objects.requireNonNull(getCommand("tpall")).setExecutor(new fr.louis.practice.commands.admin.TpAllCommand());
        java.util.Objects.requireNonNull(getCommand("invsee")).setExecutor(new fr.louis.practice.commands.admin.InvseeCommand(this));
        java.util.Objects.requireNonNull(getCommand("clear")).setExecutor(new fr.louis.practice.commands.admin.ClearCommand());
        java.util.Objects.requireNonNull(getCommand("broadcast")).setExecutor(new fr.louis.practice.commands.admin.BroadcastCommand());
        java.util.Objects.requireNonNull(getCommand("vanish")).setExecutor(new fr.louis.practice.commands.admin.VanishCommand(this));
        java.util.Objects.requireNonNull(getCommand("speed")).setExecutor(new fr.louis.practice.commands.admin.SpeedCommand());
        
        // Economy commands
        java.util.Objects.requireNonNull(getCommand("eco")).setExecutor(new fr.louis.practice.commands.admin.EcoCommand(this));
        java.util.Objects.requireNonNull(getCommand("pay")).setExecutor(new fr.louis.practice.commands.PayCommand(this));
        java.util.Objects.requireNonNull(getCommand("baltop")).setExecutor(new fr.louis.practice.commands.BalTopCommand(this));
        
        // World management
        java.util.Objects.requireNonNull(getCommand("time")).setExecutor(new fr.louis.practice.commands.admin.TimeCommand());
        java.util.Objects.requireNonNull(getCommand("weather")).setExecutor(new fr.louis.practice.commands.admin.WeatherCommand());
        
        // Extra admin commands
        java.util.Objects.requireNonNull(getCommand("sudo")).setExecutor(new fr.louis.practice.commands.admin.SudoCommand());
        java.util.Objects.requireNonNull(getCommand("killall")).setExecutor(new fr.louis.practice.commands.admin.KillAllCommand());
        java.util.Objects.requireNonNull(getCommand("give")).setExecutor(new fr.louis.practice.commands.admin.GiveCommand());
        java.util.Objects.requireNonNull(getCommand("enderchest")).setExecutor(new fr.louis.practice.commands.admin.EnderchestCommand());
        java.util.Objects.requireNonNull(getCommand("freeze")).setExecutor(new fr.louis.practice.commands.admin.FreezeCommand());
        java.util.Objects.requireNonNull(getCommand("kickall")).setExecutor(new fr.louis.practice.commands.admin.KickAllCommand());
        java.util.Objects.requireNonNull(getCommand("rename")).setExecutor(new fr.louis.practice.commands.admin.RenameCommand());
        java.util.Objects.requireNonNull(getCommand("lore")).setExecutor(new fr.louis.practice.commands.admin.LoreCommand());
        
        // Kohi features
        java.util.Objects.requireNonNull(getCommand("trail")).setExecutor(new fr.louis.practice.commands.TrailCommand(this));
        java.util.Objects.requireNonNull(getCommand("bot")).setExecutor(new fr.louis.practice.commands.BotCommand(this));
        java.util.Objects.requireNonNull(getCommand("killeffect")).setExecutor(new fr.louis.practice.commands.KillEffectCommand(this));
        java.util.Objects.requireNonNull(getCommand("hitsound")).setExecutor(new fr.louis.practice.commands.HitSoundCommand(this));
        java.util.Objects.requireNonNull(getCommand("matchhistory")).setExecutor(new fr.louis.practice.commands.MatchHistoryCommand(this));
        
        // Staff commands
        java.util.Objects.requireNonNull(getCommand("staffmode")).setExecutor(new fr.louis.practice.commands.admin.StaffModeCommand(this));
        java.util.Objects.requireNonNull(getCommand("alert")).setExecutor(new fr.louis.practice.commands.admin.AlertCommand());
        java.util.Objects.requireNonNull(getCommand("sc")).setExecutor(new fr.louis.practice.commands.admin.StaffChatCommand());
        java.util.Objects.requireNonNull(getCommand("event")).setExecutor(new fr.louis.practice.commands.admin.EventCommand(this));
        
        java.util.Objects.requireNonNull(getCommand("setspawn")).setExecutor(new fr.louis.practice.commands.admin.SetSpawnCommand(this));
        java.util.Objects.requireNonNull(getCommand("arena")).setExecutor(new fr.louis.practice.commands.admin.ArenaCommand(this));
    }
    
    private void loadSpawnLocation() {
        String worldName = getConfig().getString("general.spawn-world", "world");
        if (worldName == null) worldName = "world";
        World world = getServer().getWorld(worldName);
        
        if (world == null) {
            getLogger().warning(String.format("Spawn world '%s' not found! Using default world.", worldName));
            world = getServer().getWorlds().get(0);
        }
        
        double x = getConfig().getDouble("general.spawn-location.x", 0.5);
        double y = getConfig().getDouble("general.spawn-location.y", 100.0);
        double z = getConfig().getDouble("general.spawn-location.z", 0.5);
        float yaw = (float) getConfig().getDouble("general.spawn-location.yaw", 0.0);
        float pitch = (float) getConfig().getDouble("general.spawn-location.pitch", 0.0);
        
        this.spawnLocation = new Location(world, x, y, z, yaw, pitch);
    }
    
    @SuppressWarnings("ConstantConditions")
    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
        
        var world = location.getWorld();
        if (world != null) {
            getConfig().set("general.spawn-world", world.getName());
        }
        getConfig().set("general.spawn-location.x", location.getX());
        getConfig().set("general.spawn-location.y", location.getY());
        getConfig().set("general.spawn-location.z", location.getZ());
        getConfig().set("general.spawn-location.yaw", location.getYaw());
        getConfig().set("general.spawn-location.pitch", location.getPitch());
        saveConfig();
    }
    
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}
