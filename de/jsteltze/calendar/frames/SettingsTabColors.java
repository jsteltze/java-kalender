/**
 *  java-kalender - Java Calendar for Germany
 *  Copyright (C) 2012  Johannes Steltzer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.jsteltze.calendar.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.config.ColorSet;
import de.jsteltze.common.ColorChooserListener;
import de.jsteltze.common.ColorChooserPanel;
import de.jsteltze.common.Log;
import de.jsteltze.common.VerticalFlowPanel;
import de.jsteltze.common.ui.Button;

/**
 * Settings frame for calendar configuration: tab for color settings.
 * @author Johannes Steltzer
 *
 */
public class SettingsTabColors 
    extends JPanel 
    implements ActionListener, ItemListener, ColorChooserListener {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Radio buttons for the different color locations. */
    private JRadioButton[] colorRadioButton;
    
    /** Rectangles (as JLabels) displaying the currently selected color. */
    private JLabel[] colorRects;
    
    /** Color chooser panel. */
    private ColorChooserPanel colorChooser;
    
    /** Button: reset all colors to the default values. */ 
    private Button defaultButton = new Button("SettingsDefaultButton", this);
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(SettingsTabColors.class);

    /**
     * Arrange settings tab 3: Colors.
     * @param caller - Parent calendar object (to receive currently configured colors from)
     */
    public SettingsTabColors(Calendar caller) {
        super(new BorderLayout());
        
        JPanel pMain = new JPanel(new BorderLayout());
//        JPanel pC = new JPanel(new GridLayout(1, 1));
        VerticalFlowPanel pRadiosAll = new VerticalFlowPanel(4);

        colorRadioButton = new JRadioButton[ColorSet.MAXCOLORS];
        ButtonGroup gruppe = new ButtonGroup();
        colorRects = new JLabel[ColorSet.MAXCOLORS];
        for (byte i = 0x00; i < ColorSet.MAXCOLORS; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            colorRadioButton[i] = new JRadioButton(ColorSet.NAMES[i]);
            colorRadioButton[i].addItemListener(this);
            gruppe.add(colorRadioButton[i]);
            colorRects[i] = new JLabel("lalabla");
            colorRects[i].setOpaque(true);
            colorRects[i].setBackground(caller.getConfig().getColors()[i]);
            colorRects[i].setForeground(caller.getConfig().getColors()[i]);
            colorRects[i].setBorder(new EtchedBorder());
            row.add(colorRadioButton[i]);
            row.add(colorRects[i]);
            pRadiosAll.add(row);
        }
        
        colorChooser = new ColorChooserPanel(this, colorRects[0].getBackground());
        
        // Set the first selected, but prevent the item listener from firing
        colorRadioButton[0].removeItemListener(this);
        colorRadioButton[0].setSelected(true);
        colorRadioButton[0].addItemListener(this);
        
        JScrollPane jsp = new JScrollPane(pRadiosAll);
        pMain.add(colorChooser, BorderLayout.WEST);
        pMain.add(jsp, BorderLayout.CENTER);
        pMain.setBorder(new EmptyBorder(5, 5, 5, 5));
        pMain.setOpaque(false);
        
        add(pMain, BorderLayout.CENTER);
        add(defaultButton.getPanel(new FlowLayout(FlowLayout.RIGHT)), BorderLayout.SOUTH);
        setOpaque(false);
    }

    /**
     * For tab "Colors": Returns the index of the selected check box.
     * @return Selected index.
     */
    private int getSelectedColorIndex() {
        for (int i = 0; i < ColorSet.MAXCOLORS; i++) {
            if (colorRadioButton[i].isSelected()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * The color in the colorChooser has been changed. This will
     * update the colorRect and the R,G,B-textFields properly. 
     * @param c - Color that has been chosen
     */
    public void colorChosen(Color c) {
        colorRects[getSelectedColorIndex()].setForeground(c);
        colorRects[getSelectedColorIndex()].setBackground(c);
    }
    
    /**
     * Returns the currently selected colors. See class ColorSet for index details.
     * @return the currently selected colors.
     */
    public Color[] getColorsChosen() {
        /* get colors */
        Color[] colors = new Color[ColorSet.MAXCOLORS];
        for (int i = 0; i < ColorSet.MAXCOLORS; i++) {
            colors[i] = colorRects[i].getBackground();
        }
        
        return colors;
    }

    @Override
    public void actionPerformed(ActionEvent a) {

        if (a.getSource().equals(defaultButton)) {
            LOG.fine("TAB COLORS");
            for (byte i = 0x00; i < ColorSet.MAXCOLORS; i++) {
                colorRects[i].setForeground(ColorSet.DEFAULT[i]);
                colorRects[i].setBackground(ColorSet.DEFAULT[i]);
            }
            colorChooser.setColor(colorRects[getSelectedColorIndex()].getBackground());
        }
    }

    @Override
    public void itemStateChanged(ItemEvent i) {
        int selectedColor = getSelectedColorIndex();
        if (selectedColor == -1) {
            return;
        }
        Color color = colorRects[selectedColor].getBackground();
        colorChooser.setColor(color);
    }
}
