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
 * Canvas with yearly view on which to paint calendar contents.
 * @author Johannes Steltzer
 *
 */
public class CalendarCanvasYear 
    extends CalendarCanvas {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Highlight codes. */
    public static final int 
            HIGHLIGHT_CALENDAR_WEEK = 12,
            HIGHLIGHT_COLUMN = 30,
            HIGHLIGHT_YEAR = 100;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(CalendarCanvasYear.class);
    
    /**
     * Construct new calendar canvas with yearly view.
     * @param c - Parent calendar object
     */
    public CalendarCanvasYear(Calendar c) {
        super(c, View.year);
        
        rows = CellYear.ROWS;
        cols = CellYear.COLS;
        clear_up = CellYear.GAP.y;
        clear_left = CellYear.GAP.x;
    }

    @Override
    public Dimension calcDim() {
        int SPACE_FOR_KW = 29;
        if (this.getHeight() > 440) {
            SPACE_FOR_KW = 39;
        } else if (this.getHeight() > 420) {
            SPACE_FOR_KW = 37;
        } else if (this.getHeight() > 400) {
            SPACE_FOR_KW = 35;
        } else if (this.getHeight() > 380) {
            SPACE_FOR_KW = 33;
        } else if (this.getHeight() > 360) {
            SPACE_FOR_KW = 31;
        }

        width = this.getWidth() - clear_left - SPACE_FOR_KW;
        height = this.getHeight() - clear_up - 6;
        
        width /= cols;
        height /= rows;
        return new Dimension(width, height);
    }
    
    @Override
    protected void drawCalendarGrid(Graphics g) {
        // set the font size (depending on the height)
        if (this.getHeight() > 440) {
            fontsizeYear = 14;
        } else if (this.getHeight() > 420) {
            fontsizeYear = 13;
        } else if (this.getHeight() > 400) {
            fontsizeYear = 12;
        } else if (this.getHeight() > 380) {
            fontsizeYear = 11;
        } else if (this.getHeight() > 360) {
            fontsizeYear = 10;
        } else {
            fontsizeYear = 9;
        }
        
        // draw the rectangles
        g.setColor(Const.COLOR_GRID_LINES);
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                g.drawRect(clear_left + i * width, clear_up + j * height, width, height);
            }
        }
        
        g.setColor(Color.BLACK);

        // print the month names
        g.setFont(Const.FONT_YEAR_MONTH_NAME);
        int spaces_left[] = {6, 5, 5, 7, 7, 6, 10, 5, 4, 7, 4, 4};
        for (int i = 0; i < rows;) {
            g.drawString(Date.month2String(i, true), spaces_left[i], 
                    clear_up - 5 + height * (++i));
        }

        // print the week days (Mo...So)
        g.setColor(Const.COLOR_DEF_FONT);
        g.setFont(Const.FONT_YEAR_DAY_OF_WEEK.deriveFont((float) fontsizeYear));
        for (int i = 0; i < cols; i++) {
            g.drawString(Date.dayOfWeek2String(daysOfWeek[i % 7], true), 
                    clear_left + i * width, clear_up - 2);
        }
        
        // print the 'KWs' label
        g.drawString("KWs", clear_left + cols * width + 1, clear_up - 2);
    }
    
    @Override
    protected void resetHeadline(Graphics g) {
        if (highlightedHeadline == -1) {
            return;
        }
        
        /* unmark month */
        if (highlightedHeadline < HIGHLIGHT_CALENDAR_WEEK) {
            int spaces_left[] = {6, 5, 5, 7, 7, 6, 10, 5, 4, 7, 4, 4};
            g.setFont(Const.FONT_YEAR_MONTH_NAME);
            g.setColor(calendar.getConfig().getColors()[ColorSet.BACKGROUND]);
            g.fillRect(0, clear_up + height * highlightedHeadline, 
                    clear_left, height);
            g.setColor(Color.black);
            g.drawString(Date.month2String(highlightedHeadline, true), spaces_left[highlightedHeadline], 
                        clear_up - 5 + height * (highlightedHeadline + 1));
        
        /* unmark calendar weeks */
        } else if (highlightedHeadline < 24) {
            int kw_start, kw_end, i = 0;
            int index = highlightedHeadline - HIGHLIGHT_CALENDAR_WEEK;
            while (matrix[i][index] == null || matrix[i][index].getDate() == null) {
                i++;
            }
            kw_start = getWeekNo(matrix[i][index].getDate());
            i = cols - 1;
            while (matrix[i][index] == null || matrix[i][index].getDate() == null) {
                i--;
            }
            kw_end = getWeekNo(matrix[i][index].getDate());
            g.setFont(Const.FONT_YEAR_KW_NUMBERS.deriveFont((float) fontsizeYear));
            g.setColor(calendar.getConfig().getColors()[ColorSet.BACKGROUND]);
            g.fillRect(clear_left + cols * width + 1, clear_up + height * index, 50, height);
            g.setColor(Const.COLOR_DEF_FONT);
            
            g.drawString("" + kw_start + "-" + kw_end,
                    cols * width + clear_left + 3, clear_up - 2
                            + (index + 1) * height);
        
        /* unmark day of week */
        } else if (highlightedHeadline < HIGHLIGHT_COLUMN + cols) {
            int index = highlightedHeadline - HIGHLIGHT_COLUMN;
            g.setFont(Const.FONT_YEAR_DAY_OF_WEEK.deriveFont((float) fontsizeYear));
            int base = index % 7;
            while (base < cols) {
                g.setColor(calendar.getConfig().getColors()[ColorSet.BACKGROUND]);
                g.fillRect(clear_left + base * width - 1, clear_up - fontsizeYear - 2, 
                        width, fontsizeYear);
                g.setColor(Const.COLOR_DEF_FONT);
                g.drawString(Date.dayOfWeek2String(daysOfWeek[index % 7], true), 
                        clear_left + base * width, clear_up - 2);
                base += 7;
            }
        
        /* unmark year */
        } else if (highlightedHeadline == HIGHLIGHT_YEAR) {
            int widthHeadline = GraphicUtils.getStringWidth(g, Const.FONT_YEAR_NUMBER, 
                    "" + matrix[10][3].getDate().get(java.util.Calendar.YEAR));
            g.setColor(calendar.getConfig().getColors()[ColorSet.BACKGROUND]);
            g.fillPolygon(
                    new int[]{this.getWidth() / 2 - 55, this.getWidth() / 2 - 60, this.getWidth() / 2 - 60},
                    new int[]{clear_up - 30, clear_up - 35, clear_up - 25}, 3);
            g.fillPolygon(
                    new int[]{this.getWidth() / 2 - 50 + widthHeadline + 5, this.getWidth() / 2 - 50 + widthHeadline + 10, this.getWidth() / 2 - 50 + widthHeadline + 10},
                    new int[]{clear_up - 30, clear_up - 35, clear_up - 25}, 3);
        }
        
        highlightedHeadline = -1;
    }
    
    @Override
    protected void highlightHeadline(Graphics g, int index, int endindex) {
        if (highlightedHeadline == index) {
            return;
        }
        resetHeadline(g);
        highlightedHeadline = index;
        
        /* mark month */
        if (index < HIGHLIGHT_CALENDAR_WEEK) {
            int spaces_left[] = {6, 5, 5, 7, 7, 6, 10, 5, 4, 7, 4, 4};
            g.setFont(Const.FONT_YEAR_MONTH_NAME.deriveFont(Font.BOLD));
            g.setColor(calendar.getConfig().getColors()[ColorSet.BACKGROUND]);
            g.fillRect(0, clear_up + height * index, 
                    clear_left, height);
            g.setColor(Color.BLACK);
            g.drawString(Date.month2String(index, true), spaces_left[index], 
                        clear_up - 5 + height * (index + 1));
        
        /* mark calendar weeks */
        } else if (index < 24) {
            int kw_start, kw_end, i = 0;
            index -= HIGHLIGHT_CALENDAR_WEEK;
            while (matrix[i][index] == null || matrix[i][index].getDate() == null) {
                i++;
            }
            kw_start = getWeekNo(matrix[i][index].getDate());
            i = cols - 1;
            while (matrix[i][index] == null || matrix[i][index].getDate() == null) {
                i--;
            }
            kw_end = getWeekNo(matrix[i][index].getDate());
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontsizeYear));
            g.setColor(calendar.getConfig().getColors()[ColorSet.BACKGROUND]);
            g.fillRect(clear_left + cols * width + 1, clear_up + height * index, 50, height);
            g.setColor(Color.BLACK);
            
            g.drawString("" + kw_start + "-" + kw_end,
                    cols * width + clear_left + 3, clear_up - 2
                            + (index + 1) * height);
        
        /* mark days of week */
        } else if (index < HIGHLIGHT_COLUMN + cols) {
            index -= HIGHLIGHT_COLUMN;
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontsizeYear));
            int base = index % 7;
            while (base < cols) {
                g.setColor(calendar.getConfig().getColors()[ColorSet.BACKGROUND]);
                g.fillRect(clear_left + base * width - 1, clear_up - fontsizeYear - 2, 
                        width, fontsizeYear);
                g.setColor(Color.BLACK);
                g.drawString(Date.dayOfWeek2String(daysOfWeek[index % 7], true), 
                        clear_left + base * width, clear_up - 2);
                base += 7;
            }
        
        /* mark year */
        } else if (index == HIGHLIGHT_YEAR) {
            g.setColor(Const.COLOR_DEF_FONT);
            int widthHeadline = GraphicUtils.getStringWidth(g, Const.FONT_YEAR_NUMBER, 
                    "" + matrix[10][3].getDate().get(java.util.Calendar.YEAR));
            g.fillPolygon(
                    new int[]{this.getWidth() / 2 - 55, this.getWidth() / 2 - 60, this.getWidth() / 2 - 60},
                    new int[]{clear_up - 30, clear_up - 35, clear_up - 25}, 3);
            g.fillPolygon(
                    new int[]{this.getWidth() / 2 - 50 + widthHeadline + 5, this.getWidth() / 2 - 50 + widthHeadline + 10, this.getWidth() / 2 - 50 + widthHeadline + 10},
                    new int[]{clear_up - 30, clear_up - 35, clear_up - 25}, 3);
        }
    }
    
    @Override
    protected void fillCalendarContent(Graphics g, List<Event> events, Date date) {
        int viewedYear = date.get(java.util.Calendar.YEAR);
        
        /* print year label */
        g.setColor(Color.BLACK);
        g.setFont(Const.FONT_YEAR_NUMBER);
        g.drawString("" + viewedYear, this.getWidth() / 2 - 50, clear_up - 20);

        /* for each line (=month) do... */
        for (int month = 0; month < rows; month++) {
            date = new Date(viewedYear, month, 1);
            
            /* determine the starting column (based on the day of week) */
            int startCol = 0;
            for (int i = 0; i < 7; i++) {
                if (daysOfWeek[i] == date.get(Date.DAY_OF_WEEK)) {
                    startCol = i;
                }
            }
            int maxDays = date.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            int kw_start = getWeekNo(date);
            int kw_end;
            for (int day = 0; day < maxDays; day++) {
                date.add(java.util.Calendar.DAY_OF_MONTH, day == 0 ? 0 : 1);

                matrix[startCol + day][month] = new CellYear(this, startCol + day, month, date.clone());

                if (markedDates.contains(date)) {
                    LOG.fine("selection contains: " + day + "."
                            + month + "." + viewedYear);
                    matrix[startCol + day][month].setSelected(true);
                }

                /* Print week numbers */
                if (day == maxDays - 1) {
                    kw_end = getWeekNo(date);
                    g.setFont(Const.FONT_YEAR_KW_NUMBERS.deriveFont((float) fontsizeYear));
                    g.setColor(Const.COLOR_DEF_FONT);
                    g.drawString("" + kw_start + "-" + kw_end,
                            cols * width + clear_left + 3, clear_up - 2
                                    + (month + 1) * height);
                }

                /* Register events */
                for (Event e : events) {
                    if (e.match(date)) {
                        matrix[startCol + day][month].addEvent(e);
                    }
                }

                matrix[startCol + day][month].paint(g, new Dimension(width, height));
            }
        }    
    }
    
    @Override
    protected void markColumn(int col) {
        List<Cell> toMark = new ArrayList<Cell>();
        int base = col % 7;
        while (base < cols) {
            for (int i = 0; i < rows; i++) {
                if (matrix[base][i] != null) {
                    toMark.add(matrix[base][i]);
                }
            }
            base += 7;
        }
        mark(toMark);
        highlightHeadline(getGraphics(), col + 30, 0);
    }
    
    @Override
    protected void markIt(Point start, Point end) {
        markWeekYear(start, end);
    }
    
    @Override
    protected boolean mouseOnCalendarWeekLabel(int xPos, int yPos, int colIndex, int rowIndex) {
        return colIndex >= cols && rowIndex < rows;
    }
}