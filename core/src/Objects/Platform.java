package Objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.util.Random;

public class Platform extends GameObject implements Pool.Poolable {

    public float originalX;
    public float originalY;

    public Platform(float x, float y, float width, float height, TextureRegion sprite) {
        super(x, y, width, height);
        originalX = x;
        originalY = y;
        this.id = 0B1101;
        this.sprite = sprite;
        this.idle = true;
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

    @Override
    public void reset() {
        Random randomPos = new Random();
        this.x = (randomPos.nextFloat()*500)+GameVariables.maxX;
        this.y = (randomPos.nextFloat()*100)+GameVariables.maxY/2-35;
        this.velX = GameVariables.platform_velocity;
    }
}
