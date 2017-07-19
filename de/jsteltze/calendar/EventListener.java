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

package de.jsteltze.calendar;

/**
 * Interface for the different event actions.
 * @author Johannes Steltzer
 *
 */
public interface EventListener {

    /**
     * Adding a new event.
     */
    public void eventAdding();
    
    /**
     * Removing an event.
     * @param x - Event to remove
     * @param complete - True for complete removal, false for just adding an exception
     */
    public void eventRemoved(Event x, boolean complete);
    
    /**
     * Editing an event.
     * @param x - Event to edit
     */
    public void eventEditing(Event x);
    
    /**
     * Notify about an event.
     * @param x - Event to notify about
     */
    public void eventNotification(Event x);
    
    /**
     * Duplicating an event.
     * @param x - Event to duplicate
     */
    public void eventDuplicating(Event x);
    
    /**
     * Adding or removing a holiday event.
     */
    public void holidayChanging();
}
