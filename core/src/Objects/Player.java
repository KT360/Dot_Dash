package Objects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Player extends GameObject{

    public float originalX;
    public float originalY;

    public Player(float x, float y, float width, float height, TextureRegion sprite) {
        super(x, y, width, height);
        originalX = x;
        originalY = y;
        this.idle = true;
        this.id = 0B1111;
        this.sprite = sprite;
        this.animationKey = "player";
    }

    @Override
    public void performCollision(GameObject object, Rectangle intersection) {

    }

    @Override
    public void update() {

    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(x,y);
    }
}
