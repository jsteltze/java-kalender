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

import java.util.logging.Logger;

import javax.swing.JLabel;

import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.frames.Notification;
import de.jsteltze.common.Log;
import de.jsteltze.common.calendar.Date;

/**
 * Task to refresh the header of a launched notification each minute.
 * The Headers shows the time left until the events starts.
 * This time has to be refreshed each minute.
 * @author Johannes Steltzer
 *
 */
public class RefreshTimeLabelTask 
    extends Thread {
    
    /** Notification frame. */
    private Notification noti;
    
    /** Time label. */
    private JLabel text;
    
    /** Event of interest. */
    private Event event;
    
    /** Stop thread flag. */
    private boolean stop;
    
    /** Add a trailing colon. */
    private boolean addColon;
    
    /** Logger. */
    private static Logger logger = Log.getLogger(RefreshTimeLabelTask.class);
    
    /**
     * Construct a new refresher thread. This will recalculate the
     * time difference to the event each minute and refresh the
     * timeLabel.
     * @param noti - Notification frame to refresh
     * @param addColon - add a trailing colon
     */
    public RefreshTimeLabelTask(Notification noti, boolean addColon) {
        this.noti = noti;
        this.text = null;
        this.event = noti.getEvent();
        this.stop = false;
        this.addColon = addColon;
    }
    
    /**
     * Construct a new refresher thread. This will recalculate the
     * time difference to the event each minute and refresh the
     * timeLabel.
     * @param text - JLabel which text to refresh
     * @param event - Event of interest
     */
    public RefreshTimeLabelTask(JLabel text, Event event) {
        this.text = text;
        this.noti = null;
        this.event = event;
        this.stop = false;
        this.addColon = true;
    }
    
    @Override
    public void run() {
        String upperString;
        Date date = event.getNextDate();
        
        /*
         * Initial sleep to fill the full minute
         */
        Date dateNow = new Date();
        try {
            Thread.sleep(Date.MIN_1 - dateNow.get(Date.SECOND) * Date.SEC_1);
        } catch (InterruptedException e) {
            return;
        }
        
        while (!stop) {
            logger.fine("Refresh timeLabel...");
            
            long minDiff = date.minDiff();
            long hours = minDiff / Date.MINS_OF_HOUR;
            long minut = minDiff - hours * Date.MINS_OF_HOUR;

            if (minDiff < 0) {
                upperString = "vor " + (hours == 0 ? "" : (-hours) + "h ") + (-minut) + "min";
            } else if (minDiff > 0) {
                upperString = "in " + (hours == 0 ? "" : (hours) + "h ") + (minut) + "min";
            } else {
                upperString = "JETZT";
            }
            
            if (text == null) {
                noti.refreshTimeLabel(upperString + (addColon ? ":" : ""));
            } else {
                upperString += ": ";
                text.setText(upperString);
            }
            
            /* Sleep one minute */
            try {
                Thread.sleep(Date.MIN_1);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
    
    /**
     * Stop this Thread. This will stop the refreshments.
     */
    public void quit() {
        this.stop = true;
    }
}
