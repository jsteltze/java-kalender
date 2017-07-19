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

package de.jsteltze.calendar.config.enums;

/**
 * An enumeration of all supported remind options.
 * @author Johannes Steltzer
 * 
 */
public enum RemindOption {
    
    /** No reminder. */
    noRemind("no", "Gar nicht", -1L),
    
    /** Remind when the events begins. */
    atBegin("begin", "Bei Beginn", 0),
    
    /** Remind 5 minutes prior to event begin. */
    before5min("5min", "5 Minuten vorher", 5L),
    
    /** Remind 10 minutes prior to event begin. */
    before10min("10min", "10 Minuten vorher", 10L),
    
    /** Remind 15 minutes prior to event begin. */
    before15min("15min", "15 Minuten vorher", 15L),
    
    /** Remind 30 minutes prior to event begin. */
    before30min("30min", "30 Minuten vorher", 30L),
    
    /** Remind 1 hour prior to event begin. */
    before1h("1h", "1 Stunde vorher", 60L),
    
    /** Remind 2 hours prior to event begin. */
    before2h("2h", "2 Stunden vorher", 120L),
    
    /** Remind 3 hours prior to event begin. */
    before3h("3h", "3 Stunden vorher", 180L),
    
    /** Remind 4 hours prior to event begin. */
    before4h("4h", "4 Stunden vorher", 240L),
    
    /** Remind 5 hours prior to event begin. */
    before5h("5h", "5 Stunden vorher", 300L),
    
    /** Remind 1 day before event begin. */
    before1d("1d", "1 Tag vorher", 1L),
    
    /** Remind 2 days before event begin. */
    before2d("2d", "2 Tage vorher", 2L),
    
    /** Remind 3 days before event begin. */
    before3d("3d", "3 Tage vorher", 3L),
    
    /** Remind 4 days before event begin. */
    before4d("4d", "4 Tage vorher", 4L),
    
    /** Remind 5 days before event begin. */
    before5d("5d", "5 Tage vorher", 5L),
    
    /** Remind 6 days before event begin. */
    before6d("6d", "6 Tage vorher", 6L),
    
    /** Remind 1 week before event begin. */
    before1w("1w", "1 Woche vorher", 7L),
    
    /** Remind 10 days before event begin. */
    before10d("10d", "10 Tage vorher", 10L),
    
    /** Remind 2 weeks before event begin. */
    before2w("2w", "2 Wochen vorher", 14L),
    
    /** Remind 3 weeks before event begin. */
    before3w("3w", "3 Wochen vorher", 21L),
    
    /** Remind 1 month before event begin. */
    before1m("1m", "30 Tage vorher", 30L),
    
    /** Remind 2 months before event begin. */
    before2m("2m", "60 Tage vorher", 60L),
    
    /** Remind 3 months before event begin. */
    before3m("3m", "90 Tage vorher", 90L);
    
    /** Short and normal description of the remind option. */
    private final String shortName, name;
    
    /** 
     * The remind quantifier. This is the number of minutes/days 
     * prior to the start of an event for reminding. 
     */
    private final long quantifier;
    
    /**
     * Construct a remind option.
     * @param shortName - the short name that will be written to the config
     * @param name - the remind description that will appear in GUIs
     * @param quantifier - the remind quantifier in minutes/days. This is the number
     * of minutes/days prior to the start of an event for reminding.
     */
    private RemindOption(String shortName, String name, long quantifier) {
        this.shortName = shortName;
        this.name = name;
        this.quantifier = quantifier;
    }

    /**
     * Returns the remind name.
     * @param shortForm - True for the short form (for use in the config); false
     * for the human description of the reminder
     * @return the remind name.
     */
    public String getName(boolean shortForm) {
        return shortForm ? shortName : name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    /**
     * Returns the remind quantifier. This is the number
     * of minutes/days prior to the start of an event for reminding.
     * @return the remind quantifier (in minutes or days).
     */
    public long getQuantifier() {
        return quantifier;
    }
    
    /**
     * Returns the remind option of a corresponding short name.
     * @param shortName - Short name (e.g. from config)
     * @return the corresponding remind option.
     */
    public static RemindOption getFromShort(String shortName) {
        RemindOption[] all = RemindOption.values();
        for (RemindOption ro : all) {
            if (ro.getName(true).equals(shortName)) {
                return ro;
            }
        }
        
        // default
        return before1d;
    }
}
