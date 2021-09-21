package Objects;

public class Constants {
    //ID's that will be give to each game object in order to describe what component it contains
    //1000 = Game object has a position/ width and a height i.e a rectangle
    //0100 = Game object has a velocity
    //0010 = Game object can receive input
    //0001 = Game object has an animation/sprite
    public static final long ANIMATION_COMPONENT = 0B1001;
    public static final long COLLISION_COMPONENT = 0B1000;
    public static final long PHYSICS_COMPONENT = 0B1100;
}
