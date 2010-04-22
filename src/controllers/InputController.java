package controllers;
/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

import environment.Entity;
import environment.Settings;

import java.awt.event.KeyEvent;

/**
 * A keyboard interface to an Entity.
 */
public class InputController implements ContainsSettings {
    private final Entity controllable;
    private int KEY_UP;
    private int KEY_DOWN;
    private int KEY_LEFT;
    private int KEY_RIGHT;
    private int KEY_STRAFE_LEFT;
    private int KEY_STRAFE_RIGHT;
    private int KEY_LOOK_UP;
    private int KEY_LOOK_DOWN;
    private int GRID_SIZE;
    private boolean movingUp;
    private boolean movingDown;
    private boolean turningLeft;
    private boolean turningRight;
    private boolean strafingLeft;
    private boolean strafingRight;
    private boolean lookingUp;
    private boolean lookingDown;


    /**
     * Creates an InputController and binds an Entity to it.
     *
     * @param controllable the Entity to be bound.
     */
    public InputController(Entity controllable) {
        this.controllable = controllable;
        controllable.npc = false;
        movingUp = false;
        movingDown = false;
        turningLeft = false;
        turningRight = false;
        strafingLeft = false;
        strafingRight = false;
        lookingUp = false;
        lookingDown = false;
        SettingsController.getInstance().addListener(this);
    }

    /**
     * Sets the Entity's movement state according to the pressed key.
     * Should use a KeyEventListener as mediator.
     *
     * @param e the KeyEvent passed by an KeyEventListener.
     */
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KEY_LEFT) {
            turningLeft = true;

        } else if (keyCode == KEY_RIGHT) {
            turningRight = true;

        } else if (keyCode == KEY_UP) {
            movingUp = true;

        } else if (keyCode == KEY_DOWN) {
            movingDown = true;

        } else if (keyCode == KEY_STRAFE_LEFT) {
            strafingLeft = true;

        } else if (keyCode == KEY_STRAFE_RIGHT) {
            strafingRight = true;

        } else if (keyCode == KeyEvent.VK_W) {
            lookingUp = true;

        } else if (keyCode == KeyEvent.VK_S) {
            lookingDown = true;

        }
    }

    /**
     * Resets the movement state according to the released key. An KeyEventListener  should
     * be used as mediator.
     *
     * @param e Event passed by the KeyEventListener
     */
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KEY_LEFT) {
            turningLeft = false;

        } else if (keyCode == KEY_RIGHT) {
            turningRight = false;

        } else if (keyCode == KEY_UP) {
            movingUp = false;

        } else if (keyCode == KEY_DOWN) {
            movingDown = false;

        } else if (keyCode == KEY_STRAFE_LEFT) {
            strafingLeft = false;

        } else if (keyCode == KEY_STRAFE_RIGHT) {
            strafingRight = false;
        } else if (keyCode == KEY_LOOK_UP) {
            lookingUp = false;

        } else if (keyCode == KEY_LOOK_DOWN) {
            lookingDown = false;
        }
    }

    /**
     * Updates the acceleration and direction change speed according to the movement state
     * of the Entity.
     */
    public void update() {
        if (movingUp) {
            controllable.acceleration = controllable.ACCELERATION;
        } else if (movingDown) {
            controllable.acceleration = -controllable.ACCELERATION;

        } else {
            controllable.acceleration = 0;
        }
        if (turningLeft) {
            controllable.directionChange = -controllable.DIR_CHANGE_SPEED;
        } else if (turningRight) {
            controllable.directionChange = controllable.DIR_CHANGE_SPEED;
        } else {
            controllable.directionChange = 0;
        }
        if (strafingLeft) {
            controllable.strafe = -1;
        } else if (strafingRight) {
            controllable.strafe = 1;
        } else {
            controllable.strafe = 0;
        }
        if (lookingUp && controllable.height < GRID_SIZE ) {
            controllable.height += 10;
        } else if (lookingDown && controllable.height > 0) {
            controllable.height -= 10;
        }
    }

    /**
     * Updates the key mappings used by keyPressed and keyReleased.
     *
     * @param settings
     */
    public void updateSettings(Settings settings) {
        KEY_UP = settings.get("KEY_UP");
        KEY_DOWN = settings.get("KEY_DOWN");
        KEY_LEFT = settings.get("KEY_LEFT");
        KEY_RIGHT = settings.get("KEY_RIGHT");
        KEY_STRAFE_LEFT = settings.get("KEY_STRAFE_LEFT");
        KEY_STRAFE_RIGHT = settings.get("KEY_STRAFE_RIGHT");
        KEY_LOOK_UP = settings.get("KEY_LOOK_UP");
        KEY_LOOK_DOWN = settings.get("KEY_LOOK_DOWN");
        GRID_SIZE = settings.get("GRID_SIZE");
    }
}
