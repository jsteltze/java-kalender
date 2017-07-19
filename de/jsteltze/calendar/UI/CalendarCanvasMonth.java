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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.config.ColorSet;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.calendar.config.enums.View;
import de.jsteltze.common.GraphicUtils;
import de.jsteltze.common.Log;
import de.jsteltze.common.calendar.Date;

/**
 * Canvas with monthly view on which to paint calendar contents.
 * @author Johannes Steltzer
 *
 */
public class CalendarCanvasMonth 
    extends CalendarCanvas {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Pixel width of the numbers of calendar weeks in monthly view. */
    private static final int 
            WIDTH_CALENDAR_WEEK_LABEL = 22,
            COL_FOR_KW_MIDDLE = 7,
            COL_FOR_KW_RIGHT = 15;
    
    /** Highlight codes. */
    public static final int 
            HIGHLIGHT_COLUMN = 15,
            HIGHLIGHT_WEEK_LEFT = 20,
            HIGHLIGHT_WEEK_RIGHT = 30,
            HIGHLIGHT_MONTH_LEFT = 50,
            HIGHLIGHT_MONTH_RIGHT = 60;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(CalendarCanvasMonth.class);
    
    /**
     * Construct new calendar canvas with monthly view.
     * @param c - Parent calendar object
     */
    public CalendarCanvasMonth(Calendar c) {
        super(c, View.month);
        
        rows = CellMonth.ROWS;
        cols = CellMonth.COLS;
        clear_up = CellMonth.GAP.y;
        clear_left = CellMonth.GAP.x;
    }

    @Override
    public Dimension calcDim() {
        width = this.getWidth() - clear_left - 30;
        height = this.getHeight() - clear_up - 5;
        
        width /= cols;
        height /= rows;
        return new Dimension(width, height);
    }
    
    @Override
    protected void drawCalendarGrid(Graphics g) {
        g.setColor(Const.COLOR_DEF_FONT);
        g.setFont(Const.FONT_MONTH_KW_LABEL);
        
        // draw the 'KW' label
        g.drawString("KW", clear_left + 7 * width + 5, clear_up - 5);
        g.drawString("KW", clear_left + cols * width + 5, clear_up - 5);
        
        // draw all days of week labels
        g.setColor(Color.BLACK);
        g.setFont(Const.FONT_MONTH_DAY_OF_WEEK);
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 7; i++) {
                g.drawString(Date.dayOfWeek2String(daysOfWeek[i], true), 
                        10 + i * width + j * 8 * width, clear_up - 5);
            }
        }
        
        // draw the rectangles
        g.setColor(Const.COLOR_GRID_LINES);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j != 7) {
                    g.drawRect(clear_left + j * width, clear_up + i * height, width, height);
                }
            }
        }
    }
    
    @Override
    protected void resetHeadline(Graphics g) {
        if (highlightedHeadline == -1) {
            return;
        }

        /* unmark day of week */
        if (highlightedHeadline < HIGHLIGHT_COLUMN) {
            final int fontHeight = 17;
            g.setColor(calendar.getConfig().getColors()[ColorSet.BACKGROUND]);
            g.fillRect(10 + highlightedHeadline * width, clear_up - fontHeight, width, fontHeight);
            g.setColor(Color.BLACK);
            g.setFont(Const.FONT_MONTH_DAY_OF_WEEK);
            g.drawString(Date.dayOfWeek2String(daysOfWeek[highlightedHeadline > 6 
                    ? highlightedHeadline - 8 : highlightedHeadline], true), 
                    10 + highlightedHeadline * width, clear_up - 5);
        
        /* unmark calendar week */
        } else if (highlightedHeadline < HIGHLIGHT_MONTH_LEFT) {
            paintKWs(g);
        
        /* unmark month name */
        } else if (highlightedHeadline == HIGHLIGHT_MONTH_LEFT || highlightedHeadline == HIGHLIGHT_MONTH_RIGHT) {
            boolean leftSide = highlightedHeadline == HIGHLIGHT_MONTH_LEFT;
            
            g.setColor(calendar.getConfig().getColors()[ColorSet.BACKGROUND]);
            String line = Date.month2String(matrix[leftSide ? 3 : 10][3].getDate().get(java.util.Calendar.MONTH), false)
                    + " " + matrix[leftSide ? 3 : 10][3].getDate().get(java.util.Calendar.YEAR);
            
            int start = (leftSide ? 0 : 8 * width) + clear_left - 2;
            int widthHeadline = GraphicUtils.getStringWidth(g, Const.FONT_MONTH_NAME, line) 
                    + start + 4;

            g.fillPolygon(
                    new int[]{start + 5, start, start},
                    new int[]{clear_up - 30, clear_up - 35, clear_up - 25}, 3);
            g.fillPolygon(
                    new int[]{widthHeadline + 5, widthHeadline + 10, widthHeadline + 10},
                    new int[]{clear_up - 30, clear_up - 35, clear_up - 25}, 3);

        }
        highlightedHeadline = -1;
    }
    
    /**
     * Clear and print all calendar week numbers for the current two months displayed.
     * @param g - Graphics to paint on
     */
    private void paintKWs(Graphics g) {
        for (int site = 0; site < 2; site++) {
            for (int rowIndex = 0; rowIndex < 6; rowIndex++) {
                int week;
                if (matrix[site * 8][rowIndex] != null && matrix[site * 8][rowIndex].getDate() != null) {
                    week = getWeekNo(matrix[site * 8][rowIndex].getDate());
                } else if (matrix[site * 8 + 6][rowIndex] != null && matrix[site * 8 + 6][rowIndex].getDate() != null) {
                    week = getWeekNo(matrix[site * 8 + 6][rowIndex].getDate());
                } else {
                    continue;
                }
                
                // Delete bold font by painting a rectangle with background color over it
                g.setColor(calendar.getConfig().getColors()[ColorSet.BACKGROUND]);
                g.fillRect(clear_left + (site * 8 + 7) * width + 1, rowIndex * height + clear_up + 1,
                        width - 2, height - 2);
                
                // Write week No. with normal font again
                g.setColor(Const.COLOR_DEF_FONT);
                g.setFont(Const.FONT_MONTH_KW_NUMBER);
                g.drawString("" + week, clear_left + (site * 8 + 7) * width + 5, rowIndex * height + clear_up + 25);
            }
        }
    }
    
    @Override
    protected void highlightHeadline(Graphics g, int index, int endindex) {
        if (highlightedHeadlineEnd == endindex && highlightedHeadline == index) {
            return;
        }
        
        resetHeadline(g);
        highlightedHeadline = index;
        highlightedHeadlineEnd = endindex;

        /* mark column (week day) */
        if (index < HIGHLIGHT_COLUMN) {
            g.setColor(calendar.getConfig().getColors()[ColorSet.BACKGROUND]);
            g.fillRect(10 + highlightedHeadline * width, clear_up - 17, 25, 17);
            g.setColor(Color.BLACK);
            g.setFont(Const.FONT_MONTH_DAY_OF_WEEK.deriveFont(Font.BOLD));
            g.drawString(Date.dayOfWeek2String(daysOfWeek[index > 6 ? index - 8 : index], true),
                    10 + index * width, clear_up - 5);
        
        /* mark week by number */
        } else if (index < HIGHLIGHT_WEEK_RIGHT + 10) {
            if (endindex != -1 && endindex < index) {
                int tmp = index;
                index = endindex;
                endindex = tmp;
            }
            LOG.fine("highlight calendar week: index=" + index + " endindex=" + endindex);
            for (int tmpindex = index; tmpindex <= (endindex == -1 ? index : endindex); tmpindex++) {
                int i = tmpindex;
                boolean rightSite = i >= HIGHLIGHT_WEEK_RIGHT;
                i -= 20;
                if (rightSite) {
                    i -= 10;
                }
                int week;
                if (matrix[rightSite ? 8 : 0][i] != null && matrix[rightSite ? 8 : 0][i].getDate() != null) {
                    week = getWeekNo(matrix[rightSite ? 8 : 0][i].getDate());
                } else if (matrix[rightSite ? 14 : 6][i] != null && matrix[rightSite ? 14 : 6][i].getDate() != null) {
                    week = getWeekNo(matrix[rightSite ? 14 : 6][i].getDate());
                } else {
                    return;
                }
                g.setColor(Const.COLOR_DEF_FONT);
                g.setFont(Const.FONT_MONTH_KW_NUMBER.deriveFont(Font.BOLD));
                g.drawString("" + week, clear_left + (rightSite ? 15 : 7) * width + 5, i * height + clear_up + 25);
            }
        
        /* mark whole month */
        } else if (index == HIGHLIGHT_MONTH_LEFT || index == HIGHLIGHT_MONTH_RIGHT) {
            boolean leftSide = index == HIGHLIGHT_MONTH_LEFT;
            g.setColor(Const.COLOR_DEF_FONT);
            String line = Date.month2String(matrix[leftSide ? 3 : 10][3].getDate().get(java.util.Calendar.MONTH), false)
                    + " " + matrix[leftSide ? 3 : 10][3].getDate().get(java.util.Calendar.YEAR);
            
            int start = (leftSide ? 0 : 8 * width) + clear_left - 2;
            int widthHeadline = GraphicUtils.getStringWidth(g, Const.FONT_MONTH_NAME, line) + start + 4;

            g.fillPolygon(
                    new int[]{start + 5, start, start},
                    new int[]{clear_up - 30, clear_up - 35, clear_up - 25}, 3);
            g.fillPolygon(
                    new int[]{widthHeadline + 5, widthHeadline + 10, widthHeadline + 10},
                    new int[]{clear_up - 30, clear_up - 35, clear_up - 25}, 3);
        }
    }
    
    @Override
    protected void fillCalendarContent(Graphics g, List<Event> events, Date date) {
        date.set(java.util.Calendar.DAY_OF_MONTH, 1);
        int col, row;

        /* for both sites do... */
        for (int j = 0; j < 2; j++) {

            /* Print name of month */
            String monthName = Date.month2String(date.get(java.util.Calendar.MONTH), false)
                    + " " + date.get(java.util.Calendar.YEAR);
            g.setColor(Color.black);
            g.setFont(Const.FONT_MONTH_NAME);
            g.drawString(monthName, clear_left + j * 8 * width + 5, clear_up - 24);
            // store length of month names for mouse move function
            lenMonthName[j] = GraphicUtils.getStringWidth(g, monthName);
            
            /* Determine the column to start with (depending on the day of week). */
            col = 0;
            for (int i = 0; i < 7; i++) {
                if (daysOfWeek[i] == date.get(Date.DAY_OF_WEEK)) {
                    col = i;
                }
            }
            col += j * 8;
            
            /* Print days of previous month in gray. */
            g.setColor(Color.lightGray);
            g.setFont(Const.FONT_MONTH_DAY_OTHER);
            for (int day = col - 1; day >= j * 8; day--) {
                date.add(java.util.Calendar.DAY_OF_MONTH, -1);
                g.drawString("" + date.get(java.util.Calendar.DAY_OF_MONTH), CellMonth.GAP.x + day * width + 7, 
                        CellMonth.GAP.y + 20);
            }
            date.add(java.util.Calendar.DAY_OF_MONTH, col - j * 8);
            
            int maxDays = date.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            
            /* Always start with row 0. */
            row = 0;

            /* for each day do... */
            for (int day = 1; day <= maxDays; day++) {
                date.set(java.util.Calendar.DAY_OF_MONTH, day);
                matrix[col][row] = new CellMonth(this, col, row, date.clone());
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
                
                if (col == 6) { // Line break on left site
                    col = 0;
                    row++;
                } else if (col == 14) { // Line break on right site
                    col = 8;
                    row++;
                } else {
                    col++;
                }
            }
            
            /* Print days of next month in gray. */
            if (col != 0 && col != 8) {
                g.setColor(Color.lightGray);
                g.setFont(Const.FONT_MONTH_DAY_OTHER);
                for (int day = col; day <= 6 + j * 8; day++) {
                    g.drawString("" + (day - col + 1), CellMonth.GAP.x + day * width + 7, 
                            CellMonth.GAP.y + row * height + 20);
                }
            }
            
            /* Go to next month (for the following loop iteration). */
            date.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }
        
        // print calendar week No.s
        paintKWs(g);
    }
    
    @Override
    protected void markIt(Point start, Point end) {
        int col = start.x;
        int row = start.y;
        
        List<Cell> toMark = new ArrayList<Cell>();
        
        if (col == -1 || col == 7 || end.x == -1 || end.x == 7) {
            return;
        }
        if (end.y < row || ((end.x < col) && (row == end.y))
                || ((end.x < 7) && (col > 7))) {
            reverse = true;
        } else {
            reverse = false;
        }
        if (col < 7 && end.x > 7) {
            reverse = false;
        }

        while (true) {
            if (!reverse) {
                if (col == 7) {
                    /* Line break on left site (downwards) */
                    col = 0;
                    row++;
                } 
                else if (col == cols) { 
                    /* Line break on right site (downwards) */
                    col = 8;
                    row++;
                }
                if (row == rows && col == 0) { 
                    /* Change from left to right site */
                    row = 0;
                    col = 8;
                }
            } 
            else {
                if (col == -1) { 
                    /* Line break on left site (upwards) */
                    col = 6;
                    row--;
                } 
                else if (col == 7) { 
                    /* Line break on right site (upwards) */
                    col = 14;
                    row--;
                }
                if (row == -1 && col == 14) { 
                    /* Change from right to left site */
                    row = 5;
                    col = 6;
                }
            }
            
            if (matrix[col][row] != null) {
                toMark.add(matrix[col][row]);
            }
            if (col == end.x && row == end.y) {
                break;
            }
            if (!reverse) {
                col++;
            } else {
                col--;
            }
        }
        
        mark(toMark);
    }
    
    @Override
    protected boolean mouseOnCalendarWeekLabel(int xPos, int yPos, int colIndex, int rowIndex) {
        return (colIndex == COL_FOR_KW_MIDDLE || colIndex == COL_FOR_KW_RIGHT) 
                && rowIndex < rows
                && xPos < (clear_left + colIndex * width + WIDTH_CALENDAR_WEEK_LABEL);
    }
    
    @Override
    protected void markColumn(int col) {
        List<Cell> toMark = new ArrayList<Cell>();
        if (col != 7) {
            for (int i = 0; i < 6; i++) {
                if (matrix[col][i] != null) {
                    toMark.add(matrix[col][i]);
                }
            }
        }
        mark(toMark);
        highlightHeadline(getGraphics(), col, -1);
    }
}