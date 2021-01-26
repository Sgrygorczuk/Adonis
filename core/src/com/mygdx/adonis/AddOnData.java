package com.mygdx.adonis;


public enum AddOnData {
    // Weapons
    GUN(1, "GUN"),
    LASER_GUN(2, "LASER_GUN"), // requires energy
    MISSILE_LAUNCHER(3, "MISSILE_LAUNCHER"), // requires missile holder
    // Upgrades
    HEALTH_BAR_GUI(4, "HEALTH_BAR_GUI"), // shows health
    ENERGY_BAR_GUI(5, "ENERGY_BAR_GUI"), // shows battery
    HEALING_STATION(6, "HEALING_STATION"), // hold health packs that you can use later
    ENERGY_STATION(7, "ENERGY_STATION"), // Holds energy cells that you can collect
    SHIELD(8, "SHIELD"), // battery powered (uses energy on hit or while active)
    BATTERY(9, "BATTERY"), // helps you use energy weapons/upgrades
    WEAPON_BOOST(10, "WEAPON_BOOST"), // upgrades weapon damaage
    WEAPON_UPGRADE(11, "WEAPON_UPGRADE"), // upgrades hitbox of weapons
    MISSILE_HOLDER(12, "MISSILE_HOLDER"), // Holds missile (needed for Missile Launcher)
    MiSSILE_CATCHER(13, "MISSILE_CATCHER"); // Catches missile at the expense of energy

    private int WEAPON_END_IND = 3;


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

    public void onInstall(){
        switch(this.name) {
            case "HEALTH_BAR_GUI":
                // Allows ship to see Health Bar
                break;
            case "ENERGY_BAR_GUI":
                // Allows ship to see Energy Bar
                break;
            case "HEALING_STATION":
                // Allows ship to hold and use health packs they pick up
                break;
            case "ENERGY_STATION":
                // Allows ship to hold and use energy packs they pick up
                break;
            case "SHIELD":
                // Has a shield in a certain direction of installation
                // Shield either consumes energy while on or when it gets hit
                break;
            case "BATTERY":
                // Holds energy and lets you use weapons and upgrades that require it
                break;
            case "WEAPON_BOOST":
                // Upgrades the damage of weapons
                break;
            case "WEAPON_UPGRADE":
                // Upgrades the hitboxes of weapons
                break;
            case "MISSILE_HOLDER":
                // Allows user to hold missiles which they can pickup
                break;
            case "MISSILE_CATCHER":
                // Allows user to suck the missile incoming but consumes energy
                break;
            default:
                // Doesn't have an installation property
                break;
        }
    }

    public void onUse(){
        switch(this.name){
            case "GUN":
                // Fire Gun
                break;
            case "LASER_GUN":
                // Fire Laser Gun if required energy is met
                break;
            case "MISSILE_LAUNCHER":
                // Fires Missile
                break;
            case "HEALING_STATION":
                // Uses Healing Kit if they have a healing kit
                break;
            case "ENERGY_STATION":
                // Regenerates energy if they have an energy kit
                break;
            case "SHIELD":
                // This is only required if shield uses energy on press/hold
                break;
            case "MISSILE_CATCHER":
                // Catches missile in a short time period and puts it on cooldown
                break;
            default:
                // Has no application on use
                break;
        }
    }

    public void onDestroy(){
        switch(this.name) {
            case "HEALTH_BAR_GUI":
                // Removes Health Bar GUI
                break;
            case "ENERGY_BAR_GUI":
                // Removes Energy Bar GUI
                break;
            case "HEALING_STATION":
                // Decrease max health kit capacity
                break;
            case "ENERGY_STATION":
                // Decrease max energy kit capacity
                break;
            case "SHIELD":
                // remove use of shield
                break;
            case "BATTERY":
                // Removes their max energy
                break;
            case "WEAPON_BOOST":
                // Removes damage upgrade
                break;
            case "WEAPON_UPGRADE":
                // removes hitbox upgrade
                break;
            case "MISSILE_HOLDER":
                // decrease their max missile capacity
                break;
            case "MISSILE_CATCHER":
                // Allows user to suck the missile incoming but consumes energy
                break;
            default:
                // Doesn't have an uninstall property
                break;
        }
    }
}
