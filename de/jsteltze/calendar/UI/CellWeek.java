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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.Event.EventType;
import de.jsteltze.calendar.EventCategories;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.common.ColorUtil;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.Date.PrintFormat;
import de.jsteltze.common.calendar.Moon;
import de.jsteltze.common.calendar.Moon.MoonState;

/**
 * Single cell (date) within calendar canvas weekly view.
 * @author Johannes Steltzer
 *
 */
public class CellWeek extends Cell {

    /** The point where the actual drawing of the calendar cells starts. */
    public static final Point GAP = new Point(5, 24);
    
    /** Number of columns in the matrix. */
    public static final int COLS = 7;
    
    /** Number of rows in the matrix. */
    public static final int ROWS = 2;
    
    /** Header height (for the Mo-So labels). */
    public static final int HEADER_HEIGHT = 40;

    /**
     * Construct a new cell.
     * @param owner - CalendarFrame object where to paint
     * @param col - Column index within matrix 
     * @param row - Row index within matrix
     * @param date - Date of this cell
     */
    public CellWeek(CalendarCanvas owner, int col, int row, Date date) {
        super(owner, col, row, date);
        
        this.numLeftNeighbors  = col;
        this.numRightNeighbors = 6 - col;
        
        headerHeight = HEADER_HEIGHT;
        gap = GAP;
    }
    
    @Override
    public void paint(Graphics x, Dimension space) {
        int startX = GAP.x + col * space.width;
        int startY = GAP.y + row * space.height;
        
        x.setColor(Color.white);
        x.fillRect(startX + 1, startY + HEADER_HEIGHT + 1, space.width - 1,
                space.height - HEADER_HEIGHT - 1);

        /* print holiday over weekend */
        if (isSelected()) {
            x.setColor(selectedColor);
        } else if (isHoliday()) {
            x.setColor(holidayBgColor);
        } else if (isWeekend()) {
            x.setColor(weekendColor);
        } else {
            x.setColor(Color.white);
        }
        
        x.fillRect(startX + 1, startY + 1, space.width - 1, HEADER_HEIGHT - 1);

        if (isToday()) {
            ColorUtil.fillRectEffectful(x, startX + 1, startY + 1 + HEADER_HEIGHT, 
                    space.width - 1, space.height - HEADER_HEIGHT - 1, todayColor, TODAY_COLOR_SATURATION_DIFF);
            
            /* font for date label (today!) */
            x.setFont(Const.FONT_WEEK_HEADER.deriveFont(Font.BOLD));
        } else {
            /* font for date label (not today) */
            x.setFont(Const.FONT_WEEK_HEADER);
        }
        
        /* print date label (centered) */
        FontMetrics fm = x.getFontMetrics();
        String dateLabel = date.print(PrintFormat.DMYYYY);
        int dateLabelWidth = fm.stringWidth(dateLabel);
        x.setColor(Color.black);
        x.drawString(dateLabel, startX + (space.width - dateLabelWidth) / 2,
                startY + (HEADER_HEIGHT / 2) - 4);
        
        /* paint moon phase */
        Moon.paint(getMoonphase(), x, Color.gray, isToday() ? todayColor : Color.white, 
                new Point(startX + space.width - 12, startY + space.height - 12), 10);
        
        /* paint zodiac */
        if (getZodiac() != null) {
            ImageIcon zodiac = new ImageIcon(CellWeek.class.getResource("/media/zodiac/" 
                    + getZodiac().name() + ".png"));
            BufferedImage zodiacBi = ColorUtil.img2BuffImg(zodiac.getImage());
            ColorUtil.transform(zodiacBi, Color.black, Color.gray);
            Image newimg = zodiacBi.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            x.drawImage(newimg, startX + 2, startY + space.height - 20, null);
        }

        /* print events */
        fontsize = space.height / 10;
        if (fontsize > 17) {
            fontsize = 17;
        } else if (fontsize < 10) {
            fontsize = 10;
        }
        
        max_lines = (space.height - HEADER_HEIGHT) / fontsize;
        if (getMoonphase() != MoonState.none || getZodiac() != null) {
            // one line less to be able to display the moon state or Zodiac
            max_lines--;
        }

        // determine the event to be painted in the header
        Event headerEvent = findHeaderEvent();
        if (headerEvent != null) {
            // (temporarily) remove the header event from the list of events
            events.remove(headerEvent);
        }
        
        /* paint all remaining events in this cell */
        paintEvents(x, space, true, true);
        
        /* paint the header event */
        if (headerEvent != null) {
            events.add(headerEvent);
            ImageIcon icon = (ImageIcon) EventCategories.getScaledIcon(headerEvent.getCategory(), 20);
            x.drawImage(icon.getImage(), startX + 1, startY + HEADER_HEIGHT / 2 - 2, canvas);
            x.setFont(Const.FONT_WEEK_HEADER);
            x.setColor(holidayFtColor);
            x.drawString(cut(headerEvent.getName(), space.width - 20, x), 
                    startX + 21, startY + HEADER_HEIGHT - 5);
            x.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontsize));
        }
    }
    
    /**
     * Returns the header event from this cells events. This event is to be painted in the
     * cells header. There can only be one header event.
     * @return the header event.
     */
    private Event findHeaderEvent() {
        // if there is a holiday by law, return it first
        for (Event e : events) {
            if (e.getType() == EventType.holiday_law) {
                return e;
            }
        }
        
        // if no holiday by law -> if there is a special holiday, return it
        for (Event e : events) {
            if (e.getType() == EventType.holiday_special) {
                return e;
            }
        }
        
        // no holidays -> no header event
        return null;
    }
}
