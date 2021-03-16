package com.mygdx.adonis;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static com.mygdx.adonis.Alignment.PLAYER;
import static com.mygdx.adonis.Consts.ADD_ON_GROWTH;
import static com.mygdx.adonis.Consts.BATTERY_SIZE;
import static com.mygdx.adonis.Consts.BULLET_DAMAGE;
import static com.mygdx.adonis.Consts.ENERGY_BURN_TIME;
import static com.mygdx.adonis.Consts.ENERGY_RECHARGE;
import static com.mygdx.adonis.Consts.PLAYER_SPEED;
import static com.mygdx.adonis.Consts.SHIELD_MULTIPLIER;
import static com.mygdx.adonis.Consts.TILE_HEIGHT;
import static com.mygdx.adonis.Consts.TILE_WIDTH;
import static com.mygdx.adonis.Consts.WORLD_HEIGHT;
import static com.mygdx.adonis.Direction.DOWN;

public abstract class Ship {

    public Rectangle hitbox;       //Object hit box
    public Vector2 velocity;       //X and Y speeds
    public Alignment align;        //Enemy or Player

    public int maxHealth;           //Max health
    public int health;              //Current Health
    public boolean healthBarVisible = false;
    public int healthBarHeld = 0;

    public int maxEnergy;           //Max energy
    public int energy;              //Current energy
    public int energyRecharge;      //Energy recharge time
    public boolean energyBarVisible = false;
    public int points;              //

    public int energyBarHeld = 0; // TODO: Remove once we make it unique
    public float energyBurn = 0f;   //How fast it comes back after being used up
    public boolean hasShield = false;   //Does player have shield

    public Direction dir;           //Tells direction

    protected Array<Bullet> bulletsFired; //All of the bullets from the ship
    public Array<AddOnData> addOns;      //Any add on the ship posses

    //Sprite sheet used
    protected TextureRegion[][] spriteSheet;

    /**
     * 0 - Fly forward netural
     * 1 - Turn right,
     * 2 - Coming back from right
     * 3 - Turn left
     * 4 - Coming back from left
     * 5 - Roll right
     * 6 - Roll left
     * */
    private boolean animationPause = false;
    protected int animationState = 0;
    protected TextureRegion forwardTexture;
    protected Animation<TextureRegion> turnRightAnimation;
    protected Animation<TextureRegion> turnRightBackAnimation;
    protected Animation<TextureRegion> turnLeftAnimation;
    protected Animation<TextureRegion> turnLeftBackAnimation;
    protected Animation<TextureRegion> rollRightAnimation;
    protected Animation<TextureRegion> rollLeftAnimation;
    protected Animation<TextureRegion> dieAnimation;

    //Current animation frame time
    public int damage;
    private boolean invincibilityFlag;
    protected boolean flashing = false;
    protected float animationRightTime = 0;
    protected float animationRightBackTime = 0;
    protected float animationLeftTime = 0;
    protected float animationLeftBackTime = 0;
    protected float animationRollTime = 0;
    protected float animationDieTime = 0;

    public boolean dieFlag = false;  //Tells us the enemy is flying

    private boolean enemyImageFlag; //Tells us it's an enemy and we should inverse the texture

    public float shootTimer = 0.0f; //Tells how often the enemy should shot
    public float shootLag = 0.15f; //Player Lag

    public float shipSpeed;         //Speed multiplier

    //Timer counting down until player can be hit again
    private static final float INVINCIBILITY_TIME = 0.5F;
    private float invincibilityTimer = INVINCIBILITY_TIME;

    //Timer counting down until we turn the draw function on/Off
    private static final float FLASHING_TIME = 0.1F;
    private float flashingTimer = FLASHING_TIME;

    /**
     *
     * @param spriteSheet texture
     * @param initX position
     * @param initY position
     * @param align enemy or Player
     * @param enemyImageFlag if enemy flip image
     * @param TileMultiplier how big should the image be
     * @param points the point multiplier
     */
    public Ship(TextureRegion[][] spriteSheet, float initX, float initY, Alignment align, boolean enemyImageFlag, float TileMultiplier,
                int points) {
        // can multiply e.g. by 1.5, 1.2 to get more or less health
        this.align = align;
        this.maxHealth = 100;
        this.health = 100;
        this.points = points;

        this.enemyImageFlag = enemyImageFlag;

        this.maxEnergy = 0;
        this.energy = 0;
        this.energyRecharge = 1;
        this.damage = BULLET_DAMAGE;

        this.spriteSheet = spriteSheet;
        setUpAnimations();

        this.hitbox = new Rectangle(initX, initY, TILE_WIDTH * TileMultiplier, TILE_HEIGHT * TileMultiplier);
        this.velocity = new Vector2(0, 0);
        this.addOns = new Array<>();
        this.bulletsFired = new Array<>();
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Sets up the animation loops in all of the directions
     */
    protected void setUpAnimations() {
        //Sets up the generic going forward
        forwardTexture = spriteSheet[0][0];

        float testVar = 55;
        dieAnimation = setUpAnimation(1/120f, 0, Animation.PlayMode.NORMAL);
        turnRightAnimation = setUpAnimation(1/testVar, 4, Animation.PlayMode.LOOP);
        turnRightBackAnimation = setUpAnimation(1/testVar, 4, Animation.PlayMode.LOOP_REVERSED);
        turnLeftAnimation = setUpAnimation(1/testVar, 3, Animation.PlayMode.LOOP);
        turnLeftBackAnimation = setUpAnimation(1/testVar, 3, Animation.PlayMode.LOOP_REVERSED);
        rollLeftAnimation = setUpAnimation(1/testVar, 1, Animation.PlayMode.NORMAL);
        rollRightAnimation = setUpAnimation(1/testVar, 2, Animation.PlayMode.NORMAL);
    }

    /**
     * Sets up animation of a row from sprite sheet
     * @param duration how long should each frame last
     * @param row which row we animating
     * @param playMode how should it be played
     * @return a animation
     */
    private Animation<TextureRegion> setUpAnimation(float duration, int row, Animation.PlayMode playMode){
        Animation<TextureRegion> animation = new Animation<>(duration, this.spriteSheet[row]);
        animation.setPlayMode(playMode);
        return animation;
    }

    // collision isn't as simple as checking a single hitbox since each ship has multiple addons
    public boolean isColliding(Rectangle other) {
        return this.hitbox.overlaps(other);
    }

    /**
     * Takes damage and updates status
     * @param amt given damage
     */
    public void takeDamage(int amt) {
        if(!invincibilityFlag) {
            if(hasShield && energy > 0){
                this.energy -= amt*SHIELD_MULTIPLIER;
                if(energy <= 0){
                    energy = 0;
                    energyBurn = ENERGY_BURN_TIME;
                }
            } else {
                this.health -= amt;
                if(this.align == PLAYER) setInvincibilityFlag();
            }
        }
    }

    /**
    Input: Float delta
    Output: Void
    Purpose: Ticks down to turn off invincibility
    */
    public void invincibilityTimer(float delta){
        invincibilityTimer -= delta;
        flashingTimer -= delta;

        if (flashingTimer <= 0) {
            flashingTimer = FLASHING_TIME;
            flashing = !flashing;
        }

        if (invincibilityTimer <= 0) {
            invincibilityTimer = INVINCIBILITY_TIME;
            invincibilityFlag = false;
            flashing = false;
        }
    }


    /**
     * Central update the ship method
     * @param delta timing var
     */
    public void update(float delta) {

        if (health > 0) {
            updateAnimationState(delta);
            if(invincibilityFlag){invincibilityTimer(delta);}

            if (this.health > this.maxHealth) {
                this.health = this.maxHealth;
            }
            if(this.energyBurn > 0f){
                energyBurn -= delta;
            } else {
                if (this.maxEnergy > 0 && this.energy < this.maxEnergy) {
                    this.energy += this.energyRecharge;
                }
                if (this.energy > this.maxEnergy) {
                    this.energy = this.maxEnergy;
                }
            }

            if(hitbox.getY() + hitbox.getHeight() < WORLD_HEIGHT) {
                velocity.x = this.dir.getX();
                velocity.y = this.dir.getY();
            }
            else{
                velocity.x = DOWN.getX();
                velocity.y = DOWN.getY();
            }

            hitbox.x = hitbox.getX() + (velocity.x * delta * TILE_WIDTH * shipSpeed);
            hitbox.y = hitbox.getY() + (velocity.y * delta * TILE_HEIGHT * shipSpeed);
        }
        else{
            animationState = 10;
            animationDieTime += delta;
        }

    }

    /**
     * Tells us how the ship should be drawn
     * @param animationState which state the ship animation is in
     */
    public void setAnimationState(int animationState){this.animationState = animationState;}

    /**
     * Updates what the ship is shown to be acting like
     * @param delta timing var
     */
    private void updateAnimationState(float delta){
        switch (animationState){
            case 0:{
                if(velocity.x > 0){
                    animationState = 1;
                    animationPause = true;
                }
                else if(velocity.x < 0){
                    animationState = 3;
                    animationPause = true;
                }
            break;
            }
            case 1:{
                if (!(velocity.x > 0) || turnRightAnimation.getKeyFrameIndex(animationRightTime) != 29) {
                    if(velocity.x <= 0){
                        animationState = 2;
                        animationRightTime = 0;
                    }
                    else {
                        animationRightTime += delta;
                    }
                }
                break;
            }
            case 2:{
                if(velocity.x <= 0 && turnRightBackAnimation.getKeyFrameIndex(animationRightBackTime) == 0){
                    animationState = 0;
                    animationRightBackTime = 0;
                }
                else {
                    animationRightBackTime += delta;
                }
                break;
            }
            case 3:{
                if (!(velocity.x < 0) || turnLeftAnimation.getKeyFrameIndex(animationLeftTime) != 29) {
                    if(velocity.x >= 0){
                        animationState = 4;
                        animationLeftTime = 0;
                    }
                    else {
                        animationLeftTime += delta;
                    }
                }
                break;
            }
            case 4:{
                if(velocity.x >= 0 && turnLeftBackAnimation.getKeyFrameIndex(animationLeftBackTime) == 0){
                    animationState = 0;
                    animationLeftBackTime= 0;
                }
                else {
                    animationLeftBackTime += delta;
                }
                break;
            }
            case 5:{
                if(rollLeftAnimation.isAnimationFinished(animationRollTime)){
                    animationRollTime = 0;
                    animationState = 0;
                }
                else{
                    animationRollTime += delta;
                }
            }
            case 6:{
                if(rollRightAnimation.isAnimationFinished(animationRollTime)){
                    animationRollTime = 0;
                    animationState = 0;
                }
                else{
                    animationRollTime += delta;
                }
            }

            default:{

            }
        }

    }

    /**
     * Checks if we reached the final frame of the death animation
     * @return is the ship done dying
     */
    public boolean getBlowUpFlag() {
        return dieAnimation.isAnimationFinished(animationDieTime);
    }

    /**
     * changes the direction the ship is going
     * @param dir new direction
     */
    public void move(Direction dir) {
        this.dir = dir;
    }

    /**
     * Turn the ship to be invincible
     */
    public void setInvincibilityFlag(){invincibilityFlag = true;}

    /**
     * Installing new addons
     * @param addOn a given addOn
     */
    public void onInstall(AddOnData addOn) {
        if(this.addOns.size >= 9){
            return;
        }
        this.addOns.add(addOn);

        this.updateShipSpecs();

        System.out.println(addOn.name());
        // TODO: install the addons - Paul
        switch (addOn) {
            //case HEALTH_BAR_GUI:
                // Allows ship to see Health Bar
               // healthBarVisible = true;
                //healthBarHeld += 1;
                //break;
            //case ENERGY_BAR_GUI:
                // Allows ship to see Energy Bar
                //energyBarVisible = true;
                //energyBarHeld += 1;
                //break;
            //case HEALING_STATION:
                // Allows ship to hold and use health packs they pick up
                //break;
            //case ENERGY_STATION:
                // Allows ship to hold and use energy packs they pick up
                //break;
            case SHIELD:
                // Has a shield in a certain direction of installation
                // Shield either consumes energy while on or when it gets hit
                hasShield = true;
                this.maxEnergy += BATTERY_SIZE;
                this.energyRecharge *= ENERGY_RECHARGE;
                break;
            case SPEED:
                velocity.y += 5;
                velocity.x += 5;
                break;
            case DAMAGE:
                damage *= 2;
                break;
            //case BATTERY:
                // Holds energy and lets you use weapons and upgrades that require it
                //this.maxEnergy += BATTERY_SIZE;
                //break;
            //case CHARGER:
                //this.energyRecharge *= ENERGY_RECHARGE;
                //break;
            //case WEAPON_BOOST:
                // Upgrades the damage of weapons
                //damage *= 2;
                //break;
            //case WEAPON_UPGRADE:
                // Upgrades the hitboxes of weapons
                //break;
            //case MISSILE_HOLDER:
                // Allows user to hold missiles which they can pickup
                //break;
            //case MISSILE_CATCHER:
                // Allows user to suck the missile incoming but consumes energy
                //break;
            default:
                // Doesn't have an installation property
                break;
        }
    }

    /**
     * ???
     * @param addOn given addon
     */
    public void onUse(AddOnData addOn) {
        switch (addOn) {
            //case GUN:
                //System.out.println("Fire");
                // Fire Gun
//                Bullet firedBullet = new Bullet(this.align, Direction.UP, (float) this.hitbox.x, (float) this.hitbox.y);
//                this.bulletsFired.add(firedBullet);
                //break;
            //case LASER_GUN:
                // Fire Laser Gun if required energy is met
               // break;
            //case MISSILE_LAUNCHER:
                // Fires Missile
                //break;
            //case HEALING_STATION:
                // Uses Healing Kit if they have a healing kit
                //break;
            //case ENERGY_STATION:
                // Regenerates energy if they have an energy kit
               // break;
            case SHIELD:
                // This is only required if shield uses energy on press/hold
                break;
            //case MISSILE_CATCHER:
                // Catches missile in a short time period and puts it on cooldown
                //break;
            default:
                // Has no application on use
                break;
        }
    }


    /**
     * ???
     * @param addOn given addOn
     */
    public void onDestroy(AddOnData addOn) {

        if (this.addOns.contains(addOn, true)) {
            this.addOns.removeIndex(this.addOns.indexOf(addOn, true));
            this.updateShipSpecs();
        } else {
            return;
        }
        destroyedPart(addOn);
    }

    /**
     * Removes the addon from the ships inventory
     * @param addOn given addOn
     */
    public void destroyedPart(AddOnData addOn){
        switch (addOn) {
            //case HEALTH_BAR_GUI:
                // Removes Health Bar GUI
                //healthBarHeld -= 1;
                //if (healthBarHeld < 1) healthBarVisible = false;
               // break;
            //case ENERGY_BAR_GUI:
                // Removes Energy Bar GUI
                //energyBarHeld -= 1;
                //if (energyBarHeld < 1) energyBarVisible = false;
                //break;
            //case HEALING_STATION:
                // Decrease max health kit capacity
                //break;
            //case ENERGY_STATION:
                // Decrease max energy kit capacity
               // break;
            case SHIELD:
                boolean checkAddOn = false;
                for(AddOnData addOn1: addOns){
                    if (addOn1.getId() == 0 ){ checkAddOn = true; }
                 }
                hasShield = checkAddOn;
                this.energyRecharge /= ENERGY_RECHARGE;
                this.maxEnergy -= BATTERY_SIZE;
                break;
            case SPEED:
                velocity.y -= 5;
                velocity.x -= 5;
                break;
            case DAMAGE:
                damage /= 2;
                break;
            //case BATTERY:
                // Removes their max energy
                //this.maxEnergy -= BATTERY_SIZE;
                //break;
            //case CHARGER:
                //this.energyRecharge /= ENERGY_RECHARGE;
                //break;
            //case WEAPON_BOOST:
                // Removes damage upgrade
                //damage /= 2;
                //break;
            //case WEAPON_UPGRADE:
                // removes hitbox upgrade
                //break;
            //case MISSILE_HOLDER:
                // decrease their max missile capacity
                //break;
            //case MISSILE_CATCHER:
                // Removes Missile Catcher Use
                //break;
            default:
                // Doesn't have an uninstall property
                break;
        }
    }

    /**
     * Changes the ships data based on how many addon it has
     */
    public void updateShipSpecs(){
        this.hitbox.width = TILE_WIDTH*(1+(ADD_ON_GROWTH*(1+this.addOns.size)));
        this.hitbox.height = TILE_HEIGHT*(1+(ADD_ON_GROWTH*(1+this.addOns.size)));

        this.shipSpeed = PLAYER_SPEED + ((-2.3f)*(float)(Math.log(0.5+this.addOns.size)));
    }

    /**
     * Checks if the given add on is in the array
     * @param addOn given addOn
     * @return if it exits
     */
    public boolean hasAddOn(AddOnData addOn) {
        return this.addOns.contains(addOn, false);
    }

    /**
     * Returns add on at given index
     * @param index given index
     * @return addOn
     */
    public AddOnData getAddOnAt(int index){
        if(index < addOns.size) return this.addOns.get(index);
        return null;
    }

    /**
     * Checks if it has a specific weapon
     * @return if we do have that weapons
     */
    public boolean hasWeapon() {
        for (AddOnData addOn : this.addOns) {
            if (addOn.isWeapon()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Update the portion and existence of the fired bullets
     * @param delta timing var
     */
    public void updateBullets(float delta) {
        int i = 0;
        while (i < this.bulletsFired.size) {
            Bullet fired = this.bulletsFired.get(i);
            fired.update(delta);
            if (fired.hitbox.y > 300 || fired.hitbox.y < 0) {
                fired.dispose();
                this.bulletsFired.removeIndex(i);
                i--;
            }
            i++;
        }
    }

    /**
     * Make the ship not move in any direction
     */
    public void stop() { this.move(Direction.NONE); }

    /**
     * Input: Shaperenderer
     * Output: Void
     * Purpose: Draws the circle on the screen using render
     */
    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    /**
     * Central drawing function for texutre
     * @param spriteBatch where the function will be drawn
     */
    public void draw(SpriteBatch spriteBatch) {
        if (!flashing) {
            TextureRegion currentFrame;

            float width = hitbox.width; // The die sprite is wider
            float offset = 0;           //Need to offset the width change
            if (animationState == 0) {
                currentFrame = forwardTexture;
            }
            else if(animationState == 1){
                currentFrame = turnRightAnimation.getKeyFrame(animationRightTime);
            }
            else if(animationState == 2){
                currentFrame = turnRightBackAnimation.getKeyFrame(animationRightBackTime);
            }
            else if(animationState == 3){
                currentFrame = turnLeftAnimation.getKeyFrame(animationLeftTime);
            }
            else if(animationState == 4){
                currentFrame = turnLeftBackAnimation.getKeyFrame(animationLeftBackTime);
            }
            else if(animationState == 5){
                currentFrame = rollLeftAnimation.getKeyFrame(animationRollTime);
            }
            else if(animationState == 6){
                currentFrame = rollRightAnimation.getKeyFrame(animationRollTime);
            }
            else {
                currentFrame = dieAnimation.getKeyFrame(animationDieTime);
            }

            //The enemyImageFlag ? flips the enemy image and accounts for move 
            spriteBatch.draw(currentFrame, hitbox.x, enemyImageFlag ? hitbox.y + hitbox.height: hitbox.y , width, enemyImageFlag ? -hitbox.height : hitbox.height);
        }
    }
}
