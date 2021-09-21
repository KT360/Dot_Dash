package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import Objects.GuiElement;

public class MenuScreen implements Screen {

    private Stage stage;
    private GuiElement MenuOptions;
    private Table table;
    private Skin skin;
    private TextureAtlas atlas;
    private Sound startGame;
    private Sound exitGame;
    private Timer exitTimer;
    private Timer.Task closeGame;


    public MenuScreen(final SpriteBatch batch, final ExtendViewport viewport, final Game game)
    {

        atlas = new TextureAtlas("Menu/Menu.txt");
        skin = new Skin(atlas);

        float menuOptionsWidth = 1280;
        float menuOptionsHeight = 720;

        //Initialize menu otions
        //Set style for buttons as well as width and height
        MenuOptions = new GuiElement(2,menuOptionsWidth,menuOptionsHeight);
        TextureRegion startUp = skin.getRegion("StartButton");
        TextureRegion startDown = skin.getRegion("StartButton_Clicked");
        TextureRegion exitUp = skin.getRegion("ExitButton");
        TextureRegion exitDown = skin.getRegion("ExitButton_Clicked");
        MenuOptions.setButtonStyle(0,startUp,startDown);
        MenuOptions.setButtonStyle(1,exitUp,exitDown);
        MenuOptions.getField(0).width(500).height(200);
        MenuOptions.getField(1).width(500).height(200).padTop(20);

        //Initialize sounds
        startGame = Gdx.audio.newSound(Gdx.files.internal("Menu/Sound/start.mp3"));
        exitGame = Gdx.audio.newSound(Gdx.files.internal("Menu/Sound/exit.mp3"));

        //Setup the game to exit
        exitTimer = new Timer();
        closeGame = new Timer.Task() {
            @Override
            public void run() {
                game.dispose();
            }
        };

        //Add changeListeners to the two buttons
        //Either change the game screen or exit the game
        //Play corresponding sound effect
        MenuOptions.getField(0).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startGame.play();
                game.setScreen(new GameScreen(batch, viewport,game));
                dispose();
            }
        });

        MenuOptions.getField(1).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                exitGame.play();
                exitTimer.scheduleTask(closeGame,1);
            }
        });

        //Add to the main table
        table = new Table();
        table.setFillParent(true);
        table.add(MenuOptions).width(menuOptionsWidth).height(menuOptionsHeight);

        table.setDebug(true);
        table.debugTable();

        //Add the table to the stage
        stage = new Stage(viewport,batch);
        Gdx.input.setInputProcessor(stage);
        stage.addActor(table);

    }

    @Override
    public void show() {

        render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height,true);

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
        stage.dispose();
        atlas.dispose();
        skin.dispose();
        startGame.dispose();
        exitGame.dispose();
    }
}
