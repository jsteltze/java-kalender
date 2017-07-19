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
import java.util.logging.Logger;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.frames.Notification;
import de.jsteltze.common.Log;

/**
 * Task thread for a notification.
 * @author Johannes Steltzer
 *
 */
public class AlarmTask 
    extends TimerTask 
    implements Runnable {

    /** Subject of notification. */
    private Event event;

    /** Parent calendar object. */
    private Calendar caller;
    
    /** Scheduled time in milliseconds. */
    private long scheduledTime;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(AlarmTask.class);

    /**
     * Construct a new alarm task.
     * @param c - Parent calendar object
     * @param e - Event to notify of
     * @param scheduledTime - Scheduled time in milliseconds
     */
    public AlarmTask(Calendar c, Event e, long scheduledTime) {
        super();
        this.event = e;
        this.caller = c;
        this.scheduledTime = scheduledTime;
    }

    /**
     * Returns the event that is subject of this AlarmTask.
     * @return the event that is subject of this AlarmTask.
     */
    public Event getEvent() {
        return this.event;
    }

    /**
     * Set a new event for this AlarmTask.
     * @param x - New event to apply
     */
    public void setEvent(Event x) {
        this.event = x;
    }

    @Override
    public void run() {
        LOG.fine("fire alarm task for event: " + event.getName());
        caller.removeAlarmTask(this);
        new Notification(caller, event);
    }
    
    @Override
    public long scheduledExecutionTime() {
        return this.scheduledTime;
    }
}