package controllers;

import environment.Entity;
import environment.Settings;
import environment.World;

import java.util.ArrayList;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * Handles movement and collisions of Entities.
 */
public class PhysicsController implements ContainsSettings {
    private ArrayList<Entity> physicsObjectsList;
    private final World world;

    /**
     * Constructs a PhysicsController and assigns a World to
     * move the Entities in.
     *
     * @param world the Assigned World
     */
    public PhysicsController(World world) {
        this.world = world;
    }

    /**
     * Assigns the given list's entities to be controlled by this PhysicsController.
     *
     * @param list contains the new physics objects.
     */
    public void setPhysicsObjectsList(ArrayList<Entity> list) {
        physicsObjectsList = list;
    }

    /**
     * Updates Entities velocity, direction and position taking account on collisions.
     */
    public void update() {
        for (Entity e : physicsObjectsList) {
            e.direction += e.directionChange;
            double newDirection = e.direction;
            e.velocity += e.acceleration;
            if (e.velocity > e.MAX_VEL) {
                e.velocity = e.MAX_VEL;
            }
            else if (e.velocity < -e.MAX_VEL) {
                e.velocity = -e.MAX_VEL;
            }
            else if (e.acceleration == 0) {
                e.velocity /= 1.4; // Apply friction.
            }
            double newVelocity = e.velocity;
            if (e.strafe != 0) {
                if (Math.abs(e.acceleration) < 0.1) { // If == 0.
                    newDirection += e.strafe * Math.PI / 2;
                    newVelocity = e.MAX_VEL;
                } else {
                    if (e.velocity > 0) {
                        newDirection += e.strafe * Math.PI / 4;
                    } else {
                        newDirection -= e.strafe * Math.PI / 4;
                    }
                }
            }

            e.newX = e.x + (int) (newVelocity * Math.cos(newDirection));
            e.newY = e.y + (int) (newVelocity * Math.sin(newDirection));
            handleCollision(e);
        }
    }

    private void handleCollision(Entity e) {
        int radius = World.GRID_SIZE / 5; // Keep a safe distance to wall, makes drawing prettier.
        if (world.wallAtPos(e.x, e.newY - radius)) {
            e.newY = world.snap(e.y) + radius;
        }
        else if (world.wallAtPos(e.x, e.newY + radius)) {
            e.newY = world.snap(e.newY + radius) - radius;
        }
        if (world.wallAtPos(e.newX - radius, e.newY)) {
            e.newX = world.snap(e.x) + radius;
        }
        else if (world.wallAtPos(e.newX + radius, e.newY)) {
            e.newX = world.snap(e.newX + radius) - radius;
        }
        e.x = e.newX;
        e.y = e.newY;
    }

    /**
     * Updates the Entity's position if GRID_SIZE has changed.
     * @param settings
     */
    public void updateSettings(Settings settings) {
        // Entities must be moved to correct position if grid size has changed.
        // TODO: Get the correction working.
    }
}
