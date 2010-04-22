package renderer;

import environment.World;

import java.awt.image.BufferedImage;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * Creates the vertical strips of walls of the correct height and position in the
 * texture.
 */
public class WallStripCreator implements RendererComponent {

    private final int textureWidth;
    private final BufferedImage[][] textureStripsArray;

    /**
     * Creates a WallStripCreator. 
     * @param common contains the global variables used.
     */
    public WallStripCreator(RendererCommon common) {
        textureWidth = common.wallTextures[1].getWidth();
        int textureHeight = common.wallTextures[1].getHeight();
        textureStripsArray = new BufferedImage[common.WALL_TEXTURES + 1][textureWidth];
        for (int textureIndex = 1; textureIndex <= common.WALL_TEXTURES; textureIndex++) {
            for (int i = 0; i < textureWidth; i++) {
                textureStripsArray[textureIndex][i] = common.wallTextures[textureIndex].getSubimage(
                        i, 0, 1, textureHeight);
            }
        }
    }

    public void update(RendererCommon common) {
        int i = 0;
        for (int gridIndex : common.gridIndexes) {
            int textureIndex = common.textureIndexes[i];
            if (textureStripsArray.length <= textureIndex) { // Unsupported texture.
                textureIndex = 1;
            }
            BufferedImage textureStrips[] = textureStripsArray[textureIndex];
            int gridIndexOnTextureStrip = gridIndex * textureWidth / World.GRID_SIZE;
            common.stripsG2D[i].drawImage(textureStrips[gridIndexOnTextureStrip], null, 0, 0);
            i++;
        }
    }
}
