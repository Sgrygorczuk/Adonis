package com.mygdx.adonis;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static com.mygdx.adonis.Alignment.ENEMY;
import static com.mygdx.adonis.Consts.ADD_ON_GROWTH;
import static com.mygdx.adonis.Consts.BATTERY_SIZE;
import static com.mygdx.adonis.Consts.BULLET_DAMAGE;
import static com.mygdx.adonis.Consts.ENERGY_RECHARGE;
import static com.mygdx.adonis.Consts.PLAYER_SPEED;
import static com.mygdx.adonis.Consts.TILE_HEIGHT;
import static com.mygdx.adonis.Consts.TILE_WIDTH;

public abstract class Ship {
    public Rectangle hitbox;
    public Vector2 velocity;
    public Alignment align;

    public int maxHealth;
    public int health;
    public boolean healthBarVisible;

    public int maxEnergy;
    public int energy;
    public int energyRecharge;
    public boolean energyBarVisible;

    public Direction dir;

    protected Array<Bullet> bulletsFired;
    protected Array<AddOnData> addOns;

    //Sprite sheet used
    protected TextureRegion[][] flySpriteSheet;
    protected TextureRegion[][] dieSpriteSheet;

    protected Animation<TextureRegion> flyAnimation;
    protected Animation<TextureRegion> dieAnimation;

    //Current animation frame time
    public int damage;
    protected float animationTime = 0;
    public boolean dieFlag = false;

    public float shootTimer = 0.0f;
    public float shootLag = 0.15f;

    public float shipSpeed;

    public Ship(TextureRegion[][] flySpriteSheet, TextureRegion[][] dieSpriteSheet, float initX, float initY, Alignment align) {
        // can multiply e.g. by 1.5, 1.2 to get more or less health
        this.align = align;
        this.maxHealth = 100;
        this.health = 100;

        this.maxEnergy = 0;
        this.energy = 0;
        this.energyRecharge = 1;
        this.damage = BULLET_DAMAGE;

        this.flySpriteSheet = flySpriteSheet;
        this.dieSpriteSheet = dieSpriteSheet;
        setUpAnimation();

        this.hitbox = new Rectangle(initX, initY, TILE_WIDTH, TILE_HEIGHT);
        this.velocity = new Vector2(0, 0);
        this.addOns = new Array<>();
        this.bulletsFired = new Array<>();
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Sets up the animation loops in all of the directions
     */
    protected void setUpAnimation() {
        flyAnimation = new Animation<>(0.25f, this.flySpriteSheet[0][0], this.flySpriteSheet[0][1],
                this.flySpriteSheet[0][2], this.flySpriteSheet[0][3]);
        flyAnimation.setPlayMode(Animation.PlayMode.LOOP);

        dieAnimation = new Animation<>(0.035f, this.dieSpriteSheet[0][0], this.dieSpriteSheet[0][1],
                this.dieSpriteSheet[0][2], this.dieSpriteSheet[0][3], this.dieSpriteSheet[0][4], this.dieSpriteSheet[0][5],
                this.dieSpriteSheet[0][6], this.dieSpriteSheet[0][7], this.dieSpriteSheet[0][8]);
        dieAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    // collision isn't as simple as checking a single hitbox since each ship has multiple addons
    public boolean isColliding(Rectangle other) {
        // todo addons
        return this.hitbox.overlaps(other);
    }

    public void takeDamage(int amt) {
        this.health -= amt;
        if (health <= 0 && !dieFlag) {
            shootTimer = 999f;
            damage = 0;
            animationTime = 0;
            dieFlag = true;
        }
    }

    // TODO change velocity depending on game stuff
    public void update(float delta) {
//        for (AddOnData addOn : this.addOns) {
//             addOn.update(delta);
//        }

        animationTime += delta;

        if (health > 0) {
            if (this.health > this.maxHealth) {
                this.health = this.maxHealth;
            }
            if (this.maxEnergy > 0 && this.energy < this.maxEnergy) {
                this.energy+=this.energyRecharge;
            }
            if (this.energy > this.maxEnergy) {
                this.energy = this.maxEnergy;
            }

            velocity.x = this.dir.getX();
            velocity.y = this.dir.getY();



            hitbox.x = hitbox.getX() + (velocity.x * delta * TILE_WIDTH * shipSpeed);
            hitbox.y = hitbox.getY() + (velocity.y * delta * TILE_HEIGHT * shipSpeed);
        }
    }

    public boolean getBlowUpFlag() {
        return dieAnimation.getKeyFrame(animationTime) == this.dieSpriteSheet[0][8];
    }

    public void move(Direction dir) {
        this.dir = dir;
    }

    public void fire() {
        for (AddOnData addOn : this.addOns) {
//            System.out.println("Firing: "+addOn.name());
            this.onUse(addOn);
        }
    }

    public void onInstall(AddOnData addOn) {
        if(this.addOns.size >= 9){
            return;
        }
        this.addOns.add(addOn);

        this.updateShipSpecs();

        System.out.println(addOn.name());
        // TODO: install the addons - Paul
        switch (addOn) {
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
                this.maxEnergy += BATTERY_SIZE;
                break;
            case CHARGER:
                this.energyRecharge *= ENERGY_RECHARGE;
                break;
            case WEAPON_BOOST:
                // Upgrades the damage of weapons
                damage *= 2;
                break;
            case WEAPON_UPGRADE:
                // Upgrades the hitboxes of weapons
                break;
            case MISSILE_HOLDER:
                // Allows user to hold missiles which they can pickup
                break;
            case MISSILE_CATCHER:
                // Allows user to suck the missile incoming but consumes energy
                break;
            default:
                // Doesn't have an installation property
                break;
        }
    }

    public void onUse(AddOnData addOn) {
        switch (addOn) {
            case GUN:
                System.out.println("Fire");
                // Fire Gun
                // TODO fix this
//                Bullet firedBullet = new Bullet(this.align, Direction.UP, (float) this.hitbox.x, (float) this.hitbox.y);
//                this.bulletsFired.add(firedBullet);
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
            case MISSILE_CATCHER:
                // Catches missile in a short time period and puts it on cooldown
                break;
            default:
                // Has no application on use
                break;
        }
    }


    public void onDestroy(AddOnData addOn) {

        if (this.addOns.contains(addOn, true)) {
            this.addOns.removeIndex(this.addOns.indexOf(addOn, true));
            this.updateShipSpecs();
        } else {
            return;
        }
        destroyedPart(addOn);
    }

    public void destroyedPart(AddOnData addOn){
        switch (addOn) {
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
                this.maxEnergy -= BATTERY_SIZE;
                break;
            case CHARGER:
                this.energyRecharge /= ENERGY_RECHARGE;
                break;
            case WEAPON_BOOST:
                // Removes damage upgrade
                damage /= 2;
                break;
            case WEAPON_UPGRADE:
                // removes hitbox upgrade
                break;
            case MISSILE_HOLDER:
                // decrease their max missile capacity
                break;
            case MISSILE_CATCHER:
                // Removes Missile Catcher Use
                break;
            default:
                // Doesn't have an uninstall property
                break;
        }
    }

    public void updateShipSpecs(){
        this.hitbox.width = TILE_WIDTH*(1+(ADD_ON_GROWTH*(1+this.addOns.size)));
        this.hitbox.height = TILE_HEIGHT*(1+(ADD_ON_GROWTH*(1+this.addOns.size)));

        this.shipSpeed = PLAYER_SPEED + ((-2)*(float)(Math.log(0.5+this.addOns.size)));

        System.out.println(this.hitbox.width+", "+this.hitbox.height+"\t"+this.shipSpeed);
    }

    public boolean hasAddOn(AddOnData addOn) {
        return this.addOns.contains(addOn, false);
    }

    public AddOnData getAddOnAt(int index){
        if(index < addOns.size) return this.addOns.get(index);
        return null;
    }

    public boolean hasWeapon() {
        for (AddOnData addOn : this.addOns) {
            if (addOn.isWeapon()) {
                return true;
            }
        }

        return false;
    }

    public void updateBullets(float delta) {
        int i = 0;
        while (i < this.bulletsFired.size) {
            Bullet fired = this.bulletsFired.get(i);
            fired.update(delta);
            System.out.println("Bullet " + i + ": " + fired.hitbox.x + ", " + fired.hitbox.y);
            if (fired.hitbox.y > 300 || fired.hitbox.y < 0) {
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

    /**
     * Input: Shaperenderer
     * Output: Void
     * Purpose: Draws the circle on the screen using render
     */
    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public void draw(SpriteBatch spriteBatch) {
//        for (AddOnData addOn : this.addOns) {
//             addOn.draw(spriteBatch);
//        }

        TextureRegion currentFrame;

        float width = hitbox.width; // The die sprite is wider
        float offset = 0;           //Need to offset the width change
        if (health > 0) {
            currentFrame = flyAnimation.getKeyFrame(animationTime);
        } else {
            currentFrame = dieAnimation.getKeyFrame(animationTime);
            width *= (float) dieSpriteSheet[0][0].getRegionWidth() / flySpriteSheet[0][0].getRegionWidth();
            offset = (width - hitbox.width) / 2f;
        }

        spriteBatch.draw(currentFrame, hitbox.x - offset, hitbox.y, width, hitbox.height);
    }
}
