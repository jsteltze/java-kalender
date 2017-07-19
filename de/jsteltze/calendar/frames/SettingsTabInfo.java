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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Update;
import de.jsteltze.calendar.UI.GUIUtils;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.common.LinkLabel;
import de.jsteltze.common.Log;
import de.jsteltze.common.ui.Button;

/**
 * Settings frame for calendar configuration: tab for program information.
 * @author Johannes Steltzer
 *
 */
public class SettingsTabInfo 
    extends JPanel 
    implements ActionListener {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Parent calendar object. */
    private Calendar caller;
    
    /** Buttons for: update, show license information, help. */
    private Button updateButton = new Button("SettingsTabInfoUpdateButton", this), 
            licenseButton = new Button("SettingsTabInfoLicenseButton", this), 
            logButton = new Button("SettingsTabInfoLogButton", "/media/console32.png", Button.ICON_SIZE_L, this), 
            helpButton = new Button("SettingsTabInfoHelpButton", "/media/bulb32.png", Button.ICON_SIZE_L, this);
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(SettingsTabInfo.class);

    /**
     * Arrange tab 5: "Programminfo".
     * @param caller - Parent calendar object
     */
    public SettingsTabInfo(Calendar caller) {
        super(new BorderLayout());
        this.caller = caller;
        
        JPanel pWest = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1));
        updateButton.setHorizontalAlignment(SwingConstants.LEADING);
        licenseButton.setHorizontalAlignment(SwingConstants.LEADING);
        helpButton.setHorizontalTextPosition(SwingConstants.LEFT);
        helpButton.setHorizontalAlignment(SwingConstants.LEADING);
        logButton.setHorizontalTextPosition(SwingConstants.LEFT);
        logButton.setHorizontalAlignment(SwingConstants.LEADING);
        buttonPanel.add(updateButton);
        buttonPanel.add(licenseButton);
        buttonPanel.add(logButton);
        buttonPanel.add(new JLabel());
        buttonPanel.add(helpButton);
        buttonPanel.setOpaque(false);

        JTextArea jt = new JTextArea(
                Const.FILENAME
                + "\nVersion " + Const.VERSION
                + "\nKompiliert am " + Const.LAST_EDIT_DATE.print() + " mit " + Const.COMPILER);
        jt.setBorder(new EtchedBorder());
        jt.setEditable(false);
        jt.setBackground(Const.COLOR_SETTINGS_INFO_BG);
        
        JLabel author = new JLabel(Const.AUTHOR + " (C) " + Const.LAST_EDIT_DATE.get(java.util.Calendar.YEAR));
        author.setVerticalAlignment(SwingConstants.BOTTOM);
        
        pWest.add(buttonPanel, BorderLayout.NORTH);
        pWest.add(author, BorderLayout.CENTER);
        pWest.add(new LinkLabel(Const.HOME_URL, Const.HOME_URL), BorderLayout.SOUTH);
        pWest.setOpaque(false);
        pWest.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        add(jt, BorderLayout.NORTH);
        add(pWest, BorderLayout.WEST);
        add(new JLabel(new ImageIcon(SettingsTabInfo.class.getResource("/media/logo.gif"))));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setOpaque(false);
    }

    @Override
    public void actionPerformed(ActionEvent a) {
        
        /*
         * Search update button clicked
         */
        if (a.getSource().equals(updateButton)) {
            // check Java version
            String javaVersion = System.getProperty("java.version");
            LOG.info("java version: " + javaVersion);
            if (!javaVersion.startsWith("1.8")) {
                GUIUtils.showErrorMessage("<html><b>Neuere Kalender-Versionen laufen nur noch mit Java 1.8!</b><br>"
                        + "Die aktuell installierte Java-Version ist: " + javaVersion + "<br><br>"
                        + "Bitte Java aktualisieren, um auf die neusten Kalender-Versionen updaten zu können.</html>", 
                                "Java ist veraltet", null);
                return;
            }
            
            new Update(caller, false);
        
        /*
         * License button clicked
         */
        } else if (a.getSource().equals(licenseButton)) {
            LicenseDialog.showProgramInfo(caller.getGUI().getFrame());
        
        /*
         * License button clicked
         */
        } else if (a.getSource().equals(helpButton)) {
            new CalendarWelcomeFrame(caller.getGUI().getFrame());
        
        /*
         * Log button clicked
         */
        } else if (a.getSource().equals(logButton)) {
            LogWindow log = LogWindow.getInstance();
            log.setVisible(true);
            log.toFront();
        }
    }
}
