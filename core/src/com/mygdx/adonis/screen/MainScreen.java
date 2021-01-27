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
import com.mygdx.adonis.Bullet;
import com.mygdx.adonis.Direction;
import com.mygdx.adonis.DummyEnemy;
import com.mygdx.adonis.EnemyType;
import com.mygdx.adonis.Player;
import com.mygdx.adonis.Ship;

import static com.mygdx.adonis.Consts.ENEMY_SPAWN_TIME;
import static com.mygdx.adonis.Consts.TILE_HEIGHT;
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

    private final short NUM_BUTTONS = 5;

    //Names of buttons
    private String[] menuButtonText = new String[]{"Main Menu", "Restart", "Help", "Sound Off", "Sound On"};
    private int lives = 3;              //How many lives player has left

    //Flags
    private boolean developerMode = true;      //Developer mode shows hit boxes and phone data
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
        //showMusic();        //Sets up music
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
        popUpTexture = new Texture(Gdx.files.internal("UI/PopUpBoarder.png"));
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
        Texture playerTexture = new Texture(Gdx.files.internal("Sprites" + tmpPrefix + "/Player.png"));
        this.player = new Player(playerTexture);
    }

    private void spawnEnemy(EnemyType enemyType) {
        Texture enemyTexture;

        // spawn a little above the screen at random x
        Vector2 spawnPos = new Vector2(MathUtils.random(WORLD_WIDTH), WORLD_HEIGHT + TILE_HEIGHT);
        Ship enemy;

        switch (enemyType) {
            case DUMMY:
            default:
                enemyTexture = new Texture(Gdx.files.internal("Sprites" + tmpPrefix + "/Turret.png"));
                enemy = new DummyEnemy(enemyTexture, spawnPos.x, spawnPos.y);
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
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/MenuButton.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(45, 44); //Breaks down the texture into tiles

        //Place the button
        menuButtons[0] = new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
        menuButtons[0].setPosition(430 - buttonSpriteSheet[0][0].getRegionWidth() / 2f, WORLD_HEIGHT - 10 - buttonSpriteSheet[0][0].getRegionHeight());
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
                        if (isPaused) {
                            //Turns on 1-5 buttons
                            menuButtons[i].setVisible(true);
                        } else {
                            //Turns off 1-5 buttons
                            menuButtons[i].setVisible(false);
                        }
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
        Texture popUpButtonTexturePath = new Texture(Gdx.files.internal("UI/ButtonSpriteSheet.png"));
        final TextureRegion[][] popUpButtonSpriteSheet = new TextureRegion(popUpButtonTexturePath).split(117, 47); //Breaks down the texture into tiles

        //Sets up the position of the buttons in a square 2x2
        float x;
        float y;
        for (int i = 1; i < NUM_BUTTONS; i++) {
            menuButtons[i] = new ImageButton(new TextureRegionDrawable(popUpButtonSpriteSheet[i - 1][0]), new TextureRegionDrawable(popUpButtonSpriteSheet[i - 1][1]));
            if (i == 1 || i == 3) {
                x = 380 / 2f - NUM_BUTTONS - popUpButtonSpriteSheet[0][0].getRegionWidth();
            } else {
                x = 380 / 2f + NUM_BUTTONS;
            }
            if (i < 3) {
                y = WORLD_HEIGHT / 2f - 10 + popUpButtonSpriteSheet[0][0].getRegionHeight() / 2f;
            } else {
                y = WORLD_HEIGHT / 2f - 10 - popUpButtonSpriteSheet[0][0].getRegionHeight();
            }
            menuButtons[i].setPosition(x, y);
            menuStage.addActor(menuButtons[i]);
            menuButtons[i].setVisible(false);       //Initially all the buttons are off

            //Sets up each buttons function
            final int finalI = i;
            menuButtons[i].addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    playButtonSFX();
                    //Returns to the main menu
                    if (finalI == 1) {
                        //music.stop();
                        adonis.setScreen(new MenuScreen(adonis));
                    }
                    //Restarts the game
                    else if (finalI == 2) {
                        restart();
                    }
                    //Turns on the help menu
                    else if (finalI == 3) {
                        helpFlag = true;
                        //Turns off all the buttons
                        for (ImageButton imageButton : menuButtons) {
                            imageButton.setVisible(false);
                        }
                        //Turns exit button on
                        menuButtons[NUM_BUTTONS].setVisible(true);
                    }
                    //Turns on/off the sound
                    else {
                        soundButtonAction(finalI, popUpButtonSpriteSheet);
                    }
                }
            });
        }
    }

    /**
     * Changes the button image and turns the sound on and off
     *
     * @param finalI                 Index of last button in menu
     * @param popUpButtonSpriteSheet 2D TextureRegion array containing texture of last button
     */
    private void soundButtonAction(final int finalI, final TextureRegion[][] popUpButtonSpriteSheet) {
        //Gets rid of current button
        menuButtons[finalI].setVisible(false);
        //Turns the volume down
        if (sfxVolume == 1f) {
            //music.stop();
            sfxVolume = 0;
            menuButtons[finalI] = new ImageButton(new TextureRegionDrawable(popUpButtonSpriteSheet[4][0]), new TextureRegionDrawable(popUpButtonSpriteSheet[4][1]));
        }
        //Turns the sound on
        else {
            //music.play();
            sfxVolume = 1;
            menuButtons[finalI] = new ImageButton(new TextureRegionDrawable(popUpButtonSpriteSheet[3][0]), new TextureRegionDrawable(popUpButtonSpriteSheet[3][1]));
        }
        //Creates new button in the place of the old one with a different image
        menuButtons[finalI].setPosition(380 / 2f + NUM_BUTTONS, WORLD_HEIGHT / 2f - 10 - popUpButtonSpriteSheet[0][0].getRegionHeight());
        menuStage.addActor(menuButtons[finalI]);
        //Adds in this function if the button is clicked again
        menuButtons[finalI].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                soundButtonAction(finalI, popUpButtonSpriteSheet);
            }
        });
    }

    /**
     * Set up the button that will be used to exit the help menu
     */
    private void setUpExitButton() {
        //Sets up the texture
        Texture exitButtonTexturePath = new Texture(Gdx.files.internal("UI/ExitButton.png"));
        TextureRegion[][] exitButtonSpriteSheet = new TextureRegion(exitButtonTexturePath).split(45, 44); //Breaks down the texture into tiles

        //Sets up the position
        menuButtons[NUM_BUTTONS] = new ImageButton(new TextureRegionDrawable(exitButtonSpriteSheet[0][0]), new TextureRegionDrawable(exitButtonSpriteSheet[0][1]));
        menuButtons[NUM_BUTTONS].setPosition(WORLD_WIDTH - 50, WORLD_HEIGHT - 50);
        menuButtons[NUM_BUTTONS].setWidth(20);
        menuButtons[NUM_BUTTONS].setHeight(20);
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
        music = adonis.getAssetManager().get("Music/GoboLevelTheme.wav", Music.class);
        music.setVolume(0.1f);
        music.setLooping(true);
        music.play();
    }

    /**
     * Play sound effect for when button is pressed
     */
    private void playButtonSFX() {
        adonis.getAssetManager().get("SFX/Pop.wav", Sound.class).play(1 / 2f);
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

    /*
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

    /*
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

    /*
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

    /*
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

    /*
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

    /*
    Input: Void
    Output: Void
    Purpose: Updates all the moving components and game variables
    */
    private void update(float delta) {

        spawnTimer -= delta;

        if (developerMode){
            devInstallAddon();
        }
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

        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            player.fire();
        }

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
    /*
    Input: Void
    Output: Void
    Purpose: Puts the game in end game state
    */

    private void devInstallAddon(){

        if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0)){
            player.onInstall(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            player.onInstall(55);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            player.onDestroy(55);
        }
    }

    private void endGame() {
        isGameEnded = true;
    }

    /*
    Input: Void
    Output: Void
    Purpose: Restarts the game to base state
    */
    private void restart() {
        // TODO
    }

    /*
    Input: Void
    Output: Void
    Purpose: Central drawing function
    */
    private void draw() {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        //If dev mode is on draw hit boxes and phone stats
        if (developerMode) {
            drawDeveloperInfo();
        }
        batch.end();

        batch.begin();

        player.draw(batch);
        for (Ship enemy : enemies) {
            enemy.draw(batch);
        }

        batch.end();

        //Draw open menu button
        if (!isPaused) {
            menuStage.draw();
        }

        batch.begin();
        //Draw the menu pop up
        bitmapFont.getData().setScale(0.3f);
        if (isPaused || isGameEnded) {
            batch.draw(popUpTexture, 380 / 2f - popUpTexture.getWidth() / 2f, WORLD_HEIGHT / 2 - popUpTexture.getHeight() / 2f);
        }
        //Draw the help menu
        if (helpFlag) {
            batch.draw(popUpTexture, 10, 10, WORLD_WIDTH - 20, WORLD_HEIGHT - 20);
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
        batch.end();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the hit boxes and the phone stats
    */
    private void drawDeveloperInfo() {
        //Batch setting up texture
        int x = (int) Gdx.input.getAccelerometerX();
        int y = (int) Gdx.input.getAccelerometerY();
        int z = (int) Gdx.input.getAccelerometerZ();
//        centerText(bitmapFontDeveloper, "X: " + x, 40, 300);
//        centerText(bitmapFontDeveloper, "Y: " + y, 40, 280);
//        centerText(bitmapFontDeveloper, "Z: " + z, 40, 260);
        centerText(bitmapFontDeveloper, "Player X:" + player.hitbox.getX(), 80, 300);
        centerText(bitmapFontDeveloper, "Player Y:" + player.hitbox.getY(), 80, 280);
        centerText(bitmapFontDeveloper, "Player Max Health:" + player.maxHealth, 80, 260);
        centerText(bitmapFontDeveloper, "Player Health:" + player.health, 80, 240);
        centerText(bitmapFontDeveloper, "Player Max Energy:" + player.maxEnergy, 80, 220);
        centerText(bitmapFontDeveloper, "Player Energy:" + player.energy, 80, 200);
        if (Math.abs(x) > Math.abs(y) && Math.abs(x) > Math.abs(z)) {
            centerText(bitmapFontDeveloper, "Surface X", 40, 240);
        } else if (Math.abs(y) > Math.abs(x) && Math.abs(y) > Math.abs(z)) {
            centerText(bitmapFontDeveloper, "Surface Y", 40, 240);
        } else if (Math.abs(z) > Math.abs(x) && Math.abs(z) > Math.abs(y)) {
            centerText(bitmapFontDeveloper, "Surface Z", 40, 240);
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws text over the menu buttons
    */
    private void drawButtonText() {
        float x;
        float y;
        for (int i = 1; i < NUM_BUTTONS; i++) {
            if (i == 1 || i == 3) {
                x = 380 / 2f - 1.4f * menuButtons[0].getWidth();
            } else {
                x = 380 / 2f + 1.4f * menuButtons[0].getWidth();
            }
            if (i < 3) {
                y = WORLD_HEIGHT / 2f - 15 + menuButtons[0].getHeight();
            } else {
                y = WORLD_HEIGHT / 2f - menuButtons[0].getHeight();
            }
            //If the volume is off draw Sound On else Sound off
            if (i == 4 && sfxVolume == 0) {
                i = NUM_BUTTONS;
            }
            centerText(bitmapFont, menuButtonText[i - 1], x, y);
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the help screen
    */
    private void drawHelpScreen() {
    }


    /*
    Input: BitmapFont for size and font of text, string the text, and x and y for position
    Output: Void
    Purpose: General purpose function that centers the text on the position
    */
    private void centerText(BitmapFont bitmapFont, String string, float x, float y) {
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(bitmapFont, string);
        bitmapFont.draw(batch, string, x - glyphLayout.width / 2, y + glyphLayout.height / 2);
    }

    /*
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

    /*
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
