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

package de.jsteltze.calendar.config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.Event.EventType;
import de.jsteltze.calendar.EventCategories;
import de.jsteltze.calendar.Frequency;
import de.jsteltze.calendar.config.enums.HolidayConstants;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.DateCalculations;

/**
 * Class holding the different holidays supported by
 * the calendar.
 * @author Johannes Steltzer
 *
 */
public final class Holidays {
    
    /**
     * Hidden constructor.
     */
    private Holidays() { }
    
    /** Calculated dates easter sunday and 4th advent to cache. */ 
    private static Date ostersonntag, advent4;
    
    /**
     * Get the number of holidays enabled.
     * @param code - Holidays encoded as integer
     * @return Number of bits set.
     */
    public static int getNumberOfHolidays(int code) {
        final int maxLen = 32;
        int num = 0;
        for (int i = 0; i < maxLen; i++) {
            if ((code & (1 << i)) != 0) {
                num++;
            }
        }
        return num;
    }
    
    /**
     * Calculate easter Sunday. Use Lichtenberg. Do not use Gauss.
     * @param year - Year of interest
     * @return Date of easter Sunday.
     */
    private static Date calculateEasterSunday(int year) {
        Date easter = new Date(year, java.util.Calendar.MARCH, 1);
        easter.add(java.util.Calendar.DAY_OF_MONTH, DateCalculations.easterSunday(year));
        return easter;
    }
    
    /**
     * Get the static (each year same date) holidays by law, according to the configuration byte. 
     * @param config - Config byte where each bit indicates whether or not a holiday is set
     * @param year - Year of interest. According to this number the holidays date will be set
     * @return List of static holidays by law.
     */
    public static List<Event> getStaticByLaw(int config, int year) {
        List<HolidayConstants> staticByLaw = HolidayConstants.getStaticByLaw(config);
        List<Event> holidays = new ArrayList<Event>();
        
        for (HolidayConstants h : staticByLaw) {
            Event e = new Event(h.getDate(year), h.getTitle(), Frequency.OCCUR_YEARLY, true);
            
            // Set holiday category
            switch (h) {
            case WEIH1:
            case WEIH2:
                e.setCategory(EventCategories.CHRISTMAS);
                break;
            default:
                e.setCategory(EventCategories.HOLIDAY);
            }
            
            holidays.add(e);
        }
        
        return holidays;
    }
    
    /**
     * Get the static (each year same date) special holidays, according to the configuration byte. 
     * @param config - Config byte where each bit indicates whether or not a holiday is set
     * @param year - Year of interest. According to this number the holidays date will be set
     * @return List of static special holidays.
     */
    public static List<Event> getStaticSpecial(int config, int year) {
        List<HolidayConstants> staticByLaw = HolidayConstants.getStaticSpecial(config);
        List<Event> holidays = new ArrayList<Event>();
        
        for (HolidayConstants h : staticByLaw) {
            Event e = new Event(h.getDate(year), h.getTitle(), Frequency.OCCUR_YEARLY, false);
            
            // Set holiday category
            switch (h) {
            case HALLOWEEN:
                e.setCategory(EventCategories.HALLOWEEN);
                break;
            case WALPURGIS:
                e.setCategory(EventCategories.WALPURGIS);
                break;
            case VALENTIN:
                e.setCategory(EventCategories.VALENTINE);
                break;
            case NIKO:
            case HEILIGA:
                e.setCategory(EventCategories.CHRISTMAS);
                break;
            default:
                e.setCategory(EventCategories.HOLIDAY);
            }
            
            holidays.add(e);
        }
        
        return holidays;
    }
    
    /**
     * Get the flexible (each year other date) holidays by law, according to the configuration byte. 
     * @param config - Config byte where each bit indicates whether or not a holiday is set
     * @param year - Year of interest. According to this number the holidays date will be set
     * @return List of flexible holidays by law.
     */
    public static List<Event> getFlexibleByLaw(int config, int year) {
        List<Event> holidays = new ArrayList<Event>();
        /*
         * Recalculate, based on easter Sunday and 4th advent
         */
        ostersonntag = calculateEasterSunday(year);
        
        // 4th Advent: first Sunday before Christmas Eve
        advent4 = new Date(year, java.util.Calendar.DECEMBER, 24);
        while (advent4.get(java.util.Calendar.DAY_OF_WEEK) != java.util.Calendar.SUNDAY) {
            advent4.add(java.util.Calendar.DAY_OF_MONTH, -1);
        }
        
        List<HolidayConstants> flexibleByLaw = HolidayConstants.getFlexibleByLaw(config);
        for (HolidayConstants h : flexibleByLaw) {
            Date date = null;
            switch(h) {
            case GRDO:
                // Gründonnerstag: 3 days before Easter Sunday
                date = ostersonntag.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, -3);
                break;
            case KARFR:
                // Karfreitag: 2 days before Easter Sunday
                date = ostersonntag.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, -2);
                break;
            case OSTERMO:
                date = ostersonntag.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, 1);
                break;
            case CHRHIMMELF:
                // Christi Himmelfahrt: 39 days after Easter Sunday
                date = ostersonntag.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, 39);
                break;
            case PFINGSTMO:
                // Pfingstmontag: 50 days after Easter Sunday
                date = ostersonntag.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, 50);
                break;
            case FRONLEICH:
                // Fronleichnam: 60 days after Easter Sunday
                date = ostersonntag.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, 60);
                break;
            case BUBT:
                // Buß-und Bettag: first Wednesday before November 22nd 
                date = new Date(year, java.util.Calendar.NOVEMBER, 22);
                while (date.get(java.util.Calendar.DAY_OF_WEEK) != java.util.Calendar.WEDNESDAY) {
                    date.add(java.util.Calendar.DAY_OF_MONTH, -1);
                }
                
                break;
            default:
                System.err.println("THIS SHOULD NOT APPEAR HERE: " + h.getTitle());
                break;
            }
            
            holidays.add(new Event(date, h.getTitle(), true));
        }
        
        // Set holiday category
        for (Event e : holidays) {
            e.setCategory(EventCategories.HOLIDAY);
            
            // Set category for Easter
            if (e.getName() == HolidayConstants.OSTERMO.getTitle()) {
                e.setCategory(EventCategories.EASTER);
            }
        }
        return holidays;
    }
    
    /**
     * Get the flexible (each year other date) special holidays, according to the configuration byte.
     * <b>NOTE: Before calling this method the method {@link #getFlexibleByLaw(int, int)} must be called
     * because this method depends on the calculation of Easter Sunday!</b>
     * @param config - Config byte where each bit indicates whether or not a holiday is set
     * @param year - Year of interest. According to this number the holidays date will be set
     * @return List of flexible special holidays.
     */
    public static List<Event> getFlexibleSpecial(int config, int year) {
        List<Event> holidays = new ArrayList<Event>();
        List<HolidayConstants> flexibleByLaw = HolidayConstants.getFlexibleSpecial(config);
        for (HolidayConstants h : flexibleByLaw) {
            Date date = null;
            String category = EventCategories.HOLIDAY;
            
            switch(h) {
            case ROSENM:
                // Rosenmontag: 48 days before Easter Sunday
                date = ostersonntag.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, -48);
                category = EventCategories.CARNIVAL;
                break;
            case FASCHING:
                // Fasching: 47 days before Easter Sunday
                date = ostersonntag.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, -47);
                category = EventCategories.CARNIVAL;
                break;
            case ASCHERM:
                // Aschermittwoch: 46 days before Easter Sunday
                date = ostersonntag.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, -46);
                category = EventCategories.CARNIVAL;
                break;
            case PALMS:
                // Palmsonntag: 7 days before Easter Sunday
                date = ostersonntag.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, -7);
                break;
            case MUTTER:
                // Muttertag: 2nd Sunday in May
                date = new Date(year, java.util.Calendar.MAY, 1);
                while (date.get(java.util.Calendar.DAY_OF_WEEK) != java.util.Calendar.SUNDAY) {
                    date.add(java.util.Calendar.DAY_OF_MONTH, 1);
                }
                date.add(java.util.Calendar.DAY_OF_MONTH, 7);
                break;
            case VOLKSTRAUER:
                // Volkstrauertag: 35 days before 4th Advent
                date = advent4.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, -35);
                break;
            case TOTENS:
                // Totensonntag: 28 days before 4th Advent
                date = advent4.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, -28);
                break;
            case ADV1:
                // 1st Advent: 21 days before 4th Advent
                date = advent4.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, -21);
                category = EventCategories.CHRISTMAS;
                break;
            case ADV2:
                // 2nd Advent: 14 days before 4th Advent
                date = advent4.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, -14);
                category = EventCategories.CHRISTMAS;
                break;
            case ADV3:
                // 3rd Advent: 7 days before 4th Advent
                date = advent4.clone();
                date.add(java.util.Calendar.DAY_OF_MONTH, -7);
                category = EventCategories.CHRISTMAS;
                break;
            case ADV4:
                date = advent4;
                category = EventCategories.CHRISTMAS;
                break;
            default:
                System.err.println("THIS SHOULD NOT APPEAR HERE: " + h.getTitle());
                break;
            }
            
            Event e = new Event(date, h.getTitle(), false);
            e.setCategory(category);
            
            holidays.add(e);
        }
        
        return holidays;
    }
    
    /**
     * Get the flexible (each year other date) time shift events. 
     * @param year - Year of interest. According to this number the events date will be set
     * @return List of time shift events for the year.
     */
    public static List<Event> getFlexibleTimeShift(int year) {
        List<Event> events = new ArrayList<Event>();
        
        /*
         * Time shift forwards (summer time): last Sunday in March
         */
        Date tsDate = new Date(year, java.util.Calendar.MARCH, 31);
        while (tsDate.get(java.util.Calendar.DAY_OF_WEEK) != java.util.Calendar.SUNDAY) {
            tsDate.add(java.util.Calendar.DAY_OF_MONTH, -1);
        }
        Event ts1 = new Event(tsDate, null, HolidayConstants.ZeitumstellungVor.getTitle(), 
                EventType.time_shift, Frequency.OCCUR_ONCE, null, -1);
        ts1.setCategory(EventCategories.CALENDAR_DATE);
        events.add(ts1);
        
        /*
         * Time shift forwards (winter time): last Sunday in October
         */
        tsDate = new Date(year, java.util.Calendar.OCTOBER, 31);
        while (tsDate.get(java.util.Calendar.DAY_OF_WEEK) != java.util.Calendar.SUNDAY) {
            tsDate.add(java.util.Calendar.DAY_OF_MONTH, -1);
        }
        Event ts2 = new Event(tsDate, null, HolidayConstants.ZeitumstellungNach.getTitle(), 
                EventType.time_shift, Frequency.OCCUR_ONCE, null, -1);
        ts2.setCategory(EventCategories.CALENDAR_DATE);
        events.add(ts2);
        
        return events;
    }
    
    /**
     * Get the static (each year same date) action/world days, according to the configuration byte. 
     * @param config1 - Config byte 1 where each bit indicates whether or not a day is set
     * @param config2 - Config byte 2 where each bit indicates whether or not a day is set
     * @param year - Year of interest. According to this number the events date will be set
     * @return List of static action/world days.
     */
    public static List<Event> getStaticAction(int config1, int config2, int year) {
        List<HolidayConstants> staticAction = HolidayConstants.getStaticAction(config1, config2);
        List<Event> events = new ArrayList<Event>();
        
        for (HolidayConstants h : staticAction) {
            events.add(new Event(h.getDate(year), null, h.getTitle(), EventType.special, 
                    Frequency.OCCUR_YEARLY, null, -1));
        }
        
        // Set holiday category
        for (Event e : events) {
            e.setCategory(EventCategories.CALENDAR_DATE);
        }
        return events;
    }
    
    /**
     * Get the flexible (each year other date) action/world days, according to the configuration byte. 
     * @param config1 - Config byte 1 where each bit indicates whether or not a day is set
     * @param config2 - Config byte 2 where each bit indicates whether or not a day is set
     * @param year - Year of interest. According to this number the events date will be set
     * @return List of static action/world days.
     */
    public static List<Event> getFlexibleAction(int config1, int config2, int year) {
        List<Event> events = new ArrayList<Event>();
        List<HolidayConstants> flexibleAction = HolidayConstants.getFlexibleAction(config1, config2);
        for (HolidayConstants h : flexibleAction) {
            Date date = null;
            switch(h) {
            case BIER:
                // Tag des Bieres: 1st Friday in August
                date = new Date(year, Calendar.AUGUST, 1);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
                    date.add(Calendar.DAY_OF_MONTH, 1);
                }
                break;
            case GEBET:
                // Weltgebetstag: 1st Friday in March
                date = new Date(year, Calendar.MARCH, 1);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
                    date.add(Calendar.DAY_OF_MONTH, 1);
                }
                break;
            case KAUF_NIX:
                // Kauf-Nix-Tag: Last Saturday in November
                date = new Date(year, Calendar.NOVEMBER, 30);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                    date.add(Calendar.DAY_OF_MONTH, -1);
                }
                break;
            case LACH:
                // Weltlachtag: 1st Sunday in May
                date = new Date(year, Calendar.MAY, 1);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    date.add(Calendar.DAY_OF_MONTH, 1);
                }
                break;
            case MUSEUM:
                // Museumstag: 3rd Sunday in May
                date = new Date(year, Calendar.MAY, 1);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    date.add(Calendar.DAY_OF_MONTH, 1);
                }
                date.add(Calendar.DAY_OF_MONTH, 14);
                break;
            case PHILOSOPHIE:
                // Welttag der Philosophie: 3rd Thursday in November
                date = new Date(year, Calendar.NOVEMBER, 1);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY) {
                    date.add(Calendar.DAY_OF_MONTH, 1);
                }
                date.add(Calendar.DAY_OF_MONTH, 14);
                break;
            case LEPRA:
                // Weltlepratag: Last Sunday in January
                date = new Date(year, Calendar.JANUARY, 31);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    date.add(Calendar.DAY_OF_MONTH, -1);
                }
                break;
            case SOZ_KOMM:
                // Welttag der soz. Komm.: 42 days after Easter Sunday
                date = ostersonntag.clone();
                date.add(Calendar.DAY_OF_MONTH, 42);
                break;
            case NIEREN:
                // Weltnierentag: 2nd Thursday in March
                date = new Date(year, Calendar.MARCH, 1);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY) {
                    date.add(Calendar.DAY_OF_MONTH, 1);
                }
                date.add(Calendar.DAY_OF_MONTH, 7);
                break;
            default:
                System.err.println("THIS SHOULD NOT APPEAR HERE: " + h.getTitle());
                break;
            }
            
            events.add(new Event(date, null, h.getTitle(), EventType.special, Frequency.OCCUR_ONCE, null, -1));
        }
        
        // Set holiday category
        for (Event e : events) {
            e.setCategory(EventCategories.CALENDAR_DATE);
        }
        return events;
    }
    
    /**
     * Get the static (each year same date) season events. 
     * @param year - Year of interest. According to this number the events date will be set
     * @return List of season events for the year.
     */
    public static List<Event> getStaticSeason(int year) {
        List<Event> events = new ArrayList<Event>();
        
        /* Spring begin */
        Event spring = new Event(HolidayConstants.SPRING.getDate(year), null, HolidayConstants.SPRING.getTitle(), 
                EventType.season, Frequency.OCCUR_YEARLY, null, -1);
        spring.setCategory(EventCategories.CALENDAR_DATE);
        
        /* Summer begin */
        Event summer = new Event(HolidayConstants.SUMMER.getDate(year), null, HolidayConstants.SUMMER.getTitle(), 
                EventType.season, Frequency.OCCUR_YEARLY, null, -1);
        summer.setCategory(EventCategories.CALENDAR_DATE);
        
        /* Autumn begin */
        Event autumn = new Event(HolidayConstants.AUTUMN.getDate(year), null, HolidayConstants.AUTUMN.getTitle(), 
                EventType.season, Frequency.OCCUR_YEARLY, null, -1);
        autumn.setCategory(EventCategories.CALENDAR_DATE);
        
        /* Winter begin */
        Event winter = new Event(HolidayConstants.WINTER.getDate(year), null, HolidayConstants.WINTER.getTitle(), 
                EventType.season, Frequency.OCCUR_YEARLY, null, -1);
        winter.setCategory(EventCategories.CALENDAR_DATE);
        
        events.add(spring);
        events.add(summer);
        events.add(autumn);
        events.add(winter);
        
        return events;
    }
}

