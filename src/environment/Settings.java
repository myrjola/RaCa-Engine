package environment;

import controllers.SettingsController;
import exceptions.NoSuchSettingException;

import java.util.HashMap;
import java.util.Iterator;

/**
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * A container class for the engine-wide settings.
 */
public class Settings implements Iterable<String> {
    private final HashMap<String, Integer> settingsMap;

    /**
     * Simple constructor.
     */
    public Settings() {
        settingsMap = new HashMap<String, Integer>();
    }

    /**
     * Returns an iterator for the settings' keys.
     * @return ditto.
     */
    public Iterator<String> iterator() {
        return settingsMap.keySet().iterator();
    }

    /**
     * Performs a lookup for the given key.
     *
     * @param key the key to be searched for.
     * @return the corresponding value or null if key not found.
     */
    public int get(String key) {
        Integer value = settingsMap.get(key);
        if (value == null) { // Key not found.
            // Returns the default value for key.
            try {
                return SettingsController.getInstance().getDefaultSetting(key);
            } catch (NoSuchSettingException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    /**
     * Assigns a value to a key. The old value is replaced.
     *
     * @param key
     * @param setting
     */
    public void put(String key, int setting) {
        settingsMap.put(key, setting);
    }

    /**
     * Checks if the given key is contained in the map.
     * @param key
     * @return true if the key is contained.
     */
    public boolean containsKey(String key) {
        return settingsMap.containsKey(key);
    }
}
