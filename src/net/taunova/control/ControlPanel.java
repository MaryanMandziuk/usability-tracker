/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.taunova.control;

import net.taunova.util.Position;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.taunova.control.listeners.CleanTrackerListener;
import net.taunova.control.listeners.StartButtonListener;
import net.taunova.trackers.MouseTracker;
import net.taunova.trackers.TrackerFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 *
 * @author maryan
 */
public class ControlPanel extends JPanel implements ActionListener {
    private MouseTracker tracker;
    private TrackerFrame frame;
    public JButton button5;
    public boolean start = false;
    public JFileChooser fc;
    private final Logger logger = LoggerFactory.getLogger(ControlPanel.class);
    
    public ControlPanel(MouseTracker tracker, TrackerFrame frame) {
        super(true);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.tracker = tracker;
        this.frame = frame;
        
        JButton button1 = new JButton("Clear");
        JButton button2 = new JButton("Mark area");
        JButton button3 = new JButton("New slide");
        JButton button4 = new JButton("Take snapshot");
        button5 = new JButton("Start");
        
        add(button5);
        add(button1);
        add(button2);
        add(button3);
        add(button4);
        
        button1.addActionListener(new CleanTrackerListener(tracker));
        button4.addActionListener(this);
        button5.addActionListener(new StartButtonListener(frame));
      
        fc = new JFileChooser();
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "png"));

    }     

    @Override
    public void actionPerformed(ActionEvent ae) {
        this.frame.setVisible(false);
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException ex) {
            logger.error("Error: " + ex);
        }

        
        BufferedImage image = takeSnapShot();

        this.frame.setVisible(true);
        drawTrack(image);
        try {
            saveScreen(image, "imageName");
        } catch (IOException ex) {
            logger.error("Error: " + ex);
        }
    }
    
    private BufferedImage takeSnapShot() {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage im = null;
        try {
            im = new Robot().createScreenCapture(screenRect);
        } catch (AWTException ex) {
           logger.error("Error: " + ex);
        }

        return im;
    }
    
    private void drawTrack(BufferedImage image) {
        List<Position> positionList = this.tracker.getPosition();
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.red);
        for(int i = 0; i < positionList.size() - 1; i++) {
            g2.drawLine((int)(positionList.get(i).position.x), 
                           (int)(positionList.get(i).position.y), 
                           (int)(positionList.get(i + 1).position.x), 
                           (int)(positionList.get(i + 1).position.y));
            if(positionList.get(i).getDelay() > 10) {
                int radius = positionList.get(i).getDelay();                    
                if(radius > 50) {
                    radius = 50;
                }

                g2.drawOval((int)(positionList.get(i).position.x)-radius/2, 
                       (int)(positionList.get(i).position.y)-radius/2, radius, radius);
            }
        }
        g2.dispose();
    }
    
    private void saveScreen(BufferedImage image, String name) throws IOException {
        
        int returnVal = fc.showSaveDialog(ControlPanel.this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            String format = "png";
            File file = fc.getSelectedFile();
            if (!file.getName().endsWith(".png")) {
                file = new File(fc.getSelectedFile() + ".png");
            }
            ImageIO.write(image, format, file);
            JOptionPane.showMessageDialog(this,
            "Image saved");
        }
    }
}


