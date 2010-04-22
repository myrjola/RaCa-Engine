package renderer;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * The common interface for the components in the renderer.
 */
public interface RendererComponent {
    /**
     * Performs the component's task. For example the RayCaster calculates the distances to the walls.
     * @param common gives access to the global variables and some utility functions.
     */
    public void update(RendererCommon common);
}

