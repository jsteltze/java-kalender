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
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.EventCategories;
import de.jsteltze.calendar.EventListener;
import de.jsteltze.calendar.Frequency;
import de.jsteltze.calendar.UI.Cell;
import de.jsteltze.calendar.UI.GUIUtils;
import de.jsteltze.calendar.config.ColorSet;
import de.jsteltze.calendar.config.Configuration.EnumProperty;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.calendar.config.enums.View;
import de.jsteltze.calendar.frames.TableOfEventsButtonPanel.SelectionType;
import de.jsteltze.common.ImageButton;
import de.jsteltze.common.ImageButtonListener;
import de.jsteltze.common.Log;
import de.jsteltze.common.SelectablePanel;
import de.jsteltze.common.SelectablePanelGroup;
import de.jsteltze.common.SelectablePanelListener;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.Date.PrintFormat;

/**
 * Frame for analyzing a set of selected dates.
 * @author Johannes Steltzer
 *
 */
public class TabelOfEventsMulitDay 
    extends JDialog 
    implements ImageButtonListener, SelectablePanelListener, EventListener {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Maximum height for this frame. For sizes above a scroll bar will be used. */
    private static final int MAX_HEIGHT = 500;
    
    /** +/- buttons for extending/reducing events. */
    private ImageButton extendHolidaysButton = new ImageButton("/media/+.PNG", "/media/-.PNG", true),
            extendSpecialDaysButton = new ImageButton("/media/+.PNG", "/media/-.PNG", true), 
            extendEventsButton = new ImageButton("/media/+.PNG", "/media/-.PNG", true);
    
    /** Expand the specified group of events? */
    private boolean extendHolidays = false, extendSpecialDays = false, extendEvents = false;
    
    /** The panel with event buttons. */
    private TableOfEventsButtonPanel buttonPanel;
    
    /** Content panel, contains all events and listings. */
    private Component content;
    
    /** Selected dates. */
    private List<Date> dates;
    
    /** Parent calendar object. */
    private Calendar cal;
    
    /** Is the selection a weekly selection? */
    private boolean weekly;
    
    /** Events, holidays and special days within the selection. */
    private List<Event> events = new ArrayList<Event>(), 
            holidays = new ArrayList<Event>(),
            specialDays = new ArrayList<Event>();
    
    /** Number of labor days/weekends/holidays ... */
    private int numLabordays = 0, numWeekends = 0, numHolidays = 0, 
            /*numMoonFull = 0, numMoonHalf1 = 0, numMoonHalf2 = 0, numMoonNew = 0,*/ 
            numTotal = 0;
    
    /** Group of selectable panels. */
    private SelectablePanelGroup spg;
    
    /** All selectable panels. */
    private SelectablePanel[] eventPanels;
    
    /** Currently selected event. */
    private Event selectedEvent;
    
    /** Currently selected date. */
    private Date selectedDate;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(TabelOfEventsMulitDay.class);
    
    /**
     * Construct a multi-day analyzer frame.
     * @param cells - selected cells
     * @param cal - Parent calendar object
     * @param dates - selected dates
     * @param weekly - true for weekly selection
     */
    public TabelOfEventsMulitDay(List<Cell> cells, Calendar cal, List<Date> dates, boolean weekly) {
        super(cal.getGUI().getFrame(), "Markierter Zeitraum", true);
        
        this.cal = cal;
        this.dates = dates;
        this.weekly = weekly;
        
        /* Get the events from the selected cells and sort them */
        analyzeCells(cells);
        events = Event.sortByDate(events, false, true);
        
        setLayout(new BorderLayout());
        
        /* construct header */
        JPanel headerPanel = new JPanel();
        JLabel headerLabel;
        
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
        if (weekly) {
            headerLabel = new JLabel("Alle " 
                    + Date.dayOfWeek2String(dates.get(0).get(java.util.Calendar.DAY_OF_WEEK), false) + "e " 
                    + (cal.getConfig().getProperty(EnumProperty.DefaultView) == View.month 
                            ? "im " + Date.month2String(dates.get(0).get(java.util.Calendar.MONTH), false) + " " : "") 
                    + dates.get(0).get(java.util.Calendar.YEAR));
        } else {
            
            if (first.get(java.util.Calendar.MONTH) == last.get(java.util.Calendar.MONTH)) {
                // same month
                headerLabel = new JLabel(first.get(java.util.Calendar.DAY_OF_MONTH) + ". bis " 
                        + last.print(PrintFormat.D_MMM_YYYY));
            } else {
                // different month
                headerLabel = new JLabel(first.print(PrintFormat.D_MMM) + " bis " 
                        + last.print(PrintFormat.D_MMM_YYYY));
            }
        }
        headerLabel.setFont(Const.FONT_MULTIDAY_HEADER);
        headerPanel.add(headerLabel);
        headerPanel.setBackground(Color.white);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
        
        /* buttons for extending/hiding events */
        extendHolidaysButton.addButtonListener(this);
        extendSpecialDaysButton.addButtonListener(this);
        extendEventsButton.addButtonListener(this);
        
        buttonPanel = new TableOfEventsButtonPanel(dates, cal, this, weekly 
                ? (dates.size() > 5 ? SelectionType.weeksYear : SelectionType.weeksMonth) : SelectionType.normalRange);
        
        content = createPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        doPack();
        setLocationRelativeTo(cal.getGUI().getFrame());
        setVisible(true);
    }
    
    /**
     * Analyze the selected cells. This will fill the event/holiday list and
     * increment the counters properly.
     * @param cells - Selected cells (represent dates) to be shown in this frame
     */
    private void analyzeCells(List<Cell> cells) {
        for (Cell c : cells) {
            if (c == null || c.getDate() == null) {
                continue;
            } else if (c.isHoliday()) {
                numHolidays++;
            } else if (c.isWeekend()) {
                numWeekends++;
            } else {
                numLabordays++;
            }
            
            /*byte moon = Moon.getMoonPhase(c.getDate());
            if (moon == Moon.MOON_DEC_HALF) 
                numMoonHalf1++;
            else if (moon == Moon.MOON_INC_HALF) 
                numMoonHalf2++;
            else if (moon == Moon.MOON_FULL) 
                numMoonFull++;
            else if (moon == Moon.MOON_NEW) 
                numMoonNew++;*/
            
            /* Collect events and holidays */
            List<Event> tmpEvents = c.getEvents();
            for (Event e : tmpEvents) {
                Event tmp = e.clone();
                
                switch (e.getType()) {
                case holiday_law:
                    tmp.setDate(c.getDate());
                    holidays.add(tmp);
                    break;
                case holiday_special:
                    tmp.setDate(c.getDate());
                    specialDays.add(tmp);
                    break;
                default:
                    boolean doAdd = true;
                    for (Event etmp : events) {
                        if (etmp.getID() == e.getID() && e.getFrequency() == Frequency.OCCUR_ONCE) {
                            doAdd = false;
                            break;
                        }
                    }
                    if (!doAdd) {
                        continue;
                    }
                    
                    tmp.setDate(c.getDate());
                    if (weekly) {
                        tmp.setEndDate(null);
                    }
                    events.add(tmp);
                }
            }

            numTotal++;
        }
    }

    /**
     * Set the natural size of this frame. If height exceeds 500px,
     * a scroll bar will appear.
     */
    private void doPack() {
        pack();
        
        Dimension size = this.getSize();
        if (size.height > MAX_HEIGHT) {
            this.setSize(size.width + 20, MAX_HEIGHT + 1);
        }        
    }
    
    /**
     * Construct the main content panel, which contains the
     * listings and events.
     * @return content panel.
     */
    private Component createPanel() {
        int rows = 5, index = 0;
        boolean somethingExtended = extendHolidays || extendSpecialDays || extendEvents;
        if (extendHolidays) {
            rows += numHolidays;
        }
        if (extendSpecialDays) {
            rows += specialDays.size();
        }
        if (extendEvents) {
            rows += events.size();
        }
        JPanel properties = new JPanel(new BorderLayout());
        JPanel leftProps = new JPanel(new GridLayout(rows, 1));
        JPanel rightProps = new JPanel(new GridLayout(rows, 1));
        JPanel[] leftPanels = new JPanel[rows];
        eventPanels = new SelectablePanel[rows];
        spg = new SelectablePanelGroup(this, cal.getConfig().getColors()[ColorSet.SELECTED]);
        for (int i = 0; i < rows; i++) {
            leftPanels[i] = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, somethingExtended ? 0 : 2));
            eventPanels[i] = new SelectablePanel(new FlowLayout(FlowLayout.LEFT, 2, somethingExtended ? 0 : 2));
            leftProps.add(leftPanels[i]);
            rightProps.add(eventPanels[i]);
        }
        
        /* Headlines on the left site */
        leftPanels[index++].add(new JLabel("Wochenendtage: "));
        leftPanels[index++].add(new JLabel("gesetzliche Feiertage: "));
        if (extendHolidays) {
            index += numHolidays;
        }
        leftPanels[index++].add(new JLabel("sonstige Feiertage: "));
        if (extendSpecialDays) {
            index += specialDays.size();
        }
        leftPanels[index++].add(new JLabel("Arbeitstage: "));
        leftPanels[index++].add(new JLabel("Ereignisse: "));
        if (extendEvents) {
            index += events.size();
        }

        /* Number of events on the right site */
        index = 0;
        eventPanels[index++].add(new JLabel("" + numWeekends + "                                   "));
        
        /* Get holiday events */
        eventPanels[index++].add(new JLabel("" + numHolidays + " "));
        if (numHolidays > 0) {
            eventPanels[index - 1].add(extendHolidaysButton);
        }
        if (extendHolidays) {
            for (Event e : holidays) {
                JLabel dateLabel = new JLabel();
                String text = e.getDate().print(PrintFormat.DDMM);
                if (e.getEndDate() != null) {
                    text += "-" + e.getEndDate().print(PrintFormat.DDMM);
                }
                text += ": ";
                dateLabel.setText(text);
                dateLabel.setForeground(Color.gray);
                spg.add(eventPanels[index]);
                JLabel iconLabel = EventCategories.getIconAsLabel(e.getCategory(), 9);
                iconLabel.addMouseMotionListener(eventPanels[index]);
                iconLabel.addMouseListener(eventPanels[index]);
                eventPanels[index].add(dateLabel);
                eventPanels[index].add(iconLabel);
                eventPanels[index].add(new JLabel(e.getName()));
                eventPanels[index].setBorder(new EtchedBorder());
                eventPanels[index].addMouseMotionListener(eventPanels[index]);
                eventPanels[index].addMouseListener(eventPanels[index]);
                eventPanels[index].setGroup(spg);
                eventPanels[index++].setBackground(Color.white);
            }
        }
        
        /* Get special day events */
        eventPanels[index++].add(new JLabel("" + specialDays.size() + " "));
        if (specialDays.size() > 0) {
            eventPanels[index - 1].add(extendSpecialDaysButton);
        }
        if (extendSpecialDays) {
            for (Event e : specialDays) {
                JLabel dateLabel = new JLabel();
                String text = e.getDate().print(PrintFormat.DDMM);
                if (e.getEndDate() != null) {
                    text += "-" + e.getEndDate().print(PrintFormat.DDMM);
                }
                text += ": ";
                dateLabel.setText(text);
                dateLabel.setForeground(Color.gray);
                spg.add(eventPanels[index]);
                JLabel iconLabel = EventCategories.getIconAsLabel(e.getCategory(), 9);
                iconLabel.addMouseMotionListener(eventPanels[index]);
                iconLabel.addMouseListener(eventPanels[index]);
                eventPanels[index].add(dateLabel);
                eventPanels[index].add(iconLabel);
                eventPanels[index].add(new JLabel(e.getName()));
                eventPanels[index].setBorder(new EtchedBorder());
                eventPanels[index].addMouseMotionListener(eventPanels[index]);
                eventPanels[index].addMouseListener(eventPanels[index]);
                eventPanels[index].setGroup(spg);
                eventPanels[index++].setBackground(Color.white);
            }
        }
        
        eventPanels[index++].add(new JLabel("" + numLabordays));
        
        /* Get user events */
        eventPanels[index++].add(new JLabel("" + events.size() + " "));
        if (events.size() > 0) {
            eventPanels[index - 1].add(extendEventsButton);
        }
        if (extendEvents) {
            for (Event e : events) {
                JLabel dateLabel = new JLabel();
                String text = e.getDate().print(PrintFormat.DDMM);
                if (e.getEndDate() != null) {
                    text += "-" + e.getEndDate().print(PrintFormat.DDMM);
                }
                text += ": ";
                dateLabel.setText(text);
                dateLabel.setForeground(Color.gray);
                spg.add(eventPanels[index]);
                eventPanels[index].add(dateLabel);
                if (e.getCategory() != null) {
                    JLabel catIconLabel = EventCategories.getIconAsLabel(e.getCategory(), 9);
                    if (catIconLabel != null) {
                        eventPanels[index].add(catIconLabel);
                        catIconLabel.addMouseMotionListener(eventPanels[index]);
                        catIconLabel.addMouseListener(eventPanels[index]);
                    }
                }
                eventPanels[index].add(new JLabel(e.getName()));
                eventPanels[index].setBorder(new EtchedBorder());
                eventPanels[index].addMouseMotionListener(eventPanels[index]);
                eventPanels[index].addMouseListener(eventPanels[index]);
                eventPanels[index].setGroup(spg);
                eventPanels[index++].setBackground(Color.white);
            }
        }
        
        properties.add(leftProps, BorderLayout.WEST);
        properties.add(rightProps, BorderLayout.CENTER);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(properties, BorderLayout.NORTH);
        
        JScrollPane jsp = new JScrollPane(wrapper, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setBorder(GUIUtils.getTiteledBorder(numTotal + (weekly 
                        ? (" " + Date.dayOfWeek2String(dates.get(0).get(java.util.Calendar.DAY_OF_WEEK), false) + "e") 
                        : " zusammenhängende Tage"),
                        false, 5, 5, 0, 0));
        return jsp;
    }
    
    /**
     * Refresh this frames content (due to click on extending button).
     */
    private void refresh() {
        selectedEvent = null;
        buttonPanel.setSelectedEvent(null, null);
        
        remove(content);
        content = null;
        content = createPanel();
        add(content, BorderLayout.CENTER);
        doPack();
    }

    @Override
    public void buttonPressed(ImageButton x) {
        if (x.equals(extendHolidaysButton)) {
            extendHolidays = extendHolidaysButton.isPressed();
        
        } else if (x.equals(extendSpecialDaysButton)) {
            extendSpecialDays = extendSpecialDaysButton.isPressed();
        
        } else if (x.equals(extendEventsButton)) {
            extendEvents = extendEventsButton.isPressed();
        }
        
        refresh();
    }
    
    /**
     * Determines the event displayed in a selectable panel and fills the variables 'selectedEvent'
     * and 'selectedDate' accordingly.
     * @param sp - Selectable panel to get event from
     */
    private void getSelectedEvent(SelectablePanel sp) {
        int plainIndex = eventPanels.length;
        for (int i = 0; i < eventPanels.length; i++) {
            if (sp.equals(eventPanels[i])) {
                plainIndex = i;
                break;
            }
        }
        LOG.fine("getSelectedEvent: plain_index=" + plainIndex);
        if (plainIndex == eventPanels.length) {
            selectedEvent = null;
        }
        
        int startIndexHolidays = 2;
        int startIndexSpecial = startIndexHolidays + 1;
        if (extendHolidays) {
            startIndexSpecial += holidays.size();
        }
        int startIndexEvents = startIndexSpecial + 2;
        if (extendSpecialDays) {
            startIndexEvents += specialDays.size();
        }
        
        if (plainIndex < startIndexSpecial) {
            selectedEvent = holidays.get(plainIndex - startIndexHolidays);
        } else if (plainIndex < startIndexEvents) {
            selectedEvent = specialDays.get(plainIndex - startIndexSpecial);
        } else {
            selectedEvent = events.get(plainIndex - startIndexEvents);
        }
        selectedDate = selectedEvent.getDate();
        
        if (selectedEvent.getID() != -1) {
            selectedEvent = cal.getEventByID(selectedEvent.getID());
        }
    }
    
    @Override
    public void panelSelected(SelectablePanel source) {
        getSelectedEvent(source);
        buttonPanel.setSelectedEvent(selectedEvent, selectedDate);
    }
    
    @Override
    public void panelDoubleClicked(SelectablePanel source) {
        getSelectedEvent(source);
        new Notification(cal, selectedEvent);
    }

    @Override
    public void eventAdding() {
        this.setVisible(false);
        this.dispose();
    }

    @Override
    public void eventRemoved(Event x, boolean complete) {
        if (!complete) {
            // remove the one single event at the selected date
            int index = 0;
            for ( ; index < events.size(); index++) {
                if (events.get(index).getID() == x.getID() && events.get(index).getDate().sameDateAs(selectedDate)) {
                    break;
                }
            }
            events.remove(index);
        } else {
            // remove ALL occurrences of the selected event
            int index = 0;
            for ( ; index < events.size(); index++) {
                if (events.get(index).getID() == x.getID()) {
                    events.remove(index--);
                }
            }
        }
        
        refresh();
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
