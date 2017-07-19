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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.jsteltze.calendar.UI.EventPanel;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.calendar.config.enums.RemindOption;
import de.jsteltze.common.Log;
import de.jsteltze.common.Msg;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.Date.PrintFormat;

/**
 * Event that takes place on a date.
 * @author Johannes Steltzer
 *
 */
public class Event extends EventComparator 
    implements Comparable<Event> {
    
    /** Possible event types. */
    public static enum EventType {
        /** Holiday by law. */
        holiday_law,
        
        /** Special holiday (but no free day). */
        holiday_special,
        
        /** Any special day. */
        special,
        
        /** Time shift event. */
        time_shift,
        
        /** Season event. */
        season,
        
        /** Normal user event. */
        user;
        
        /**
         * Returns true if the type if one of the holiday types.
         * @return true if the type if one of the holiday types.
         */
        public boolean isHoliday() {
            return this.equals(holiday_law) || this.equals(holiday_special); 
        }
        
        /**
         * Returns a short descriptive name for the event type.
         * @return a short descriptive name for the event type.
         */
        public String getShortName() {
            switch (this) {
            case holiday_law:
                return "gesetzl. Feiertag";
            case holiday_special:
                return "sonstiger Feiertag";
            case special:
                return "Welt-/Aktionstag";
            case time_shift:
                return "Zeitumstellung";
            case season:
                return "Jahrezeit";
            default:
                return "";
            }
        }
    }
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(Event.class);

    /** Name of this event. */
    private String name;
    
    /** Start date (opt. with time). */
    private Date date;
    
    /** End date (might be null). */
    private Date endDate;
    
    /** Frequency code. */
    private short frequency;
    
    /** Event type: holiday / special day / normal event. */
    private EventType type;
    
    /** Time period prior to event begin for reminding. */
    private RemindOption remind;
    
    /** Event ID. */
    private int id;
    
    /** Graphical panel for this event. */
    private EventPanel panel;
    
    /** Event category. */
    private String category;
    
    /** For frequent events: list of exception dates. */
    private List<Date> exceptionDates;

    /**
     * Construct a new non-holiday non-frequent event without 
     * notes/attachment.<br>
     * <b>FOR PLAIN EVENTS</b>
     * @param date - Date of this event
     * @param name - Name of this event
     * @param id - Event ID
     */
    public Event(Date date, String name, int id) {
        this(date, name, Frequency.OCCUR_ONCE, id);
    }

    /**
     * Construct a new non-holiday multi-day event without 
     * notes/attachment.
     * @param startDate - Start date of this event
     * @param endDate - End date of this event
     * @param name - Name of this event
     * @param id - Event ID
     */
    public Event(Date startDate, Date endDate, String name, int id) {
        this(startDate, endDate, name, EventType.user, Frequency.OCCUR_ONCE, null, id);
    }

    /**
     * Construct a new non-frequent event without notes/attachment 
     * and without ID.<br>
     * <b>FOR FLEXIBLE HOLIDAYS ONLY!</b>
     * @param date - Date of this holiday
     * @param name - Name of this holiday
     * @param law - True if is this a holiday by law
     */
    public Event(Date date, String name, boolean law) {
        this(date, name, Frequency.OCCUR_ONCE, law);
    }

    /**
     * Construct a new holiday event without notes/attachment 
     * and without ID.<br>
     * <b>FOR HOLIDAYS ONLY!</b>
     * @param date - Date of this holiday
     * @param name - Name of this holiday
     * @param f - Frequency of this holiday (e.g. yearly)
     * @param law - True if this is a holiday by law
     */
    public Event(Date date, String name, short f, boolean law) {
        this(date, null, name, law ? EventType.holiday_law : EventType.holiday_special, f, null, -1);
    }

    /**
     * Constructs a new frequent event without notes/attachment.<br>
     * <b>FOR PLAIN FREQUENT EVENTS (e.g. birthdays)</b>
     * @param date - Date of this event
     * @param name - Name of this event
     * @param f - Frequency of this event (e.g. yearly)
     * @param id - Event ID
     */
    public Event(Date date, String name, short f, int id) {
        this(date, null, name, EventType.user, f, null, id);
    }

    /**
     * Construct a new event.
     * @param startDate - Start date of this event
     * @param endDate - End date (might be null in case of single day events)
     * @param name - Name of this event
     * @param type - Type of this event
     * @param f - Frequency of this event (e.g. yearly)
     * @param remind - Reminder for this event
     * @param id - Event ID
     */
    public Event(Date startDate, Date endDate, String name, EventType type, 
            short f, RemindOption remind, int id) {
        this.date = startDate.clone();
        this.endDate = endDate == null ? null : (Date) endDate.clone();
        this.name = name;
        this.type = type;
        this.frequency = f;
        this.remind = remind;
        this.id = id;
        this.category = null;
        this.exceptionDates = new ArrayList<Date>();
        this.panel = new EventPanel(this);
    }

    /**
     * Returns this events name.
     * @return this events name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns this events frequency.
     * @return this events frequency.
     */
    public short getFrequency() {
        return this.frequency;
    }

    /**
     * Returns this events start date.
     * @return this events start date.
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Returns this events end date.
     * @return this events end date (might be null in case of single
     * day events).
     */
    public Date getEndDate() {
        return this.endDate;
    }
    
    /**
     * Returns this events type.
     * @return this events type.
     * day events).
     */
    public EventType getType() {
        return this.type;
    }

    /**
     * Returns this events ID.
     * @return Event ID.
     */
    public int getID() {
        return this.id;
    }

    /**
     * Returns this events reminder.
     * @return Reminder for this event.
     */
    public RemindOption getRemind() {
        return this.remind;
    }

    /**
     * Set ID of this event.
     * @param newID - ID to set
     */
    public void setID(int newID) {
        this.id = newID;
    }

    /**
     * Set start date of this event.
     * @param x - Date to set
     */
    public void setDate(Date x) {
        this.date = x;
    }

    /**
     * Set end date of this event (might be null in case of single day
     * events).
     * @param x - Date to set
     */
    public void setEndDate(Date x) {
        this.endDate = x;
    }
    
    /**
     * Returns this events category (or null).
     * @return this events category or null if this event has no category.
     */
    public String getCategory() {
        return this.category;
    }
    
    /**
     * Set the category name for this event.
     * @param cat - Category name or null (for no category)
     */
    public void setCategory(String cat) {
        this.category = cat;
    }
    
    /**
     * For frequent events: add a date as exception from the frequency.
     * This means the event will not match at this date.
     * @param x - Exception date to add
     */
    public void addExceptionDate(Date x) {
        this.exceptionDates.add(x);
    }
    
    /**
     * Returns the list of exception dates. On this dates the event
     * does not take place, although the frequency would match it.
     * @return the list of exception dates.
     */
    public List<Date> getExceptionDates() {
        return this.exceptionDates;
    }
    
    /**
     * Returns the date (from now on) when this event occurs next.
     * @return the date (from now on) when this event occurs next.
     */
    public Date getNextDate() {
        Date today = new Date();
        return getNextDate(today);
    }

    /**
     * Calculate the closest possible date when this event occurs again.<br>
     * In case of non-frequent events the events date is returned (might
     * be in the past).<br>In case of frequent events the next date in
     * future is returned.
     * @param begin - Begin date to start searching for the next occurrence
     * @return Next possible date.
     */
    public Date getNextDate(Date begin) {
        /* If unique... */
        if (this.frequency == Frequency.OCCUR_ONCE) {
            /* If single date... */
            if (this.endDate == null) {
                return this.date;
            
            /* If multi-day event... */
            } else {
                Date today = begin.clone();
                today.set(java.util.Calendar.HOUR, 0);
                today.set(java.util.Calendar.MINUTE, 0);
                today.setHasTime(this.date.hasTime());
                long diffStart = this.date.dayDiff(today);
                long diffEnd = this.endDate.dayDiff(today);
                /* today in the middle of event */
                if (diffStart <= 0 && diffEnd >= 0) {
                    today.set(java.util.Calendar.HOUR, 
                            this.date.get(java.util.Calendar.HOUR));
                    today.set(java.util.Calendar.MINUTE, 
                            this.date.get(java.util.Calendar.MINUTE));
                    return today;
                
                } else if (diffStart > 0) {
                    /* today before event */
                    return this.date;
                
                } else {
                    /* today after event */
                    return this.endDate;
                }
            }
        
        /* Else: frequent event */
        } else {
            // Days in future to check. 370 is a little
            // more than a year so that any frequency within
            // a year can be recognized.
            final int daysToCheck = 370;
            Date testDate = begin.clone();
            testDate.set(java.util.Calendar.HOUR, 0);
            testDate.set(java.util.Calendar.MINUTE, 0);
            if (this.date.hasTime()) {
                testDate.set(java.util.Calendar.HOUR_OF_DAY,
                        this.date.get(java.util.Calendar.HOUR_OF_DAY));
                testDate.set(java.util.Calendar.MINUTE,
                        this.date.get(java.util.Calendar.MINUTE));
                testDate.setHasTime(true);
            } else {
                testDate.setHasTime(false);
            }
            int daysAdded = 0;

            /*
             * Check if next date of this event is in future. So add a maximum
             * of 370 days (1 year) and see if matches.
             */
            while (true) {
                if (this.match(testDate)) {
                    return testDate;
                }

                testDate.add(java.util.Calendar.DAY_OF_MONTH, 1);
                if (++daysAdded >= daysToCheck) {
                    break;
                }
            }
            testDate.add(java.util.Calendar.DAY_OF_MONTH, -daysAdded);
            daysAdded = 0;

            /*
             * If looking for future was not successful, last next date MUST be
             * in the past. So reduce by a maximum of 370 days.
             */
            while (true) {
                if (this.match(testDate)) {
                    return testDate;
                }
                testDate.add(java.util.Calendar.DAY_OF_MONTH, -1);
                if (++daysAdded >= daysToCheck) {
                    break;
                }
            }

            return null; // This is actually impossible
        }
    }
    
    /**
     * Returns the graphical panel for this event.
     * @return the graphical panel for this event.
     */
    public EventPanel getGUI() {
        return this.panel;
    }
    
    
    /**
     * Creates an exact copy of this event.
     * @return Clone.
     */
    @Override
    public Event clone() {
        Date start = this.date.clone();
        Date end = this.endDate == null ? null : this.endDate.clone();
        String s = this.name;
        EventType et = this.type;
        short f = this.frequency;
        RemindOption r = this.remind;
        int iD = this.id;
        Event cloned = new Event(start, end, s, et, f, r, iD);
        cloned.setCategory(this.category);
        for (Date d : exceptionDates) {
            cloned.addExceptionDate(d);
        }
        return cloned;
    }

    /**
     * Check if this event takes place on a specific date.
     * @param date - Date to check
     * @return True if this event takes place on the date.
     */
    public boolean match(Date date) {
        if (this.frequency == Frequency.OCCUR_ONCE) {
            /* Case 1: unique event */
            if (this.getDate().sameDateAs(date)) {
                return true;
            } else if (this.getEndDate() != null) {
                long dayDiffStart = date.dayDiff(this.getDate());
                long dayDiffEnd = date.dayDiff(this.getEndDate());
                /*
                 * dayDiffEnd has to be negative (EndDate is in future) AND
                 * dayDiffStart has to be positive (StartDate is in past)
                 */
                if (dayDiffStart >= 0 && dayDiffEnd <= 0) {
                    return true;
                }
            }
            
            return false;
        }
        
        /*
         * Check if the date is among the exceptions
         */
        for (Date exception : exceptionDates) {
            if (exception.sameDateAs(date)) {
                return false;
            }
        }
        
        if (Frequency.isByDate(this.frequency)) {
            if (this.frequency == Frequency.OCCUR_YEARLY) {
                /* Case 2: yearly event (eg. birthday) */
                if (this.getDate().get(java.util.Calendar.MONTH) == date.get(java.util.Calendar.MONTH)
                        && this.getDate().get(java.util.Calendar.DAY_OF_MONTH) 
                        == date.get(java.util.Calendar.DAY_OF_MONTH)) {
                    return true;
                }
            
            } else if (this.frequency == Frequency.OCCUR_MONTHLY) {
                /* Case 3: event each month, but only this year */
                if (this.getDate().get(java.util.Calendar.YEAR) == date.get(java.util.Calendar.YEAR)
                        && this.getDate().get(java.util.Calendar.DAY_OF_MONTH) 
                        == date.get(java.util.Calendar.DAY_OF_MONTH)) {
                    return true;
                }
            
            } else if (this.frequency == (Frequency.OCCUR_MONTHLY | Frequency.OCCUR_YEARLY)) {
                /* Case 4: monthly event every year */
                if (this.getDate().get(java.util.Calendar.DAY_OF_MONTH) == date.get(java.util.Calendar.DAY_OF_MONTH)) {
                    return true;
                }
            
            } else if (this.frequency == Frequency.OCCUR_WEEKLY) {
                /* Case 5: Weekly event but this month only */
                if (this.getDate().get(Date.DAY_OF_WEEK) == date.get(Date.DAY_OF_WEEK)
                        && this.getDate().get(Date.MONTH) == date.get(Date.MONTH)
                        && this.getDate().get(Date.YEAR) == date.get(Date.YEAR)) {
                    return true;
                }
            
            } else if (this.frequency == (Frequency.OCCUR_MONTHLY | Frequency.OCCUR_WEEKLY)) {
                /* Case 6: Weekly event for the whole year */
                if (this.getDate().get(Date.DAY_OF_WEEK) == date.get(Date.DAY_OF_WEEK)
                        && this.getDate().get(Date.YEAR) == date.get(Date.YEAR)) {
                    return true;
                }
            
            } else if (this.frequency == (Frequency.OCCUR_YEARLY | Frequency.OCCUR_WEEKLY)) {
                /* Case 7: Weekly event this month only but every year */
                if (this.getDate().get(Date.DAY_OF_WEEK) == date.get(Date.DAY_OF_WEEK)
                        && this.getDate().get(Date.MONTH) == date.get(Date.MONTH)) {
                    return true;
                }
            
            } else if (this.frequency == (Frequency.OCCUR_YEARLY
                    | Frequency.OCCUR_MONTHLY | Frequency.OCCUR_WEEKLY)) {
                /* Case 8: Weekly event every month every year */
                if (this.getDate().get(Date.DAY_OF_WEEK) == date.get(Date.DAY_OF_WEEK)) {
                    return true;
                }
            }
        
        } else if (Frequency.isByWeekday(this.frequency)) {
            int weekday = this.date.get(java.util.Calendar.DAY_OF_WEEK);
            int index = this.date.getWeekdayIndex();
            return date.get(java.util.Calendar.DAY_OF_WEEK) == weekday && date.getWeekdayIndex() == index;
        
        } else if (Frequency.isByInterval(this.frequency)) {
            if (Frequency.getUnit(this.frequency) == Frequency.UNIT_DAYS
                    || Frequency.getUnit(this.frequency) == Frequency.UNIT_WEEKS) {
                long dayDiff = this.date.dayDiff(date);
                if (dayDiff < 0) {
                    dayDiff = -dayDiff;
                }
                long interval = Frequency.getInterval(this.frequency);
                if (Frequency.getUnit(this.frequency) == Frequency.UNIT_WEEKS) {
                    interval *= 7;
                }
                long div = dayDiff / interval;
                return div * interval == dayDiff;
            
            } else if (Frequency.getUnit(this.frequency) == Frequency.UNIT_MONTHS
                    || Frequency.getUnit(this.frequency) == Frequency.UNIT_YEARS) {
                if (this.date.get(java.util.Calendar.DAY_OF_MONTH) != date.get(java.util.Calendar.DAY_OF_MONTH)) {
                    return false;
                }
                long monDiff = this.date.get(java.util.Calendar.MONTH) 
                        + this.date.get(java.util.Calendar.YEAR) * Date.MONTHS_OF_YEAR 
                        - (date.get(java.util.Calendar.MONTH) 
                                + date.get(java.util.Calendar.YEAR) * Date.MONTHS_OF_YEAR);
                if (monDiff < 0) {
                    monDiff = -monDiff;
                }
                long interval = Frequency.getInterval(this.frequency);
                if (Frequency.getUnit(this.frequency) == Frequency.UNIT_YEARS) {
                    interval *= Date.MONTHS_OF_YEAR;
                }
                long div = monDiff / interval;
                return div * interval == monDiff;
            }
        
        } else if (Frequency.isByEndOfMonth(this.frequency)) {
            return date.getDaysToEndOfMonth() == this.date.getDaysToEndOfMonth();
        }
        return false;
    }

    
    /**
     * Sort the events list by date. In ambiguous cases, move holidays to front.
     * @param events - Event list to sort
     * @param withFrequency - True for sorting with attention to
     *         the events frequencies; false for sorting by base date
     *         only
     * @param asc - True for ascending order: earliest first
     * @return Sorted list.
     */
    public static List<Event> sortByDate(List<Event> events, 
            boolean withFrequency, boolean asc) {
        /* Insert sort */
        List<Event> res = new ArrayList<Event>();
        for (Event e : events) {
            long time = (withFrequency ? e.getNextDate() : e.getDate()).getTimeInMillis();
            int i = 0;
            
            if (e.getType().isHoliday()) {
                /* 
                 * Special case for holidays:
                 * Always move them to the front
                 */
                for ( ; i < res.size(); i++) {
                    if (time <= (withFrequency ? res.get(i).getNextDate() : res.get(i).getDate()).getTimeInMillis()) {
                        break;
                    }
                }
            
            } else {
                for ( ; i < res.size(); i++) {
                    if (time < (withFrequency ? res.get(i).getNextDate() : res.get(i).getDate()).getTimeInMillis()) {
                        break;
                    }
                }
            }
            res.add(i, e);
        }
        
        /* Sort the reverse for descending order */ 
        if (!asc) {
            Collections.reverse(res);
        }
        
        return res;
    }

    /**
     * Returns the file attached to this event or null.
     * @param workspace - Working directory for the calendar
     * @return Attached file of this event. If there are more than 
     * one file, a first one will be returned. Will NEVER return the file 
     * "notes.txt". If there is no attached file (except for "notes.txt"), 
     * null will be returned.
     */
    public File getAttachment(String workspace) {
        File folder = new File(workspace + File.separator + Const.EVENT_DIR + File.separator + this.id);
        File[] files = null;
        if (folder.exists() && folder.isDirectory()) {
            files = folder.listFiles();
        }
        if (files != null) {
            for (File f : files) {
                LOG.fine("all file for this event:" + f.getName());
                if (!f.getName().equals(Const.NOTES_FILE)) {
                    if (f.getName().equals(Const.LINK_FILE)) {
                        return this.followLink(f);
                    } else {
                        return f;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns weather or not the attached file is a real file (copy)
     * or just a link to a file.
     * @param workspace - Working directory for the calendar
     * @return True if there is a file "link.txt" for this event.
     */
    public boolean attachmentIsLink(String workspace) {
        File res = new File(workspace + File.separator + Const.EVENT_DIR 
                + File.separator + this.id + File.separator + Const.LINK_FILE);
        return res.exists();
    }

    /**
     * Gets the file pointed to by a link.
     * @param link - File which contains the link
     * @return File pointed to by the link. If the file specified does 
     * not exist, null will be returned.
     */
    private File followLink(File link) {
        LOG.fine("following link:" + link);
        if (link != null && link.exists()) {
            try {
                String path = "";
                Scanner sc = new Scanner(link);
                if (sc.hasNextLine()) {
                    path = sc.nextLine();
                }
                sc.close();
                File dest = new File(path);
                if (dest.exists()) {
                    return dest;
                }
            } catch (IOException io) {
                LOG.log(Level.WARNING, "cannot get file from link...", io);
            }
        }
        return null;
    }

    /**
     * Returns the attached notes for this event or an empty string
     * if notes do not exist.
     * @param workspace - Working directory for the calendar
     * @return Content of the file "notes.txt" for this event. 
     * If there is no such file, "" will be returned.
     */
    public String getNotes(String workspace) {
        File notesTxt = new File(workspace + File.separator + Const.EVENT_DIR 
                + File.separator + this.id + File.separator + Const.NOTES_FILE);
        String res = "";
        if (notesTxt.exists() && notesTxt.canRead()) {
            Scanner in = null;
            try {
                in = new Scanner(notesTxt);
                boolean firstLine = true;
                while (in.hasNextLine()) {
                    if (firstLine) {
                        res += in.nextLine();
                        firstLine = false;
                    } else {
                        res += "\n" + in.nextLine();
                    }
                }
            } catch (FileNotFoundException f) {
                LOG.severe("There is no file " + Const.NOTES_FILE
                        + " for this event but there should be!");
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "", e);
            } finally {
                in.close();
            }
        }
        return res;
    }
    
    /**
     * Attach notes to this event (will write the notes.txt file).
     * @param notes - Notes to write
     * @param parent - Parent calendar object (needed for the working
     * directory)
     */
    public void writeNotes(String notes, Calendar parent) {
        try {
            String dirname = parent.getPath(Const.EVENT_DIR);
            // try to create the events directory (will fail if already exists)
            new File(dirname).mkdir();
            dirname += File.separator + id;
            // try to create the event specific directory (will fail if already exists)
            new File(dirname).mkdir();

            File notesTxt = new File(dirname + File.separator + Const.NOTES_FILE);
            notesTxt.createNewFile();
            if (!notesTxt.canWrite()) {
                parent.errorOccurred("Der Kalender hat hier keine Schreibrechte zum Schreiben der Notizen.",
                        "Keine Schreibrechte...", null);
                return;
            }

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(notesTxt)));

            out.write(notes);
            out.close();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "error while trying to write notes to a file...", e);
        }
    }
    
    /**
     * Remove the complete event directory and all files contained.
     * @param parent - Parent calendar object (needed for the workspace)
     */
    public void removeDirectory(Calendar parent) {
        if (id != -1) {
            File eventDir = new File(parent.getPath(Const.EVENT_DIR) + File.separator + id);
            if (eventDir.exists() && eventDir.isDirectory()) {
                File[] files = eventDir.listFiles();
                for (File f : files) {
                    if (!f.delete()) {
                        LOG.warning("cannot delete file: " + f.getName());
                    }
                }
            }
            if (!eventDir.delete()) {
                LOG.warning("cannot delete directory: " + eventDir.getName());
            }
        }
    }
    
    /**
     * Write XML event tag.
     * @param out - Stream to write
     * @throws IOException if an error occurred during writing
     */
    public void write(BufferedWriter out) throws IOException {
        out.write("    <Event");
        out.write(" ID=\"" + id + "\"");
        out.write(" date=\"" + date.print() + "\"");
        if (endDate != null) {
            out.write(" endDate=\"" + endDate.print() + "\"");
        }
        if (date.hasTime()) {
            out.write(" time=\"" + date.print(PrintFormat.HHmm) + "\"");
        }
        if (frequency != Frequency.OCCUR_ONCE) {
            out.write(" frequency=\"" + frequency + "\"");
        }
        if (!exceptionDates.isEmpty()) {
            out.write(" exceptions=\"" + exceptionDates.get(0).print());
            for (int i = 1; i < exceptionDates.size(); i++) {
                out.write("," + exceptionDates.get(i).print());
            }
            out.write("\"");
        }
        if (remind != null) {
            out.write(" remind=\"" + remind.getName(true) + "\"");
        }
        if (category != null) {
            out.write(" category=\"" + category + "\"");
        }
        out.write(">" + Msg.replaceSpecialCharacters(name) + "</Event>\n");
    }
    
    /**
     * In case of this event being a birthday: calculate the age of the person (if possible) 
     * and returning a string to be appended to the events label.
     * For this to work the events notes must contain the year of birth as a 4 digit number.
     * @param workspace - Location of the workspace to contain the events notes
     * @return a string with the age of the person to be appended to the events label.
     * If not a birthday event or no valid year of birth there, an empty string will be returned.
     */
    public String getBirthdayAge(final String workspace) {
        /* try to parse birth year in case of category birthday */
        if ("Geburtstag".equals(category)) {
            String birthYear = getNotes(workspace).trim();
            if (birthYear.length() == 4) {
                try {
                    int year = Integer.parseInt(birthYear);
                    year = getNextDate().get(java.util.Calendar.YEAR) - year;
                    /* calculate and show age of the person */
                    return " (wird " + year + " Jahr" + (year == 1 ? "" : "e") + " alt)";
                } catch (NumberFormatException e) {
                    LOG.fine("cannot parse birhtyear: " + birthYear);
                }
            }
        }
        
        return "";
    }
    
    @Override
    public int compareTo(Event o) {
        return this.compare(this, o);
    }
}
