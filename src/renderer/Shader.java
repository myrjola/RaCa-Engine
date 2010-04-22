package renderer;

import environment.World;

import java.awt.image.RescaleOp;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * Handles the shading of objects according to distance. Depends on RayCaster.
 */
public class Shader implements RendererComponent {
    /**
     * @param viewDistance the distance to the projection plane.
     * @param distance to the object.
     * @return the BufferedImageOp to shade an BufferedImage according to distance.
     */
    public static RescaleOp getShadeOp(int viewDistance, int distance) {
        float intensity = shade(distance, viewDistance);
        return new RescaleOp(intensity, 0, null);
    }

    public void update(RendererCommon common) {
        int i = 0;
        for (int distance: common.distancesToWalls) {
            float intensity = shade(distance, common.VIEW_DISTANCE);
            common.shaderOperations[i++] = new RescaleOp(intensity, 0, null);
        }
    }

    private static float shade(int distance, int viewDistance) {
        float intensity = (float) viewDistance * World.GRID_SIZE / distance;
        return Math.min(intensity, 1.0f);
    }
}
