package com.mygdx.adonis;


/**
 * Breaks down the different addOns and their properties
 */
public enum AddOnData {
    UNKNOWN(5, "UNKNOWN", "Unknown", "UNKNOWN", false),
    // Weapons
    //GUN(1, "GUN", "Faster Fire Rate",true),
    //LASER_GUN(2, "LASER_GUN", "Shoots laser", true), // requires energy
    //MISSILE_LAUNCHER(3, "MISSILE_LAUNCHER","Shoots missiles", true), // requires missile holder
    // Upgrades
    //HEALTH_BAR_GUI(0, "HEALTH_BAR_GUI","Health Vision" , "Shows HP Bar", false), // shows health
    //ENERGY_BAR_GUI(1, "ENERGY_BAR_GUI","Energy Vision", "Shows Energy Bar", false), // shows battery
    //HEALING_STATION(52, "HEALING_STATION", "Holds Health Packs", false), // hold health packs that you can use later
    //ENERGY_STATION(53, "ENERGY_STATION", "Holds Energy Packs", false), // Holds energy cells that you can collect
    SHIELD(0, "SHIELD", "Shield", "Blocks Bullets with Energy", false), // battery powered (uses energy on hit or while active)
    SPEED(2, "SPEED", "Speed" , "Increases Speed", false), // helps you use energy weapons/upgrades
    DAMAGE(1, "DAMAGE", "Damage","Increases Damage",false); // charges battery faster
    //SHIELD(2, "SHIELD", "Shield", "Blocks Bullets with Energy", false), // battery powered (uses energy on hit or while active)
    //BATTERY(3, "BATTERY", "Battery" , "Holds Energy", false), // helps you use energy weapons/upgrades
    //CHARGER(5, "CHARGER", "Charger","Increased Energy Rate",false), // charges battery faster
    //WEAPON_BOOST(4, "WEAPON_BOOST", "Weapon Boost", "Increase Weapon Damage", false); // upgrades weapon damaage
    //WEAPON_UPGRADE(58, "WEAPON_UPGRADE", "Inrease Weapon Hitbox", false), // upgrades hitbox of weapons
    //MISSILE_HOLDER(59, "MISSILE_HOLDER", "Holds Missile Ammo", false), // Holds missile (needed for Missile Launcher)
    //MISSILE_CATCHER(60, "MISSILE_CATCHER", "Catches Missile with Energy", false); // Catches missile at the expense of energy

    private final int id;               //unique id for addOn
    private final String objectName;    //What it's called
    private final String name;          //What will be displayed to user
    private final String description;   //A description shown to user
    private final boolean isWeapon;     //Tells us if it is a weapon upgrade

    /**
     * Constructor
     * @param id what's the item id
     * @param objectName the name
     * @param name displayed to the player
     * @param description displayed to the player
     * @param isWeapon is it a new weapon type
     */
    AddOnData(int id, String objectName, String name, String description, boolean isWeapon) {
        this.id = id;
        this.objectName = objectName;
        this.name = name;
        this.isWeapon = isWeapon;
        this.description = description;
    }

    /**
     * Gets the data for when player equips or discard the addOn
     * @param id unique addOn id
     * @return gives back the desired addOn
     */
    public static AddOnData getById(int id) {
        for (AddOnData a : values()) {
            if (a.id == id) return a;
        }
        return UNKNOWN;
    }

    /**
     * Returns object name
     * @return object Name
     */
    public String getObjectName() { return objectName; }

    /**
     * Returns user display Name
     * @return user display Name
     */
    public String getName(){return name;}

    /**
     * Returns user display description
     * @return user display description
     */
    public String getDescription() { return description; }

    /**
     * @return is wepon
     */
    public boolean isWeapon() { return isWeapon; }

    /**
     * @return the id
     */
    public int getId(){return id;}
}
