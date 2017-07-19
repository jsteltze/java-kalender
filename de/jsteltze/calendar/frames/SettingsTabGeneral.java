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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.UI.GUIUtils;
import de.jsteltze.calendar.config.Configuration;
import de.jsteltze.calendar.config.Configuration.BoolProperty;
import de.jsteltze.calendar.config.Configuration.EnumProperty;
import de.jsteltze.calendar.config.Configuration.IntProperty;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.calendar.config.enums.OnClickDayAction;
import de.jsteltze.calendar.config.enums.OnClickEventAction;
import de.jsteltze.calendar.config.enums.OnCloseAction;
import de.jsteltze.calendar.config.enums.RemindOption;
import de.jsteltze.calendar.config.enums.Style;
import de.jsteltze.common.Log;
import de.jsteltze.common.Msg;
import de.jsteltze.common.Music;
import de.jsteltze.common.VerticalFlowPanel;
import de.jsteltze.common.ui.Button;
import de.jsteltze.common.ui.Checkbox;
import de.jsteltze.common.ui.Polygon;

/**
 * Settings frame for calendar configuration: tab for general settings.
 * @author Johannes Steltzer
 *
 */
public class SettingsTabGeneral 
    extends JPanel
    implements ItemListener, ActionListener {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Parent calendar object. */
    private Calendar caller;

    /** Drop down lists for configuration items. */
    private JComboBox<String> remindBox, onCloseBox, styleBox, 
            onClickDayBox, onClickEventBox, weekStart;
    
    /** Checkboxes for configuration items. */
    private Checkbox autoUpdateBox = new Checkbox("SettingsTabGeneralAutoUpdateBox"), 
            moonBox = new Checkbox("SettingsTabGeneralMoonBox"), 
            zodiacBox = new Checkbox("SettingsTabGeneralZodiacBox"), 
            systrayBox = new Checkbox("SettingsTabGeneralSystrayBox"), 
            buttonTextsBox = new Checkbox("SettingsTabGeneralButtonTextBox"), 
            notifyTimeShiftBox = new Checkbox("SettingsTabGeneralTimeShiftBox"),
            notifySeasonBox = new Checkbox("SettingsTabGeneralSeasonBox");
    
    /** Radio button for configuration items. */
    private JRadioButton noThemeOption = new JRadioButton("keine"),
            defaultThemeOption = new JRadioButton("Standard"),
            ownThemeOption = new JRadioButton("eigene");
    
    /** Label for the currently selected notification theme. */
    private JLabel ownThemeLabel;
    
    /** Button for playing the currently selected notification theme. */
    private Button playThemeButton = new Button("SettingsTabGeneralPlayButton", 
            new ImageIcon(new Polygon(Color.gray, Color.white, 
                    new int[] {0, 0, 7 * 2}, new int[] {0, 7 * 2, 7}, 3).getImage()), this);
    
    /** Button for choosing a local music theme. */
    private Button chooseButton = new Button("SettingsTabGeneralChooseButton", this);
    
    /** Button for opening the table of notifications window. */
    private Button openTableOfNotifications = 
            new Button("SettingsTabGeneralOpenNotisButton", "/media/calendar_new64.png", Button.ICON_SIZE_L, this);
    
    /** Initial and currently selected look&feel. */
    private Style initUI, currentUI;

    /** Button for resetting the config selecttion to their default values. */ 
    private Button defaultButton = new Button("SettingsDefaultButton", this);
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(SettingsTabGeneral.class);
    
    /**
     * Arrange tab 1: "Allgemein".
     * @param caller - Parent calendar object
     */
    public SettingsTabGeneral(Calendar caller) {
        super(new BorderLayout());
        this.caller = caller;
        
        VerticalFlowPanel pMain = new VerticalFlowPanel();
        pMain.add(createGeneralSettingsPanel());
        pMain.add(createViewSettingsPanel());
        pMain.add(createReminderSettingsPanel());
        
        JPanel pC = new JPanel(new GridLayout(1, 1));
        pC.add(new JScrollPane(pMain));
        pC.setBorder(new EmptyBorder(5, 5, 5, 5));
        pC.setOpaque(false);
        
        add(pC, BorderLayout.CENTER);
        add(defaultButton.getPanel(new FlowLayout(FlowLayout.RIGHT)), BorderLayout.SOUTH);
        setOpaque(false);
    }
    
    /**
     * Create the panel section with general settings.
     * @return the panel section with general settings.
     */
    private JPanel createGeneralSettingsPanel() {
        /* Create the panels. */
        JPanel mainPanel = new JPanel(new GridLayout(5, 1));
        JPanel workDirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel onCloseActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel onClickEventActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel onClickDayActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel systrayAndUpdatePanel = new JPanel(new GridLayout(1, 2));
        
        /* Setup the check boxes / selection menus. */
        File workingDir = new File(caller.getWorkspace());
        JLabel workingDirLabel = new JLabel(workingDir.getAbsolutePath());
        workingDirLabel.setForeground(Color.GRAY);
        
        remindBox = new JComboBox<String>();
        for (RemindOption ro : RemindOption.values()) {
            remindBox.addItem(ro.getName(false));
        }
        remindBox.setSelectedIndex(caller.getConfig().getProperty(EnumProperty.Remind).ordinal());
        onCloseBox = new JComboBox<String>();
        onCloseBox.addItem(OnCloseAction.exit.toString());
        onCloseBox.addItem(OnCloseAction.moveToSystray.toString());
        onCloseBox.setSelectedIndex(caller.getConfig().getProperty(EnumProperty.AtClose).ordinal());
        onClickDayBox = new JComboBox<String>();
        onClickDayBox.addItem(OnClickDayAction.overview.toString());
        onClickDayBox.addItem(OnClickDayAction.newEvent.toString());
        onClickDayBox.addItem(OnClickDayAction.none.toString());
        onClickDayBox.setSelectedIndex(caller.getConfig().getProperty(EnumProperty.AtClickDay).ordinal());
        onClickEventBox = new JComboBox<String>();
        onClickEventBox.addItem(OnClickEventAction.open.toString());
        onClickEventBox.addItem(OnClickEventAction.edit.toString());
        onClickEventBox.addItem(OnClickEventAction.none.toString());
        onClickEventBox.setSelectedIndex(caller.getConfig().getProperty(EnumProperty.AtClickEvent).ordinal());
        systrayBox.setSelected(caller.getConfig().getProperty(BoolProperty.SystrayStart));
        autoUpdateBox.setSelected(caller.getConfig().getProperty(BoolProperty.AutoUpdate));
        
        /* Add the content to the panels. */
        workDirPanel.add(new JLabel(" Arbeitsverzeichnis: "));
        workDirPanel.add(workingDirLabel);
        onCloseActionPanel.add(new JLabel(" " + Msg.getMessage("SettingsTabGeneralOnCloseAction")));
        onCloseActionPanel.add(onCloseBox);
        onClickEventActionPanel.add(new JLabel(" " + Msg.getMessage("SettingsTabGeneralOnClickEventAction")));
        onClickEventActionPanel.add(onClickEventBox);
        onClickDayActionPanel.add(new JLabel(" " + Msg.getMessage("SettingsTabGeneralOnClickDayAction")));
        onClickDayActionPanel.add(onClickDayBox);
        JPanel padding = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        padding.add(autoUpdateBox);
        systrayAndUpdatePanel.add(padding);
        systrayAndUpdatePanel.add(systrayBox);
        
        mainPanel.add(workDirPanel);
        mainPanel.add(onCloseActionPanel);
        mainPanel.add(onClickEventActionPanel);
        mainPanel.add(onClickDayActionPanel);
        mainPanel.add(systrayAndUpdatePanel);
        mainPanel.setBorder(GUIUtils.getTiteledBorder("Allgemein"));
        
        return mainPanel;
    }
    
    /**
     * Create the panel section with view / look&feel settings.
     * @return the panel section with view / look&feel settings.
     */
    private JPanel createViewSettingsPanel() {
        /* Create the panels. */
        JPanel mainPanel = new JPanel(new GridLayout(3, 2));
        JPanel stylePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel weekStartPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        /* Setup the check boxes / selection menus. */
        styleBox = new JComboBox<String>();
        styleBox.addItem(Style.system.toString());
        styleBox.addItem(Style.swingOcean.toString());
        styleBox.addItem(Style.motif.toString());
        styleBox.addItem(Style.nimbus.toString());
        styleBox.addItem(Style.swingDefault.toString());
        
        initUI = (Style) caller.getConfig().getProperty(EnumProperty.Style);
        currentUI = initUI; 
        styleBox.setSelectedIndex(caller.getConfig().getProperty(EnumProperty.Style).ordinal());
        styleBox.addItemListener(this);
        moonBox.setSelected(caller.getConfig().getProperty(BoolProperty.ShowMoon));
        zodiacBox.setSelected(caller.getConfig().getProperty(BoolProperty.ShowZodiac));
        buttonTextsBox.setSelected(caller.getConfig().getProperty(BoolProperty.ButtonTexts));

        weekStart = new JComboBox<String>(new String[] {"Montag", "Sonntag"});
        weekStart.setSelectedIndex(0);
        if (caller.getConfig().getProperty(IntProperty.FirstDayOfWeek) == java.util.Calendar.SUNDAY) {
            weekStart.setSelectedIndex(1);
        }
        
        /* Add the content to the panels. */
        stylePanel.add(new JLabel(" " + Msg.getMessage("SettingsTabGeneralStyle")));
        stylePanel.add(styleBox);
        weekStartPanel.add(new JLabel(" " + IntProperty.FirstDayOfWeek.getDescription()));
        weekStartPanel.add(weekStart);

        mainPanel.add(stylePanel);
        mainPanel.add(moonBox);
        mainPanel.add(weekStartPanel);
        mainPanel.add(zodiacBox);
        mainPanel.add(new JLabel());
        mainPanel.add(buttonTextsBox);
        mainPanel.setBorder(GUIUtils.getTiteledBorder("Anzeige"));
        
        return mainPanel;
    }
    
    /**
     * Create the panel section with reminder settings.
     * @return the panel section with reminder settings.
     */
    private JPanel createReminderSettingsPanel() {
        /* Create the panels. */
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        VerticalFlowPanel notifyOptionsWest = new VerticalFlowPanel(5);
        JPanel openNotiWindowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel remindTimeDistancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel notifyThemePanel = new JPanel(new BorderLayout());
        JPanel ownThemePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        VerticalFlowPanel eventsPanel = new VerticalFlowPanel();
        JPanel notifyThemeOptions = new JPanel(new GridLayout(3, 1));
        JPanel notifyThemeLabel = new JPanel(new GridLayout(3, 1));
        JPanel playThemePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        
        /* Setup the check boxes / selection menus. */
        notifyTimeShiftBox.setSelected(caller.getConfig().getProperty(BoolProperty.NotifyTimeShift));
        notifySeasonBox.setSelected(caller.getConfig().getProperty(BoolProperty.NotifySeason));
        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(noThemeOption);
        themeGroup.add(defaultThemeOption);
        themeGroup.add(ownThemeOption);
        
        if (caller.getConfig().getProperty(BoolProperty.PlayTheme)) {
            defaultThemeOption.setSelected(true);
        } else {
            noThemeOption.setSelected(true);
        }
        
        chooseButton.setMargin(new Insets(2, 2, 2, 2));
        if (caller.getConfig().getTheme() == Configuration.DEFAULT_CONFIG.getTheme()) {
            ownThemeLabel = new JLabel("");
            ownThemeLabel.setToolTipText(null);
            chooseButton.setEnabled(false);
        } else {
            ownThemeOption.setSelected(true);
            String path = caller.getConfig().getTheme();
            ownThemeLabel = new JLabel(path);
            ownThemeLabel.setToolTipText(path);
            chooseButton.setEnabled(true);
        }
        
        ownThemeLabel.setForeground(Color.GRAY);
        ownThemeOption.addItemListener(this);
        playThemeButton.setMargin(new Insets(0, 2, 0, 2));
        
        /* Add the content to the panels. */
        ownThemePanel.add(ownThemeOption);
        ownThemePanel.add(chooseButton);
        ownThemePanel.add(ownThemeLabel);
        notifyThemeOptions.add(noThemeOption);
        notifyThemeOptions.add(defaultThemeOption);
        notifyThemeOptions.add(ownThemePanel);
        notifyThemeLabel.add(new JLabel("   Erinnerungsmelodie:"));
        playThemePanel.add(playThemeButton);
        notifyThemeLabel.add(playThemePanel);
        
        openNotiWindowPanel.add(openTableOfNotifications);
        remindTimeDistancePanel.add(new JLabel(" " + Msg.getMessage("SettingsTabGeneralRemindOption")));
        remindTimeDistancePanel.add(remindBox);
        notifyThemePanel.add(notifyThemeLabel, BorderLayout.WEST);
        notifyThemePanel.add(notifyThemeOptions, BorderLayout.CENTER);
        
        eventsPanel.add(new JLabel("<html><br><br><br>Besondere Ereignisse:</html>"));
        eventsPanel.add(notifyTimeShiftBox);
        eventsPanel.add(notifySeasonBox);
        
        notifyOptionsWest.add(openNotiWindowPanel);
        notifyOptionsWest.add(remindTimeDistancePanel);
        notifyOptionsWest.add(notifyThemePanel);
        mainPanel.setBorder(GUIUtils.getTiteledBorder("Erinnerung"));
        mainPanel.add(notifyOptionsWest);
        mainPanel.add(eventsPanel);
        
        return mainPanel;
    }
    
    /**
     * Return a configuration object with the currently set selections in this
     * tab. Other configuration parts remain default.
     * @return configuration with the current settings in this tab.
     */
    public Configuration getConfig() {
        Configuration newConfig = new Configuration();
        newConfig.setProperty(EnumProperty.DefaultView, caller.getGUI().getFrame().getView());
        newConfig.setProperty(EnumProperty.Remind, remindBox.getSelectedIndex());
        newConfig.setProperty(EnumProperty.AtClose, onCloseBox.getSelectedIndex());
        newConfig.setProperty(EnumProperty.AtClickDay, onClickDayBox.getSelectedIndex());
        newConfig.setProperty(EnumProperty.AtClickEvent, onClickEventBox.getSelectedIndex());
        newConfig.setProperty(EnumProperty.Style, styleBox.getSelectedIndex());
        newConfig.setProperty(BoolProperty.AutoUpdate, autoUpdateBox.isSelected());
        newConfig.setProperty(BoolProperty.NotifyTimeShift, notifyTimeShiftBox.isSelected());
        newConfig.setProperty(BoolProperty.NotifySeason, notifySeasonBox.isSelected());
        newConfig.setProperty(BoolProperty.ShowMoon, moonBox.isSelected());
        newConfig.setProperty(BoolProperty.ShowZodiac, zodiacBox.isSelected());
        newConfig.setProperty(BoolProperty.SystrayStart, systrayBox.isSelected());
        newConfig.setProperty(BoolProperty.PlayTheme, !noThemeOption.isSelected());
        newConfig.setProperty(BoolProperty.ButtonTexts, buttonTextsBox.isSelected());
        newConfig.setTheme(ownThemeOption.isSelected() ? ownThemeLabel.getToolTipText() : null);
        newConfig.setProperty(IntProperty.FirstDayOfWeek, weekStart.getSelectedIndex() == 0 
                ? java.util.Calendar.MONDAY : java.util.Calendar.SUNDAY);

        return newConfig;
    }
    
    /**
     * Cancel this settings dialog without applying new settings. This will reset
     * the look&feel to its previous state.
     */
    public void cancelled() {
        if (currentUI != initUI) {
            caller.getGUI().getFrame().setUI(initUI, true);
        }
    }
    
    @Override
    public void itemStateChanged(ItemEvent i) {
        if (i.getStateChange() == ItemEvent.DESELECTED) {
            // only react to SELECTED events
            return;
        }
        
        if (i.getSource().equals(styleBox)) {
            currentUI = Style.values()[styleBox.getSelectedIndex()];
            caller.getGUI().getFrame().setUI(currentUI, true);
        } else if (i.getSource().equals(ownThemeOption)) {
            chooseButton.setEnabled(ownThemeOption.isSelected());
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent a) {
        
        /*
         * Use defaults button clicked
         */
        if (a.getSource().equals(defaultButton)) {
            LOG.fine("TAB GENERAL");
            remindBox.setSelectedIndex(
                    Configuration.DEFAULT_CONFIG.getProperty(EnumProperty.Remind).ordinal());
            onCloseBox.setSelectedIndex(
                    Configuration.DEFAULT_CONFIG.getProperty(EnumProperty.AtClose).ordinal());
            onClickDayBox.setSelectedIndex(
                    Configuration.DEFAULT_CONFIG.getProperty(EnumProperty.AtClickDay).ordinal());
            onClickEventBox.setSelectedIndex(
                    Configuration.DEFAULT_CONFIG.getProperty(EnumProperty.AtClickEvent).ordinal());
            styleBox.setSelectedIndex(
                    Configuration.DEFAULT_CONFIG.getProperty(EnumProperty.Style).ordinal());
            autoUpdateBox.setSelected(Configuration.DEFAULT_CONFIG.getProperty(BoolProperty.AutoUpdate));
            notifyTimeShiftBox.setSelected(Configuration.DEFAULT_CONFIG.getProperty(BoolProperty.NotifyTimeShift));
            notifySeasonBox.setSelected(Configuration.DEFAULT_CONFIG.getProperty(BoolProperty.NotifySeason));
            moonBox.setSelected(Configuration.DEFAULT_CONFIG.getProperty(BoolProperty.ShowMoon));
            zodiacBox.setSelected(Configuration.DEFAULT_CONFIG.getProperty(BoolProperty.ShowZodiac));
            systrayBox.setSelected(Configuration.DEFAULT_CONFIG.getProperty(BoolProperty.SystrayStart));
            buttonTextsBox.setSelected(Configuration.DEFAULT_CONFIG.getProperty(BoolProperty.ButtonTexts));
            defaultThemeOption.setSelected(true);
        
        /*
         * Play theme button clicked
         */
        } else if (a.getSource().equals(playThemeButton)) {
            if (ownThemeOption.isSelected()) {
                Music.playTheme(ownThemeLabel.getToolTipText(), false, caller);
            } else if (defaultThemeOption.isSelected()) {
                Music.playTheme(Const.DEFAULT_THEME, true, caller);
            }
            return;
        
        /*
         * Choose theme file button clicked 
         */
        } else if (a.getSource().equals(chooseButton)) {
            String dir = ownThemeLabel.getToolTipText() == null ? null : ownThemeLabel.getToolTipText().substring(0,
                    ownThemeLabel.getToolTipText().lastIndexOf(File.separator));
            LOG.fine("theme dir=" + dir);
            JFileChooser chooser = new JFileChooser(dir);
            chooser.setFileFilter(new FileNameExtensionFilter("Musik-Datei (*.wav)", "wav"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();
                ownThemeLabel.setText(path);
                ownThemeLabel.setToolTipText(path);
            }
        
        /*
         * Open the frame with current events (based on the remind option selected)
         */
        } else if (a.getSource().equals(openTableOfNotifications)) {
            List<Event> events2notify = caller.getEvents2Notify(
                    RemindOption.values()[remindBox.getSelectedIndex()], false);
            
            if (events2notify.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Momentan stehen keine Ereignisse an", 
                        "Keine anstehenden Ereignisse", JOptionPane.INFORMATION_MESSAGE);
            } else if (events2notify.size() == 1) {
                new Notification(caller, events2notify.get(0));
            } else {
                new TableOfNotifications(caller, events2notify);
            }
        }
    }
}
