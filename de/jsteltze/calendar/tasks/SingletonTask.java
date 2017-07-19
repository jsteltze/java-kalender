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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.jsteltze.calendar.config.Const;
import de.jsteltze.calendar.frames.CalendarFrame;
import de.jsteltze.common.Log;

/**
 * Thread to make sure the calendar is only started once.
 * @author Johannes Steltzer
 *
 */
public class SingletonTask 
    extends Thread {
    
    /** Calendar frame to observe. */
    private CalendarFrame cal;
    
    /** Set to false to stop this thread. */
    private boolean running;
    
    /** Time interval in milliseconds to perform check. */
    private static final long SLEEP_INTERVAL = 1000L;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(SingletonTask.class);
    
    /**
     * Construct a new thread to make sure the calendar
     * is only started once.
     * @param c - Calendar frame to observe
     */
    public SingletonTask(CalendarFrame c) {
        super();
        cal = c;
        running = true;
        
        try {
            /*
             * Create lock file
             */
            LOG.fine("create Lock");
            new File(c.getPath(Const.LOCKFILE)).createNewFile();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "cannot create lock file...", e);
        }
    }
    
    /**
     * Stop this thread.
     */
    public void stopit() {
        this.running = false;
        this.interrupt();
    }
    
    @Override
    public void run() {
        File maxfile = new File(cal.getPath(Const.MAXIMIZEFILE));
        while (running) {
            /*
             * Check for a maximize file
             */
            try {
                if (maxfile.exists()) {
                    LOG.fine("New calendar tries to launch. Maximize.");
                    maxfile.delete();
                    cal.maximize();
                }
                Thread.sleep(SLEEP_INTERVAL);
            } catch (InterruptedException e) {
                LOG.warning("sleep interrupted: " + e.toString());
                return;
            }
        }
    }
}
