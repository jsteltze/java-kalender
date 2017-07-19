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

import de.jsteltze.calendar.UI.CalendarGUI;

/**
 * Refresh calendar canvas.
 * @author Johannes Steltzer
 *
 */
public class CalendarRefreshTask 
    extends TimerTask 
    implements Runnable {
    
    /** Calendar to refresh. */
    private CalendarGUI c;

    /**
     * Construct a new refresher.
     * @param c - Calendar to refresh
     */
    public CalendarRefreshTask(CalendarGUI c) {
        super();
        this.c = c;
    }

    @Override
    public void run() {
        c.update();
    }
}