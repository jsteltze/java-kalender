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

package de.jsteltze.calendar.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.Event.EventType;
import de.jsteltze.calendar.EventCategories;
import de.jsteltze.calendar.EventListener;
import de.jsteltze.calendar.UI.CalendarPopupMenu;
import de.jsteltze.calendar.UI.EventTable;
import de.jsteltze.calendar.UI.EventTable.Columns;
import de.jsteltze.calendar.UI.GUIUtils;
import de.jsteltze.calendar.config.Configuration.BoolProperty;
import de.jsteltze.calendar.frames.TableOfEventsButtonPanel.SelectionType;
import de.jsteltze.common.Log;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.Moon;
import de.jsteltze.common.calendar.Moon.MoonState;
import de.jsteltze.common.calendar.Zodiac;
import de.jsteltze.common.calendar.Date.PrintFormat;
import de.jsteltze.common.ui.SearchPanel;
import de.jsteltze.common.ui.SearchPanelListener;

/**
 * Frame for controlling events on a specific date (or
 * all events).
 * @author Johannes Steltzer
 *
 */
public class TableOfEventsSingleDay 
    extends JDialog 
    implements MouseListener, SearchPanelListener, EventListener {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Matched events to display. */
    private List<Event> events = new ArrayList<Event>(),
            holidays = new ArrayList<Event>();
    
    /** Parent calendar object. */
    private Calendar caller;
    
    /** Date of interest (can be null -> all events). */
    private Date date;
    
    /** Currently selected event. */
    private Event selectedEvent = null;
    
    /** Button panel for event options. */
    private TableOfEventsButtonPanel buttonPanel;
    
    /** Event table. */
    private EventTable eventTable;
    
    /** Show holidays or events? */
    private boolean holidaysOnly;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(TableOfEventsSingleDay.class);

    /**
     * Arranges all elements in this dialog window.
     */
    private void arrangeDialog() {
        setLayout(new BorderLayout());

        JPanel eventPanel = new JPanel(new BorderLayout());
        buttonPanel = new TableOfEventsButtonPanel(Arrays.asList(date), caller, this, 
                holidaysOnly ? SelectionType.allHolidays 
                    : (date == null ? SelectionType.allEvents : SelectionType.normalRange));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel northPanel  = new JPanel(new BorderLayout());
        JPanel infoCorner  = new JPanel(new BorderLayout());
        northPanel.add(infoCorner, BorderLayout.EAST);
        centerPanel.add(northPanel, BorderLayout.NORTH);
        eventPanel.add(buttonPanel, BorderLayout.SOUTH);
        centerPanel.add(eventPanel, BorderLayout.CENTER);
        centerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        if (date == null) {
            if ((events.size() > 0 && holidaysOnly)
                    || (!holidaysOnly && events.size() > 0)) {
                /*
                 * Add filter text field and put event list into a scroll pane 
                 */
                String defFilterText = holidaysOnly ? "Name, Datum oder Typ" : "Name, Datum, Uhrzeit oder Kategorie";
                SearchPanel searchPanel = new SearchPanel(defFilterText, "Ereignisse nach Namen, "
                        + "Datum oder weiteren Attributen suchen (ohne Groﬂ-/Kleinschreibung)");
                searchPanel.addSearchListener(this);
                add(searchPanel, BorderLayout.NORTH);
            }
        } else {
            /*
             * No filter / scroll pane necessary
             */
            eventPanel.setBorder(GUIUtils.getTiteledBorder(date == null ? "Ereignisse" : "‹bersicht"));

            /*
             * Show moon phase (if desired)
             */
            if (caller.getConfig().getProperty(BoolProperty.ShowMoon)) {
                MoonState moon = Moon.getMoonPhase(date);
                JPanel moonPanel = new JPanel(new BorderLayout(20, 20));
                int daysToNextFull = Moon.getDaysToNextMoonState(date, MoonState.fullMoon);
                int daysToNextNew  = Moon.getDaysToNextMoonState(date, MoonState.newMoon);
                String phaseDescription = "";
                
                if (moon != MoonState.none) {
                    // paint moon in case of special moon state
                    JLabel moonImage = null;
                    String moonToolTip = moon.getDescription();
                    if (moon == MoonState.halfDesc) {
                        moonImage = new JLabel(new ImageIcon(
                                TableOfEventsSingleDay.class.getResource("/media/moon/half-desc.png")));
                        phaseDescription = "<html>abnehmender Halbmond<br>"
                                + "<p align='center' style='border-top: 1px solid gray;'>n‰chster Neumond<br>etwa in " 
                                + (daysToNextNew == 1 ? "einem Tag" : (daysToNextNew + " Tagen")) + "</p></html>";
                    } else if (moon == MoonState.fullMoon) {
                        moonImage = new JLabel(new ImageIcon(
                                TableOfEventsSingleDay.class.getResource("/media/moon/full.png")));
                        phaseDescription = "<html>Vollmond</html>";
                    } else if (moon == MoonState.halfAsc) {
                        moonImage = new JLabel(new ImageIcon(
                                TableOfEventsSingleDay.class.getResource("/media/moon/half-asc.png")));
                        phaseDescription = "<html>zunehmender Halbmond<br>"
                                + "<p align='center' style='border-top: 1px solid gray;'>n‰chster Vollmond<br>etwa in " 
                                + (daysToNextFull == 1 ? "einem Tag" : (daysToNextFull + " Tagen")) + "</p></html>";
                    } else if (moon == MoonState.newMoon) {
                        moonImage = new JLabel(new ImageIcon(
                                TableOfEventsSingleDay.class.getResource("/media/moon/new.png")));
                        phaseDescription = "<html>Neumond</html>";
                    }
                    moonToolTip += " in der Nacht des " + date.print(PrintFormat.D_MMM_YYYY);
                    moonImage.setToolTipText(moonToolTip);
                    moonPanel.add(moonImage, BorderLayout.WEST);
                } else {
                    // only show text with number of days to next moon phase when no special moon phase right now
                    phaseDescription = daysToNextFull < daysToNextNew 
                            ? "<html>zunehmender Mond<br><p align='center' style='border-top: 1px solid gray;'>"
                                    + "n‰chster Vollmond<br>etwa in " 
                                    + (daysToNextFull == 1 ? "einem Tag" : (daysToNextFull + " Tagen")) + "</p></html>"
                            : "<html>abnehmender Mond<br><p align='center' style='border-top: 1px solid gray;'>"
                                    + "n‰chster Neumond<br>etwa in " 
                                    + (daysToNextNew == 1 ? "einem Tag" : (daysToNextNew + " Tagen")) + "</p></html>";
                }
                
                JLabel phaseDescriptionLabel = new JLabel(phaseDescription);
                phaseDescriptionLabel.setForeground(Color.gray);
                moonPanel.add(phaseDescriptionLabel, BorderLayout.CENTER);
                moonPanel.setBorder(GUIUtils.getTiteledBorder("Mondphase"));
                infoCorner.add(moonPanel, BorderLayout.EAST);
            }
            
            /*
             * Show Zodiac (if desired)
             */
            if (caller.getConfig().getProperty(BoolProperty.ShowZodiac)) {
                Zodiac zodiac = Zodiac.getByDate(date);
                JPanel zodiacPanel = new JPanel(new BorderLayout(5, 5));
                JLabel zodiacDescriptionLabel = new JLabel(
                        "<html><p align='center' style='border-bottom: 1px solid gray;'>" 
                        + zodiac.getName() + "</p><p align='center'>"
                        + zodiac.getStartDate().print(PrintFormat.DM) + "-"
                        + zodiac.getEndDate().print(PrintFormat.DM) + "</p></html>");
                zodiacDescriptionLabel.setForeground(Color.gray);
                zodiacPanel.add(new JLabel(new ImageIcon(
                        TableOfEventsSingleDay.class.getResource("/media/zodiac/" + zodiac.name() + ".png"))), 
                        BorderLayout.WEST);
                zodiacPanel.add(zodiacDescriptionLabel, BorderLayout.CENTER);
                zodiacPanel.setBorder(GUIUtils.getTiteledBorder("Sternzeichen"));
                infoCorner.add(zodiacPanel, BorderLayout.WEST);
            }
            
            if (holidays.size() > 0) {
                JPanel holidayPanel = new JPanel(new GridLayout(holidays.size(), 1));
                for (Event e : holidays) {
                    JPanel holiday = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    holiday.add(EventCategories.getIconAsLabel(e.getCategory(), 20));
                    holiday.add(new JLabel(e.getName()));
                    holiday.setComponentPopupMenu(new CalendarPopupMenu(e, null, caller, null, this, false));
                    holidayPanel.add(holiday);
                }
                holidayPanel.setBorder(GUIUtils.getTiteledBorder("Feiertage"));
                northPanel.add(holidayPanel, BorderLayout.CENTER);
            }
        }
        
        if (events.size() == 0) {
            JPanel noEvents = new JPanel();
            noEvents.add(new JLabel(holidaysOnly ? "keine Feiertage registriert"
                    : "noch keine Ereignisse geplant..."));
            eventPanel.add(noEvents, BorderLayout.NORTH);
            selectedEvent = null;
        } else {
            /*
             * Add events
             */
            List<Columns> columns;
            if (date == null && holidaysOnly) {
                /* All holidays */
                columns = Arrays.asList(Columns.id, Columns.name, Columns.date, 
                        Columns.freq, Columns.holidayType);
            } else if (date == null) {
                /* All events */
                columns = Arrays.asList(Columns.id, Columns.name, Columns.date, 
                        Columns.time, Columns.freq, Columns.category);
            
            } else {
                /* Events on a date */
                columns = Arrays.asList(Columns.id, Columns.name, Columns.time, Columns.freq);
            }
            
            eventTable = new EventTable(events, columns);
            eventTable.addMouseListener(this);
            
            eventPanel.add(new JScrollPane(eventTable));
        }
        
        add(centerPanel, BorderLayout.CENTER);
        
//        addWindowFocusListener(new WindowAdapter() {
//            public void windowGainedFocus(WindowEvent e) {
//                newButton.requestFocusInWindow();
//            }
//        });
        
        pack();
        
        setLocationRelativeTo(caller.getGUI().getFrame());
        setVisible(true);
    }
    
    /**
     * Construct a new table of events frame.
     * @param d - Date to survey (will show all events on this date)
     *         or null (will show all events)
     * @param c - Parent calendar object
     * @param nonUserOnly - <li>If true and if d is null, all non-user events are shown (holidays / special days)
     *         <li>If false and if d is null, all user events are shown
     *         <li>Ignored if d is not null
     */
    public TableOfEventsSingleDay(Date d, Calendar c, boolean nonUserOnly) {
        super(c.getGUI().getFrame(), true);
        
        this.caller = c;
        this.date = d;
        this.holidaysOnly = nonUserOnly;

        /*
         * Query events of interest (pending on date)
         */
        List<Event> all = caller.getAllEvents();
        
        if (d == null) {
            if (holidaysOnly) {
                /*
                 * Get all holiday events
                 */
                for (Event e : all) {
                    if (e.getType() != EventType.user) {
                        events.add(e);
                    }
                }
            } else {
                /*
                 * Get all user events
                 */
                for (Event e : all) {
                    if (e.getType() == EventType.user) {
                        events.add(e);
                    }
                }
            }
        } else {
            if (!d.hasTime()) {
                /*
                 * Get all events on the date specified
                 */
                for (Event e : all) {
                    if (e.match(d)) {
                        if (e.getType().isHoliday()) {
                            holidays.add(e);
                        } else {
                            events.add(e);
                        }
                    }
                }
            } else {
                /*
                 * Get all events on the date and time specified
                 */
                for (Event e : all) {
                    if (e.getDate().hasTime() && e.match(d)
                            && e.getDate().get(java.util.Calendar.HOUR_OF_DAY) 
                                == d.get(java.util.Calendar.HOUR_OF_DAY)) {
                        events.add(e);
                    }
                }
            }
        }
        
        LOG.fine("events to be shown: " + events.size());
        
        /*
         * Build frame title
         */
        String title = "";
        if (d == null) {
            title = "Alle " + (holidaysOnly ? "Feier- und Aktionstage" 
                    : "Ereignisse") + " (" + events.size() + ")";
        } else {
            long dayDiff = d.dayDiff();
            if (dayDiff == 0) {
                title = "Heute - ";
            } else if (dayDiff == 1) {
                title = "Morgen - ";
            } else if (dayDiff == 2) {
                title = "‹bermorgen - ";
            } else if (dayDiff == -1) {
                title = "Gestern - ";
            }
            title += d.print(PrintFormat.DDD_der_D_MMM_YYYY);
            
            if (d.hasTime()) {
                title += ", " + d.get(java.util.Calendar.HOUR_OF_DAY)
                    + " bis " + (d.get(java.util.Calendar.HOUR_OF_DAY) + 1) + " Uhr";
            }
        }
        this.setTitle(title);

        arrangeDialog();
    }
    
    @Override
    public void searchRequested(String searchText) {
        eventTable.filter(searchText);
        
        if (searchText.isEmpty()) {
            setTitle("Alle " + (holidaysOnly ? "Feier- und Aktionstage" 
                    : "Ereignisse") + " (" + events.size() + ")");
        } else {
            setTitle((holidaysOnly ? "Feier-/Aktionstage" : "Ereignisse") + " mit \""
                    + searchText + "\" (" + eventTable.getRowCount() + ")");
        }
    }
    
    /**
     * Determines the selected event of the table and stores it in 'selectedEvent'.
     * Configures buttons accordingly (disable edit button for holidays ...)
     */
    private void getSelectedEvent() {
        // get the selected event
        selectedEvent = eventTable.getSelectedEvent();        
        buttonPanel.setSelectedEvent(selectedEvent, date);
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        if (!SwingUtilities.isLeftMouseButton(arg0)) {
            /* ignore mouse clicks other than left mouse button. */
            return;
        }
        
        if (arg0.getSource().equals(eventTable)) {
            getSelectedEvent();
            if (selectedEvent != null && arg0.getClickCount() == 2) {
                new Notification(caller, selectedEvent);
                this.setVisible(false);
                this.dispose();
                return;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        if (!SwingUtilities.isLeftMouseButton(arg0)) {
            /* ignore mouse clicks other than left mouse button. */
            return;
        }
        
        getSelectedEvent();
    }

    @Override
    public void eventAdding() {
        this.setVisible(false);
        this.dispose();
    }

    @Override
    public void eventRemoved(Event x, boolean complete) {
        events.remove(selectedEvent);
        eventTable.removeSelectedEvent();
    }

    @Override
    public void eventEditing(Event x) {
        this.setVisible(false);
        this.dispose();
    }

    @Override
    public void eventNotification(Event x) {
        this.setVisible(false);
        this.dispose();
    }

    @Override
    public void eventDuplicating(Event x) {
        this.setVisible(false);
        this.dispose();
    }

    @Override
    public void holidayChanging() {
        this.setVisible(false);
        this.dispose();
    }
}
