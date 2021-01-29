package com.mygdx.adonis;


public enum AddOnData {
    UNKNOWN(0, "UNKNOWN", false),
    // Weapons
    GUN(1, "GUN", true),
    LASER_GUN(2, "LASER_GUN", true), // requires energy
    MISSILE_LAUNCHER(3, "MISSILE_LAUNCHER", true), // requires missile holder
    // Upgrades
    HEALTH_BAR_GUI(50, "HEALTH_BAR_GUI", false), // shows health
    ENERGY_BAR_GUI(51, "ENERGY_BAR_GUI", false), // shows battery
    HEALING_STATION(52, "HEALING_STATION", false), // hold health packs that you can use later
    ENERGY_STATION(53, "ENERGY_STATION", false), // Holds energy cells that you can collect
    SHIELD(54, "SHIELD", false), // battery powered (uses energy on hit or while active)
    BATTERY(55, "BATTERY", false), // helps you use energy weapons/upgrades
    WEAPON_BOOST(56, "WEAPON_BOOST", false), // upgrades weapon damaage
    WEAPON_UPGRADE(57, "WEAPON_UPGRADE", false), // upgrades hitbox of weapons
    MISSILE_HOLDER(58, "MISSILE_HOLDER", false), // Holds missile (needed for Missile Launcher)
    MISSILE_CATCHER(59, "MISSILE_CATCHER", false); // Catches missile at the expense of energy

    private final int id;
    private final String name;
    private final boolean isWeapon;

    AddOnData(int id, String name, boolean isWeapon) {
        this.id = id;
        this.name = name;
        this.isWeapon = isWeapon;
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

    public boolean isWeapon() {
        return isWeapon;
    }
}
