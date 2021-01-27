package com.mygdx.adonis.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.adonis.Adonis;
import com.mygdx.adonis.Alignment;
import com.mygdx.adonis.Bullet;
import com.mygdx.adonis.Direction;
import com.mygdx.adonis.DummyEnemy;
import com.mygdx.adonis.EnemyType;
import com.mygdx.adonis.Player;
import com.mygdx.adonis.Ship;

import static com.mygdx.adonis.Consts.ENEMY_SPAWN_TIME;
import static com.mygdx.adonis.Consts.TILE_HEIGHT;
import static com.mygdx.adonis.Consts.TILE_WIDTH;
import static com.mygdx.adonis.Consts.WORLD_HEIGHT;
import static com.mygdx.adonis.Consts.WORLD_WIDTH;

class MainScreen extends ScreenAdapter {
    /*
    Image processing -- Objects that modify the view and textures
    */
    private Viewport viewport;             //The screen where we display things
    private Camera camera;                 //The camera viewing the viewport
    private SpriteBatch batch = new SpriteBatch();             //Batch that holds all of the textures

    private ShapeRenderer shapeRendererEnemy;       //Creates the wire frames for enemies
    private ShapeRenderer shapeRendererUser;        //Creates the wire frame for user
    private ShapeRenderer shapeRendererBackground;  //Creates the wire frame for background objects
    private ShapeRenderer shapeRendererCollectible; //Creates wireframe for collectibles

    //The buttons that will be used in the menu
    private Stage menuStage;
    private ImageButton[] menuButtons;

    //Game object that holds the settings
    private final Adonis adonis;

    private Player player;
    private Array<Ship> enemies = new Array<>();
    private Array<Bullet> projectiles = new Array<>();
    // seconds in between each enemy spawn
    private float spawnTimer = ENEMY_SPAWN_TIME * .5f;

    // TODO change to empty string when we actually have assets
    private final String tmpPrefix = "/tmp/";

    //Music that will start
    private Music music;

    //Font used for the user interaction
    private BitmapFont bitmapFont = new BitmapFont();
    //Font for viewing phone stats in developer mode
    private BitmapFont bitmapFontDeveloper = new BitmapFont();

    //Textures
    private Texture popUpTexture;                       //Pop up menu to show menu buttons and Help screen
    private Texture backgroundUITexture;
    private Texture backgroundGameTexture;
    private Texture skillBarTexture;
    private Texture highlightTexture;
    private Texture infoBoardTexture;
    private Texture healthTexture;
    private Texture energyTexture;

    private TextureRegion[][] playerFlyTexture;
    private TextureRegion[][] playerDieTexture;
    private TextureRegion[][] enemyOneFlyTexture;
    private TextureRegion[][] enemyOneDieTexture;
    private TextureRegion[][] enemyTwoFlyTexture;
    private TextureRegion[][] enemyTwoDieTexture;
    private TextureRegion[][] playerLaserTexture;
    private TextureRegion[][] enemyLaserTexture;


    private final short NUM_BUTTONS = 5;

    //Names of buttons
    private String[] menuButtonText = new String[]{"Restart", "Help", "Sound Off", "Main Menu", "Sound On"};
    private int lives = 3;              //How many lives player has left

    //Flags
    private int itemSelected = 8;
    private boolean developerMode = false;      //Developer mode shows hit boxes and phone data
    private boolean isPaused = false;         //Stops the game from updating
    private boolean isGameEnded = false;            //Tells us game has been lost
    private float sfxVolume = 1f;               //Current sfx volume
    private boolean helpFlag = false;           //Tells us if help flag is on or off

    /*
    Input: SpaceHops
    Output: Void
    Purpose: Grabs the info from main screen that holds asset manager
    */
    MainScreen(Adonis adonis) {
        this.adonis = adonis;
    }

    /*
    Input: The width and height of the screen
    Output: Void;
    Purpose: Updates the dimensions of the screen
    */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Initializes all the variables that are going to be displayed
    */
    @Override
    public void show() {
        showCamera();       //Set up the camera
        showTextures();     //Sets up textures
        showObjects();      //Sets up player and font
        showButtons();      //Sets up the buttons
        setUpPlayer();
        showMusic();        //Sets up music
        if (developerMode) {
            showRender();
        }    //If in developer mode sets up the redners
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the camera through which all the objects are view through
    */
    private void showCamera() {
        camera = new OrthographicCamera();                                    //Sets a 2D view
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);    //Places the camera in the center of the view port
        camera.update();                                                    //Updates the camera
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);  //Stretches the image to fit the screen
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up all of the textures
    */
    private void showTextures() {
        popUpTexture = new Texture(Gdx.files.internal("UI/MenuPanel.png"));
        backgroundUITexture = new Texture(Gdx.files.internal("UI/GameBackground.png"));
        backgroundGameTexture = new Texture(Gdx.files.internal("UI/MainMenuBackground.png"));
        skillBarTexture = new Texture(Gdx.files.internal("UI/SkillBar.png"));
        highlightTexture = new Texture(Gdx.files.internal("UI/Highlight.png"));
        infoBoardTexture = new Texture(Gdx.files.internal("UI/InformationPanel.png"));
        healthTexture = new Texture(Gdx.files.internal("UI/PilotHealth.png"));
        energyTexture = new Texture(Gdx.files.internal("UI/PilotEnergy.png"));

        Texture playerFlyTexturePath = new Texture(Gdx.files.internal("Sprites/PlayerSpriteSheetFly.png"));
        playerFlyTexture = new TextureRegion(playerFlyTexturePath).split(playerFlyTexturePath.getWidth()/4, playerFlyTexturePath.getHeight());
        Texture playerDieTexturePath = new Texture(Gdx.files.internal("Sprites/PlayerSpriteSheetDie.png"));
        playerDieTexture =  new TextureRegion(playerDieTexturePath).split(playerDieTexturePath.getWidth()/9, playerDieTexturePath.getHeight());

        Texture enemyOneFlyPath = new Texture(Gdx.files.internal("Sprites/EnemyOneSpriteSheetFly.png"));
        enemyOneFlyTexture = new TextureRegion(enemyOneFlyPath).split(enemyOneFlyPath.getWidth()/4, enemyOneFlyPath.getHeight());
        Texture enemyOneDiePath = new Texture(Gdx.files.internal("Sprites/EnemyOneSpriteSheetDie.png"));
        enemyOneDieTexture =  new TextureRegion(enemyOneDiePath).split(enemyOneDiePath.getWidth()/9, enemyOneDiePath.getHeight());

        Texture enemyTwoFlyPath = new Texture(Gdx.files.internal("Sprites/EnemyOneSpriteSheetFly.png"));
        enemyTwoFlyTexture = new TextureRegion(enemyTwoFlyPath).split(enemyTwoFlyPath.getWidth()/4, enemyTwoFlyPath.getHeight());
        Texture enemyTwoDiePath = new Texture(Gdx.files.internal("Sprites/EnemyOneSpriteSheetDie.png"));
        enemyTwoDieTexture =  new TextureRegion(enemyTwoDiePath).split(enemyTwoDiePath.getWidth()/9, enemyTwoDiePath.getHeight());

        Texture playerLaserTexturePath = new Texture(Gdx.files.internal("Sprites/PlayerShot.png"));
        playerLaserTexture = new TextureRegion(playerLaserTexturePath).split(playerLaserTexturePath.getWidth()/2, playerLaserTexturePath.getHeight());
        Texture enemyLaserTexturePath = new Texture(Gdx.files.internal("Sprites/EnemyShot.png"));
        enemyLaserTexture =  new TextureRegion(enemyLaserTexturePath).split(enemyLaserTexturePath.getWidth()/2, enemyLaserTexturePath.getHeight());
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the button
    */
    private void showButtons() {
        menuStage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(menuStage); //Gives control to the stage for clicking on buttons
        //Sets up 6 Buttons
        menuButtons = new ImageButton[6];

        setUpOpenMenuButton();  //Sets up button used to open the menu
        setUpMenuButtons();     //Sets up the button in the menu
        setUpExitButton();      //Sets up the button used to exit Help
    }

    private void setUpPlayer() {
        // TODO sprite sheet for player; right now we just have a static image
        this.player = new Player(playerFlyTexture, playerDieTexture);
    }

    private void spawnEnemy(EnemyType enemyType) {
        // spawn a little above the screen at random x
        Vector2 spawnPos = new Vector2(MathUtils.random(100 + TILE_WIDTH, 380 - TILE_WIDTH), WORLD_HEIGHT + TILE_HEIGHT);
        Ship enemy;

        switch (enemyType) {
            case DUMMY:
            default:
                enemy = new DummyEnemy(enemyOneFlyTexture, enemyOneDieTexture, spawnPos.x, spawnPos.y);
        }

        this.enemies.add(enemy);
    }
    /*
    Input: Void
    Output: Void
    Purpose: Sets up button used to open the menu
    */
    private void setUpOpenMenuButton() {
        //Set up the texture
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(620, 141); //Breaks down the texture into tiles

        //Place the button
        menuButtons[0] = new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
        menuButtons[0].setPosition(430 - 93/2f, WORLD_HEIGHT - 20 - 21.15f);
        menuButtons[0].setWidth(93);
        menuButtons[0].setHeight(21.15f);
        menuStage.addActor(menuButtons[0]);

        //If button has not been clicked turn on menu and pause game,
        //If the menu is up turn it off and un-pause the game
        menuButtons[0].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                if (!isGameEnded) {
                    playButtonSFX();
                    isPaused = !isPaused;
                    for (int i = 1; i < NUM_BUTTONS; i++) {
                        menuButtons[i].setVisible(isPaused);
                    }
                }
            }
        });
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the button in the menu
    */
    private void setUpMenuButtons() {
        //Sets up the texture
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(620, 141); //Breaks down the texture into tiles

        //Sets up the position of the buttons in a square 2x2
        for (int i = 1; i < NUM_BUTTONS; i++) {
            menuButtons[i] = new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));

            menuButtons[i].setPosition(WORLD_WIDTH/2f - 150/2f, 215 - (10 + 40f) * (i-1));
            menuStage.addActor(menuButtons[i]);
            menuButtons[i].setVisible(false);       //Initially all the buttons are off

            menuButtons[i].setWidth(150);
            menuButtons[i].setHeight(40f);

            //Sets up each buttons function
            final int finalI = i;
            menuButtons[i].addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    playButtonSFX();
                    //Restarts the game
                    if (finalI == 1) {
                        restart();
                    }
                    //Turns on the help menu
                    else if (finalI == 2) {
                        helpFlag = true;
                        //Turns off all the buttons
                        for (ImageButton imageButton : menuButtons) {
                            imageButton.setVisible(false);
                        }
                        //Turns exit button on
                        menuButtons[NUM_BUTTONS].setVisible(true);
                    }
                    //Turns sound on and off
                    else if (finalI == 3) {
                        soundButtonAction();
                    }
                    //Back to Main Menu Screen
                    else {
                        music.stop();
                        adonis.setScreen(new MenuScreen(adonis));
                    }
                }
            });
        }
    }

    /**
     * Changes the button image and turns the sound on and off
     *
     */
    private void soundButtonAction() {
        //Turns the volume down
        if (sfxVolume == 1f) {
            music.stop();
            sfxVolume = 0;
        }
        //Turns the sound on
        else {
            music.play();
            sfxVolume = 1;
        }
    }

    /**
     * Set up the button that will be used to exit the help menu
     */
    private void setUpExitButton() {
        //Sets up the texture
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(620, 141); //Breaks down the texture into tiles

        //Sets up the position
        menuButtons[NUM_BUTTONS] = new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
        menuButtons[NUM_BUTTONS].setPosition(WORLD_WIDTH/2f - 150/2f, 80);
        menuButtons[NUM_BUTTONS].setWidth(150);
        menuButtons[NUM_BUTTONS].setHeight(40f);
        menuStage.addActor(menuButtons[NUM_BUTTONS]);
        menuButtons[NUM_BUTTONS].setVisible(false);
        //Sets up to turn of the help menu if clicked
        menuButtons[NUM_BUTTONS].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                playButtonSFX();
                helpFlag = false;
                //Turn on all buttons but turn off this one
                for (ImageButton imageButton : menuButtons) {
                    imageButton.setVisible(true);
                }
                menuButtons[NUM_BUTTONS].setVisible(false);
            }
        });
    }

    /**
     * Set up player and the font
     */
    private void showObjects() {
        if (adonis.getAssetManager().isLoaded("Fonts/Font.fnt")) {
            bitmapFont = adonis.getAssetManager().get("Fonts/Font.fnt");
        }
        bitmapFont.getData().setScale(0.6f);
    }

    /**
     * Set up the music for the level
     */
    private void showMusic() {
        music = adonis.getAssetManager().get("Music/GameMusic.mp3", Music.class);
        music.setLooping(true);
        music.setVolume(.1f);
        music.play();
    }

    /**
     * Play sound effect for when button is pressed
     */
    private void playButtonSFX() {
        adonis.getAssetManager().get("SFX/Pop.wav", Sound.class).play(0.1f * sfxVolume);
    }

    /**
     * Play sound effect for when button is pressed
     */
    private void playMMBUp() {
        adonis.getAssetManager().get("SFX/MMB_Up.wav", Sound.class).play(0.1f * sfxVolume);
    }

    /**
     * Play sound effect for when button is pressed
     */
    private void playMMBDown() {
        adonis.getAssetManager().get("SFX/MMB_Down.wav", Sound.class).play(0.1f * sfxVolume);
    }

    /**
     * Purpose: Sets up the different renders to draw objects in wireframe
     */
    private void showRender() {
        //Enemy
        shapeRendererEnemy = new ShapeRenderer();
        shapeRendererEnemy.setColor(Color.RED);

        //User
        shapeRendererUser = new ShapeRenderer();
        shapeRendererUser.setColor(Color.GREEN);

        //Background
        shapeRendererBackground = new ShapeRenderer();
        shapeRendererBackground.setColor(Color.WHITE);

        //Intractable
        shapeRendererCollectible = new ShapeRenderer();
        shapeRendererCollectible.setColor(Color.BLUE);
    }

    /**
    Input: Void
    Output: Void
    Purpose: Draws all of the variables on the screen
    */
    @Override
    public void render(float delta) {
        if (!isPaused) {
            update(delta);
        }

        clearScreen();
        draw();
        if (developerMode) {
            renderEnemy();
            renderUser();
            renderCollectible();
            renderBackground();
        }
    }

    /**
    Input: Void
    Output: Void
    Purpose: Draws the enemy/obstacle wireframe
    */
    private void renderEnemy() {
        shapeRendererEnemy.setProjectionMatrix(camera.projection); //Screen set up camera
        shapeRendererEnemy.setTransformMatrix(camera.view);        //Screen set up camera
        shapeRendererEnemy.begin(ShapeRenderer.ShapeType.Line);    //Sets up to draw lines

        shapeRendererEnemy.end();
    }

    /**
    Input: Void
    Output: Void
    Purpose: Draws user wireframe
    */
    private void renderUser() {
        shapeRendererUser.setProjectionMatrix(camera.projection);    //Screen set up camera
        shapeRendererUser.setTransformMatrix(camera.view);           //Screen set up camera
        shapeRendererUser.begin(ShapeRenderer.ShapeType.Line);       //Sets up to draw lines
        shapeRendererUser.end();
    }

    /**
    Input: Void
    Output: Void
    Purpose: Draws the background object and UI wireframes
    */
    private void renderBackground() {
        shapeRendererBackground.setProjectionMatrix(camera.projection);  //Screen set up camera
        shapeRendererBackground.setTransformMatrix(camera.view);         //Screen set up camera
        shapeRendererBackground.begin(ShapeRenderer.ShapeType.Line);     //Starts to draw
        shapeRendererBackground.end();
    }

    /**
    Input: Void
    Output: Void
    Purpose: Draws wireframe of the collectibles -- needs to be redone along with collectible objects
    */
    private void renderCollectible() {
        shapeRendererCollectible.setProjectionMatrix(camera.projection);
        shapeRendererCollectible.setTransformMatrix(camera.view);
        shapeRendererCollectible.begin(ShapeRenderer.ShapeType.Line);
        shapeRendererCollectible.end();
    }

    /**
    Input: Void
    Output: Void
    Purpose: Updates all the moving components and game variables
    */
    private void update(float delta) {
        menuStage.getViewport().update(viewport.getScreenWidth(), viewport.getScreenHeight(), true);

        spawnTimer -= delta;

        handleInput();
        if (spawnTimer <= 0) {
            spawnEnemy(EnemyType.DUMMY);
            spawnTimer = ENEMY_SPAWN_TIME + MathUtils.random(-5f, 5f);
        }

        for (Bullet bullet : projectiles) {
            bullet.update(delta);

            if (player.isColliding(bullet.hitbox)) {
                // player take damage
                player.takeDamage(bullet.damage);
                if (player.health <= 0) {
                    lives--;
                    player.health = 100;
                }
            }

            // should be cheap-ish, not too many enemies in the game
            // definitely want a better way to check collisions
            for (int i = 0; i < enemies.size; i++) {
                Ship enemy = enemies.get(i);

                if (enemy.isColliding(bullet.hitbox)) {
                    // enemy take damage
                    enemy.takeDamage(bullet.damage);
                    if (enemy.health <= 0) {
                        enemies.removeValue(enemy, true);
                    }
                }
            }
        }

        player.update(delta);
        //If player has 0 lives end the game
        if (lives == 0) {
            endGame();
            // we don't need to update anymore if the game is over
            if (isGameEnded) return;
        }

        for (Ship enemy : enemies) {
            enemy.update(delta);
        }
    }

    private void handleInput() {
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean mouseDown = Gdx.input.isKeyJustPressed(Input.Keys.NUM_2);
        boolean mouseUp = Gdx.input.isKeyJustPressed(Input.Keys.NUM_3);

        if (left && up) {
            player.move(Direction.UP_LEFT);
        } else if (left && down) {
            player.move(Direction.DOWN_LEFT);
        } else if (right && up) {
            player.move(Direction.UP_RIGHT);
        } else if (right && down) {
            player.move(Direction.DOWN_RIGHT);
        } else if (left) {
            player.move(Direction.LEFT);
        } else if (right) {
            player.move(Direction.RIGHT);
        } else if (up) {
            player.move(Direction.UP);
        } else if (down) {
            player.move(Direction.DOWN);
        } else {
            player.stop();
        }

        if(mouseDown){
            itemSelected--;
            if(itemSelected < 0){itemSelected = 8;}
            playMMBDown();
        }
        else if(mouseUp){
            itemSelected++;
            if(itemSelected > 8){itemSelected = 0;}
            playMMBUp();
        }
    }

    /**
    Input: Void
    Output: Void
    Purpose: Puts the game in end game state
    */
    private void endGame() {
        isGameEnded = true;
    }

    /**
    Input: Void
    Output: Void
    Purpose: Restarts the game to base state
    */
    private void restart() {
        // TODO
    }

    /**
    Input: Void
    Output: Void
    Purpose: Central drawing function
    */
    private void draw() {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        batch.draw(backgroundUITexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(backgroundGameTexture, 100, 0, 280, WORLD_HEIGHT);
        batch.draw(infoBoardTexture, 390, 30, 80, 200);
        drawSkillBar();
        drawStats();

        //If dev mode is on draw hit boxes and phone stats
        if (developerMode) {
            drawDeveloperInfo();
        }
        batch.end();

        batch.begin();

        player.draw(batch);
        for (Ship enemy : enemies) { enemy.draw(batch); }
        for (Bullet bullet : projectiles) { bullet.draw(batch); }

        batch.end();

        //Draw open menu button
        if (!isPaused) {
            menuStage.draw();
        }

        batch.begin();
        //Draw the menu pop up
        bitmapFont.getData().setScale(0.3f);
        if (isPaused || isGameEnded) {
            batch.draw(popUpTexture, WORLD_WIDTH/2f - 200/2f, WORLD_HEIGHT/2 - 300/2f, 200, 300);
        }
        //Draw the help menu
        if (helpFlag) {
            batch.draw(popUpTexture, WORLD_WIDTH/2f - 200/2f, WORLD_HEIGHT/2 - 300/2f, 200, 300);
            drawInstructions();
            drawHelpScreen();
        }
        batch.end();

        //Draw the buttons over the pop up
        if (isPaused || isGameEnded || helpFlag) {
            menuStage.draw();
        }

        batch.begin();
        //Draw the menu button text
        if (isPaused && !helpFlag) {
            drawButtonText();
        }
        else if(helpFlag) {
            bitmapFont.getData().setScale(.4f);
            centerText(bitmapFont, "Back",  WORLD_WIDTH/2f, 103);
        }

        if(!helpFlag){ drawMenuText(); }

        batch.end();
    }

    private void drawSkillBar(){
        batch.draw(skillBarTexture, 50 - 47/2f, 0, 47, 250);
        batch.draw(highlightTexture, 50 - 25/2f, 35 + 19 * itemSelected, 25, 25);
    }

    private void drawStats(){
        batch.draw(healthTexture, 10, WORLD_HEIGHT - 40, 80, 30);
        bitmapFont.getData().setScale(.15f);
        centerText(bitmapFont, "Health", 50, WORLD_HEIGHT - 17);
        batch.draw(energyTexture, 10, WORLD_HEIGHT - 70, 80, 30);
        centerText(bitmapFont, "Power", 50, WORLD_HEIGHT - 47);
    }

    private void drawMenuText(){
        bitmapFont.getData().setScale(.4f);
        centerText(bitmapFont, "Menu", WORLD_WIDTH - 97/2f, WORLD_HEIGHT - 20 - 21.15f/4f);
    }

    private void drawInstructions(){
        bitmapFont.getData().setScale(.5f);
        centerText(bitmapFont, "Instruction", WORLD_WIDTH/2f, 230);
        bitmapFont.getData().setScale(.35f);

        centerText(bitmapFont, "Move - WASD", WORLD_WIDTH/2f, 210);
        centerText(bitmapFont, "Shoot - Left Mouse Button", WORLD_WIDTH/2f, 190);
        centerText(bitmapFont, "Select Item - Mouse Scroll",  WORLD_WIDTH/2f, 170);
        centerText(bitmapFont, "Remove Item - Space",  WORLD_WIDTH/2f, 150);
    }

    /**
    Input: Void
    Output: Void
    Purpose: Draws the hit boxes and the phone stats
    */
    private void drawDeveloperInfo() {
        //Batch setting up texture
        int x = (int) Gdx.input.getAccelerometerX();
        int y = (int) Gdx.input.getAccelerometerY();
        int z = (int) Gdx.input.getAccelerometerZ();
        centerText(bitmapFontDeveloper, "X: " + x, 40, 300);
        centerText(bitmapFontDeveloper, "Y: " + y, 40, 280);
        centerText(bitmapFontDeveloper, "Z: " + z, 40, 260);
        if (Math.abs(x) > Math.abs(y) && Math.abs(x) > Math.abs(z)) {
            centerText(bitmapFontDeveloper, "Surface X", 40, 240);
        } else if (Math.abs(y) > Math.abs(x) && Math.abs(y) > Math.abs(z)) {
            centerText(bitmapFontDeveloper, "Surface Y", 40, 240);
        } else if (Math.abs(z) > Math.abs(x) && Math.abs(z) > Math.abs(y)) {
            centerText(bitmapFontDeveloper, "Surface Z", 40, 240);
        }
    }

    /**
    Input: Void
    Output: Void
    Purpose: Draws text over the menu buttons
    */
    private void drawButtonText() {
        String string;
        for (int i = 1; i < NUM_BUTTONS; i++) {
            string = menuButtonText[i - 1];
            //If the volume is off draw Sound On else Sound off
            if (i == 3 && sfxVolume == 0) {string = menuButtonText[4];}
            centerText(bitmapFont, string, WORLD_WIDTH/2f , 238 - (10 + 40f) * (i-1));
        }
    }

    /**
    Input: Void
    Output: Void
    Purpose: Draws the help screen
    */
    private void drawHelpScreen() {
    }


    /**
    Input: BitmapFont for size and font of text, string the text, and x and y for position
    Output: Void
    Purpose: General purpose function that centers the text on the position
    */
    private void centerText(BitmapFont bitmapFont, String string, float x, float y) {
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(bitmapFont, string);
        bitmapFont.draw(batch, string, x - glyphLayout.width / 2, y + glyphLayout.height / 2);
    }

    /**
    Input: The given string, length - how many chars do we go till we start a new line
    Output: Void
    Purpose: This function take a string and adds a new line whenever it reaches the length between it's starting position andlengtht,
    if start + length happens to occur on a non space char it goes back to the nearest space char
    */
    private String addNewLine(String str){
        int spaceFound;
        int reminder = 0; //Used to push back the check to wherever the last " " was
        for (int j = 0; 14 * (j + 1) + j - reminder < str.length(); j++) {
            //Finds the new position of where a " " occurs
            spaceFound = str.lastIndexOf(" ", 14 * (j + 1) + j - reminder);
            //Adds in a new line if this is not the end of the string
            if(str.length() >= spaceFound + 1){
                str = str.substring(0, spaceFound + 1) + "\n" + str.substring(spaceFound);
                reminder = 14 * (j + 1) + j - spaceFound;
            }
        }
        return str;
    }

    /**
    Input: Void
    Output: Void
    Purpose: Updates all the variables on the screen
    */
    private void clearScreen() {
        clearScreen(Color.BLACK);
    }

    private void clearScreen(Color c) {
        Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
    Input: Void
    Output: Void
    Purpose: Destroys everything once we move onto the new screen
    */
    @Override
    public void dispose() {
        menuStage.dispose();
        music.dispose();
    }
}
