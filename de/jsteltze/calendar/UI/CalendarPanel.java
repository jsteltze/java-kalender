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

package de.jsteltze.calendar.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.EventCategories;
import de.jsteltze.calendar.Update;
import de.jsteltze.calendar.config.ColorSet;
import de.jsteltze.calendar.config.Configuration;
import de.jsteltze.calendar.config.Configuration.BoolProperty;
import de.jsteltze.calendar.config.Configuration.EnumProperty;
import de.jsteltze.calendar.config.Configuration.IntProperty;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.calendar.config.Holidays;
import de.jsteltze.calendar.config.enums.Style;
import de.jsteltze.calendar.config.enums.View;
import de.jsteltze.calendar.frames.JumpToDialog;
import de.jsteltze.calendar.frames.Settings;
import de.jsteltze.calendar.frames.TableOfEventsSingleDay;
import de.jsteltze.calendar.tasks.AlarmTask;
import de.jsteltze.calendar.tasks.RefreshDateTask;
import de.jsteltze.common.ImageButton;
import de.jsteltze.common.ImageButtonGroup;
import de.jsteltze.common.ImageButtonListener;
import de.jsteltze.common.LinkLabel;
import de.jsteltze.common.Log;
import de.jsteltze.common.MessageTransparator;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.Date.PrintFormat;
import de.jsteltze.common.ui.Button;
import de.jsteltze.common.ui.ColorPanel;
import de.jsteltze.common.ui.RoundedColorPanel;

/**
 * Calendar GUI component to be added in applet or 
 * stand-alone frame.
 * @author Johannes Steltzer
 *
 */
public class CalendarPanel 
    extends JPanel 
    implements MouseListener, ActionListener, ImageButtonListener {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Minimum possible year limit (set by Gregorian calendar). */
    private static final int YEAR_LIMIT = 1582;
        
    /** Main object that manages all data and events. */
    private Calendar calendar;
    
    /** Canvas where everything is painted. */
    private CalendarCanvas canvas;
    
    /** Control bar rolling borders. */
    private RoundedColorPanel leftBorder, rightBorder;

    /** Buttons for the different possible views (year, month ...). */
    private ImageButton[] views = new ImageButton[]{
            new ImageButton("/media/365.PNG", "/media/365_2.PNG", true),
            new ImageButton("/media/30.PNG", "/media/30_2.PNG", true),
            new ImageButton("/media/7.PNG", "/media/7_2.PNG", true),
            new ImageButton("/media/1.PNG", "/media/1_2.PNG", true)
    };

    /** Button for jumping to any date. */
    private ImageButton jump = new ImageButton("/media/jump-to-any2.png", "/media/jump-to-any2.png", false);

    /** Buttons for browsing within the current view. */
    private Button toPrev = new Button("MainToPrevButton", "/media/arrow_l16.png", this),
            toNext = new Button("MainToNextButton", "/media/arrow_r16.png", this), 
            toToday;
    
    /** Button for settings dialog. */
    private ImageButton settings = new ImageButton("/media/settings.png", "/media/settings.png", false);
    
    /** Status bar labels. */
    private JLabel dateLabel, notificationsLabel, infoLabel = new JLabel("");
    
    /** Selectable labels for opening further dialogs. */
    private LinkLabel eventsLabel, holidaysLabel, updateLabel;
    
    /** Status bar area for number of events & holidays. */
    private JPanel statusBarInfos = new ColorPanel(),
            statusBarDate = new ColorPanel(),
            statusBarNumberPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)), 
            infoTextPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

    /** Panels to contain the different kinds of buttons and views. */
    private JPanel buttonLeft = new JPanel(), 
            buttonPanelCore = new ColorPanel(),
            buttonRight = new JPanel();

    /** Currently displayed view (year/month/week/day). */
    private View view;
    
    /** is this a browser applet? */
    private boolean appletMode;
    
    /** Slowly makes a label transparent. */
    private MessageTransparator msgPutter;
    
    /** is an update available? */
    private boolean updateIsAvailable = false;
    
    /** Refresh date&time every minute. */
    private RefreshDateTask refreshDateTask;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(CalendarPanel.class);
    
    /**
     * Construct a new calendar panel.
     * @param view - View to start with (see Configuration.VIEW_XXX)
     * @param calendar - Calendar parent object
     * @param appletMode - True for a java browser applet, false
     *         otherwise
     */
    public CalendarPanel(View view, Calendar calendar, boolean appletMode) {
        this.view = view;
        this.calendar = calendar;
        this.appletMode = appletMode;
        
        canvas = CalendarCanvas.create(calendar, view);
        //canvas = new CalendarCanvas(calendar, view);

        arrangePanel();

        msgPutter = new MessageTransparator(infoLabel);
        msgPutter.start();
        refreshDateTask = new RefreshDateTask(dateLabel);
        refreshDateTask.start();
    }

    /**
     * Arrange all components in this panel.
     */
    private void arrangePanel() {
        setLayout(new BorderLayout());
        this.setBackground(calendar.getConfig().getColors()[ColorSet.BACKGROUND]);

        /* Button Panel */
        JPanel buttonPanel = new JPanel();
        JPanel buttonPanelCoreCenter = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanelCore.setLayout(new BorderLayout());
        
        if (view == View.year) {
            toToday = new Button("MainToTodayYearButton", this);
        } else if (view == View.month) {
            toToday = new Button("MainToTodayMonthButton", this);
        } else if (view == View.week) {
            toToday = new Button("MainToTodayWeekButton", this);
        } else {
            toToday = new Button("MainToTodayDayButton", this);
        }
        jump.addButtonListener(this);
        jump.setToolTipText("zu einem bestimmten Datum springen...");

        buttonLeft.add(toPrev);
        buttonLeft.add(jump);
        buttonLeft.setOpaque(false);
        buttonPanelCore.add(buttonLeft, BorderLayout.WEST);

        ImageButtonGroup group = new ImageButtonGroup();
        views[0].setToolTipText(View.year.toString());
        views[1].setToolTipText(View.month.toString());
        views[2].setToolTipText(View.week.toString());
        views[3].setToolTipText(View.day.toString());
        views[view.ordinal()].setPressed(true);

        for (int i = 0; i < 4; i++) {
            views[i].setButtonGroup(group);
            views[i].addButtonListener(this);
        }

        buttonPanelCoreCenter.add(views[0]);
        buttonPanelCoreCenter.add(views[1]);
        buttonPanelCoreCenter.add(toToday);
        buttonPanelCoreCenter.add(views[2]);
        buttonPanelCoreCenter.add(views[3]);
        buttonPanelCoreCenter.setOpaque(false);

        buttonPanelCore.add(buttonPanelCoreCenter, BorderLayout.CENTER);
        buttonRight.add(toNext);
        buttonRight.setOpaque(false);
        buttonPanelCore.add(buttonRight, BorderLayout.EAST);
        buttonPanel.add(buttonPanelCore, BorderLayout.CENTER);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, 
                Const.COLOR_CONTROL_BORDER));
        /* End Button Panel */
        
        leftBorder = new RoundedColorPanel(Color.white, Const.COLOR_CONTROL_BORDER, true, false);
        rightBorder = new RoundedColorPanel(Color.white, Const.COLOR_CONTROL_BORDER, false, true);
        JPanel controlBar = new JPanel(new BorderLayout());
        controlBar.add(leftBorder, BorderLayout.WEST);
        controlBar.add(buttonPanel, BorderLayout.CENTER);
        controlBar.add(rightBorder, BorderLayout.EAST);
        controlBar.setOpaque(false);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlBar, BorderLayout.CENTER);
        settings.addMouseListener(this);
        settings.setToolTipText("Einstellungen...");
        topPanel.add(settings, BorderLayout.EAST);
        topPanel.setOpaque(false);
    
        add(topPanel, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        
        Date today = new Date();
        dateLabel = new JLabel(today.print(PrintFormat.DDMMYYYY_HHmm));
        dateLabel.setForeground(Color.gray);
        dateLabel.setFont(Const.FONT_STATUSBAR);
        statusBarDate.setLayout(new BorderLayout());
        statusBarDate.add(dateLabel);
        statusBarDate.setBorder(new EtchedBorder());
        
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.add(statusBarDate, BorderLayout.WEST);
        eventsLabel = new LinkLabel("x Ereignisse", "Alle Ereignisse anzeigen", (Window) null);
        eventsLabel.addMouseListener(this);
        holidaysLabel = new LinkLabel("x Feiertage", "Alle Feiertage anzeigen", (Window) null);
        holidaysLabel.addMouseListener(this);
        notificationsLabel = new JLabel("x Erinnerungen aktiv");
        updateStatusBar();
        
        eventsLabel.setFont(Const.FONT_STATUSBAR);
        holidaysLabel.setFont(Const.FONT_STATUSBAR);
        notificationsLabel.setForeground(Color.gray);
        notificationsLabel.setFont(Const.FONT_STATUSBAR);
        JLabel separator = new JLabel(" | ");
        separator.setFont(Const.FONT_STATUSBAR);
        separator.setForeground(Color.gray);
        JLabel separator2 = new JLabel(" | ");
        separator2.setFont(Const.FONT_STATUSBAR);
        separator2.setForeground(Color.gray);
        
        statusBarNumberPanel.add(eventsLabel);
        statusBarNumberPanel.add(separator);
        statusBarNumberPanel.add(holidaysLabel);
        statusBarNumberPanel.add(separator2);
        statusBarNumberPanel.add(notificationsLabel);
        statusBarNumberPanel.setOpaque(false);
        
        infoLabel.setFont(Const.FONT_STATUSBAR);
        infoTextPanel.add(infoLabel);
        infoTextPanel.setOpaque(false);
        statusBarInfos.setLayout(new BorderLayout());
        statusBarInfos.add(infoTextPanel, BorderLayout.CENTER);
        statusBarInfos.add(statusBarNumberPanel, BorderLayout.EAST);
        statusBarInfos.setBorder(new EtchedBorder());
        
        statusBar.add(statusBarInfos, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        
        setButtonPanelColor(calendar.getConfig().getColors()[ColorSet.CONTROLPANEL]);
    }
    
    /**
     * Sets the background color of the upper control panel which 
     * includes the view buttons and the bottom status bar.
     * @param x - Color to set
     */
    public void setButtonPanelColor(Color x) {
        LOG.fine("setButtonPanelColor");
        
        buttonPanelCore.setBackground(x);
        leftBorder.setColor(x);
        rightBorder.setColor(x);
        statusBarDate.setBackground(x);
        statusBarInfos.setBackground(x);
    }
    
    /**
     * Unset any selection.
     */
    public void resetSelection() {
        canvas.unmarkAll();
    }
    
    /**
     * Update the number of events, holidays and pending notifications
     * in the bottom status bar.
     */
    public void updateStatusBar() {
        int numLawHolidays = Holidays.getNumberOfHolidays(calendar.getConfig().getProperty(IntProperty.HolidayID));
        int numSpecialDays = Holidays.getNumberOfHolidays(calendar.getConfig().getProperty(IntProperty.SpecialDaysID));
        int numActionDays  = Holidays.getNumberOfHolidays(calendar.getConfig().getProperty(IntProperty.ActionDays1ID))
                + Holidays.getNumberOfHolidays(calendar.getConfig().getProperty(IntProperty.ActionDays2ID));
        int numTimeShift   = calendar.getConfig().getProperty(BoolProperty.NotifyTimeShift) ? 2 : 0;
        int numSeason      = calendar.getConfig().getProperty(BoolProperty.NotifySeason) ? 4 : 0;
        int numTotal       = numLawHolidays + numSpecialDays + numActionDays + numTimeShift + numSeason;
        
        //cannot use this, since flexible holidays may occure twice
        //int num_events = calendar.getAllEvents().size() - num_holidays;
        int numEvents = calendar.getAllUserEvents().size();
        int numNotis = calendar.getAlarmTasks().size();
        eventsLabel.setText(numEvents + " Ereignis" + (numEvents == 1 ? "" : "se"));
        holidaysLabel.setText(numTotal + " Feier-/Aktionstag" + (numTotal == 1 ? "" : "e"));
        notificationsLabel.setText(numNotis + " Erinnerung" + (numNotis == 1 ? "" : "en") + " aktiv");
        
        // tooltip for active notification
        String toolTip = "<html>" + (numNotis == 0 ? "Keine" : numNotis == 1 ? "Eine" : numNotis)
                + " laufende Erinnerung" + (numNotis > 1 ? "en" : "");
        if (numNotis > 0) {
            toolTip += ":<hr>";
            for (AlarmTask at : calendar.getAlarmTasks()) {
                Date alarmTime = new Date(System.currentTimeMillis() + at.scheduledExecutionTime());
                toolTip += "<img src='" +  CalendarPanel.class.getResource("/media/clock_alarm32.png") 
                        + "' width='12' height='12'>&nbsp;"
                        + "<font color='gray'>" + alarmTime.print(PrintFormat.HHmm_Uhr) 
                        + ":</font> Erinnerung an <b>" + at.getEvent().getName() + "</b><br>";
            }
        }
        notificationsLabel.setToolTipText(toolTip + "</html>");
        
        // tooltip for all events: show top 3 categories
        toolTip = "<html>Alle <b>" + numEvents + " Ereignisse</b> (außer Feiertage) auflisten";
        LinkedHashMap<String, Integer> countedCategories = EventCategories.count(calendar.getAllUserEvents());
        if (!countedCategories.isEmpty()) {
            toolTip += ":<hr>";
            int top = 0, countCats = 0;
            for (String topCat : countedCategories.keySet()) {
                toolTip += " - " + EventCategories.getIconAsHTML(topCat, Button.ICON_SIZE_L) + " "
                        + countedCategories.get(topCat) + " Kategorie " + topCat + "<br>";
                countCats += countedCategories.get(topCat);
                top++;
                if (top == 3) {
                    toolTip += " - " + (numEvents - countCats) + " weitere...";
                    break;
                }
            }
        }
        eventsLabel.setToolTipText(toolTip + "</html>");
        
        // tooltip for holidays
        toolTip = "<html>Alle <b>" + numTotal + " Feiertage / besondere Tage</b> auflisten";
        if (numTotal > 0) {
            toolTip += ":<hr>";
            if (numLawHolidays > 0) {
                toolTip += " - " + EventCategories.getIconAsHTML(EventCategories.HOLIDAY, Button.ICON_SIZE_L)
                        + " " + numLawHolidays + " gesetzliche Feiertage<br>";
            }
            if (numSpecialDays > 0) {
                toolTip += " - " + EventCategories.getIconAsHTML(EventCategories.HOLIDAY, Button.ICON_SIZE_L)
                        + " " + numSpecialDays + " besondere Feiertage<br>";
            }
            if (numActionDays > 0) {
                toolTip += " - " + EventCategories.getIconAsHTML(EventCategories.CALENDAR_DATE, Button.ICON_SIZE_L)
                        + " " + numActionDays + " Welt-/Aktionstage<br>";
            }
            if (numTimeShift > 0) {
                toolTip += " - " + EventCategories.getIconAsHTML(EventCategories.CALENDAR_DATE, Button.ICON_SIZE_L)
                        + " " + numTimeShift + " Zeitumstellungen<br>";
            }
            if (numSeason > 0) {
                toolTip += " - " + EventCategories.getIconAsHTML(EventCategories.CALENDAR_DATE, Button.ICON_SIZE_L)
                        + " " + numSeason + " Jahreszeitenwechsel<br>";
            }
        }
        holidaysLabel.setToolTipText(toolTip + "</html>");
    }
    
    /**
     * Apply a new look&feel.
     * @param style - Look&Feel to apply (see Configuration.STYLE_XXX)
     */
    public void setUI(Style style) {
        // Set preferred size of borders to 1.
        // This is to allow the whole control bar to layout its size automatically.
        // So the actual control bar defines the height of the panel.
        leftBorder.setPreferredSize(new Dimension(1, 1));
        rightBorder.setPreferredSize(new Dimension(1, 1));
        
        LOG.fine("set UI: " + style);
        
        try {
            switch (style) {
            case system:
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                break;
            case swingOcean:
                MetalLookAndFeel.setCurrentTheme(new OceanTheme());
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                break;
            case swingDefault:
                MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                break;
            case motif:
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                break;
            case nimbus:
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                break;
            default:
                LOG.severe("setUI: unknown code " + style);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "cannot set UI...", e);
        }
        
        SwingUtilities.updateComponentTreeUI(this);
        // Set control bar color again, in order to force repaint of panels.
        // This is necessary because the height of components might change.
        setButtonPanelColor(calendar.getConfig().getColors()[ColorSet.CONTROLPANEL]);
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    /**
     * Display an info message in the status bar.
     * @param msg - Message to display
     */
    public void putInfoMessage(String msg) {
        if (updateIsAvailable) {
            return;
        }
        infoLabel.setText(msg);
        msgPutter.interrupt();
    }
    
    /**
     * Notify about an available update.
     * (Will display a message and lock it)
     */
    public void updateAvailable() {
        updateIsAvailable = true;
        infoLabel.setText(" >> Es ist ein ");
        infoLabel.setForeground(Color.gray);
        updateLabel = new LinkLabel("Update");
        updateLabel.addMouseListener(this);
        JLabel updateLabel2 = new JLabel(" verfügbar");
        updateLabel.setFont(Const.FONT_STATUSBAR);
        updateLabel2.setFont(Const.FONT_STATUSBAR);
        updateLabel2.setForeground(Color.gray);
        infoTextPanel.add(updateLabel);
        infoTextPanel.add(updateLabel2);
        infoTextPanel.validate();
    }
    
    /**
     * Update the calendar (in case of new events / removed events).
     */
    public void update() {
        canvas.update();
    }
    
    /**
     * Returns the current view type (year/month...).
     * @return the current view type (year/month...).
     */
    public View getView() {
        return view;
    }
    
    /**
     * Gracefully shutdown the program and stop GUI related
     * tasks.
     */
    public void shutdown() {
        if (refreshDateTask != null) {
            refreshDateTask.stopit();
        }
        if (msgPutter != null) {
            msgPutter.stopit();
        }
    }
    
    @Override
    public void mouseExited(MouseEvent m) {
        m.getComponent().setCursor(
                Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseEntered(MouseEvent m) {
        m.getComponent().setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseReleased(MouseEvent m) {
    }

    @Override
    public void mousePressed(MouseEvent m) {
        if (m.getSource().equals(settings)) {
            if (appletMode) {
                putInfoMessage("IN DIESEM ONLINE-APPLET KÖNNEN KEINE EINSTELLUNGEN GEÄNDERT WERDEN!");
            } else {
                new Settings(calendar);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent m) {
        if (m.getSource().equals(eventsLabel)) {
            new TableOfEventsSingleDay(null, calendar, false);
        } else if (m.getSource().equals(holidaysLabel)) {
            new TableOfEventsSingleDay(null, calendar, true);
        } else if (m.getSource().equals(updateLabel)) {
            new Update(calendar, false);
            updateIsAvailable = false;
            infoLabel.setText("");
            infoTextPanel.removeAll();
            infoTextPanel.add(infoLabel);
            infoTextPanel.validate();
            infoTextPanel.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent a) {
        if (a.getSource().equals(toNext) || a.getSource().equals(toPrev)) {
            Date dest = calendar.getViewedDate().clone();
            int factor = a.getSource().equals(toNext) ? 1 : -1;
            if (view == View.year) {
                dest.add(java.util.Calendar.YEAR, factor * 1);
            } else if (view == View.month) {
                dest.add(java.util.Calendar.MONTH, factor * 1);
            } else if (view == View.week) {
                dest.add(java.util.Calendar.DAY_OF_MONTH, factor * 7);
            } else if (view == View.day) {
                dest.add(java.util.Calendar.DAY_OF_MONTH, factor * 1);
            }

            jumpTo(dest);
            
        } else if (a.getSource().equals(toToday)) {
            jumpTo(new Date());
        }
    }
    
    /**
     * Show a message with interesting information if a jump to years < 1582 is requested. 
     * This is not possible because the Gregorian calendar was released in 1582.
     * This is kind of an easter egg ;-) 
     */
    private void showGregorMessage() {
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.add(new JLabel("<html>Der heute weltweit verwendete gregorianische Kalender<br>"
                + "wurde 1582 durch Papst Gregor XIII. eingeführt.<br>"
                + "Eine Darstellung früherer Jahre in dieser Ansicht wird<br>"
                + "nicht unterstützt.<br>"
                + "Vor dem gregorianischen Kalender war der julianische Kalender<br>"
                + "im Einsatz (eingeführt von Julius Caesar).</html>"), BorderLayout.CENTER);
        JPanel linkPanel = new JPanel(new GridLayout(3, 1));
        JLabel linkTitle = new JLabel("weiterführende Links:");
        linkTitle.setForeground(Color.DARK_GRAY);
        linkPanel.add(linkTitle);
        linkPanel.add(new LinkLabel("    http://de.wikipedia.org/wiki/Gregor_XIII.", 
                "http://de.wikipedia.org/wiki/Gregor_XIII."));
        linkPanel.add(new LinkLabel("    http://de.wikipedia.org/wiki/Gregorianischer_Kalender", 
                "http://de.wikipedia.org/wiki/Gregorianischer_Kalender"));
        content.add(linkPanel, BorderLayout.SOUTH);
        
        JOptionPane.showMessageDialog(this, content, "Kalendergrenze...", JOptionPane.INFORMATION_MESSAGE, 
                new ImageIcon(CalendarPanel.class.getResource("/media/gregor_xiii.png")));
    }
    
    /**
     * Jump to a any date.
     * @param x - Date to jump to
     */
    private void jumpTo(Date x) {
        LOG.info("jump to date " + x.print());
        
        /*
         * Kind of easter egg
         */
        if (x.get(java.util.Calendar.YEAR) <= YEAR_LIMIT) {
            showGregorMessage();
            return;
        }

        Date viewedDate = calendar.getViewedDate();

        /* In case of change of year: Update flexible Holidays */
        if (x.get(java.util.Calendar.YEAR) != viewedDate.get(java.util.Calendar.YEAR)) {
            calendar.updateFlexibleHolidays(x.get(java.util.Calendar.YEAR), false, false);
        }

        calendar.setViewedDate(x);
        canvas.repaint();
    }

    @Override
    public void buttonPressed(ImageButton x) {
        /*
         * Change of view (year, month ...)
         */
        if (x.equals(views[0]) || x.equals(views[1]) || x.equals(views[2])
                || x.equals(views[3])) {
            if (views[0].isPressed()) {
                view = View.year;
                toToday.setText("zum aktuellen Jahr");
            } else if (views[1].isPressed()) {
                view = View.month;
                toToday.setText("zum aktuellen Monat");
            } else if (views[2].isPressed()) {
                view = View.week;
                toToday.setText("zur aktuellen Woche");
            } else if (views[3].isPressed()) {
                view = View.day;
                toToday.setText("zum aktuellen Tag");
            }

            if (canvas.getView() != view) {
                remove(canvas);
                canvas = CalendarCanvas.create(calendar, view);
                add(canvas);
                if (!appletMode) {
                    Configuration config = calendar.getConfig().clone();
                    config.setProperty(EnumProperty.DefaultView, view);
                    calendar.setConfig(config);
                }
            }
        
        
        /*
         * Jump button clicked
         */
        } else if (x.equals(jump)) {
            Date dest = JumpToDialog.show(this, view, calendar.getViewedDate());
            if (dest != null) {
                jumpTo(dest);
            }
        }
    }
}
