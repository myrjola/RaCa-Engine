package renderer;
/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

import controllers.ContainsSettings;
import controllers.FileController;
import controllers.SettingsController;
import environment.Entity;
import environment.Settings;
import environment.World;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * A Top-Down view of the level. Used by the level editor or could be an in-game map.
 */
public class MapView extends JPanel implements ContainsSettings {
    public int PIXELS_PER_SQUARE;
    private World world;
    private ArrayList<Entity> drawablesList;
    private BufferedImage buffer;
    private Graphics2D bufferG2D;
    private final Predictor predictor;
    private double interpolation;
    private BufferedImage wallSprites[];

    /**
     * Creates a MapView of the given World.
     * @param world
     */
    public MapView(World world) {
        super();
        this.world = world;
        drawablesList = new ArrayList<Entity>();
        predictor = new Predictor();
        interpolation = 0;
        setDoubleBuffered(true);
        SettingsController.getInstance().addListener(this);
    }

    /**
     * ReInits the MapView if world has changed.
     */
    public void reInit() {
        if (world != null) { // Wait if world is null.
            buffer = new BufferedImage(world.getWidth() * PIXELS_PER_SQUARE,
                    world.getHeight() * PIXELS_PER_SQUARE,
            BufferedImage.TYPE_INT_RGB);
            bufferG2D = buffer.createGraphics();
        }
    }

    /**
     * Used by pack().
     * @return the correct dimension of the level.
     */
    public Dimension getPreferredSize() {
        return new Dimension(world.getWidth() * PIXELS_PER_SQUARE,
                             world.getHeight() * PIXELS_PER_SQUARE);
    }

    /**
     * Draws the map.
     * @param interpolation used to predict position of moving objects.
     */
    public void update(double interpolation) {
        this.interpolation = interpolation;
        repaint();
    }

    /**
     * Shouldn't be used. Public because of the interface constraints.
     * Use update() instead.
     *
     * @param screen
     */
    public void paint(Graphics screen) {
        bufferG2D.setPaint(Color.lightGray);
        bufferG2D.fill(getVisibleRect());
        drawWalls(bufferG2D);
        for (Entity e : drawablesList) {
            drawEntity(bufferG2D, e, interpolation);
        }
        Toolkit.getDefaultToolkit().sync();
        ((Graphics2D) screen).drawImage(buffer, null, 0, 0);
        screen.dispose();
    }

    /**
     * Sets the list of Entities to draw on the level.
     * @param list
     */
    public void setDrawablesList(ArrayList<Entity> list) {
        drawablesList = list;
    }

    public void updateSettings(Settings settings) {
        PIXELS_PER_SQUARE = settings.get("PIXELS_PER_SQUARE");
        int WALL_TEXTURES = settings.get("WALL_TEXTURES");
        if (WALL_TEXTURES == 0) { // Skip texture support.
            WALL_TEXTURES = 1;
            wallSprites = new BufferedImage[1];
            wallSprites[0] = new BufferedImage(PIXELS_PER_SQUARE, PIXELS_PER_SQUARE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) wallSprites[0].getGraphics();
            g2d.setPaint(Color.darkGray);
            g2d.fillRect(0, 0, PIXELS_PER_SQUARE, PIXELS_PER_SQUARE);
        } else {
            wallSprites = new BufferedImage[WALL_TEXTURES];
            for (int textureNumber = 0; textureNumber < wallSprites.length; textureNumber++) {
                wallSprites[textureNumber] = new BufferedImage(PIXELS_PER_SQUARE,
                        PIXELS_PER_SQUARE, BufferedImage.TYPE_INT_RGB);
                BufferedImage wallSprite = wallSprites[textureNumber];
                // Wall numbering starts from 1.
                BufferedImage spriteTemp = FileController.loadImage("res/wall" + (textureNumber + 1) + ".png");
                Image scaledSpriteTemp = spriteTemp.getScaledInstance(PIXELS_PER_SQUARE,
                        PIXELS_PER_SQUARE, Image.SCALE_SMOOTH);
                Graphics2D g2d = (Graphics2D) wallSprite.getGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.drawImage(scaledSpriteTemp, 0, 0, PIXELS_PER_SQUARE, PIXELS_PER_SQUARE, null);
            }
        }
        reInit();
    }

    private void drawWalls(Graphics2D g2d) {
        for (int y = 0; y < world.getHeight(); y++) {
            for (int x = 0; x < world.getWidth(); x++) {
                char c = world.charAtGridPos(x, y);
                if (Character.isDigit(c) && c != '0') { // If character is wall.
                    try {
                        g2d.drawImage(wallSprites[c - '0' - 1], null, x * PIXELS_PER_SQUARE, y * PIXELS_PER_SQUARE);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        // Draw the default wall if texture not found.
                        g2d.drawImage(wallSprites[0], null, x * PIXELS_PER_SQUARE, y * PIXELS_PER_SQUARE);
                    }
                } else if (Character.isLetter(c)) { // If character is object.
                    int xPos = x * PIXELS_PER_SQUARE;
                    int yPos = y * PIXELS_PER_SQUARE;
                    g2d.setPaint(Color.lightGray);
                    g2d.fillRect(xPos, yPos,
                            PIXELS_PER_SQUARE, PIXELS_PER_SQUARE);
                    g2d.setPaint(Color.red);
                    // Draw the character inside a red circle.
                    Ellipse2D entityCircle = new Ellipse2D.Double(
                            xPos + PIXELS_PER_SQUARE / 2 - 7,
                            yPos + PIXELS_PER_SQUARE / 2 - 15,
                            20, 20);
                    g2d.fill(entityCircle);
                    g2d.setPaint(Color.black);
                    g2d.draw(entityCircle);
                    g2d.setFont(new Font("Serif", Font.BOLD, PIXELS_PER_SQUARE / 16 + 10));
                    g2d.drawString(
                            "" + c,
                            xPos + PIXELS_PER_SQUARE / 2,
                            yPos + PIXELS_PER_SQUARE / 2);
                }
            }
        }
    }

    private void drawEntity(Graphics2D g2d, Entity entity, double interpolation) {
        predictor.predict(entity, interpolation);
        int entityX = predictor.getX();
        int entityY = predictor.getY();
        double entityDirection = predictor.getDirection();

        // Place entity on screen.
        entityX /= (World.GRID_SIZE / PIXELS_PER_SQUARE);
        entityY /= (World.GRID_SIZE / PIXELS_PER_SQUARE);
        g2d.setStroke(new BasicStroke(2));
        if (entity.npc) {
            g2d.setColor(Color.blue);
            g2d.setPaint(Color.blue);
        } else {
            g2d.setColor(Color.red);
            g2d.setPaint(Color.red);
        }
        Ellipse2D entityCircle = new Ellipse2D.Double(
                entityX - 5,
                entityY - 5,
                10, 10);
        g2d.fill(entityCircle);
        // Draw a line to show the entity's direction.
        g2d.drawLine(entityX, entityY,
                     (int) (entityX + 10 * Math.cos(entityDirection)),
                     (int) (entityY + 10 * Math.sin(entityDirection)));
    }
}
