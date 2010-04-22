package environment;

/**
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * The base object in the World. The different types aren't usually subclassed, but set by the Type
 *  enum used in the constructor.
 */
public class Entity {

    // Enum to define the attributes of the entity.
    public enum Type {
        VIEWER(100.0, 30, 0.1),
        NPC(20.0, 10, 0.2),
        STATIC(0, 0, 0);

        public final double MAX_VEL;
        public final double ACCELERATION;
        public final double DIR_CHANGE_SPEED;

        Type(double maximumVelocity, double acceleration, double directionChangeSpeed) {
            MAX_VEL = maximumVelocity;
            ACCELERATION = acceleration;
            DIR_CHANGE_SPEED = directionChangeSpeed;
        }
    }

    public final double MAX_VEL;
    public final double ACCELERATION;
    public final double DIR_CHANGE_SPEED;
    public int x, y, newX, newY;
    public double direction;
    public double velocity;
    public double acceleration;
    public double directionChange;
    public int strafe; // Negative left, Positive right
    public boolean npc;
    public int height;

    /**
     * Creates an Entity of the given type.
     * @param attributes Type enum.
     */
    public Entity(Type attributes) {
        MAX_VEL = attributes.MAX_VEL;
        ACCELERATION = attributes.ACCELERATION;
        DIR_CHANGE_SPEED = attributes.DIR_CHANGE_SPEED;
        x = 0;
        y = 0;
        newX = 0;
        newY = 0;
        direction = 0;
        velocity = 0;
        acceleration = 0;
        directionChange = 0;
        strafe = 0;
        npc = true;
        height = World.GRID_SIZE/2; // Half as high as walls;
    }
}
