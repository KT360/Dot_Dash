package Objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import Interfaces.Entity;

//My Wrapper class in which all my game elements fall under
public class GameObject implements Entity{
    public float width;
    public float height;
    public float x;
    public float y;
    public float velX;
    public float velY;
    public boolean idle;
    public long id;
    public String animationKey;
    public TextureRegion sprite;
    public Rectangle hitBox;
    public boolean overlay_element;

    //A basic game object will have an x and y as well has a width and a height
    //The rest of the other values can be set when needed
    public GameObject(float x,float y, float width, float height)
    {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.hitBox = new Rectangle(x,y,width,height);
    }


    @Override
    public void performCollision(GameObject object, Rectangle intersection) {

    }

    @Override
    public void update() {

    }

    @Override
    public Vector2 getPosition() {
        return null;
    }
}
