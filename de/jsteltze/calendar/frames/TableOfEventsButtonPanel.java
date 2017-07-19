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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.Event.EventType;
import de.jsteltze.calendar.EventListener;
import de.jsteltze.calendar.Frequency;
import de.jsteltze.calendar.config.Configuration.BoolProperty;
import de.jsteltze.common.Msg;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.ui.Button;
import de.jsteltze.common.ui.JSplitButton;
import de.jsteltze.common.ui.JSplitButtonActionListener;

/**
 * Panel containing event buttons for invoking default event options.
 * @author Johannes Steltzer
 *
 */
public class TableOfEventsButtonPanel 
    extends JPanel 
    implements ActionListener, JSplitButtonActionListener {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Possible types of date selection. */
    public enum SelectionType {
        /** ALL holidays have been selected. */
        allHolidays,
        /** ALL events have been selected. */
        allEvents,
        /** ALL week days within a month have been selected. */
        weeksMonth,
        /** ALL week days within a year have been selected. */
        weeksYear,
        /** A normal range of dates have been selected. */
        normalRange;
    };
    
    /** Parent calendar object. */
    private Calendar caller;
    
    /** Selected dates (can be null -> all events; or can be one -> single day). */
    private List<Date> dates;
    
    /** Event action buttons. */
    private Button editButton, newButton, duplicateButton, remindButton;
    
    /** The event delete button (split button). */
    private JSplitButton deleteButton;
    
    /** The event deletion popup menu (for frequent events only!). */
    private JPopupMenu deletePopup = new JPopupMenu();
    /** The event deletion popup menu options (for frequent events only!). */
    private JMenuItem deleteSingleItem = new JMenuItem("nur dieses Datum", 
                    new ImageIcon(TableOfEventsButtonPanel.class.getResource("/media/event_delete_single20.png"))),
            deleteAllItem = new JMenuItem("komplette Serie", 
                    new ImageIcon(TableOfEventsButtonPanel.class.getResource("/media/event_delete_all20.png")));
    
    /** Currently selected event. */
    private Event selectedEvent = null;
    
    /** Currently selected date. */
    private Date selectedDate = null;
    
    /** Parent listener dialog. */
    private EventListener el;
    
    /** Type of selected events. */
    private SelectionType type;

    /**
     * Arranges all elements in this panel.
     */
    private void arrangeDialog() {
        
        /*
         * Add buttons
         */
        editButton = new Button("TableOfEventsButtonPanelEditButton", "/media/event_edit20.png", this);
        duplicateButton = new Button("TableOfEventsButtonPanelDuplicateButton", "/media/event_copy20.png", this);
        remindButton = new Button("TableOfEventsButtonPanelRemindButton", "/media/clock_alarm32.png", 20, this);
        newButton = new Button("TableOfEventsButtonPanelNewButton", "/media/event_new20.png", this);
        deleteButton = new JSplitButton(Msg.getMessage("TableOfEventsButtonPanelDeleteButton"));
        deleteButton.setIcon(new ImageIcon(TableOfEventsButtonPanel.class.getResource("/media/event_delete20.png")));
        deleteButton.setArrowSize(1);
        deletePopup.add(deleteSingleItem);
        deletePopup.add(deleteAllItem);
        
        if (type.equals(SelectionType.allHolidays)) {
            // Edit tooltip for new event button if we have holidays 
            newButton.setToolTipText("TableOfEventsButtonPanelNewButtonTooltip2");
        }
        
        if (!caller.getConfig().getProperty(BoolProperty.ButtonTexts)) {
            // Remove the button texts
            editButton.setText("");
            duplicateButton.setText("");
            remindButton.setText("");
            newButton.setText("");
            deleteButton.setText("");
        }
        
        add(newButton);
        add(editButton);
        add(duplicateButton);
        add(deleteButton);
        add(remindButton);
        
        deleteButton.addJSplitButtonActionListener(this);
        deleteSingleItem.addActionListener(this);
        deleteAllItem.addActionListener(this);

        if (selectedEvent == null) {
            editButton.setEnabled(false);
            duplicateButton.setEnabled(false);
            deleteButton.setEnabled(false);
            remindButton.setEnabled(false);
        
        } else if (selectedEvent.getType() != EventType.user) {
            editButton.setEnabled(false);
            duplicateButton.setEnabled(false);
        }
    }
    
    /**
     * Construct a new button panel for events.
     * @param dates - Selected dates for creating a new event. Possible values:
     *      <li>null - means todays date will be preset for a new event
     *      <li>single date (size 1) - this date will be preset for a new event
     *      <li>list of dates - these dates will be preset for a new event 
     * @param c - Parent calendar object
     * @param el - Event listener to be notified when a button was clicked
     * @param type - Selection type. This is relevant for the reaction on new event button
     */
    public TableOfEventsButtonPanel(List<Date> dates, Calendar c, EventListener el, SelectionType type) {
        super();
        
        this.caller = c;
        this.dates = dates;
        this.el = el;
        this.type = type;

        arrangeDialog();
    }
    
    @Override
    public void actionPerformed(ActionEvent a) {
        
        /*
         * If Java Applet: cancel
         */
        if (caller.getGUI().getApplet() != null) {
            caller.getGUI().getApplet().newSelection();
            
            el.eventAdding();
            return;
        }
        
        /*
         * Edit event button 
         */
        if (a.getSource().equals(editButton)) {
            new EditEvent(caller, selectedEvent, false, null);
            el.eventEditing(selectedEvent);
        
        /*
         * Duplicate event button 
         */
        } else if (a.getSource().equals(duplicateButton)) {
            new EditEvent(caller, selectedEvent, true, null);
            el.eventDuplicating(selectedEvent);
        
        /*
         * Delete complete event series (for frequent events: all occurrences)
         */
        } else if (a.getSource().equals(deleteAllItem)) {
            deleleEvent(true);
        
        /*
         * Delete event on single date (for frequent events: only at this date)
         */
        } else if (a.getSource().equals(deleteSingleItem)) {
            deleleEvent(false);

        /* 
         * New event button 
         */
        } else if (a.getSource().equals(newButton)) {
            switch (type) {
            case allHolidays:
                new Settings(caller, Settings.TAB_HOLIDAYS);
                el.holidayChanging();
                break;
            case weeksMonth:
            case weeksYear:
                caller.newWeeklySelection(dates.get(0), type == SelectionType.weeksYear);
                el.eventAdding();
                break;
            case allEvents:
                new EditEvent(caller, new Date());
                el.eventAdding();
                break;
            default:
                if (dates == null) {
                    new EditEvent(caller, new Date());
                } else if (dates.size() == 1) {
                    new EditEvent(caller, dates.get(0));
                } else {
                    caller.newSelection(dates, true);
                }
                el.eventAdding();
                break;
                
            }

        /* 
         * Notify button 
         */
        } else if (a.getSource().equals(remindButton)) {
            new Notification(caller, selectedEvent);
            el.eventNotification(selectedEvent);
        }
    }
    
    /**
     * Delete the selected event.
     * @param complete - Delete complete event (all occurrences) or add an exception
     * date to the event
     */
    private void deleleEvent(boolean complete) {
        boolean deleteSuccessful;
        if (complete) {
            deleteSuccessful = caller.deleteRequested(selectedEvent);
        } else {
            caller.addException(selectedEvent, selectedDate);
            deleteSuccessful = true;
        }
        
        if (selectedEvent.getType() != EventType.user) {
            // The selected event is no user event (but e.g. holiday)
            // So lets close this dialog.
            el.holidayChanging();
            return;
        }

        if (deleteSuccessful) {
            // Remove the corresponding row if deleting was successful
            el.eventRemoved(selectedEvent, complete);
            setSelectedEvent(null, null);
        }
    }
    
    /**
     * Reset the event delete button to be a normal button
     * without popup menu.
     */
    private void resetDeleteButton() {
        deleteButton.setArrowSize(1);
        deleteButton.setPopupMenu(null);
        deleteButton.setAlwaysDropDown(false);
        deleteButton.repaint();
    }
    
    /**
     * Set an event as selected event. According to the event
     * properties the buttons will be modified.
     * @param x - Selected event
     * @param date - Selected date
     */
    public void setSelectedEvent(Event x, Date date) {
        this.selectedEvent = x;
        this.selectedDate = date;
        
        // disable all buttons if event is null (non selected)
        if (selectedEvent == null) {
            editButton.setEnabled(false);
            duplicateButton.setEnabled(false);
            resetDeleteButton();
            deleteButton.setEnabled(false);
            remindButton.setEnabled(false);
            return;
        
        // disable edit buttons if event is a holiday
        } else if (selectedEvent.getType() != EventType.user) {
            editButton.setEnabled(false);
            duplicateButton.setEnabled(false);
        
        // enable all buttons if event is a normal user event
        } else {
            editButton.setEnabled(true);
            duplicateButton.setEnabled(true);
        }
        
        // those buttons are enabled for all types of events
        deleteButton.setEnabled(true);
        remindButton.setEnabled(true);
            
        if (type != SelectionType.allEvents && type != SelectionType.allHolidays) {
            // Set the delete button properly (depending on the events frequency)
            if (selectedEvent.getType() == EventType.user 
                    && selectedEvent.getFrequency() != Frequency.OCCUR_ONCE) {
                deleteButton.setArrowSize(8);
                deleteButton.setPopupMenu(deletePopup);
                deleteButton.setAlwaysDropDown(true);
                deleteButton.repaint();
            } else {
                resetDeleteButton();
            }
        }
    }

    @Override
    public void buttonClicked(ActionEvent e) {
        /* 
         * Delete event button 
         */
        if (e.getSource().equals(deleteButton)) {
            deleleEvent(true);
        }
    }

    @Override
    public void splitButtonClicked(ActionEvent e) {
        // The delete button is now a JSplitButton -> so we do nothing here
        // because the delete popup menu appears and provides the delete functions.
    }
}
