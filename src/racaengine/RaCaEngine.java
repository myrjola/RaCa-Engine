package racaengine;
/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

import controllers.*;
import environment.Entity;
import environment.EntityFactory;
import environment.Settings;
import environment.World;
import exceptions.CorruptLevelFileException;
import exceptions.CorruptSettingsException;
import exceptions.ViewerNotFoundException;
import renderer.RendererView;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import static java.lang.System.currentTimeMillis;

/**
 * The main class for the RaCa-Engine.
 */
public class RaCaEngine extends JFrame implements ContainsSettings, Runnable {
    private static int MS_PER_TICK;
    private static int MS_PER_FRAME;
    private InputController inputController;
    private PhysicsController physicsController;
    private RendererView rendererView;
    private World world;
    private Entity viewer;
    private ArrayList<Entity> entityList;

    /**
     * Creates the engines window and initializes everything.
     * The engine is started with the run method.
     */
    public RaCaEngine() {
        setTitle("RaCa-Engine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        try {
            SettingsController.getInstance().updateSettings(FileController.loadSettings());
        } catch (CorruptSettingsException e) {
            JOptionPane.showMessageDialog(this, "Corrupt settings file. Loading defaults.");
            SettingsController.getInstance().updateSettings();
        }
        SettingsController.getInstance().addListener(this);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                inputController.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                inputController.keyReleased(e);
            }
        });
        // Load the first level.
        try {
            world = new World(FileController.loadWorld("levels/1.lvl"));
            changeWorld(world);

        } catch (CorruptLevelFileException e) {
            JOptionPane.showMessageDialog(this, "Corrupt level file. Terminating");
            e.printStackTrace();
            System.exit(1);
        } catch (ViewerNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Viewer not found in level. Terminating");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Starts the engine. Only possible to stop if suspending the thread it's running in.
     */
    public void run() {
        long currentMS = currentTimeMillis();
        long nextTickAtMS = currentMS + MS_PER_TICK;
        long lastFrameMS = currentMS;
        double interpolation;

        while (true) {
            // Update engine logic.
            if (currentTimeMillis() > nextTickAtMS) {
                inputController.update();
                physicsController.update();
                nextTickAtMS += MS_PER_TICK;
            }
            // Draw screen.
            if (currentTimeMillis() - lastFrameMS > MS_PER_FRAME) {
                lastFrameMS = currentTimeMillis();
                interpolation = getInterpolation(nextTickAtMS);
                rendererView.update(interpolation);
            }
        }
    }

    /**
     * Returns the current world the engine uses.
     * @return ditto.
     */
    public World getWorld() {
        return world;
    }


    public static void main(String[] Args) {
        RaCaEngine racaEngine = new RaCaEngine();
        racaEngine.setVisible(true);
        racaEngine.run();
    }

    /**
     * Changes the current world.
     * @param newWorld the new World.
     * @throws ViewerNotFoundException
     */
    public void changeWorld(World newWorld) throws ViewerNotFoundException {
        world = newWorld;
        // Reinit almost everything because of world change.
        SettingsController settingsController = SettingsController.getInstance();
        settingsController.clearSettingsListeners();
        settingsController.addListener(this);
        physicsController = new PhysicsController(newWorld);
        EntityFactory entityFactory = new EntityFactory(newWorld, physicsController);
        entityList = entityFactory.fill();
        viewer = entityFactory.getViewer();
        if (rendererView != null) {
            this.remove(rendererView);
        }
        rendererView = new RendererView(newWorld, viewer);
        add(rendererView);
        inputController = new InputController(viewer);
    }

    public void updateSettings(Settings settings) {
        MS_PER_FRAME = 1000 / settings.get("MAX_FPS");
        MS_PER_TICK = settings.get("MS_PER_TICK");

        pack(); // Resize window if resolution changed.
    }

    /**
     * Returns the current list of entities in the engine.
     * @return ditto.
     */
    public ArrayList<Entity> getEntityList() {
        return entityList;
    }

    /**
     * Returns the viewer Entity.
     * @return ditto.
     */
    public Entity getViewer() {
        return viewer;
    }

    private double getInterpolation(long nextTickAt) {
        return (currentTimeMillis() + MS_PER_TICK - nextTickAt) / (double) MS_PER_TICK;
    }
}
