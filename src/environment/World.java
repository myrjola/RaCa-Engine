package environment;

import controllers.ContainsSettings;
import controllers.SettingsController;
import exceptions.ViewerNotFoundException;

/**
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * The level representation in the engine. Contains walls and entities represented in
 * a char matrix.
 */
public class World implements ContainsSettings {
    private int width;
    private int height;
    private char[][] levelMatrix;
    public static int GRID_SIZE;

    /**
     * Constructs a World with the given level matrix.
     * @param levelMatrix the objects and walls in a char matrix.
     * @throws ViewerNotFoundException
     */
    public World(char[][] levelMatrix) throws ViewerNotFoundException {
        SettingsController.getInstance().addListener(this);
        reInit(levelMatrix);
    }

    /**
     * Returns a deep copy of the levelMatrix.
     * @return ditto.
     */
    public char[][] copyLevelMatrix() {
        char[][] copy = new char[height][width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                copy[y][x] = levelMatrix[y][x];
            }
        }
        return copy;
    }

    /**
     * Essentially as the constructor, but doesn't create a new object.
     * To be used when the same World is used in many places, say the engine
     * and level editor.
     * 
     * @param levelMatrix
     * @throws ViewerNotFoundException
     */
    public void reInit(char[][] levelMatrix) {
        this.levelMatrix = levelMatrix;
        width = levelMatrix[0].length;
        height = levelMatrix.length;
    }

    /**
     * Returns the matrix representing the level.
     * @return ditto.
     */
    public char[][] getLevelMatrix() {
        return levelMatrix;
    }

    /**
     * Checks if there's a solid wall at the given coordinates.
     * There's always a wall outside the levelMatrix.
     *
     * @param x coordinate.
     * @param y coordinate.
     * @return true if wall at position.
     */
    public boolean wallAtPos(int x, int y) {
        try {
            x /= GRID_SIZE;
            y /= GRID_SIZE;
            return levelMatrix[y][x] != '0';
        } catch (ArrayIndexOutOfBoundsException e) {
            return true;
        }
    }

    /**
     * Returns a char from the levelMatrix.
     * @param x coordinate
     * @param y coordinate
     * @return the char at given position in the levelMatrix.
     */
    public char charAtPos(int x, int y) {
        try {
            x /= GRID_SIZE;
            y /= GRID_SIZE;
            return levelMatrix[y][x];
        } catch (ArrayIndexOutOfBoundsException e) {
            // If raycaster outside of matrix. Return the wall1.
            return '1';
        }
    }

    /**
     * Same as charAtPos but, snapped to the grid.
     *
     * @param x coordinate in grid.
     * @param y coordinate in grid.
     * @return char at given position in levelMatrix.
     */
    public char charAtGridPos(int x, int y) {
        return levelMatrix[y][x];
    }

    /**
     * Setter for char at given grid position.
     *
     * @param x coordinate in grid.
     * @param y coordinate in grid.
     * @param c char to be set.
     */
    public void setCharAtGridPos(int x, int y, char c) {
        levelMatrix[y][x] = c;
    }

    /**
     * Returns the levels width.
     * @return ditto.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the levels height.
     * @return ditto.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Updates the GRID_SIZE. Unsafe when the application is running.
     * @param settings contains GRID_SIZE.
     */
    public void updateSettings(Settings settings) {
        GRID_SIZE = settings.get("GRID_SIZE");
    }

    /**
     * Snaps the given coordinate to the grid.
     * @param position
     * @return the snapped coordinate.
     */
    public int snap(int position) {
        return position / GRID_SIZE * GRID_SIZE;
    }

    /**
     * Fills the border with walls. Quick fix for crash when drawing walls outside level.
     */
    public void fillOuterWalls() {
        for (int x = 0; x < width; x++) {
            levelMatrix[0][x] = '1';
            levelMatrix[height - 1][x] = '1';
        }
        for (int y = 0; y < height; y++) {
            levelMatrix[y][0] = '1';
            levelMatrix[y][width - 1] = '1';
        }
    }

    /**
     * Checks if the given coordinates are inside the level.
     * @param x coordinate in grid.
     * @param y coordinate in grid.
     * @return true if inside the World.
     */
    public boolean coordinatesInsideWorld(int x, int y) {
        // Outer walls are outside the world.
        return !(x < 1 || x > width - 2 || y < 1 || y > height - 2);
    }
}
