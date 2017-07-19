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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.calendar.config.enums.View;
import de.jsteltze.common.calendar.Date;

/**
 * Canvas with daily view on which to paint calendar contents.
 * @author Johannes Steltzer
 *
 */
public class CalendarCanvasDay 
    extends CalendarCanvas {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    ///** Logger. */
    //private static final Logger LOG = Log.getLogger(CalendarCanvasDay.class);
    
    /**
     * Construct new calendar canvas with daily view.
     * @param c - Parent calendar object
     */
    public CalendarCanvasDay(Calendar c) {
        super(c, View.day);
        
        rows = CellDay.ROWS;
        cols = CellDay.COLS;
        clear_up = CellDay.GAP.y;
        clear_left = CellDay.GAP.x;
    }
    
    @Override
    public Dimension calcDim() {
        width = this.getWidth() - clear_left - 5;
        height = this.getHeight() - clear_up - 5;
        
        width /= cols;
        height /= rows;
        return new Dimension(width, height);
    }
    
    @Override
    protected void drawCalendarGrid(Graphics g) {
        g.setColor(Const.COLOR_GRID_LINES);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                g.drawRect(clear_left + j * width, clear_up + i * height,
                        width, height);
            }
        }
        
        g.drawRect(clear_left, clear_up - 19, 280, 20);
        g.drawRect(clear_left + width, clear_up - 19, 280, 20);
        g.drawLine(clear_left + 279, clear_up - 19, clear_left + 298, clear_up);
        g.drawLine(clear_left + width + 279, clear_up - 19, clear_left + width + 298, clear_up);
        
        g.setColor(Const.COLOR_DEF_FONT);
        g.setFont(Const.FONT_DAY_HOURS);
        for (int i = 0; i < Date.HOURS_OF_DAY; i++) {
            g.drawString(i + ":00", (i < 10) ? 9 : 3, clear_up + (i + 1) * height + 10);
        }
    }
    
    @Override
    protected void fillCalendarContent(Graphics g, List<Event> events, Date date) {
        /* For both days do... */
        for (int i = 0; i < cols; i++) {
            Date localDate = date.clone();
            localDate.add(java.util.Calendar.DAY_OF_MONTH, i);
            for (int j = 0; j < rows; j++) {
                if (j == 0) {
                    localDate.setHasTime(false);
                } else {
                    localDate.setHasTime(true);
                    localDate.set(java.util.Calendar.HOUR_OF_DAY, j - 1);
                    localDate.set(java.util.Calendar.MINUTE, 0);
                }
                matrix[i][j] = new CellDay(this, i, j, localDate.clone());

                if (markedDates.contains(localDate)) {
                    matrix[i][j].setSelected(true);
                }

                matrix[i][j].paint(g, new Dimension(width, height));
            }

            for (Event e : events) {
                if (e.match(localDate)) {
                    if (e.getDate().hasTime()) {
                        matrix[i][e.getDate().get(java.util.Calendar.HOUR_OF_DAY) + 1]
                                .addEvent(e);
                        matrix[i][e.getDate().get(java.util.Calendar.HOUR_OF_DAY) + 1]
                                .paint(g, new Dimension(width, height));
                    }

                    else {
                        matrix[i][0].addEvent(e);
                        matrix[i][0].paint(g, new Dimension(width, height));
                    }
                }
            }
        }
    }
    
    @Override
    protected void markIt(Point start, Point end) { }
    
    @Override
    protected boolean mouseOnCalendarWeekLabel(int xPos, int yPos, int colIndex, int rowIndex) {
        // Daily view does not have calendar week labels
        return false;
    }
    
    @Override
    protected void resetHeadline(Graphics g) { }
    
    @Override
    protected void highlightHeadline(Graphics g, int index, int endindex) { }
    
    @Override
    protected void markColumn(int col) { }
}