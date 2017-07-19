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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Calendar;

import de.jsteltze.common.ColorUtil;
import de.jsteltze.common.calendar.Date;

/**
 * Single cell (date) within calendar canvas yearly view.
 * @author Johannes Steltzer
 *
 */
public class CellYear extends Cell {

    /** The point where the actual drawing of the calendar cells starts. */
    public static final Point GAP = new Point(28, 44);
    
    /** Number of columns in the matrix. */
    public static final int COLS = 37;
    
    /** Number of rows in the matrix. */
    public static final int ROWS = 12;

    /**
     * Construct a new cell.
     * @param owner - CalendarFrame object where to paint
     * @param col - Column index within matrix 
     * @param row - Row index within matrix
     * @param date - Date of this cell
     */
    public CellYear(CalendarCanvas owner, int col, int row, Date date) {
        super(owner, col, row, date);
        
        this.numLeftNeighbors  = date.get(Calendar.DAY_OF_MONTH) - 1;
        this.numRightNeighbors = date.getDaysToEndOfMonth();
        
        headerHeight = 0;
        gap = GAP;
    }
    
    @Override
    public void paint(Graphics x, Dimension space) {
        if (date == null) {
            return;
        }
            
        /* adapt font size */
        fontsize = 8;
        if (space.width >= 14 && space.height >= 22) {
            fontsize = 9;
        }
        if (space.width >= 14 && space.height >= 24) {
            fontsize = 10;
        }
        if (space.width >= 16 && space.height >= 24) {
            fontsize = 11;
        }
        if (space.width >= 18 && space.height >= 25) {
            fontsize = 12;
        }
        if (space.width >= 18 && space.height >= 27) {
            fontsize = 13;
        }
        if (space.width >= 20 && space.height >= 29) {
            fontsize = 14;
        }
        
        Color c1, c2;

        /* paint holiday over weekend */
        if (isHoliday()) {
            c1 = holidayBgColor;
            c2 = ColorUtil.addSaturation(c1, SATURATION_DIFF);
        }
        else if (isWeekend()) {
            c1 = weekendColor;
            c2 = ColorUtil.addSaturation(c1, SATURATION_DIFF);
        } else {
            c1 = Color.white;
            c2 = Color.white;
        }
        
        x.setColor(c1);
        x.fillRect(GAP.x + 1 + col * space.width, GAP.y
                + 1 + row * space.height, space.width - 1, space.height / 2);
        x.setColor(c2);
        x.fillRect(GAP.x + 1 + col * space.width, GAP.y
                + 1 + row * space.height + space.height / 2, space.width - 1, (space.height - 1) / 2);

        
        /* paint selection over today */
        if (isSelected()) {
            c1 = selectedColor;
            c2 = selectedColorD;
        }
        else if (isToday()) {
//            c1 = todayColor;
//            c2 = ColorUtil.addSaturation(c1, _SATURATION);
            c2 = todayColor;
        }
        
        if (isSelected() || isToday()) {
            x.setColor(c1);
//            x.fillRect(GAP.x + 1 + col * space.width, GAP.y + HEADER_HEIGHT + row * space.height, 
//                    space.width - 1, (space.height - HEADER_HEIGHT) / 2);
//            x.setColor(c2);
//            x.fillRect(GAP.x + 1 + col * space.width, 
//                    GAP.y + HEADER_HEIGHT + (space.height - HEADER_HEIGHT) / 2 + row * space.height, 
//                    space.width - 1, (space.height - HEADER_HEIGHT) / 2);
            
            x.fillRect(GAP.x + 1 + col * space.width, GAP.y + row * space.height, 
                    space.width - 1, space.height / 2);
            x.setColor(c2);
            x.fillRect(GAP.x + 1 + col * space.width, 
                    GAP.y + space.height / 2 + row * space.height, 
                    space.width - 1, (space.height + 1) / 2);

        }

        /* print number of day */
        x.setColor(Color.darkGray);
        x.setFont(new Font(Font.SANS_SERIF, isToday() ? Font.BOLD : Font.PLAIN, fontsize));
        x.drawString("" + date.get(Calendar.DAY_OF_MONTH), GAP.x
                + col * space.width + 1, GAP.y + (row + 1) * space.height - 1);

        /* print number of events */
//            if (events.size() > 0) {
//                x.setColor(ftColor);
//                x.drawString("(" + events.size() + ")", Y_CLEAR_LEFT + col * space.width, 
//                        Y_CLEAR_UP + (row + 1) * space.height - space.height / 2);
//            }
        
        max_lines = (space.height - fontsize) / 4;
        if (max_lines < 1) {
            return;
        }
        
        fontsize = 3;
        
        /* paint all events in this cell */
        paintEvents(x, space, false, true);
    }
}