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

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.EventCategories;
import de.jsteltze.calendar.Frequency;
import de.jsteltze.common.Log;
import de.jsteltze.common.calendar.DateComparator;
import de.jsteltze.common.calendar.Date.PrintFormat;
import de.jsteltze.common.ui.SearchPanelListener;

/**
 * Graphical table of calendar events.
 * @author Johannes Steltzer
 *
 */
public class EventTable 
    extends JTable
    implements SearchPanelListener {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Maximum number of events to be displayed without scroll bar. */
    private static final int MAX_EVENTS_IN_FRAME = 25;
    
    /** Default size of the JTable. */
    private static final Dimension DEFAULT_TABLE_SIZE = new Dimension(500, 400);
    
    /** Index of the table headers (or -1 if column is not present). */
    private static int colID = 0, colName = 1, colDate = 2, colTime = 3, colFreq = 4, colCat = 5,
            colHolidayType = -1, colCheckbox = -1;
    
    /** Table model. */
    private DefaultTableModel tableModel;
    
    /** Events to display. */
    private List<Event> events,
            filteredEvents = new ArrayList<Event>();
    
    /** The desired table columns. */
    private List<Columns> columns;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(EventTable.class);
    
    /** Possible table columns. */
    public enum Columns {
        /** Column: event id. */
        id("ID", 0, Integer.class),
        /** Column: event name. */
        name("Name", 220, String.class),
        /** Column: event date (=start date). */
        date("Datum", 180, String.class),
        /** Column: event frequency. */
        freq("Regelm‰ﬂigkeit", 150, String.class),
        /** Column: event time. */
        time("Uhrzeit", 80, String.class),
        /** Column: type of holiday (only applicable if the event is a holiday). */
        holidayType("Typ", 120, String.class),
        /** Column: event category. */
        category("Kategorie", 120, String.class),
        /** Column: selection checkbox. */
        checkbox("", 20, Boolean.class);
        
        /** The table column header. */
        private String headerText;
        
        /** The preferred column width in pixels. */
        private int preferredSize;
        
        /** The class of the column content (String, Integer, ...). */
        private Class<?> columnClass;
        
        /**
         * Constructor for a column.
         * @param headerText - Column header text
         * @param preferredSize - Preferred column width in pixels
         * @param columnClass - The class of the column content (String, Integer, ...)
         */
        private Columns(String headerText, int preferredSize, Class<?> columnClass) {
            this.headerText    = headerText;
            this.preferredSize = preferredSize;
            this.columnClass   = columnClass;
        }
    }
    
    /**
     * Construct a new table of events (derived from JTable) with the following columns:
     * <li>Event ID (this column is hidden)
     * <li>Event Name
     * <li>Event Date (start date)
     * <li>Event time
     * <li>Event frequency
     * <li>Event category.
     * @param events - List of events to be displayed in the table
     */
    public EventTable(List<Event> events) {
        this(events, Arrays.asList(
                Columns.id, Columns.name, Columns.date, Columns.time, Columns.freq, Columns.category));
    }
    
    /**
     * Construct a new table of events (derived from JTable) with a custom set of columns.
     * @param events - List of events to be displayed in the table
     * @param columns - List of columns to be displayed
     */
    public EventTable(List<Event> events, List<Columns> columns) {
        super(new DefaultTableModel(getTableContent(events, columns), getTableHeaders(columns)));
        
        this.tableModel = (DefaultTableModel) getModel();
        this.events     = events;
        this.columns    = columns;
        this.filteredEvents.addAll(events);
        
        /*
         * Setup column sorting
         */
        setUpdateSelectionOnSort(true);
        if (colDate != -1) {
            // sorter for dates (otherwise alphabetical order would apply)
            TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>();
            rowSorter.setModel(tableModel);
            rowSorter.setComparator(colDate, new DateComparator());
            setRowSorter(rowSorter);
        }
        
        setAutoscrolls(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        setDragEnabled(false);
        setColumnSelectionAllowed(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getTableHeader().setReorderingAllowed(false);
        
        /*
         * Set column sizes
         */
        getColumnModel().getColumn(colID).setMinWidth(0);
        getColumnModel().getColumn(colID).setMaxWidth(0);
        getColumnModel().getColumn(colName).setPreferredWidth(Columns.name.preferredSize);
        if (colDate != -1) {
            getColumnModel().getColumn(colDate).setPreferredWidth(Columns.date.preferredSize);
        }
        if (colFreq != -1) {
            getColumnModel().getColumn(colFreq).setPreferredWidth(Columns.freq.preferredSize);
        }
        if (colTime != -1) {
            getColumnModel().getColumn(colTime).setPreferredWidth(Columns.time.preferredSize);
        }
        if (colCat != -1) {
            getColumnModel().getColumn(colCat).setPreferredWidth(Columns.category.preferredSize);
        }
        if (colHolidayType != -1) {
            getColumnModel().getColumn(colHolidayType).setPreferredWidth(Columns.holidayType.preferredSize);
        }
        if (colCheckbox != -1) {
            getColumnModel().getColumn(colCheckbox).setMinWidth(Columns.checkbox.preferredSize);
            getColumnModel().getColumn(colCheckbox).setMaxWidth(Columns.checkbox.preferredSize);
        }
        
        /*
         * Render category icon
         */
        getColumnModel().getColumn(colName).setCellRenderer(new DefaultTableCellRenderer() {
            /** Default serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
               
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                int index = convertRowIndexToModel(row);
                if (index >= 0 && filteredEvents.get(index).getCategory() != null) {
                    setIcon(EventCategories.getScaledIcon(filteredEvents.get(index).getCategory(), 9));
                } else {
                    setIcon(null);
                }
               
                return this;
            }
        });
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // only selection checkbox column shall be editable
        return columns.get(column).equals(Columns.checkbox);
    }
    
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        if (events.size() > MAX_EVENTS_IN_FRAME) {
            return DEFAULT_TABLE_SIZE;
        } else {
            Dimension dim = getPreferredSize();
            dim.width = DEFAULT_TABLE_SIZE.width;
            if (dim.height < 100) {
                dim.height = 100;
            }
            return dim;
        }
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        return columns.get(column).columnClass;
    }
    
    @Override
    public void searchRequested(String searchText) {
        filter(searchText);
    }
    
    /**
     * Returns the currently selected event in the table.
     * @return the currently selected event in the table.
     */
    public Event getSelectedEvent() {
        int selectedRow = getSelectedRow();
        if (selectedRow == -1) {
            // nothing selected
            return null;
        }
        
        return getEventAtRow(selectedRow);
    }
    
    /**
     * Returns the event at a specific table row.
     * @param row - Row for which to return the event
     * @return the event at a specific table row.
     */
    protected Event getEventAtRow(int row) {
        // determine selected event ID and name
        String id   = (String) getValueAt(row, colID);
        String name = (String) getValueAt(row, colName);
        LOG.fine("requested event: id=" + id + " name=" + name);
        
        // get the selected event
        for (Event ev : events) {
            if (id.equals("" + ev.getID()) && name.equals(ev.getName())) {
                return ev;
            }
        }
        
        // this case should not happen
        return null;
    }
    
    /**
     * Remove the currently selected event row from the table (permanently).
     */
    public void removeSelectedEvent() {
        Event selected = getSelectedEvent();
        
        tableModel.removeRow(getSelectedRow());
        events.remove(selected);
        filteredEvents.remove(selected);
    }
    
    /**
     * Filter the table of events by a user specific search text. This (temporarily) removes
     * all rows from the table which do NOT match the filter text.
     * @param searchText - Filter text. If text is empty, this restored all events again (no filter)
     */
    public void filter(String searchText) {
        String text = searchText.toLowerCase();
        LOG.fine("filter: " + text);
        filteredEvents.clear();
        
        int count = tableModel.getRowCount();
        LOG.fine("row count before filter: " + count);
        
        // first remove all rows
        for (int i = 0; i < count; i++) {
            tableModel.removeRow(0);
        }
        
        
        if (text.isEmpty()) {
            /*
             * If text field is empty, reset filter (query all events)
             */
            filteredEvents.addAll(events);
        
        } else {
            /*
             * Query events which contain the filter pattern
             */
            for (Event e : events) {
                if (e.getName().toLowerCase().contains(text) // search name
                        || e.getDate().print(PrintFormat.DMYYYY).contains(text) // search date
                        || e.getDate().print(PrintFormat.DDMMYYYY).contains(text) // search date
                        || (e.getCategory() != null && e.getCategory().toLowerCase().contains(text)) // search category
                        || (e.getDate().hasTime() && e.getDate().print(PrintFormat.HHmm).contains(text)) // search time
                        || e.getType().getShortName().toLowerCase().contains(text)) { // search type (for holidays)
                    filteredEvents.add(e);
                }
            }
        }
        
        // finally create a new list of filtered events and add them again
        String[][] content = getTableContent(filteredEvents, columns);
        for (String[] row : content) {
            tableModel.addRow(row);
        }
    }
    
    /**
     * Build the table data (rows and columns to be shown).
     * @param ev - Events to be displayed in the table
     * @param columns - List of columns to show
     * @return Table matrix.
     */
    private static String[][] getTableContent(List<Event> ev, List<Columns> columns) {
        // set index of the columns, -1 means column is not present
        colID          = columns.indexOf(Columns.id);
        colName        = columns.indexOf(Columns.name);
        colDate        = columns.indexOf(Columns.date);
        colTime        = columns.indexOf(Columns.time);
        colFreq        = columns.indexOf(Columns.freq);
        colHolidayType = columns.indexOf(Columns.holidayType);
        colCat         = columns.indexOf(Columns.category);
        colCheckbox    = columns.indexOf(Columns.checkbox);
        
        // fill table data in matrix
        String[][] rows = new String[ev.size()][columns.size()];
        for (int i = 0; i < ev.size(); i++) {
            if (colDate != -1) {
                rows[i][colDate] = ev.get(i).getDate().print();
                if (ev.get(i).getEndDate() != null) {
                    rows[i][colDate] += "-" + ev.get(i).getEndDate().print();
                }
            }
            if (colID != -1) {
                rows[i][colID] = "" + ev.get(i).getID();
            }
            if (colName != -1) {
                rows[i][colName] = ev.get(i).getName();
            }
            if (colTime != -1 && ev.get(i).getDate().hasTime()) {
                rows[i][colTime] = ev.get(i).getDate().print(PrintFormat.HHmm);
            }
            if (colFreq != -1) {
                rows[i][colFreq] = Frequency.getLabel(ev.get(i).getFrequency(), ev.get(i).getDate());
            }
            if (colHolidayType != -1) {
                rows[i][colHolidayType] = ev.get(i).getType().getShortName();
            }
            if (colCat != -1) {
                rows[i][colCat] = ev.get(i).getCategory();
            }
        }

        return rows;
    }
    
    /**
     * Creates the list of column headers.
     * @param columns - List of desired columns
     * @return the list of column headers.
     */
    private static String[] getTableHeaders(List<Columns> columns) {
        String[] headers = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            headers[i] = columns.get(i).headerText;
        }
        return headers;
    }
}
