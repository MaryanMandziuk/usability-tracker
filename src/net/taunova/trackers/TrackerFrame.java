package net.taunova.trackers;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.taunova.control.ControlPanel;
import net.taunova.util.Grid;

/**
 *
 * @author maryan
 */
public class TrackerFrame extends JFrame {
    ControlPanel buttonPanel;
    public TrackerPanel trackerPanel;
    public MouseTracker tracker;

    private static final int DIVIDER = 2;

    public ColorTracker colorTracker = new ColorTracker();
    public boolean startThread = false;
    public boolean stopTrackWhileDeactivated = false;
    public boolean start;
    public Thread thread;
    private int deactivatedCount = 0;
    public int widthBased;
    public int heightBased;
    public int widthPrevious;
    public int heightPrevious;


    public TrackerFrame() {
        super("Tracker frame");
        getContentPane().setLayout(new BorderLayout());
        Rectangle dim = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        int baseWidth = dim.width / DIVIDER;
        int baseHeight = dim.height / DIVIDER;
        int buttonPanelWidth = 150;
        Grid grid = new Grid(Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height, 30, 3);
        tracker = new MouseTracker(this, colorTracker, grid);
        buttonPanel = new ControlPanel(tracker, this);
        trackerPanel = new TrackerPanel(tracker);
        buttonPanel.setPreferredSize(new Dimension(120,
                                       baseHeight));
        trackerPanel.setPreferredSize(new Dimension(baseWidth, baseHeight));
        getContentPane().add(BorderLayout.EAST, buttonPanel);
        getContentPane().add(BorderLayout.CENTER, trackerPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(baseWidth + buttonPanelWidth, baseHeight);
        setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent arg0) {
                clearButtonActivate();
                buttonPanel.startButton.setText("Start");
                if(startThread) {
                    if (colorTracker.isSwitchColor()) {
                        colorTracker.nextColor();
                        colorTracker.setSwitchColor(false);
                    } else {
                        colorTracker.nextColor();
                        colorTracker.setSwitchColor(true);
                    }
                }
                tracker.frameActive = true;
            }

            public void windowDeactivated(WindowEvent e) {
                clearButtonActivate();
                tracker.frameActive = false;
                buttonPanel.startButton.setText("Pause");
                int deactivatedTimes = 3;
                if(stopTrackWhileDeactivated) {
                    deactivatedCount++;
                        if (deactivatedCount == deactivatedTimes) {
                        tracker.setTrack(true);
                        stopTrackWhileDeactivated = false;
                    }

                }
                if (start) {
                    if (startThread && !thread.isAlive()) {
                        tracker.setTrack(true);
                        thread.start();
                    } else {
                        startThread = true;
                    }
                }
            }

        });

        widthBased = getSize().width;
        heightBased = getSize().height;
        widthPrevious = getSize().width;
        heightPrevious = getSize().height;

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                    int currentWidth = e.getComponent().getWidth();
                    int currentHeight = e.getComponent().getHeight();
                    if (currentWidth != widthPrevious && currentHeight != heightPrevious) {
                        int h = (int) (currentWidth / (widthBased / 100.0));
                        int height = (int) (h * (heightBased / 100.0));
                        setSize(currentWidth, height);
                    } else if (currentWidth != widthPrevious) {
                        int h = (int) (currentWidth / (widthBased / 100.0));
                        int height = (int) (h * (heightBased / 100.0));
                        setSize(currentWidth, height);
                    } else if (currentHeight != heightPrevious) {
                        int w = (int) (currentHeight / (heightBased / 100.0));
                        int width = (int) (w * (widthBased / 100.0));
                        setSize(width, currentHeight);
                    }



                widthPrevious = e.getComponent().getWidth();
                heightPrevious = e.getComponent().getHeight();
            }
        });


        this.addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent arg0) {
                 if ((arg0.getNewState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH){
                    setExtendedState(JFrame.NORMAL);
                }
            }
        });
    }

    public void dropDeactivetedCount() {
        this.deactivatedCount = 0;
    }

    private void clearButtonActivate() {
        if (tracker.getPosition().isEmpty()) {
            buttonPanel.cleanButton.setEnabled(false);
            buttonPanel.takeSnapShotButton.setEnabled(false);
            buttonPanel.newSlideButton.setEnabled(false);
        } else {
            buttonPanel.cleanButton.setEnabled(true);
            buttonPanel.takeSnapShotButton.setEnabled(true);
            buttonPanel.newSlideButton.setEnabled(true);
        }
    }
}

