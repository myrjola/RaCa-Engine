package renderer;

import controllers.ContainsSettings;
import controllers.FileController;
import environment.Settings;
import environment.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * Contains the global variables and some utility functions used by the renderer's components.
 */
public class RendererCommon implements ContainsSettings {
    public int FOV;
    public int RESOLUTION_X;
    public int RESOLUTION_Y;
    public int VIEW_DISTANCE;
    public int WALL_TEXTURES;
    public World world;
    public int viewerX;
    public int viewerY;
    public int viewerHeight;
    public double viewerDirection;
    public int[] distancesToWalls;
    public int[] wallHeights;
    public int[] gridIndexes; // The place in the grid for each wall, needed for textures.
    public int[] textureIndexes; // Which texture to draw at which strip.
    public RescaleOp[] shaderOperations;
    public Graphics2D bufferG2D;
    public Graphics2D[] stripsG2D;
    public BufferedImage[] strips;
    public BufferedImage[] wallTextures;
    public int wallDrawShift;
    public int furthestWallTop;
    public int distanceToProjectionPlane;

    /**
     * Creates a shaded floor image used by the floor and ceiling drawers.
     * @param color the base color to render the floor in.
     * @return a shaded floor image.
     */
    public BufferedImage createFloorImage(Color color) {
        BufferedImage floorImage = new BufferedImage(RESOLUTION_X, RESOLUTION_Y, BufferedImage.TYPE_INT_RGB);
        Graphics2D floorG2D = (Graphics2D) floorImage.getGraphics();
        BufferedImage floorImageRow = new BufferedImage(RESOLUTION_X, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D floorRowG2D = (Graphics2D) floorImageRow.getGraphics();
        double radsPerRow = Math.atan(1 / (double) distanceToProjectionPlane);
        double angle = 0.00000000000000000001; // To avoid division by zero
        floorRowG2D.setPaint(color);
        floorRowG2D.fillRect(0, 0, RESOLUTION_X, 1);
        for (int row = 0; row < RESOLUTION_Y; row++) {
            int distanceToFloor = (int) (viewerHeight / Math.sin(angle));
            RescaleOp shadeOp = Shader.getShadeOp(VIEW_DISTANCE, distanceToFloor);
            floorG2D.drawImage(floorImageRow, shadeOp, 0, row);
            angle += radsPerRow;
        }
        return floorImage;
    }

    public void updateSettings(Settings settings) {
        viewerHeight = World.GRID_SIZE / 2;
        FOV = settings.get("FOV");
        RESOLUTION_X = settings.get("RESOLUTION_X");
        RESOLUTION_Y = settings.get("RESOLUTION_Y");
        WALL_TEXTURES = settings.get("WALL_TEXTURES");
        VIEW_DISTANCE = settings.get("VIEW_DISTANCE");
        wallTextures = new BufferedImage[WALL_TEXTURES + 1];
        Graphics2D[] wallTextureG2Ds = new Graphics2D[WALL_TEXTURES + 1];
        for (int textureNumber = 1; textureNumber <= WALL_TEXTURES; textureNumber++) {
            BufferedImage wallTextureTemp = FileController.loadImage("res/wall" + textureNumber + ".png");
            wallTextures[textureNumber] = new BufferedImage(RESOLUTION_Y, RESOLUTION_Y,
                    BufferedImage.TYPE_INT_RGB);
            wallTextureG2Ds[textureNumber] = wallTextures[textureNumber].createGraphics();
            wallTextureG2Ds[textureNumber].drawImage(wallTextureTemp, 0, 0, RESOLUTION_Y,
                    RESOLUTION_Y, null);
        }
        distanceToProjectionPlane = (int) (RESOLUTION_X / 2 / Math.tan(Math.toRadians(FOV) / 2));
        distancesToWalls = new int[RESOLUTION_X];
        wallHeights = new int[RESOLUTION_X];
        gridIndexes = new int[RESOLUTION_X];
        textureIndexes = new int[RESOLUTION_X];
        strips = new BufferedImage[RESOLUTION_X];
        stripsG2D = new Graphics2D[RESOLUTION_X];
        shaderOperations = new RescaleOp[RESOLUTION_X];
        for (int i = 0; i < RESOLUTION_X; i++) {
            strips[i] = new BufferedImage(1, RESOLUTION_Y, BufferedImage.TYPE_INT_RGB);
            stripsG2D[i] = strips[i].createGraphics();
        } 
    }
}
