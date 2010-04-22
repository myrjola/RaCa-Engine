package renderer;

import environment.World;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * The "heart" of the rendering. Uses simplified ray-tracing to determine the height of walls.
 */
public class RayCaster implements RendererComponent {
    private int x;
    private int y;
    private World world;
    private static final int VERTICAL_WALL = 0;
    private static final int HORIZONTAL_WALL = 1;

    public void update(RendererCommon common) {
        x = common.viewerX;
        y = common.viewerY;
        double direction = common.viewerDirection;
        int[] gridIndex = new int[2]; // Used for textures. [0] for vertical walls and [1] for horizontal.
        int[] textureIndex = new int[2]; // Used for textures. [0] for vertical walls and [1] for horizontal.
        world = common.world;
        double radsPerRay = Math.toRadians(common.FOV) / common.RESOLUTION_X;
        // Start casting rays from viewers leftmost point of view.
        double rayDirection = common.viewerDirection - radsPerRay * common.RESOLUTION_X / 2;
        for (int i = 0; i < common.RESOLUTION_X; i++) {
            int d1 = castVerticalWallRay(rayDirection, gridIndex, textureIndex);
            int d2 = castHorizontalWallRay(rayDirection, gridIndex, textureIndex);
            rayDirection += radsPerRay;
            // Shortest distance gets added.
            int shortest;
            if (d1 < d2) {
                common.gridIndexes[i] = gridIndex[VERTICAL_WALL];
                common.textureIndexes[i] = textureIndex[VERTICAL_WALL];
                shortest = d1;
            } else {
                common.gridIndexes[i] = gridIndex[HORIZONTAL_WALL];
                common.textureIndexes[i] = textureIndex[HORIZONTAL_WALL];
                shortest = d2;
            }
            // Distance correction because of fish-eye effect.
            double directionOffset = direction - rayDirection;
            shortest *= Math.cos(directionOffset);
            common.distancesToWalls[i] = shortest;
        }
        // Calculate wall height.
        int minWallProjectedHeight = common.RESOLUTION_Y;
        int wallProjectedHeight;
        int distanceToProjectionPlane = common.distanceToProjectionPlane;
        int i = 0;
        for (int distance : common.distancesToWalls) {
            wallProjectedHeight = World.GRID_SIZE * distanceToProjectionPlane / distance;
            if (wallProjectedHeight < minWallProjectedHeight) {
                minWallProjectedHeight = wallProjectedHeight;
            }
            common.wallHeights[i++] = wallProjectedHeight;
        }
        common.wallDrawShift = (common.viewerHeight - World.GRID_SIZE / 2) * common.RESOLUTION_Y / World.GRID_SIZE;
        common.furthestWallTop = (common.RESOLUTION_Y - minWallProjectedHeight) / 2 - common.wallDrawShift;
    }

    private int castVerticalWallRay(double direction, int[] gridIndex, int[] textureIndex) {
        int leftOrRight = leftOrRight(direction);
        int upOrDown = upOrDown(direction);
        // Snap x to nearest grid intersection according to direction.
        int intersectX = world.snap(x);
        intersectX += ((leftOrRight < 0) ? -1 : World.GRID_SIZE);
        // Snap y to nearest grid intersection according to direction.
        double intersectY = y + (intersectX - x) * Math.tan(direction);
        // Next intersection at (intersectX +- GRID_SIZE, intersectY  + dy)
        double dx = World.GRID_SIZE * leftOrRight;
        double dy = Math.abs(World.GRID_SIZE * Math.tan(direction)) * upOrDown;
        while (!world.wallAtPos(intersectX, (int) intersectY)) {
            intersectX += dx;
            intersectY += dy;
        }
        gridIndex[VERTICAL_WALL] = (int) intersectY % World.GRID_SIZE;
        textureIndex[VERTICAL_WALL] = world.charAtPos(intersectX, (int) intersectY) - '0';
        // Faster to calculate length with trigonometry than pythogorean.
        return (int) Math.abs((x - intersectX) / Math.cos(direction));
    }

    private int castHorizontalWallRay(double direction, int[] gridIndex, int[] textureIndex) {
        int leftOrRight = leftOrRight(direction);
        int upOrDown = upOrDown(direction);
        // Snap y to nearest grid intersection according to direction.
        int intersectY = world.snap(y);
        intersectY += ((upOrDown < 0) ? -1: World.GRID_SIZE);
        // Snap x to nearest grid intersection according to direction.
        double intersectX = x + ((intersectY - y) / Math.tan(direction));
        // Next intersection at (intersectX + dx, intersectY  +- GRID_SIZE)
        double dy = World.GRID_SIZE * upOrDown;
        double dx = Math.abs(World.GRID_SIZE / Math.tan(direction)) * leftOrRight;
        while (!world.wallAtPos((int) intersectX, intersectY)) {
            intersectX += dx;
            intersectY += dy;
        }
        gridIndex[HORIZONTAL_WALL] = (int) intersectX % World.GRID_SIZE;
        textureIndex[HORIZONTAL_WALL] = world.charAtPos((int) intersectX, intersectY) - '0';
        // Faster to calculate length with trigonometry than pythogorean.
        return (int) Math.abs((x - intersectX) / Math.cos(direction));
    }

    private int leftOrRight(double direction) {
        if (Math.cos(direction) < 0) {
            return -1;
        } else {
            return 1;
        }
    }

    private int upOrDown(double direction) {
        if (Math.sin(direction) < 0) {
            return -1;
        } else {
            return 1;
        }
    }
}
