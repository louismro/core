package fr.louis.practice.models;

public class GlobalEvent {
    private final String id;
    private final EventType type;
    private final String name;
    private final String description;
    private final long startTime;
    private final long endTime;
    private final double multiplier;
    private boolean active;
    
    public enum EventType {
        DOUBLE_XP("§b2x XP", 2.0),
        DOUBLE_COINS("§62x Coins", 2.0),
        TRIPLE_XP("§d3x XP", 3.0),
        HAPPY_HOUR("§e§lHAPPY HOUR", 1.5),
        WEEKEND_BOOST("§a§lWEEKEND", 2.0),
        SPECIAL("§5§lSPECIAL", 2.5);
        
        private final String displayName;
        private final double defaultMultiplier;
        
        EventType(String displayName, double defaultMultiplier) {
            this.displayName = displayName;
            this.defaultMultiplier = defaultMultiplier;
        }
        
        public String getDisplayName() { return displayName; }
        public double getDefaultMultiplier() { return defaultMultiplier; }
    }
    
    public GlobalEvent(String id, EventType type, String name, String description, long durationMinutes) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + (durationMinutes * 60 * 1000);
        this.multiplier = type.getDefaultMultiplier();
        this.active = true;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > endTime;
    }
    
    public long getTimeRemaining() {
        return Math.max(0, endTime - System.currentTimeMillis());
    }
    
    public String getTimeRemainingFormatted() {
        long remaining = getTimeRemaining();
        long hours = remaining / (60 * 60 * 1000);
        long minutes = (remaining % (60 * 60 * 1000)) / (60 * 1000);
        
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        return minutes + "m";
    }

    // Getters
    public String getId() { return id; }
    public EventType getType() { return type; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public double getMultiplier() { return multiplier; }
    public boolean isActive() { return active; }

    // Setters
    public void setActive(boolean active) { this.active = active; }
}

