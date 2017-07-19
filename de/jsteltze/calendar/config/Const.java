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
import java.awt.Font;
import java.util.Calendar;

import de.jsteltze.common.calendar.Date;

/**
 * Class holding the various constants.
 * @author Johannes Steltzer
 *
 */
public final class Const {
    
    /**
     * Constructor not for public use.
     */
    private Const() { }
    
    /** Version number. */
    public static final String VERSION = "2.8_svn103";
    /** Last edit date. */
    public static final Date LAST_EDIT_DATE = new Date(2017, Calendar.APRIL, 28);
    /** File encoding. */
    public static final String ENCODING = "UTF-8";
    /** Author. */
    public static final String AUTHOR = "Johannes Steltzer";
    /** Compiler. */
    public static final String COMPILER = "javac 1.8.0_65";
    
    /** Program file name. */
    public static final String FILENAME = "Kalender.jar";
    /** Program file name used for update. */
    public static final String NEW_FILENAME = "KalenderNEU.jar";
    /** Program updater file. */
    public static final String UPDATER = "KalenderUpdater.jar";
    /** XML file name holding all settings and events. */
    public static final String XMLFILE = "Kalender.xml";
    /** Lock file indicating a running instance. */
    public static final String LOCKFILE = "Kalender.lock";
    /** Log file. */
    public static final String LOGFILE = "Kalender.log";
    /** Maximize file indicating a new instance wants to start. */
    public static final String MAXIMIZEFILE = "Kalender.maximize";
    /** Release file with the latest online version number. */
    public static final String RELEASE_FILE = "Kalender.release";
    /** Events folder. */
    public static final String EVENT_DIR = "Kalender.Events";
    /** Events notes file (containing event notes). */
    public static final String NOTES_FILE = "notes.txt";
    /** Events link file (containing link to local file as attachment). */
    public static final String LINK_FILE = "link.txt";
    /** Default music theme for notifications. */ 
    public static final String DEFAULT_THEME = "/media/notify.wav";
    
    /** Homepage. */
    public static final String HOME_URL = "http://java-kalender.sourceforge.net";
    /** Website from where to download updates. */
    public static final String DOWNLOAD_URL = "http://java-kalender.sourceforge.net/";
    
    /** Default color for today. */
    public static final Color COLOR_DEF_TODAY = new Color(189, 232, 179);
    /** Default color for weekend. */
    public static final Color COLOR_DEF_WEEKEND = new Color(247, 250, 188);
    /** Default color for holiday. */
    public static final Color COLOR_DEF_HOLIDAY = new Color(247, 250, 188);
    /** Default color for selection. */
    public static final Color COLOR_DEF_SELECTED = new Color(187, 219, 252);
    /** Default font color for event names. */
    public static final Color COLOR_DEF_FONT = new Color(150, 150, 150);
    /** Default font color for holiday names. */
    public static final Color COLOR_DEF_FONT_HOLIDAY = Color.blue;
    /** Default color for control bar / status bar. */
    public static final Color COLOR_DEF_CONTROL = Color.white;
    /** Default background color for notification frame. */
    public static final Color COLOR_DEF_NOTI = new Color(236, 233, 216);
    /** Default background color. */
    public static final Color COLOR_DEF_BACKGROUND = new Color(221, 239, 248);
   
    /** Background color for program info text field. */
    public static final Color COLOR_SETTINGS_INFO_BG = new Color(200, 221, 242);
    /** Color for calendar grid lines. */
    public static final Color COLOR_GRID_LINES = new Color(220, 220, 220);
    /** Color for control bar border lines. */
    public static final Color COLOR_CONTROL_BORDER = new Color(172, 168, 153);
    /** Font color for titled borders. */
    public static final Color COLOR_BORDER_TITLE = new Color(139, 69, 0);
    /** Font color for time in notification dialogs. */
    public static final Color COLOR_FONT_NOTI_TIME_HEADER = new Color(69, 108, 143);
    /** Font color for non-today events in notification dialogs. */
    public static final Color COLOR_FONT_DAY_NON_TODAY = Color.darkGray;

    /** Font for titled borders. */
    public static final Font FONT_BORDER_TEXT = new Font(Font.SANS_SERIF, Font.ITALIC, 11);
    /** Font for header in TableOfEventsMultiDay. */
    public static final Font FONT_MULTIDAY_HEADER = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    /** Font for event time header in notification dialogs. */
    public static final Font FONT_NOTI_TIME_HEADER = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    /** Font for event text in notification dialogs. */
    public static final Font FONT_NOTI_TEXT = new Font(Font.SANS_SERIF, Font.BOLD, 20);
    /** Status bar font. */
    public static final Font FONT_STATUSBAR = new Font(Font.SANS_SERIF, Font.PLAIN, 9);
    /** Font for events. */
    public static final Font FONT_EVENT_NAME = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    /** Font for year number in yearly view. */
    public static final Font FONT_YEAR_NUMBER = new Font(Font.SANS_SERIF, Font.BOLD, 30);
    /** Font for month names in yearly view. */
    public static final Font FONT_YEAR_MONTH_NAME = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    /** Font for KW numbers in yearly view. */
    public static final Font FONT_YEAR_KW_NUMBERS = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    /** Font for days of week header in yearly view. */
    public static final Font FONT_YEAR_DAY_OF_WEEK = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    /** Font for month names in monthly view. */
    public static final Font FONT_MONTH_NAME = new Font(Font.SANS_SERIF, Font.BOLD, 20);
    /** Font for days of previous or next month in monthly view. */
    public static final Font FONT_MONTH_DAY_OTHER = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    /** Font for days in monthly view. */
    public static final Font FONT_MONTH_DAY = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    /** Font for days of week header in monthly view. */
    public static final Font FONT_MONTH_DAY_OF_WEEK = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    /** Font for KW number in weekly view. */
    public static final Font FONT_WEEK_KW_NUMBER = new Font(Font.SANS_SERIF, Font.PLAIN, 64);
    /** Font for days of week header in weekly view. */
    public static final Font FONT_WEEK_DAY_OF_WEEK = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    /** Font for days header in weekly view. */
    public static final Font FONT_WEEK_HEADER = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    /** Font for day header in daily view. */
    public static final Font FONT_DAY_HEADER = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    /** Font for the hours of day in daily view. */
    public static final Font FONT_DAY_HOURS = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    /** Welcome text font in TableOfNotifications. */
    public static final Font FONT_WELCOME_TEXT = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    /** Font for KW header in monthly view. */
    public static final Font FONT_MONTH_KW_LABEL = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    /** Font for KW number in monthly view. */
    public static final Font FONT_MONTH_KW_NUMBER = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    /** Event notes font for the text areas. */
    public static final Font FONT_EVENT_NOTES = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    /** Font for error introduction. */
    public static final Font FONT_ERROR_INTRO = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    /** Font for log messages in the text area. */
    public static final Font FONT_LOGGING = new Font(Font.MONOSPACED, Font.PLAIN, 11);
}
