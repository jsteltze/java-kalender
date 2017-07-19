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

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import de.jsteltze.calendar.config.Const;
import de.jsteltze.common.Log;
import de.jsteltze.common.calendar.Date;

/**
 * Import and parse an iCAL file for the conversion of the VEVENT elements as calendar events.
 * @author Johannes Steltzer
 *
 */
public class ICalParser {
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(ICalParser.class);
   
    /** Event parsed from the ical file. */
    private List<Event> parsedEvents = new ArrayList<Event>();
    
    /** List of notes (=DESCRIPTION) belonging to a parsed event. */
    private Map<Event, String> notes = new HashMap<Event, String>();
    
    /** List of attachments (=ATTACH) belonging to a parsed event. */
    private Map<Event, String> attachments = new HashMap<Event, String>();
    
    /** List of error messages. */
    private List<String> errorMessages = new ArrayList<String>();
    
    /**
     * Start parsing an iCal file.
     * @param icalFile - iCal file to parse
     * @param parent - Parent calendar object
     */
    public ICalParser(File icalFile, Calendar parent) {
        LOG.fine("start parsing ical file: " + icalFile.getName());
        try {
            StringBuffer buf = new StringBuffer();
            Scanner scanner = new Scanner(icalFile, Const.ENCODING);
            boolean append = false;
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("BEGIN:VEVENT")) {
                    append = true;
                }
                
                if (append) {
                    buf.append(line + "\n");
                }
                
                if (line.equals("END:VEVENT")) {
                    append = false;
                    parseVEVENT(buf);
                    buf.delete(0, buf.length());
                }
            }
            scanner.close();
            
            if (!errorMessages.isEmpty()) {
                String msg = "<html>Einige Informationen aus der iCal-Datei konnten nicht (vollständig)<br>" 
                        + "verarbeitet werden:<br><p style=\"font-family:monospace; background-color:white; "
                        + "border-width:1px; border-style:solid; border-color:gray; padding:2.5em\">";
                for (String line : errorMessages) {
                    msg += line.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\\n", "<br>");
                }
                msg += "</p></html>";
                parent.errorOccurred(msg, "Fehler beim Einlesen", null);
            }
            
        } catch (Exception e) {
            LOG.log(Level.WARNING, "cannot parse ical file...", e);
            parent.errorOccurred("<html>Die Datei <b>" + icalFile.getName() 
                    + "</b> kann nicht importiert werden!<br></html>", "Import fehlgeschlagen", e);
        }
    }
    
    /**
     * Parse a single VEVENT section from an ICAL file.
     * @param buf - String buffer containing the VEVENT (from BEGIN to END)
     */
    private void parseVEVENT(StringBuffer buf) {
        String summary = null, dtStart = null, dtEnd = null, rrule = null;
        String thisNotes = null, thisAttachment = null;
        short frequency = Frequency.OCCUR_ONCE;
        try {
            StringTokenizer tok = new StringTokenizer(buf.toString(), "\n");
            
            while (tok.hasMoreTokens()) {
                String line = tok.nextToken();
                if (line.startsWith("SUMMARY")) {
                    summary = line.substring(line.indexOf(':') + 1);
                } else if (line.startsWith("DESCRIPTION:")) {
                    thisNotes = line.substring(line.indexOf(':') + 1).replaceAll("\\\\n", "\n");
                } else if (line.startsWith("DTSTART")) {
                    dtStart = line.substring(8);
                } else if (line.startsWith("DTEND")) {
                    dtEnd = line.substring(6);
                } else if (line.startsWith("RRULE")) {
                    rrule = line;
                } else if (line.startsWith("ATTACH")) {
                    thisAttachment = buf.toString();
                }
            }
            
            if (summary == null) {
                errorMessages.add("VEVENT enthält kein SUMMARY. Wird übersprungen.");
                return;
            } else if (dtStart == null) {
                errorMessages.add("VEVENT '" + summary + "' enthält kein DTSTART. Wird übersprungen.");
                return;
            }
            
            Date startDate = new Date(dtStart);
            Date endDate = dtEnd == null ? null : new Date(dtEnd);
            if (endDate != null) {
                long dayDiff = endDate.dayDiff(startDate);
                if (dayDiff <= 1) {
                    endDate = null;
                } else {
                    endDate.add(java.util.Calendar.DAY_OF_MONTH, -1);
                }
            }
            
            try {
                frequency = parseRRule(rrule, startDate);
            } catch (ParseException pe) {
                errorMessages.add("RRULE von VEVENT '" + summary + "' kann nicht geparst/konvertiert werden: '" 
                        + rrule + "'. Das Ereignis erhält keine Regelmäßigkeit.");
            }
            
            Event parsedEvent = new Event(startDate, summary, frequency, -1);
            parsedEvent.setEndDate(endDate);
            notes.put(parsedEvent, thisNotes);
            attachments.put(parsedEvent, thisAttachment);
            parsedEvents.add(parsedEvent);
        } catch (Exception e) {
            errorMessages.add("Parsing-Fehler bei VEVENT" + (summary == null ? "" : (" '" + summary + "'")) 
                    + ": " + e.getLocalizedMessage() + ". Wird übersprungen.");
        }
    }
    
    /**
     * Returns the list of parsed events.
     * @return the list of parsed events.
     */
    public List<Event> getEvents() {
        return this.parsedEvents;
    }
    
    /**
     * Parse the ATTACH element of a single VEVENT, decode the base64 content
     * and write the decoded bytes as file attachment to the event.
     * @param attach - Complete VEVENT block that contains the ATTACH block
     * @param event - Event which attachment shall be written
     * @param caller - Parent calendar object (needed for the workspace)
     */
    private void writeAttachment(String attach, Event event, Calendar caller) {
        File attachment = null;
        
        try {
            StringTokenizer tok = new StringTokenizer(attach, "\n");
            while (tok.hasMoreTokens()) {
                String line = tok.nextToken();
                if (line.startsWith("ATTACH")) {
                    if (!line.contains("ENCODING=BASE64")) {
                        throw new IOException("nicht gefunden: 'ENCODING=BASE64'");
                    }
                    int start = line.indexOf("X-FILENAME=");
                    if (start == -1) {
                        throw new IOException("nicht gefunden: 'X-FILENAME=...'");
                    }
                    start += "X-FILENAME=".length();
                    int end = line.indexOf(':', start);
                    String fileName = line.substring(start, end);
                    
                    // Remove " or ' from the file name
                    if (fileName.startsWith("\"") || fileName.startsWith("'")) {
                        fileName = fileName.substring(1);
                    }
                    if (fileName.endsWith("\"") || fileName.endsWith("'")) {
                        fileName = fileName.substring(0, fileName.length() - 1);
                    }
                    
                    LOG.fine("attachment file name: " + fileName);
                    attachment = new File(caller.getPath(Const.EVENT_DIR) 
                            + File.separator + event.getID() + File.separator + fileName);
                    
                    decodeBase64(attachment, tok, line.substring(++end));
                    break;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "cannot parse and write attachment...", e);
            if (attachment != null) {
                attachment.delete();
            }
            
            caller.errorOccurred("<html>Die angehangene Datei kann nicht importiert werden!"
                    + "<br>Nur Anhänge in spezieller iCal-Syntax können decodiert werden.</html>", 
                    "Anhang kann nicht importiert werden", e);
        }
    }
    
    /**
     * Read the base64 encoded attachment and write the decoded bytes to a file.
     * @param output - File for writing. Decoded bytes will be written to this file. 
     * The file will be created. The file will be deleted, if it already exists!
     * @param sc - Scanner to read the base64 code from. The scanner must be initialized
     * already and will be used from the current position.
     * @param initial - Initial base64 code (e.g. before the scanner position)
     * @throws Exception if any error occurs
     */
    private void decodeBase64(File output, StringTokenizer sc, String initial) throws Exception {
        // Put all base64 lines in this buffer
        StringBuffer base64Buffer = new StringBuffer(initial);
        String line;
        FileOutputStream fos = null;
        
        try {
            output.delete();
            output.createNewFile();
            fos = new FileOutputStream(output);
            
            while (sc.hasMoreTokens()) {
                line = sc.nextToken();
                line = line.replaceAll("    ", "\t");
                if (!line.startsWith("\t")) {
                    break;
                }
                line = line.substring(1);
                base64Buffer.append(line);
                
                // Decode the base64 buffer in multiples of 4
                while (base64Buffer.length() > 3) {
                    byte[] decoded = DatatypeConverter.parseBase64Binary(base64Buffer.substring(0, 4));
                    base64Buffer.delete(0, 4);
                    fos.write(decoded);
                }
            }
            
            // If there is a rest (<4) in the buffer, finally decode it
            fos.write(DatatypeConverter.parseBase64Binary(base64Buffer.toString()));
            
        } catch (Exception e) {
            throw e;
        } finally {
            closeQuiet(fos);
        }
    }
    
    /**
     * Close a closeable without throwing exceptions.
     * @param x - Closeable to close
     */
    private void closeQuiet(Closeable x) {
        if (x != null) {
            try {
                x.close();
            } catch (Exception ex) {
                LOG.warning(x.getClass().getName() + " cannot be closed: " + ex.getLocalizedMessage());
            }
        }
    }

    /**
     * Write the notes (=DESCRIPTION) and the attached file (=ATTACH) if
     * exist.
     * @param event - Event which notes/attachment shall be written
     * @param caller - Parent calendar object (needed for workspace)
     */
    public void writeNotesAndAttachment(Event event, Calendar caller) {
        if (event.getID() == -1) {
            event.setID(caller.genID());
        }
        
        String thisNotes = notes.get(event);
        if (thisNotes != null && !thisNotes.isEmpty()) {
            event.writeNotes(thisNotes, caller);
        }
        
        String thisAttachment = attachments.get(event);
        if (thisAttachment != null && !thisAttachment.isEmpty()) {
            writeAttachment(thisAttachment, event, caller);
        }
    }
    
    /**
     * Parse the RRULE line and convert it to a frequency this program can process.
     * @param rruleLine - RRULE line of a single VEVENT
     * @param startDate - Start date of this VEVENT (might be relevant for some frequencies)
     * @return the frequency parsed
     * @throws ParseException if the RRULE line cannot be parsed/converted
     */
    private short parseRRule(String rruleLine, Date startDate) throws ParseException {
        short freq = Frequency.OCCUR_ONCE;
        
        // no RRULE string -> no frequency
        if (rruleLine == null || rruleLine.isEmpty()) {
            return Frequency.OCCUR_ONCE;
        
        // weekly, monthly or yearly RRULE
        } else if (rruleLine.contains("FREQ=WEEKLY;")) {
            freq = Frequency.OCCUR_WEEKLY | Frequency.OCCUR_MONTHLY | Frequency.OCCUR_YEARLY; 
        } else if (rruleLine.contains("FREQ=MONTHLY;")) {
            freq = Frequency.OCCUR_MONTHLY;
        } else if (rruleLine.contains("FREQ=YEARLY;")) {
            freq = Frequency.OCCUR_YEARLY; 
        }     
        
        if (freq == Frequency.OCCUR_MONTHLY) {
            Pattern pattern = Pattern.compile("BYSETPOS=-?\\d+");
            Matcher matcher = pattern.matcher(rruleLine);
            if (matcher.find()) {
                String weekIndexString = rruleLine.substring(matcher.start() + "BYSETPOS=".length(), matcher.end());
                int weekIndex = Integer.parseInt(weekIndexString);
                if (weekIndex == -1) {
                    weekIndex = 0;
                }
                return Frequency.genByWeekday(startDate.get(java.util.Calendar.DAY_OF_WEEK), weekIndex);
            } else {
                return Frequency.OCCUR_MONTHLY | Frequency.OCCUR_YEARLY;
            }
        }
        
        // frequency by some interval
        if (rruleLine.contains("INTERVAL=")) {
            Pattern pattern = Pattern.compile("INTERVAL=\\d+");
            Matcher matcher = pattern.matcher(rruleLine);
            if (matcher.find()) {
                String intervalString = rruleLine.substring(matcher.start() + "INTERVAL=".length(), matcher.end());
                int interval = Integer.parseInt(intervalString);
                int unitCode = 0;
                if (rruleLine.contains("FREQ=DAILY;")) {
                    unitCode = 0;
                } else if (rruleLine.contains("FREQ=WEEKLY;")) {
                    unitCode = 1;
                } else if (rruleLine.contains("FREQ=MONTHLY;")) {
                    unitCode = 2;
                } else if (rruleLine.contains("FREQ=YEARLY;")) {
                    unitCode = 3;
                } else {
                    //ERROR
                    throw new ParseException(null, 0);
                }
                return Frequency.genByInterval(interval, unitCode); 
            }
            // ERROR 
            throw new ParseException(null, 0);
        }
        
        if (freq == Frequency.OCCUR_ONCE) {
            // this should not happen: content in the RRULE but no frequency parsed
            throw new ParseException(null, 0);
        }
        
        return freq;
    }
}
