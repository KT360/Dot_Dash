package Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;

public class CollisionManager {


    public CollisionManager()
    {

    }

    public void checkCollision(Array<GameObject> entities ,Pool<Platform> platformPool,Player player, float maxX , float maxY)
    {
        if (player.y > player.originalY + 100)
        {
         GameVariables.movementPointer = 0;
        }

        //Cases:
        //Every time space is pressed, the player activates a "Movement pointer"
        //0 = Player is falling
        //1 = Player is touching a platform
        //2 = Player is jumping
        //3 = Player is dashing
        switch (GameVariables.movementPointer)
        {
            case 0:
                player.velY = -10;
                player.velX = 0;
                player.x += player.velX;
                player.y += player.velY;
                player.hitBox.setPosition(player.x,player.y);
                break;
            case 1:
                if (player.velX < 0 && player.velY == 0) //If the player's velocity is in the same direction
                                                         //As a platform and it is not falling down then
                    //that means player is on platform and velocity should be set the same
                {
                    player.velX = GameVariables.platform_velocity;
                }
                player.x+= player.velX;
                player.y+= player.velY;
                player.hitBox.setPosition(player.x,player.y);
                break;
            case 2:
                player.velY = 10;
                player.velX = 0;
                player.x += player.velX;
                player.y += player.velY;
                player.hitBox.setPosition(player.x,player.y);
                break;
            case 3:
                player.velX = 10;
                player.velY = 0;
                player.x += player.velX;
                player.y += player.velY;
                player.hitBox.setPosition(player.x,player.y);
                break;
        }

        //Update my platforms
        Array<Platform> platforms = new Array<>();

        for (GameObject object : entities)
        {
            if (object instanceof Platform)
            {
                platforms.add((Platform) object);
            }
        }

        for (Platform p : platforms)
        {
            p.velX  = GameVariables.platform_velocity;
            p.x += p.velX;
            p.y += p.velY;
            p.hitBox.setPosition(p.x,p.y);
        }

        //Check for player out of bounds
        Array<GameObject> platformsToRemove = new Array<>();
        if (player.x < 0 - player.width)
        {
            GameVariables.playerHasDied = true;
        }else if (player.y < 0 - player.height)
        {
            GameVariables.playerHasDied = true;
        }else if (player.x > maxX)
        {
            GameVariables.playerHasDied = true;
        }else if (player.y > maxY)
        {
            GameVariables.playerHasDied = true;
        }

        //Check for any collision between player and platforms
        //Adjust position, velocity and movement pointer
       for (Platform p : platforms)
       {
           if (player.hitBox.overlaps(p.hitBox))
           {
               Rectangle intersection = new Rectangle();
               Intersector.intersectRectangles(p.hitBox,player.hitBox,intersection);

               //Player at the top
               if (player.y > p.y)
               {
                   GameVariables.movementPointer = 1;
                   player.velY = 0;
                   player.velX = GameVariables.platform_velocity;
                   player.y += intersection.height;
                   player.hitBox.setPosition(player.x,player.y);
                   player.originalX = player.x;
                   player.originalY = player.y;
               }
               //Player to the left
               else if (player.x < p.x)
               {
                   GameVariables.movementPointer  = 1;
                   player.x -= intersection.width;
                   player.velX = GameVariables.platform_velocity;
               }
               //Player at the bottom
               else if (player.y < p.y)
               {
                   GameVariables.movementPointer = 1;
                   player.velY = -10;
                   player.y -= intersection.height;
               }
               //Player to the right
               else if (player.x > p.x)
               {
                   GameVariables.movementPointer = 1;
                   player.velX = 0;
                   player.x += intersection.width;
               }
           }

           if (p.x < 0 - p.width) //If the platform is not visible anymore remove it from the world and add it to the pool
           {
                platformsToRemove.add(p);
           }
       }

       for (GameObject platform : platformsToRemove)
       {
           entities.removeValue(platform, true);
           platformPool.free((Platform) platform);
       }

    }

}
