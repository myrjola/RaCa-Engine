package controllers;

import environment.Settings;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * An interface to be used by the SettingsController for updating settings.
 */
public interface ContainsSettings {
    /** Updates settings immediately from the given Settings object.
     *
     * @param settings The given settings to update.
     */
    public void updateSettings(Settings settings);
}
