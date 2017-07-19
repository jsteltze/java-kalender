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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.jsteltze.calendar.Event.EventType;
import de.jsteltze.calendar.config.Configuration;
import de.jsteltze.calendar.config.Configuration.BoolProperty;
import de.jsteltze.calendar.config.Configuration.EnumProperty;
import de.jsteltze.calendar.config.Configuration.IntProperty;
import de.jsteltze.calendar.config.enums.RemindOption;
import de.jsteltze.common.Log;
import de.jsteltze.common.Msg;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.xml.XMLHandler;

/**
 * XML parser for the calendar XML file.
 * @author Johannes Steltzer
 *
 */
public class XMLParser {
    
    /** Parsed settings. */
    private Configuration config = Configuration.DEFAULT_CONFIG.clone();
    
    /** Number of days after which an exception date will be ignored. */
    public static final long MAX_EXCEPTION_DAYS_IN_PAST = 30L;
    
    /** Parsed events. */
    private List<Event> events = new ArrayList<Event>();
    
    /** File to parse. */
    private File file;
    
    /** Logger. */
    private static Logger logger = Log.getLogger(XMLParser.class);

    /**
     * Construct a new XML parser.
     * Call .parse to start parsing.
     */
    public XMLParser() { }
    
    /**
     * Parse a XML node with an integer as value. If the content cannot be parsed
     * a error message will be shown and the default value will be returned.
     * @param node - Input node
     * @param defaultValue - Default value (returned in case of errors occurred)
     * @return Parsed integer or default value.
     */
    private int parseInteger(Node node, int defaultValue) {
        try {
            int parsed = Integer.parseInt(node.getTextContent().trim());
            return parsed;
        } catch (NumberFormatException ex) {
            String line;
            try {
                line = XMLHandler.nodeToString(node);
            } catch (TransformerException e) {
                line = node.getNodeName();
            }
            showErrorMessage(line);
            return defaultValue;
        }
    }
    
    /**
     * Parse a XML node with an boolean as value. If the content cannot be parsed
     * FALSE will be returned.
     * @param node - Input node
     * @return Parsed boolean (or FALSE in case of errors).
     */
    private boolean parseBoolean(Node node) {
        boolean parsed = Boolean.parseBoolean(node.getTextContent().trim());
        return parsed;
    }
    
    /**
     * Parse the configuration part of the calendar XML file.
     * @param configNode - Config node
     */
    private void parseConfig(Node configNode) {
        NodeList configNodes = configNode.getChildNodes();
        for (int i = 0; i < configNodes.getLength(); i++) {
            Node node = configNodes.item(i);
            boolean errorOccurred = false;
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            logger.fine("config node: " + node.getNodeValue());
            
            switch (node.getNodeName()) {
            // The following 5 properties are enum properties, identified by their ordinal
            case "DefaultView":
            case "AtClose":
            case "AtClickDay":
            case "AtClickEvent":
            case "Style":
                /* Is enum property? */
                EnumProperty eProp = EnumProperty.getByName(node.getNodeName());
                if (eProp != null) {
                    config.setProperty(eProp, parseInteger(node, config.getProperty(eProp).ordinal()));
                
                } else {
                    // If no enum property name matches this is a unknown line -> error
                    errorOccurred = true;
                }
                break;
            // Remind is a enum property, identified by its short name. So we need a special case here :-( 
            case "Remind":
                config.setProperty(EnumProperty.Remind, RemindOption.getFromShort(node.getTextContent().trim()));
                break;
            case "HolidayID":
            case "SpecialDaysID":
            case "ActionDays1ID":
            case "ActionDays2ID":
            case "FirstDayOfWeek":
                /* Is enum property? */
                IntProperty iProp = IntProperty.getByName(node.getNodeName());
                if (iProp != null) {
                    config.setProperty(iProp, parseInteger(node, config.getProperty(iProp)));
                
                } else {
                    // If no integer property name matches this is a unknown line -> error
                    errorOccurred = true;
                }
                break;
            case "Theme":
                config.setTheme(node.getTextContent().trim());
                break;
            case "Color":
                int index = parseInteger(node, -1);
                if (index != -1) {
                    NamedNodeMap attrs = node.getAttributes();
                    Node rNode = attrs.getNamedItem("r");
                    Node gNode = attrs.getNamedItem("g");
                    Node bNode = attrs.getNamedItem("b");
                    try {
                        int rValue = Integer.parseInt(rNode.getNodeValue());
                        int gValue = Integer.parseInt(gNode.getNodeValue());
                        int bValue = Integer.parseInt(bNode.getNodeValue());
                        config.setColor(new Color(rValue, gValue, bValue), index);
                    } catch (Exception e) {
                        errorOccurred = true;
                    }
                }
                break;
            default:
                /* 
                 * Is boolean property?
                 * Why make a lot of fixed cases here? By going this default way, we easily can define new
                 * boolean properties without having to change this code.
                 */
                BoolProperty bProp = BoolProperty.getByName(node.getNodeName());
                if (bProp != null) {
                    config.setProperty(bProp, parseBoolean(node));
                
                } else {
                    // If no boolean property name matches this is a unknown line -> error
                    errorOccurred = true;
                }
                break;
            }
            
            // Error case
            if (errorOccurred) {
                String line;
                try {
                    line = XMLHandler.nodeToString(node);
                } catch (TransformerException ex) {
                    line = node.getNodeName();
                }
                showErrorMessage(line);
            }
        }
    }
    
    /**
     * Parse all event nodes of the calendar XML file.
     * @param eventsNode - Events node
     */
    private void parseEvents(Node eventsNode) {
        NodeList eventNodes = eventsNode.getChildNodes();
        for (int i = 0; i < eventNodes.getLength(); i++) {
            Node node = eventNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            logger.fine("event node: " + node.getNodeValue());
            
            NamedNodeMap attrs = node.getAttributes();
            Node idNode = attrs.getNamedItem("ID");
            Node dateNode = attrs.getNamedItem("date");
            Node timeNode = attrs.getNamedItem("time");
            Node endDateNode = attrs.getNamedItem("endDate");
            Node freqNode = attrs.getNamedItem("frequency");
            Node excNode = attrs.getNamedItem("exceptions");
            Node weeklyNode = attrs.getNamedItem("weekly");
            Node monthlyNode = attrs.getNamedItem("monthly");
            Node yearlyNode = attrs.getNamedItem("yearly");
            Node remindNode = attrs.getNamedItem("remind");
            Node categoryNode = attrs.getNamedItem("category");
            
            try {
                // Parse name
                String eventName = node.getTextContent().trim();
                
                // Parse date
                Date startDate = new Date(dateNode.getNodeValue());
                Date endDate = null;
                short freq = Frequency.OCCUR_ONCE;
                RemindOption remind = null;
                int id = -1;
                
                // Parse end date
                if (endDateNode != null) {
                    endDate = new Date(endDateNode.getNodeValue());
                }
                
                // Parse time
                if (timeNode != null) {
                    startDate = new Date(dateNode.getNodeValue() + "-" + timeNode.getNodeValue());
                }
                
                // Parse weekly=... (deprecated)
                if (weeklyNode != null) {
                    if (Boolean.parseBoolean(weeklyNode.getNodeValue())) {
                        freq |= Frequency.OCCUR_WEEKLY;
                    }
                }
                
                // Parse monthly=... (deprecated)
                if (monthlyNode != null) {
                    if (Boolean.parseBoolean(monthlyNode.getNodeValue())) {
                        freq |= Frequency.OCCUR_MONTHLY;
                    }
                }
                
                // Parse yearly=... (deprecated)
                if (yearlyNode != null) {
                    if (Boolean.parseBoolean(yearlyNode.getNodeValue())) {
                        freq |= Frequency.OCCUR_YEARLY;
                    }
                }
                
                // Parse frequency
                if (freqNode != null) {
                    freq = Short.parseShort(freqNode.getNodeValue());
                }
                
                // Parse remind
                if (remindNode != null) {
                    remind = RemindOption.getFromShort(remindNode.getNodeValue());
                }
                
                // Parse ID
                if (idNode != null) {
                    id = Integer.parseInt(idNode.getNodeValue());
                }
                
                // Construct the parsed event so far
                Event parsedEvent = new Event(startDate, endDate, eventName, EventType.user, freq, remind, id);
                
                // Parse category
                if (categoryNode != null) {
                    parsedEvent.setCategory(categoryNode.getNodeValue());
                }
                
                // Parse exceptions
                if (freq != Frequency.OCCUR_ONCE && excNode != null) {
                    String excNodeStr = excNode.getNodeValue();
                    String[] excDates = excNodeStr.split(",");
                    for (String excDate : excDates) {
                        Date date = new Date(excDate.trim());
                        if (date.dayDiff(new Date()) >= -MAX_EXCEPTION_DAYS_IN_PAST) {
                            parsedEvent.addExceptionDate(date);
                        } else {
                            logger.fine("skip exception date " + date.print());
                        }
                    }
                }
                
                // Finally: add to list of parsed events
                events.add(parsedEvent);
                
            } catch (Exception e) {
                String line;
                try {
                    line = XMLHandler.nodeToString(node);
                } catch (TransformerException ex) {
                    line = node.getNodeName();
                }
                showErrorMessage(line);
            }
        }
    }

    /**
     * Parses the specified calendar XML file for settings and events. 
     * @param inputFile - XML file to parse
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created which 
     * satisfies the configuration requested.
     * @throws IOException if a DocumentBuilder cannot be created which satisfies the configuration requested.
     * @throws SAXException If any parse errors occur.
     */
    public void parse(File inputFile) throws 
        ParserConfigurationException, SAXException, IOException {
        this.file = inputFile;
        DocumentBuilderFactory dbBuilderFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbBuilderFac.newDocumentBuilder();
        Document doc = dbBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        
        Node configNode = doc.getElementsByTagName("Config").item(0);
        if (configNode != null) {
            parseConfig(configNode);
        }
        
        Node eventsNode = doc.getElementsByTagName("Events").item(0);
        if (eventsNode != null) {
            parseEvents(eventsNode);
        }
    }
    
    /**
     * Shows an error massage that there was a problem with a
     * specific line and that this line will be skipped.
     * @param line - Line that could not be parsed
     */
    private void showErrorMessage(String line) {
        JOptionPane.showMessageDialog(null,
                "<html>Die folgende Zeile aus der Datei <i>" + file.getName() 
                + "</i> passt nicht ins Schema.<br><p style=\"font-family:monospace; background-color:white; "
                + "border-width:1px; border-style:solid; border-color:gray; padding:2.5em\">"
                + Msg.replaceSpecialCharacters(line).replaceAll("\\n", "<br>") + "</p><br>"
                + "Die Zeile wird ignoriert.</html>",
                "Fehler beim Parsen...", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Returns all events parsed. 
     * @return List of events parsed.
     */
    public List<Event> getEvents() {
        return this.events;
    }

    /**
     * Returns the configuration parsed.
     * @return Configuration parsed.
     */
    public Configuration getConfig() {
        return this.config;
    }
}
