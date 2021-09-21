package Interfaces;


import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import Objects.GameObject;

//Makes it easier to invoke methods later on in the code
public interface Entity {
    void performCollision(GameObject object, Rectangle intersection);
    void update();
    Vector2 getPosition();
}
