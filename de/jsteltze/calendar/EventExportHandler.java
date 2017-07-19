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

package de.jsteltze.calendar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.DatatypeConverter;

import de.jsteltze.calendar.UI.GUIUtils;
import de.jsteltze.calendar.config.Configuration;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.calendar.frames.CalendarFrame;
import de.jsteltze.common.Log;
import de.jsteltze.common.calendar.Date;

/**
 * Export of an event as file (XML or ICAL).
 * @author Johannes Steltzer
 *
 */
public final class EventExportHandler {
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(EventExportHandler.class);
    
    /** Default email address for ical files. */
    private static String email = "anonymous@java-kalender.sf.net";
    
    /**
     * Hidden constructor.
     */
    private EventExportHandler() { }
    
    /**
     * Show a save file dialog to export a list of events and (optionally) a config as XML or ICAL.
     * @param parent - Parent calendar object
     * @param events - List of event to export
     * @param config - Configuration to export (can be null or defaul config).
     */
    public static void export(Calendar parent, List<Event> events, Configuration config) {
        // prevent the notification windows from overlapping this dialog
        parent.setNotisAlwaysOnTop(false);
        CalendarFrame guiParent = parent.getGUI().getFrame();
        String suggestedFileName = events.size() == 1 ? events.get(0).getName() : "events";
        
        // allow xml or iCal as destination format
        FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("XML-Format (*.xml)", new String[] {"xml"});
        FileNameExtensionFilter icsFilter = new FileNameExtensionFilter("iCalendar-Format (*.ics; *.ical)", 
                new String[] {"ics", "ical"});
        File dstFile = GUIUtils.saveFileDialog("Daten exportieren...", 
                new FileNameExtensionFilter[] {xmlFilter, icsFilter}, 
                new File(parent.getWorkspace()), suggestedFileName);
        
        try {
            if (dstFile != null) {
                String fileName = dstFile.getName().toLowerCase();
                boolean haveICAL = false;
                LOG.info("export to file: " + fileName);
                if (fileName.endsWith(".xml")) {
                    parent.save(events, config, dstFile.getAbsolutePath());
                } else if (fileName.endsWith(".ics") || fileName.endsWith(".ical")) {
                    exportIcal(events, parent, dstFile);
                    haveICAL = true;
                } else {
                    LOG.warning("invalid file name: " + dstFile.getName());
                }
                
                if (dstFile.exists()) {
                    String freqHint = "";
                    String attachmentHint = "";
                    String configHint = "";
                    
                    // check for unsupported frequencies that cannot be converted to iCal
                    if (haveICAL) {
                        for (Event event : events) {
                            if (Frequency.isByEndOfMonth(event.getFrequency())) {
                                freqHint = "<br><br><b>Hinweis:</b> Ereignisse mit der monatlichen Regelmäßigkeit "
                                        + "<i>x Tage vor Ende des Monats</i><br>"
                                        + "können nicht ins iCal-Format abgebildet werden.<br><br>"
                                        + "Das Ereignis wurde als einmaliges Ereignis gespeichert.";
                                break;
                            }
                        }
                    }
                    
                    // check if config settings shall be exported to iCal
                    if (haveICAL && config != null && !config.equals(Configuration.DEFAULT_CONFIG)) {
                        configHint = "<br><br><b>Hinweis:</b> Kalender-Einstellungen können nicht in das iCal-Format"
                                + "exportiert werden!<br>."
                                + "Die Einstellungen wurden übersprungen.";
                    }
                    
                    // check if events have notes/attachments -> cannot be exported to xml
                    if (!haveICAL) {
                        for (Event event : events) {
                            if (event.getAttachment(parent.getWorkspace()) != null 
                                    || !event.getNotes(parent.getWorkspace()).isEmpty()) {
                                attachmentHint = "<br><br><b>Hinweis:</b> Anhänge oder Notizen von Ereignissen können "
                                        + "beim XML-Export nicht mit exportiert werden!<br>"
                                        + "Diese Daten befinden sich im Ordner <i>" + Const.EVENT_DIR + "</i><br>"
                                        + "und müssen manuell mit übertragen werden.";
                                break;
                            }
                        }
                    }
                    
                    // success message (with possible hints)
                    JOptionPane.showMessageDialog(guiParent, "<html>Die Datei <b>" + dstFile.getName() + "</b>"
                            + " wurde erfolgreich erstellt." + freqHint + attachmentHint + configHint + "</html>", 
                            "Export erfolgreich", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // error message (unspecified)
                    LOG.log(Level.WARNING, "this should not happen...");
                    GUIUtils.showErrorMessage("<html>Die Datei <b>" + dstFile.getName() 
                            + "</b> konnt nicht erstellt werden!", "Export fehlgeschlagen", null);
                }
            }
        } catch (Exception e) {
            // error message with concrete exception
            LOG.log(Level.WARNING, "cannot export event...", e);
            parent.errorOccurred("<html>Die Datei <b>" + dstFile.getName() + "</b>"
                    + " kann nicht erstellt werden.</html>", "Export fehlgeschlagen", e);
            
            // in case of partially written content: delete file
            dstFile.delete();
        }
        parent.setNotisAlwaysOnTop(true);
    }

    /**
     * Show a save file dialog to export an single event as XML or ICAL.
     * @param parent - Parent calendar object
     * @param event - Event to export
     */
    public static void export(Calendar parent, Event event) {
        export(parent, Arrays.asList(event), null);
    }
    
    /**
     * Ask the user to type an email address to be used in the ical file.
     * @param parent - Parent gui object
     * @return email address typed or null.
     */
    private static String askEmailAddress(CalendarFrame parent) {
        String input = JOptionPane.showInputDialog(parent, "Im folgenden Textfeld kann eine Email-Adresse "
                + "angegeben werden.\nDie Email dient als Kontakt zum Organisator des Ereignisses "
                + "(z.B. bei Import der iCal-Datei in ein Email-Programm).", email);
        if (input == null || input.isEmpty()) {
            return "";
        } else {
            return input;
        }
    }
    
    /**
     * Export a list of events as iCal.
     * @param events - Events to export
     * @param parent - Parent calendar object
     * @param dst - Destination iCal file to write/create
     * @throws IOException in case of any IO error
     */
    private static void exportIcal(List<Event> events, Calendar parent, File dst) throws IOException {
        email = askEmailAddress(parent.getGUI().getFrame());
        
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(dst), Const.ENCODING));
        out.write("BEGIN:VCALENDAR\n"
            + "VERSION:2.0\n"
            + "METHOD:PUBLISH\n");
        for (Event event : events) {
            exportIcal(out, event, event.getNotes(parent.getWorkspace()), 
                    event.getAttachment(parent.getWorkspace()));
        }
        
        out.write("END:VCALENDAR\n");
        out.close();
    }
    
    /**
     * Append a single event iCalendar (ICS) encoded to a writer.
     * @param writer - BufferedWriter to append this events iCal converted. THE WRITER MUST
     * BE OPENED ALREADY AND WILL NOT BE CLOSED HERE!
     * @param event - Event to write
     * @param notes - This events notes (or empty string or null)
     * @param attachment - Attachment file for this event (or null)
     * @throws IOException if an error occurred during writing
     */
    private static void exportIcal(BufferedWriter writer, Event event, String notes, File attachment) 
            throws IOException {
        final int base64LineLength = 64;
        
        Date icsEndDate = null;
        if (event.getEndDate() == null) {
            icsEndDate = event.getDate().clone();
            icsEndDate.add(java.util.Calendar.DAY_OF_MONTH, 1);
            icsEndDate.setHasTime(false);
        } else {
            icsEndDate = event.getEndDate().clone();
            icsEndDate.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }
        
        writer.write("BEGIN:VEVENT\n"
                + "UID:" + event.getID() + "@java-kalender.sf.net\n");
        if (email != null && !email.isEmpty()) {
            writer.write("ORGANIZER:MAILTO:" + email + "\n");
        }
        writer.write("SUMMARY:" + event.getName() + "\n"
                + "DESCRIPTION:" + notes.replaceAll("\n", "\\\\n") + "\n"
                + "CLASS:PUBLIC\n");
        if (attachment != null) {
            String b64 = null;
            try {
                byte[] bytes = Files.readAllBytes(attachment.toPath());
                b64 = DatatypeConverter.printBase64Binary(bytes);
                writer.write("ATTACH;ENCODING=BASE64;VALUE=BINARY;X-FILENAME="
                        + attachment.getName() + ":");
                int len;
                do {
                    len = b64.length();
                    writer.write("\n\t" + b64.substring(0, len > base64LineLength ? base64LineLength : len));
                    b64 = b64.substring(len > base64LineLength ? base64LineLength : len);
                } while (len > base64LineLength);
                writer.write("\n");
            } catch (IOException e) {
                LOG.severe("cannot export attachment: " + e.toString());
            }
        }
        writer.write("DTSTART" + event.getDate().getICSFormat() + "\n"
                + "DTEND" + icsEndDate.getICSFormat() + "\n" 
                + "DTSTAMP" + new Date().getICSFormat() + "\n"
                + (event.getFrequency() != Frequency.OCCUR_ONCE 
                        ? Frequency.getIcal(event.getFrequency(), event.getDate()) : "")
                + "\nEND:VEVENT\n");
    }
}
