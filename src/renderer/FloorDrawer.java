package renderer;

import java.awt.*;
import java.awt.image.BufferedImage;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * Draws a shaded floor.
 */
public class FloorDrawer implements RendererComponent {
    private final BufferedImage floorImage;

    /**
     * Constructs the FloorDrawer.
     * @param common used to get shading information.
     */
    public FloorDrawer(RendererCommon common) {
        floorImage = common.createFloorImage(Color.lightGray);
    }


    public void update(RendererCommon common) {
        Graphics2D g2d = common.bufferG2D;
        g2d.drawImage(floorImage, null, 0, common.RESOLUTION_Y / 2 - common.wallDrawShift);
    }
}
