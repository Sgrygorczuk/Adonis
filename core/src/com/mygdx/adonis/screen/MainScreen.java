package com.mygdx.adonis.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.adonis.AddOn;
import com.mygdx.adonis.AddOnData;
import com.mygdx.adonis.Adonis;
import com.mygdx.adonis.Alignment;
import com.mygdx.adonis.Bullet;
import com.mygdx.adonis.Direction;
import com.mygdx.adonis.enemies.BaseEnemy;
import com.mygdx.adonis.enemies.BossEnemy;
import com.mygdx.adonis.enemies.CargoEnemy;
import com.mygdx.adonis.enemies.DiveEnemy;
import com.mygdx.adonis.enemies.EnemyType;
import com.mygdx.adonis.Player;
import com.mygdx.adonis.Ship;

import static com.mygdx.adonis.Consts.ADD_ON_TILE;
import static com.mygdx.adonis.Consts.BULLET_SPAWN_TIME_END;
import static com.mygdx.adonis.Consts.BULLET_SPAWN_TIME_START;
import static com.mygdx.adonis.Consts.BULLET_TILE_SIZE;
import static com.mygdx.adonis.Consts.ENERGY_BURN_TIME;
import static com.mygdx.adonis.Consts.LEFT_BOUND;
import static com.mygdx.adonis.Consts.RIGHT_BOUND;
import static com.mygdx.adonis.Consts.TILE_WIDTH;
import static com.mygdx.adonis.Consts.WORLD_HEIGHT;
import static com.mygdx.adonis.Consts.WORLD_WIDTH;

class MainScreen extends ScreenAdapter implements InputProcessor {
    /*
    Image processing -- Objects that modify the view and textures
    */
    private Viewport uiViewport;             //The screen where we display things
    private Camera tiledCamera;                 //The camera viewing the viewport
    private Viewport tiledViewport;             //The screen where we display things
    private Camera uiCamera;                 //The camera viewing the viewport
    private final SpriteBatch batch = new SpriteBatch();             //Batch that holds all of the textures

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
    private final Array<Ship> enemies = new Array<>();
    private final Array<Bullet> projectiles = new Array<>();
    private final Array<AddOn> addOns = new Array<>();

    //Music that will start
    private Music music;

    //Font used for the user interaction
    private BitmapFont bitmapFont = new BitmapFont();

    //Textures
    private Texture popUpTexture;                       //Pop up menu to show menu buttons and Help screen
    private Texture backgroundUITexture;
    private Texture skillBarTexture;
    private Texture highlightTexture;
    private Texture infoBoardTexture;
    private Texture energyOnTexture;
    private Texture energyOffTexture;
    private Texture dividerTexture;
    private Texture healthTexture;
    private Texture energyTexture;
    private Texture scoreBoardTexture;
    private Texture shieldTexture;

    private TextureRegion[][] playerSpriteSheet;
    private TextureRegion[][] enemyZeroSpriteSheet;
    private TextureRegion[][] enemyOneSpriteSheet;
    private TextureRegion[][] enemyTwoSpriteSheet;
    private TextureRegion[][] playerLaserTexture;
    private TextureRegion[][] enemyLaserTexture;
    private TextureRegion[][] addOnTexture;

    private final short NUM_BUTTONS = 5;

    // updated each frame; amount that was scrolled last frame
    private int scrollAmt = 0;

    //Tiled
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private float levelHeight;

    //Names of buttons
    private final String[] menuButtonText = new String[]{"Restart", "Help", "Sound Off", "Main Menu", "Sound On"};

    //Flags
    private int itemSelected = 8;
    private boolean developerMode = true;      //Developer mode shows hit boxes and phone data
    private boolean startGame = false;
    private boolean isPaused = false;         //Stops the game from updating
    private boolean isGameEnded = false;            //Tells us game has been lost
    private float sfxVolume = 1f;               //Current sfx volume
    private boolean helpFlag = false;           //Tells us if help flag is on or off
    boolean letGo = true;
    String endString = "";
    private int score = 0;

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
        uiViewport.update(width, height);
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Central function for setting up intial data
     */
    @Override
    public void show() {
        showCamera();       //Set up the camera
        showTextures();     //Sets up textures
        showObjects();      //Sets up font and Tiled data
        showButtons();      //Sets up the buttons
        showMusic();        //Sets up music
        if (developerMode) {
            showRender();
        }    //If in developer mode sets up the renders
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Sets up the two camera, one for the UI one for tracking the Tiled Map
     */
    private void showCamera() {
        tiledCamera = new OrthographicCamera();                                          //Sets a 2D view
        tiledCamera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);           //Places the camera in the center of the view port
        tiledCamera.update();                                                            //Updates the camera
        uiViewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, tiledCamera);        //Stretches the image to fit the screen
        uiViewport.apply();

        uiCamera = new OrthographicCamera();                                       //Sets a 2D view
        uiCamera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);        //Places the camera in the center of the view port
        uiCamera.update();                                                         //Updates the camera
        tiledViewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, uiCamera);  //Stretches the image to fit the screen
        tiledViewport.apply();
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Sets up all the textures and texture regions
     */
    private void showTextures() {
        //Textures
        popUpTexture = new Texture(Gdx.files.internal("UI/MenuPanel.png"));
        backgroundUITexture = new Texture(Gdx.files.internal("UI/GameBackground.png"));
        skillBarTexture = new Texture(Gdx.files.internal("UI/SkillBar.png"));
        highlightTexture = new Texture(Gdx.files.internal("UI/Highlight.png"));
        infoBoardTexture = new Texture(Gdx.files.internal("UI/InformationPanel.png"));
        energyOnTexture = new Texture(Gdx.files.internal("UI/PilotEnergy.png"));
        energyOffTexture = new Texture(Gdx.files.internal("UI/EnergyOff.png"));
        dividerTexture = new Texture(Gdx.files.internal("UI/Divider.png"));
        healthTexture = new Texture(Gdx.files.internal("UI/Health.png"));
        energyTexture = new Texture(Gdx.files.internal("UI/Energy.png"));
        scoreBoardTexture = new Texture(Gdx.files.internal("UI/PointPanel.png"));
        shieldTexture = new Texture(Gdx.files.internal("Sprites/Shield.png"));

        //Texture Regions
        Texture playerTexturePath = new Texture(Gdx.files.internal("Sprites/PlayerShip.png"));
        playerSpriteSheet = new TextureRegion(playerTexturePath).split(playerTexturePath.getWidth() / 30, playerTexturePath.getHeight() / 5);

        Texture enemyZeroTexturePath = new Texture(Gdx.files.internal("Sprites/EnemyZero.png"));
        enemyZeroSpriteSheet = new TextureRegion(enemyZeroTexturePath).split(enemyZeroTexturePath.getWidth() / 30, enemyZeroTexturePath.getHeight() / 5);

        Texture enemyOneTexturePath = new Texture(Gdx.files.internal("Sprites/EnemyOne.png"));
        enemyOneSpriteSheet = new TextureRegion(enemyOneTexturePath).split(enemyOneTexturePath.getWidth() / 30, enemyOneTexturePath.getHeight() / 5);

        Texture enemyTwoTexturePath = new Texture(Gdx.files.internal("Sprites/EnemyTwo.png"));
        enemyTwoSpriteSheet = new TextureRegion(enemyTwoTexturePath).split(enemyTwoTexturePath.getWidth() / 30, enemyTwoTexturePath.getHeight() / 5);


        Texture playerLaserTexturePath = new Texture(Gdx.files.internal("Sprites/PlayerShot.png"));
        playerLaserTexture = new TextureRegion(playerLaserTexturePath).split(playerLaserTexturePath.getWidth() / 2, playerLaserTexturePath.getHeight());
        Texture enemyLaserTexturePath = new Texture(Gdx.files.internal("Sprites/EnemyShot.png"));
        enemyLaserTexture = new TextureRegion(enemyLaserTexturePath).split(enemyLaserTexturePath.getWidth() / 2, enemyLaserTexturePath.getHeight());

        Texture addOnTexturePath = new Texture(Gdx.files.internal("Sprites/AddOns.png"));
        addOnTexture = new TextureRegion(addOnTexturePath).split(addOnTexturePath.getWidth(), addOnTexturePath.getHeight() / 3);

    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Central function for setting up all the buttons and stage
     */
    private void showButtons() {
        menuStage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));

        InputMultiplexer mux = new InputMultiplexer(this, menuStage);
        Gdx.input.setInputProcessor(mux); //Gives control to the stage for clicking on buttons


        //Sets up 6 Buttons
        menuButtons = new ImageButton[6];

        setUpOpenMenuButton();  //Sets up button used to open the menu
        setUpMenuButtons();     //Sets up the button in the menu
        setUpExitButton();      //Sets up the button used to exit Help
    }

    /**
     * Input: EnemyType - What texture/behavior to give the enemy
     * X and Y - position where the enemy spawn
     * Output: Void
     * Purpose: Creates all the enemies in the level
     */
    private void spawnEnemy(EnemyType enemyType, float x, float y) {
        Ship enemy;
        switch (enemyType) {
            case CARGO: {
                enemy = new CargoEnemy(enemyZeroSpriteSheet, x, y);
                break;
            }
            case BASE: {
                enemy = new BaseEnemy(enemyOneSpriteSheet, x, y);
                break;
            }
            case DIVE: {
                enemy = new DiveEnemy(enemyTwoSpriteSheet, x, y);
                break;
            }
            case BOSS: {
                enemy = new BossEnemy(playerSpriteSheet, WORLD_WIDTH/2f - 15*TILE_WIDTH/2f, y);
                break;
            }
            default:
                enemy = new CargoEnemy(enemyZeroSpriteSheet, x, y);
        }

        this.enemies.add(enemy);
    }

    /**

     */
    private void spawnAddOn(int id, float x, float y) {
        AddOn addOn = new AddOn(addOnTexture[id][0], x, y, id);

        this.addOns.add(addOn);
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Sets up the button that will open the menu
     */
    private void setUpOpenMenuButton() {
        //Set up the texture
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(620, 141); //Breaks down the texture into tiles

        //Place the button
        menuButtons[0] = new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
        menuButtons[0].setPosition(432 - 90 / 2f, WORLD_HEIGHT - 20 - 21.15f);
        menuButtons[0].setWidth(90);
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

    /**
     * Input: Void
     * Output: Void
     * Purpose: Sets up the buttons in the main menu, Restart, Help, Sound Off/On and Main Menu
     */
    private void setUpMenuButtons() {
        //Sets up the texture
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(620, 141); //Breaks down the texture into tiles

        //Sets up the position of the buttons in a square 2x2
        for (int i = 1; i < NUM_BUTTONS; i++) {
            menuButtons[i] = new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));

            menuButtons[i].setPosition(WORLD_WIDTH / 2f - 150 / 2f, 215 - (10 + 40f) * (i - 1));
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
     * Input: Void
     * Output: Void
     * Purpose: Turns the Volume and Music on and off.
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
     * Input: Void
     * Output: Void
     * Purpose: Sets up the button that exits the user out of the Help Menu
     */
    private void setUpExitButton() {
        //Sets up the texture
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(620, 141); //Breaks down the texture into tiles

        //Sets up the position
        menuButtons[NUM_BUTTONS] = new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
        menuButtons[NUM_BUTTONS].setPosition(WORLD_WIDTH / 2f - 150 / 2f, 80);
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
                if(helpFlag){
                    helpFlag = false;
                    //Turn on all buttons but turn off this one
                    for (ImageButton imageButton : menuButtons) {
                        imageButton.setVisible(true);
                    }
                    menuButtons[NUM_BUTTONS].setVisible(false);
                }
                if(isGameEnded){
                    music.stop();
                    adonis.setScreen(new MenuScreen(adonis));
                }
            }
        });
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Sets up the Font and reads the tiled map to spawn background,
     * enemies and player.
     */
    private void showObjects() {
        if (adonis.getAssetManager().isLoaded("Fonts/Font.fnt")) {
            bitmapFont = adonis.getAssetManager().get("Fonts/Font.fnt");
        }
        bitmapFont.getData().setScale(0.6f);

        //Gets the map
        tiledMap = adonis.getAssetManager().get("Tiled/AdonisMap.tmx");
        //Makes it into a drawing that we can call
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        //Center the drawing based on the camera
        orthogonalTiledMapRenderer.setView((OrthographicCamera) tiledCamera);

        //Uses to tell
        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        levelHeight = tiledMapTileLayer.getHeight() * tiledMapTileLayer.getTileHeight();

        populateEnemies();
        populatePlayer();
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Take the Tiled map data and spawn the user on it
     */
    private void populatePlayer() {
        //Grab the layer from tiled map
        MapLayer mapLayer = tiledMap.getLayers().get("Player");
        //For each instance of that in the layered map create a skull collectible at it's position
        for (MapObject mapObject : mapLayer.getObjects()) {
            player = new Player(playerSpriteSheet, shieldTexture,
                    mapObject.getProperties().get("x", Float.class),
                    mapObject.getProperties().get("y", Float.class));
        }
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Takes the Object data from the tiled Map and extracts it into diffident enemies
     */
    private void populateEnemies() {
        //Grab the layer from tiled map
        MapLayer mapLayer = tiledMap.getLayers().get("Cargo");
        //For each instance of that in the layered map create a skull collectible at it's position
        for (MapObject mapObject : mapLayer.getObjects()) {
            spawnEnemy(EnemyType.CARGO,
                    mapObject.getProperties().get("x", Float.class),
                    mapObject.getProperties().get("y", Float.class));
        }

        mapLayer = tiledMap.getLayers().get("Base");
        //For each instance of that in the layered map create a skull collectible at it's position
        for (MapObject mapObject : mapLayer.getObjects()) {
            spawnEnemy(EnemyType.BASE,
                    mapObject.getProperties().get("x", Float.class),
                    mapObject.getProperties().get("y", Float.class));
        }


        mapLayer = tiledMap.getLayers().get("Dive");
        //For each instance of that in the layered map create a skull collectible at it's position
        for (MapObject mapObject : mapLayer.getObjects()) {
            spawnEnemy(EnemyType.DIVE,
                    mapObject.getProperties().get("x", Float.class),
                    mapObject.getProperties().get("y", Float.class));
        }

        mapLayer = tiledMap.getLayers().get("Boss");
        //For each instance of that in the layered map create a skull collectible at it's position
        for (MapObject mapObject : mapLayer.getObjects()) {
            spawnEnemy(EnemyType.BOSS,
                    mapObject.getProperties().get("x", Float.class),
                    mapObject.getProperties().get("y", Float.class));
        }
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Sets up all the textures and texture regions
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
     * Play sound effect for when button is pressed
     */
    private void playPlayerShoot() {
        adonis.getAssetManager().get("SFX/PlayerShoot.wav", Sound.class).play(0.1f * sfxVolume);
    }


    /**
     * Play sound effect for when button is pressed
     */
    private void playEnemyShoot() {
        adonis.getAssetManager().get("SFX/EnemyShoot.wav", Sound.class).play(0.05f * sfxVolume);
    }

    /**
     * Play sound effect for when button is pressed
     */
    private void playHit() {
        adonis.getAssetManager().get("SFX/Hit.wav", Sound.class).play(0.1f * sfxVolume);
    }


    /**
     * Play sound effect for when button is pressed
     */
    private void playExplosion() {
        adonis.getAssetManager().get("SFX/Explosion.wav", Sound.class).play(0.1f * sfxVolume);
    }

    /**
     * Play sound effect for when button is pressed
     */
    private void playPowerDown() {
        adonis.getAssetManager().get("SFX/PowerDown.wav", Sound.class).play(1f * sfxVolume);
    }

    /**
     * Play sound effect for when button is pressed
     */
    private void playPowerUp() {
        adonis.getAssetManager().get("SFX/PowerUp.mp3", Sound.class).play(0.1f * sfxVolume);
    }


    /**
     * Input: Void
     * Output: Void
     * Purpose: Central function to draw shaperenders/Hitboxes
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
     * Input: Void
     * Output: Void
     * Purpose: Draws all of the variables on the screen
     */
    @Override
    public void render(float delta) {
        if (!isPaused && startGame && !isGameEnded) {
            update(delta);
        }
        else{
            if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)){startGame = true;}
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
     * Input: Void
     * Output: Void
     * Purpose: Draws the enemy/obstacle wireframe
     */
    private void renderEnemy() {
        shapeRendererEnemy.setProjectionMatrix(uiCamera.projection); //Screen set up camera
        shapeRendererEnemy.setTransformMatrix(uiCamera.view);        //Screen set up camera
        shapeRendererEnemy.begin(ShapeRenderer.ShapeType.Line);    //Sets up to draw lines
        for (Ship enemy : enemies) {
//            if(enemy.dieFlag) continue;
            enemy.drawDebug(shapeRendererEnemy);
        }
        for (Bullet bullet : projectiles) {
            if (bullet.alignment == Alignment.ENEMY) {
                bullet.drawDebug(shapeRendererEnemy);
            }
        }
        shapeRendererEnemy.end();
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws user wireframe
     */
    private void renderUser() {
        shapeRendererUser.setProjectionMatrix(uiCamera.projection);    //Screen set up camera
        shapeRendererUser.setTransformMatrix(uiCamera.view);           //Screen set up camera
        shapeRendererUser.begin(ShapeRenderer.ShapeType.Line);       //Sets up to draw lines
        player.drawDebug(shapeRendererUser);
        for (Bullet bullet : projectiles) {
                if (bullet.alignment == Alignment.PLAYER) {
                bullet.drawDebug(shapeRendererUser);

                }
        }
        shapeRendererUser.end();
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws the background object and UI wireframes
     */
    private void renderBackground() {
        shapeRendererBackground.setProjectionMatrix(uiCamera.projection);  //Screen set up camera
        shapeRendererBackground.setTransformMatrix(uiCamera.view);         //Screen set up camera
        shapeRendererBackground.begin(ShapeRenderer.ShapeType.Line);     //Starts to draw
        shapeRendererBackground.end();
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws wireframe of the collectibles -- needs to be redone along with collectible objects
     */
    private void renderCollectible() {
        shapeRendererCollectible.setProjectionMatrix(uiCamera.projection);
        shapeRendererCollectible.setTransformMatrix(uiCamera.view);
        shapeRendererCollectible.begin(ShapeRenderer.ShapeType.Line);
        for (AddOn addOn : addOns) { addOn.drawDebug(shapeRendererCollectible); }
        shapeRendererCollectible.end();
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Central function for updating changing variables
     */
    private void update(float delta) {
        handleInput();              //Checks user input
        updatePlayer(delta);        //Updates player ship based on input
        updateEnemies(delta);       //Updates enemy ship actions
        updateAddOns(delta);        //Updates addOns
        updateCollision(delta);     //Check for collision between ships and bullets
        updateCamera(delta);        //Update Tiled camera placement and menu stage size.

        if(enemies.size == 0){ endGame(0); }
        else if (player.health <= 0 && player.getBlowUpFlag()) { endGame(1); }
    }

    /**
     * Input: Delta - for timing
     * Output: Void
     * Purpose: Updates player position
     * Checks life to end game
     */
    public void updatePlayer(float delta) {
        player.update(delta);
    }

    public void updateAddOns(float delta){
        for (AddOn addOn : addOns) {
            addOn.update(delta);

            if (addOn.hitbox.y < -addOn.hitbox.height) { addOns.removeValue(addOn, true); }
        }
    }


    /**
     * Input: Delta - for timing
     * Output: Void
     * Purpose: Updates enemy position and actions
     */
    public void updateEnemies(float delta) {
        for (Ship enemy : enemies) {
             enemy.update(delta);

            if (enemy.health <= 0 && enemy.getBlowUpFlag() || enemy.hitbox.y < -enemy.hitbox.height) {
                if(enemy.hitbox.y > -enemy.hitbox.height) {
                    playExplosion();
                    this.score += enemy.points;
                }
                if(enemy.maxHealth == 60) {
                    spawnAddOn(MathUtils.random(0, 2), enemy.hitbox.x + enemy.hitbox.getWidth() / 2f - ADD_ON_TILE / 2f,
                            enemy.hitbox.y + enemy.hitbox.height / 2f - ADD_ON_TILE / 2f);
                }
                enemies.removeValue(enemy, true);
            }

            if (enemy.dieFlag) continue;

            if(enemy.hitbox.y <= WORLD_HEIGHT && enemy.maxHealth != 60) {
                if (enemy.shootTimer <= 0) {
                    projectiles.add(new Bullet(Alignment.ENEMY, Direction.DOWN,
                            enemy.hitbox.getX() + enemy.hitbox.getWidth()/2f - BULLET_TILE_SIZE/2f,
                            enemy.hitbox.getY(), player.hitbox.x + player.hitbox.width/2f, player.hitbox.y,
                            enemyLaserTexture,
                            enemy.damage));
                    enemy.shootTimer = MathUtils.random(BULLET_SPAWN_TIME_START, BULLET_SPAWN_TIME_END);
                    playEnemyShoot();
                }
            }
        }

    }

    /**
     * Input: Delta - for timing
     * Output: Void
     * Purpose: Checks for collisions between enemy and bullets and then gives damage
     */
    public void updateCollision(float delta) {
        int bulletInd = 0;
        while (bulletInd < projectiles.size) {
            Bullet bullet = projectiles.get(bulletInd);
            boolean hit = false;
            bullet.update(delta);
            if (bullet.hitbox.getY() < 0 || bullet.hitbox.getY() > WORLD_HEIGHT
                    || bullet.hitbox.getX() + bullet.hitbox.getWidth() < 100 || bullet.hitbox.getX()  > 380) {
                projectiles.removeValue(bullet, true);
                continue;
            }

            for (AddOn addOn : addOns) {
                if(player.isColliding(addOn.hitbox) && player.addOns.size < 9){
                    player.onInstall(AddOnData.getById(addOn.id));
                    addOns.removeValue(addOn, true);
                    playPowerUp();
                }

            }

            if (bullet.alignment != Alignment.PLAYER && player.isColliding(bullet.hitbox)) {
                // player take damage
                hit = true;
                player.takeDamage(bullet.damage);
                playHit();
                if (player.health <= 0) {
                    playExplosion();
                    player.health = 0;
                }
            }

            // should be cheap-ish, not too many enemies in the game
            // definitely want a better way to check collisions
            for (int i = 0; i < enemies.size; i++) {
                Ship enemy = enemies.get(i);
                if (enemy.dieFlag) continue;
                if (bullet.alignment != Alignment.ENEMY && enemy.isColliding(bullet.hitbox)) {
                    // enemy take damage
                    hit = true;
                    playHit();
                    enemy.takeDamage(bullet.damage);
                }
            }
            if (hit) {
                projectiles.removeValue(bullet, true);
            } else {
                bulletInd++;
            }
        }
    }

    /**
     * Input: Detla - used for timing change in tiled map movement
     * Output: Void
     * Purpose: Updates the size of the stage in case user changes size of window
     * Moves the Tiled Camera over the tiled map
     */
    public void updateCamera(float delta) {
        //Resize the menu Stage if the screen changes size
        menuStage.getViewport().update(uiViewport.getScreenWidth(), uiViewport.getScreenHeight(), true);

        if (tiledCamera.position.y + delta * 100 + WORLD_HEIGHT < levelHeight) {
            tiledCamera.position.y += delta * 100;
            tiledCamera.position.set(tiledCamera.position.x, tiledCamera.position.y, tiledCamera.position.z);
            tiledCamera.update();
            orthogonalTiledMapRenderer.setView((OrthographicCamera) tiledCamera);
        }
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Central Input Handling function
     */
    private void handleInput() {
        handleShooting();           //Checks user shoot input
        handleMovement();           //Checks user movement input
        handleScrolling();          //Checks user scroll input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            System.out.println(8-itemSelected);
            boolean soundCheck = player.ejectSelected(8-itemSelected);
            if(soundCheck){playPowerDown();}
        }
        if(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)){
            if(player.dir.getX() > 0){
                player.setAnimationState(6);
            }
            else {
                player.setAnimationState(5);
            }
        }

        //Allows user to turn on dev mode
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) { developerMode = !developerMode; }
        //Give Dev actions
        if (developerMode) { handleDevInputs(); }

    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Handles shooting using the LMB
     */
    public void handleShooting() {
//        if (!player.hasWeapon()) { return; }
        //Used to make sure that the bullets don't start shooting when the user is trying to
        //click main menu
        float touchedX = Gdx.input.getX() * WORLD_WIDTH / Gdx.graphics.getWidth();
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && player.shootTimer <= 0 && touchedX >= LEFT_BOUND - 5 && touchedX <= RIGHT_BOUND) {
            projectiles.add(new Bullet(Alignment.PLAYER, Direction.UP,
                    player.hitbox.x + player.hitbox.width/2f - BULLET_TILE_SIZE/2f,
                    player.hitbox.y + player.hitbox.height*0.835f, 0, 0,
                    playerLaserTexture,
                    player.damage));
            player.shootTimer = player.shootLag;
            playPlayerShoot();
        }
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Handles ship movement using WASD or arrow keys
     */
    public void handleMovement() {
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);


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
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Handles MMB scroll to select addOns in inventory
     */
    public void handleScrolling() {
        boolean mouseDown = scrollAmt > 0;
        boolean mouseUp = scrollAmt < 0;

        if (mouseDown) {
            itemSelected--;
            if (itemSelected < 0) {
                itemSelected = 8;
            }
            playMMBDown();
        } else if (mouseUp) {
            itemSelected++;
            if (itemSelected > 8) {
                itemSelected = 0;
            }
            playMMBUp();
        }

        scrollAmt = 0;
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Allows Dev to mess with addOns for the ship
     */
    private void handleDevInputs() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            //player.onInstall(AddOnData.BATTERY);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            //player.onInstall(AddOnData.CHARGER);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            //player.onInstall(AddOnData.SHIELD);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)){
            //player.onInstall(AddOnData.HEALTH_BAR_GUI);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            //player.onInstall(AddOnData.ENERGY_BAR_GUI);
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.NUM_6))){
            //player.onInstall(AddOnData.WEAPON_BOOST);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) {

        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {

        } else if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
           enemies.clear();
        }else if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            player.health = 10;
        }
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Puts the game in end game state
     */
    private void endGame(int endType) {
        System.out.println("Hello");
        menuButtons[NUM_BUTTONS].setVisible(true);
        switch (endType){
            case 0:{
                //TODO add score
                endString = addNewLine("You have won! You're score is: " + score, 15);
                break;
            }
            case 1:{
                endString =  addNewLine("You are lost, your parts are now galactic trash...", 15);
                break;
            }
            default:{
                endString = "";
            }
        }
        isGameEnded = true;
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Restarts the game to base state
     */
    private void restart() {
        tiledCamera.position.y = uiCamera.position.y;
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Central drawing function
     */
    private void draw() {
        //Draws the background image
        batch.setProjectionMatrix(uiCamera.projection);
        batch.setTransformMatrix(uiCamera.view);
        batch.begin();
        batch.draw(backgroundUITexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.end();

        //Draws the tiled map
        drawTiledMap();

        batch.setProjectionMatrix(uiCamera.projection);
        batch.setTransformMatrix(uiCamera.view);
        batch.begin();
        //Draws moving objects
        for (AddOn addOn : addOns) {
//            if(enemy.dieFlag) continue;
            addOn.draw(batch);
        }
        for (Ship enemy : enemies) {
//            if(enemy.dieFlag) continue;
            if(enemy.hitbox.x > 100 && enemy.hitbox.x < 380) {enemy.draw(batch);}
            if(enemy.hitbox.x > 100 && enemy.hitbox.x < 380) {enemy.draw(batch);}
        }
        for (Bullet bullet : projectiles) {
            bullet.draw(batch);
        }
        player.draw(batch);

        //Draws UI elements
        batch.draw(dividerTexture, 92, 0, 4, WORLD_HEIGHT);
        batch.draw(dividerTexture, 380, 0, 4, WORLD_HEIGHT);
        drawAddOnInfo();
        drawScore();
        drawAddOnBar();
        drawStats();

        batch.end();

        //Draw open menu button
        if (!isPaused) {
            menuStage.draw();
        }

        batch.begin();
        //Draw the menu pop up
        drawPopUpMenu();
        batch.end();

        //Draw the buttons over the pop up
        if (isPaused || isGameEnded || helpFlag) {
            menuStage.draw();
        }

        batch.begin();
        //Draw the menu button text
        if (isPaused && !helpFlag) {
            drawButtonText();
        } else if (helpFlag) {
            bitmapFont.getData().setScale(.4f);
            centerText(bitmapFont, "Back", WORLD_WIDTH / 2f, 103);
        }

        if (!helpFlag) {
            drawMenuText();
        }

        if (developerMode) {
            drawDeveloperInfo();
        }

        if(!startGame && !isPaused){drawStartGame();}
        if(isGameEnded){drawEndScreen();}
        batch.end();
    }

    private void drawStartGame() {
        bitmapFont.setColor(Color.WHITE);
        //batch.draw(popUpTexture, WORLD_WIDTH / 2f - 100, WORLD_HEIGHT / 2 - 25, 200, 50);
        bitmapFont.getData().setScale(.4f);
        centerText(bitmapFont, "Press Any Key To Start", WORLD_WIDTH/2f, WORLD_HEIGHT/2f);

    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws the tiled map
     */
    private void drawTiledMap() {
        batch.setProjectionMatrix(tiledCamera.projection);
        batch.setTransformMatrix(tiledCamera.view);
        //Draws tiled map
        orthogonalTiledMapRenderer.render();
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws the inventory bar and currently highlighted added on
     */
    private void drawAddOnBar() {
        batch.draw(skillBarTexture, 50 - 47 / 2f, 10, 47, 200);
        for(int i = 0; i < player.addOns.size ; i++){
            batch.draw(addOnTexture[player.addOns.get(i).getId()][0], 51 - 10 / 2f, 164.5f - 15.2f * i, 10, 10);
        }
        batch.draw(highlightTexture, 51 - 22 / 2f, 36 + 15.2f * itemSelected, 22, 22);
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws the player's health and energy bar if the player has those addOns equipped
     */
    private void drawStats() {

        batch.draw(energyOnTexture, 10, WORLD_HEIGHT - 65, 80, 55);
        batch.draw(healthTexture, 14, WORLD_HEIGHT - 44, (float) 73 * player.health / player.maxHealth, 7);



        if(player.energyBurn > 0f){
                // TODO: Burnout Energy Bar right now it just does same thing
            batch.draw(energyOffTexture, 10, WORLD_HEIGHT - 115, 80, 55);
            batch.draw(healthTexture, 14, WORLD_HEIGHT - 94, 73 * (float) player.energyBurn / ENERGY_BURN_TIME, 7);
        } else {
            batch.draw(energyOnTexture, 10, WORLD_HEIGHT - 115, 80, 55);
            batch.draw(energyTexture, 14, WORLD_HEIGHT - 94, 73 * (float) player.energy / player.maxEnergy, 7);
        }
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws the menu background and instructions
     */
    private void drawPopUpMenu() {
        bitmapFont.getData().setScale(0.3f);
        if (isPaused || isGameEnded || helpFlag) {
            batch.draw(popUpTexture, WORLD_WIDTH / 2f - 200 / 2f, WORLD_HEIGHT / 2 - 300 / 2f, 200, 300);
            if (helpFlag) {
                drawInstructions();
            }
        }
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws text over the menu buttons, Restart, Help, Sound Off/On and Main Menu
     */
    private void drawButtonText() {
        String string;
        for (int i = 1; i < NUM_BUTTONS; i++) {
            string = menuButtonText[i - 1];
            //If the volume is off draw Sound On else Sound off
            if (i == 3 && sfxVolume == 0) {
                string = menuButtonText[4];
            }
            centerText(bitmapFont, string, WORLD_WIDTH / 2f, 238 - (10 + 40f) * (i - 1));
        }
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws "Menu" text over the menu button
     */
    private void drawMenuText() {
        bitmapFont.getData().setScale(.4f);
        centerText(bitmapFont, "Menu", WORLD_WIDTH - 97 / 2f, WORLD_HEIGHT - 20 - 21.15f / 4f);
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws the text for instructions
     */
    private void drawInstructions() {
        bitmapFont.getData().setScale(.5f);
        centerText(bitmapFont, "Instruction", WORLD_WIDTH / 2f, 230);
        bitmapFont.getData().setScale(.35f);

        centerText(bitmapFont, "Move - WASD", WORLD_WIDTH / 2f, 210);
        centerText(bitmapFont, "Shoot - Left Mouse Button", WORLD_WIDTH / 2f, 190);
        centerText(bitmapFont, "Select Item - Mouse Scroll", WORLD_WIDTH / 2f, 170);
        centerText(bitmapFont, "Remove Item - Space", WORLD_WIDTH / 2f, 150);
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws the addOn information for currently selected addOn.
     */
    private void drawAddOnInfo() {
        float startPoint  = 230;
        AddOnData selectedAddOn = player.getAddOnAt(8-itemSelected);
        String name;
        String description;
        if(selectedAddOn == null){
            name = "";
            description = "";
        } else {
            name = selectedAddOn.getName();
            description = selectedAddOn.getDescription();
        }
//        String descriptions = selectedAddOn.description();
        batch.draw(infoBoardTexture, 390, 60, 80, 200);
        bitmapFont.getData().setScale(.3f);
        bitmapFont.setColor(Color.BLACK);
        centerText(bitmapFont, addNewLine(name, 10), WORLD_WIDTH - 50, startPoint-30);
        if(selectedAddOn != null) {
            batch.draw(addOnTexture[player.addOns.get(8-itemSelected).getId()][0], WORLD_WIDTH - 70, startPoint-80, 40, 20);
        }
        bitmapFont.getData().setScale(.2f);
        centerText(bitmapFont, addNewLine(description, 12), WORLD_WIDTH - 50, startPoint-100);
    }

    private void drawScore(){
        batch.draw(scoreBoardTexture, 390, 20, 80, 20);
        bitmapFont.getData().setScale(.4f);
        centerText(bitmapFont, score + "", WORLD_WIDTH - 50, 35);
    }

    private void drawEndScreen(){
        bitmapFont.setColor(Color.BLACK);
        bitmapFont.getData().setScale(.5f);
        centerText(bitmapFont, endString, WORLD_WIDTH/2f, WORLD_HEIGHT/2f + 20);

        bitmapFont.getData().setScale(.4f);
        centerText(bitmapFont, "Main Menu", WORLD_WIDTH / 2f, 103);
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Draws dev data
     */
    private void drawDeveloperInfo() {
        bitmapFont.setColor(Color.WHITE);
        bitmapFont.getData().setScale(0.2f);
        centerText(bitmapFont, "Player X:" + player.hitbox.getX(), 140, 300);
        centerText(bitmapFont, "Player Y:" + player.hitbox.getY(), 140, 290);
        centerText(bitmapFont, "Player Max Health:" + player.maxHealth, 140, 280);
        centerText(bitmapFont, "Player Health:" + player.health, 140, 270);
        centerText(bitmapFont, "Player Max Energy:" + player.maxEnergy, 140, 260);
        centerText(bitmapFont, "Player Energy:" + player.energy, 140, 250);
    }

    /**
     * Input: BitmapFont for size and font of text, string the text, and x and y for position
     * Output: Void
     * Purpose: General purpose function that centers the text on the position
     */
    private void centerText(BitmapFont bitmapFont, String string, float x, float y) {
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(bitmapFont, string);
        bitmapFont.draw(batch, string, x - glyphLayout.width / 2, y + glyphLayout.height / 2);
    }

    /**
     * Input: The given string, length - how many chars do we go till we start a new line
     * Output: Void
     * Purpose: This function take a string and adds a new line whenever it reaches the length between it's starting position andlengtht,
     * if start + length happens to occur on a non space char it goes back to the nearest space char
     */
    private String addNewLine(String str, int lineLength) {
        int spaceFound;
        int reminder = 0; //Used to push back the check to wherever the last " " was
        for (int j = 0; lineLength * (j + 1) + j - reminder < str.length(); j++) {
            //Finds the new position of where a " " occurs
            spaceFound = str.lastIndexOf(" ", lineLength * (j + 1) + j - reminder);
            //Adds in a new line if this is not the end of the string
            if (str.length() >= spaceFound + 1) {
                str = str.substring(0, spaceFound + 1) + "\n" + str.substring(spaceFound);
                reminder = lineLength * (j + 1) + j - spaceFound;
            }
        }
        return str;
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Updates all the variables on the screen
     */
    private void clearScreen() {
        clearScreen(Color.BLACK);
    }

    private void clearScreen(Color c) {
        Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Destroys everything once we move onto the new screen
     */
    @Override
    public void dispose() {
        menuStage.dispose();
        music.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        scrollAmt = amount;
        return true;
    }
}
