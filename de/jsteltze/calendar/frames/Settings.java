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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.config.Configuration;
import de.jsteltze.calendar.config.Configuration.IntProperty;
import de.jsteltze.common.Log;
import de.jsteltze.common.ui.Button;

/**
 * Settings frame for calendar configuration.
 * @author Johannes Steltzer
 *
 */
public class Settings 
    extends JDialog 
    implements ActionListener {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Parent calendar object. */
    private Calendar caller;
    
    /** Tab pane. */
    private JTabbedPane tab;
    
    /** Tab index. */
    public static final int TAB_GENERAL = 0,
            TAB_HOLIDAYS = 1,
            TAB_COLORS = 2,
            TAB_IMEXPORT = 3,
            TAB_INFO = 4;
    
    /** Tab 1: general settings. */
    private SettingsTabGeneral tabGeneral;

    /** Tab 2: holiday settings. */
    private SettingsTabHolidays tabHolidays;
    
    /** Tab 3: color settings. */
    private SettingsTabColors tabColors;
    
    /** Tab 4: import/export. */
    private SettingsTabImportExport tabImportExport;
    
    /** Tab 5: program info. */
    private SettingsTabInfo tabInfo;
    
    /** Button for ok (submit settings) and cancel. */
    private Button okButton = new Button("SettingsSaveButton", this), 
            cancelButton = new Button("SettingsCancelButton", this);
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(Settings.class);
    
    /** Default size on start up. */
    private static final Dimension DEFAULT_SIZE = new Dimension(650, 400);

    /**
     * Arranges all elements in this settings frame.
     * Fills the tabs with content.
     * @param tabNo - Tab to select (frame will launch with this
     *         tab shown), see Settings.TAB_XXX
     */
    private void arrangeFrame(int tabNo) {
        setLayout(new BorderLayout());
        
        tabGeneral = new SettingsTabGeneral(caller);
        tabHolidays = new SettingsTabHolidays(caller);
        tabColors = new SettingsTabColors(caller);
        tabImportExport = new SettingsTabImportExport(caller);
        tabInfo = new SettingsTabInfo(caller);
        
        tab = new JTabbedPane();
        tab.addTab("Allgemein", tabGeneral);
        tab.addTab("Feiertage", tabHolidays);
        tab.addTab("Farben", tabColors);
        tab.addTab("Import/Export", tabImportExport);
        tab.addTab("Programminfo", tabInfo);
        tab.setSelectedIndex(tabNo);
        tab.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel buttonPanel = okButton.getPanel();
        buttonPanel.add(cancelButton);

        add(tab, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        addWindowListener(new WindowAdapter() {
            
            @Override
            public void windowClosing(WindowEvent e) {
                tabGeneral.cancelled();
                setVisible(false);
                dispose();
            }
        });

        setSize(DEFAULT_SIZE);
        setLocationRelativeTo(caller.getGUI().getFrame());
        setVisible(true);
        setResizable(true);
    }

    /**
     * Construct a new settings frame.
     * @param c - Parent calendar object
     */
    public Settings(Calendar c) {
        this(c, 0);
    }

    /**
     * Construct a new settings frame.
     * @param c - Parent calendar object
     * @param tabNo - Tab to select (frame will launch with this
     *         tab shown), see Settings.TAB_XXX
     */
    public Settings(Calendar c, int tabNo) {
        super(c.getGUI().getFrame(), "Einstellungen...");
        caller = c;
        setIconImage(new ImageIcon(Settings.class.getResource("/media/settings.png")).getImage());
        arrangeFrame(tabNo);
    }

    @Override
    public void actionPerformed(ActionEvent a) {
        /*
         * OK button clicked
         */
        if (a.getSource().equals(okButton)) {
            LOG.info("ok button: apply settings");
            if (tab.getSelectedIndex() == TAB_IMEXPORT && tabImportExport.checkSomethingSelectedNotYetExported()) {
                
                if (JOptionPane.showOptionDialog(caller.getGUI().getFrame(), 
                        "Es wurden Daten zum Export ausgewählt aber der Export wurde noch nicht durchgeführt.\n"
                        + "Über den Button \"Datei...\" kann der Export gestartet werden.", 
                        "Export-Auswahl verwerfen?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, 
                        new String[] {"Auswahl verwerfen und Export nicht durchführen", "zurück zur Auswahl"}, 
                        "zurück zur Auswahl") == 1) {
                    return;
                }
            }
            
            /* apply new configuration */
            Configuration newConfig = tabGeneral.getConfig();
            int[] holidayCodes = tabHolidays.getHolidayCodes();
            Color[] colors = tabColors.getColorsChosen();

            newConfig.setColors(colors);
            newConfig.setProperty(IntProperty.HolidayID, holidayCodes[0]);
            newConfig.setProperty(IntProperty.SpecialDaysID, holidayCodes[1]);
            newConfig.setProperty(IntProperty.ActionDays1ID, holidayCodes[2]);
            newConfig.setProperty(IntProperty.ActionDays2ID, holidayCodes[3]);

            caller.setConfig(newConfig);
            
            /* any case: close this frame */ 
            this.setVisible(false);
            this.dispose();
        
        
        /*
         * Cancel
         */
        } else {
            // call window closing listener
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }
}
