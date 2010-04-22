package controllers;

import environment.Settings;
import exceptions.CorruptSettingsException;
import exceptions.NoSuchSettingException;

import java.awt.event.KeyEvent;
import java.util.LinkedList;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * Manages the engine-wide settings by messaging all of it's listeners.
 * Uses the singleton pattern and is therefore globally available.
 */
public class SettingsController {
    private static final SettingsController singleton = new SettingsController();
    private final LinkedList<ContainsSettings> settingsListeners;
    private Settings settings;
    private final Settings defaults;

    /**
     * Returns a singleton instance of the SettingsController.
     * @return The one and only SettingsController.
     */
    public static SettingsController getInstance() {
        return singleton;
    }

    /**
     * Adds another ContainsSettings to listen for changes.
     *
     * @param settingsListener the listener
     */
    public void addListener(ContainsSettings settingsListener) {
        // The main window gets added first and must be updated last to update the window correctly.
        settingsListeners.addFirst(settingsListener);
        settingsListener.updateSettings(settings);
    }

    /**
     * Removes all listeners from the list. Used when the engine reinits.
     */
    public void clearSettingsListeners() {
        settingsListeners.clear();
    }

    /**
     * @return the current settings in use.
     */
    public Settings getCurrentSettings() {
        return settings;
    }

    /**
     * Updates the current settings to all listeners.
     */
    public void updateSettings() {
        for (ContainsSettings cs: settingsListeners) {
            cs.updateSettings(settings);
        }
    }

    /**
     * Updates the listeners with the given settings.
     *
     * @param settings the new settings.
     * @throws exceptions.CorruptSettingsException if the settings are incorrect.
     */
    public void updateSettings(Settings settings) throws CorruptSettingsException {
        this.settings = settings;
        checkSettings(settings);
        updateSettings();
    }

    /**
     * Used for finding the default value for an option.
     * @param key the key searched.
     * @return the default setting for the key or null if key not found.
     * @throws exceptions.NoSuchSettingException
     */
    public int getDefaultSetting(String key) throws NoSuchSettingException {
        if (defaults.containsKey(key)) {
            return defaults.get(key);
        } else {
            throw new NoSuchSettingException("No such setting with key: " + key);
        }
    }

    private void checkSettings(Settings settings) throws CorruptSettingsException {
        for (String key : settings) {
            if (!defaults.containsKey(key)) {
                throw new CorruptSettingsException("Invalid key \"" + key + "\" in settings.");
            }
        }
    }

    private void setDefaults() {
        defaults.put("MAX_FPS", 100);
        defaults.put("MS_PER_TICK", 25);
        defaults.put("RESOLUTION_X", 640);
        defaults.put("RESOLUTION_Y", 480);
        defaults.put("KEY_UP", KeyEvent.VK_UP);
        defaults.put("KEY_DOWN", KeyEvent.VK_DOWN);
        defaults.put("KEY_LEFT", KeyEvent.VK_LEFT);
        defaults.put("KEY_RIGHT", KeyEvent.VK_RIGHT);
        defaults.put("KEY_STRAFE_LEFT", KeyEvent.VK_A);
        defaults.put("KEY_STRAFE_RIGHT", KeyEvent.VK_D);
        defaults.put("KEY_LOOK_UP", KeyEvent.VK_W);
        defaults.put("KEY_LOOK_DOWN", KeyEvent.VK_S);
        defaults.put("PIXELS_PER_SQUARE", 32);
        defaults.put("GRID_SIZE", 1024);
        defaults.put("FOV", 60);
        defaults.put("WALL_TEXTURES", 3);
        defaults.put("VIEW_DISTANCE", 3);
        defaults.put("SHOW_FPS", 1);
    }

    private SettingsController() {
        settingsListeners = new LinkedList<ContainsSettings>();
        defaults = new Settings();
        setDefaults();
        settings = defaults;
    }

}
