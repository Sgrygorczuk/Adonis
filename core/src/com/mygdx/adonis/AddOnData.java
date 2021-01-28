package com.mygdx.adonis;


public enum AddOnData {
    UNKNOWN(0, "UNKNOWN"),
    // Weapons
    GUN(1, "GUN"),
    LASER_GUN(2, "LASER_GUN"), // requires energy
    MISSILE_LAUNCHER(3, "MISSILE_LAUNCHER"), // requires missile holder
    // Upgrades
    HEALTH_BAR_GUI(50, "HEALTH_BAR_GUI"), // shows health
    ENERGY_BAR_GUI(51, "ENERGY_BAR_GUI"), // shows battery
    HEALING_STATION(52, "HEALING_STATION"), // hold health packs that you can use later
    ENERGY_STATION(53, "ENERGY_STATION"), // Holds energy cells that you can collect
    SHIELD(54, "SHIELD"), // battery powered (uses energy on hit or while active)
    BATTERY(55, "BATTERY"), // helps you use energy weapons/upgrades
    WEAPON_BOOST(56, "WEAPON_BOOST"), // upgrades weapon damaage
    WEAPON_UPGRADE(57, "WEAPON_UPGRADE"), // upgrades hitbox of weapons
    MISSILE_HOLDER(58, "MISSILE_HOLDER"), // Holds missile (needed for Missile Launcher)
    MISSILE_CATCHER(59, "MISSILE_CATCHER"); // Catches missile at the expense of energy

    private int WEAPON_END_IND = 49; // we're never gonna make 50 weapons
    private int id;
    private String name;
    private String category;

    AddOnData(int id, String name){
        String category;
        this.id = id;
        this.name = name;
        if(id <= WEAPON_END_IND){ // Change this when we add more weapons
            this.category = "Weapons";
        } else {
            this.category = "Upgrades";
        }
    }

    public static AddOnData getById(int id){
        for(AddOnData a : values()){
            if (a.id == id) return a;
        }
        return UNKNOWN;
    }


    public String getName() {
        return name;
    }
}
