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

package de.jsteltze.calendar.UI;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.EventCategories;
import de.jsteltze.calendar.Frequency;
import de.jsteltze.common.Log;
import de.jsteltze.common.calendar.Date.PrintFormat;

/**
 * Graphical panel of one calendar event.
 * @author Johannes Steltzer
 *
 */
public class EventPanel {
    
    /** Event of subject. */
    private Event event;
    
    /** Is this event selected on the gui? */
    private boolean selected = false;
    
    /** Is this event transparent on the gui? */
    private boolean transparent = false;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(EventPanel.class);
    
    /**
     * Construct a new event panel.
     * @param event - Event to be displayed in the GUI
     */
    public EventPanel(Event event) {
        this.event = event;
    }
    
    /**
     * Returns whether or not this event is selected on the gui.
     * @return True if this event is currently selected on the gui.
     */
    public boolean isSelected() {
        return this.selected;
    }
    
    /**
     * Returns whether or not this event is transparent on the gui.
     * @return True if this event is currently transparent on the gui.
     */
    public boolean isTransparent() {
        return this.transparent;
    }
    
    /**
     * Set the selected flag of this event.
     * @param x - True for selected state, false otherwise
     */
    public void setSelected(boolean x) {
        this.selected = x;
    }
    
    /**
     * Set the transparent flag of this event.
     * @param x - True for transparent state, false otherwise
     */
    public void setTransparent(boolean x) {
        this.transparent = x;
    }
    
    
    /**
     * Create a JLabel icon that opens this events attachment
     * (if applicable).
     * @param workspace - Workspace to look for an attachment
     * @return Icon as JLabel if attachment found, null otherwise.
     */
    public JLabel getAttachmentIcon(final String workspace) {
        if (event.getAttachment(workspace) == null) {
            return null;
        }
        
        JLabel img = new JLabel(new ImageIcon(
                Event.class.getResource("/media/attachment20.png")));
        img.setToolTipText("Anhang: " + event.getAttachment(workspace).getName());
        img.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent m) {
                try {
                    Desktop.getDesktop().open(event.getAttachment(workspace));
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "[mouseClicked] error while trying to open attached file...", e);
                    GUIUtils.showErrorMessage("Der Anhang kann nicht geöffnet werden!", 
                            "Anhang kann nicht geöffnet werden", e);
                }
            }

            @Override
            public void mouseEntered(MouseEvent m) {
                m.getComponent().setCursor(
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent m) {
                m.getComponent().setCursor(
                        Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
        return img;
    }

    /**
     * Returns the tool tip for this event to be displayed on mouse hover.
     * @return the tool tip for this event to be displayed on mouse hover.
     */
    public String getToolTip() {
        final int fontSize = 12;
        
        String freqLabel = Frequency.getLabel(event.getFrequency(), event.getDate());
        String toolTip = "<html><body align='center' color='gray'>";
        
        // add day diff label
        toolTip += event.getNextDate().print(PrintFormat.DAY_DIFF) + ":";
        
        toolTip += "<p style='font-size:" + fontSize + "px; color: black;'>";
        
        // add event time
        if (event.getDate().hasTime()) {
            toolTip += event.getDate().print(PrintFormat.Hmm_Uhr) + ": ";
        }
        
        // add event icon
        if (event.getCategory() != null) {
            toolTip += EventCategories.getIconAsHTML(event.getCategory(), fontSize + 2) + "&nbsp;";
        }
        
        // add event name
        toolTip += "<b>" + event.getName() + "</b></p>";
        
        toolTip += "(";
        if (!freqLabel.isEmpty()) {
            // add frequency description
            toolTip += freqLabel;
        } else {
            if (event.getEndDate() == null) {
                // add date
                toolTip += event.getDate().print(PrintFormat.DDD_DDMMYYYY);
            } else {
                // add date to end date
                if (event.getDate().get(Calendar.MONTH) == event.getEndDate().get(Calendar.MONTH)) {
                    // same month
                    toolTip += event.getDate().get(Calendar.DAY_OF_MONTH) + ". bis " 
                            + event.getEndDate().print(PrintFormat.D_MMM);
                } else {
                    // different month
                    toolTip += event.getDate().print(PrintFormat.D_MMM) + " bis " 
                            + event.getEndDate().print(PrintFormat.D_MMM);
                }
            }
        }
        toolTip += ")";
        
        return toolTip + "</body></html>";
    }
}
