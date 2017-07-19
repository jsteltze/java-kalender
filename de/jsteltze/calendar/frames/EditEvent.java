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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.palantir.hedgehog.ui.widgets.timechooser.clockhands.ClockBackground;
import com.palantir.hedgehog.ui.widgets.timechooser.clockhands.ClockTimeChooser;
import com.palantir.hedgehog.ui.widgets.timechooser.clockhands.ClockTimeChooser.CLOCK_LABEL;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.Event.EventType;
import de.jsteltze.calendar.EventCategories;
import de.jsteltze.calendar.Frequency;
import de.jsteltze.calendar.XMLParser;
import de.jsteltze.calendar.UI.GUIUtils;
import de.jsteltze.calendar.config.Configuration;
import de.jsteltze.calendar.config.Configuration.EnumProperty;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.calendar.config.enums.RemindOption;
import de.jsteltze.common.ImageButton;
import de.jsteltze.common.ImageButtonListener;
import de.jsteltze.common.LimitedTextField;
import de.jsteltze.common.LinkLabel;
import de.jsteltze.common.Log;
import de.jsteltze.common.Msg;
import de.jsteltze.common.VerticalFlowPanel;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.JSpinnerForDate;
import de.jsteltze.common.calendar.JSpinnerForTime;
import de.jsteltze.common.io.Copy;
import de.jsteltze.common.ui.Button;
import de.jsteltze.common.ui.Checkbox;
import de.jsteltze.common.ui.FocusPolicy;
import de.jsteltze.common.ui.JComboBoxIconRenderer;
import de.jsteltze.common.ui.JTextAreaContextMenu;

/**
 * Frame for editing / creating events.
 * @author Johannes Steltzer
 *
 */
public class EditEvent 
    extends JDialog 
    implements ActionListener, KeyListener, ItemListener, WindowListener, ImageButtonListener, DateListListener {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Maximum length for event name. */
    private static final int MAX_LEN_NAME = 30;
    
    /** Number of columns (by default) for the notes text area. */
    private static final int COLUMNS_NOTES = 25;
    
    /** Shift the window by 100px to the top. */
    private static final int SHIFT_POS_Y = 100;
    
    /** Text for extending (expert mode). */
    private static final String EXTEND = "erweitert ";
    
    /** Text for reducing (easy mode). */
    private static final String REDUCE = "einfach ";
    
    /** Label for extending / reducing. */
    private LinkLabel extendLabel = new LinkLabel(EXTEND);
    
    /** Button for extending / reducing. */
    private ImageButton extendButton = new ImageButton("/media/+.PNG", "/media/-.PNG", true);
    
    /** Name of event (must). */
    private LimitedTextField nameField = new LimitedTextField(MAX_LEN_NAME, MAX_LEN_NAME);
    
    /** Attachment file path (optional). */
    private JTextField attachField = new JTextField(20);
    
    /** Additional notes (optional). */
    private JTextArea notesField = new JTextArea(3, COLUMNS_NOTES);
    
    /** Starting an ending date (auto-filled). */
    private JSpinnerForDate startSpinner, endSpinner;
    
    /** Radio buttons for choosing frequency. */
    private JRadioButton freq1  = new JRadioButton("nach Datum:"), 
            freq2 = new JRadioButton("nach Wochentag im Monat:"), 
            freq3 = new JRadioButton("nach Abstand:"), 
            freq4 = new JRadioButton("nach Monatsende:");
    
    /** Frequency boxes for the freq1 option. */
    private Checkbox wBox = new Checkbox("EditEventWeeklyBox"), 
            mBox = new Checkbox("EditEventMonthlyBox"),
            yBox = new Checkbox("EditEventYearlyBox");
    
    /** Drop down lists for the freq3 option. */
    private JComboBox<String> intervalBox = new JComboBox<String>(), 
            unitBox = new JComboBox<String>();
    
    /** Maximum quantity for for the frequency (in days or months, ...). */
    private static final int MAX_QUANT = 30;
    
    /** Frequency labels (can have different texts, depending on the date); they can be disabled. */
    private JLabel byWeekdayLabel = new JLabel(), 
            byIntervalLabel = new JLabel(), 
            byEndOfMonthLabel = new JLabel();
    
    /** Button for the exception dates. */
    private Button exceptionButton = new Button("EditEventExceptionButton", this);
    
    /** The clock label; can be disabled. */
    private JLabel clockLabel = new JLabel("Uhr");
    
    /** Event with time (optional). */
    private Checkbox timeBox = new Checkbox("EditEventTimeBox");
    
    /** Panel that contains all time components. */
    private JPanel timePanel;
    
    /** Event time (optional). */
    private JSpinnerForTime timeSpinner;
    
    /** Clock for choosing time. */
    private ClockTimeChooser clockTimeChooser;
    
    /** Event with attachment (optional). */
    private Checkbox attachmentBox = new Checkbox("EditEventAttachmentBox");
    
    /** Event categories (optional). */
    private JComboBox<String> categoryBox  = new JComboBox<String>();
    
    /** Reminder selection. */
    private JComboBox<String> remindBox = new JComboBox<String>();
    
    /** Event with the default reminder or with an individual one. */
    private JRadioButton useDefaultRemind = new JRadioButton("Standard:"), 
            useIndividualRemind = new JRadioButton("Individuell:");
    
    /** In case default reminder is selected: default remind label. */
    private JLabel defaultRemindLabel;

    /** Event to be edited. */
    private Event event;
        
    /** Parent calendar object. */
    private Calendar caller;
    
    /** Control buttons. */
    private Button okButton = new Button("EditEventSaveButton", "/media/save16.png", Button.ICON_SIZE_S, this), 
            cancelButton = new Button("EditEventCancelButton", this), 
            chooseButton = new Button("EditEventChooseButton", this);
    
    /** Area to contain extended settings. */
    private JPanel extendedSettingsPanel;
    
    /** Flag for creating a copy of the event. */
    private boolean duplicate = false;
    
    /** Is frequency setting enabled for with the specific setting? */
    private boolean frequencySupported = false;
    
    /** List containing multiple dates for creating one event on these dates at once. */
    private List<Date> multipleDates = null;
    
    /** Local list of exceptions from the frequency. We use this local list because editing can be cancelled. */
    private List<Date> exceptionDates = new ArrayList<Date>();
    
    /** List of dates on which to add the event. null means only one date, according to event property. */ 
    private LinkLabel multipleDatesLabel = null;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(EditEvent.class);
    
    /**
     * Arranges all elements in the dialog window.
     */
    private void arrangeDialog() {
        setLayout(new BorderLayout());

        /* General section */
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(createGeneralPanel(), BorderLayout.CENTER);
        
        /* Add switch for extending (expert mode) / reducing (easy mode) */
        JPanel extendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 2));
        extendLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent m) {
                if (m.getSource().equals(extendLabel)) {
                    extendButton.setPressed(!extendButton.isPressed());
                }
            }
        });
        extendButton.addButtonListener(this);
        extendPanel.add(extendLabel);
        extendPanel.add(extendButton);
        extendPanel.add(new JLabel("    "));
        northPanel.add(extendPanel, BorderLayout.SOUTH);
        northPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(northPanel, BorderLayout.NORTH);
        
        /* Additional information section */
        extendedSettingsPanel = new JPanel(new BorderLayout());
        
        VerticalFlowPanel additionalEast = new VerticalFlowPanel();
        additionalEast.add(createTimePanel());
        additionalEast.add(createAttachmentPanel());
        additionalEast.add(createCategoryPanel());
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createNotesPanel(), additionalEast);
        splitPane.setBorder(null);
        splitPane.setResizeWeight(1.0 / 2.0);
        splitPane.setBorder(GUIUtils.getTiteledBorder("Zusätzliche Informationen"));
        extendedSettingsPanel.add(splitPane, BorderLayout.CENTER);
        extendedSettingsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel freqAndRemindPanel = new JPanel(new BorderLayout());
        if (event.getEndDate() == null && multipleDates == null) {
            freqAndRemindPanel.add(createFrequencyPanel(), BorderLayout.CENTER);
            freqAndRemindPanel.add(createRemindPanel(), BorderLayout.EAST);
        } else {
            freqAndRemindPanel.add(createRemindPanel(), BorderLayout.CENTER);
        }
        
        extendedSettingsPanel.add(freqAndRemindPanel, BorderLayout.SOUTH);
        
        JPanel buttons = okButton.getPanel();
        buttons.add(cancelButton);
        
        add(buttons, BorderLayout.SOUTH);
        
        // focus order
        setFocusTraversalPolicy(new FocusPolicy(new Component[] {nameField, notesField, okButton}, true));
        
        pack();
        setLocationRelativeTo(caller.getGUI().getFrame());
        // move a little higher because of the extension of the frame by the user
        setLocation(getLocation().x, getLocation().y - SHIFT_POS_Y);
        setVisible(true);
        setResizable(false);
    }
    
    /**
     * Editing (creating) a single event.
     * @param c - Parent calendar object
     * @param ev - Event to edit (to create)
     * @param duplicate - Create a copy (duplicate) of the event
     * @param multipleDates - List of dates for adding copies of the event
     */
    public EditEvent(Calendar c, Event ev, boolean duplicate, List<Date> multipleDates) {
        /* Set title of dialog */
        super(c.getGUI().getFrame(), true);
        if (ev.getName().length() == 0) {
            if (ev.getFrequency() == Frequency.OCCUR_WEEKLY) {
                setTitle("Neues wöchentliches Ereignis...");
            } else if (ev.getEndDate() == null) {
                setTitle("Neues Ereignis...");
            } else {
                setTitle("Neues mehrtägiges Ereignis...");
            }
        } else {
            setTitle("Ereignis bearbeiten...");
        }

        addWindowListener(this);
        this.event = ev;
        this.caller = c;
        this.duplicate = duplicate;
        this.multipleDates = multipleDates;
        this.exceptionDates.addAll(ev.getExceptionDates()); // create copy of exception dates
        this.caller.setNotisAlwaysOnTop(false);

        arrangeDialog();
    }

    /**
     * Create a new event.
     * @param c - Parent calendar object
     * @param d - Date of the event to create
     */
    public EditEvent(Calendar c, Date d) {
        this(c, new Event(d, "", -1), false, null);
    }

    /**
     * Create a new multi-day event.
     * @param c - Parent calendar object
     * @param start - Start date
     * @param end - End date
     */
    public EditEvent(Calendar c, Date start, Date end) {
        this(c, new Event(start, end, "", -1), false, null);
    }

    /**
     * Create a new event on a set of dates which are not 
     * connected to each other (by the use of CTRL key).
     * @param c - Parent calendar object
     * @param d - List of dates (MUST NOT BE NULL OR EMPTY!)
     */
    public EditEvent(Calendar c, List<Date> d) {
        this(c, new Event(d.get(0), "", -1), false, d);
    }

    @Override
    public void actionPerformed(ActionEvent a) {
        if (a.getSource().equals(okButton)) {
            submit();
        } else if (a.getSource().equals(cancelButton)) {
            windowClosing(null);
        } else if (a.getSource().equals(chooseButton)) {
            selectAttachmentFile();
        } else if (a.getSource().equals(exceptionButton)) {
            DateListDialog dld = new DateListDialog(this, exceptionDates, "Ausnahmen",
                    "<html>An den folgenden Tagen findet das Ereignis <i>nicht</i>  statt.<br><br>"
                    + "<i>Hinweis:</i> " + XMLParser.MAX_EXCEPTION_DAYS_IN_PAST
                    + " Tage nach Ablauf wird ein Ausnahmedatum automatisch entfernt.</html>", this);
            dld.setVisible(true);
        }
    }
    
    /**
     * Create and return the panel with general information (name & dates).
     * @return Panel with general information.
     */
    private JPanel createGeneralPanel() {
        JPanel generalPanel = new JPanel(new BorderLayout());
        JPanel generalWest = new JPanel(new GridLayout(2, 1));
        JPanel generalCenter = new JPanel(new GridLayout(2, 1));
        JPanel generalCenterN = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel generalCenterS = new JPanel(new BorderLayout());
        generalCenterS.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        generalWest.add(new JLabel("Datum: "));
        generalWest.add(new JLabel("Name: "));
        
        nameField.addKeyListener(this);
        nameField.setToolTipText("Hier die Kurzbeschreibung für dieses Ereignis eingeben. "
                + "Dieser Name erscheint in der Kalenderübersicht.");
        nameField.setComponentPopupMenu(new JTextAreaContextMenu(nameField));

        if (multipleDates == null) {
            startSpinner = new JSpinnerForDate(event == null ? new Date() : event.getDate(), "EEE, dd.MM.yyyy");
            startSpinner.addChangeListener(new ChangeListener() {
                
                @Override
                public void stateChanged(ChangeEvent e) {
                    // on changes of event date: set new frequency labels
                    setFrequencyLabels();
                }
            });
            generalCenterN.add(startSpinner);
            
            /* Fill date text fields */
            if (event != null) {
                nameField.setText(event.getName());
                if (event.getEndDate() != null) {
                    endSpinner = new JSpinnerForDate(event.getEndDate(), "EEE, dd.MM.yyyy");
                    generalCenterN.add(new JLabel("bis"));
                    generalCenterN.add(endSpinner);
                }
            }
        } else {
            DateListDialog dld = new DateListDialog(this, multipleDates, "Ausgewählte Tage", "", this);
            multipleDatesLabel = new LinkLabel("< " + multipleDates.size() + " einzeln ausgewählte Tage >", 
                    "Die ausgewählten Tage anzeigen/ändern", dld);
            
            multipleDatesLabel.setForeground(Color.GRAY);
            generalCenterN.add(multipleDatesLabel);
            generalCenterN.setBorder(new EmptyBorder(3, 0, 0, 0));
        }
        
        generalCenterS.add(nameField, BorderLayout.CENTER);
        generalCenter.add(generalCenterN);
        generalCenter.add(generalCenterS);
        generalPanel.add(generalWest, BorderLayout.WEST);
        generalPanel.add(generalCenter, BorderLayout.CENTER);
        generalPanel.setBorder(GUIUtils.getTiteledBorder("Allgemein"));
        
        return generalPanel;
    }
    
    /**
     * Create and return the panel with frequency settings.
     * @return Panel with frequency settings.
     */
    private JPanel createFrequencyPanel() {
        JPanel frequency = new JPanel(new GridLayout(5, 1));
        JPanel freq1Panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel freq2Panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel freq3Panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel freq4Panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        ButtonGroup freqGroup = new ButtonGroup();
        freq1.setSelected(true);
        freq1.addItemListener(this);
        wBox.addItemListener(this);
        mBox.addItemListener(this);
        yBox.addItemListener(this);
        freqGroup.add(freq1);
        freq1Panel.add(freq1);
        freq1Panel.add(wBox);
        freq1Panel.add(mBox);
        freq1Panel.add(yBox);
        frequency.add(freq1Panel);
        
        byWeekdayLabel.setEnabled(false);
        freq2.addItemListener(this);
        freqGroup.add(freq2);
        freq2Panel.add(freq2);
        freq2Panel.add(byWeekdayLabel);
        frequency.add(freq2Panel);
        
        freq3.addItemListener(this);
        for (int i = 1; i <= MAX_QUANT; i++) {
            intervalBox.addItem("" + i);
        }
        intervalBox.setEnabled(false);
        unitBox.addItem("Tage");
        unitBox.addItem("Wochen");
        unitBox.addItem("Monate");
        unitBox.addItem("Jahre");
        unitBox.setEnabled(false);
        byIntervalLabel = new JLabel("Aller");
        byIntervalLabel.setEnabled(false);
        freqGroup.add(freq3);
        freq3Panel.add(freq3);
        freq3Panel.add(byIntervalLabel);
        freq3Panel.add(intervalBox);
        freq3Panel.add(unitBox);
        freq3Panel.add(new JLabel("                                  "));
        frequency.add(freq3Panel);
        
        freq4.addItemListener(this);
        byEndOfMonthLabel.setEnabled(false);
        freqGroup.add(freq4);
        freq4Panel.add(freq4);
        freq4Panel.add(byEndOfMonthLabel);
        frequency.add(freq4Panel);
        
        JPanel excPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        exceptionButton.setEnabled(false);
        excPanel.add(exceptionButton);
        frequency.add(excPanel);
        
        if (event != null) {
            if (Frequency.isByDate(event.getFrequency())) {
                freq1.setSelected(true);
                wBox.setSelected(Frequency.isW(event.getFrequency()));
                mBox.setSelected(Frequency.isM(event.getFrequency()));
                yBox.setSelected(Frequency.isY(event.getFrequency()));
            } else if (Frequency.isByWeekday(event.getFrequency())) {
                freq2.setSelected(true);
            } else if (Frequency.isByInterval(event.getFrequency())) {
                freq3.setSelected(true);
                intervalBox.setSelectedIndex(Frequency.getInterval(event.getFrequency()) - 1);
                unitBox.setSelectedIndex(Frequency.getUnit(event.getFrequency()));
            } else if (Frequency.isByEndOfMonth(event.getFrequency())) {
                freq4.setSelected(true);
            }
        }
        
        frequency.setBorder(GUIUtils.getTiteledBorder("Regelmäßigkeit"));
        setFrequencyLabels();
        frequencySupported = true;
        
        return frequency;
    }
    
    /**
     * Create and return the panel with time settings. 
     * @return Panel with time settings. Note: the return value is a
     * reference to the global variable 'timePanel'.
     */
    private JPanel createTimePanel() {
        timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        final SpinnerDateModel spm = new SpinnerDateModel();
        if (event != null && event.getDate().hasTime()) {
            timeSpinner = new JSpinnerForTime(event.getDate());
            spm.setValue(event.getDate().getTime());
            timeBox.setSelected(true);
        } else {
//            timeBox.setSelected(false);
            timeSpinner = new JSpinnerForTime();
//            timeSpinner.setEnabled(false);
//            clockLabel.setEnabled(false);
            Date date = new Date();
            date.set(java.util.Calendar.SECOND, 0);
            spm.setValue(date.getTime());
        }
        timeBox.addItemListener(this);
        timePanel.add(new JLabel(new ImageIcon(
                new ImageIcon(SettingsTabInfo.class.getResource("/media/clock_alarm32.png"))
                .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))));
        timePanel.add(timeBox);
        
        spm.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                timeSpinner.setValue(spm.getValue());
            }
        });
        clockTimeChooser = new ClockTimeChooser(spm, new ClockBackground(), false, CLOCK_LABEL.SELECTED_TIME);
        clockTimeChooser.setEnabled(false);
        timeSpinner.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                spm.setValue(timeSpinner.getValue());            
            }
        });
        if (timeBox.isSelected()) {
            timePanel.add(clockTimeChooser);
            timePanel.add(timeSpinner);
            timePanel.add(clockLabel);
        }
        
        return timePanel;
    }
    
    /**
     * Create and return the panel with attachment settings.
     * @return Panel with attachment settings.
     */
    private JPanel createAttachmentPanel() {
        JPanel attachment = new JPanel(new BorderLayout()); 
        
        attachField.setToolTipText(Msg.getMessage("EditEventAttachmentBoxTooltip"));
        attachField.setEditable(false);
        attachField.setComponentPopupMenu(new JTextAreaContextMenu(attachField));
        chooseButton.setMargin(new Insets(2, 2, 2, 2));
        chooseButton.setEnabled(false);
        chooseButton.addActionListener(this);
        chooseButton.setToolTipText("Datei auswählen");
        if (event != null && event.getID() != -1) {
            notesField.setText(event.getNotes(caller.getWorkspace()));
            File attachmentF = event.getAttachment(caller.getWorkspace());
            if (attachmentF != null) {
                attachField.setText(attachmentF.getPath());
                attachmentBox.setSelected(true);
                chooseButton.setEnabled(true);
            }
        }
        
        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        checkBoxPanel.add(new JLabel(new ImageIcon(SettingsTabInfo.class.getResource("/media/attachment20.png"))));
        checkBoxPanel.add(attachmentBox);
        
        attachmentBox.addItemListener(this);
        attachment.add(checkBoxPanel, BorderLayout.WEST);
        attachment.add(attachField, BorderLayout.CENTER);
        attachment.add(chooseButton, BorderLayout.EAST);
        attachment.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        return attachment;
    }
    
    /**
     * Create and return the panel with event notes.
     * @return Panel with event notes.
     */
    private JPanel createNotesPanel() {
        notesField.setFont(nameField.getFont());
        notesField.setToolTipText("Hier ist Platz für Informationen, die nicht in den Namen passen.");
        notesField.setComponentPopupMenu(new JTextAreaContextMenu(notesField));
        JScrollPane pScroll = new JScrollPane(notesField,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        VerticalFlowPanel westPanel = new VerticalFlowPanel();
        westPanel.add(new JLabel("Notizen: "));
        westPanel.add(new JLabel(new ImageIcon(SettingsTabInfo.class.getResource("/media/notes20.png"))));

        JPanel notesP = new JPanel(new BorderLayout());
        notesP.add(westPanel, BorderLayout.WEST);
        notesP.add(pScroll, BorderLayout.CENTER);
        notesP.setBorder(new EmptyBorder(0, 0, 0, 5));
        
        return notesP;
    }
    
    /**
     * Create and return the panel with reminder settings.
     * @return Panel with reminder settings.
     */
    private JPanel createRemindPanel() {
        VerticalFlowPanel reminderArea = new VerticalFlowPanel();
        ButtonGroup remindGroup = new ButtonGroup();
        remindGroup.add(useDefaultRemind);
        remindGroup.add(useIndividualRemind);
        RemindOption defaultRemind = (RemindOption) caller.getConfig().getProperty(EnumProperty.Remind);
        defaultRemindLabel = new JLabel(defaultRemind.getName(false));
        useIndividualRemind.setSelected(true);
        useIndividualRemind.addItemListener(this);
        JPanel defaultRemindPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel individualRemindPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        for (RemindOption ro : RemindOption.values()) {
            remindBox.addItem(ro.getName(false));
        }
        
        if (event != null && event.getRemind() != null) {
            remindBox.setSelectedIndex(event.getRemind().ordinal());
            defaultRemindLabel.setEnabled(false);
        } else {
            useDefaultRemind.setSelected(true);
            remindBox.setSelectedIndex(Configuration.DEFAULT_CONFIG.getProperty(EnumProperty.Remind).ordinal());
        }

        defaultRemindPanel.add(useDefaultRemind);
        defaultRemindPanel.add(defaultRemindLabel);
        individualRemindPanel.add(useIndividualRemind);
        individualRemindPanel.add(remindBox);
        reminderArea.add(defaultRemindPanel);
        reminderArea.add(individualRemindPanel);
        reminderArea.setBorder(GUIUtils.getTiteledBorder("Erinnerung"));
        
        return reminderArea;
    }
    
    /**
     * Create and return the panel with category settings.
     * @return Panel with category settings.
     */
    private JPanel createCategoryPanel() {
        JPanel catPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        catPanel.add(new JLabel("  Kategorie:"));
        
        Map<String, Icon> categories = EventCategories.getMap();
        categoryBox.setRenderer(new JComboBoxIconRenderer(categories));
        categoryBox.addItem("-- keine --");
        for (String item : categories.keySet()) {
            // skip non-user categories
            if (!item.startsWith(EventCategories.SYS_PREFIX)) {
                categoryBox.addItem(item);
            }
        }
        
        catPanel.add(categoryBox);
        if (event != null && event.getCategory() != null) {
            categoryBox.setSelectedItem(event.getCategory());
        }
        
        categoryBox.addItemListener(this);
        return catPanel;
    }
    
    /**
     * Depending on the currently selected date set the labels for
     * the different frequency options.
     */
    private void setFrequencyLabels() {
        Date currentlySelectedDate = startSpinner.getSelectedDate();
        
        // frequency by week day index
        int weekdayCnt = currentlySelectedDate.getWeekdayIndex();
        int dayOfWeek = currentlySelectedDate.get(java.util.Calendar.DAY_OF_WEEK);
        byWeekdayLabel.setText("Jeden " + (weekdayCnt == 0 ? "letzten " : weekdayCnt + ". ") 
                + Date.dayOfWeek2String(dayOfWeek, false) + " im Monat");
        
        // frequency by days to end of month
        int daysToEnd = currentlySelectedDate.getDaysToEndOfMonth();
        byEndOfMonthLabel.setText(daysToEnd == 0 
                ? "Jeden letzten Tag im Monat" : daysToEnd == 1
                ? "Jeden vorletzten Tag im Monat"
                : daysToEnd + " Tage vor Ende des Monats");
    }

    /**
     * Asks to select a file to attach to an event. If a proper file
     * has been chosen, the file path will be set to the attachment text field. 
     */
    private void selectAttachmentFile() {
        LOG.fine("attach");
        JFileChooser jfc;
        if (attachField.getText().equals("")) {
            jfc = new JFileChooser();
        } else {
            String dir = attachField.getText().substring(0,
                    attachField.getText().lastIndexOf(File.separator));
            LOG.fine("attach dir=" + dir);
            jfc = new JFileChooser(dir);
        }
        
        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            attachField.setText(jfc.getSelectedFile().getPath());
        } else if (attachField.getText().equals("")) {
            attachmentBox.setSelected(false);
        }
    }

    /**
     * Pressing button OK. This will add the event (submit changes).
     */
    private void submit() {
        LOG.fine("submit");
        
        /*
         * Check valid 'Name'
         */
        if (nameField.getText().length() == 0) {
            caller.errorOccurred("Der Name darf nicht leer sein!", "Eingabefehler", null);
            return;
        } else if (nameField.getText().length() > MAX_LEN_NAME) {
            caller.errorOccurred("<html>Der Name darf " + MAX_LEN_NAME + " Zeichen nicht überschreiten!"
                    + "<br>Die <i>Notizen</i> können für ausführlichere Beschreibungen genutzt werden.",
                    "Name zu lang...", null);
            return;
        }
        
        /*
         * If there is only one date -> add it to the list
         * So we can use the same loop as for multiple dates
         */
        if (multipleDates == null) {
            multipleDates = new ArrayList<Date>();
            multipleDates.add(startSpinner.getSelectedDate());
        }
        
        /*
         * Loop over all dates 
         */
        for (Date startD : multipleDates) {
            LOG.fine("DATE=" + startD.print());
            
            Date endD = null;
            /*
             * Check end date
             */
            if (event.getEndDate() != null) {
                endD = endSpinner.getSelectedDate();
                LOG.fine("ENDDATE=" + endD.print());
    
                long dayDiff = endD.dayDiff(startD);
                if (dayDiff == 0) {
                    endD = null;
                } else if (dayDiff < 0) {
                    caller.errorOccurred("Das Endedatum liegt vor dem Startdatum!", "Eingabefehler", null);
                    return;
                }
            }
    
            /*
             * In case name and Dates are OK...
             */
            if (timeBox.isSelected()) {
                Date selectedTime = timeSpinner.getSelectedTime();
                startD.set(java.util.Calendar.HOUR_OF_DAY, selectedTime.get(java.util.Calendar.HOUR_OF_DAY));
                startD.set(java.util.Calendar.MINUTE, selectedTime.get(java.util.Calendar.MINUTE));
                startD.setHasTime(true);
            } else {
                startD.setHasTime(false);
            }
    
            short freq = Frequency.OCCUR_ONCE;
            if (frequencySupported) {
                LOG.fine("INDEX=" + remindBox.getSelectedIndex());
                if (freq1.isSelected()) {
                    freq = Frequency.bool2short(wBox.isSelected(), mBox.isSelected(), yBox.isSelected());
                } else if (freq2.isSelected()) {
                    freq = Frequency.OCCUR_BY_WEEKDAY;
                } else if (freq3.isSelected()) {
                    freq = Frequency.genByInterval(intervalBox.getSelectedIndex() + 1, unitBox.getSelectedIndex());
                } else if (freq4.isSelected()) {
                    freq = Frequency.OCCUR_BY_MONTHEND;
                }
            }
            
            Event newEvent = new Event(startD, endD, nameField.getText(), EventType.user, freq,
                    useIndividualRemind.isSelected() ? RemindOption.values()[remindBox.getSelectedIndex()] : null,
                    event.getID() == -1 || duplicate ? caller.genID() : event.getID());
            
            /*
             * Handle attachment & notes
             */
            if (attachFile(newEvent)) {
                // Attachment handling was successful
                if (notesField.getText().length() != 0) {
                    /*
                     * Write notes to a file
                     */
                    newEvent.writeNotes(notesField.getText(), caller);
                }
            } else {
                // Attachment handling was faulty or user cancelled an action
                // -> remove notes and event folder again and abort
                return;
            }
            
            if (notesField.getText().length() == 0 && !attachmentBox.isSelected()) {
                /*
                 * If there is still an old directory from a previous event (should not happen) -> remove it
                 */
                newEvent.removeDirectory(caller);
            }
            
            /*
             * Check the selected category
             */
            if (categoryBox.getSelectedIndex() > 0) {
                if (categoryBox.getSelectedItem().equals("Geburtstag")
                        && !(freq1.isSelected() && yBox.isSelected() && !mBox.isSelected() && !wBox.isSelected())) {
                    if (JOptionPane.showConfirmDialog(this, 
                            "<html>Als Kategorie wurde <i>Geburtstag</i> ausgewählt.<br>"
                            + "Geburtstage finden gewöhnlich jährlich statt.<br>"
                            + "Die eingestellte Regelmäßigkeit entspricht nicht der jährlichen.<br><br>"
                            + "Soll das Ereignis trotzdem angelegt werden?", "Regelmäßigkeit bei Geburtstagen", 
                            JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
                newEvent.setCategory(categoryBox.getSelectedItem().toString());
            }
            
            /*
             * Add exception dates
             */
            newEvent.getExceptionDates().addAll(this.exceptionDates);
    
            /*
             * Finally create/edit the event
             */
            boolean success;
            if (duplicate) {
                success = caller.newEvent(newEvent);
            } else {
                success = caller.editEvent(event.getID(), newEvent);
            }
            
            if (!success) {
                /*
                 * If adding was not successful -> remove written files and do not close window
                 */
                newEvent.removeDirectory(caller);
                return;
            }
        }

        windowClosing(null);
    }
    
    /**
     * Handle file attachment (attach new file / delete old attachment).
     * @param newEvent - New event data
     * @return True if attachment handling was successful. False if there was an
     * error or abortion.
     */
    private boolean attachFile(Event newEvent) {
        /*
         * Is there an old attachment to delete?
         */
        File oldAttachment = event.getAttachment(caller.getWorkspace());
        if (oldAttachment != null && !duplicate) {
            LOG.fine("old attachment was:" + oldAttachment.getPath());
            LOG.fine("new file=" + attachField.getText());
            if (oldAttachment.getPath().equals(attachField.getText())) {
                // Nothing to change about the attachment
                return true;
            }
            
            if (!event.attachmentIsLink(caller.getWorkspace())) {
                int answer = JOptionPane.showConfirmDialog(this, "Der bestehende Anhang dieses Ereignisses "
                        + "war eine Kopie einer Datei.\nSoll diese Kopie wirklich gelöscht werden?\n\n"
                        + "Bei 'Nein' wird der alte Anhang beibehalten.",
                        "Alten Anhang löschen...", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                
                if (answer == JOptionPane.NO_OPTION) {
                    // Leave the old attachment
                    return true;
                } else if (answer == JOptionPane.CANCEL_OPTION) {
                    // Indicate cancellation
                    return false;
                }
                
                // In case of YES_OPTION -> delete old attachment and proceed
                event.getAttachment(caller.getWorkspace()).delete();
            }
            new File(caller.getPath(Const.EVENT_DIR) + File.separator + event.getID()
                    + File.separator + Const.LINK_FILE).delete();
        }

        /*
         * Attach file
         */
        if (attachField.getText().length() != 0) {
            String dirname = caller.getPath(Const.EVENT_DIR);
         // try to create the events directory (will fail if already exists)
            new File(dirname).mkdir();
            dirname += File.separator + newEvent.getID();
            // try to create the event specific directory (will fail if already exists)
            new File(dirname).mkdir();

            boolean asLink;
            Object[] options = {"Link", "Kopie", "Abbruch"};
            int auswahl = JOptionPane.showOptionDialog(caller.getGUI().getFrame(),
                    "Soll der neue Anhang ein Link auf die ausgewählte Datei sein\n"
                    + "oder soll eine Kopie erstellt werden?\n\nLinks sind speichereffizient, "
                    + "aber wenn die Originaldatei\nverschoben oder gelöscht wird, "
                    + "funktioniert der Link nicht mehr.",
                    "Neuer Anhang...", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (auswahl == 0) {
                asLink = true;
            } else if (auswahl == 1) {
                asLink = false;
            } else {
                return false;
            }

            if (asLink) {
                /*
                 * Create a little text file with the link as content
                 */
                try {
                    File linkTxt = new File(dirname + File.separator + Const.LINK_FILE);
                    linkTxt.createNewFile();
                    if (!linkTxt.canWrite()) {
                        caller.errorOccurred("Der Kalender hat hier keine Schreibrechte.",
                                "Keine Schreibrechte...", null);
                        return false;
                    }
                    BufferedWriter out = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(linkTxt), Const.ENCODING));

                    out.write(attachField.getText());
                    out.close();
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "error while trying to create link file...", e);
                    return false;
                }
            } else {
                /*
                 * Copy the complete file
                 */
                File origFile = new File(attachField.getText());
                File copyFile = new File(dirname + File.separator + origFile.getName());
                Copy copyProcess = new Copy(caller.getGUI().getFrame(), origFile, copyFile);
                copyProcess.start();
            }
        }
        
        return true;
    }

    /**
     * Extend or reduce the "edit event" frame.
     * @param full - True to extend, false to reduce
     */
    public void extend(boolean full) {
        if (full) {
            add(extendedSettingsPanel, BorderLayout.CENTER);
        } else {
            remove(extendedSettingsPanel);
        }
        
        pack();
    }

    @Override
    public void keyReleased(KeyEvent k) {
    }

    @Override
    public void keyPressed(KeyEvent k) {
        if (k.getKeyCode() == KeyEvent.VK_ENTER) {
            submit();
        } else if (k.getKeyCode() == KeyEvent.VK_ESCAPE) {
            windowClosing(null);
        }
    }

    @Override
    public void keyTyped(KeyEvent k) {
    }

    @Override
    public void itemStateChanged(ItemEvent i) {
        if (i.getSource().equals(timeBox)) {
//            timeSpinner.setEnabled(timeBox.isSelected());
//            clockLabel.setEnabled(timeBox.isSelected());
            if (!timeBox.isSelected()) {
                timePanel.remove(clockTimeChooser);
                timePanel.remove(timeSpinner);
                timePanel.remove(clockLabel);
                timePanel.invalidate();
            } else {
                timePanel.add(clockTimeChooser);
                timePanel.add(timeSpinner);
                timePanel.add(clockLabel);
            }
            timePanel.invalidate();
            pack();
        } else if (i.getSource().equals(freq1)) {
            wBox.setEnabled(true);
            mBox.setEnabled(true);
            yBox.setEnabled(true);
            intervalBox.setEnabled(false);
            unitBox.setEnabled(false);
            byWeekdayLabel.setEnabled(false);
            byIntervalLabel.setEnabled(false);
            byEndOfMonthLabel.setEnabled(false);
            exceptionButton.setEnabled(wBox.isSelected() || mBox.isSelected() || yBox.isSelected());
        } else if (i.getSource().equals(freq2)) {
            wBox.setEnabled(false);
            mBox.setEnabled(false);
            yBox.setEnabled(false);
            intervalBox.setEnabled(false);
            unitBox.setEnabled(false);
            byWeekdayLabel.setEnabled(true);
            byIntervalLabel.setEnabled(false);
            byEndOfMonthLabel.setEnabled(false);
            exceptionButton.setEnabled(true);
        } else if (i.getSource().equals(freq3)) {
            wBox.setEnabled(false);
            mBox.setEnabled(false);
            yBox.setEnabled(false);
            intervalBox.setEnabled(true);
            unitBox.setEnabled(true);
            byWeekdayLabel.setEnabled(false);
            byIntervalLabel.setEnabled(true);
            byEndOfMonthLabel.setEnabled(false);
            exceptionButton.setEnabled(true);
        } else if (i.getSource().equals(freq4)) {
            wBox.setEnabled(false);
            mBox.setEnabled(false);
            yBox.setEnabled(false);
            intervalBox.setEnabled(false);
            unitBox.setEnabled(false);
            byWeekdayLabel.setEnabled(false);
            byIntervalLabel.setEnabled(false);
            byEndOfMonthLabel.setEnabled(true);
            exceptionButton.setEnabled(true);
        } else if (i.getSource().equals(attachmentBox)) {
            if (!attachmentBox.isSelected()) {
                attachField.setText("");
                attachField.setEnabled(false);
                chooseButton.setEnabled(false);
            } else {
                attachField.setEnabled(true);
                chooseButton.setEnabled(true);
                
                selectAttachmentFile();
                
                /* ??? */
                if (attachField.getText().length() != 0) {
                    attachmentBox.removeItemListener(this);
                    attachmentBox.setSelected(true);
                    chooseButton.setEnabled(true);
                    attachField.setEnabled(true);
                    attachmentBox.addItemListener(this);
                }
            }
        } else if (i.getSource().equals(useIndividualRemind)) {
            remindBox.setEnabled(useIndividualRemind.isSelected());
            defaultRemindLabel.setEnabled(!useIndividualRemind.isSelected());
        } else if (i.getSource().equals(wBox) || i.getSource().equals(mBox) || i.getSource().equals(yBox)) {
            exceptionButton.setEnabled(wBox.isSelected() || mBox.isSelected() || yBox.isSelected());
        } else if (i.getSource().equals(categoryBox)) {
            if (i.getStateChange() == ItemEvent.SELECTED && categoryBox.getSelectedItem().equals("Geburtstag")) {
                final EditEvent thiss = this;
                SwingUtilities.invokeLater(new Runnable() {
                    
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(thiss, "<html>Bei der Kategorie <b>Geburtstag</b> kann im Feld "
                                + "\"Notizen\" eine 4-stellige Jahreszahl als Geburtsjahr angegeben werden.<br>"
                                + "In diesem Fall kann der Kalender anzeigen, wie alt die Person wird.</html>", 
                                "Eingabe Geburtsjahr", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                
            }
        }
    }

    @Override
    public void windowDeactivated(WindowEvent w) {
    }

    @Override
    public void windowActivated(WindowEvent w) {
    }

    @Override
    public void windowDeiconified(WindowEvent w) {
    }

    @Override
    public void windowIconified(WindowEvent w) {
    }

    @Override
    public void windowClosed(WindowEvent w) {
    }

    @Override
    public void windowClosing(WindowEvent w) {
        caller.setNotisAlwaysOnTop(true);
        setVisible(false);
        dispose();
    }

    @Override
    public void windowOpened(WindowEvent w) {
    }

    @Override
    public void buttonPressed(ImageButton x) {
        this.extendLabel.setText(x.isPressed() ? REDUCE : EXTEND);
        this.extend(x.isPressed());
    }

    @Override
    public void dateListChanged(List<Date> dates) {
        if (multipleDates == null) {
            this.exceptionDates = dates;
        } else {
            this.multipleDates = dates;
            multipleDatesLabel.setText("< " + multipleDates.size() + " einzeln ausgewählte Tage >");
        }
    }
}
