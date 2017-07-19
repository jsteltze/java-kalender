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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Calendar;

import javax.swing.ImageIcon;

import de.jsteltze.calendar.config.Const;
import de.jsteltze.common.ColorUtil;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.Moon;
import de.jsteltze.common.calendar.Moon.MoonState;

/**
 * Single cell (date) within calendar canvas monthly view.
 * @author Johannes Steltzer
 *
 */
public class CellMonth extends Cell {

    /** The point where the actual drawing of the calendar cells starts. */
    public static final Point GAP = new Point(5, 44);
    
    /** Number of columns in the matrix. */
    public static final int COLS = 15;
    
    /** Number of rows in the matrix. */
    public static final int ROWS = 6;
    
    /** Header height (for the Mo-So labels). */
    public static final int HEADER_HEIGHT = 25;

    /**
     * Construct a new cell.
     * @param owner - CalendarFrame object where to paint
     * @param col - Column index within matrix 
     * @param row - Row index within matrix
     * @param date - Date of this cell
     */
    public CellMonth(CalendarCanvas owner, int col, int row, Date date) {
        super(owner, col, row, date);
        
        int daysToEnd = date.getDaysToEndOfMonth();
        int todayDay  = date.get(Calendar.DAY_OF_MONTH);
        int actCol    = col > 6 ? col - 7 : col;
        
        this.numLeftNeighbors  = todayDay >= actCol ? actCol : todayDay - 1;
        this.numRightNeighbors = actCol + daysToEnd > 6 ? 6 - actCol : daysToEnd;
        
        headerHeight = HEADER_HEIGHT;
        gap = GAP;
    }
    
    @Override
    public void paint(Graphics x, Dimension space) {
        if (date == null) {
            return;
        }

        Color c1, c2;
        int startX = GAP.x + col * space.width;
        int startY = GAP.y + row * space.height;
            
        /* paint cell frame */
        x.setColor(Const.COLOR_DEF_FONT);
        x.drawRect(startX, startY, space.width, space.height);
        
        /* paint selected or white ground */
        if (isSelected()) {
            c1 = selectedColor;
            c2 = selectedColorD;
        } else {
            c1 = Color.white;
            c2 = Color.white;
        }
        x.setColor(c1);
        x.fillRect(startX + 1, startY + 1, space.width - 1, 13);
        x.setColor(c2);
        x.fillRect(startX + 1, startY + 14, space.width - 1, 12);

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
        x.fillRect(startX + 6, startY + 6, space.width - 11, 8);
        x.setColor(c2);
        x.fillRect(startX + 6, startY + 14, space.width - 11, 7);
        
        if (isToday()) {
            ColorUtil.fillRectEffectful(x, startX + 1, startY + HEADER_HEIGHT + 1, 
                    space.width - 1, space.height - (HEADER_HEIGHT + 1), todayColor, TODAY_COLOR_SATURATION_DIFF);
            
            /* color for day number (today!) */
            x.setColor(Color.BLACK);
            
        } else {
            c1 = Color.white;
            x.setColor(c1);
            x.fillRect(startX + 1, startY + HEADER_HEIGHT + 1, 
                    space.width - 1, space.height - (HEADER_HEIGHT + 1));
            
            /* color for day number (not today) */
            x.setColor(Const.COLOR_FONT_DAY_NON_TODAY);
        }
        
        /* print number of day */
        x.setFont(Const.FONT_MONTH_DAY);
        x.drawString("" + date.get(Calendar.DAY_OF_MONTH), startX + 7, 
                startY + 20);
        
        /* paint moon phase */
        Color moonBg = isHoliday() ? holidayBgColor : isWeekend() ? weekendColor : Color.white;
        Moon.paint(getMoonphase(), x, //moonSelected ? canvas.getOwner().getConfig().getColors()[ColorSet.SELECTED] : 
            Color.gray, moonBg, new Point(startX + space.width - 14, startY + 10), 8);
        
        /* paint Zodiac (only if no moon yet painted) */
        if (getMoonphase() == MoonState.none && getZodiac() != null) {
            ImageIcon zodiac = new ImageIcon(CellWeek.class.getResource("/media/zodiac/" 
                    + getZodiac().name() + ".png"));
            BufferedImage zodiacBi = ColorUtil.img2BuffImg(zodiac.getImage());
            ColorUtil.transform(zodiacBi, Color.black, Color.gray);
            Image newimg = zodiacBi.getScaledInstance(14, 14, Image.SCALE_SMOOTH);
            x.drawImage(newimg, startX + space.width - 16, startY + 7, null);
        }

        /* print events */
        if (space.height > 72) {
            fontsize = 13;
        } else if (space.height > 62) {
            fontsize = 12;
        } else if (space.height > 52) {
            fontsize = 11;
        } else {
            fontsize = 10;
        }
        
        max_lines = (space.height - 27) / fontsize;
        if (max_lines < 1) {
            return;
        }

        /* paint all events in this cell */
        paintEvents(x, space, false, true);
    }
}
