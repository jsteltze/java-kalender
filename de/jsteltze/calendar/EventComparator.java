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

import java.util.Comparator;

import de.jsteltze.common.calendar.Date.PrintFormat;

/**
 * Comparison of two events.
 * Events will by sorted with the following priorities:
 * <li>by length (number of days), longest events first
 * <li>by type, holidays first
 * <li>by time, events without time first, then early to late events first
 * @author Johannes Steltzer
 *
 */
public class EventComparator implements Comparator<Event> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Event o1, Event o2) {
        if (o1.getEndDate() == null && o2.getEndDate() != null) {
            // o1 shorter (by days) than o2
            return 1;
        }
        if (o1.getEndDate() != null && o2.getEndDate() == null) {
            // o1 longer (by days) than o1
            return -1;
        }
        if (o1.getEndDate() != null && o2.getEndDate() != null) {
            // compare the length (by days) of both events 
//            long length1 = o1.getEndDate().dayDiff(o1.getDate());
//            long length2 = o2.getEndDate().dayDiff(o2.getDate());
//            if (length1 > length2) {
//                return -1;
//            } else if (length1 < length2) {
//                return 1;
//            } else {
//                return 0;
//            }
            
            // compare by start date
            long dayDiff = o1.getDate().dayDiff(o2.getDate());
            if (dayDiff < 0) {
                return -1;
            } else if (dayDiff > 0) {
                return 1;
            } else {
                // compare by length
                long length1 = o1.getEndDate().dayDiff(o1.getDate());
                long length2 = o2.getEndDate().dayDiff(o2.getDate());
                if (length1 > length2) {
                    return -1;
                } else if (length1 < length2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
        
        // compare by type (holidays first)
        boolean holiday1 = o1.getType().isHoliday();
        boolean holiday2 = o2.getType().isHoliday();
        if (holiday1 && !holiday2) {
            return -1;
        }
        if (!holiday1 && holiday2) {
            return 1;
        }
        if (holiday1 && holiday2) {
            return 0;
        }
        
        // both events are non-holiday single day events
        return compareByTime(o1, o2);
    }
    
    /**
     * Compares two single day non-holiday events by their time.
     * @param o1 - Event 1
     * @param o2 - Event 2
     * @return 
     * <li>-1 if event 1 has no time or is earlier than event 2
     * <li>0 if both events have no time or the same time
     * <li>1 if event 2 has no time or is earlier than event 1
     */
    private int compareByTime(Event o1, Event o2) {
        if (o1.getDate().hasTime() && !o2.getDate().hasTime()) {
            // o2 has no time but o1 has
            return 1;
        }
        if (!o1.getDate().hasTime() && o2.getDate().hasTime()) {
            // o1 has no time but o2 has
            return -1;
        }
        if (!o1.getDate().hasTime() && !o2.getDate().hasTime()) {
            // both events dont have times
            return 0;
        }
        
        // compare events by time
        String time1 = o1.getDate().print(PrintFormat.HHmm);
        String time2 = o2.getDate().print(PrintFormat.HHmm);
        return time1.compareTo(time2);
    }
}
