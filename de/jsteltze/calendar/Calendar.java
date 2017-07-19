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

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import de.jsteltze.calendar.Event.EventType;
import de.jsteltze.calendar.UI.CalendarGUI;
import de.jsteltze.calendar.UI.GUIUtils;
import de.jsteltze.calendar.applet.CalendarApplet;
import de.jsteltze.calendar.config.ColorSet;
import de.jsteltze.calendar.config.Configuration;
import de.jsteltze.calendar.config.Configuration.BoolProperty;
import de.jsteltze.calendar.config.Configuration.EnumProperty;
import de.jsteltze.calendar.config.Configuration.IntProperty;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.calendar.config.Holidays;
import de.jsteltze.calendar.config.enums.OnClickDayAction;
import de.jsteltze.calendar.config.enums.OnClickEventAction;
import de.jsteltze.calendar.config.enums.RemindOption;
import de.jsteltze.calendar.config.enums.Style;
import de.jsteltze.calendar.config.enums.View;
import de.jsteltze.calendar.frames.CalendarFrame;
import de.jsteltze.calendar.frames.CalendarWelcomeFrame;
import de.jsteltze.calendar.frames.EditEvent;
import de.jsteltze.calendar.frames.LogWindow;
import de.jsteltze.calendar.frames.Notification;
import de.jsteltze.calendar.frames.Settings;
import de.jsteltze.calendar.frames.TableOfEventsSingleDay;
import de.jsteltze.calendar.frames.TableOfNotifications;
import de.jsteltze.calendar.tasks.AlarmTask;
import de.jsteltze.calendar.tasks.AutoUpdateTask;
import de.jsteltze.calendar.tasks.CalendarRefreshTask;
import de.jsteltze.calendar.tasks.RollingBackup;
import de.jsteltze.calendar.tasks.SingletonTask;
import de.jsteltze.common.ErrorListener;
import de.jsteltze.common.Log;
import de.jsteltze.common.Msg;
import de.jsteltze.common.SimpleTextPane;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.Date.PrintFormat;
import de.jsteltze.common.io.Copy;

/**
 * Main calendar class. This will start the calendar frame and
 * load the settings and events (if exist).
 * @author Johannes Steltzer
 *
 */
public class Calendar implements ErrorListener {

    /** Frame which displays information managed here. */
    private CalendarGUI gui;
    
    /** Logger. */
    private static Logger log;

    /** 
     * Current date the user is viewing. 
     * On starting this is always todays date. 
     */
    private Date viewedDate;

    /**
     * List of AlarmTasks that will pop up soon for notifying (timer thread is
     * started already).
     */
    private List<AlarmTask> pendingAlarms;

    /** List of open notifications (actual frames waiting for user input). */
    private List<Notification> notis;

    /** All events (including holidays). */
    private List<Event> events;

    /** Current configuration (settings). */
    private Configuration config;

    /** True if this is the very first launch of this program. */
    private boolean firstStartup;
    
    /** True if application is yet fully started and everything loaded. */
    private boolean fullyLaunched;
    
    /** True if calendar was started empty although there was a config file. */
    private boolean launchedEmpty;

    /** Refresh date and notifications at midnight. */
    private static Timer refreshAtMidnight; 
    
    /**
     * Make sure this application is only launched once in order
     * to prevent inconsistency.
     */
    private static SingletonTask singletonThread;
    
    /** Auto update timer. */
    private static Timer autoUpdateTimer;
    
    /** True for a browser applet instead of standalone JFrame. */
    private boolean appletMode;
    
    /** Working directory (not changeable). */
    private String workspace;
    
    /** Command line arguments used. */
    private static String[] cmdArgs;
    
    /** Table of notifications. */
    private TableOfNotifications tableOfNotis;

    /**
     * Construct a new calendar.
     * @param size - Dimension (in pixels) to start with. Pass -1,-1 to use
     * default dimension. Pass 0,0 to start with full screen.
     * @param view - Specify view to start with. If null the value parsed from the config file will be used
     * @param appletMode - Start calendar in applet mode. If true, NO config file will be read
     * (empty calendar with default config) and access will be read-only
     * @param workspace - Use a specific working directory (default is ".").
     */
    public Calendar(final Dimension size, final View view, 
            final boolean appletMode, final String workspace) {
        this.viewedDate = new Date();
        this.firstStartup = false;
        this.fullyLaunched = false;
        this.launchedEmpty = false;
        this.pendingAlarms = new ArrayList<AlarmTask>();
        this.events = new ArrayList<Event>();
        this.notis = new ArrayList<Notification>();
        this.appletMode = appletMode;
        this.workspace = workspace;

        // init logger (if not yet initialized)
        if (log == null) {
            log = Log.getLogger(Calendar.class);
        }
        
        if (!appletMode) {
            
            /*
             * Read events from xml file
             */
            XMLParser parser = new XMLParser();
    
            try {
                parser.parse(new File(getPath(Const.XMLFILE)));
                events = parser.getEvents();
                RollingBackup.start(this);
            } catch (FileNotFoundException e) {
                log.fine("XML file \"" + getPath(Const.XMLFILE) + "\" not found, assuming first startup");
                firstStartup = true;
            } catch (Exception e) {
                launchedEmpty = true;
                errorOccurred(Msg.getMessage("errorMessageCannotParseXML", new String[] {Const.XMLFILE}), 
                        Msg.getMessage("errorMessageCannotParseXMLTitle"), e);
            }

            config = parser.getConfig();
            if (firstStartup && Locale.getDefault().equals(Locale.US)) {
                config.setProperty(IntProperty.FirstDayOfWeek, java.util.Calendar.SUNDAY);
            }
    
            /*
             * Test for write rights
             */
            File test = new File(getPath(Const.XMLFILE));
            if (!test.exists()) {
                try {
                    test.createNewFile();
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(test), Const.ENCODING));
                    out.write("<?xml version=\"1.0\" encoding=\"" + Const.ENCODING
                            + "\" standalone=\"yes\"?>\n<Calendar version=\"" + Const.VERSION
                            + "\" />\n");
                    out.close();
                } catch (IOException e) {
                    log.log(Level.WARNING, Const.XMLFILE + " cannot be created: " + e.toString());
                }
            }
            if (!test.canWrite()) {
                errorOccurred(Msg.getMessage("errorMessageNoWriteRights"), 
                        Msg.getMessage("errorMessageNoWriteRightsTitle"), null);
            }
        
        } else {
            config = Configuration.DEFAULT_CONFIG.clone();
        }

        updateFlexibleHolidays(viewedDate.get(java.util.Calendar.YEAR), true, false);
        updateStaticHolidays(false);
        
        /* Remove Updater program if exists */
        new File(Const.UPDATER).delete();
        
        fullyLaunched = true;
        log.info("calendar now fully started");
    }

    /**
     * Updates the flexible holidays (such as easter). They always pend on the
     * year.
     * @param year - Year of interest
     * @param force <li>false for browsing in different years (no settings changes, 
     *         no new notifications)<li>true for complete update (settings changes,
     *         running notifications get lost, new notifications may occur)
     * @param notify - true if notifications to be launched (if applicable), false
     *         for silent adding
     */
    public void updateFlexibleHolidays(int year, boolean force, boolean notify) {
        List<Event> holidays = new ArrayList<Event>();
        int thisYear = new Date().get(java.util.Calendar.YEAR);

        /* Get all flexible holidays/special events */
        for (Event e : events) {
            if (e.getType() != EventType.user && e.getFrequency() == Frequency.OCCUR_ONCE) {
                holidays.add(e);
            }
        }

        if (force) {
            /* Remove all flexible holidays */
            for (Event e : holidays) {
                events.remove(e);
            }
        
        } else {
            /* Remove all flexible holidays of years other than @year */
            for (Event e : holidays) { 
                if (e.getDate().get(java.util.Calendar.YEAR) != thisYear) {
                    events.remove(e);
                }
            }
            
            if (year == thisYear) {
                return;
            }
        }
        
        /* Collect holiday events to add (depending on the config and the current year) */
        holidays.clear();
        holidays.addAll(Holidays.getFlexibleByLaw(config.getProperty(IntProperty.HolidayID), year));
        holidays.addAll(Holidays.getFlexibleSpecial(config.getProperty(IntProperty.SpecialDaysID), year));
        holidays.addAll(Holidays.getFlexibleAction(config.getProperty(IntProperty.ActionDays1ID), 
                config.getProperty(IntProperty.ActionDays2ID), year));
        
        /* Collect time shift events (if enabled) */
        if (config.getProperty(BoolProperty.NotifyTimeShift)) {
            holidays.addAll(Holidays.getFlexibleTimeShift(year));
        }
        
        for (Event e : holidays) {
            if (notify) {
                addEvent(e, false);
            } else {
                events.add(e);
            }
        }
        
        /* Update the status bar text */
        if (gui != null) {
            gui.updateStatusBar();
        }
    }

    /**
     * Updates the static holidays (such as Christmas...).
     * To be called in case of settings changes only.
     * @param notify - true if notifications to be launched (if applicable), false
     *         for silent adding
     */
    public void updateStaticHolidays(boolean notify) {
        List<Event> holidays = new ArrayList<Event>();
        int year = viewedDate.get(java.util.Calendar.YEAR);

        /*
         * Get all static holidays
         */
        for (Event e : events) {
            if (e.getType() != EventType.user && Frequency.isY(e.getFrequency())) {
                holidays.add(e);
            }
        }

        /*
         * Remove all static holidays
         */
        for (Event e : holidays) {
            events.remove(e);
        }
        
        /* collect holiday events to add */
        holidays.clear();
        holidays.addAll(Holidays.getStaticByLaw(config.getProperty(IntProperty.HolidayID), year));
        holidays.addAll(Holidays.getStaticSpecial(config.getProperty(IntProperty.SpecialDaysID), year));
        holidays.addAll(Holidays.getStaticAction(config.getProperty(IntProperty.ActionDays1ID), 
                config.getProperty(IntProperty.ActionDays2ID), year));
        
        /* Collect season events (if enabled) */
        if (config.getProperty(BoolProperty.NotifySeason)) {
            holidays.addAll(Holidays.getStaticSeason(year));
        }
        
        for (Event e : holidays) {
            if (notify) {
                addEvent(e, false);
            } else {
                events.add(e);
            }
        }
        
        if (gui != null) {
            gui.updateStatusBar();
        }
    }

    /**
     * Returns all events.
     * @return List of all events.
     */
    public List<Event> getAllEvents() {
        return events;
    }
    
    /**
     * Returns all user events (non-holiday).
     * @return all user events.
     */
    public List<Event> getAllUserEvents() {
        List<Event> ret = new ArrayList<Event>();
        for (Event e : events) {
            if (e.getType() == EventType.user) {
                ret.add(e);
            }
        }
        return ret;
    }

    /**
     * Returns the date which is currently in focus.
     * @return Currently viewed date.
     */
    public Date getViewedDate() {
        return viewedDate;
    }

    /**
     * Set the currently viewed date.
     * @param x - Date to jump to
     */
    public void setViewedDate(Date x) {
        this.viewedDate = x;
    }

    /**
     * Returns all running notification tasks.
     * @return List of running alarm tasks.
     */
    public List<AlarmTask> getAlarmTasks() {
        return pendingAlarms;
    }

    /**
     * Add an alarm task to the list of running alarm tasks.
     * If there is already a task with for the same event, this
     * task will be replaced.
     * @param x - AlarmTask to add
     */
    public void addAlarmTask(AlarmTask x) {
        log.fine("[addPendingEvent] " + x.getEvent().getName());
        if (pendingAlarms.contains(x)) {
            return;
        }
        
        /* remove duplicate alarm task */
        int i, size = pendingAlarms.size();
        for (i = 0; i < size; i++) {
            if (pendingAlarms.get(i).getEvent().equals(x.getEvent())) {
                pendingAlarms.get(i).cancel();
                pendingAlarms.remove(i);
                gui.putMessage(Msg.getMessage("guiMessageAlarmChanged", new String[] {x.getEvent().getName()}));
                break;
            }
        }
        
        if (i == size) {
            gui.putMessage(Msg.getMessage("guiMessageAlarmSet", new String[] {x.getEvent().getName()}));
        }
        
        pendingAlarms.add(x);
        gui.updateStatusBar();
    }

    /**
     * Removes an alarm task from the list of running alarm tasks.
     * DOES NOT CANCEL THE TASK.
     * @param x - AlarmTask to remove
     */
    public void removeAlarmTask(AlarmTask x) {
        pendingAlarms.remove(x);
        gui.updateStatusBar();
    }

    /**
     * Main function.
     * @param args - Command line arguments
     */
    public static void main(String[] args) {
        /* use default dimension */
        int width = -1, height = -1;
            
        /* use default view */
        View view = null;
        
        /* use a specific working directory */
        String workspaceArg = new File("").getAbsolutePath();
        
        /* default log level */
        String logLevel = "INFO";
        
        /*
         * Parse command line parameters
         */
        for (String s : args) {
            if (s.equals("--maximized")) {
                width = 0;
                height = 0;
            
            } else if (s.equals("--view=YEAR")) {
                view = View.year;
            
            } else if (s.equals("--view=MONTH")) {
                view = View.month;
            
            } else if (s.equals("--view=WEEK")) {
                view = View.week;
            
            } else if (s.equals("--view=DAY")) {
                view = View.day;
                
            } else if (s.startsWith("--logLevel=")) {
                logLevel = s.substring("--logLevel=".length());
            
            } else if (s.startsWith("--workspace=")) {
                workspaceArg = s.substring("--workspace=".length());
                File test = new File(workspaceArg);
                if (!test.exists() || !test.isDirectory()) {
                    System.err.println("workspace \"" + workspaceArg + "\" is no valid directory!");
                    System.exit(1);
                }
            
            } else if (s.equals("--version")) {
                versioninfo();
                System.exit(0);
                
            } else if (s.startsWith("--size=")) {
                String w = s.substring(7, s.lastIndexOf("x"));
                String h = s.substring(s.lastIndexOf("x") + 1, s.length());
                try {
                    width = Integer.parseInt(w);
                    height = Integer.parseInt(h);
                } catch (NumberFormatException n) {
                    System.err.println("Cannot parse \"" + s + "\"");
                    System.err.println("Use interges only!");
                    System.exit(1);
                }
                
            } else {
                System.err.println("Unsupported option \"" + s + "\"");
                usage();
                System.exit(1);
            }
        }
        cmdArgs = args;
        
        /*
         * Initialize Logging
         */
        SimpleTextPane logTextArea = LogWindow.getInstance().getTextArea();
        Log.setFileHandler(workspaceArg + File.separator + Const.LOGFILE, false);
        Log.setLoggingTextArea(logTextArea);
        Log.setLevel(logLevel);
        log = Log.getLogger(Calendar.class);
        
        /*
         * Check if lock file exists
         */
        if (new File(workspaceArg + File.separator + Const.LOCKFILE).exists()) {
            /*
             * Call running calendar to appear. This will create a maximize-file.
             * The running calendar is frequently looking for this file. If the file
             * is detected by a already running calendar process it will be deleted.
             * The deleting of the maximize-file is the signal for this process that
             * there is an already running calendar process.
             */
            log.info("Lockfile exists. Already started?");
            File maxfile = new File(workspaceArg + File.separator + Const.MAXIMIZEFILE);
            try {
                log.fine("create file to call running calendar");
                /* Create maximize file. */
                maxfile.createNewFile();
                /* Wait two seconds for the (possibly) running calendar to react. */
                Thread.sleep(2 * Date.SEC_1);
                
                if (!maxfile.exists()) {
                    /*
                     * Exit if running calendar answers
                     */
                    log.info("calendar is already running. Exit.");
                    return;
                
                } else {
                    log.info("calendar is not responding. Launching new.");
                    maxfile.delete();
                }
                
            } catch (IOException e) {
                log.log(Level.WARNING, "problems with the lock file...", e);
            } catch (InterruptedException e) {
                log.log(Level.WARNING, "sleep interrupted...", e);
            }
        }
        
        /*
         * Case: no lock file found or running calendar does not answer
         */
        launchCalendarFrame(new Dimension(width, height), view, workspaceArg);        
    }
    
    /**
     * Launch a new calendar in a new JFrame. Only call this method if no other
     * calendar process is running parallel (can cause damage to config!).
     * @param size - Size of the new frame 
     * @param view - Desired view to start with
     * @param fworkspace - Workspace path (for config file and events)
     */
    private static void launchCalendarFrame(final Dimension size, final View view, final String fworkspace) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                // Create new calendar object 
                Calendar cal = new Calendar(size, view, false, fworkspace);
                Configuration config = cal.getConfig();
                
                // Load config items
                View confView       = (View) config.getProperty(EnumProperty.DefaultView);
                Style confStyle     = (Style) config.getProperty(EnumProperty.Style);
                RemindOption remind = (RemindOption) config.getProperty(EnumProperty.Remind);
                
                // Create frame to show calendar
                CalendarFrame mainFrame = new CalendarFrame(size, view == null ? confView : view, cal);
                if (config.getProperty(BoolProperty.SystrayStart)) {
                    mainFrame.setUI(confStyle, false);
                    mainFrame.toSystray();
                } else {
                    mainFrame.setUI(confStyle, true);
                }
                cal.setGUI(mainFrame);
                GUIUtils.setParentComponent(mainFrame);
                
                // Start task to maintain singleton (prevent subsequent start of calendars
                // in order to prevent overwriting of config file)
                singletonThread = new SingletonTask(mainFrame);
                singletonThread.start();
        
                /*
                 * Refresh at midnight (task) (plus 10 seconds to prevent timing errors)
                 */
                refreshAtMidnight = new Timer(true);
                refreshAtMidnight.schedule(new CalendarRefreshTask(mainFrame),
                        Date.minutesToMidnight() * Date.MIN_1 + 10 * Date.SEC_1);
                
                /*
                 * Start auto update after 5 minutes
                 */
                if (config.getProperty(BoolProperty.AutoUpdate)) {
                    autoUpdateTimer = new Timer(true);
                    autoUpdateTimer.schedule(new AutoUpdateTask(cal), 5 * Date.MIN_1);
                }
                
                /*
                 * Add shutdown hook for a graceful shutdown
                 */
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    private final File lockFile = new File(cal.getPath(Const.LOCKFILE));
                    @Override
                    public void run() {
                        log.info("shutdown, remove lock, cancel all tasks");
                        // delete lock file
                        lockFile.delete();
                        // close GUI
                        mainFrame.shutdown();
                        // stop singleton thread
                        if (singletonThread != null) {
                            singletonThread.stopit();
                        }
                        // cancel all alarm tasks
                        for (AlarmTask at : cal.getAlarmTasks()) {
                            at.cancel();
                        }
                        // stop auto updater
                        if (autoUpdateTimer != null) {
                            autoUpdateTimer.cancel();
                        }
                        // close all loggers/handlers
                        Log.closeAll();
                    }
                });
                
                if (cal.isFirstStartup()) {
                    /* show welcome (help) window in case of first launch of this program */
                    new CalendarWelcomeFrame(mainFrame);
                
                } else {
                    /* Collect events to notify */
                    List<Event> events2notify = cal.getEvents2Notify(remind, true);
                    
                    /* show table of coming events */
                    if (!events2notify.isEmpty()) {
                        new TableOfNotifications(cal, events2notify);
                    }
                }
            }
        });
    }

    /**
     * Print version information on stdout.
     */
    private static void versioninfo() {
        System.out.println(Const.FILENAME);
        System.out.println("Version " + Const.VERSION);
        System.out.println();
    }

    /**
     * Print usage information on stdout.
     */
    private static void usage() {
        System.out.println("Calendar command line options:");
        System.out.println();
        System.out.println("--maximized                   Start calendar maximized");
        System.out.println("--workspace=[path]            Start calendar with a certain working directory");
        System.out.println("--view=[YEAR|MONTH|WEEK|DAY]  Start calendar with a specified view");
        System.out.println("--size=WIDTHxHEIGHT           Start calendar with size WIDTH and HEIGHT");
        System.out.println("--logLevel=[level]            Start calendar with log level [level]");
        System.out.println("                                Possible values: ALL,CONFIG,FINE,FINER,");
        System.out.println("                                FINEST,INFO,OFF,SEVERE,WARNING");
        System.out.println("--version                     Print version on stdout and exit");
        System.out.println();
        System.out.println("Call \"java -jar " + Const.FILENAME + " [options]\" to start calendar with options.");
        System.out.println("Or simply doubleclick to start without options.");
    }

    /**
     * User did a selection of one or more dates.<br>
     * 2 cases:
     * <br>(1) dates are connected to each other without gaps 
     * <br>(2) randomly chosen dates (unconnected)
     * @param dates - List of selected dates
     * @param connected - True if selected dates are in a row, false
     *         if randomly chosen dates (with gaps) 
     */
    public void newSelection(List<Date> dates, boolean connected) {
        log.fine("New selection: " + dates.size() + " dates");
        
        for (Date d : dates) {
            log.fine("selected date: " + d.print());
        }
        
        /*
         * Short cut if one date only
         */
        if (dates.size() == 1) {
            if (config.getProperty(EnumProperty.AtClickDay) == OnClickDayAction.overview) {
                new TableOfEventsSingleDay(dates.get(0), this, false);
            
            } else if (config.getProperty(EnumProperty.AtClickDay) == OnClickDayAction.newEvent) {
                new EditEvent(this, dates.get(0));
            }
            return;
        }
        
        if (appletMode) {
            gui.getApplet().newSelection();
            return;
        }

        if (connected) {
            /*
             * Get first and last date
             */
            Date first = dates.get(dates.size() - 1);
            Date last = dates.get(dates.size() - 1);
            for (Date d : dates) {
                if (d.getTimeInMillis() < first.getTimeInMillis()) {
                    first = d;
                } else if (d.getTimeInMillis() > last.getTimeInMillis()) {
                    last = d;
                }
            }
            log.fine("first date: " + first.print());
            log.fine("last date: " + last.print());
            new EditEvent(this, first, last);
        
        } else {
            new EditEvent(this, dates);
        }
    }
    
    /**
     * User did a selection of an event. This will launch a new
     * notification.
     * @param e - Selected event
     */
    public void newSelection(Event e) {
        if (appletMode) {
            gui.getApplet().newSelection();
        
        } else {
            if (config.getProperty(EnumProperty.AtClickEvent) == OnClickEventAction.open) {
                new Notification(this, e);
            } else if (config.getProperty(EnumProperty.AtClickEvent) == OnClickEventAction.edit) {
                new EditEvent(this, e, false, null);
            }
        }
    }
    
    /**
     * User did a selection of a column that represents a week day. This
     * will launch the EditEvent frame with a new weekly event.
     * @param start - Start date for weekly event
     * @param yearly - True for context of a full year. False for context of a month.
     */
    public void newWeeklySelection(Date start, boolean yearly) {
        new EditEvent(this, new Event(start, "", yearly 
                ? Frequency.OCCUR_WEEKLY | Frequency.OCCUR_MONTHLY : Frequency.OCCUR_WEEKLY, -1),
                false, null);
    }

    /**
     * Alters an existing event, references by its ID.
     * @param oldID - ID of the event to edit
     * @param newEvent - New event information to save
     * @return true weather or not this event was successfully added.
     */
    public boolean editEvent(int oldID, Event newEvent) {
        log.fine("EDIT EVENT: ID=" + oldID + " new name="
                + newEvent.getName() + " remind=" + newEvent.getRemind());
        
        /* 
         * Avoid corruption if not yet fully launched 
         */
        if (!fullyLaunched) {
            log.warning("edit requested BUT application NOT FULLY LAUNCHED!!!");
            JOptionPane.showMessageDialog(gui.getFrame(), 
                    Msg.getMessage("errorMessageNotYetFullyStarted"), 
                    Msg.getMessage("errorMessageNotYetFullyStartedTitle"), 
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        /*
         * Simply add new event if ID equals -1
         */
        if (oldID == -1) {
            return newEvent(newEvent);
        }

        /*
         * Remove old event, copy the ID, add new event
         */
        Event oldEvent = getEventByID(oldID);
        events.remove(oldEvent);
        newEvent.setID(oldID);
        events.add(newEvent);

        /*
         * Update alarm task if exists
         */
        for (AlarmTask a : pendingAlarms) {
            if (a.getEvent().equals(oldEvent)) {
                a.setEvent(newEvent);
            }
        }
        
        /* Edit this event in the table of notifications frame (if present). */
        if (tableOfNotis != null && tableOfNotis.isVisible()) {
            tableOfNotis.eventEdited(newEvent);
        }

        log.fine("old event was: " + oldEvent.getName());
        gui.update();
        gui.putMessage(Msg.getMessage("guiMessageEventEdited", new String[] {oldEvent.getName()}));
        log.fine("new date=" + newEvent.getDate().print());

        save();
        return true;
    }
    
    /**
     * Copy an user event. Duplicate will get a new ID.
     * Attachment/notes will be copied, too.
     * @param event - Event to copy with the original ID (not -1)
     */
    public void copyEvent(Event event) {
        /* Check if event to copy has attachments */
        File srcEventDir = new File(workspace + File.separator
                + Const.EVENT_DIR + File.separator + event.getID());
        int newID = genID();
        event.setID(newID);
        if (srcEventDir.exists()) {
            /* Copy attachments */
            File dstEventDir = new File(workspace + File.separator
                    + Const.EVENT_DIR + File.separator + newID);
            Copy.copyAll(gui.getFrame(), srcEventDir, dstEventDir);
        }
        
        addEvent(event, true);
        gui.putMessage(Msg.getMessage("guiMessageEventCopied", new String[] {event.getName()}));
    }
    
    /**
     * Add a new event.
     * @param event - New event to add
     * @return true weather or not this event was successfully added.
     */
    public boolean newEvent(Event event) {
        boolean ret = addEvent(event, true);
        if (ret) {
            gui.putMessage(Msg.getMessage("guiMessageEventAdded", new String[] {event.getName()}));
        }
        return ret;
    }

    /**
     * Register a new event.
     * @param event - New event to add. Automatically checks if this event is
     *         close enough to launch a notification
     * @param saveAfter <li>true if changes to be written
     *         <li>false if no writing (will be used on startup)
     * @return true weather or not this event was successfully added.
     */
    private boolean addEvent(Event event, boolean saveAfter) {
        if (event == null) {
            return false;
        }
        
        if (appletMode) {
            events.add(event);
            return true;
        }

        log.fine("NEW EVENT: " + event.getDate().print()
                + " -> " + event.getName() + " (" + event.getType().name() + ")");

        /*
         * Does this event already exist?
         */
        for (Event e : events) {
            if (e.match(event.getDate())) {
                
                /* Check event with same name. */
                if (e.getName().equals(event.getName()) && JOptionPane.showConfirmDialog(gui.getFrame(),
                        Msg.getMessage("questionEventAlreadyExists", 
                                new String[] {e.getName(), e.getDate().print(PrintFormat.DM)}), 
                                Msg.getMessage("questionEventAlreadyExistsTitle"), 
                                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null) 
                            == JOptionPane.NO_OPTION) {
                    return false;
                }
                
                /* Check event with same time. */
                if (e.getDate().hasTime() && event.getDate().hasTime() && e.getDate().sameTimeAs(event.getDate()) 
                        && JOptionPane.showConfirmDialog(gui.getFrame(),
                        Msg.getMessage("questionEventTimeAlreadyExists", 
                                new String[] {e.getName(), e.getDate().print(PrintFormat.DM), 
                                        e.getDate().print(PrintFormat.Hmm)}), 
                                Msg.getMessage("questionEventTimeAlreadyExistsTitle"), 
                                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null) 
                            == JOptionPane.NO_OPTION) {
                    return false;
                }
            }
        }

        if (event.getType() == EventType.user) {
            /*
             * All non-holiday events get an ID
             */
            if (event.getID() == -1) {
                event.setID(genID());
            }
            log.fine(" ID=" + event.getID());
        }

        events.add(event);
        gui.update();
        gui.updateStatusBar();

        /*
         * If time to wait matches the config, launch a new notification
         */
        long notifyTimer = checkNotification(event, (RemindOption) config.getProperty(EnumProperty.Remind));
        if (notifyTimer == 0) {
            
            /* Add this event in the table of notifications frame (if present). */
            if (tableOfNotis != null && tableOfNotis.isVisible()) {
                tableOfNotis.eventAdded(event);
            }
            
            // DO NOT open single notification, is not needed
            
//            /* otherwise open a separate notification */
//            else {
//                new Notification(this, event);
//            }
        } else if (notifyTimer != -1) {
            Timer timer = new Timer(true);
            timer.schedule(new AlarmTask(this, event, notifyTimer), notifyTimer);
        }

        if (saveAfter) {
            save();
        }
        return true;
    }

    /**
     * Unregister a specified event. 
     * @param e - Event to remove
     * @return True if the event really was deleted.
     */
    private boolean deleteEvent(Event e) {
        log.fine("REMOVE EVENT: " + e.getDate().print() + " -> " + e.getName());

        if (!events.contains(e)) {
            log.severe("NO SUCH EVENT TO REMOVE: " + e.getName());
            return false;
        }
        
        if (appletMode) {
            events.remove(e);
            return true;
        }
        
        /* 
         * Avoid corruption if not yet fully launched 
         */
        if (!fullyLaunched) {
            log.warning("deletion requested BUT application NOT FULLY LAUNCHED!!!");
            JOptionPane.showMessageDialog(gui.getFrame(), 
                    Msg.getMessage("errorMessageNotYetFullyStarted"), 
                    Msg.getMessage("errorMessageNotYetFullyStartedTitle"), 
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        /*
         * for regular events: also delete notes and attachments
         */
        if (e.getType() == EventType.user) {
            
            /*
             * Warn in case of frequent events.
             */
            if (e.getFrequency() != Frequency.OCCUR_ONCE) {
                if (JOptionPane.showConfirmDialog(gui.getFrame(), 
                        Msg.getMessage("questionRemoveFrequentEvent", new String[] {e.getName()}),
                        Msg.getMessage("questionRemoveEventTitle", new String[] {e.getName()}),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
            
            /*
             * Warn in case of multi-day events.
             */
            if (e.getEndDate() != null) {
                if (JOptionPane.showConfirmDialog(gui.getFrame(),
                        Msg.getMessage("questionRemoveMultidayEvent", new String[] {e.getName()}),
                        Msg.getMessage("questionRemoveEventTitle", new String[] {e.getName()}), 
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return false;                        
                }
            }

            /*
             * Warn if there are notes to delete.
             */
//            if (!e.getNotes(workspace).equals("")) {
//                if (JOptionPane.showConfirmDialog(gui.getFrame(),
//                        Msg.getMessage("questionRemoveEventNotes"),
//                        Msg.getMessage("questionRemoveEventTitle", new String[] {e.getName()}),
//                        JOptionPane.YES_NO_OPTION,
//                        JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
//                    return false;
//                }
//            }

            /*
             * Warn if there is a real attachment to delete.
             */
            if (e.getAttachment(workspace) != null && !e.attachmentIsLink(workspace)) {
                if (JOptionPane.showConfirmDialog(gui.getFrame(),
                        Msg.getMessage("questionRemoveEventAttachment"),
                        Msg.getMessage("questionRemoveEventTitle", new String[] {e.getName()}),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                    return false;
                }
            }

            /*
             * Delete folder if all warnings have been confirmed.
             */
            e.removeDirectory(this);
        }

        /*
         * Remove this event from the list of events to notify.
         */
        events.remove(e);
        for (AlarmTask a : pendingAlarms) {
            if (a.getEvent().equals(e)) {
                a.cancel();
                pendingAlarms.remove(a);
                break;
            }
        }
        
        /* Remove this event from the table of notifications frame (if present). */
        if (tableOfNotis != null && tableOfNotis.isVisible()) {
            tableOfNotis.eventRemoved(e, true);
        }

        gui.update();
        gui.updateStatusBar();
        gui.putMessage(Msg.getMessage("guiMessageEventRemoved", new String[] {e.getName()}));

        save();
        return true;
    }
    
    /**
     * Request for deleting an event.
     * If the event is of a holiday type this will open the settings dialog.
     * @param event - Event to delete
     * @return True if the event was removed. False if removal was aborted or this was a non-user event.
     */
    public boolean deleteRequested(Event event) {
        switch (event.getType()) {
        case holiday_law:
        case holiday_special:
        case special:
            new Settings(this, Settings.TAB_HOLIDAYS);
            return false;
        case time_shift:
            new Settings(this, Settings.TAB_GENERAL);
            return false;
        default:
            return deleteEvent(event);
        }
    }
    
    /**
     * Add an exception date to a frequent event. Although the frequency covers this
     * date, the event will not take place there.
     * @param event - Event to add the exception date
     * @param exceptionDate - Exception date to add
     */
    public void addException(Event event, Date exceptionDate) {
        /* Remove this event from the table of notifications frame (if present). */
        if (tableOfNotis != null && tableOfNotis.isVisible()) {
            tableOfNotis.eventExceptionAdded(event, exceptionDate);
        }
        
        event.addExceptionDate(exceptionDate);
        
        gui.update();
        gui.putMessage(Msg.getMessage("guiMessageEventExceptionAdded", 
                new String[] {event.getName(), exceptionDate.print()}));

        save();
    }

    /**
     * Saves all settings and events to the default xml-file.
     */
    public void save() {
        save(this.events, this.config, getPath(Const.XMLFILE));
    }

    /**
     * Writes events and settings to the file specified.
     * @param v - List of events to save
     * @param c - Configuration (settings) to save
     * @param filename - File to write
     */
    public void save(List<Event> v, Configuration c, String filename) {
        log.info("SAVE");
        
        /* 
         * Avoid corruption if not yet fully launched 
         */
        if (!fullyLaunched) {
            log.warning("save requested BUT application NOT FULLY LAUNCHED!!!");
            JOptionPane.showMessageDialog(gui.getFrame(), 
                    Msg.getMessage("errorMessageNotYetFullyStarted"), 
                    Msg.getMessage("errorMessageNotYetFullyStartedTitle"), 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        /* 
         * Avoid overriding previous configurations
         */
        if (launchedEmpty) {
            log.warning("save requested BUT application was launched empty!!!");
            JOptionPane.showMessageDialog(gui.getFrame(), 
                    Msg.getMessage("errorMessageLaunchedEmpty"), 
                    Msg.getMessage("errorMessageLaunchedEmptyTitle"), 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(new File(filename)), Const.ENCODING));

            /*
             * Write XML header
             */
            out.write("<?xml version=\"1.0\" encoding=\"" + Const.ENCODING
                    + "\" standalone=\"yes\"?>\n<Calendar version=\"" + Const.VERSION
                    + "\">\n");

            /*
             * Write config section
             */
            if (c != null && !c.equals(Configuration.DEFAULT_CONFIG)) {
                c.write(out);
            }

            /*
             * Write events section
             */
            out.write("  <Events>\n");
            for (Event event : v) {
                if (event.getID() != -1) {
                    event.write(out);
                }
            }

            /*
             * Write XML trailer
             */
            out.write("  </Events>\n</Calendar>");
            out.close();
        } catch (Exception e) {
            log.log(Level.SEVERE, "cannot save...", e);
        }
    }

    /**
     * Returns the current configuration.
     * @return Current configuration
     */
    public Configuration getConfig() {
        return this.config;
    }
    
    /**
     * Apply a new configuration.
     * @param x - Settings to activate
     */
    public void setConfig(Configuration x) {
        Configuration old = this.config;
        this.config = x;
        
        /* Check changes for holiday events. */
        if (old.getProperty(IntProperty.HolidayID) != x.getProperty(IntProperty.HolidayID) 
                || old.getProperty(IntProperty.SpecialDaysID) != x.getProperty(IntProperty.SpecialDaysID)
                || old.getProperty(IntProperty.ActionDays1ID) != x.getProperty(IntProperty.ActionDays1ID)
                || old.getProperty(IntProperty.ActionDays2ID) != x.getProperty(IntProperty.ActionDays2ID)
                || old.getProperty(BoolProperty.NotifyTimeShift) != x.getProperty(BoolProperty.NotifyTimeShift)
                || old.getProperty(BoolProperty.NotifySeason) != x.getProperty(BoolProperty.NotifySeason)) {
            updateFlexibleHolidays(this.viewedDate.get(java.util.Calendar.YEAR), true, true);
            updateStaticHolidays(true);
        }
        
        /* Check changes for auto updater. */
        if (old.getProperty(BoolProperty.AutoUpdate) && !x.getProperty(BoolProperty.AutoUpdate)) {
            // Cancel auto updater
            autoUpdateTimer.cancel();
        } else if (!old.getProperty(BoolProperty.AutoUpdate) && x.getProperty(BoolProperty.AutoUpdate)) {
            // Setup new auto updater in one minute
            autoUpdateTimer = new Timer(true);
            autoUpdateTimer.schedule(new AutoUpdateTask(this), Date.MIN_1);
        }
        
        /* Apply all other (visual) changes. */
        gui.getFrame().setButtonPanelColor(x.getColors()[ColorSet.CONTROLPANEL]);
        gui.getFrame().setBackground(x.getColors()[ColorSet.BACKGROUND]);
        gui.update();
        gui.putMessage(Msg.getMessage("guiMessageSettingsSaved"));
        save();
    }
    
    /**
     * Apply a Java applet as calendar GUI.
     * @param x - Applet to set
     */
    public void setApplet(CalendarApplet x) {
        gui = x;
    }

    /**
     * Returns whether or not this is the first launch of the calendar.
     * @return True if this is the very first launch
     */
    public boolean isFirstStartup() {
        return this.firstStartup;
    }

    /**
     * Find an empty ID.
     * @return unused ID
     */
    public int genID() {
        int idCandidate = 0;
        for (int i = 0; i < events.size(); i++) {
            if (idCandidate == events.get(i).getID()) {
                idCandidate++;
                i = 0;
            }
        }
        return idCandidate;
    }

    /**
     * Returns the event with a specific ID.
     * @param id - ID
     * @return Event
     */
    public Event getEventByID(int id) {
        for (Event e : events) {
            if (e.getID() == id) {
                return e;
            }
        }
        return null;
    }
    
    /**
     * Returns the event with a specific ID and name.
     * @param id - ID
     * @param name - Name
     * @return Event
     */
    public Event getEventByIDAndName(int id, String name) {
        for (Event e : events) {
            if (e.getID() == id && e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Returns the Calendar GUI object of this calendar.
     * Can be implemented by either a CalendarFrame or a CalendarApplet.
     * @return CalendarGUI object
     */
    public CalendarGUI getGUI() {
        return gui;
    }
    
    /**
     * Set the Calendar GUI object of this calendar.
     * @param gui - Calendar GUI object (can be either Java Applet or JFrame)
     */
    public void setGUI(CalendarGUI gui) {
        this.gui = gui;
    }
    
    /**
     * Returns the path of a file or directory with the current
     * working directory.
     * @param file - File or directory to get
     * @return Path of the file within the working directory.
     */
    public String getPath(String file) {
        return workspace + File.separator + file;
    }
    
    /**
     * Returns the working directory as string.
     * @return The current working directory.
     */
    public String getWorkspace() {
        return workspace;
    }
    
    /**
     * Returns the command line arguments the application was started with.
     * @return Command line arguments the calendar was started with.
     */
    public String[] getArgs() {
        return cmdArgs;
    }

    /**
     * Adds a notification to the list of notifications currently displayed.
     * @param x - Notification to add
     */
    public void addCurrentNoti(Notification x) {
        if (notis.size() == 0) {
            gui.getFrame().startChangeIcon();
        }

        if (!notis.contains(x)) {
            notis.add(x);
        }
    }

    /**
     * Removes a notification from the list of notifications currently displayed.
     * @param x - Notification to unregister
     */
    public void removeCurrentNoti(Notification x) {
        notis.remove(x);

        if (notis.size() == 0) {
            gui.getFrame().stopChangeIcon();
        }
    }
    
    /**
     * Returns the number of currently displayed notifications.
     * @return Number of currently displayed notification frames.
     */
    public int getNotificationSize() {
        return notis.size();
    }

    /**
     * Checks if an event is close enough for notification.
     * @param event - Event of interest
     * @param defaultRemindOption - Remind option to apply if the event does not have an individual remind option
     * @return Time in milliseconds to wait before a notification is to be launched. 0 for immediate notification. -1
     * for no notification at all.
     */
    private long checkNotification(Event event, RemindOption defaultRemindOption) {
        long dayDiff = event.getNextDate().dayDiff();
        long minDiff = event.getNextDate().minDiff();

        RemindOption remind = event.getRemind();
        if (remind == null) {
            // use default reminder, if no event specific available
            log.fine("remind=DEFAULT");
            remind = defaultRemindOption;
        }

        log.fine("remind=" + remind);
        
        if (remind.equals(RemindOption.noRemind)) {
            // No reminder desired
            return -1;
        }

        if (dayDiff < 0) {
            // If no holiday: always notify immediately if event is in past
            if (event.getType() == EventType.user) {
                return 0;
            }
        
        } else if (remind.ordinal() >= RemindOption.atBegin.ordinal()
                && remind.ordinal() <= RemindOption.before5h.ordinal()) {
            // Minute-wise reminders (depending on the quantifier -> see RemindOption) 
            
            if (minDiff <= remind.getQuantifier()) {
                // time has come: notify immediately
                return 0;
            } else {
                // still in the future: set timer
                return Date.MIN_1 * (minDiff - remind.getQuantifier());
            }
            
        } else if (remind.ordinal() >= RemindOption.before1d.ordinal()
                && remind.ordinal() <= RemindOption.before3m.ordinal()) {
            // Day-wise reminders (depending on the quantifier -> see RemindOption) 
            
            if (dayDiff <= remind.getQuantifier()) {
                // time has come: notify immediately
                return 0;
            } else if (dayDiff < 6) {
                // still in the future: set timer
                // to save resources we only consider events up to 5 days in future
                // we assume no one runs this program for longer than 5 days without restart 
                long minutesToMidnight = Date.minutesToMidnight();
                return Date.MIN_1 * (Date.MINS_OF_DAY * (dayDiff - remind.getQuantifier() - 1L) + minutesToMidnight);
            }
        }
        
        return -1;
    }
    
    /**
     * Creates a list of events to notify. 
     * @param remindOption - Default remind setting for events with no individual setting
     * @param activateTimer - True for activating the notification timer for events soon in the future
     * @return List of events to notify.
     */
    public List<Event> getEvents2Notify(RemindOption remindOption, boolean activateTimer) {
        List<Event> events2notify = new ArrayList<Event>();
        for (Event e : events) {
            log.fine("check event: " + e.getName());
            long notifyTimer = checkNotification(e, remindOption);
            
            if (notifyTimer == 0) {
                /* Event is ready for notification. */
                log.fine("notify!");
                events2notify.add(e);
            
            } else if (notifyTimer != -1 && activateTimer) {
                /* Notification for this event is in the future. */
                log.fine("set timer for: " + e.getName());
                Timer timer = new Timer(true);
                timer.schedule(new AlarmTask(this, e, notifyTimer), notifyTimer);
            }
        }
        
        return events2notify;
    }
    
    /**
     * Set a new TableOfNotifications frame.
     * @param x - Frame to set
     */
    public void setTableOfNotifications(TableOfNotifications x) {
        this.tableOfNotis = x;
    }

    /**
     * Specify weather or not to make all notifications currently displayed always on top of all other windows.
     * @param visible - True for alwaysOnTop. False for normal window behavior.
     */
    public void setNotisAlwaysOnTop(boolean visible) {
        for (Notification n : notis) {
            n.setAlwaysOnTop(visible);
        }
        if (tableOfNotis != null) {
            tableOfNotis.setAlwaysOnTop(visible);
        }
    }

    @Override
    public void errorOccurred(String intro, String title, Exception e) {
        GUIUtils.showErrorMessage(intro, title, e);
    }
}
