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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import de.jsteltze.calendar.UI.GUIUtils;
import de.jsteltze.common.ui.Button;
import de.jsteltze.common.ui.JTextAreaContextMenu;

/**
 * Calendar help (or welcome) dialog.
 * This dialog displays the HTML help pages from media/html.
 * @author Johannes Steltzer
 *
 */
public class CalendarWelcomeFrame 
    extends JFrame 
    implements HyperlinkListener {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Default window size and minimum window size. */
    private static final Dimension MIN_SIZE = new Dimension(580, 320),
            DEFAULT_SIZE = new Dimension(630, 600);

    /** Close button. */
    private Button closeButton = new Button("CalendarWelcomeFrameCloseButton", new ActionListener() {
        
        @Override
        public void actionPerformed(ActionEvent arg0) {
            setVisible(false);
            dispose();
        }
    });

    /** Tabs to categorize the help pages. */
    private JTabbedPane tabs = new JTabbedPane();

    /** The different tab idicies. */
    public static final int TAB_GUI = 0, TAB_EVENTS = 1, TAB_FREQ = 2,
            TAB_REMIND = 3, TAB_SETTINGS = 4, TAB_INTERNAL = 5;

    /**
     * Build and show the help dialog with the first tab selected.
     * @param parent - Parent frame
     */
    public CalendarWelcomeFrame(JFrame parent) {
        this(parent, TAB_GUI);
    }

    /**
     * Build and show the help dialog.
     * @param parent - Parent frame
     * @param tabNo - Tab index to be initially selected 
     */
    public CalendarWelcomeFrame(JFrame parent, int tabNo) {
        super("Willkommen zum Java-Kalender");
        setLayout(new BorderLayout());
        setIconImage(new ImageIcon(CalendarWelcomeFrame.class.getResource("/media/bulb32.png")).getImage());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel headerLabel = new JLabel(
                "<html><h1>Willkommen zum Java-Kalender!</h1>"
                        + "<p style=\"font-size: 105%;\">Dieses Programm ist ein einfach zu bedienender"
                        + " und platformunabh‰ngiger Kalender.<br>"
                        + "Mit dem Kalender kann man Ereignisse verwalten und Erinnerungen einstellen.</p></html>");
        headerPanel.add(new JLabel(new ImageIcon(CalendarWelcomeFrame.class
                .getResource("/media/calendar_new64.png"))));
        headerPanel.add(headerLabel);

        tabs.addTab("Oberfl‰che", viewPage("/media/html/gui.html"));
        tabs.addTab("Ereignisse", viewPage("/media/html/events.html"));
        tabs.addTab("Regelm‰ﬂigkeiten", viewPage("/media/html/frequency.html"));
        tabs.addTab("Erinnerungen", viewPage("/media/html/remind.html"));
        tabs.addTab("Einstellungen", viewPage("/media/html/settings.html"));
        tabs.addTab("Intern", viewPage("/media/html/internal.html"));
        tabs.setSelectedIndex(tabNo);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        JLabel infoText = new JLabel(
                "‹ber die Einstellungen kann dieses Fenster jederzeit wieder angezeigt werden.");
        infoText.setForeground(Color.gray);
        buttonPanel.add(infoText, BorderLayout.WEST);
        buttonPanel.add(closeButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel, BorderLayout.CENTER);

        setSize(DEFAULT_SIZE);
        setMinimumSize(MIN_SIZE);
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    /**
     * Display a HTML page, wrapped by scroll bars.
     * @param url - Internal (inside jar) URL of the HTML page to be displayed 
     * @return scroll pane with the HTML content
     */
    private JScrollPane viewPage(String url) {
        Component panel = null;
        try {
            JEditorPane htmlViewer = new JEditorPane(CalendarWelcomeFrame.class.getResource(url));
            htmlViewer.setEditable(false);
            htmlViewer.addHyperlinkListener(this);
            htmlViewer.setComponentPopupMenu(new JTextAreaContextMenu(htmlViewer));
            panel = htmlViewer;
        } catch (IOException e) {
            // show error message instead of HTML content
            panel = new JLabel("<html>Durch einen Fehler kann diese Seite nicht angezeigt werden:<br>" 
                    + e.toString() + "</html>");
        }
        
        JScrollPane scrollPane = new JScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.white);
        return scrollPane;
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        String link = e.getURL().toString();
        
        if (e.getEventType() == EventType.ACTIVATED) {
            
            if (link.startsWith("http")) {
                // open web pages with default browser
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException | URISyntaxException e1) {
                    GUIUtils.showErrorMessage("<html>Die URL <i>" + link + "</i> kann nicht geˆffnet werden.", 
                            "URL-Fehler", e1);
                }
            } else
            
            // in case of references to local help page -> open corresponding tab
            if (link.endsWith("events.html")) {
                tabs.setSelectedIndex(TAB_EVENTS);
            } else if (link.endsWith("remind.html")) {
                tabs.setSelectedIndex(TAB_REMIND);
            } else if (link.endsWith("frequency.html")) {
                tabs.setSelectedIndex(TAB_FREQ);
            } else if (link.endsWith("gui.html")) {
                tabs.setSelectedIndex(TAB_GUI);
            } else if (link.endsWith("settings.html")) {
                tabs.setSelectedIndex(TAB_SETTINGS);
            } else if (link.endsWith("internal.html")) {
                tabs.setSelectedIndex(TAB_INTERNAL);
            }
        } else
        
        if (e.getEventType() == EventType.ENTERED && link.startsWith("http")) {
            ((JComponent) e.getSource()).setToolTipText(link);
        } else {
            ((JComponent) e.getSource()).setToolTipText(null);
        }
    }
}
