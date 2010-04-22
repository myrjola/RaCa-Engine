package leveleditor;

import controllers.FileController;
import environment.Entity;
import environment.EntityFactory;
import environment.World;
import exceptions.CorruptLevelFileException;
import exceptions.ViewerNotFoundException;
import racaengine.RaCaEngine;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * This is the main class for the level editor. It's the recommended way of
 * testing the engine. 
 */
public class LevelEditor {
    private RaCaEngine engine;
    private Thread engineThread;
    private SwingWorker<Void, Void> worker;
    private final LevelEditorGUI gui;
    private World world;
    private int LEVEL_NUMBER;
    private boolean engineRunning;
    private int viewerPositionX; // Tells where the viewer is situated.
    private int viewerPositionY; // Tells where the viewer is situated.


    /**
     * Creates the Editor and an instance of the RaCa-engine in the background.
     */
    private LevelEditor() {
        gui = new LevelEditorGUI(this);
        init();
    }

    /**
     * This method is used when the GUI's mapview has been clicked. Assigns a wall
     * or object at the mouse position.
     * @param x coordinate.
     * @param y coordinate.
     * @param objectName the currently chosen object in the list.
     */
    public void mapViewClick(int x, int y, String objectName) {
        if (world.coordinatesInsideWorld(x, y)) {
            if (objectName.startsWith("wall")) {
                Entity viewer = engine.getViewer();
                int gridX = viewer.x / World.GRID_SIZE;
                int gridY = viewer.y / World.GRID_SIZE;
                if (x != gridX || y != gridY) { // Can't place walls on viewer.
                    world.setCharAtGridPos(x, y, objectName.charAt(4));
                    if (engineRunning) {
                        engine.getWorld().setCharAtGridPos(x, y, objectName.charAt(4));
                    }
                }

            } else if (objectName.startsWith("delete")) {
                if (world.charAtGridPos(x, y) == 'v') {
                    viewerPositionX = -1; // mark viewer as missing. 
                }
                // Delete object at position.
                world.setCharAtGridPos(x, y, '0');
                if (engineRunning) {
                    engine.getWorld().setCharAtGridPos(x, y, '0');
                }

            } else if (objectName.startsWith("viewer")) {
                // Clear the last viewer position.
                if (viewerPositionX != -1) {
                    world.setCharAtGridPos(viewerPositionX, viewerPositionY, '0');
                }
                // Put viewer to new position.
                world.setCharAtGridPos(x, y, 'v');
                viewerPositionX = x;
                viewerPositionY = y;

            } else { // Object must be another Entity.
                world.setCharAtGridPos(x, y, objectName.charAt(0));
                if (engineRunning) {
                    engine.getEntityList().add(EntityFactory.createEntity(x, y, objectName.charAt(0)));
                }
            }
        }
    }

    /**
     * Saves the current level to it's corresponding file.
     */
    public void saveLevel() {
        try {
            updateViewerPos(world.getLevelMatrix());
            if (viewerPositionX == -1) { // if viewer missing.
                JOptionPane.showMessageDialog(gui, "Viewer missing from level. Can't save");
                return;
            }
            FileController.saveWorld(world, LEVEL_NUMBER);
            JOptionPane.showMessageDialog(gui, "Level saved.");
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(gui, "Level file couldn't be saved.\n" +
                    "Maybe wrong permissions or disk full.");
        }
    }

    /**
     * Launches the engine in the current level.
     */
    public void testLevel() {
        try {
            engine.changeWorld(new World(world.copyLevelMatrix()));
        } catch (ViewerNotFoundException e) {
            JOptionPane.showMessageDialog(gui, "No viewer found.");
            return;
        }
        engineRunning = true;
        engine.setVisible(true);
        gui.engineRun();
        worker.execute();
    }

    /**
     * Opens a JFileChooser to open a level.
     */
    public void loadLevel() {
        try {
            char[][] levelMatrix = FileController.loadWorld(gui);
            // Find the viewer in the matrix and save it's position.
            if (levelMatrix == null) {
                return;
            }
            updateViewerPos(levelMatrix);
            world.reInit(levelMatrix);
        } catch (CorruptLevelFileException e) {
            JOptionPane.showMessageDialog(gui, "Corrupt level file. A new one will be created.");
            newLevel(1, 10, 10);
        }
    }

    /**
     * Creates an new empty level with the viewer in the upper left corner.
     * 
     * @param levelNumber
     * @param levelWidth
     * @param levelHeight
     */
    public void newLevel(Integer levelNumber, Integer levelWidth, Integer levelHeight) {
        LEVEL_NUMBER = levelNumber;
        char levelMatrix[][] = new char[levelHeight][levelWidth];
        // Defaults to empty level.
        for (int i = 0; i < levelMatrix.length; i++) {
            for (int j = 0; j < levelMatrix[0].length; j++) {
                levelMatrix[i][j] = '0';
            }
        }
        // Add viewer to upper left corner.
        levelMatrix[1][1] = 'v';
        viewerPositionX = 1;
        viewerPositionY = 1;
        try {
            if (world == null) {
                world = new World(new char[1][1]);
            }
            world.reInit(levelMatrix);
        } catch (ViewerNotFoundException e) {
            System.out.println("Viewer not found in newly created level. One of the impossible bugs");
            e.printStackTrace();
            System.exit(1);
        }
        world.fillOuterWalls();
    }

    public static void main(String[] args) {
        new LevelEditor();
    }

    private void init() {
        LEVEL_NUMBER = 1;
        try {
            char[][] levelMatrix = FileController.loadWorld("levels/" + LEVEL_NUMBER + ".lvl");
            world = new World(levelMatrix);
            updateViewerPos(levelMatrix);

        } catch (CorruptLevelFileException e) {
            JOptionPane.showMessageDialog(gui, "Corrupt level file. A new one will be created.");
            newLevel(1, 10, 10);
            saveLevel();

        } catch (ViewerNotFoundException e) {
            // Do nothing the viewer missing will be caught again when testing the level.
        } finally {
            // Create the engine and assign it to a background thread.
            engine = new RaCaEngine();
            engineThread = new Thread(engine);
            engineRunning = false;
            engine.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            engine.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    worker.cancel(true);
                    gui.engineStopped();
                    engineRunning = false;
                }
            });
            worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    engineThread.run();
                    return null;
                }
            };

            // Show the GUI.
            gui.init(world);
            gui.setVisible(true);
        }
    }

    private void updateViewerPos(char[][] levelMatrix) {
        for (int y = 0; y < levelMatrix.length; y++) {
            for (int x = 0; x < levelMatrix[0].length; x++) {
                if (levelMatrix[y][x] == 'v') {
                    viewerPositionX = x;
                    viewerPositionY = y;
                    return;
                }
            }
        }
        // Mark viewer not found.
        viewerPositionX = -1;
    }
}
