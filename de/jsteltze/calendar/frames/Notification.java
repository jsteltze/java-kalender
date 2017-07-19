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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.Event.EventType;
import de.jsteltze.calendar.EventCategories;
import de.jsteltze.calendar.EventExportHandler;
import de.jsteltze.calendar.Frequency;
import de.jsteltze.calendar.UI.CalendarPopupMenu;
import de.jsteltze.calendar.UI.GUIUtils;
import de.jsteltze.calendar.config.ColorSet;
import de.jsteltze.calendar.config.Configuration.BoolProperty;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.calendar.config.enums.HolidayConstants;
import de.jsteltze.calendar.tasks.AlarmTask;
import de.jsteltze.calendar.tasks.RefreshTimeLabelTask;
import de.jsteltze.common.Log;
import de.jsteltze.common.Music;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.Date.PrintFormat;
import de.jsteltze.common.calendar.JSpinnerForTime;
import de.jsteltze.common.ui.Button;
import de.jsteltze.common.ui.JTextAreaContextMenu;

/**
 * Notification frame.
 * @author Johannes Steltzer
 *
 */
public class Notification 
    extends JDialog 
    implements ActionListener, ItemListener {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Buttons for alarm options. */
    private Button buttonAgain1, buttonAgain2; 
    
    /** Buttons for event options. */
    private JMenuItem deleteItem = new JMenuItem("Löschen", 
                    new ImageIcon(Notification.class.getResource("/media/event_delete20.png"))), 
            deleteSingleItem = new JMenuItem("nur dieses Datum", 
                    new ImageIcon(Notification.class.getResource("/media/event_delete_single20.png"))),
            deleteAllItem = new JMenuItem("komplette Serie", 
                    new ImageIcon(Notification.class.getResource("/media/event_delete_all20.png"))),
            editItem = new JMenuItem("Bearbeiten", 
                    new ImageIcon(Notification.class.getResource("/media/event_edit20.png"))),
            exportItem = new JMenuItem("Exportieren", 
                    new ImageIcon(Notification.class.getResource("/media/event_export20.png")));
    
    /** Time boxes. */
    private JComboBox<Integer> hourBox = new JComboBox<Integer>(), 
            minuteBox = new JComboBox<Integer>();
    
    /** Time spinner. */
    private JSpinnerForTime spinner;
    
    /** Some labels. */
    private JLabel label1 = new JLabel("in "),
            label2 = new JLabel(" h und "),
            label3 = new JLabel(" min"),
            label4 = new JLabel("um "),
            label5 = new JLabel(" Uhr");
    
    /** Label showing the time difference to the event. */
    private JLabel timeLabel;
    
    /** The event is today and has a time before now. */
    private boolean beforeBegin;
    
    /** Radio buttons to choose the type of reminder. */
    private JRadioButton remindByDurationButton = new JRadioButton("nach Zeitspanne:"),
            remindByTimeButton = new JRadioButton("zu Uhrzeit:");
    
    /** Event to notify about. */
    private Event event;
    
    /** Notification date. */
    private Date thisDate;
    
    /** Parent calendar object. */
    private Calendar caller;
    
    /** Thread for refreshing timeLabel each minute. */
    private RefreshTimeLabelTask refresher = null;
    
    /** Do the notes represent the birth year (in case of birthdays)? Then the notes will not be displayed. */
    private boolean birthYearNotes = false;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(Notification.class);
    
    /**
     * Creates the panel where to repeat the notification in the time interval
     * based on the beginning of the event.
     * @param dayDiff - the difference of days (compared to today) of this event
     * @return the created panel.
     */
    private JPanel getAlarmByDuration(long dayDiff) {
        final int defaultMinIndex = 30;
        JPanel alarmPanel = new JPanel(new BorderLayout(0, 0));
        JPanel alarmByDuration = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonAgain1 = new Button("NotificationRemindAgain1Button", "/media/clock_alarm32.png", 
                Button.ICON_SIZE_L, this);
        buttonAgain1.setBackground(caller.getConfig().getColors()[ColorSet.NOTI]);
        for (int i = 0; i < Date.HOURS_OF_DAY; i++) {
            hourBox.addItem(i);
        }
        for (int i = 0; i < Date.MINS_OF_HOUR; i++) {
            minuteBox.addItem(i);
        }
        hourBox.setSelectedIndex(0);
        hourBox.addItemListener(this);
        minuteBox.setSelectedIndex(defaultMinIndex);
        minuteBox.addItemListener(this);
        
        beforeBegin = event.getDate().hasTime() && dayDiff == 0;
        if (beforeBegin) {
            LOG.fine("event has a time and is today");
            long minDiff = event.getNextDate().minDiff();
            if (minDiff <= 0) {
                beforeBegin = false;
            } else if (minDiff <= defaultMinIndex) {
                int multipleOf5 = (int) (minDiff - 1) / 5;
                multipleOf5 *= 5;
                minuteBox.setSelectedIndex(multipleOf5);
            }
        }
        LOG.fine("beforeBegin=" + beforeBegin);

        if (beforeBegin) {
            remindByDurationButton.setText("vor Beginn:");
            label1 = new JLabel("");
        }
        
        remindByDurationButton.addItemListener(this);
        remindByDurationButton.setOpaque(false);
        alarmByDuration.add(remindByDurationButton);
        alarmByDuration.add(label1);
        alarmByDuration.add(hourBox);
        alarmByDuration.add(label2);
        alarmByDuration.add(minuteBox);
        if (beforeBegin) {
            label3 = new JLabel(" min vor Beginn ");
        }
        alarmByDuration.add(label3);
        alarmPanel.add(alarmByDuration, BorderLayout.CENTER);
        alarmPanel.add(buttonAgain1, BorderLayout.EAST);
        alarmByDuration.setOpaque(false);
        alarmPanel.setOpaque(false);
        return alarmPanel;
    }
    
    /**
     * Creates the panel where to repeat the notification at a time of day.
     * @return the created panel.
     */
    private JPanel getAlarmByTime() {
        JPanel alarmPanel = new JPanel(new BorderLayout(0, 0));
        JPanel alarmByClock = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonAgain2 = new Button("NotificationRemindAgain1Button", "/media/clock_alarm32.png", 
                Button.ICON_SIZE_L, this);
        buttonAgain2.setBackground(caller.getConfig().getColors()[ColorSet.NOTI]);
        Date now = new Date();
        now.add(java.util.Calendar.MINUTE, 5);
        spinner = new JSpinnerForTime(now);
        
        remindByTimeButton.addItemListener(this);
        remindByTimeButton.setOpaque(false);
        alarmByClock.add(remindByTimeButton);
        alarmByClock.add(label4);
        alarmByClock.add(spinner);
        alarmByClock.add(label5);
        alarmPanel.add(alarmByClock, BorderLayout.CENTER);
        alarmPanel.add(buttonAgain2, BorderLayout.EAST);
        alarmByClock.setOpaque(false);
        alarmPanel.setOpaque(false);
        return alarmPanel;
    }
    
    /**
     * Construct the panel with the main information (name, time, icon, duration) of the event.
     * @param dayDiff - Event dayDiff
     * @return the panel with main event information.
     */
    private JPanel createMainInfoPanel(long dayDiff) {
        /*
         * Main area: constructing timeLabel and eventLabel
         */
        String upperString = "", lowerString = "", durationString = "";
        thisDate = event.getNextDate();
        LOG.info("NOTIFICATION for event: " + thisDate.print(PrintFormat.DDMMYYYY_HHmm));
        
        /*
         * Determine the duration of this event
         */
        long duration = -1;
        if (event.getEndDate() != null && dayDiff >= 0) {
            if (dayDiff != 0) {
                duration = event.getEndDate().dayDiff(event.getDate()) + 1;
            } else if (dayDiff == 0) {
                duration = event.getEndDate().dayDiff();
            }
        }
        
        if (dayDiff == 0) {
            upperString += "Heute";
            if (thisDate.hasTime()) {
                upperString += ", " + event.getDate().print(PrintFormat.MIN_DIFF);
                
                refresher = new RefreshTimeLabelTask(this, duration == -1);
                refresher.start();
            }
            
            /* add duration statement, if this is a multi-day event (duration != -1) */
            if (duration == 0) {
                durationString += " (letzter Tag):";
            } else if (duration == 1) {
                durationString += " (geht noch bis morgen):";
            } else if (duration == 2) {
                durationString += " (geht noch bis übermorgen):";
            } else if (duration != -1) {
                durationString += " (geht noch " + (duration + 1) + " Tage):";
            }
        } else {
            upperString += event.getNextDate().print(PrintFormat.DAY_DIFF);
        }
        
        /* add duration statement */
        if (dayDiff > 0 && duration != -1) {
            durationString += " (geht " + duration + " Tage):";
        } else if (duration == -1) {
            upperString += ":";
        }
        
        lowerString += event.getName();
        if (thisDate.hasTime()) {
            lowerString += " (" + thisDate.print(PrintFormat.Hmm_Uhr) + ")";
        }
        
        /* try to parse birth year in case of category birthday */
        String birthdayText = event.getBirthdayAge(caller.getWorkspace());
        if (!birthdayText.isEmpty()) {
            lowerString += birthdayText;
            birthYearNotes = true;
        }
        
        timeLabel = new JLabel(upperString);
        JLabel duratioLabel = new JLabel(durationString);
        JLabel eventLabel = new JLabel(lowerString);
        timeLabel.setFont(Const.FONT_NOTI_TIME_HEADER);
        timeLabel.setForeground(Const.COLOR_FONT_NOTI_TIME_HEADER);
        //timeLabel.setForeground(Const.COLOR_BORDER_TITLE);
        duratioLabel.setFont(Const.FONT_NOTI_TIME_HEADER);
        duratioLabel.setForeground(Const.COLOR_FONT_NOTI_TIME_HEADER);
        eventLabel.setFont(Const.FONT_NOTI_TEXT);

        JPanel upperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        upperPanel.setOpaque(false);
        upperPanel.add(timeLabel);
        upperPanel.add(duratioLabel);
        
        JPanel lowerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lowerPanel.setOpaque(false);
        
        /*
         * Add event category icon
         */
        if (event.getCategory() != null) {
            JLabel iconLabel = EventCategories.getIconAsLabel(event.getCategory(), 20);
            if (iconLabel != null) {
                lowerPanel.add(iconLabel);
            }
        }
        
        lowerPanel.add(eventLabel);
        
        /*
         * Add icon for attachment.
         * Click on this icon will open attachment.
         */
        JLabel attachmentIcon = event.getGUI().getAttachmentIcon(caller.getWorkspace());
        if (attachmentIcon != null) {
            lowerPanel.add(attachmentIcon);
        }
        
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.add(upperPanel, BorderLayout.NORTH);
        main.add(lowerPanel, BorderLayout.CENTER);
        
        return main;
    }
    
    /**
     * Construct the panel with the event notes and the statement about reoccurence of the event
     * (if applicable).
     * @param dayDiff - Event dayDiff
     * @return the panel with additional event information.
     */
    private JPanel createAdditionalInfoPanel(long dayDiff) {
        JPanel additionalInfoPanel = new JPanel(new BorderLayout());
        additionalInfoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        additionalInfoPanel.setOpaque(false);
        
        /* For the birth year special case: no display of additional info. */
        if (birthYearNotes) {
            return additionalInfoPanel;
        }
        
        /*
         * Add a text field with the notes if applicable.
         */
        String notes = "";
        if (event.getType() != EventType.user) {
            HolidayConstants h = HolidayConstants.getByName(event.getName());
            if (h != null) {
                notes = h.getDescription();
            }
        } else {
            notes = event.getNotes(caller.getWorkspace());
        }
        
        if (!notes.isEmpty()) {
            JTextArea notesArea = new JTextArea(2, 1);
            notesArea.setText(notes);
            notesArea.setEditable(false);
            notesArea.setFont(Const.FONT_EVENT_NOTES);
            notesArea.setForeground(Color.gray);
            notesArea.setLineWrap(true);
            notesArea.setWrapStyleWord(true);
            notesArea.setComponentPopupMenu(new JTextAreaContextMenu(notesArea));
            notesArea.setCaretPosition(0);
            JLabel notesIconLabel = new JLabel(
                    new ImageIcon(Notification.class.getResource("/media/notes20.png")));
            notesIconLabel.setToolTipText("Notizen diesem Ereignis");
            additionalInfoPanel.add(notesIconLabel, BorderLayout.WEST);
            additionalInfoPanel.add(new JScrollPane(notesArea), BorderLayout.CENTER);
        }
        
        if (event.getFrequency() != Frequency.OCCUR_ONCE) {
            String frequencyInfo = "Dieses regelmäßige Ereignis tritt ";
            if (dayDiff == 0) {
                Date tomorrow = new Date();
                tomorrow.add(java.util.Calendar.DAY_OF_MONTH, 1);
                Date nextOccurance = event.getNextDate(tomorrow);
                long nextPeriod = nextOccurance.dayDiff(tomorrow) + 1;
                if (nextPeriod > 0) {
                    frequencyInfo += "in " + nextPeriod + " Tagen das nächste Mal auf.";
                } else {
                    frequencyInfo += "zum letzten Mal auf.";
                }
            } else {
                frequencyInfo += Frequency.getLabel(event.getFrequency(), event.getDate()) + " auf.";
            }
            additionalInfoPanel.add(new JLabel(frequencyInfo), BorderLayout.NORTH);
        }
        
        return additionalInfoPanel;
    }

    /**
     * Arranges all elements in this dialog window.
     */
    private void arrangeDialog() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(caller.getConfig().getColors()[ColorSet.NOTI]);
        long dayDiff = event.getNextDate().dayDiff();
        
        /*
         * Setup event menu bar
         */
        JMenuBar menuBar = new JMenuBar();
        JMenu eventMenu = new JMenu("Ereignis");
        eventMenu.add(editItem);
        
        if (event.getType().equals(EventType.user) && event.getFrequency() != Frequency.OCCUR_ONCE) {
            // delete for frequent events
            JMenu deleteMenu = new JMenu("Löschen");
            deleteMenu.setIcon(new ImageIcon(CalendarPopupMenu.class.getResource("/media/event_delete20.png")));
            deleteMenu.add(deleteSingleItem);
            deleteMenu.add(deleteAllItem);
            eventMenu.add(deleteMenu);
        } else {
            // delete for single-day events
            eventMenu.add(deleteItem);
        }
        
        eventMenu.add(exportItem);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(eventMenu);
        deleteItem.addActionListener(this);
        deleteAllItem.addActionListener(this);
        deleteSingleItem.addActionListener(this);
        editItem.addActionListener(this);
        exportItem.addActionListener(this);
        if (event.getType() != EventType.user) {
            editItem.setEnabled(false);
        }
        setJMenuBar(menuBar);

        /*
         * Alarm options
         */
        JPanel alarmOptions = new JPanel(new GridLayout(2, 1));
        alarmOptions.setBorder(GUIUtils.getTiteledBorder("Erinnerung", false, 5, 5, 0, 5));
        alarmOptions.add(getAlarmByDuration(dayDiff));
        alarmOptions.add(getAlarmByTime());
        alarmOptions.setBackground(caller.getConfig().getColors()[ColorSet.NOTI]);
        remindByTimeButton.setSelected(true);
        remindByDurationButton.setSelected(true);
        
        if (!caller.getConfig().getProperty(BoolProperty.ButtonTexts)) {
            buttonAgain1.setText("");
            buttonAgain2.setText("");
        }

        add(createMainInfoPanel(dayDiff), BorderLayout.NORTH);
        add(createAdditionalInfoPanel(dayDiff), BorderLayout.CENTER);
        add(alarmOptions, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(caller.getGUI().getFrame());
        setVisible(true);
    }

    /**
     * Launch a new notification.
     * @param c - Parent calendar object
     * @param e - Event to notify of
     */
    public Notification(Calendar c, Event e) {
        super(c.getGUI().getFrame(), "Denk dran...");

        if (e == null) {
            return;
        }
        
        c.addCurrentNoti(this);

        setIconImage(new ImageIcon(Notification.class.getResource("/media/clock_alarm32.png")).getImage());

        event = e;
        caller = c;

        ButtonGroup bg = new ButtonGroup();
        bg.add(remindByDurationButton);
        bg.add(remindByTimeButton);
        arrangeDialog();
        
        if (c.getConfig().getProperty(BoolProperty.PlayTheme)) {
            if (c.getConfig().getTheme() == null) {
                Music.playTheme(Const.DEFAULT_THEME, true, null);
            } else { 
                Music.playTheme(c.getConfig().getTheme(), false, null);
            }
        }

        this.setAlwaysOnTop(true);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent w) {
                close();
            }
        });
    }
    
    /**
     * Close this notification.
     */
    private void close() {
        if (refresher != null) {
            refresher.quit();
        }
        caller.removeCurrentNoti(this);
        setVisible(false);
        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent a) {
        boolean editEvent = false;
        
        /*
         * Notify again button clicked: start new timer
         */
        if (a.getSource().equals(buttonAgain1) || a.getSource().equals(buttonAgain2)) {
            long minDuration = 0L;
            
            if (a.getSource().equals(buttonAgain2)) {
                // notify at specific time
                Date reminderTime = spinner.getSelectedTime();
                LOG.fine("reminder time: " + reminderTime.print(PrintFormat.HHmm));
                minDuration = reminderTime.minDiff();
                LOG.fine("reminder minutes: " + minDuration);
                if (minDuration < 0) {
                    minDuration += Date.MINS_OF_DAY;
                }
            } else {
                // notify after specific time span
                long minDiff = event.getDate().minDiff();
                LOG.fine("minDiff=" + minDiff);
                if (beforeBegin && minDiff <= 0) {
                    LOG.fine("relaunch notification");
                    new Notification(caller, event);
                } else {
                    int stunden = hourBox.getSelectedIndex();
                    int minuten = minuteBox.getSelectedIndex();
                    minDuration = Date.MINS_OF_HOUR * stunden + minuten;
                    if (beforeBegin) {
                        minDuration = minDiff - minDuration;
                        LOG.fine("beforeBegin -> minDuration=" + minDuration);
                        if (minDuration < 0) {
                            minDuration = 0;
                        }
                    }
                }
            }
            
            LOG.info("schedule new notification for '" + event.getName() + "' in + " + minDuration + " min");
            Timer timer = new Timer(true);
            AlarmTask at = new AlarmTask(caller, event, minDuration * Date.MIN_1);
            caller.addAlarmTask(at);
            timer.schedule(at, minDuration * Date.MIN_1);
        
        
        /*
         * Delete event button clicked: call parent calendar for
         * deleting
         */
        } else if (a.getSource().equals(deleteItem) || a.getSource().equals(deleteAllItem)) {
            setVisible(false);
            caller.deleteRequested(event);
        
        
        /*
         * Add exception date to event
         */
        } else if (a.getSource().equals(deleteSingleItem)) {
            setVisible(false);
            caller.addException(event, thisDate);
        
        
        /*
         * Edit event button clicked: open edit event frame
         */
        } else if (a.getSource().equals(editItem)) {
            editEvent = true;
        
        
        /*
         * Export event button clicked: open file chooser
         */
        } else if (a.getSource().equals(exportItem)) {
            EventExportHandler.export(caller, event);
        }

        /*
         * Any case: close this frame
         */
        close();
        
        if (editEvent) {
            new EditEvent(caller, event, false, null);
        }
    }
    
    /**
     * Return the event of subject of this notification.
     * @return Event to notify about.
     */
    public Event getEvent() {
        return this.event;
    }
    
    /**
     * Set a time label in this notification frame.
     * @param timeLabel - New label
     */
    public void refreshTimeLabel(String timeLabel) {
        LOG.fine("refresh Label");
        this.timeLabel.setText(timeLabel);
        this.timeLabel.invalidate();
        this.pack();
    }

    @Override
    public void itemStateChanged(ItemEvent i) {
        if (i.getSource().equals(hourBox) || i.getSource().equals(minuteBox)) {
            int a = hourBox.getSelectedIndex();
            int b = minuteBox.getSelectedIndex();
            if (a == 0 && b == 0) {
                buttonAgain1.setEnabled(false);
            } else {
                buttonAgain1.setEnabled(true);
            }
        
        } else if (i.getSource().equals(remindByDurationButton)) {
            if (remindByDurationButton.isSelected()) {
                hourBox.setEnabled(true);
                minuteBox.setEnabled(true);
                buttonAgain1.setEnabled(true);
                label1.setEnabled(true);
                label2.setEnabled(true);
                label3.setEnabled(true);
            } else {
                hourBox.setEnabled(false);
                minuteBox.setEnabled(false);
                buttonAgain1.setEnabled(false);
                label1.setEnabled(false);
                label2.setEnabled(false);
                label3.setEnabled(false);
            }
        
        } else if (i.getSource().equals(remindByTimeButton)) {
            if (remindByTimeButton.isSelected()) {
                spinner.setEnabled(true);
                buttonAgain2.setEnabled(true);
                label4.setEnabled(true);
                label5.setEnabled(true);
            } else {
                spinner.setEnabled(false);
                buttonAgain2.setEnabled(false);
                label4.setEnabled(false);
                label5.setEnabled(false);
            }
        }
    }
}
