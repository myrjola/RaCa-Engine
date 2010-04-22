package tests;


import controllers.SettingsController;
import environment.Settings;
import exceptions.CorruptSettingsException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

public class SettingsControllerTest {

    private Settings settings;

    @Before
    public void init () {
        settings = new Settings();
    }

    @Test
    public void testInvalidSettingsKey() {
        settings.put("Incorrect key.", 0);
        try {
            SettingsController.getInstance().updateSettings(settings);
            Assert.fail("Should have thrown CorruptSettingsException. Used wrong key");
        } catch (CorruptSettingsException e) {
            // This was expected.
        }
    }

    @Test
    public void testNoSuchSetting() {
        try {
            settings.get("No such setting");
            Assert.fail("Should have thrown NullPointerException when casting null Integer to int");
        } catch (NullPointerException e) {
            // Also expected.
        }
    }

    @Test
    public void testPutGet() {
        settings.put("FOV", 60);
        Assert.assertEquals("Put and get not working.", settings.get("FOV"), 60);
    }
}
