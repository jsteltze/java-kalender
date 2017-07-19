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

import java.awt.Color;

/**
 * Class holding the different default colors
 * of the calendar UI.
 * @author Johannes Steltzer
 *
 */
public final class ColorSet {
    
    /** Total number of configurable colors. */
    public static final byte MAXCOLORS = 0x09;
    /** Color for todays day. */
    public static final byte TODAY = 0x00;
    /** Color for weekends. */
    public static final byte WEEKEND = 0x01;
    /** Color for holidays. */
    public static final byte HOLIDAY = 0x02;
    /** Color for selected dates. */
    public static final byte SELECTED = 0x03;
    /** Font color for user events. */
    public static final byte FONT = 0x04;
    /** Font color for holidays. */
    public static final byte FONT_HOLIDAY = 0x05;
    /** Control panel color. */
    public static final byte CONTROLPANEL = 0x06;
    /** Background color of the notification dialog. */
    public static final byte NOTI = 0x07;
    /** Background color of the calendar. */
    public static final byte BACKGROUND = 0x08;

    /** Default color set. */
    public static final Color[] DEFAULT = {
        Const.COLOR_DEF_TODAY, Const.COLOR_DEF_WEEKEND, 
        Const.COLOR_DEF_HOLIDAY, Const.COLOR_DEF_SELECTED,
        Const.COLOR_DEF_FONT, Const.COLOR_DEF_FONT_HOLIDAY,
        Const.COLOR_DEF_CONTROL, Const.COLOR_DEF_NOTI,
        Const.COLOR_DEF_BACKGROUND
    };
    
    /** Names for the color settings. Index in this array matches color index. */
    public static final String[] NAMES = {"Heute", "Wochenende", 
        "Feiertag",    "Markierung", "Schriftfarbe für Ereignisse", 
        "Schriftfarbe für Feiertage", "Steuerleiste",  
        "Erinnerungsfenster", "Hintergrund" };
    
    /**
     * Hidden constructor.
     */
    private ColorSet() { }
}
