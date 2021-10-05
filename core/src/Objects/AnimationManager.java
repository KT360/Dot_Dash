package Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.lang.reflect.GenericArrayType;
import java.util.HashMap;

public class AnimationManager {
    private BitmapFont show_score;
    private BitmapFont show_milestone;
    private float stateTime;
    private Batch batch;
    private Viewport viewport;
    private Timer scoreTimer;
    private Timer.Task incrementScore;
    private HashMap<String, Animation<TextureRegion>> animations;
    private Texture pauseOverlay;
    private Array<GameObject> overlayItems;
    private TextureRegion test;

    //Background
    Texture [] backgrounds;
    float [] speeds;
    float[] offsets;

    public AnimationManager(Batch batch , Viewport viewport, Array<GameObject> gameObjects)
    {
        //Each object will be assigned an animation key that maps to a certain animation
        animations = new HashMap<>();

        Texture pressButton = new Texture(Gdx.files.internal("Animations/Overlay/press_button.png"));
        Animation<TextureRegion> press_button_animation = createAnimation(pressButton,2,2,1/8f);
        animations.put("press_button",press_button_animation);

        Texture milestone_frames = new Texture(Gdx.files.internal("Animations/Overlay/milestone_reached.png"));
        Animation<TextureRegion> milestone_reached = createAnimation(milestone_frames,3,3,1/40f);
        animations.put("milestone_reached",milestone_reached);

        Texture effect = new Texture(Gdx.files.internal("Animations/Overlay/speed_effect.png"));
        Animation<TextureRegion> speed_effect = createAnimation(effect,3,4,1/20f);
        animations.put("speed_effect",speed_effect);

        //Background textures
        backgrounds = new Texture[2];
        speeds = new float[2];
        offsets = new float[2];
        backgrounds[0] = new Texture(Gdx.files.internal("Background/background_far.png"));
        backgrounds[1] = new Texture(Gdx.files.internal("Background/background_near.png"));
        speeds[0] = viewport.getCamera().viewportWidth/10;
        speeds[1] = viewport.getCamera().viewportWidth/5;
        offsets[0] = 0;
        offsets[1] = 0;

        this.batch = batch;
        this.viewport = viewport;

        //Initialize score variable
        //Assign a timer that will increment it at a rate of less than half a second
        show_score = new BitmapFont();
        show_score.getData().setScale(2);
        show_milestone = new BitmapFont();
        show_milestone.getData().setScale(2);
        scoreTimer = new Timer();
        incrementScore = new Timer.Task() {
            @Override
            public void run() {
                GameVariables.score ++;
            }
        };
        scoreTimer.scheduleTask(incrementScore,0.2f,0.2f);

        stateTime = 0f;

        //Setup transparent overlay that gets rendered once the game is paused
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(0,0,0,0.5f);
        pix.fill();
        pauseOverlay = new Texture(pix);

        overlayItems = new Array<>();
        //Assemble each one of my animated overlay items
        for (GameObject object : gameObjects)
        {
            if (object.overlay_element && object.animationKey != null)
            {
                overlayItems.add(object);
            }
        }
    }

    public void renderObjects(Array<GameObject> gameObjects,float delta)
    {
        Array<GameObject> canRender = new Array<>();

        //Check whether the game object is to be rendered
        for (GameObject object : gameObjects)
        {
            long key = object.id & 0B1001;
            if (key == Constants.ANIMATION_COMPONENT)
            {
                canRender.add(object);
            }
        }

        //Initialize renderers as well as state time for the animations
        Camera camera = viewport.getCamera();
        camera.update();
        stateTime += Gdx.graphics.getDeltaTime();
        Batch batch = this.batch;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderBackground(batch,camera,delta);
        for (GameObject animationElement : canRender)
        {
            float x = animationElement.x;
            float y = animationElement.y;
            float width = animationElement.width;
            float height = animationElement.height;

            if (animationElement.idle)//If idle, draw object sprite,
            {
                batch.draw(animationElement.sprite, x, y, width, height);

            }else if (!animationElement.overlay_element) //else render their animation if they are not an overlay item
                {

                TextureRegion currentFrame = animations.get(animationElement.animationKey).getKeyFrame(stateTime,true);
                batch.draw(currentFrame,x,y,width,height);
            }
        }

        //Draw score (Top/Middle of screen)
        //Check if player has reached check point
        //Increment checkpoint
        int score = GameVariables.score;
        int currentMilestone = GameVariables.currentMilestone;
        if (score == currentMilestone)
        {
            GameVariables.currentMilestone*= 2;
            currentMilestone = GameVariables.currentMilestone;
            GameVariables.milestoneReached = true;
        }
        show_score.draw(batch,String.valueOf(score),camera.viewportWidth/2,camera.viewportHeight-50);
        show_milestone.draw(batch,"/"+currentMilestone,camera.viewportWidth/2+50,camera.viewportHeight-50);
        //Once the game is paused stop incrementing the score
        if (GameVariables.gameIsPaused)
        {
            scoreTimer.stop();
            batch.draw(pauseOverlay,0,0,camera.viewportWidth,camera.viewportHeight);//Draw a dark transparent background over

             //Render my overlay items if the have an animation available                                                                               //The current scene
            for (GameObject item : overlayItems)
            {
                if (item.animationKey != null)
                {
                    TextureRegion currentFrame = animations.get(item.animationKey).getKeyFrame(stateTime,true);
                    batch.draw(currentFrame,item.x,item.y,item.width,item.height);
                }
            }

        }else { //If not paused, keep incrementing the score
            scoreTimer.start();
        }

        batch.end();
    }

    //For a parallax effect its the same thing, but with different speeds
    //Speed depends on a division by the world width
    // -> speed =  viewportWidth/secNumb
    //-> offset+= deltatime * speed;
    public void renderBackground(Batch batch, Camera camera, float delta)
    {
        for (int i =0; i<backgrounds.length; i++)
        {
            batch.draw(backgrounds[i],-offsets[i],0,camera.viewportWidth,camera.viewportHeight);
            batch.draw(backgrounds[i],-offsets[i]+camera.viewportWidth,0,camera.viewportWidth,camera.viewportHeight);

            offsets[i] += delta * speeds[i];

            if (offsets[i] > camera.viewportWidth)
            {
                offsets[i] = 0;
            }
        }
    }

    //Helper method to create animations
    public Animation<TextureRegion> createAnimation(Texture spriteSheet, int cols, int rows, float frameRate)
    {
        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
                spriteSheet.getWidth() / cols,
                spriteSheet.getHeight() / rows);


        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] walkFrames = new TextureRegion[cols * rows];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        return new Animation<TextureRegion>(frameRate, walkFrames);
    }

    public void removeOverlayElement(GameObject element)
    {
        for (GameObject object: overlayItems)
        {
            if (object == element)
            {
                overlayItems.removeValue(object,true);
                System.out.println("Working");
            }

        }
    }

    public Viewport getViewport()
    {
        return viewport;
    }
    public Camera getCamera(){return viewport.getCamera();}
}
