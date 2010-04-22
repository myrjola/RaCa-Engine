package controllers;

import environment.Settings;
import environment.World;
import exceptions.CorruptLevelFileException;
import exceptions.CorruptSettingsException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * Handles the file I/O of settings-, level- and imagefiles.
 */
public class FileController {

    /**
     * Open a level-file with a JFileChooser-dialog.
     *
     * @param mainWindow The parent of the dialog.
     * @return the levelMatrix used by a World object or null if no world was chosen.
     * @throws CorruptLevelFileException
     */
    public static char[][] loadWorld(JFrame mainWindow) throws CorruptLevelFileException {
        JFileChooser chooser = new JFileChooser("levels/");
        int returnVal = chooser.showOpenDialog(mainWindow);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String fileName = chooser.getSelectedFile().getAbsolutePath();
            return loadWorld(fileName);
        }
        return null;
    }

    /**
     * Open a level-file with file name.
     *
     * @param fileName The file name.
     * @return the levelMatrix used by a World object.
     * @throws CorruptLevelFileException
     */
    public static char[][] loadWorld(String fileName) throws CorruptLevelFileException {
        try {
            FileReader reader = new FileReader(fileName);
            return loadWorld(reader);
        } catch (FileNotFoundException e) {
            throw new CorruptLevelFileException("File not found:" + fileName);
        }
    }

    public static char[][] loadWorld(Reader reader) throws CorruptLevelFileException {
        try {
            String currentLine;
            BufferedReader lineReader = new BufferedReader(reader);
            currentLine = lineReader.readLine();
            String[] widthAndHeight = currentLine.split(" ");
            if (widthAndHeight.length != 2) {
                throw new CorruptLevelFileException("Corrupt width and height in level file.");
            }
            int width = Integer.parseInt(widthAndHeight[0]);
            int height = Integer.parseInt(widthAndHeight[1]);
            ArrayList<char[]> levelList = new ArrayList<char[]>(height);
            for (int i = 0; i < height; i++) {
                currentLine = lineReader.readLine();
                if (currentLine == null) {
                    throw new CorruptLevelFileException("Incorrect height in header");
                } else if (currentLine.length() != width) {
                    throw new CorruptLevelFileException("Line number " + i + " has incorrect width.");
                }
                levelList.add(currentLine.toCharArray());
            }
            char[][] levelMatrix = new char[height][width];
            levelList.toArray(levelMatrix);
            lineReader.close();
            return levelMatrix;

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(1);
        }
        return null;
    }

    /**
     * Saves the Settings object to the settings.ini file.
     *
     * @param settings The object to be saved.
     */
    private static void saveSettings(Settings settings) {
        try {
            File file = new File("settings.ini");
            FileWriter fw = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fw);
            for (String key: settings) {
                // environment.Settings format: key:value\n
                writer.write(key + ":" + settings.get(key));
                writer.newLine();
            }
            writer.flush();
            writer.close();
           
        } catch (IOException e) {
            System.out.println("Settings file couldn't be saved.\n" +
                    "Maybe incorrect permissions or drive is full");
            e.printStackTrace();
        }
    }

    /**
     * Loads a Settings object from the settings.ini file.
     *
     * @return the Settings object.
     * @throws exceptions.CorruptSettingsException
     */
    public static Settings loadSettings() throws CorruptSettingsException {
        try {
            Settings settings = new Settings();
            File file = new File("settings.ini");
            if (!file.exists()) {
                settings = SettingsController.getInstance().getCurrentSettings();
                saveSettings(settings);
                return settings;
            }
            FileReader fr = new FileReader(file);
            BufferedReader lineReader = new BufferedReader(fr);
            String currentLine = lineReader.readLine();
            while (currentLine != null) {
                currentLine = currentLine.trim();
                if (currentLine.startsWith("#") || currentLine.length() == 0) {
                    // Skip comment lines and empty lines.
                    currentLine = lineReader.readLine();
                    continue;
                }
                String[] keyAndValue = currentLine.split(":");
                settings.put(keyAndValue[0].toUpperCase(), Integer.parseInt(keyAndValue[1]));
                currentLine = lineReader.readLine();
            }
            lineReader.close();
            return settings;

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(1);
        } catch (NullPointerException e) {
            throw new CorruptSettingsException("Key or value missing on a line in the settings file.");
        }
        return null;
    }

    /**
     * Simple image loader.
     *
     * @param fileName The file name.
     * @return an BufferedImage representation of the image.
     */
    public static BufferedImage loadImage(String fileName) {
        try {
            BufferedImage img;
            File f = new File(fileName);
            img = ImageIO.read(f);
            return img;
            
        } catch (IOException e) {
            System.out.println("Image couldn't be loaded. Will terminate.");
            e.printStackTrace();
            System.exit(1);
        }
        return null; // Is required for compile though execution doesn't arrive here.
    }

    /**
     * Saves the world to ${levelNumber}.lvl.
     * @param world the level to be saved
     * @param levelNumber
     * @throws java.io.IOException
     */
    public static void saveWorld(World world, int levelNumber) throws IOException {
        File file = new File("levels/" + levelNumber + ".lvl");
        FileWriter fw = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(fw);
        writer.write(world.getWidth() + " " + world.getHeight() + "\n");
        for (char[] row: world.getLevelMatrix()) {
            writer.write(row);
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }
}
