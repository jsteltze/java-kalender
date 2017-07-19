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

package de.jsteltze.calendar.config.enums;

/**
 * An enumeration of all supported calendar views.
 * @author Johannes Steltzer
 *
 */
public enum View {
    
    /** Yearly view. */
    year("Jahresansicht"),
    
    /** Monthly view. */
    month("Monatsansicht"),
    
    /** Weekly view. */
    week("Wochenansicht"),
    
    /** Daily view. */
    day("Tagesansicht");
    
    /** View name. */
    private final String viewName;
    
    /**
     * Construct a view.
     * @param viewName - view name
     */
    private View(String viewName) {
        this.viewName = viewName;
    }

    @Override
    public String toString() {
        return viewName;
    }
}
