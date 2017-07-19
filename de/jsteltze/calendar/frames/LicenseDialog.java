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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import de.jsteltze.calendar.config.Const;
import de.jsteltze.common.LinkLabel;
import de.jsteltze.common.Msg;
import de.jsteltze.common.VerticalFlowPanel;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.ui.Button;
import de.jsteltze.common.ui.JTextAreaContextMenu;

/**
 * LicenseDialog.
 * @author Johannes Steltzer
 *
 */
public final class LicenseDialog {
    
    /** Dimension of the scaled icon. */
    private static final int ICON_W = 101, ICON_H = 58;
    
    /** Preferred dimension for the icons table. */
    private static final Dimension DIM_ICONS_PANE = new Dimension(520, 250);
    
    /** Parent component. */
    private static CalendarFrame parentComponent;
    
    /**
     * Private constructor (not for public use).
     */
    private LicenseDialog() { }

    /**
     * Launch the frame with program/license information.
     * @param parent - Parent frame
     */
    public static void showProgramInfo(CalendarFrame parent) {
        parentComponent = parent;
        ImageIcon icon = new ImageIcon(LicenseDialog.class.getResource("/media/logo.gif"));
        Image img = icon.getImage().getScaledInstance(ICON_W, ICON_H, Image.SCALE_SMOOTH);
        
        final JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Version", createVersionTab());
        tabs.addTab("Icons", createIconsTab());
        tabs.addTab("Bibliotheken", createLibsTab());
        tabs.addHierarchyListener(new HierarchyListener() {
            
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                Window window = SwingUtilities.getWindowAncestor(tabs);
                if (window instanceof Dialog) {
                    Dialog dialog = (Dialog) window;
                    if (!dialog.isResizable()) {
                        dialog.setResizable(true);
                    }
                }
            }
        });
        JScrollPane mainPane = new JScrollPane(tabs);
        mainPane.setBorder(null);
        JOptionPane.showMessageDialog(parent, mainPane, Msg.getMessage("InfoDialogTitle"), 
                JOptionPane.INFORMATION_MESSAGE, new ImageIcon(img));
    }
    
    /**
     * Create a tab with program version and license information.
     * @return the version tab.
     */
    private static Component createVersionTab() {
        JPanel versionMainPanel = new JPanel(new BorderLayout());
        JPanel versionPanel = new JPanel(new BorderLayout(10, 10));
        versionPanel.setOpaque(false);
        versionPanel.add(new JLabel("<html><h2>Java-Kalender " + Const.VERSION + "</h2>" 
                + Const.AUTHOR + ", " + Date.month2String(Const.LAST_EDIT_DATE.get(java.util.Calendar.MONTH), false)
                + " " + Const.LAST_EDIT_DATE.get(java.util.Calendar.YEAR) + "<br><br>" 
                + Msg.getMessage("InfoDialogVersionTabLicenseTitle") + "</html>"), BorderLayout.WEST);
        JPanel webSitePanel = new JPanel(new BorderLayout());
        webSitePanel.add(new JLabel(""), BorderLayout.CENTER);
        webSitePanel.add(new LinkLabel("<html>" + Const.HOME_URL + "<br><br><br></html>", "Java-Kalender Homepage", 
                Const.HOME_URL), BorderLayout.SOUTH);
        webSitePanel.setOpaque(false);
        versionPanel.add(webSitePanel, BorderLayout.EAST);
        JTextArea gplText = new JTextArea(6, 1);
        gplText.setEditable(false);
        gplText.setComponentPopupMenu(new JTextAreaContextMenu(gplText));
        InputStream licenseStream = ClassLoader.getSystemResourceAsStream("License_GPL3.txt");
        Scanner sc = new Scanner(licenseStream);
        while (sc.hasNext()) {
            gplText.append(sc.nextLine() + "\n");
        }
        sc.close();
        gplText.setCaretPosition(0);
        gplText.setForeground(Color.gray);
        versionMainPanel.add(versionPanel, BorderLayout.NORTH);
        versionMainPanel.add(new JScrollPane(gplText), BorderLayout.CENTER);
        versionMainPanel.setOpaque(false);
        versionMainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        return versionMainPanel;
    }
    
    /** List of all libraries which are used. */
    private static List<IconInfo> icons = Arrays.asList(
            new IconInfo(Msg.getMessage("IconsCalendarIcon"), "/media/calendar_new64.png", 
                    "Snipicons", "Creative Commons Attribution", "JSnip Master"),
            new IconInfo(Msg.getMessage("IconsClockIcon"), "/media/clock_alarm32.png", 
                    "WooCons", "Creative Commons Attribution", "Janik Baumgartner"),
            new IconInfo(Msg.getMessage("IconsDateIcon"), "/media/categories/date20.png",
                    "Green And Blue", "Creative Commons Attribution", "Juan Miguel Gómez Wilson"),
            new IconInfo(Msg.getMessage("IconsTravelIcon"), "/media/categories/travel20.png",
                    "Standard Transport", "Freeware Non-commercial", "Aha-Soft"),
            new IconInfo(Msg.getMessage("IconsImportantIcon"), "/media/categories/important20.png",
                    "Snowish", "GNU/GPL", "Alexander Moore"),
            new IconInfo(Msg.getMessage("IconsPartyIcon"), "/media/categories/cookie20.png",
                    "Nuvola", "GNU/GPL", "David Vignoni"),
            new IconInfo(Msg.getMessage("IconsBalloonsIcon"), "/media/categories/balloons20.png",
                    "wpclipart", "Public Domain", ""),
            new IconInfo(Msg.getMessage("IconsPrivateIcon"), "/media/categories/private20.png",
                    "Crystal Project", "GNU/GPL", "YellowIcon"),
            new IconInfo(Msg.getMessage("IconsWorkIcon"), "/media/categories/work20.png",
                    "GiNUX", "Creative Commons Attribution", "Asher"),
            new IconInfo(Msg.getMessage("IconsSportIcon"), "/media/categories/sport20.png",
                    "Sport", "Freeware Non-commercial", "Aha-Soft"),
            new IconInfo(Msg.getMessage("IconsPizzaIcon"), "/media/categories/pizza20.png",
                    "Farm-fresh", "Creative Commons Attribution", "FatCow Web Hosting"),
            new IconInfo(Msg.getMessage("IconsPlantIcon"), "/media/categories/plant20.png",
                    "Green", "Creative Commons Attribution", "IconTexto"),
            new IconInfo(Msg.getMessage("IconsCarnivalIcon"), "/media/categories/carnival20.png",
                    "Happy New Year", "Free for commercial use", "Vignesh Oviyan"),
            new IconInfo(Msg.getMessage("IconsMusicIcon"), "/media/categories/music20.png",
                    "Snipicons", "Creative Commons Attribution", "Snip Master"),
            new IconInfo(Msg.getMessage("IconsMoneyIcon"), "/media/categories/money20.png",
                    "Fresh", "Free for commercial use", "IconEden"),
            new IconInfo(Msg.getMessage("IconsMovieIcon"), "/media/categories/movie20.png",
                    "Somatic Rebirth System", "Freeware Non-commercial", "David Lanham"),
            new IconInfo(Msg.getMessage("IconsGameIcon"), "/media/categories/chess20.png",
                    "Mac", "Freeware Non-commercial", "Iconshock"),
            new IconInfo(Msg.getMessage("IconsHalloweenIcon"), "/media/categories/pumpkin20.png",
                    "Vintage Halloween", "Freeware Non-commercial", "David Lanham"),
            new IconInfo(Msg.getMessage("IconsWitchIcon"), "/media/categories/witch20.png",
                    "Desktop Halloween", "Creative Commons Attribution", "Aha-Soft"),
            new IconInfo(Msg.getMessage("IconsEasterIcon"), "/media/categories/easter20.png",
                    "Easter Icon Set", "Free for commercial use", "Visual Pharm"),
            new IconInfo(Msg.getMessage("IconsFlowerIcon"), "/media/categories/flower20.png",
                    "Green Emoticons", "Creative Commons Attribution", "Visual Pharm"),
            new IconInfo(Msg.getMessage("IconsCandleIcon"), "/media/categories/candle20.png",
                    "Silent Night Christmas", "Freeware", "Artdesigner (Tanya)"),
            new IconInfo(Msg.getMessage("IconsMagnifierIcon"), "/de/jsteltze/common/ui/lupe16.png",
                    "Fugue", "Creative Commons Attribution", "Yusuke Kamiyamane"),
            new IconInfo(Msg.getMessage("IconsSaveIcon"), "/media/save16.png",
                    "Toolbar", "Freeware Non-commercial", "Artua.com"),
            new IconInfo(Msg.getMessage("IconsAddIcon"), "/media/add12.png",
                    "Office", "Freeware Non-commercial", "Custom Icon Design"),
            new IconInfo(Msg.getMessage("IconsDeleteIcon"), "/media/delete12.png",
                    "Base Software", "Freeware (Link Required)", "Icons-Land"),
            new IconInfo(Msg.getMessage("IconsBulbIcon"), "/media/bulb32.png",
                    "Human o2", "Freeware Non-commercial", "Oliver Scholtz (and others)"),
            new IconInfo(Msg.getMessage("IconsConsoleIcon"), "/media/console32.png",
                    "Free icons for Windows8/Metro", "Creative Commons Attribution", "VisualPharm"),
            new IconInfo(Msg.getMessage("IconsSettingsIcon"), "/media/settings.png",
                    "Juicy Fruit", "Freeware", "Jeremy Sallee"),
            new IconInfo(Msg.getMessage("IconsToolIcon"), "/media/categories/tools20.png",
                    "Gloss: Basic", "Creative Commons Attribution", "Momenticons"),
            new IconInfo(Msg.getMessage("IconsZodiacIcon"), "/media/zodiac/krebs.png",
                    "Astronomical signs", "Creative Commons Attribution", "Chameleon Design"),
            new IconInfo(Msg.getMessage("IconsGregorXIII"), "",
                    "", "", "http://www.janeuber.de/papst-gregor-13.htm")
            );
    
    /**
     * Create a tab with information about external icons used.
     * @return the icons tab.
     */
    private static Component createIconsTab() {
        String msg = "<table border=\"1\">" + IconInfo.getHtmlTableHeader();
        
        for (IconInfo icon : icons) {
            msg += icon.getHtmlTableRow();
        }
        msg += "</table>";
        
        JPanel iconsPanel = new JPanel(new BorderLayout());
        iconsPanel.add(new JLabel("<html><h3>" + Msg.getMessage("IconsUsedTitle") 
                + "</h3></html>"), BorderLayout.NORTH);
        JLabel iconsTable = new JLabel("<html>" + msg + "</html>");
        iconsTable.setVerticalAlignment(SwingConstants.NORTH);
        iconsPanel.add(iconsTable, BorderLayout.CENTER);
        iconsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        iconsPanel.setOpaque(false);
        JScrollPane iconsPane = new JScrollPane(iconsPanel);
        iconsPane.setPreferredSize(DIM_ICONS_PANE);
        iconsPane.setBorder(null);
        iconsPane.getViewport().setBackground(Color.white);
        
        return iconsPane;
    }
    
    /**
     * Create a Button that on click displays a license text in a separate JDialog.
     * @param licenseFileName - File name of the license text to display (on click)
     * @return Button.
     */
    private static Button showLicenseButton(final String licenseFileName) {
        Button showButton = new Button("LibsUsedShowLicenseButton", new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                final int defaultRows = 10, defaultCols = 80;
                JTextArea licenseText = new JTextArea(defaultRows, defaultCols);
                licenseText.setEditable(false);
                licenseText.setComponentPopupMenu(new JTextAreaContextMenu(licenseText));
                InputStream licenseStream = ClassLoader.getSystemResourceAsStream(licenseFileName);
                Scanner sc = new Scanner(licenseStream);
                while (sc.hasNext()) {
                    licenseText.append(sc.nextLine() + "\n");
                }
                sc.close();
                licenseText.setCaretPosition(0);
                licenseText.setForeground(Color.gray);
                
                JDialog licenseFrame = new JDialog(parentComponent, licenseFileName, true);
                licenseFrame.setLayout(new BorderLayout());
                licenseFrame.add(new JScrollPane(licenseText), BorderLayout.CENTER);
                licenseFrame.pack();
                licenseFrame.setLocationRelativeTo(parentComponent);
                licenseFrame.setVisible(true);
            }
        });
        
        return showButton;
    }
    
    /** List of all libraries which are used. */
    private static List<LibraryInfo> libs = Arrays.asList(
            new LibraryInfo("timechoosers.jar", 
                    new LinkLabel("Palantir Technologies", "https://www.palantir.com/", "https://www.palantir.com/"), 
                    "palantir_license.txt", "<html>Auswahl/Anzeige einer Uhrzeit über eine grafische Uhr."
                            + "<br>Der Code wurde leicht modifiziert.<html>")
            );

    /**
     * Create a tab with information about libraries used.
     * @return the libraries tab.
     */
    private static Component createLibsTab() {
        VerticalFlowPanel libsPanel = new VerticalFlowPanel();
        libsPanel.add(new JLabel("<html><h3>" + Msg.getMessage("LibsUsedTitle") 
                + "</h3></html>"));
        libsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        libsPanel.setOpaque(false);
        
        for (LibraryInfo lib : libs) {
            JPanel libPanel = new JPanel(new BorderLayout(20, 20));
            JPanel libDescPanel = new JPanel(new BorderLayout(10, 10));
            JPanel libProducerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            libProducerPanel.add(new JLabel(Msg.getMessage("LibsLabelProducer")));
            libProducerPanel.add(lib.url);
            libDescPanel.add(libProducerPanel, BorderLayout.NORTH);
            libDescPanel.add(new JLabel(lib.description), BorderLayout.CENTER);
            
            JPanel button1Panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            button1Panel.add(showLicenseButton(lib.licenseTextFileName));
            
            JLabel title = new JLabel(lib.libTitle);
            title.setVerticalAlignment(SwingConstants.NORTH);
            libPanel.add(title, BorderLayout.WEST);
            libPanel.add(libDescPanel, BorderLayout.CENTER);
            libPanel.add(button1Panel, BorderLayout.EAST);
            libPanel.setBorder(new EtchedBorder());
            
            libsPanel.add(libPanel);
        }
        
        JScrollPane libsPane = new JScrollPane(libsPanel);
        libsPane.setBorder(null);
        libsPane.getViewport().setBackground(Color.white);
        return libsPane;
    }
    
    /**
     * Data model for libraries used. This calls holds information about a library.
     * @author Johannes Steltzer
     *
     */
    private static class LibraryInfo {
        
        /** Title of the library. */
        private String libTitle;
        
        /** Link to the library producer web site. */
        private LinkLabel url;
        
        /** Name of the license file (top level of jar directory). */
        private String licenseTextFileName;
        
        /** Description of the library (reason for use, modifications, ...). */ 
        private String description;
        
        /**
         * Construct a new library data instance.
         * @param libTitle - Title of the library
         * @param url - Link to the library producer web site
         * @param licenseTextFileName - Name of the license file (top level of jar directory)
         * @param description - Description of the library (reason for use, modifications, ...)
         */
        public LibraryInfo(String libTitle, LinkLabel url, String licenseTextFileName, String description) {
            this.libTitle = libTitle;
            this.url = url;
            this.licenseTextFileName = licenseTextFileName;
            this.description = description;
        }
    }
    
    /**
     * Data model for icons used. This calls holds information about an icon.
     * @author Johannes Steltzer
     *
     */
    private static class IconInfo {
        
        /** Title of the icon. */
        private String iconTitle;
        
        /** Path to the icon within this jar package structure. */
        private String path;
        
        /** Name of package containing the icon. */
        private String iconPackage;
        
        /** License under which the icon may be used. */ 
        private String license;
        
        /** Author of the icon. */ 
        private String author;
        
        /**
         * Construct a new icon data instance.
         * @param iconTitle - Title of the icon
         * @param path - Path to the icon within this jar package structure
         * @param iconPackage - Name of package containing the icon
         * @param license - License under which the icon may be used
         * @param author - Author of the icon
         */
        public IconInfo(String iconTitle, String path, String iconPackage, String license, String author) {
            this.iconTitle = iconTitle;
            this.path = path;
            this.iconPackage = iconPackage;
            this.license = license;
            this.author = author;
        }
        
        /**
         * Creates a HTML table row string with the following columns:
         * <li>icon title together with icon itself
         * <li>icon Package
         * <li>author
         * <li>license.
         * @return HTML table row string.
         */
        public String getHtmlTableRow() {
            String imgCode = path.isEmpty() ? "" : "<img src='" + LicenseDialog.class.getResource(path) 
                    + "' height='16' width='16'>&nbsp;";
            
            return "<tr><td>" + imgCode + iconTitle + "</td>"
                    + "<td>" + iconPackage + "</td>"
                    + "<td>" + author + "</td>"
                    + "<td>" + license + "</td></tr>";
        }
        
        /**
         * Returns the HTML table header.
         * @return the HTML table header.
         */
        public static String getHtmlTableHeader() {
            return "<tr><td></td><td><b>" + Msg.getMessage("IconsPacket") + "</b></td>"
                    + "<td><b>" + Msg.getMessage("IconsAuthor") + "</b></td>"
                    + "<td><b>" + Msg.getMessage("IconsLicense") + "</b></td></tr>";
        }
    }
}
