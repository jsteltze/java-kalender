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

package de.jsteltze.calendar.tasks;

import java.util.TimerTask;

import de.jsteltze.calendar.frames.CalendarFrame;

/**
 * Task to change the window (and tray) icon in case of open notifications.
 * @author Johannes Steltzer
 *
 */
public class ChangeIconTask 
    extends TimerTask 
    implements Runnable {

    /** Calendar frame that is subject of icon changes. */
    private CalendarFrame caller;

    /**
     * Construct a new task for icon changes.
     * @param c - Calendar frame that shall be subject of
     *         icon changes 
     */
    public ChangeIconTask(CalendarFrame c) {
        super();
        caller = c;
    }

    @Override
    public void run() {
        caller.changeIcon();
    }
}