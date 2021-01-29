package com.mygdx.adonis;


public enum AddOnData {
    UNKNOWN(0, "UNKNOWN","UNKNOWN", false),
    // Weapons
    GUN(1, "GUN", "Shoots bullets",true),
    LASER_GUN(2, "LASER_GUN", "Shoots laser", true), // requires energy
    MISSILE_LAUNCHER(3, "MISSILE_LAUNCHER","Shoots missiles", true), // requires missile holder
    // Upgrades
    HEALTH_BAR_GUI(50, "HEALTH_BAR_GUI", "Shows HP Bar", false), // shows health
    ENERGY_BAR_GUI(51, "ENERGY_BAR_GUI", "Shows Energy Bar", false), // shows battery
    HEALING_STATION(52, "HEALING_STATION", "Holds Health Packs", false), // hold health packs that you can use later
    ENERGY_STATION(53, "ENERGY_STATION", "Holds Energy Packs", false), // Holds energy cells that you can collect
    SHIELD(54, "SHIELD", "Blocks Bullets with Energy", false), // battery powered (uses energy on hit or while active)
    BATTERY(55, "BATTERY", "Holds Energy", false), // helps you use energy weapons/upgrades
    CHARGER(56, "CHARGER", "Increased Energy Rate",false), // charges battery faster
    WEAPON_BOOST(57, "WEAPON_BOOST", "Increase Weapon Damage", false), // upgrades weapon damaage
    WEAPON_UPGRADE(58, "WEAPON_UPGRADE", "Inrease Weapon Hitbox", false), // upgrades hitbox of weapons
    MISSILE_HOLDER(59, "MISSILE_HOLDER", "Holds Missile Ammo", false), // Holds missile (needed for Missile Launcher)
    MISSILE_CATCHER(60, "MISSILE_CATCHER", "Catches Missile with Energy", false); // Catches missile at the expense of energy

    private final int id;
    private final String name;
    private final String description;
    private final boolean isWeapon;

    AddOnData(int id, String name, String description, boolean isWeapon) {
        this.id = id;
        this.name = name;
        this.isWeapon = isWeapon;
        this.description = description;
    }

    public static AddOnData getById(int id) {
        for (AddOnData a : values()) {
            if (a.id == id) return a;
        }
        return UNKNOWN;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isWeapon() {
        return isWeapon;
    }
}
