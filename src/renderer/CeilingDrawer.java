package renderer;

import java.awt.*;
import java.awt.image.BufferedImage;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * Draws a shaded ceiling.
 */
public class CeilingDrawer implements RendererComponent {
    private final BufferedImage ceilingImage;

    /**
     * Constructs the CeilingDrawer.
     * @param common is used to get the info for the shading.
     */
    public CeilingDrawer(RendererCommon common) {
        BufferedImage temp = common.createFloorImage(Color.darkGray);
        ceilingImage = new BufferedImage(common.RESOLUTION_X, common.RESOLUTION_Y, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) ceilingImage.getGraphics();
        g2d.translate(common.RESOLUTION_X / 2, common.RESOLUTION_Y / 2);
        g2d.rotate(Math.PI);
        g2d.translate(-common.RESOLUTION_X / 2, -common.RESOLUTION_Y / 2);
        g2d.drawImage(temp, null, 0, 0);
    }

    public void update(RendererCommon common) {
        Graphics2D g2d = common.bufferG2D;
        g2d.drawImage(ceilingImage, null, 0, -common.RESOLUTION_Y / 2 - common.wallDrawShift);
    }
}
