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

package de.jsteltze.calendar.applet;

import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JApplet;
import javax.swing.UIManager;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.UI.CalendarGUI;
import de.jsteltze.calendar.UI.CalendarPanel;
import de.jsteltze.calendar.config.enums.View;
import de.jsteltze.calendar.frames.CalendarFrame;
import de.jsteltze.common.Log;

/**
 * Java-Applet for use within browsers.
 * @author Johannes Steltzer
 *
 */
public class CalendarApplet 
    extends JApplet
    implements CalendarGUI {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Parent calendar object. */
    private Calendar calendar;
    
    /** Main GUI component. */
    private CalendarPanel calendarPanel;
    
    /** Logger. */
    private static Logger logger = Log.getLogger(CalendarApplet.class);

    /**
     * Construct a new browser applet.
     */
    public void init() {
        calendar = new Calendar(new Dimension(-1, -1), View.month, true, ".");
        calendar.setApplet(this);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "cannot set UI...", e);
        }
        
        calendarPanel = new CalendarPanel(View.month, calendar, true);
        add(calendarPanel);
    }
    
    /**
     * One or more dates has been selected.
     * Remove selection and throw a message.
     */
    public void newSelection() {
        putMessage("IN DIESEM ONLINE-APPLET KÖNNEN KEINE EREIGNISSE GEÄNDERT WERDEN");
    }
    
    @Override
    public void updateStatusBar() {
        calendarPanel.updateStatusBar();
    }
    
    @Override
    public void update() {
        calendarPanel.update();
    }
    
    @Override
    public CalendarFrame getFrame() {
        return null;
    }
    
    @Override
    public CalendarApplet getApplet() {
        return this;
    }
    
    @Override
    public void putMessage(String msg) {
        calendarPanel.putInfoMessage(msg);
    }
    
    @Override
    public void shutdown() {
        calendarPanel.shutdown();
    }
}
