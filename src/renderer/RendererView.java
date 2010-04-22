package renderer;

import controllers.ContainsSettings;
import controllers.SettingsController;
import environment.Entity;
import environment.Settings;
import environment.World;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * The main class of the renderer. Uses the RendererComponents to draw the image.
 */
public class RendererView extends JPanel implements ContainsSettings {
    private boolean SHOW_FPS;
    private final RendererCommon common;
    private final Entity viewer;
    private BufferedImage buffer;
    private Predictor predictor;
    private RendererComponent floorDrawer;
    private RendererComponent ceilingDrawer;
    private RendererComponent rayCaster;
    private RendererComponent wallStripCreator;
    private RendererComponent shader;
    private RendererComponent wallDrawer;
    private long lastDrawMS;

    /**
     * Constructor.
     * @param world the World to be used.
     * @param viewer the Entity representing the point of view.
     */
    public RendererView(World world, Entity viewer) {
        common = new RendererCommon();
        common.world = world;
        this.viewer = viewer;
        common.viewerX = viewer.x;
        common.viewerY = viewer.y;
        common.viewerDirection = viewer.direction;
        common.viewerHeight = viewer.height;
        setDoubleBuffered(true);
        SettingsController.getInstance().addListener(this);
        lastDrawMS = 0;
    }

    /**
     * Used by pack()
     * @return the resolution of the engine.
     */
    public Dimension getPreferredSize() {
        return new Dimension(common.RESOLUTION_X, common.RESOLUTION_Y);
    }

    /**
     * Renders the image.
     * @param interpolation used in prediction.
     */
    public void update(double interpolation) {
        predictor.predict(viewer, interpolation);
        common.viewerX = predictor.getX();
        common.viewerY = predictor.getY();
        common.viewerDirection = predictor.getDirection();
        common.viewerHeight = viewer.height;
        repaint();
    }

    /**
     * Use the update method instead.
     * @param screen where to draw the image.
     */
    public void paint(Graphics screen) {
        floorDrawer.update(common);
        ceilingDrawer.update(common);
        rayCaster.update(common);
        if (wallStripCreator != null) {
            wallStripCreator.update(common);
        }
        shader.update(common);
        wallDrawer.update(common);
        if (wallStripCreator == null) {
            for (Graphics2D g2d: common.stripsG2D) {
                g2d.fillRect(0, 0, 1, common.RESOLUTION_Y);
            }
        }
        long currentMS = System.currentTimeMillis();
        int fps = (int) (1000 / (currentMS - lastDrawMS));
        if (SHOW_FPS) {
            lastDrawMS = currentMS;
            common.bufferG2D.setColor(Color.red);
            common.bufferG2D.setFont(new Font("Dialog", Font.PLAIN, 12));
            common.bufferG2D.drawString("FPS: " + fps, 5, 15);
        }
        Toolkit.getDefaultToolkit().sync();

        ((Graphics2D) screen).drawImage(buffer, null, 0, 0);
        screen.dispose();
    }

    public void updateSettings(Settings settings) {
        SHOW_FPS = (settings.get("SHOW_FPS") == 1);
        common.updateSettings(settings);
        buffer = new BufferedImage(common.RESOLUTION_X, common.RESOLUTION_Y, BufferedImage.TYPE_INT_ARGB);
        common.bufferG2D = buffer.createGraphics();
        // Init RendererComponents.
        predictor = new Predictor();
        rayCaster = new RayCaster();
        shader = new Shader();
        wallDrawer = new WallDrawer(this);
        floorDrawer = new FloorDrawer(common);
        ceilingDrawer = new CeilingDrawer(common);
        if (common.WALL_TEXTURES == 0) {
            wallStripCreator = null;
        } else {
            wallStripCreator = new WallStripCreator(common);
        }
    }
}
