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
 * An enumeration of all supported calendar styles (Look&Feel).
 * @author Johannes Steltzer
 *
 */
public enum Style {
    
    /** System (default) style. */
    system("System"),
    
    /** Java Swing style (theme Ocean). */
    swingOcean("Swing (Ocean)"),
    
    /** Motif style. */
    motif("Motif"),
    
    /** Nimbus style. */
    nimbus("Nimbus"),
    
    /** Java Swing style (theme Default). */
    swingDefault("Swing (Default)");
    
    /** Style name. */
    private final String styleName;
    
    /**
     * Construct a style.
     * @param styleName - style name
     */
    private Style(String styleName) {
        this.styleName = styleName;
    }

    @Override
    public String toString() {
        return styleName;
    }
}
