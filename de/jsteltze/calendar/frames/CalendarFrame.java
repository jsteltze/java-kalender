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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.UI.CalendarGUI;
import de.jsteltze.calendar.UI.CalendarPanel;
import de.jsteltze.calendar.UI.GUIUtils;
import de.jsteltze.calendar.applet.CalendarApplet;
import de.jsteltze.calendar.config.Configuration.EnumProperty;
import de.jsteltze.calendar.config.Configuration.IntProperty;
import de.jsteltze.calendar.config.Holidays;
import de.jsteltze.calendar.config.enums.OnCloseAction;
import de.jsteltze.calendar.config.enums.Style;
import de.jsteltze.calendar.config.enums.View;
import de.jsteltze.calendar.tasks.AlarmTask;
import de.jsteltze.calendar.tasks.ChangeIconTask;
import de.jsteltze.common.Log;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.Date.PrintFormat;

/**
 * Main frame of the calendar.
 * @author Johannes Steltzer
 *
 */
public class CalendarFrame 
    extends JFrame 
    implements CalendarGUI {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** 16x16 and 32x32 calendar & alarm icons. */
    private static final Image 
            ICON_CAL_64   = new ImageIcon(CalendarFrame.class.getResource("/media/calendar_new64.png")).getImage(),
            ICON_CAL_16   = ICON_CAL_64.getScaledInstance(16, 16, Image.SCALE_SMOOTH),
            ICON_ALARM_32 = new ImageIcon(CalendarFrame.class.getResource("/media/clock_alarm32.png")).getImage(),
            ICON_ALARM_16 = ICON_ALARM_32.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
    
    /** Default frame size when starting. */
    public static final Dimension DEFAULT_SIZE = new Dimension(702, 443);

    /** Main object that manages all data and events. */
    private Calendar calendar;
    
    /** Main calendar panel that contains all calendar GUI elements. */
    private CalendarPanel calendarPanel;

    /** Is the frame currently invisible and at systray? */
    private boolean atSystray;

    /** Systray object of the host operating system. */
    private SystemTray systray;

    /** Icon object at systray. */
    private TrayIcon trayIcon;

    /** Task for changing the icon in case of notifications. */
    private Timer changeIcon;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(CalendarFrame.class);

    /**
     * Construct a new calendar main frame.
     * @param size - Dimension to start with. If width equals 0, frame 
     *         will start maximized. If width equals -1, frame will start 
     *         with default size.
     * @param view - Specify view to start with (pass -1 to use default),
     *         see Configuration.VIEW_XXX
     * @param calendar - Parent calendar object
     */
    public CalendarFrame(Dimension size, View view, final Calendar calendar) {
        super("Kalender");
        setIconImage(ICON_CAL_64);

        this.atSystray = false;
        this.calendar = calendar;

        calendarPanel = new CalendarPanel(view, calendar, false);
        add(calendarPanel);
        
        setLocationRelativeTo(null);
        setResizable(true);
        
        /* set visible when UI is set */
        setVisible(false);
        
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent w) {
                LOG.fine("windowClosing");
                if (calendar.getConfig().getProperty(EnumProperty.AtClose) == OnCloseAction.exit) {
                    /* call shutdown hook */
                    System.exit(0);
                } else if (calendar.getConfig().getProperty(EnumProperty.AtClose) == OnCloseAction.moveToSystray
                        && !atSystray) {
                    toSystray();
                }
            }
        });
        
        if (size.width == 0) {
            this.setExtendedState(Frame.MAXIMIZED_BOTH);
        } else if (size.width == -1) {
            setSize(DEFAULT_SIZE);
        } else {
            setSize(size);
        }
        setLocationRelativeTo(null);
    }
    
    @Override
    public void updateStatusBar() {
        calendarPanel.updateStatusBar();
    }
    
    /**
     * Apply a new look&feel.
     * @param style - new style to set
     * @param visible - change visibility to visible
     */
    public void setUI(Style style, boolean visible) {
        /* this is where the actual look&feel for the application is set */
        calendarPanel.setUI(style);
        /* after setting new look&feel: update this frame */
        SwingUtilities.updateComponentTreeUI(this);
        
        if (visible) {
            /* set frame visible */
            this.setVisible(true);
        }
        
        /* apply new look&feel to all child windows. */
        for (Window w : getOwnedWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
        }
        
        /* apply new look&feel to log window. */
        SwingUtilities.updateComponentTreeUI(LogWindow.getInstance());
    }

    @Override
    public void update() {
        calendarPanel.update();
    }

    /**
     * Maximize this main windows (set visible again and remove 
     * tray icon from systray). DOES NOT CHANGE FRAME SIZE.
     */
    public void maximize() {
        LOG.fine("maximize");
        setVisible(true);
        this.toFront();
        if (atSystray) {
            try {
                systray.remove(trayIcon);
                atSystray = false;
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "error while trying to remove tray icon...", e);
            }
        }
    }
    
    @Override
    public void putMessage(String msg) {
        calendarPanel.putInfoMessage(msg);
    }
    
    /**
     * Set the update available flag.
     * @param version - New version available
     */
    public void updateAvailable(String version) {
        if (atSystray) {
            trayIcon.displayMessage("Kalender-Update verfügbar", 
                    "Version " + version + " wurde veröffentlicht.", TrayIcon.MessageType.INFO);
        }
        calendarPanel.updateAvailable();
    }
    
    /**
     * Apply a new color for the control bar.
     * @param x - Color to set
     */
    public void setButtonPanelColor(Color x) {
        calendarPanel.setButtonPanelColor(x);
    }
    
    @Override
    public void setBackground(Color x) {
        super.setBackground(x);
        if (calendarPanel != null) {
            calendarPanel.setBackground(x);
        }
    }

    /**
     * Tells if the window is currently at systray.
     * @return True if minimized in systray. False otherwise.
     */
    public boolean isInSystray() {
        return atSystray;
    }
    
    /**
     * Setup the tray icon object.
     */
    private void setupTrayIcon() {
        /* Tray icon title */
        String title = "Java-Kalender" 
                + "\n - 1 Klick: Statusinformation abrufen"
                + "\n - Doppelklick: Kalender hervorholen"
                + "\n - rechte Maustaste: Menü";
        
        trayIcon = new TrayIcon(ICON_CAL_16, title, new TrayIconMenu(this));
        trayIcon.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent m) {
                final long sleepTime = 100L;
                m.consume();
                if (m.getClickCount() == 1 && !m.isMetaDown()) {
                    
                    /*
                     * If we have a double click mouseClicked will be called twice:
                     * first with clickCount=1 then with clickCount=2. Additionally
                     * on double click an action events gets fired.
                     * So we give him a short sleep time to wait and see if we have 
                     * a double click. In this time isInSystray() can return the 
                     * proper value.
                     * 
                     * If we dont spend this sleep time the action event removes the
                     * tray icon first and the second mouse event gets directed to some
                     * other tray icon (because isInSystray is not yet correct). 
                     */
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        LOG.warning("sleep interrupted in mouseClicked...");
                    }
                    
                    if (isInSystray()) {
                        showTrayIconInfo();
                    }
                }
            }
        });
        trayIcon.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                maximize();
            }
        });
        trayIcon.setImageAutoSize(true);
    }

    /**
     * Makes the entire program invisible and adds an icon to 
     * the systray. DOES NOT EXIT!
     */
    public void toSystray() {
        LOG.fine("minimize");
        try {
            if (!SystemTray.isSupported()) {
                LOG.info("systray not supported!");
                if (JOptionPane.showOptionDialog(this,
                        "Das Betriebssystem unterstützt keinen Systray!\n"
                                + "Daher kann der Kalender nicht im Hintergrund laufen.\n\n"
                                + "Um diese Meldung zukünftig zu vermeiden, muss in den Einstellungen\n"
                                + "das Verhalten des Programms beim Schließen des Fensters geändert werden.\n\n"
                                + "Soll der Kalender jetzt beendet werden?",
                        "Kein Systray möglich", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, new String[] {"Beenden", "Abbrechen"}, "Beenden") == 0) {
                    
                    /* call shutdown hook */
                    System.exit(0);
                }
                return;
            }
            atSystray = true;
            setVisible(false);

            /*
             * Create tray icon and popup menu
             */
            setupTrayIcon();
            
            boolean firstSystrayMove = false;
            if (systray == null) {
                systray = SystemTray.getSystemTray();
                firstSystrayMove = true;
            }
            
            systray.add(trayIcon);
            if (calendar.isFirstStartup() && firstSystrayMove) {
                trayIcon.displayMessage("Kalender:",
                        "Der Kalender befindet sich jetzt hier.\nDoppelklicken zum Hervorholen "
                        + "oder rechte Maustaste zum Beenden.",
                        TrayIcon.MessageType.INFO);
            }
        } catch (Exception e) {
            GUIUtils.showErrorMessage("<html>Der Kalender kann nicht zum Systray hinzugefügt werden!"
                    + "<br>Der Kalender wird nun beendet...</html>", "Kein Systray möglich", e);
            
            /* call shutdown hook */
            System.exit(0);
        }
    }
    
    /**
     * Show basic information on the calendar in a tray icon
     * message box.
     */
    private void showTrayIconInfo() {
        if (!isInSystray()) {
            return;
        }
        int numHolidays = Holidays.getNumberOfHolidays(calendar.getConfig().getProperty(IntProperty.HolidayID))
                + Holidays.getNumberOfHolidays(calendar.getConfig().getProperty(IntProperty.SpecialDaysID));
        List<Event> events = calendar.getAllEvents();
        List<AlarmTask> alarms = calendar.getAlarmTasks();
        String message = "Insgesamt: " + (events.size() - numHolidays) + " Ereignisse, "
                + numHolidays + " Feiertage\n\n";
        message += "Heute:";
        Date now = new Date();
        boolean haveMatches = false;
        for (Event e : events) {
            if (e.match(now)) {
                message += "\n    - " + e.getName();
                if (e.getDate().hasTime()) {
                    message += " (" + e.getDate().print(PrintFormat.HHmm_Uhr) + ")";
                }
                haveMatches = true;
            }
        }
        if (!haveMatches) {
            message += " keine Ereignisse";
        }
        
        if (alarms.size() > 0) {
            message += "\n\nLaufende Erinnerungen:";
            for (AlarmTask alarm : alarms) {
                Event event = alarm.getEvent();
                message += "\n    - " + event.getNextDate().print(PrintFormat.DAY_DIFF)
                        + ": " + event.getName();
                if (event.getDate().hasTime()) {
                    message += " (" + event.getDate().print(PrintFormat.HHmm_Uhr) + ")";
                }
            }
        }
        trayIcon.displayMessage("Kalender - " + now.print(PrintFormat.D_MMM_YYYY), 
                message, TrayIcon.MessageType.INFO);
    }

    /**
     * Returns the current view type.
     * @return Current view type (year/month...).
     */
    public View getView() {
        return calendarPanel.getView();
    }
    
    /**
     * Returns the path of a file or directory with the current
     * working directory.
     * @param file - File or directory to get
     * @return Path of the file within the working directory.
     */
    public String getPath(String file) {
        return calendar.getPath(file);
    }
    
    /**
     * Returns the command line argument that were used when starting the java program.
     * @return Command line arguments the calendar was started with.
     */
    public String[] getArgs() {
        return calendar.getArgs();
    }

    /**
     * Starts a task to switch the icon each second. Change between
     * default icon and notification icon.
     */
    public void startChangeIcon() {
        if (changeIcon != null) {
            return;
        }
        changeIcon = new Timer(true);
        changeIcon.schedule(new ChangeIconTask(this), 0, Date.SEC_1);
    }

    /**
     * Change the icon of this frame. Change from default icon to
     * notification icon or other way.
     */
    public void changeIcon() {
        /* Stop changing if no more notifications */
        LOG.fine("changeIcon: notis size=" + calendar.getNotificationSize());
        if (calendar.getNotificationSize() == 0) {
            stopChangeIcon();
            return;
        }
        
        if (!atSystray) {
            if (this.getIconImage().equals(ICON_CAL_64)) {
                this.setIconImage(ICON_ALARM_32);
            } else {
                this.setIconImage(ICON_CAL_64);
            }
        } else {
            if (trayIcon.getImage().equals(ICON_CAL_16)) {
                trayIcon.setImage(ICON_ALARM_16);
            } else {
                trayIcon.setImage(ICON_CAL_16);
            }
        }
    }

    /**
     * Stop changing the icon and set the default icon again.
     */
    public void stopChangeIcon() {
        if (changeIcon != null) {
            changeIcon.cancel();
            changeIcon = null;
        }
        if (!atSystray) {
            setIconImage(ICON_CAL_64);
        } else {
            trayIcon.setImage(ICON_CAL_16);
        }
    }
    
    @Override
    public CalendarFrame getFrame() {
        return this;
    }
    
    @Override
    public CalendarApplet getApplet() {
        return null;
    }
    
    @Override
    public void shutdown() {
        if (changeIcon != null) {
            changeIcon.cancel();
        }
        calendarPanel.shutdown();
//        this.setVisible(false);
//        this.dispose();
    }
}

/**
 * Calendar systray popup menu.
 * @author Johannes Steltzer
 *
 */
class TrayIconMenu 
    extends PopupMenu 
    implements ActionListener {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Calendar frame which hides behind systray icon. */
    private CalendarFrame cf;
    
    /** Menu items for the popup menu. */
    private MenuItem maxItem, exitItem;

    /**
     * Construct new systray menu.
     * @param cf - Calendar frame to refer
     */
    public TrayIconMenu(CalendarFrame cf) {
        super("Kalender Optionen:");
        maxItem = new MenuItem("Kalender öffnen");
        exitItem = new MenuItem("Kalender beenden");
        maxItem.addActionListener(this);
        exitItem.addActionListener(this);
        this.add(maxItem);
        this.addSeparator();
        this.add(exitItem);

        this.cf = cf;
    }

    @Override
    public void actionPerformed(ActionEvent a) {
        if (a.getSource().equals(maxItem)) {
            /* maximize the program */
            cf.maximize();
        } else if (a.getSource().equals(exitItem)) {
            /* call shutdown hook */
            System.exit(0);
        }
    }
}