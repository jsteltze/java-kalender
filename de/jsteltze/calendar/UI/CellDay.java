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

import javax.swing.ImageIcon;

import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.EventCategories;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.common.GraphicUtils;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.Date.PrintFormat;

/**
 * Single cell (date) within calendar canvas daily view.
 * @author Johannes Steltzer
 *
 */
public class CellDay extends Cell {

    /** The point where the actual drawing of the calendar cells starts. */
    public static final Point GAP = new Point(30, 20);
    
    /** Number of columns in the matrix. */
    public static final int COLS = 2;
    
    /** Number of rows in the matrix. */
    public static final int ROWS = 25;

    /**
     * Construct a new cell.
     * @param owner - CalendarFrame object where to paint
     * @param col - Column index within matrix 
     * @param row - Row index within matrix
     * @param date - Date of this cell
     */
    public CellDay(CalendarCanvas owner, int col, int row, Date date) {
        super(owner, col, row, date);
        headerHeight = 0;
        gap = GAP;
    }
    
    @Override
    public Event getEventUnderCursor(Point cursor, Dimension cellSpace) {
        int startX = 3;
        Font currentFont = Const.FONT_EVENT_NAME.deriveFont((float) fontsize);
        int widthPoints = GraphicUtils.getStringWidth(canvas.getGraphics(), currentFont, "+99");
        cursor.x -= GAP.x + col * cellSpace.width;
        
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            int labelWidth = GraphicUtils.getStringWidth(canvas.getGraphics(), currentFont, event.getName());
            if (event.getCategory() != null) {
                /* icon size */
                labelWidth += fontsize - 1;
            }
            if (startX + labelWidth + 2 > cellSpace.width
                    || (i < events.size() - 1 
                            && startX + labelWidth + widthPoints + 2 > cellSpace.width)) {
                return null;
            }
            if (cursor.x >= startX && cursor.x <= (startX + labelWidth + 4)) {
                return event;
            }
            startX += labelWidth + 5;
        }
        
        return null;
    }
    
    @Override
    protected void paintEvent(Graphics x, Event event, int off, Dimension space) {
        int startY = GAP.y + row * space.height;
        int width = GraphicUtils.getStringWidth(x, event.getName());
        int iconSize = fontsize - 1;
        if (event.getCategory() != null) {
            /* icon size */
            width += iconSize;
        }
        
        /* paint event bar background */
        x.setColor(event.getGUI().isSelected() ? selectedColor : Color.white);
        x.fillRect(off, startY + 2, width + 2, space.height / 2);
        x.setColor(event.getGUI().isSelected() ? selectedColorD : Color.white);
        x.fillRect(off, startY + space.height / 2, width + 2, space.height / 2);
        
        /* paint event bar frame */
        x.setColor(event.getGUI().isSelected() ? Color.black : frameColor);
        x.drawRoundRect(off, startY + 1, width + 2, space.height - 2, 3, 3);
        
        /* paint category icon */
        int indent = 2;
        if (event.getCategory() != null) {
            ImageIcon icon = (ImageIcon) EventCategories.getScaledIcon(event.getCategory(), iconSize);
            x.drawImage(icon.getImage(), off + 1, startY + 2, canvas);
            indent += iconSize;
        }
        
        /* set the color for the event name label */
        if (event.getType().isHoliday()) {
            x.setColor(holidayFtColor);
        } else if (event.getGUI().isSelected()) {
            x.setColor(Color.black);
        } else {
            x.setColor(ftColor);
        }
        
        x.drawString(event.getName(), off + indent, startY + space.height - 2);
    }
    
    /**
     * Paint the header for a particular day.
     * @param x - Graphics to paint on
     * @param width - Available width
     */
    private void paintDayHeader(Graphics x, int width) {
        x.setColor(isSelected() ? selectedColor : Color.white);
        x.fillRect(GAP.x + 1 + col * width, GAP.y - 18, 279, 19);
        x.fillPolygon(
                new int[]{GAP.x + 280 + col * width, GAP.x + 280 + col * width, GAP.x + 299 + col * width},
                new int[]{GAP.y - 18, GAP.y + 1, GAP.y + 1}, 3);
        
        if (isSelected()) {
            x.setColor(selectedColor);
            x.drawLine(GAP.x + 1 + col * width, GAP.y + 1, GAP.x + (col + 1) * width - 1, GAP.y + 1);
            x.setColor(selectedColorB);
            x.drawLine(GAP.x + 1 + col * width, GAP.y - 18, GAP.x + 1 + col * width, GAP.y + 1);
            x.drawLine(GAP.x + 1 + col * width, GAP.y - 18, GAP.x + 279 + col * width, GAP.y - 18);
        }
        
        String header = "";
        long dayDiff = this.date.dayDiff();
        if (dayDiff == 0) {
            header += "Heute: ";
        } else if (dayDiff == -1) {
            header += "Gestern: ";
        } else if (dayDiff == 1) {
            header += "Morgen: ";
        } else if (dayDiff == 2) {
            header += "Übermorgen: ";
        }
        header += date.print(PrintFormat.DDD_DDMMYYYY);
        
        x.setColor(Color.black);
        x.setFont(Const.FONT_DAY_HEADER);
        x.drawString(header, GAP.x + col * width + 3, GAP.y - 4);
    }
    
    @Override
    public void paint(Graphics x, Dimension space) {
        x.setColor(isSelected() ? selectedColor : Color.white);
        x.fillRect(GAP.x + 1 + col * space.width, GAP.y + 1
                + row * space.height, space.width - 1, space.height - 1);
        
        if (isSelected()) {
            x.setColor(selectedColorD);
            x.drawLine(GAP.x + 1 + col * space.width, GAP.y + (row + 1) * space.height - 1, 
                    GAP.x + (col + 1) * space.width - 1, GAP.y + (row + 1) * space.height - 1);
            x.drawLine(GAP.x + (col + 1) * space.width - 1, GAP.y + row * space.height + 1, 
                    GAP.x + (col + 1) * space.width - 1, GAP.y + (row + 1) * space.height - 1);
            x.setColor(selectedColorB);
            x.drawLine(GAP.x + 1 + col * space.width, GAP.y + row * space.height + 1, 
                    GAP.x + (col + 1) * space.width - 1, GAP.y + row * space.height + 1);
            x.drawLine(GAP.x + col * space.width + 1, GAP.y + row * space.height + 1, 
                    GAP.x + col * space.width + 1, GAP.y + (row + 1) * space.height - 1);
        }
        
        if (row == 0) {
            paintDayHeader(x, space.width);
        }

        fontsize = space.height - 2;
        
        paintEvents(x, space, true, false);
    }
}