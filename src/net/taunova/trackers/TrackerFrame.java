/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.taunova.trackers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.JFrame;
import net.taunova.control.ControlPanel;



/**
 *
 * @author maryan
 */
public class TrackerFrame extends JFrame implements WindowFocusListener {
    ControlPanel buttonPanel;
    public TrackerPanel trackerPanel;
    public MouseTracker tracker;
    public boolean startTracking = false;
    private static final int DIVIDER = 2;
    public TrackerFrame() {
        super("Tracker frame");
        getContentPane().setLayout(new BorderLayout());
        Rectangle dim = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int baseWidth = dim.width/DIVIDER;
        int baseHeight = dim.height/DIVIDER;
        tracker = new MouseTracker(this);
        buttonPanel = new ControlPanel(tracker, this);
        trackerPanel = new TrackerPanel(tracker);
        
        buttonPanel.setPreferredSize(new Dimension(120,
                                       baseHeight));
        trackerPanel.setPreferredSize(new Dimension(baseWidth, baseHeight));

        getContentPane().add(BorderLayout.EAST, buttonPanel);
        getContentPane().add(BorderLayout.CENTER, trackerPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(baseWidth+150, baseHeight);
        setVisible(true); 
        addWindowFocusListener(this);
    }


    @Override
    public void windowGainedFocus(WindowEvent we) {
       this.buttonPanel.button5.setText("Start");
       this.startTracking = true;
    }

    @Override
    public void windowLostFocus(WindowEvent we) {
        this.buttonPanel.button5.setText("Pause");
        this.startTracking = false;
    }
}
