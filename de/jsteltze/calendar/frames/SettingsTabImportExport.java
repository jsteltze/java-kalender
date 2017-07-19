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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.EventExportHandler;
import de.jsteltze.calendar.ICalParser;
import de.jsteltze.calendar.XMLParser;
import de.jsteltze.calendar.UI.EventTableWithCheckbox;
import de.jsteltze.calendar.config.ColorSet;
import de.jsteltze.calendar.config.Configuration;
import de.jsteltze.calendar.config.Configuration.BoolProperty;
import de.jsteltze.calendar.config.Configuration.EnumProperty;
import de.jsteltze.calendar.config.Configuration.IntProperty;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.common.Log;
import de.jsteltze.common.VerticalFlowPanel;
import de.jsteltze.common.ui.Button;
import de.jsteltze.common.ui.SearchPanel;
import de.jsteltze.common.ui.SelectAllCheckbox;

/**
 * Settings frame for calendar configuration: tab for import/export.
 * @author Johannes Steltzer
 *
 */
public class SettingsTabImportExport 
    extends JPanel 
    implements ActionListener, ItemListener {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Parent calendar object. */
    private Calendar caller;

    /** Array of checkboxes that hold all possible config items for import/export. */
    private JCheckBox[] configBoxes;
    
    /** Checkboxes for selecting all config items or events. */
    private SelectAllCheckbox selectAllConfig = new SelectAllCheckbox("alle Einstellungen..."),
            selectAllEvents = new SelectAllCheckbox("alle angezeigten Ereignisse...");

    /** The body that holds either the import or export content. */
    private JPanel body = new JPanel(new GridLayout(1, 1));
    
    /** The import file name. At the beginning no file yet selected. */
    private String importFileName = "Noch keine Datei gewählt...";
    
    /** 
     * The title label. At import this shows the selected import file name.
     * At export this introduces the own settings/events. We start with export title.
     */
    private JLabel titleLabel = new JLabel("Eigene Einstellungen und Ereignisse:");
    
    /** The radio buttons for selecting import or export. */
    private JRadioButton importRadio = new JRadioButton("Import"),
            exportRadio = new JRadioButton("Export");
    
    /** The label with the user hint on how to use the import/export. We start with export hint. */
    private JLabel hintLabel = new JLabel("<html><body>"
            + "Rechts die<br>Auswahl treffen<br>und dann \"Datei...\"<br>"
            + "klicken zum<br>Exportieren.</body></html>");
    
    /** The possible configuration to be exported/imported. */
    private Configuration possibleConfig;
    
    /** The choose file button for selecting a file for import/export. */
    private Button fileButton = new Button("SettingsTabImportChooseButton", this);
    
    /** The import button for starting the import. */
    private Button importButton = new Button("SettingsTabImportImportButton", this);
    
    /** The ICAL parser for importing ICAL files. */
    private ICalParser icalParser;
    
    /** Has the export function been finished? */
    private boolean exportFunctionCalled = false;
    
    /** Table of events to export/import. */
    private EventTableWithCheckbox eventTable;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(SettingsTabImportExport.class);

    /**
     * Arrange settings tab 4: Import/Export.
     * @param caller - Parent calendar object (for importing/exporting events/settings)
     */
    public SettingsTabImportExport(Calendar caller) {
        super(new BorderLayout(5, 5));
        this.caller = caller;
        
        JPanel pImEx = new JPanel(new GridLayout(4, 1));
        JPanel pWest = new JPanel(new BorderLayout());
        JPanel pMain = new JPanel(new BorderLayout());

        ButtonGroup g = new ButtonGroup();
        g.add(importRadio);
        g.add(exportRadio);
        
        importRadio.setOpaque(false);
        exportRadio.setOpaque(false);
        exportRadio.setSelected(true);
        importRadio.addItemListener(this);
        exportRadio.addItemListener(this);
        
        hintLabel.setForeground(Color.gray);

        pImEx.add(new JLabel());
        pImEx.add(importRadio);
        pImEx.add(exportRadio);
        pImEx.add(fileButton);
        pImEx.setOpaque(false);
        pWest.add(pImEx, BorderLayout.NORTH);
        pWest.add(hintLabel, BorderLayout.SOUTH);
        pWest.setOpaque(false);
        
        body.add(fillTable(caller.getAllUserEvents(), caller.getConfig()));

        titleLabel.setFont(Const.FONT_BORDER_TEXT);
        pMain.add(titleLabel, BorderLayout.NORTH);
        pMain.add(body, BorderLayout.CENTER);
        pMain.setOpaque(false);
        
        add(pWest, BorderLayout.WEST);
        add(pMain, BorderLayout.CENTER);
        setOpaque(false);
        setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    /**
     * For tab "Import/Export": Construct a JPanel with the possible 
     * positions for importing or exporting. This JPanel will be 
     * within the JScrollPanel.
     * @param e - List of events to import/export
     * @param c - Configuration to import/export
     * @return JPanel that contains all events and configurations
     *         (which differ from the default) as selectable check boxes.
     */
    private JPanel fillTable(List<Event> e, Configuration c) {
        possibleConfig = c;

        JPanel pScroll = new JPanel(new BorderLayout());
        VerticalFlowPanel pScrollConfig = new VerticalFlowPanel(0);
        JPanel pScrollEvents = new JPanel(new BorderLayout());

        /* Get config lines to print */
        List<String> configLines = new ArrayList<String>();
        if (!c.equals(Configuration.DEFAULT_CONFIG)) {
            
            for (EnumProperty prop : EnumProperty.values()) {
                if (c.getProperty(prop) != Configuration.DEFAULT_CONFIG.getProperty(prop)) {
                    configLines.add(prop.getDescription() + c.getProperty(prop).toString());
                }
            }
            
            for (BoolProperty prop : BoolProperty.values()) {
                if (c.getProperty(prop) != Configuration.DEFAULT_CONFIG.getProperty(prop)) {
                    configLines.add(prop.getDescription() + ": " + (c.getProperty(prop) ? "Ja" : "Nein"));
                }
            }
            
            if (c.getProperty(IntProperty.HolidayID) 
                    != Configuration.DEFAULT_CONFIG.getProperty(IntProperty.HolidayID)) {
                configLines.add(IntProperty.HolidayID.getDescription());
            }
            if (c.getProperty(IntProperty.SpecialDaysID) 
                    != Configuration.DEFAULT_CONFIG.getProperty(IntProperty.SpecialDaysID)) {
                configLines.add(IntProperty.SpecialDaysID.getDescription());
            }
            if (c.getProperty(IntProperty.ActionDays1ID) 
                    != Configuration.DEFAULT_CONFIG.getProperty(IntProperty.ActionDays1ID)
                    || c.getProperty(IntProperty.ActionDays2ID) 
                    != Configuration.DEFAULT_CONFIG.getProperty(IntProperty.ActionDays2ID)) {
                configLines.add(IntProperty.ActionDays1ID.getDescription());
            }
            if (c.getProperty(IntProperty.FirstDayOfWeek) 
                    != Configuration.DEFAULT_CONFIG.getProperty(IntProperty.FirstDayOfWeek)) {
                configLines.add("Wochenbeginn: " + (c.getProperty(IntProperty.FirstDayOfWeek) 
                        == java.util.Calendar.MONDAY ? "Montag" : "Sonntag"));
            }
            if (c.getTheme() != Configuration.DEFAULT_CONFIG.getTheme()) {
                configLines.add("Eigene Erinnerungsmelodie (nur Dateipfad)");
            }
            for (byte i = 0x00; i < ColorSet.MAXCOLORS; i++) {
                if (!c.getColors()[i].equals(ColorSet.DEFAULT[i])) {
                    configLines.add("Farbe (" + ColorSet.NAMES[i] + ")");
                }
            }
        }
        
        /* Calculate number of total lines */
        int numConfig = configLines.size();
        int numEvents = e.size();
        int index = 0;
        configBoxes = new JCheckBox[numConfig];

        /* fill config checkboxes */
        if (configLines.size() > 0) {
            pScrollConfig.add(selectAllConfig);
            for (String s : configLines) {
                configBoxes[index] = new JCheckBox(s);
                configBoxes[index].setMargin(new Insets(0, 2, 0, 0));
                selectAllConfig.addToGroup(configBoxes[index]);
                pScrollConfig.add(configBoxes[index++]);
            }
        }
        
        /* fill event table */
        if (numEvents > 0) {
            eventTable = new EventTableWithCheckbox(e);
            selectAllEvents.addItemListener(this);
            
            /* add text field for searching events */
            String defFilterText = "Name, Datum, Uhrzeit oder Kategorie";
            SearchPanel searchPanel = new SearchPanel(defFilterText, "Ereignisse nach Namen, "
                    + "Datum oder weiteren Attributen suchen (ohne Groß-/Kleinschreibung)");
            searchPanel.addSearchListener(eventTable);
            
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.add(selectAllEvents, BorderLayout.WEST);
            headerPanel.add(searchPanel, BorderLayout.EAST);
            
            pScrollEvents.add(headerPanel, BorderLayout.NORTH);
            pScrollEvents.add(new JScrollPane(eventTable), BorderLayout.CENTER);
        }

        /* no data available */
        if (numConfig + numEvents == 0) {
            pScrollEvents.add(new JLabel("Keine Daten zum Portieren vorhanden..."), BorderLayout.CENTER);
        }
        
        // in case of import: add import button
        if (importRadio.isSelected()) {
            JPanel importButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            importButtonPanel.add(importButton);
            pScrollEvents.add(importButtonPanel, BorderLayout.SOUTH);
        }

        pScrollConfig.setBorder(new EtchedBorder());
        pScrollEvents.setBorder(new EtchedBorder());
        if (numConfig > 0) {
            pScroll.add(pScrollConfig, BorderLayout.NORTH);
        }
        pScroll.add(pScrollEvents, BorderLayout.CENTER);
        
        return pScroll;
    }
    
    /**
     * Opens an open-file-dialog for choosing a XML file to import.
     * Parses the XML file and stores events and configuration
     * in possibleEvents and possibleConfig.
     */
    private void openImport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("XML-Datei oder iCal-Datei", "xml", "ical", "ics"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File importFile = chooser.getSelectedFile();
            importFileName = importFile.getPath();
            List<Event> events = new ArrayList<Event>();
            Configuration config = Configuration.DEFAULT_CONFIG.clone();
            LOG.fine("import file: " + importFileName);

            /* new version file */
            if (importFileName.toLowerCase().endsWith(".xml")) {
                XMLParser parser = new XMLParser();
                try {
                    parser.parse(importFile);
                } catch (FileNotFoundException e) {
                    caller.errorOccurred("<html>Die Datei <b>" + importFileName + "</b> kann nicht gelesen werden.",
                            "Fehler beim Lesen...", e);
                    return;
                } catch (Exception e) {
                    caller.errorOccurred("<html>Der Inhalt der Datei <b>" + importFileName
                            + "</b> passt nicht ins Schema!" 
                            + "<br>Der Import kann nicht durchgeführt werden.</html>",
                            "Fehler beim Lesen...", e);
                    return;
                }

                config = parser.getConfig();
                events = parser.getEvents();
            } else if (importFileName.toLowerCase().endsWith(".ical") 
                    || importFileName.toLowerCase().endsWith(".ics")) {
                icalParser = new ICalParser(importFile, caller);
                events = icalParser.getEvents();
            } else {
                caller.errorOccurred("<html>Nur Dateien mit den Endungen <b>.xml</b> oder <b>.ical</b> "
                        + "(bzw. <b>.ics</b>) können importiert werden.</html>", "Keine gültige Datei", null);
            }

            titleLabel.setText(importFileName);
            titleLabel.setToolTipText(importFileName);
            
            body.removeAll();
            body.add(fillTable(events, config));
            hintLabel.setText("<html><body>Nun rechts<br>die Auswahl<br>zum Importieren<br>treffen und dann"
                    + "<br>\"Importieren\"<br>klicken.</body></html>");
        }
    }

    /**
     * Import events and configurations that has been parsed
     * from a external XML file.
     * Properties stored in possibleConfig and possibleEvents.
     * This will call the parent calendar object to apply the
     * new configuration and add new events.
     */
    private void doImport() {
        if (!isSomethingSelected()) {
            JOptionPane.showMessageDialog(this,
                    "Es sind keine Daten zum Importieren ausgewählt.",
                    "Importieren...", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        
        Configuration conf = caller.getConfig().clone();
        
        int index   = 0; // index over all checkboxes
        int confCnt = 0; // counter for imported config items
        int evtCnt  = 0; // counter for successfully imported events
        
        /*
         * Check which config lines differ from the default configuration
         */
        if (possibleConfig != Configuration.DEFAULT_CONFIG) {
            
            // import enum properties
            for (EnumProperty prop : EnumProperty.values()) {
                if (possibleConfig.getProperty(prop) != Configuration.DEFAULT_CONFIG.getProperty(prop)) {
                    if (configBoxes[index++].isSelected()) {
                        conf.setProperty(prop, possibleConfig.getProperty(prop));
                        confCnt++;
                        LOG.fine("import: " + prop.name() + "=" + possibleConfig.getProperty(prop).name());
                    }
                }
            }
            
            // import boolean properties
            for (BoolProperty prop : BoolProperty.values()) {
                if (possibleConfig.getProperty(prop) != Configuration.DEFAULT_CONFIG.getProperty(prop)) {
                    if (configBoxes[index++].isSelected()) {
                        conf.setProperty(prop, possibleConfig.getProperty(prop));
                        confCnt++;
                        LOG.fine("import: " + prop.name() + "=" + possibleConfig.getProperty(prop));
                    }
                }
            }
            
            // import integer properties
            for (IntProperty prop : IntProperty.values()) {
                if (possibleConfig.getProperty(prop) != Configuration.DEFAULT_CONFIG.getProperty(prop)) {
                    if (configBoxes[index++].isSelected()) {
                        conf.setProperty(prop, possibleConfig.getProperty(prop));
                        confCnt++;
                        LOG.fine("import: " + prop.name() + "=" + possibleConfig.getProperty(prop));
                    }
                }
            }
            
            // import theme
            if (possibleConfig.getTheme() != Configuration.DEFAULT_CONFIG.getTheme()) {
                if (configBoxes[index++].isSelected()) {
                    conf.setTheme(possibleConfig.getTheme());
                    confCnt++;
                    LOG.fine("import theme");
                }
            }
            
            // import colors
            for (byte i = 0x00; i < ColorSet.MAXCOLORS; i++) {
                if (!possibleConfig.getColors()[i].equals(ColorSet.DEFAULT[i])) {
                    if (configBoxes[index++].isSelected()) {
                        conf.setColor(possibleConfig.getColors()[i], i);
                        confCnt++;
                        LOG.fine("import color " + ColorSet.NAMES[i]);
                    }
                }
            }

            /*
             * Finally: apply the new settings
             */
            caller.setConfig(conf);
        }

        /*
         * Add new events
         */
        List<Event> selectedEvents = eventTable.getSelectedEvents();
        for (Event ev : selectedEvents) {
            if (icalParser != null) {
                icalParser.writeNotesAndAttachment(ev, caller);
            }
            boolean success = caller.newEvent(ev);
            if (success) {
                evtCnt++;
            }
        }
        
        /*
         * Info message
         */
        String msg = "";
        if (confCnt > 0 && selectedEvents.size() > 0) {
            msg = "Es wurde(n) <b>" + confCnt + " Einstellung(en)</b> und <b>" 
                    + evtCnt + " Ereignis(se)</b> erfolgreich importiert.";
            if (evtCnt < selectedEvents.size()) {
                msg += "<br><br><b>" + (selectedEvents.size() - evtCnt) + " Ereignis(se)</b> konnten aufgrund"
                        + "von Fehlern nicht importiert werden.";
            }
        } else if (confCnt > 0) {
            msg = "Es wurde(n) <b>" + confCnt + " Einstellung(en)</b> erfolgreich importiert.";
        } else {
            msg = "Es wurde(n) <b>" + evtCnt + " Ereignis(se)</b> erfolgreich importiert.";
            if (evtCnt < selectedEvents.size()) {
                msg += "<br><br><b>" + (selectedEvents.size() - evtCnt) + " Ereignis(se)</b> wurde(n)"
                        + " <u>nicht</u> importiert.";
            }
        }
        JOptionPane.showMessageDialog(this, "<html>" + msg + "</html>", "Import", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Opens a save-file-dialog for writing all checked events
     * and configurations.
     */
    private void doExport() {
        if (!isSomethingSelected()) {
            JOptionPane.showMessageDialog(this,
                    "Es sind keine Daten zum Exportieren ausgewählt.",
                    "Exportieren...", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        
        Configuration exportConfig = Configuration.DEFAULT_CONFIG.clone();

        /*
         * Check configurations which differ from the default
         */
        int index = 0;
        if (!possibleConfig.equals(Configuration.DEFAULT_CONFIG)) {
            
            // export enum properties
            for (EnumProperty prop : EnumProperty.values()) {
                if (possibleConfig.getProperty(prop) != Configuration.DEFAULT_CONFIG.getProperty(prop)) {
                    if (configBoxes[index++].isSelected()) {
                        exportConfig.setProperty(prop, possibleConfig.getProperty(prop));
                        LOG.fine("export: " + prop.name() + "=" + possibleConfig.getProperty(prop).name());
                    }
                }
            }
            
            // export boolean properties
            for (BoolProperty prop : BoolProperty.values()) {
                if (possibleConfig.getProperty(prop) != Configuration.DEFAULT_CONFIG.getProperty(prop)) {
                    if (configBoxes[index++].isSelected()) {
                        exportConfig.setProperty(prop, possibleConfig.getProperty(prop));
                        LOG.fine("export: " + prop.name() + "=" + possibleConfig.getProperty(prop));
                    }
                }
            }
            
            // export integer properties
            for (IntProperty prop : IntProperty.values()) {
                if (possibleConfig.getProperty(prop) != Configuration.DEFAULT_CONFIG.getProperty(prop)) {
                    if (configBoxes[index++].isSelected()) {
                        exportConfig.setProperty(prop, possibleConfig.getProperty(prop));
                        LOG.fine("export: " + prop.name() + "=" + possibleConfig.getProperty(prop));
                    }
                }
            }
            
            // export theme
            if (possibleConfig.getTheme() != Configuration.DEFAULT_CONFIG.getTheme()) {
                if (configBoxes[index++].isSelected()) {
                    exportConfig.setTheme(possibleConfig.getTheme());
                    LOG.fine("export theme");
                }
            }

            // export colors
            for (byte i = 0x00; i < ColorSet.MAXCOLORS; i++) {
                if (!possibleConfig.getColors()[i].equals(ColorSet.DEFAULT[i])) {
                    if (configBoxes[index++].isSelected()) {
                        exportConfig.setColor(possibleConfig.getColors()[i], i);
                        LOG.fine("export color " + ColorSet.NAMES[i]);
                    }
                }
            }
            LOG.fine("export index=" + index);

            /*
             * Get selected events
             */
            List<Event> selectedEvents = eventTable.getSelectedEvents();

            /*
             * Start writing checked configuration and checked events
             */
            EventExportHandler.export(caller, selectedEvents, exportConfig);
            exportFunctionCalled = true;
        }
    }
    
    /**
     * Returns whether or not at least one config line or event line is selected.
     * @return whether or not at least one config line or event line is selected.
     */
    private boolean isSomethingSelected() {
        for (JCheckBox c : configBoxes) {
            if (c.isSelected()) {
                return true;
            }
        }
        
        return eventTable.isSomethingSelected();
    }
    
    /**
     * Check if something is selected for export but the export function has not been
     * executed yet.
     * @return true or false.
     */
    public boolean checkSomethingSelectedNotYetExported() {
        return exportRadio.isSelected() && !exportFunctionCalled && isSomethingSelected();
    }

    @Override
    public void actionPerformed(ActionEvent a) {
        
        /*
         * File-chooser button clicked
         */
        if (a.getSource().equals(fileButton)) {
            if (importRadio.isSelected()) {
                openImport();
            } else if (exportRadio.isSelected()) {
                doExport();
            }
            
        /*
         * Import button clicked
         */
        } else if (a.getSource().equals(importButton)) {
            doImport();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent i) {
        if (i.getSource().equals(importRadio) && importRadio.isSelected()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBorder(new EtchedBorder());
            body.removeAll();
            body.add(emptyPanel);
            hintLabel.setText("<html><body>"
                    + "\"Datei...\" klicken,<br>um Datei zum<br>Einlesen zu wählen<br>und dann rechts<br>"
                    + "die Auswahl treffen.<br>Import mit<br>\"Importieren\"<br>abschließen.</body></html>");
            titleLabel.setText(importFileName);
            // reset previously chosen items
            possibleConfig = Configuration.DEFAULT_CONFIG.clone();
            
            // update window
            SwingUtilities.updateComponentTreeUI(body);
            this.invalidate();
            this.revalidate();
        } else if (i.getSource().equals(exportRadio) && exportRadio.isSelected()) {
            body.removeAll();
            body.add(fillTable(caller.getAllUserEvents(), caller.getConfig()));
            hintLabel.setText("<html><body>"
                    + "Rechts die<br>Auswahl treffen<br>und dann \"Datei...\"<br>"
                    + "klicken zum<br>Exportieren.</body></html>");
            titleLabel.setText("Eigene Einstellungen und Ereignisse:");
            
            // update window
            SwingUtilities.updateComponentTreeUI(body);
            this.invalidate();
            this.revalidate();
        } else if (i.getSource().equals(selectAllEvents)) {
            eventTable.selectAll(selectAllEvents.isSelected());
        }
    }
}
