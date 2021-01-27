package com.mygdx.adonis;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static com.mygdx.adonis.Consts.TILE_HEIGHT;
import static com.mygdx.adonis.Consts.TILE_WIDTH;

public abstract class Ship {
    public Rectangle hitbox;
    public Vector2 velocity;
    public Alignment align;
    public int maxHealth;
    public int health;
    public int maxEnergy;
    public int energy;
    public Direction dir;
    protected Array<AddOnData> addOns;
    protected Array<Bullet> bulletsFired;
    protected Texture texture;
    public boolean healthBarVisible = false;
    public boolean energyBarVisible = false;

    public Ship(Texture texture, float initX, float initY, Alignment align) {
        // can multiply e.g. by 1.5, 1.2 to get more or less health
        this.align = align;
        this.maxHealth = 100;
        this.health = 100;
        this.maxEnergy = 0;
        this.energy = 0;
        this.texture = texture;
        this.hitbox = new Rectangle(initX, initY, TILE_WIDTH, TILE_HEIGHT);
        this.velocity = new Vector2(0, 0);
        this.addOns = new Array<>();
        this.bulletsFired = new Array<>();
    }

    // collision isn't as simple as checking a single hitbox since each ship has multiple addons
    public boolean isColliding(Rectangle other) {
        // todo addons
        return this.hitbox.contains(other);
    }

    public void takeDamage(int amt) {
        this.health -= amt;
    }

    // TODO change velocity depending on game stuff
    public void update(float delta) {
        for (AddOnData addOn : this.addOns) {
             // addOn.update(delta);
        }
        if(this.health > this.maxHealth){
            this.health = this.maxHealth;
        }
        if(this.maxEnergy > 0 && this.energy < this.maxEnergy){
            this.energy++;
        }
        if(this.energy > this.maxEnergy){
            this.energy = this.maxEnergy;
        }

        velocity.x = this.dir.getX();
        velocity.y = this.dir.getY();

        float newX = hitbox.getX() + (velocity.x * delta * TILE_WIDTH*5);
        float newY = hitbox.getY() + (velocity.y * delta * TILE_HEIGHT*5);

        hitbox.setPosition(newX, newY);
    }

    public void draw(SpriteBatch spriteBatch) {
        for (AddOnData addOn : this.addOns) {
             // addOn.draw(spriteBatch);
        }

        spriteBatch.draw(this.texture, this.hitbox.x, this.hitbox.y, TILE_WIDTH, TILE_HEIGHT);
    }

    public void move(Direction dir) {
        this.dir = dir;
    }

    public void fire(){
        for( AddOnData addOn : this.addOns){
//            System.out.println("Firing: "+addOn.name());
            this.onUse(addOn);
        }
    }

    public void onInstall(int id){
        AddOnData newAddOn = AddOnData.getById(id);
        this.addOns.add(newAddOn);
        System.out.println(newAddOn.name());
        // TODO: install the addons - Paul
        switch(newAddOn) {
            case HEALTH_BAR_GUI:
                // Allows ship to see Health Bar
                healthBarVisible = true;
                break;
            case ENERGY_BAR_GUI:
                // Allows ship to see Energy Bar
                energyBarVisible = true;
                break;
            case HEALING_STATION:
                // Allows ship to hold and use health packs they pick up
                break;
            case ENERGY_STATION:
                // Allows ship to hold and use energy packs they pick up
                break;
            case SHIELD:
                // Has a shield in a certain direction of installation
                // Shield either consumes energy while on or when it gets hit
                break;
            case BATTERY:
                // Holds energy and lets you use weapons and upgrades that require it
                this.maxEnergy += 100;
                break;
            case WEAPON_BOOST:
                // Upgrades the damage of weapons
                break;
            case WEAPON_UPGRADE:
                // Upgrades the hitboxes of weapons
                break;
            case MISSILE_HOLDER:
                // Allows user to hold missiles which they can pickup
                break;
            case MiSSILE_CATCHER:
                // Allows user to suck the missile incoming but consumes energy
                break;
            default:
                // Doesn't have an installation property
                break;
        }
    }

    public void onUse(AddOnData addOn){
        switch(addOn){
            case GUN:
                // Fire Gun
                Bullet firedBullet = new Bullet(this.align, Direction.UP, (float) this.hitbox.x, (float) this.hitbox.y);
                this.bulletsFired.add(firedBullet);
                break;
            case LASER_GUN:
                // Fire Laser Gun if required energy is met
                break;
            case MISSILE_LAUNCHER:
                // Fires Missile
                break;
            case HEALING_STATION:
                // Uses Healing Kit if they have a healing kit
                break;
            case ENERGY_STATION:
                // Regenerates energy if they have an energy kit
                break;
            case SHIELD:
                // This is only required if shield uses energy on press/hold
                break;
            case MiSSILE_CATCHER:
                // Catches missile in a short time period and puts it on cooldown
                break;
            default:
                // Has no application on use
                break;
        }
    }

    public void onDestroy(int id){
        AddOnData addOn = AddOnData.getById(id);
        if(this.addOns.contains(addOn, true) ) {
            this.addOns.removeIndex(this.addOns.indexOf(addOn, true));
        } else {
            return;
        }
        switch(addOn) {
            case HEALTH_BAR_GUI:
                // Removes Health Bar GUI
                healthBarVisible = false;
                break;
            case ENERGY_BAR_GUI:
                // Removes Energy Bar GUI
                energyBarVisible = false;
                break;
            case HEALING_STATION:
                // Decrease max health kit capacity
                break;
            case ENERGY_STATION:
                // Decrease max energy kit capacity
                break;
            case SHIELD:
                // remove use of shield
                break;
            case BATTERY:
                // Removes their max energy
                this.maxEnergy -= 100;

                break;
            case WEAPON_BOOST:
                // Removes damage upgrade
                break;
            case WEAPON_UPGRADE:
                // removes hitbox upgrade
                break;
            case MISSILE_HOLDER:
                // decrease their max missile capacity
                break;
            case MiSSILE_CATCHER:
                // Removes Missile Catcher Use
                break;
            default:
                // Doesn't have an uninstall property
                break;
        }
    }

    public void updateBullets(float delta){
        int i = 0;
        while(i < this.bulletsFired.size){
            Bullet fired = this.bulletsFired.get(i);
            fired.update(delta);
            System.out.println("Bullet "+i+": "+fired.hitbox.x+", "+fired.hitbox.y);
            if(fired.hitbox.y > 300 || fired.hitbox.y < 0){
                fired.dispose();
                this.bulletsFired.removeIndex(i);
                i--;
            }
            i++;
        }
    }

    public void stop() {
        this.move(Direction.NONE);
    }
}
