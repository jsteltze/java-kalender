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

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuListener;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.Event.EventType;
import de.jsteltze.calendar.EventExportHandler;
import de.jsteltze.calendar.Frequency;
import de.jsteltze.calendar.frames.EditEvent;
import de.jsteltze.calendar.frames.NotesEditor;
import de.jsteltze.calendar.frames.Notification;
import de.jsteltze.calendar.frames.TableOfEventsSingleDay;
import de.jsteltze.common.Log;
import de.jsteltze.common.Msg;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.Date.PrintFormat;

/**
 * Popup menu for right click of a selected event or date.
 * @author Johannes Steltzer
 *
 */
public class CalendarPopupMenu 
    extends JPopupMenu
    implements ActionListener {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Popup menu items. */
    private JMenuItem newItem = new JMenuItem("Neues Ereignis", 
                    new ImageIcon(CalendarPopupMenu.class.getResource("/media/event_new20.png"))), 
            overviewItem = new JMenuItem("Übersicht öffnen", 
                    new ImageIcon(new ImageIcon(CalendarPopupMenu.class.getResource("/media/calendar_new64.png"))
                            .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))), 
            editItem = new JMenuItem("Bearbeiten", 
                    new ImageIcon(CalendarPopupMenu.class.getResource("/media/event_edit20.png"))), 
            deleteItem = new JMenuItem("Löschen", 
                    new ImageIcon(CalendarPopupMenu.class.getResource("/media/event_delete20.png"))), 
            deleteSingleItem = new JMenuItem("nur dieses Datum", 
                    new ImageIcon(CalendarPopupMenu.class.getResource("/media/event_delete_single20.png"))), 
            deleteAllItem = new JMenuItem("komplette Serie", 
                    new ImageIcon(CalendarPopupMenu.class.getResource("/media/event_delete_all20.png"))), 
            remindItem = new JMenuItem("Erinnerung aufrufen", 
                    new ImageIcon(new ImageIcon(CalendarPopupMenu.class.getResource("/media/clock_alarm32.png"))
                            .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))), 
            showAttachmentItem = new JMenuItem("Anhang öffnen", 
                    new ImageIcon(CalendarPopupMenu.class.getResource("/media/attachment20.png"))),
            showNotesItem = new JMenuItem("Notizen anzeigen", 
                    new ImageIcon(CalendarPopupMenu.class.getResource("/media/notes20.png"))),
            copyItem = new JMenuItem("Kopieren", 
                    new ImageIcon(CalendarPopupMenu.class.getResource("/media/event_copy20.png"))),
            pasteItem = new JMenuItem("Einfügen", 
                    new ImageIcon(CalendarPopupMenu.class.getResource("/media/event_paste20.png"))), 
            cutItem = new JMenuItem("Ausschneiden", 
                    new ImageIcon(CalendarPopupMenu.class.getResource("/media/event_cut20.png"))),
            exportItem = new JMenuItem("Exportieren", 
                    new ImageIcon(CalendarPopupMenu.class.getResource("/media/event_export20.png")));
    
    /** In case this popup is for an event: selected event. */
    private Event selectedEvent = null;
    
    /** In case this popup is for a date: selected date. */
    private Date selectedDate = null;
    
    /** 
     * For the copy/cut/paste process: cutted or copied event.
     * Objects are static because they can only exist once over all popups.
     */
    private static Event copiedEvent = null, cuttedEvent = null;
    
    /** Parent calendar object. */
    private Calendar calendar;
    
    /** GUI component on which the popup menu was opened. */
    private Component invoker;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(CalendarPopupMenu.class);
    
    /**
     * Launch a new popup menu over a selected event.
     * @param selectedEvent - Selected event (subject of this popup)
     * @param selectedDate - Selected date (where the event takes place)
     * @param parent - Parent calendar object
     * @param location - Location where to place the popup menu
     * @param invoker - GUI object to place the popup menu on (calling object)
     * @param allowCopyPaste - Add cut/copy/paste menu items?
     */
    public CalendarPopupMenu(Event selectedEvent, Date selectedDate, Calendar parent, Point location, 
            Component invoker, boolean allowCopyPaste) {
        super(selectedEvent.getName());
        this.selectedEvent = selectedEvent;
        this.selectedDate = selectedDate;
        this.calendar = parent;
        this.invoker = invoker;
        
        // add action listeners
        addListeners();

        if (selectedEvent.getEndDate() != null) {
            copyItem.setEnabled(false);
            cutItem.setEnabled(false);
        }
        
        if (selectedEvent.getType() != EventType.user) {
            editItem.setEnabled(false);
            deleteItem.setText(selectedEvent.getType().isHoliday() ? "Feiertage einstellen" : "Einstellungen öffnen");
            copyItem.setEnabled(false);
            cutItem.setEnabled(false);
        }
        
        if (cuttedEvent != null) {
            copyItem.setEnabled(false);
            cutItem.setEnabled(false);
        }
        
        // add gui items
        addItems(allowCopyPaste);

        if (location != null) {
            setLocation(location);
        }
        if (invoker != null) {
            if (invoker instanceof PopupMenuListener) {
                addPopupMenuListener((PopupMenuListener) invoker);
            }
            setInvoker(invoker);
        }
    }
    
    /**
     * Add all items (title, menu items) to the event popup menu.
     * @param allowCopyPaste - Add cut/copy/paste menu items?
     */
    private void addItems(boolean allowCopyPaste) {
        String type = selectedEvent.getType().isHoliday() ? "Feiertags" : "Ereignis";
        JLabel title = new JLabel("<html><h4>" + type + "-Optionen:</h4>- " + selectedEvent.getName() + " -</html>");
        title.setForeground(Color.gray);
        title.setBorder(null);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);
        addSeparator();
        add(editItem);
        if (allowCopyPaste) {
            add(copyItem);
            add(cutItem);
        }
        
        if (selectedEvent.getType().equals(EventType.user) && selectedEvent.getFrequency() != Frequency.OCCUR_ONCE) {
            JMenu deleteMenu = new JMenu("Löschen");
            deleteMenu.setIcon(new ImageIcon(CalendarPopupMenu.class.getResource("/media/event_delete20.png")));
            deleteMenu.add(deleteSingleItem);
            deleteMenu.add(deleteAllItem);
            add(deleteMenu);
        } else {
            add(deleteItem);
        }
        
        add(exportItem);
        addSeparator();
        add(remindItem);
        if (selectedEvent.getAttachment(calendar.getWorkspace()) != null) {
            add(showAttachmentItem);
        }
        if (!selectedEvent.getNotes(calendar.getWorkspace()).equals("")) {
            add(showNotesItem);
        }
    }
    
    /**
     * Launch a new popup menu over a selected date. Dont miss to specify the location
     * outside of this constructor.
     * @param date - Selected date (subject of this popup)
     * @param parent - Parent calendar object
     * @param location - Location where to place the popup menu
     * @param invoker - GUI object to place the popup menu on (calling object)
     */
    public CalendarPopupMenu(Date date, Calendar parent, Point location, CalendarCanvas invoker) {
        super(date.print(PrintFormat.D_MMM_YYYY));
        this.selectedDate = date;
        this.calendar = parent;
        
        // add action listener
        addListeners();
        
        if (copiedEvent == null && cuttedEvent == null) {
            pasteItem.setEnabled(false);
        }

        JLabel title = new JLabel("<html><h4>Datums-Optionen:</h4>- " + date.print(PrintFormat.D_MMM_YYYY) 
                + (date.hasTime() ? " (" + date.print(PrintFormat.Hmm_Uhr) + ")" : "") + " -</html>");
        title.setForeground(Color.gray);
        title.setBorder(null);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);
        addSeparator();
        add(newItem);
        add(pasteItem);
        addSeparator();
        add(overviewItem);
        
        addPopupMenuListener(invoker);
        setLocation(location);
        setInvoker(invoker);
        setVisible(true);
    }
    
    /**
     * Add action listeners to all menu items.
     */
    private void addListeners() {
        editItem.addActionListener(this);
        deleteItem.addActionListener(this);
        deleteSingleItem.addActionListener(this);
        deleteAllItem.addActionListener(this);
        copyItem.addActionListener(this);
        cutItem.addActionListener(this);
        remindItem.addActionListener(this);
        exportItem.addActionListener(this);
        newItem.addActionListener(this);
        overviewItem.addActionListener(this);
        pasteItem.addActionListener(this);
        showAttachmentItem.addActionListener(this);
        showNotesItem.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(newItem)) {
            new EditEvent(calendar, selectedDate);
        } else if (e.getSource().equals(overviewItem)) {
            new TableOfEventsSingleDay(selectedDate, calendar, false);
        } else if (e.getSource().equals(editItem)) {
            new EditEvent(calendar, selectedEvent, false, null);
        } else if (e.getSource().equals(deleteItem) || e.getSource().equals(deleteAllItem)) {
            calendar.deleteRequested(selectedEvent);
        } else if (e.getSource().equals(deleteSingleItem)) {
            calendar.addException(selectedEvent, selectedDate);
        } else if (e.getSource().equals(remindItem)) {
            new Notification(calendar, selectedEvent);
        } else if (e.getSource().equals(exportItem)) {
            EventExportHandler.export(calendar, selectedEvent);
        } else if (e.getSource().equals(showNotesItem)) {
            new NotesEditor(calendar, selectedEvent);
        } else if (e.getSource().equals(showAttachmentItem)) {
            try {
                Desktop.getDesktop().open(selectedEvent.getAttachment(calendar.getWorkspace()));
            } catch (IOException e1) {
                LOG.log(Level.SEVERE, "cannot open attachment...", e1);
            }
        } else if (e.getSource().equals(copyItem)) {
            copiedEvent = selectedEvent.clone();
            calendar.getGUI().putMessage(Msg.getMessage("guiMessageEventClipboard", 
                    new String[] {selectedEvent.getName()}));
        } else if (e.getSource().equals(pasteItem)) {
             copyPaste();
        } else if (e.getSource().equals(cutItem)) {
            selectedEvent.getGUI().setTransparent(true);
            cuttedEvent = selectedEvent.clone();
        }
        
        // Finally: check if special handling is necessary, depending on what the invoker was
        if (invoker != null && invoker instanceof TableOfEventsSingleDay) {
            TableOfEventsSingleDay dialog = (TableOfEventsSingleDay) invoker;
            dialog.setVisible(false);
            dialog.dispose();
        }
    }
    
    /**
     * Handle pasting of a copied or cutted event.
     */
    private void copyPaste() {
        /* paste cutted event */
        if (cuttedEvent != null) {
            cuttedEvent.getDate().set(java.util.Calendar.YEAR, selectedDate.get(java.util.Calendar.YEAR));
            cuttedEvent.getDate().set(java.util.Calendar.MONTH, selectedDate.get(java.util.Calendar.MONTH));
            cuttedEvent.getDate().set(java.util.Calendar.DAY_OF_MONTH, 
                    selectedDate.get(java.util.Calendar.DAY_OF_MONTH));
            calendar.editEvent(cuttedEvent.getID(), cuttedEvent);
            cuttedEvent = null;
            copiedEvent = null;
        
        /* paste copied event */
        } else if (copiedEvent != null) {
            Event tmpEvent = copiedEvent.clone();
            copiedEvent.getDate().set(java.util.Calendar.YEAR, selectedDate.get(java.util.Calendar.YEAR));
            copiedEvent.getDate().set(java.util.Calendar.MONTH, selectedDate.get(java.util.Calendar.MONTH));
            copiedEvent.getDate().set(java.util.Calendar.DAY_OF_MONTH, 
                    selectedDate.get(java.util.Calendar.DAY_OF_MONTH));
            calendar.copyEvent(copiedEvent);
            copiedEvent = tmpEvent;
        }
    }
}

