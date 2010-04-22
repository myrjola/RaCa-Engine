package renderer;

import environment.Entity;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * Predicts the position and direction of an Entity using interpolation.
 */
class Predictor {
    private int x;
    private int y;
    private double direction;

    /**
     * @return the predicted x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * @return the predicted y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * @return the predicted direction.
     */
    public double getDirection() {
        return direction;
    }

    /**
     * Performs the prediction.
     * @param entity to be predicted.
     * @param interpolation tells at which place in the game logic tick the prediction is done.
     */
    public void predict(Entity entity, double interpolation) {
        // Predict entity direction.
        direction = entity.directionChange * interpolation + entity.direction;
        // Predict entity position.
        x = (int) (entity.x + (entity.x - entity.newX) * interpolation);
        y = (int) (entity.y + (entity.y - entity.newY) * interpolation);
    }
}
