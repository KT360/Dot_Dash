package Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.lang.reflect.GenericArrayType;
import java.util.Random;

import Objects.AnimationManager;
import Objects.CollisionManager;
import Objects.GameObject;
import Objects.GameVariables;
import Objects.GuiElement;
import Objects.Platform;
import Objects.Player;

public class GameScreen extends InputAdapter implements Screen {

    private AnimationManager renderer;
    private CollisionManager engine;
    private Array<GameObject> entities;
    private TextureAtlas sprites;
    private ShapeRenderer debug;
    private Pool<Platform> platformPool;
    private float maxX;
    private float maxY;
    private float platformSpawnTime = 2.5f;
    private Music gameMusic;
    private float platform_vel_increase = 2;
    private int buttonPressed = 0;
    Player player;
    Platform startingPlatform;
    GameObject press_button;
    GameObject milestone_reached;
    GameObject speed_effect;

    //Timers
    private Timer platformTimer;
    private Timer.Task spawnPlatform;
    private Timer tutorialTimer;
    private Timer.Task show_tutorial;
    private Timer checkPointTimer;
    private Timer.Task removeCheckPointAnimation;

    //UI
    private Stage stage;
    private Table gameOptions;
    private Skin skin;
    private TextureAtlas menu_atlas;
    private Table table;
    private GuiElement game_option;
    private Button game_options_button;
    private Rectangle game_opt_button_rect;
    private boolean game_options_on = false;

    //TODO: decide input processing device
    public GameScreen(final SpriteBatch batch, final ExtendViewport viewport, final Game game)
    {
        //Initialize textures
        sprites = new TextureAtlas("GameObjects/game_objects.txt");
        TextureRegion playerSprite = sprites.findRegion("player");
        TextureRegion sprite1 = sprites.findRegion("platforms-2");

        GameVariables.maxX = viewport.getCamera().viewportWidth;
        GameVariables.maxY = viewport.getCamera().viewportHeight;
        maxX = GameVariables.maxX;
        maxY = GameVariables.maxY;

        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("In_Game_Sounds/Puzzle-Dreams.mp3"));

        //Initialize start of game
        player = new Player(400,maxY/2,30,30,playerSprite);
        player.velX = GameVariables.platform_velocity;
        startingPlatform = new Platform(350,maxY/2-35,200,35, sprite1);
        startingPlatform.velX = GameVariables.platform_velocity;
        startingPlatform.originalX = maxX;

        platformPool = new Pool<Platform>() {
            @Override
            protected Platform newObject() {

                Random randomPos = new Random();
                Random randomWidth = new Random();
                Random randomSprite = new Random();
                TextureRegion platformSprite = sprites.findRegion("platforms-"+(randomSprite.nextInt(3)+1));

                //Platforms will be given a random position starting from the right of the screen
                Platform p = new Platform((randomPos.nextFloat()*200)+maxX, (randomPos.nextFloat()*100)+maxY/2-35, randomWidth.nextFloat()*200, 35, platformSprite);
                p.velX = GameVariables.platform_velocity;

                return p;
            }
        };
        //Set up timers
        platformTimer = new Timer();
        spawnPlatform = new Timer.Task() {
            @Override
            public void run() {
                Platform p = platformPool.obtain();
                entities.add(p);
            }
        };
        platformTimer.scheduleTask(spawnPlatform,platformSpawnTime,platformSpawnTime);

        tutorialTimer = new Timer();
        show_tutorial = new Timer.Task() {
            @Override
            public void run() {
                entities.add(press_button);
            }
        };

        checkPointTimer = new Timer();
        removeCheckPointAnimation = new Timer.Task() {
            @Override
            public void run() {
                entities.removeValue(milestone_reached,true);
                entities.removeValue(speed_effect,true);
            }
        };

        //Setup UI elements
        press_button = new GameObject(viewport.getCamera().viewportWidth/2-50,viewport.getCamera().viewportHeight/2-50,200,200);
        press_button.animationKey = "press_button";
        press_button.idle = false;
        press_button.overlay_element = true;

        milestone_reached = new GameObject(viewport.getCamera().viewportWidth/2-100,viewport.getCamera().viewportHeight/2-25,200,200);
        milestone_reached.animationKey = "milestone_reached";
        milestone_reached.idle = false;
        milestone_reached.overlay_element = false;
        milestone_reached.id = 0B1001;

        speed_effect = new GameObject(0,0,viewport.getCamera().viewportWidth,viewport.getCamera().viewportHeight);
        speed_effect.animationKey = "speed_effect";
        speed_effect.idle = false;
        speed_effect.overlay_element = false;
        speed_effect.id = 0B1001;

        //Add objects to "Game world"
        entities = new Array<>();
        entities.add(player);
        entities.add(startingPlatform);
        if (GameVariables.can_show_tutorial)
        {
            entities.add(press_button);
        }

        //Initialize stage, (Will be used for input)
        stage = new Stage(viewport,batch)
        {
            @Override
            public boolean keyDown(int keycode) {
                //Once key pressed and player is touching a plat form (1), activate other movement pointer
                if (keycode == Input.Keys.SPACE)
                {
                    if (GameVariables.movementPointer >= 1 )
                    {
                        GameVariables.movementPointer++;
                    }
                }

                //Switch to pause the game
                if (keycode == Input.Keys.P)
                {
                    GameVariables.gameIsPaused = !GameVariables.gameIsPaused;
                }

                return super.keyDown(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.SPACE)
                {
                    if (GameVariables.movementPointer >= 3) //Make player fall after dash (3)
                    {
                        GameVariables.movementPointer = 0;
                    }

                }
                return super.keyUp(keycode);
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                buttonPressed++;

                Vector3 worldCoords = renderer.getCamera().unproject(new Vector3(screenX,screenY,0));//Convert screen coordinates to world coordinates
                if (!game_opt_button_rect.contains(worldCoords.x,worldCoords.y))                        //Check if we are hitting the button before performing any action
                {
                    if (GameVariables.movementPointer >= 1 )
                    {
                        GameVariables.movementPointer++;
                    }
                }

                return super.touchDown(screenX,screenY,pointer,button);
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (GameVariables.movementPointer >= 3)
                {
                    GameVariables.movementPointer = 0;
                }

                return super.touchUp(screenX,screenY,pointer,button);
            }
        };

        menu_atlas = new TextureAtlas(Gdx.files.internal("Menu/Menu.txt"));
        skin = new Skin(menu_atlas);

        //Menu table
        gameOptions = new Table();
        gameOptions.setDebug(true);
        gameOptions.setBackground(skin.getDrawable("game_options"));
        gameOptions.setPosition(viewport.getCamera().viewportWidth/4, viewport.getCamera().viewportHeight /4);//Set position to center of screen

        //button to return to home screen at the center of the menu table
        TextureRegion up = skin.getRegion("back_to_menu");

        game_option = new GuiElement(1);
        game_option.getField(0).width(150).height(150);
        game_option.setButtonStyle(0,up,up);
        game_option.getField(0).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MenuScreen(batch,viewport,game));
            }
        });

        gameOptions.add(game_option); // add it

        //toggle button at the top right
        game_options_button = new Button(skin.getDrawable("OptionsButton"));
        game_options_button.setWidth(100);
        game_options_button.setHeight(100);
        game_options_button.setPosition(viewport.getCamera().viewportWidth - 100,viewport.getCamera().viewportHeight - 100);
        game_opt_button_rect = new Rectangle(game_options_button.getX(),game_options_button.getY(),100,100);

        stage.addActor(game_options_button);//add it to the stage

        //Add a change listener to the button at the top right
        //Check for my boolean, add the menu table(game is paused) to the stage or remove it if its already there (game is unpaused)
        stage.getActors().get(0).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game_options_on = !game_options_on;

                if (game_options_on)
                {
                    stage.getActors().add(gameOptions);
                    GameVariables.gameIsPaused = true;
                }

                if (!game_options_on)
                {
                    if (stage.getActors().size >= 2)
                    {
                        stage.getActors().removeIndex(1);
                        GameVariables.gameIsPaused = false;
                        gameMusic.play();
                    }
                }
            }
        });

        //For debugging hit-boxes
        debug = new ShapeRenderer();

        //Initialize Rendering, collision, and physics systems
        renderer = new AnimationManager(batch,viewport,entities);
        engine = new CollisionManager();
        Gdx.input.setInputProcessor(stage);

        GameVariables.gameIsPaused = false;
        GameVariables.platform_velocity = -2;
        gameMusic.play();
        gameMusic.setLooping(true);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Apply physics -> Resolve Collisions -> Render

        if (GameVariables.playerHasDied)
        {
            resetGame();
        }


        //If checkpoint has been passed, add "milestone" so that it can be animated
        //Then remove it from world after 2s
        if (GameVariables.milestoneReached)
        {
            entities.add(milestone_reached);
            entities.add(speed_effect);
            checkPointTimer.scheduleTask(removeCheckPointAnimation,2);
            //Decrease platform spawn time increase their velocity
            platformTimer.clear();
            if(platformSpawnTime > 1)
            {
                platformSpawnTime -= 0.85f;
            }
            platformTimer.scheduleTask(spawnPlatform,platformSpawnTime,platformSpawnTime);
            GameVariables.platform_velocity -= platform_vel_increase;
            GameVariables.milestoneReached = false;
        }

        for (GameObject object: entities)
        {
            //If true that means the tutorial is playing, pause the game
            if (object.equals(press_button)) {
                GameVariables.gameIsPaused = true;
                break;
            }
        }

        if (!GameVariables.gameIsPaused)
        {
            engine.checkCollision(entities,platformPool,player,maxX,maxY);
        }else
        {
            gameMusic.pause();
        }
        renderer.renderObjects(entities,delta);
//        debug.setProjectionMatrix(renderer.getCamera().combined);
//        debug.setAutoShapeType(true);
//        debug.begin();
//        for (GameObject o : entities)
//        {
//            debug.rect(o.hitBox.x,o.hitBox.y,o.hitBox.width,o.hitBox.height);
//        }
//        debug.end();

        //Stage
        stage.act(delta);
        stage.draw();

    }

    public void resetGame()
    {
     GameVariables.platform_velocity = -1;
     player.x = 400;
     player.y = maxY/2;
     player.velX = GameVariables.platform_velocity;
     for (GameObject entity : entities)
     {
         if (entity instanceof Platform)
         {
             entities.removeValue(entity,true);
         }
     }
     Platform starter = platformPool.obtain();
     starter.x = 350;
     starter.y = maxY/2-35;
     starter.velX = GameVariables.platform_velocity;
     entities.add(starter);
     platformTimer.clear();
     platformSpawnTime = 2.5f;
     platformTimer.scheduleTask(spawnPlatform,platformSpawnTime,platformSpawnTime);
     GameVariables.playerHasDied = false;
     GameVariables.score = 0;
     GameVariables.currentMilestone = 50;
    }

    @Override
    public void resize(int width, int height) {

        renderer.getViewport().update(width, height,true);
        stage.getViewport().update(width,height,true);
        gameOptions.setWidth(renderer.getViewport().getCamera().viewportWidth/2);
        gameOptions.setHeight(renderer.getViewport().getCamera().viewportHeight/2);
        GameVariables.maxX = renderer.getViewport().getCamera().viewportWidth;
        GameVariables.maxY = renderer.getViewport().getCamera().viewportHeight;
        maxX = GameVariables.maxX;
        maxY = GameVariables.maxY;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
