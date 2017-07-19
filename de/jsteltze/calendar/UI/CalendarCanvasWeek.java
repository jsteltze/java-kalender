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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.logging.Logger;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.calendar.config.enums.View;
import de.jsteltze.common.Log;
import de.jsteltze.common.calendar.Date;

/**
 * Canvas with weekly view on which to paint calendar contents.
 * @author Johannes Steltzer
 *
 */
public class CalendarCanvasWeek 
    extends CalendarCanvas {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Pixel size of the arrow for selecting a calendar week. */
    public static final int
            ARROW_START_X = 5,
            ARROW_END_X = 10,
            ARROW_START_Y = 6,
            ARROW_END_Y = 16;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(CalendarCanvasWeek.class);
    
    /**
     * Construct new calendar canvas with weekly view.
     * @param c - Parent calendar object
     */
    public CalendarCanvasWeek(Calendar c) {
        super(c, View.week);
        
        rows = CellWeek.ROWS;
        cols = CellWeek.COLS;
        clear_up = CellWeek.GAP.y;
        clear_left = CellWeek.GAP.x;
    }

    @Override
    public Dimension calcDim() {
        width = this.getWidth() - clear_left - 80;
        height = this.getHeight() - clear_up - 5;
        
        width /= cols;
        height /= rows;
        return new Dimension(width, height);
    }
    
    @Override
    protected void drawCalendarGrid(Graphics g) {
        // draw rectangles
        g.setColor(Const.COLOR_DEF_FONT);
        for (int i = 0; i < rows; i++) {
            g.drawRect(clear_left, clear_up + i * height, cols * width, CellWeek.HEADER_HEIGHT);
        }
//        g.setColor(Color.black);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                g.drawRect(clear_left + j * width, clear_up + i * height,
                        width, height);
            }
        }
        
        // print the days of week
        g.setColor(Const.COLOR_DEF_FONT);
        g.setFont(Const.FONT_WEEK_DAY_OF_WEEK);
        FontMetrics fm = g.getFontMetrics();
        for (int i = 0; i < cols; i++) {
            String weekdayLabel = Date.dayOfWeek2String(daysOfWeek[i], false);
            int labelWidth = fm.stringWidth(weekdayLabel);
            g.drawString(weekdayLabel, clear_left + i * width + (width - labelWidth) / 2, clear_up - 5);
        }
        
        // print the 'Woche' label
        g.drawString("Woche", clear_left + 5 + cols * width, clear_up - 5);
        
        // draw the highlight arrows
        for (int i = 0; i < rows; i++) {
            g.fillPolygon(
                    new int[] {
                            clear_left + ARROW_START_X + cols * width, 
                            clear_left + ARROW_END_X + cols * width, 
                            clear_left + ARROW_END_X + cols * width},
                    new int[] {
                            clear_up + (ARROW_START_Y + ARROW_END_Y) / 2 + i * height,
                            clear_up + ARROW_START_Y + i * height, 
                            clear_up + ARROW_END_Y + i * height},
                    3);
        }
    }
    
    @Override
    protected void fillCalendarContent(Graphics g, List<Event> events, Date date) {
        int col, row;

        /* find first day of upper week */
        int weekNo = getWeekNo(date);
        int tmp = weekNo;
        while (tmp == weekNo) {
            date.add(java.util.Calendar.DAY_OF_MONTH, -1);
            tmp = getWeekNo(date);
        }
        date.add(java.util.Calendar.DAY_OF_MONTH, 1);
        col = 0;
        row = 0;

        for (int j = 0; j < rows; j++) {
            
            /* Print number of week */
            weekNo = date.get(java.util.Calendar.WEEK_OF_YEAR);
            g.setColor(Const.COLOR_DEF_FONT);
            g.setFont(Const.FONT_WEEK_KW_NUMBER);
            g.drawString("" + weekNo, clear_left + cols * width + 5, 
                    j * height + clear_up + 80);

            for (int i = 0; i < 7; i++) {

                matrix[col][row] = new CellWeek(this, col, row, date.clone());
                if (markedDates.contains(date)) {
                    LOG.fine("selection contains: " + date.print());
                    matrix[col][row].setSelected(true);
                }

                /* Register events */
                for (Event e : events) {
                    if (e.match(date)) {
                        matrix[col][row].addEvent(e);
                    }
                }
                    
                matrix[col][row].paint(g, new Dimension(width, height));

                col++;
                date.add(java.util.Calendar.DAY_OF_MONTH, 1);
            }
            row++;
            col = 0;
        }
    }
    
    @Override
    protected void markIt(Point start, Point end) {
        markWeekYear(start, end);
    }
    
    @Override
    protected boolean mouseOnCalendarWeekLabel(int xPos, int yPos, int colIndex, int rowIndex) {
        return (colIndex == cols 
                && xPos >= clear_left + 5 + cols * width && xPos <= clear_left + ARROW_END_X + cols * width
                && yPos >= clear_up + 6 + rowIndex * height && yPos <= clear_up + ARROW_END_Y + rowIndex * height);
    }
    
    @Override
    protected void resetHeadline(Graphics g) { }
    
    @Override
    protected void highlightHeadline(Graphics g, int index, int endindex) { }
    
    @Override
    protected void markColumn(int col) { }
}