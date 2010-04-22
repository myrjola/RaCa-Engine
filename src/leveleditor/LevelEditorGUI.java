package leveleditor;

import controllers.ContainsSettings;
import controllers.SettingsController;
import environment.Settings;
import environment.World;
import renderer.MapView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

/**
 * The GUI for the level editor.
 */
public class LevelEditorGUI extends JFrame implements MouseListener, ContainsSettings {
    private Settings settings;
    private MapView mapView;
    private final LevelEditor levelEditor;
    private JScrollPane scrollPane;
    private JList objectList;
    private JButton testButton;
    private JButton loadButton;
    private JButton newButton;

    /**
     * Creates the help screen when button clicked.
     */
    private class HelpButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            StringBuffer helpInformationBuffer = new StringBuffer();
            // Print the current key bindings.
            helpInformationBuffer.append("Key Bindings:");
            helpInformationBuffer.append("\nMove up: ");
            helpInformationBuffer.append(KeyEvent.getKeyText(settings.get("KEY_UP")));
            helpInformationBuffer.append("\nMove down: ");
            helpInformationBuffer.append(KeyEvent.getKeyText(settings.get("KEY_DOWN")));
            helpInformationBuffer.append("\nMove left: ");
            helpInformationBuffer.append(KeyEvent.getKeyText(settings.get("KEY_LEFT")));
            helpInformationBuffer.append("\nMove right: ");
            helpInformationBuffer.append(KeyEvent.getKeyText(settings.get("KEY_RIGHT")));
            helpInformationBuffer.append("\nStrafe left: ");
            helpInformationBuffer.append(KeyEvent.getKeyText(settings.get("KEY_STRAFE_LEFT")));
            helpInformationBuffer.append("\nStrafe right: ");
            helpInformationBuffer.append(KeyEvent.getKeyText(settings.get("KEY_STRAFE_RIGHT")));
            helpInformationBuffer.append("\nLook up: ");
            helpInformationBuffer.append(KeyEvent.getKeyText(settings.get("KEY_LOOK_UP")));
            helpInformationBuffer.append("\nLook down: ").append(KeyEvent.getKeyText(settings.get("KEY_LOOK_DOWN")));

            JOptionPane.showMessageDialog(null, helpInformationBuffer.toString());
        }
    }

    /**
     * Opens the new level window, where the user chooses level number, height and width.
     */
    class NewLevelListener implements ActionListener {

        JDialog newLevelDialog;

        JSpinner levelNumberSpinner;
        JSpinner levelWidthSpinner;
        JSpinner levelHeightSpinner;
        SpinnerNumberModel numberModel;
        SpinnerNumberModel widthModel;
        SpinnerNumberModel heightModel;
        JButton createButton;

        public void actionPerformed(ActionEvent e) {
            if (newLevelDialog == null) {
                newLevelDialog = new JDialog(getOwner(), "New Level");
                newLevelDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            }

            JPanel panel = new JPanel(new FlowLayout());

            JLabel levelNumberLabel = new JLabel("Choose level number: ");
            JLabel levelWidthLabel = new JLabel("Level width:");
            JLabel levelHeightLabel = new JLabel("Level height: ");
            numberModel = new SpinnerNumberModel(1, 1, 10000, 1);
            widthModel = new SpinnerNumberModel(10, 3, 100000, 1);
            heightModel = new SpinnerNumberModel(10, 3, 100000, 1);
            levelNumberSpinner = new JSpinner(numberModel);
            levelWidthSpinner = new JSpinner(widthModel);
            levelHeightSpinner = new JSpinner(heightModel);
            createButton = new JButton("Create");
            createButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    levelEditor.newLevel(
                            (Integer) levelNumberSpinner.getValue(),
                            (Integer) levelWidthSpinner.getValue(),
                            (Integer) levelHeightSpinner.getValue());
                    mapView.reInit();
                    scrollPane.updateUI();
                    mapView.update(0);
                    newLevelDialog.dispose();
                    newLevelDialog = null;
                }
            });

            panel.add(levelNumberLabel);
            panel.add(levelNumberSpinner);

            panel.add(levelWidthLabel);
            panel.add(levelWidthSpinner);

            panel.add(levelHeightLabel);
            panel.add(levelHeightSpinner);
            
            panel.add(createButton);

            newLevelDialog.add(panel);
            newLevelDialog.pack();
            newLevelDialog.setVisible(true);
        }
    }

    /**
     * Creates the level editor's window.
     *
     * @param levelEditor the LevelEditor service which is to be interfaced.
     */
    public LevelEditorGUI(LevelEditor levelEditor) {
        this.levelEditor = levelEditor;
        setTitle("RaCa-Engine Level Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        SettingsController.getInstance().addListener(this);
    }

    /**
     * Initializes the window's widgets. The level editor is functional after this.
     *
     * @param world the current World to be edited.
     */
    public void init(World world) {
        final Container contentPane = getContentPane();

        // The editable map
        mapView = new MapView(world);
        mapView.addMouseListener(this);
        scrollPane = new JScrollPane(mapView);

        // The list of objects, which can be added to the level.
        int numberOfTextures = settings.get("WALL_TEXTURES");
        Object objects[] = new Object[numberOfTextures + 2];
        objects[0] = "viewer";
        for (int i = 1; i <= numberOfTextures; i++) {
            objects[i] = "wall" + i;
        }
        objects[numberOfTextures + 1] = "delete";

        objectList = new JList(objects);
        objectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        objectList.setLayoutOrientation(JList.VERTICAL);
        objectList.setVisibleRowCount(-1);
        objectList.setSelectedIndex(0);
        JScrollPane listScroller = new JScrollPane(objectList);

        // Some buttons for saving, loading, testing and creating levels.
        testButton = new JButton("Test");
        testButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                levelEditor.testLevel();
            }
        });
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                levelEditor.saveLevel();
            }
        });
        loadButton = new JButton("Load");
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                levelEditor.loadLevel();
                mapView.reInit();
                scrollPane.updateUI();
                mapView.update(0);
            }
        });
        newButton = new JButton("New");
        newButton.addActionListener(new NewLevelListener());

        // The help button.
        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(new HelpButtonListener());

        // Add the widgets to the window.
        Container leftColumn = new Container();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.add(listScroller);
        leftColumn.add(testButton);
        leftColumn.add(loadButton);
        leftColumn.add(saveButton);
        leftColumn.add(newButton);
        leftColumn.add(helpButton);
        contentPane.setLayout(new BorderLayout());
        contentPane.add(leftColumn, BorderLayout.WEST);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        mapView.update(0);
        pack();
    }

    /**
     * Updates the GUI to engine running state. Disables dangerous buttons.
     */
    public void engineRun() {
        newButton.setEnabled(false);
        loadButton.setEnabled(false);
        testButton.setEnabled(false);
    }

    /**
     * Updates the GUI to engine stopped state. Enables the dangerous buttons again.
     */
    public void engineStopped() {
        newButton.setEnabled(true);
        loadButton.setEnabled(true);
        testButton.setEnabled(true);
    }

    public void updateSettings(Settings settings) {
        this.settings = settings;
    }

    /**
     * Interfaces with the leveleditors mapViewClick. Places or deletes objects in the level.
     *
     * @param e the MouseEvent.
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getComponent() == mapView) {
            int x = e.getX() / mapView.PIXELS_PER_SQUARE;
            int y = e.getY() / mapView.PIXELS_PER_SQUARE;
            levelEditor.mapViewClick(x, y, objectList.getSelectedValue().toString());
            mapView.update(0);
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}


}

