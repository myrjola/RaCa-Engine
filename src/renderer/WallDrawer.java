package renderer;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * Draws the wall strips applying the shading operations. Depends on Shader and WallStripCreator.
 */
public class WallDrawer implements RendererComponent {

    private final RendererView parent;

    /**
     * Constructor.
     * @param parent the RendererView containing this WallDrawer.
     */
    public WallDrawer(RendererView parent) {
        this.parent = parent;
    }

    public void update(RendererCommon common) {
        int i = 0;
        for (int wallHeight: common.wallHeights) {
            common.shaderOperations[i].filter(common.strips[i], common.strips[i]);
            common.bufferG2D.drawImage(common.strips[i],
                    i++,
                    (common.RESOLUTION_Y - wallHeight) / 2 - common.wallDrawShift,
                    1,
                    wallHeight,
                    parent);
        }

    }
}
