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

import de.jsteltze.calendar.applet.CalendarApplet;
import de.jsteltze.calendar.frames.CalendarFrame;

/**
 * Calendar interface for browser applet or stand-alone frame.
 * @author Johannes Steltzer
 *
 */
public interface CalendarGUI {
    
    /**
     * Update the calendar status bar.
     */
    public void updateStatusBar();
    
    /**
     * Refresh the calendar. Is called when an event is added or
     * removed.
     */
    public void update();
    
    /**
     * 
     * @return Active calendar frame or null in case of java applet.
     */
    public CalendarFrame getFrame();
    
    /**
     * 
     * @return Active calendar applet or null in case of 
     *         frame based java application.
     */
    public CalendarApplet getApplet();
    
    /**
     * Show a message in the status bar.
     * @param msg - Message to put
     */
    public void putMessage(String msg);
    
    /**
     * Gracefully shutdown the program and stop
     * GUI related tasks.
     */
    public void shutdown();
}
