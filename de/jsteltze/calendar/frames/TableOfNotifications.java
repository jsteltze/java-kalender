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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.Event.EventType;
import de.jsteltze.calendar.EventCategories;
import de.jsteltze.calendar.UI.CalendarPopupMenu;
import de.jsteltze.calendar.UI.GUIUtils;
import de.jsteltze.calendar.config.ColorSet;
import de.jsteltze.calendar.config.Configuration.BoolProperty;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.calendar.config.enums.HolidayConstants;
import de.jsteltze.calendar.tasks.RefreshTimeLabelTask;
import de.jsteltze.common.ImageButton;
import de.jsteltze.common.ImageButtonListener;
import de.jsteltze.common.Log;
import de.jsteltze.common.Music;
import de.jsteltze.common.SelectablePanel;
import de.jsteltze.common.SelectablePanelGroup;
import de.jsteltze.common.SelectablePanelListener;
import de.jsteltze.common.VerticalFlowPanel;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.Date.PrintFormat;
import de.jsteltze.common.ui.Checkbox;
import de.jsteltze.common.ui.JTextAreaContextMenu;
import de.jsteltze.common.ui.Polygon;

/**
 * Notification summary frame.
 * @author Johannes Steltzer
 *
 */
public class TableOfNotifications 
    extends JDialog 
    implements SelectablePanelListener, ImageButtonListener, ItemListener {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Maximum height this window can have. For sizes above a scroll bar will appear. */
    private static final int MAX_HEIGHT = 500;
    
    /** Minimum width this window can have. */
    private static final int MIN_WIDTH = 300;
    
    /** Font size for non-today and today events. */
    private static final int FONT_SIZE_NON_TODAY = 16,
            FONT_SIZE_TODAY = 20;
    
    /** Main component that contains all daySummaryPanels (one per day). */
    private VerticalFlowPanel mainPanel = new VerticalFlowPanel(5);
    
    /** Events to notify of. */
    private List<Event> events;
    
    /** Map of the events and their corresponding panel. */
    private Map<Event, SelectablePanel> eventMap = new HashMap<Event, SelectablePanel>();
    
    /** Map of the day diff summary groups with their corresponding flow panel. */
    private Map<Long, VerticalFlowPanel> summaryMap = new HashMap<Long, VerticalFlowPanel>();
    
    /** Parent calendar object. */
    private Calendar caller;
    
    /** Group of selectible panels (each for one event). */
    private SelectablePanelGroup spg;
    
    /** List of tasks to update time labels. */
    private List<RefreshTimeLabelTask> refresherTasks = new ArrayList<RefreshTimeLabelTask>();
    
    /** Label indicating the sort order. */
    private JLabel sortLabel = new JLabel("Sortierung: neuste zuerst");
    
    /** Welcome label with short summary. */
    private JLabel welcomeLabel = new JLabel();
    
    /** Button for changing the sort order. */
    private ImageButton sortButton = new ImageButton(
            new ImageIcon(new Polygon(Color.lightGray, null, 
                    new int[] {0, 5, 10}, new int[] {3, 10, 3}, 3).getImage()),
            new ImageIcon(new Polygon(Color.lightGray, null, 
                    new int[] {0, 5, 10}, new int[] {10, 3, 10}, 3).getImage()),
            true);
    
    /** Checkbox for hiding past events. */
    private Checkbox hidePastEvents = new Checkbox("TableOfNotificationsHidePastBox", this);
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(TableOfNotifications.class);
    
    /** The number of past and future events. */
    private int numPastEvents, numFutureEvents;
    
    /** Have ascending or descending order (by day diffs)? */
    private boolean ascOrder = false; 
    
    /**
     * Set the title for this frame depending on the number of past and future events
     * and whether or not to hide past events. 
     */
    private void setTitle() {
        numFutureEvents = countFutureEvents();
        numPastEvents   = countPastEvents();
        
        // set frame title
        if (!hidePastEvents.isSelected()) {
            if (numFutureEvents > 0 && numPastEvents > 0) {
                setTitle("Anstehende (" + numFutureEvents + ") und abgelaufene (" + numPastEvents + ") Ereignisse");
            } else if (numFutureEvents > 0) {
                setTitle("Anstehende Ereignisse (" + numFutureEvents + ")");
            } else {
                setTitle("Abgelaufene Ereignisse (" + numPastEvents + ")");
            }
        } else {
            setTitle("Anstehende Ereignisse (" + numFutureEvents + ")");
        }
        
        // set welcome text
        String welcomeText = "<html>";
        
        Date now = new Date();
        int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
        final int morning = 10, evening = 17;
        if (hour <= morning) {
            welcomeText += "Guten Morgen,<br>";
        } else if (hour >= evening) {
            welcomeText += "Guten Abend,<br>";
        } else {
            welcomeText += "Guten Tag,<br>";
        }
        
        welcomeText += "heute ist <b>" + now.print(PrintFormat.DDD_der_D_MMM) + "</b>."
                + "<br><p align='left' style='padding-top: 5px'>";
        
        if (numFutureEvents == 0) {
            welcomeText += "Es stehen keine Ereignisse an.";
        } else if (numFutureEvents == 1) {
            welcomeText += "Es steht ein Ereignis an.";
        } else {
            welcomeText += "Es stehen " + numFutureEvents + " Ereignisse an.";
        }
        welcomeText += "</p></html>";
        welcomeLabel.setText(welcomeText);
    }

    /**
     * Arranges all elements in this dialog window.
     */
    private void arrangeDialog() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.white);
        
        JPanel wrapperMain = new JPanel(new BorderLayout());
        
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        spg = new SelectablePanelGroup(this, 
                caller.getConfig().getColors()[ColorSet.SELECTED], new EtchedBorder());
        fillPanels();
        
        JScrollPane jsp = new JScrollPane(mainPanel, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setBorder(new EmptyBorder(0, 0, 0, 0));
        //jsp.getViewport().getView().setBackground(caller.getConfig().getColors()[ColorSet.NOTI]);
        wrapperMain.add(jsp, BorderLayout.CENTER);
        wrapperMain.setBackground(Color.white);
        
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        sortLabel.setFont(Const.FONT_BORDER_TEXT);
        sortLabel.setForeground(Color.GRAY);
        
        sortButton.addButtonListener(this);
        sortPanel.add(sortLabel);
        sortPanel.add(sortButton);
        sortPanel.setOpaque(false);
        sortPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
        wrapperMain.add(sortPanel, BorderLayout.NORTH);
        if (numPastEvents > 0 && numFutureEvents > 0) {
            JPanel skipPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            skipPanel.add(hidePastEvents);
            skipPanel.setOpaque(false);
            skipPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray));
            hidePastEvents.setForeground(Color.GRAY);
            wrapperMain.add(skipPanel, BorderLayout.SOUTH);
        }
        
        welcomeLabel.setFont(Const.FONT_WELCOME_TEXT);
        welcomeLabel.setBorder(new EmptyBorder(5, 10, 5, 5));
        
        add(welcomeLabel, BorderLayout.NORTH);
        add(wrapperMain, BorderLayout.CENTER);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                setVisible(false);
                dispose();
            }
        });
        
        doPack();
        setLocationRelativeTo(caller.getGUI().getFrame());
        setVisible(true);
    }
    
    /**
     * Create a new selectable panel for an event. Depending on the
     * group this panel must be arranged properly.
     * @param e - Source event
     * @param dayDiff - Pre-calculated difference of days to the events date
     * @return Panel with event information that can be selected.
     */
    private SelectablePanel createEventPanel(Event e, long dayDiff) {
        final int okIconSize = 14;                 // icon size for event_ok icon
        final int pgOffset = dayDiff == 0 ? 8 : 5; // vertical offset for arrow
        JLabel nameLabel = new JLabel();
        JLabel durationLabel = new JLabel();
        
        SelectablePanel textPanel = new SelectablePanel(new BorderLayout(5, 0));
        textPanel.addMouseListener(textPanel);
        textPanel.addMouseMotionListener(textPanel);
        textPanel.setGroup(spg);
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        /* Set the popup menu. */
        Date selectedDate = new Date();
        selectedDate.add(java.util.Calendar.DAY_OF_MONTH, (int) dayDiff);
        CalendarPopupMenu popup = new CalendarPopupMenu(e, selectedDate, caller, null, null, false);
        popup.addPopupMenuListener(textPanel);
        textPanel.setComponentPopupMenu(popup);
        
        String eventName = e.getName();
        boolean birthYearNotes = false;
        
        /* try to parse birth year in case of category birthday */
        String birthdayText = e.getBirthdayAge(caller.getWorkspace());
        if (!birthdayText.isEmpty()) {
            eventName += birthdayText;
            birthYearNotes = true;
        }
        
        addEventTime(e, dayDiff, labelPanel);
        addEventDurationLabel(e, dayDiff, durationLabel);
        if (!birthYearNotes) {
            addEventNotes(e, textPanel, popup);
        }
        addEventIcon(e, labelPanel, textPanel, popup, dayDiff);
        
        nameLabel.setText(eventName);
        nameLabel.setFont(Const.FONT_NOTI_TEXT);
        if (dayDiff != 0) {
            // shade for non-today events
            nameLabel.setForeground(Color.gray);
            nameLabel.setFont(Const.FONT_NOTI_TEXT.deriveFont((float) FONT_SIZE_NON_TODAY));
        }
        
        labelPanel.add(nameLabel);
        labelPanel.add(durationLabel);
        
        /* Add icon for attachment. Click on this icon will open attachment file. */
        JLabel attachmentIcon = e.getGUI().getAttachmentIcon(caller.getWorkspace());
        if (attachmentIcon != null) {
            labelPanel.add(attachmentIcon);
            attachmentIcon.addMouseMotionListener(textPanel);
        }
        
        /* Add some space */
        labelPanel.add(new JLabel("            "));
        labelPanel.setOpaque(false);
        
        ImageButton okButton = new ImageButton(
                new ImageIcon(
                        new ImageIcon(TableOfNotifications.class.getResource("/media/event_ok20.png"))
                                .getImage().getScaledInstance(okIconSize, okIconSize, Image.SCALE_SMOOTH)), 
                new ImageIcon(
                        new ImageIcon(TableOfNotifications.class.getResource("/media/event_ok20.png"))
                                .getImage().getScaledInstance(okIconSize, okIconSize, Image.SCALE_SMOOTH)), 
                false);
        okButton.addMouseMotionListener(textPanel);
        okButton.addButtonListener(this);
        okButton.setToolTipText("Ereignis aus Liste entfernen (erledigt)");
        
        Polygon pg = new Polygon(Const.COLOR_FONT_NOTI_TIME_HEADER, null, 
                new int[] {0, 0, 5}, new int[] {pgOffset, 10 + pgOffset, 5 + pgOffset}, 3);
        
        textPanel.add(pg, BorderLayout.WEST);
        textPanel.add(labelPanel, BorderLayout.CENTER);
        textPanel.add(okButton, BorderLayout.EAST);
        spg.add(textPanel);
        return textPanel;
    }
    
    /**
     * Add the event icon (if applicable).
     * @param e - Source event with icon or not
     * @param labelPanel - Panel for adding the icon
     * @param textPanel - Listener for mouse events on the icon
     * @param popup - Popup menu for context menu clicks on the icon
     * @param dayDiff - Pre-calculated difference of days to the events date. 
     * Non-today events shall have a smaller font size and therefore a smaller icon. 
     */
    private void addEventIcon(Event e, JPanel labelPanel, SelectablePanel textPanel, 
            CalendarPopupMenu popup, long dayDiff) {
        /* Add event category icon. */
        if (e.getCategory() != null) {
            JLabel iconLabel = EventCategories.getIconAsLabel(e.getCategory(), 
                    dayDiff == 0 ? FONT_SIZE_TODAY : FONT_SIZE_NON_TODAY - 1);
            if (iconLabel != null) {
                iconLabel.addMouseListener(textPanel);
                iconLabel.addMouseMotionListener(textPanel);
                iconLabel.setComponentPopupMenu(popup);
                labelPanel.add(iconLabel);
                labelPanel.add(new JLabel(" "));
            }
        }
    }
    
    /**
     * Add an event duration statement if the event lasts more than one day.
     * @param e - Source event with a certain duration
     * @param dayDiff - Difference of days from today
     * @param durationLabel - Container for adding the duration statement
     */
    private void addEventDurationLabel(Event e, long dayDiff, JLabel durationLabel) {
        /*
         * Determine the duration of this event
         */
        long duration = -1;
        if (e.getEndDate() != null && dayDiff >= 0) {
            if (dayDiff != 0) {
                duration = e.getEndDate().dayDiff(e.getDate()) + 1;
            } else if (dayDiff == 0) {
                duration = e.getEndDate().dayDiff();
            }
        }
        
        if (dayDiff == 0) {
            /* add duration statement */
            if (duration == 0) {
                durationLabel.setText(" (letzter Tag) ");
            } else if (duration == 1) {
                durationLabel.setText(" (geht noch bis morgen) ");
            } else if (duration == 2) {
                durationLabel.setText(" (geht noch bis übermorgen) ");
            } else if (duration != -1) {
                durationLabel.setText(" (geht noch " + (duration + 1) + " Tage) ");
            }
        
        } else if (dayDiff > 0 && duration != -1) {
            durationLabel.setText(" (geht " + duration + " Tage) ");
        }
    }
    
    /**
     * Add the event time label (if applicable).
     * @param e - Source event with a time or not
     * @param dayDiff - Difference of days from today
     * @param labelPanel - Container for adding the time label
     */
    private void addEventTime(Event e, long dayDiff, JPanel labelPanel) {
        if (e.getDate().hasTime()) {
            String timeString = "";
            JLabel timeLabel  = new JLabel();
            Font timeFont     = Const.FONT_NOTI_TEXT;
            
            if (dayDiff == 0) {
                timeString = e.getDate().print(PrintFormat.MIN_DIFF) + ": ";
                RefreshTimeLabelTask refreshTask = new RefreshTimeLabelTask(timeLabel, e);
                refreshTask.start();
                refresherTasks.add(refreshTask);
            } else {
                timeString = e.getDate().print(PrintFormat.Hmm) + " - ";
                timeFont   = timeFont.deriveFont((float) FONT_SIZE_NON_TODAY);
            }
            timeLabel.setText(timeString);
            timeLabel.setForeground(Const.COLOR_FONT_NOTI_TIME_HEADER);
            timeLabel.setFont(timeFont);
            labelPanel.add(timeLabel);
        }
    }
    
    /**
     * Add a text area with the event notes (if applicable).
     * @param e - Source event with notes or not
     * @param textPanel - Container for adding the text area
     * @param popup - Popup menu for context menu clicks on the text area
     */
    private void addEventNotes(Event e, SelectablePanel textPanel, CalendarPopupMenu popup) {
        /*
         * Add icon for notes.
         * Click on this icon will open frame with text field.
         */
        String notes = "";
        if (e.getType() == EventType.time_shift) {
            HolidayConstants h = HolidayConstants.getByName(e.getName());
            if (h != null) {
                notes = h.getDescription();
            }
        } else {
            notes = e.getNotes(caller.getWorkspace());
        }
        
        if (notes != null && !notes.isEmpty()) {
            JPanel notesPanel = new JPanel(new BorderLayout());
            JLabel notesIconLabel = new JLabel(
                    new ImageIcon(TableOfNotifications.class.getResource("/media/notes20.png")));
            notesIconLabel.setToolTipText("Notizen zu diesem Ereignis");
            notesIconLabel.addMouseListener(textPanel);
            notesIconLabel.setComponentPopupMenu(popup);
            notesIconLabel.setOpaque(false);
            notesPanel.add(notesIconLabel, BorderLayout.WEST);
            JTextArea notesArea = new JTextArea(2, 1);
            notesArea.setEditable(false);
            notesArea.setFont(Const.FONT_EVENT_NOTES);
            notesArea.setForeground(Color.gray);
            notesArea.setLineWrap(true);
            notesArea.setWrapStyleWord(true);
            notesArea.setComponentPopupMenu(new JTextAreaContextMenu(notesArea));
            notesArea.setText(notes);
            notesArea.setCaretPosition(0);
            notesPanel.add(new JScrollPane(notesArea), BorderLayout.CENTER);
            notesPanel.setOpaque(false);
            notesPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
            notesPanel.addMouseMotionListener(textPanel);
            notesPanel.addMouseListener(textPanel);
            textPanel.add(notesPanel, BorderLayout.SOUTH);
        }
    }
    
    /**
     * Fills the mainPanel with content according to the events to be shown.
     */
    private void fillPanels() {
        boolean firstRun = true;
        long previousDayDiff = -1;
        VerticalFlowPanel currentPanel = null;
        
        /* for all events ... */
        for (Event e : events) {
            
            /* 
             * depending on the difference of days of the events date
             * assign this events in group of same day differences 
             */
            long dayDiff = e.getNextDate().dayDiff();
            if (firstRun || dayDiff != previousDayDiff) {
                VerticalFlowPanel daySummaryPanel = new VerticalFlowPanel(8);
                daySummaryPanel.setBorder(
                        dayDiff == 0 
                        ? GUIUtils.getTiteledBorder(e.getNextDate().print(PrintFormat.DAY_DIFF))
                        // 5px left padding for non-today events
                        : GUIUtils.getTiteledBorder(e.getNextDate().print(PrintFormat.DAY_DIFF), false, 5, 0, 0, 0));
                
                currentPanel = daySummaryPanel;
                mainPanel.add(daySummaryPanel);
                firstRun = false;
                
                /* add this new summary group to the map */ 
                summaryMap.put(dayDiff, daySummaryPanel);
            }
            
            /* create and add an event panel */
            SelectablePanel eventPanel = createEventPanel(e, dayDiff);
            currentPanel.add(eventPanel);
            eventMap.put(e, eventPanel);
            
            previousDayDiff = dayDiff;
        }
    }
    
    /**
     * Set the natural size of this frame. If height exceeds 500px,
     * a scroll bar will appear.
     */
    private void doPack() {
        pack();
        
        Dimension size = this.getSize();
        int height = size.height > MAX_HEIGHT ? MAX_HEIGHT : size.height + 8;
        int width = size.width > MIN_WIDTH ? size.width + 20 : MIN_WIDTH + 20;
        this.setSize(width, height);
    }
    
    /**
     * Count the number of events which take place in the future.
     * @return number of future events.
     */
    private int countFutureEvents() {
        Date today = new Date();
        int count = 0;
        for (Event e : events) {
            if (e.getNextDate().dayDiff(today) >= 0) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Count the number of events which took place in the past.
     * @return number of past events.
     */
    private int countPastEvents() {
        Date today = new Date();
        int count = 0;
        for (Event e : events) {
            if (e.getNextDate().dayDiff(today) < 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * Launch a new notification summary.
     * @param c - Parent calendar object
     * @param events - Events to notify
     */
    public TableOfNotifications(Calendar c, List<Event> events) {
        super(c.getGUI().getFrame(), "Anstehende Termine");

        if (events == null) {
            return;
        }
        
        this.caller = c;
        this.events = Event.sortByDate(events, true, ascOrder);

        setIconImage(new ImageIcon(TableOfNotifications.class.getResource("/media/calendar_new64.png")).getImage());
        setTitle();

        arrangeDialog();
        
        if (c.getConfig().getProperty(BoolProperty.PlayTheme)) {
            try {
                if (c.getConfig().getTheme() == null) {
                    Music.playTheme(Const.DEFAULT_THEME, true, null);
                } else {
                    Music.playTheme(c.getConfig().getTheme(), false, null);
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "error while trying to play music file...", ex);
            }
        }
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                for (RefreshTimeLabelTask rtlt : refresherTasks) {
                    rtlt.quit();
                }
            }
        });

//        this.setAlwaysOnTop(true);
        caller.setTableOfNotifications(this);
    }
    
    /**
     * Returns a list of sorted long values corresponding to the currently
     * used day diffs.
     * @param asc - Ascending or descending order (true for ascending)
     * @return sorted array of indices.
     */
    private Long[] getSortedSummaryIndex(boolean asc) {
        Long[] list = summaryMap.keySet().toArray(new Long[0]);
        
        if (asc) {
            Arrays.sort(list);
        } else {
            Arrays.sort(list, Collections.reverseOrder());
        }
        
        return list;
    }
    
    /**
     * Method to be called when an event was added.
     * This will added the corresponding panel.
     * @param e - Added event
     */
    public void eventAdded(Event e) {
        long dayDiff = e.getNextDate().dayDiff();
        SelectablePanel eventPanel = createEventPanel(e, dayDiff);
        events.add(e);
        eventMap.put(e, eventPanel);
        
        /* retrieve the proper summary group panel */
        VerticalFlowPanel summaryPanel = summaryMap.get(dayDiff);
        if (summaryPanel == null) {
            /* 
             * No proper summary panel with this day diff exists
             * -> we have to create a new day diff group and insert it at the right position 
             */
            summaryPanel = new VerticalFlowPanel(8);
            summaryPanel.setBorder(
                    dayDiff == 0 
                    ? GUIUtils.getTiteledBorder(e.getNextDate().print(PrintFormat.DAY_DIFF))
                    // 5px left padding for non-today events
                    : GUIUtils.getTiteledBorder(e.getNextDate().print(PrintFormat.DAY_DIFF), false, 5, 0, 0, 0));
            
            summaryMap.put(dayDiff, summaryPanel);
            
            /* finally (because VerticialFlowPanel does not support insert at index) remove all and add all */
            mainPanel.removeAll();
            Long[] sortedList = getSortedSummaryIndex(ascOrder);
            for (Long l : sortedList) {
                mainPanel.add(summaryMap.get(l));
            }
        } 
        
        /* in any case: add the event panel to the summary group panel */
        summaryPanel.add(eventPanel);
        
        setTitle();
        this.doPack();
    }

    /**
     * Method to be called when an event was removed.
     * This will remove the corresponding panel.
     * @param e - Removed event
     * @param allowFrameUpdate - Allow updating the frame properly (pack()). 
     * Check if the event to remove is the last one -> in that case close the whole frame  
     */
    public void eventRemoved(Event e, boolean allowFrameUpdate) {
        long dayDiff = e.getNextDate().dayDiff();
        
        VerticalFlowPanel summaryPanel = summaryMap.get(dayDiff);
        SelectablePanel eventPanel = eventMap.get(e);
        if (summaryPanel != null && eventPanel != null) {
            summaryPanel.removeComponent(eventPanel);
            
            eventMap.remove(e);
            events.remove(e);
            
            if (summaryPanel.getComponents().length == 0) {
                // summary panel has no more elements
                mainPanel.removeComponent(summaryPanel);
                summaryMap.remove(dayDiff);
            }
        } else {
            LOG.warning("cannot remove event: one of the panels has null value!");
        }
        
        if (allowFrameUpdate) {
            /* close if no more events to show */
            if (mainPanel.getComponents().length == 0) {
                this.setVisible(false);
                this.dispose();
                return;
            }
            
            setTitle();
            this.doPack();
        }
    }
    
    /**
     * Method to be called when an event was edited.
     * This will edit the corresponding panel.
     * @param e - Edited event
     */
    public void eventEdited(Event e) {
        /* 
         * there can be many types of event changes (date changed, name changed ...)
         * so it seems the safest way to remove the old panel and create a new one 
         */
        
        Event toRemove = null;
        for (Event ev : events) {
            if (ev.getID() == e.getID()) {
                toRemove = ev;
                break;
            }
        }
        
        if (toRemove != null) {
            eventRemoved(toRemove, false);
        }
        eventAdded(e);
    }
    
    /**
     * Method to be called when an frequent event was removed on a single date.
     * If this table of notifications contains this event at this date, the line will be
     * removed.
     * @param e - Frequent event to be removed at a specific date
     * @param exceptionDate - Date to be removed from the list of dates of the event
     */
    public void eventExceptionAdded(Event e, Date exceptionDate) {
        long dayDiff = exceptionDate.dayDiff();
        
        if (events.contains(e) && summaryMap.containsKey(dayDiff)) {
            eventRemoved(e, true);
        }
    }
    
    /**
     * Returns the event displayed in a selectable panel.
     * @param sp - Selectable panel to get event from
     * @return Event
     */
    private Event getSelectedEvent(SelectablePanel sp) {
        for (Entry<Event, SelectablePanel> entry : eventMap.entrySet()) {
            if (sp.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void panelSelected(SelectablePanel sp) {
//        selectedEvent = getSelectedEvent(sp);
    }
    
    @Override
    public void panelDoubleClicked(SelectablePanel sp) {
        Event selectedEvent = getSelectedEvent(sp);
        new Notification(caller, selectedEvent);
    }

    @Override
    public void buttonPressed(ImageButton x) {
        if (x.equals(sortButton)) {
            /*
             * Sort button pressed
             */
            ascOrder = sortButton.isPressed();
            if (ascOrder) {
                sortLabel.setText("Sortierung: älteste zuerst");
            } else {
                sortLabel.setText("Sortierung: neuste zuerst");
            }
    
            mainPanel.removeAll();
            Long[] sortedList = getSortedSummaryIndex(ascOrder);
            for (Long l : sortedList) {
                mainPanel.add(summaryMap.get(l));
            }
            doPack();
        } else {
            /*
             * Event ok button pressed
             */
            
            // find related event panel
            Event eventToRemove = null;
            for (Entry<Event, SelectablePanel> entry : eventMap.entrySet()) {
                Component[] components = entry.getValue().getComponents();
                for (Component c : components) {
                    if (c.equals(x)) {
                        eventToRemove = entry.getKey();
                        break;
                    }
                }
            }
            
            // remove event from view
            if (eventToRemove != null) {
                eventRemoved(eventToRemove, true);
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(hidePastEvents)) {
            if (hidePastEvents.isSelected()) {
                /* Hide the panels with a negative dayDiff */
                for (Long key : summaryMap.keySet()) {
                    if (key < 0) {
                        summaryMap.get(key).setVisible(false);
                    }
                }
            } else {
                /* Set all panels visible again */
                for (VerticalFlowPanel panel : summaryMap.values()) {
                    panel.setVisible(true);
                }
            }
            
            setTitle();
            doPack();
        }
    }
}
