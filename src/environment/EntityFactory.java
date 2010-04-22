package environment;

import controllers.PhysicsController;
import exceptions.ViewerNotFoundException;
import renderer.MapView;

import java.util.ArrayList;

/**
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * A factory for the entities in a World. Also works with a physicsController.
 */
public class EntityFactory {
    private final World world;
    private final PhysicsController physicsController;
    private renderer.MapView mapView;
    private ArrayList<Entity> entityList;
    private int viewerIndex;

    /**
     * Constructs new EntityFactory and assigns a World and PhysicsController to it.
     *
     * @param world the World with entities.
     * @param physicsController the controller to assign the entities to.
     */
    public EntityFactory(World world, PhysicsController physicsController) {
        this.world = world;
        this.physicsController = physicsController;
        entityList = new ArrayList<Entity>();
        mapView = null;
    }

    /**
     * Constructs new EntityFactory and assigns a World, PhysicsController and MapView to it.
     *
     * @param world the World with entities.
     * @param physicsController the controller to assign the entities to.
     * @param mapView the overhead map view.
     */
    public EntityFactory(World world, PhysicsController physicsController, MapView mapView) {
        this(world, physicsController);
        this.mapView = mapView;
    }

    /**
     * Static function to create an Entity at given grid position of the type assigned by
     * the char in the level file.
     *
     * @param x grid position x-coordinate.
     * @param y grid position y-coordinate.
     * @param c tells the Entity's Type.
     * @return the created entity.
     */
    public static Entity createEntity(int x, int y, char c) {
        Entity.Type type;
        switch (c) {
            case('v'): type = Entity.Type.VIEWER; break;
            case('n'): type = Entity.Type.NPC; break;
            case('s'): type = Entity.Type.STATIC; break;
            default: type = Entity.Type.STATIC; break;
        }
        Entity entity = new Entity(type);
        // Place entity at middle of square.
        entity.x = x * World.GRID_SIZE + World.GRID_SIZE / 2;
        entity.newX = entity.x;
        entity.y = y * World.GRID_SIZE + World.GRID_SIZE / 2;
        entity.newY = entity.y;
        return entity;
    }

    /**
     * Returns the viewer Entity in the World. Fills the entityList as a side-effect.
     * @return the found viewer.
     * @throws ViewerNotFoundException
     */
    public Entity getViewer() throws ViewerNotFoundException {
        if (entityList.isEmpty()) {
            fill();
        }
        try {
            return entityList.get(viewerIndex);
        } catch(IndexOutOfBoundsException e) {
            throw new ViewerNotFoundException();
        }
    }


    /**
     * Parses the world and creates entities according to the symbols.
     *
     * @return a list containing the created entities.
     */
    public ArrayList<Entity> fill() {
        entityList = new ArrayList<Entity>();

        for (int y = 0; y < world.getHeight(); y++) {
            for (int x = 0; x < world.getWidth(); x++) {
                char c = world.charAtGridPos(x, y);
                if (Character.isLetter(c)) {
                    if (c == 'v') { // Store index if entity is viewer for the getViewer() method.
                        viewerIndex = entityList.size();
                    }
                    entityList.add(createEntity(x, y, c));
                    world.setCharAtGridPos(x, y, '0'); // clear levelMatrix of entity.
                }
            }
        }

        physicsController.setPhysicsObjectsList(entityList);
        if (mapView != null) {
           mapView.setDrawablesList(entityList);
        }
        return entityList;
    }
}
