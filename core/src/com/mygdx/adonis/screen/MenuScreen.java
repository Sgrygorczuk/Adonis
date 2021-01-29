package com.mygdx.adonis.screen;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.adonis.Adonis;
import com.mygdx.adonis.screen.planet.Planet;

import static com.mygdx.adonis.Consts.WORLD_HEIGHT;
import static com.mygdx.adonis.Consts.WORLD_WIDTH;

public class MenuScreen extends ScreenAdapter{
    //Visual objects
    private SpriteBatch batch = new SpriteBatch();			 //Batch that holds all of the textures
    private Viewport viewport;
    private Camera camera;

    //The buttons used to move around the menus
    private Stage menuStage;
    private ImageButton[] menuButtons;

    //Textures
    private Texture popUpTexture;                       //Pop-up screen that the Credits and Help are displayed on
    private Texture backgroundTexture;                  //Main background
    private Texture planetTexture;
    private Texture moonTexture;
    private Texture menuSliderTexture;
    private Texture creditsTexture;

    //String used on the buttons
    private String[] buttonText = new String[]{"Play", "Help", "Credits"};
    //Font used to write in
    private BitmapFont bitmapFont = new BitmapFont();

    //Music player
    private Music music;

    //Game object that keeps track of settings
    private Adonis adonis;
    private Planet planet;

    private final float OFF_SET = 80;

    private boolean helpFlag;      //Tells if help menu is up or not
    private boolean creditsFlag;   //Tells if credits menu is up or not
    boolean letGo = true;

    /*
    Input: SpaceHops
    Output: Void
    Purpose: Grabs the info from main screen that holds asset manager
    */
    MenuScreen(Adonis adonis) { this.adonis = adonis;}

    /*
    Input: Dimensions
    Output: Void
    Purpose: Resize the screen when window size changes
    */
    @Override
    public void resize(int width, int height) { viewport.update(width, height); }

    /*
    Input: Void
    Output: Void
    Purpose: Set up the the textures and objects
    */
    @Override
    public void show() {
        //Sets up the camera
        showCamera();           //Sets up camera through which objects are draw through
        showTextures();         //Sets up the textures
        showButtons();          //Sets up the buttons
        showMusic();            //Sets up the music
        showObjects();          //Sets up the font
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the camera through which all the objects are view through
    */
    private void showCamera(){
        camera = new OrthographicCamera();									//Sets a 2D view
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);	//Places the camera in the center of the view port
        camera.update();													//Updates the camera
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);		//
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets textures that will be drawn
    */
    private void showTextures(){
        //Basic single image textures
        popUpTexture = new Texture(Gdx.files.internal("UI/MenuPanel.png"));
        backgroundTexture = new Texture(Gdx.files.internal("UI/MainMenuBackground.png"));
        planetTexture = new Texture(Gdx.files.internal("UI/Planet.png"));
        moonTexture = new Texture(Gdx.files.internal("UI/Moon.png"));
        menuSliderTexture = new Texture(Gdx.files.internal("UI/MenuSlide.png"));
        creditsTexture = new Texture(Gdx.files.internal("UI/Credits.png"));
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets buttons and their interactions
    */
    private void showButtons(){
        menuStage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(menuStage); //Give power to the menuStage

        //Set up all the buttons used by the stage
        menuButtons = new ImageButton[4];

        setUpMainButtons(); //Places the three main Play|Help|Credits buttons on the screen
        setUpExitButton();  //Palaces the exit button that leaves the Help and Credits menus
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets main three main Play|Help|Credits buttons on the screen
    */
    private void setUpMainButtons(){
        //Get the texures of the buttons
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(620, 141); //Breaks down the texture into tiles

        //Places the three main Play|Help|Credits buttons on the screen
        for(int i = 0; i < 3; i ++){
            menuButtons[i] =  new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
            menuButtons[i].setPosition(20, 2*WORLD_HEIGHT/3 - (21.15f + 10) * i);
            menuButtons[i].setWidth(93);
            menuButtons[i].setHeight(21.15f);
            menuStage.addActor(menuButtons[i]);

            final int finalI = i;
            menuButtons[i].addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    playButtonSFX();
                    //Launches the game
                    if(finalI == 0){
                        music.stop();
                        adonis.setScreen(new MainScreen(adonis));
                    }
                    //Turns on the help menu
                    else if(finalI == 1){
                        for (ImageButton imageButton : menuButtons) { imageButton.setVisible(false); }
                        helpFlag = true;
                        menuButtons[3].setVisible(true);
                        menuButtons[3].setPosition(WORLD_WIDTH/2f - 150/2f, 80);
                    }
                    //Turns on the credits menu
                    else{
                        for (ImageButton imageButton : menuButtons) { imageButton.setVisible(false); }
                        creditsFlag = true;
                        menuButtons[3].setVisible(true);
                        menuButtons[3].setPosition(WORLD_WIDTH/2f - 150/2f, 40);
                    }
                }
            });
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets exit button that leaves the Help and Credits menus
    */
    private void setUpExitButton(){
        //Sets up the texture
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/Button.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(620, 141); //Breaks down the texture into tiles

        //Sets up the position
        menuButtons[3] = new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
        menuButtons[3].setPosition(WORLD_WIDTH/2f - 150/2f, 80);
        menuButtons[3].setWidth(150);
        menuButtons[3].setHeight(40f);
        menuStage.addActor(menuButtons[3]);
        menuButtons[3].setVisible(false);
        //Sets up to turn of the help menu if clicked
        menuButtons[3].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                playButtonSFX();
                helpFlag = false;
                creditsFlag = false;
                //Turn on all buttons but turn off this one
                for (ImageButton imageButton : menuButtons) {
                    imageButton.setVisible(true);
                }
                menuButtons[3].setVisible(false);
            }
        });
    }

    /**
    Input: Void
    Output: Void
    Purpose: Sets up the music that will play when screen is started
    */
    private void showMusic(){
        music = adonis.getAssetManager().get("Music/MainMenuMusic.mp3", Music.class);
        music.setLooping(true);
        music.setVolume(0.25f);
        music.play();
    }

    /**
    Input: Void
    Output: Void
    Purpose: SFX will be played any time a button is clicked
    */
    private void playButtonSFX() { adonis.getAssetManager().get("SFX/Pop.wav", Sound.class).play(1/2f); }

    /**
    Input: Void
    Output: Void
    Purpose: Sets up the font
    */
    private void showObjects(){
        if(adonis.getAssetManager().isLoaded("Fonts/Font.fnt")){bitmapFont = adonis.getAssetManager().get("Fonts/Font.fnt");}
        bitmapFont.getData().setScale(0.6f);
        bitmapFont.setColor(Color.BLACK);

        planet = new Planet(350, 100, 70, planetTexture);
        planet.createMoon(20, 80, false, moonTexture);
    }


    /*
    Input: Delta, timing
    Output: Void
    Purpose: What gets drawn
    */
    @Override
    public void render(float delta) {
        update();       //Update the variables
        draw();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the leaves and gobo's color
    */
    private void update() {
        menuStage.getViewport().update(viewport.getScreenWidth(), viewport.getScreenHeight(), true);
        planet.update();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the progress
    */
    private void draw() {
        clearScreen();
        //Viewport/Camera projection
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        //Batch setting up texture before drawing buttons
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(menuSliderTexture, 0, 0, 135, WORLD_HEIGHT);
        planet.draw(batch);
        //Draw the pop up menu
        if(helpFlag){
            batch.draw(popUpTexture, WORLD_WIDTH/2f - 200/2f, WORLD_HEIGHT/2 - 300/2f, 200, 300);
            drawInstructions();
        }
        if(creditsFlag){ batch.draw(creditsTexture, 10, 10, WORLD_WIDTH - 20, WORLD_HEIGHT - 20); }
        batch.end();

        menuStage.draw(); // Draws the buttons

        batch.begin();
        //Draws the Play|Help|Credits text on buttons
        if(!helpFlag && !creditsFlag){drawButtonText();}
        else {
            bitmapFont.getData().setScale(.4f);
            if(creditsFlag){
                centerText(bitmapFont, "Back",  WORLD_WIDTH/2f,  63);
                drawCredits();
            }
            else{
                centerText(bitmapFont, "Back",  WORLD_WIDTH/2f, 103);
            }
        }

        batch.end();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets screen color
    */
    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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

    /*
    Input: Void
    Output: Void
    Purpose: Draws the text on the Play|Help|Credits buttons
    */
    private void drawButtonText(){
        bitmapFont.getData().setScale(0.4f);
        bitmapFont.setColor(Color.BLACK);
        for(int i = 0; i < 3; i ++) {
            centerText(bitmapFont, buttonText[i], 20 + 46.5f,
                    2 * WORLD_HEIGHT/3 + 15 - (31) * i);
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the credits screen
    */
    private void drawCredits(){
        float start = 70;
        //Title
        bitmapFont.getData().setScale(0.5f);
        centerText(bitmapFont, "Credits", WORLD_WIDTH/2f, WORLD_HEIGHT-50);
        bitmapFont.getData().setScale(0.25f);

        centerText(bitmapFont, "Programming", WORLD_WIDTH/2f, WORLD_HEIGHT - start);
        centerText(bitmapFont, "Sebastian Grygorczuk", WORLD_WIDTH/2f, WORLD_HEIGHT - (start + 10));
        centerText(bitmapFont, "Nelson Batista", WORLD_WIDTH/4f + 10, WORLD_HEIGHT - (start + 10));
        centerText(bitmapFont, "Paul Tan", 3*WORLD_WIDTH/4f - 10, WORLD_HEIGHT - (start + 10));


        centerText(bitmapFont, "Art", WORLD_WIDTH/2f, WORLD_HEIGHT - (start + 25));
        centerText(bitmapFont, addNewLine("All art was bought from Craftpix.net, the specific packets used are: " +
                        "Space Game Background, Space Vertical 2D Background, Spaceship 2D Sprites, and Sci-Fi UI.", 70), WORLD_WIDTH/2f, WORLD_HEIGHT - (start + 45));


        centerText(bitmapFont, "SFX and Music", WORLD_WIDTH/2f, WORLD_HEIGHT - (start + 65));
        centerText(bitmapFont, addNewLine("All SFX and Music were gather from Freesound.com here are the users and the name of their piece.", 70), WORLD_WIDTH/2f, WORLD_HEIGHT - (start + 80));
        bitmapFont.getData().setScale(0.20f);

        centerText(bitmapFont, "DrMrSir - PowerDown Faster.wav", WORLD_WIDTH/2f - OFF_SET, WORLD_HEIGHT - (start + 100));
        centerText(bitmapFont, "josepharaoh99 - Game Powerup", WORLD_WIDTH/2f- OFF_SET, WORLD_HEIGHT - (start + 107));
        centerText(bitmapFont, "ChristianAnd - 25_Metal_chocando.wav", WORLD_WIDTH/2f- OFF_SET, WORLD_HEIGHT - (start + 114));

        centerText(bitmapFont, "michael_grinnel - Laser shot.wav", WORLD_WIDTH/2f - OFF_SET, WORLD_HEIGHT - (start + 121));
        centerText(bitmapFont, "MATRIXXX_ - Retro_Laser_Shot_04.wav", WORLD_WIDTH/2f- OFF_SET, WORLD_HEIGHT - (start + 128));
        centerText(bitmapFont, "NightWolfCFM - Minotaur (Chase Music)", WORLD_WIDTH/2f- OFF_SET, WORLD_HEIGHT - (start + 135));

        centerText(bitmapFont, "Xanco123 - Dark Ambient Music 3: Hunted", WORLD_WIDTH/2f + OFF_SET, WORLD_HEIGHT - (start + 100));
        centerText(bitmapFont, "GameAudio - Button Spacey Confirm", WORLD_WIDTH/2f + OFF_SET, WORLD_HEIGHT - (start + 107));
        centerText(bitmapFont, "GameAudio - Button Deny Spacey", WORLD_WIDTH/2f + OFF_SET, WORLD_HEIGHT - (start + 114));
        centerText(bitmapFont, "JapanYoshiTheGamer - 8-bit Cymbal + Kick or Impact", WORLD_WIDTH/2f + OFF_SET, WORLD_HEIGHT - (start + 121));
        centerText(bitmapFont, "kwahmah_02 - Pop!.wav", WORLD_WIDTH/2f + OFF_SET, WORLD_HEIGHT - (start + 128));
    }


    /**
    Input: BitmapFont for size and font of text, string the text, and x and y for position
    Output: Void
    Purpose: General purpose function that centers the text on the position
    */
    private void centerText(BitmapFont bitmapFont, String string, float x, float y){
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(bitmapFont, string);
        bitmapFont.draw(batch, string,  x - glyphLayout.width/2, y + glyphLayout.height/2);
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


    /*
    Input: Void
    Output: Void
    Purpose: Gets rid of all visuals
    */
    @Override
    public void dispose() {
        menuStage.dispose();
        music.dispose();

        popUpTexture.dispose();
    }


}