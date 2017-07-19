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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.TableModel;

import de.jsteltze.calendar.Event;

/**
 * Graphical table of calendar events with a checkbox in each row to allow multi-selections.
 * @author Johannes Steltzer
 *
 */
public class EventTableWithCheckbox extends EventTable {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Index of the checkbox column. */
    private int indexOfCheckboxColumn;

    /**
     * Construct a new table of events (derived from JTable) with the following columns:
     * <li>Event ID (this column is hidden)
     * <li>Checkbox for selection
     * <li>Event Name
     * <li>Event Date (start date)
     * <li>Event time
     * <li>Event frequency.
     * @param events - List of events to be displayed in the table
     */
    public EventTableWithCheckbox(List<Event> events) {
        this(events, Arrays.asList(Columns.id, Columns.checkbox, Columns.name, 
                Columns.date, Columns.time, Columns.freq));
    }
    
    /**
     * Construct a new table of events (derived from JTable) with a custom set of columns.
     * @param events - List of events to be displayed in the table
     * @param columns - List of columns to be displayed (<b>here you must add
     * the checkbox column yourself. Otherwise this class makes no sense!</b>)
     */
    public EventTableWithCheckbox(List<Event> events, List<Columns> columns) {
        super(events, columns);
        
        // selection only via the checkboxes!
        setRowSelectionAllowed(false);
        
        this.indexOfCheckboxColumn = columns.indexOf(Columns.checkbox);
    }
    
    /**
     * Select or unselect all rows. This sets the checkbox states properly.
     * <b>Please note: This only applies to the rows currently displayed (e.g. use of filter)!</b> 
     * @param value - True for select the checkboxes in all rows; false for unselect all checkboxes
     */
    public void selectAll(boolean value) {
        for (int row = 0; row < getRowCount(); row++) {
            setValueAt(value, row, indexOfCheckboxColumn);
        }
    }
    
    /**
     * Returns a list of all selected (checked) events.
     * @return a list of all selected (checked) events.
     */
    public List<Event> getSelectedEvents() {
        TableModel model = getModel();
        List<Event> selectedEvents = new ArrayList<Event>();
        
        for (int row = 0; row < getRowCount(); row++) {
            if (model.getValueAt(row, indexOfCheckboxColumn) == Boolean.TRUE) {
                selectedEvents.add(getEventAtRow(row));
            }
        }
        
        return selectedEvents;
    }
    
    /**
     * Returns whether or not at least one row is selected (checked).
     * @return whether or not at least one row is selected (checked).
     */
    public boolean isSomethingSelected() {
        TableModel model = getModel();
        for (int row = 0; row < getRowCount(); row++) {
            if (model.getValueAt(row, indexOfCheckboxColumn) == Boolean.TRUE) {
                return true;
            }
        }
        
        return false;
    }
}
