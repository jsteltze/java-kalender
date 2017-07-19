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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.Event.EventType;
import de.jsteltze.calendar.EventCategories;
import de.jsteltze.calendar.config.ColorSet;
import de.jsteltze.calendar.config.Configuration.BoolProperty;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.common.ColorUtil;
import de.jsteltze.common.GraphicUtils;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.Date.PrintFormat;
import de.jsteltze.common.calendar.Moon;
import de.jsteltze.common.calendar.Moon.MoonState;
import de.jsteltze.common.calendar.Zodiac;

/**
 * Single cell (date) within calendar canvas.
 * @author Johannes Steltzer
 *
 */
public abstract class Cell {
    
    /** Saturation ratio for building a colored bar. */
    protected static final float SATURATION_DIFF = .1f;
    
    /** Brightness ratio for making events transparent. */
    protected static final float TRANSPARENT_DIFF = .8f;
    
    /** Saturation difference for today color effect. */
    protected static final float TODAY_COLOR_SATURATION_DIFF = .2f;
    
    /** Colors to use in certain situations (depending on the user settings). */
    protected static Color weekendColor, holidayBgColor, 
            holidayFtColor, ftColor, todayColor, 
            selectedColor, selectedColorD, selectedColorB;
    
    /** Colors to use in certain situations (depending on the user settings). */
    private static Color frameColorNonTransparent, frameColorTransparent;
    
    /** Color of the event frame. */
    protected Color frameColor;

    /** Column of this cell within the matrix. */
    protected int col;
    
    /** Row of this cell within the matrix. */
    protected int row;
    
    /** Date this cell represents (can be null). */
    protected Date date;
    
    /** Is the date of this cell todays date? */
    private boolean today;
    
    /** Is one event of this cell a holiday? */
    private boolean holiday;
    
    /** Does the date belong to a weekend? */
    private boolean weekend;
    
    /** Is this cell selected? */
    private boolean selected;
    
    /** Number of cell neighbors left and right in this row. */
    protected int numLeftNeighbors, numRightNeighbors;
    
    /** All events on this cells date. */
    protected List<Event> events;
    
    /** The canvas on which to paint. */
    protected CalendarCanvas canvas;
    
    /** The fontsize for event labels and the maximum number of events bars that fit in a cell. */
    protected int fontsize, max_lines;
    
    /** Moon phase (of the date -> for this cell). */
    private MoonState moonPhase;
    
    /** Zodiac (of the date -> for this cell). */
    private Zodiac zodiac;
    
    protected int headerHeight;
    protected Point gap;
    
    /** 
     * Map of events and corresponding index where to paint the event panel.
     * This map is static because the subsequent cell needs to know what index to use.
     */
    protected static List<Map<Event, Integer>> indexMap = new ArrayList<Map<Event, Integer>>();
    static {
        // we use 12 as maximum number of rows, because the yearly view contains the most rows affected by this problem
        for (int i = 0; i < Date.MONTHS_OF_YEAR; i++) {
            indexMap.add(new HashMap<Event, Integer>());
        }
    }
    
    /** 
     * Map of index and which event is on that index.
     * This map is private because each cell has its own map.
     */
    protected Map<Integer, Event> eventMap = new HashMap<Integer, Event>();

    /**
     * Construct a new cell.
     * @param owner - CalendarFrame object where to paint
     * @param col - Column index within matrix 
     * @param row - Row index within matrix
     * @param date - Date of this cell
     */
    public Cell(CalendarCanvas owner, int col, int row, Date date) {
        this.col = col;
        this.row = row;
        this.date = date;
        this.canvas = owner;
        this.today = false;
        this.holiday = false;
        this.weekend = false;
        this.selected = false;
        this.moonPhase = MoonState.none;
        this.zodiac = null;
        
        if (this.date != null) {
            if (this.date.sameDateAs(new Date())) {
                today = true;
            }
            
            // set the moon phase
            if (canvas.getOwner().getConfig().getProperty(BoolProperty.ShowMoon)) {
                moonPhase = Moon.getMoonPhase(this.date);
            }
            
            // set the zodiac
            if (canvas.getOwner().getConfig().getProperty(BoolProperty.ShowZodiac)) {
                zodiac = Zodiac.getStartsByDate(this.date);
            }
            
            int dayOfWeek = date.get(java.util.Calendar.DAY_OF_WEEK);
            this.weekend = dayOfWeek == java.util.Calendar.SATURDAY || dayOfWeek == java.util.Calendar.SUNDAY;
        }
        
        /*
         * In case of yearly view: Weekends will be set in CalendarCanvas 
         * since it is too complicated to calculate here...
         */

        events = new ArrayList<Event>();
    }

    /**
     * Returns this cells date.
     * @return this cells date.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Returns true if this cells date is today.
     * @return true if this cells date is today.
     */
    public boolean isToday() {
        return today;
    }

    /**
     * Returns true if one of this cells events is a holiday.
     * @return true if one of this cells events is a holiday.
     */
    public boolean isHoliday() {
        return holiday;
    }

    /**
     * Returns true if the cells date is a weekend.
     * @return true if the cells date is a weekend.
     */
    public boolean isWeekend() {
        return weekend;
    }

    /**
     * Returns true if this cell is currently selected.
     * @return true if this cell is currently selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Returns all events of this cell (including holidays).
     * @return all events of this cell.
     */
    public List<Event> getEvents() {
        return events;
    }
    
    /**
     * Returns the cells moon phase (only calculated if moon is activated).
     * @return the cells moon phase.
     */
    public MoonState getMoonphase() {
        return moonPhase;
    }
    
    /**
     * Returns the cells Zodiac (only calculated if moon is activated).
     * @return the cells Zodiac.
     */
    public Zodiac getZodiac() {
        return zodiac;
    }
    
    /**
     * Returns the event under the mouse cursor position.
     * @param cursor - Mouse cursor position
     * @param cellSpace - Dimension of the cell
     * @return The event under the current mouse position (if exists) or
     *         null.
     */
    public Event getEventUnderCursor(Point cursor, Dimension cellSpace) {
        int yCoord = cursor.y;
        
        yCoord -= (gap.y + headerHeight + 2 + row * cellSpace.height);
        if (yCoord < 0) {
            return null;
        }
        int index = yCoord / fontsize;
        return eventMap.get(index);
    }

    /**
     * Set selected flag.
     * @param x - True if this cell is currently selected
     */
    public void setSelected(boolean x) {
        selected = x;
        paint(canvas.getGraphics(), canvas.calcDim());
    }

    /**
     * Add an event to this cell.
     * @param x - Event to add
     */
    public void addEvent(Event x) {
        if (x.getType() == EventType.holiday_law) {
            this.holiday = true;
            events.add(0, x);
        } else {
            events.add(x);
        }
//        paint(canvas.getGraphics(), canvas.calcDim());
    }

    /**
     * Remove an event from this cell.
     * @param x - Event to remove
     */
    public void removeEvent(Event x) {
        /* Was this the last holiday? */
        events.remove(x);

        holiday = false;
        for (Event e : events) {
            if (e.getType() == EventType.holiday_law) {
                holiday = true;
                break;
            }
        }

        paint(canvas.getGraphics(), canvas.calcDim());
    }
    
    /**
     * Check if this cell contains a specific event.
     * @param e - Event to check
     * @return True if this cell contains the event, false otherwise.
     */
    public boolean containsEvent(Event e) {
        return events.contains(e);
    }
    
    /**
     * Paint all events (if sufficient space) within this cell.
     * @param x - Graphics to paint on
     * @param space - Available space in this cell
     * @param paintNumberOfHidden - True for printing the number of remaining (hidden) events
     * @param vertical - Align events vertically? (else horizontally)
     * if the space is not sufficient to paint all events. If false events will be painted
     * as space is available, remaining events will be hidden.
     */
    protected void paintEvents(Graphics x, Dimension space, boolean paintNumberOfHidden, boolean vertical) {
        int startX = vertical ? gap.x + col * space.width : 3;
        int startY = gap.y + row * space.height;
        
        Collections.sort(events);
        eventMap.clear();
        x.setFont(Const.FONT_EVENT_NAME.deriveFont((float) fontsize));
        
        int selectedIndex = -1;
        int widthPoints = GraphicUtils.getStringWidth(x, "+99");
        int width;
        Event selectedEvent = null;
        
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            int paintIndex = i;
            
            if (vertical) {
                /*
                 * align events vertically (one each row)
                 */
                
                if (paintNumberOfHidden) {
                    /* print +<mun> if not enough space for the remaining events */
                    if (i == max_lines - 1 && i != events.size() - 1) {
                        x.setColor(ftColor);
                        x.drawString("+" + (events.size() - i), 
                                startX + 1, startY + headerHeight + 1 + (i + 1) * fontsize);
                        break;
                    }
                } else if (i >= max_lines) {
                    /* paint little arrow in the corner to indicate that there are more events */
                    int arrowSize = 6;
                    x.setColor(frameColorNonTransparent);
                    x.fillPolygon(
                            new int[]{startX + space.width - arrowSize, startX + space.width, startX + space.width},
                            new int[]{startY + space.height, 
                                    startY + space.height - arrowSize, startY + space.height}, 3);
                    
                    break;
                }
                
                /*
                 * INDEX HANDLING
                 */
                if (event.getEndDate() != null) {
                    if (numLeftNeighbors == 0 || event.getDate().sameDateAs(date)) {
                        /* clear the index if this is the first cell in a row */
                        indexMap.get(row).remove(event);
                    }
                    
                    if (indexMap.get(row).containsKey(event)) {
                        /* use the previously saved index if the map contains this event */
                        paintIndex = indexMap.get(row).get(event);
                    }
                } 
                if (event.getEndDate() == null || !indexMap.get(row).containsKey(event)) {
                    /* search first empty index */
                    paintIndex = 0;
                    while (eventMap.containsKey(paintIndex)) {
                        paintIndex++;
                    }
                    /* add event with index to the map if the map does not contain the event */
                    indexMap.get(row).put(event, paintIndex);
                }
                /* add event with specific index to event map */
                eventMap.put(paintIndex, event);
            
            
            
            } else {
                /*
                 * align events horizontally (one next to each other)
                 */
                
                width = GraphicUtils.getStringWidth(x, event.getName()) + 1;
                
                if (event.getCategory() != null) {
                    // icon size
                    width += fontsize - 1;
                }
                
                if ((startX + width + 2 > space.width)
                        || (paintNumberOfHidden && i < events.size() - 1 
                                && startX + width + widthPoints + 2 > space.width)) {
                    // paint number of event that dont fit
                    if (paintNumberOfHidden) {
                        x.drawString("+" + (events.size() - i), 
                                gap.x + col * space.width + startX, gap.y + (row + 1) * space.height - 2);
                    }
                    break;
                }
                
                paintIndex = gap.x + col * space.width + startX;
                startX += width + 4;
            }
            
            /* if there is a selected event remember and skip it -> it will be painted at last */
            if (event.getGUI().isSelected()) {
                selectedIndex = paintIndex;
                selectedEvent = event;
                continue;
            }
            
            paintEvent(x, event, paintIndex, space);
        }
        
        /* if we have a selected event paint it again -> painting always on top */
        if (selectedIndex != -1) {
            paintEvent(x, selectedEvent, selectedIndex, space);
        }
    }
    
//    /**
//     * Draw an image on the canvas.
//     * @param g - Graphics to paint on
//     * @param img - Image to draw
//     * @param x - X position (upper left point)
//     * @param y - Y position (upper left point)
//     * @param bgColor - Background color (for transparent images)
//     */
//    protected void drawImage(Graphics g, Image img, int x, int y, Color bgColor) {
//        g.drawImage(img, x, y, bgColor, canvas);
//    }
    
    /**
     * Initialize colors according to the current config.
     * @param canvas - Current canvas to receive settings from
     */
    public static void initColors(CalendarCanvas canvas) {
        weekendColor = canvas.getOwner().getConfig().getColors()[ColorSet.WEEKEND];
        holidayBgColor = canvas.getOwner().getConfig().getColors()[ColorSet.HOLIDAY];
        holidayFtColor = canvas.getOwner().getConfig().getColors()[ColorSet.FONT_HOLIDAY];
        todayColor = canvas.getOwner().getConfig().getColors()[ColorSet.TODAY];
        ftColor = canvas.getOwner().getConfig().getColors()[ColorSet.FONT];
        selectedColor = canvas.getOwner().getConfig().getColors()[ColorSet.SELECTED];
        selectedColorD = ColorUtil.addSaturation(selectedColor, SATURATION_DIFF); // little darker
        selectedColorB = ColorUtil.addSaturation(selectedColor, -SATURATION_DIFF); // little brighter
        
        frameColorNonTransparent = Const.COLOR_DEF_FONT;
        frameColorTransparent = ColorUtil.addBrightness(frameColorNonTransparent, TRANSPARENT_DIFF);
    }

    /**
     * Paint this cell on a canvas.
     * @param x - Graphics object to paint on
     * @param space - Available space
     */
    public abstract void paint(Graphics x, Dimension space);
    
    /**
     * Paint an selectable event on the this cell in the specific view.
     * @param x - Graphics to paint on
     * @param event - Event to paint
     * @param i - Index (vertical position) where to paint the event
     * @param space - Available space of this cell
     */
    protected void paintEvent(Graphics x, Event event, int i, Dimension space) {
        int startX = gap.x + col * space.width;
        int startY = gap.y + row * space.height;
        Color c1 = event.getGUI().isSelected() ? selectedColor : Color.white;
        Color c2 = event.getGUI().isSelected() ? selectedColorD : Color.white;
        int timeShift = 0;
        
        frameColor = event.getGUI().isTransparent() ? frameColorTransparent : frameColorNonTransparent;
        frameColor = event.getGUI().isSelected() ? Color.black : frameColor;
        
        /* group multi-day events */
        if (event.getEndDate() != null) {
            boolean remLeftBorder = false, remRightBorder = false;
            
            if (event.getDate().sameDateAs(date)) {
                
                /*
                 * For multiday events: this is the left border
                 */
                
                /* mark as selected */
                if (event.getGUI().isSelected()) {
                    x.setColor(c1);
                    x.fillRect(startX + 3, startY + 3 + i * fontsize + headerHeight, 
                            space.width - 3, fontsize / 2);
                    x.setColor(c2);
                    x.fillRect(startX + 3, startY + 3 + i * fontsize + fontsize / 2 + headerHeight, 
                            space.width - 3, fontsize / 2);
                }
                
                x.setColor(frameColor);
                x.drawRoundRect(startX + 2, startY + 2 + headerHeight + i * fontsize, 
                        space.width - 2, fontsize, 4, 4);
                
                if (numRightNeighbors > 0) {
                    remRightBorder = true;
                } else {
                    // line break on right side
                    x.setColor(Const.COLOR_DEF_FONT);
                    x.drawLine(startX + space.width, startY + 2 + headerHeight + i * fontsize, 
                            startX + space.width, startY + headerHeight + (i + 1) * fontsize + 2);
                }
            
            } else if (event.getEndDate().sameDateAs(date)) {
                
                /*
                 * For multiday events: this is the right border
                 */
                
                if (event.getGUI().isSelected()) {
                    x.setColor(c1);
                    x.fillRect(startX + 1, startY + 3 + headerHeight + i * fontsize, 
                            space.width - 2, fontsize / 2);
                    x.setColor(c2);
                    x.fillRect(startX + 1, startY + 3 + headerHeight + i * fontsize + fontsize / 2, 
                            space.width - 2, fontsize / 2);
                }
                    
                x.setColor(frameColor);
                x.drawRoundRect(startX, startY + 2 + headerHeight + i * fontsize, 
                        space.width - 2, fontsize, 2, 2);
                
                if (numLeftNeighbors > 0) {
                    remLeftBorder = true;
                } else {
                    // line break on left side
                    x.setColor(Const.COLOR_DEF_FONT);
                    x.drawLine(startX, startY + 2 + headerHeight + i * fontsize, 
                            startX, startY + headerHeight + (i + 1) * fontsize + 2);
                }
            
            } else {
                
                /*
                 * For multiday events: this is in the middle
                 */
                
                if (event.getGUI().isSelected()) {
                    x.setColor(c1);
                    x.fillRect(startX + 1, startY + 3 + headerHeight + i * fontsize, 
                            space.width - 1, fontsize / 2);
                    x.setColor(c2);
                    x.fillRect(startX + 1, startY + 3 + headerHeight + i * fontsize + fontsize / 2, 
                            space.width - 1, fontsize / 2);
                }
                
                x.setColor(frameColor);
                x.drawLine(startX + 1, startY + 2 + headerHeight + i * fontsize, 
                        startX + space.width - 1, startY + 2 + headerHeight + i * fontsize);
                x.drawLine(startX + 1, startY + 2 + headerHeight + (i + 1) * fontsize, 
                        startX + space.width - 1, startY + 2 + headerHeight + (i + 1) * fontsize);
                
                if (numRightNeighbors > 0) {
                    remRightBorder = true;
                }
                if (numLeftNeighbors > 0) {
                    remLeftBorder = true;
                }
            }
            
            if (remRightBorder) {
                /* remove right border of rectangle */
                x.setColor(c1);
                x.drawLine(startX + space.width, startY + 3 + headerHeight + i * fontsize, 
                        startX + space.width, startY + headerHeight + i * fontsize + fontsize / 2 + 2);
                x.setColor(c2);
                x.drawLine(startX + space.width, startY + 3 + headerHeight + i * fontsize + fontsize / 2, 
                        startX + space.width, startY + 1 + headerHeight + (i + 1) * fontsize);
                x.setColor(frameColor);
                x.drawLine(startX + space.width, startY + 2 + headerHeight + i * fontsize, 
                        startX + space.width, startY + 2 + headerHeight + i * fontsize);
                x.drawLine(startX + space.width, startY + 2 + headerHeight + (i + 1) * fontsize, 
                        startX + space.width, startY + 2 + headerHeight + (i + 1) * fontsize);
            }
            if (remLeftBorder) {
                /* remove left border of rectangle */
                x.setColor(c1);
                x.drawLine(startX, startY + 3 + headerHeight + i * fontsize, 
                        startX, startY + 1 + headerHeight + i * fontsize + fontsize / 2 + 1);
                x.setColor(c2);
                x.drawLine(startX, startY + 3 + headerHeight + i * fontsize + fontsize / 2, 
                        startX, startY + 1 + headerHeight + (i + 1) * fontsize);
                x.setColor(frameColor);
                x.drawLine(startX, startY + 2 + headerHeight + i * fontsize, 
                        startX, startY + 2 + headerHeight + i * fontsize);
                x.drawLine(startX, startY + 2 + headerHeight + (i + 1) * fontsize, 
                        startX, startY + 2 + headerHeight + (i + 1) * fontsize);
            }
        
        /* single day events */
        } else {
        
            if (event.getDate().hasTime()) {
                int timeSizeSub = 7;
                
                // print event time (but only if the font size is still readable)
                if (fontsize - timeSizeSub > 7) { 
                    String timeString = event.getDate().print(PrintFormat.HHmm);
                    x.setFont(x.getFont().deriveFont((float) fontsize - timeSizeSub));
                    x.setColor(frameColor);
                    x.drawString(timeString, startX + 1, 
                            startY + 3 + headerHeight + (i + 1) * fontsize - timeSizeSub);
                    timeShift = GraphicUtils.getStringWidth(x, timeString) - 1;
                    x.setFont(x.getFont().deriveFont((float) fontsize));
                }
            }
            
            /* mark as selected */
            if (event.getGUI().isSelected()) {
                x.setColor(c1);
                x.fillRect(startX + 3 + timeShift, startY + 3 + headerHeight + i * fontsize, 
                        space.width - 5 - timeShift, fontsize / 2);
                x.setColor(c2);
                x.fillRect(startX + 3 + timeShift, startY + 3 + headerHeight + i * fontsize + fontsize / 2,
                        space.width - 5 - timeShift, fontsize / 2);
            }
                
            x.setColor(frameColor);
            x.drawRoundRect(startX + 2 + timeShift, startY + 2 + headerHeight + i * fontsize, 
                    space.width - 4 - timeShift, fontsize, /*fontsize /*/ 2, /*fontsize /*/ 2);
        }
        
        // print the event name if font size is large enough
        if (fontsize > 5) {
            printEventName(event, x, space, i, timeShift);
        }
    }
    
    /**
     * Print the name of an event over affected cells for monthly 
     * and weekly view.
     * @param event - Event to label
     * @param x - Graphics to paint on
     * @param space - Available space per cell
     * @param index - Index (row) of this event 
     * @param indent - Space to leave clear on the left side
     */
    protected void printEventName(Event event, Graphics x, Dimension space, int index, int indent) {
        /* set the color for the event name label */
        if (event.getGUI().isSelected()) {
            x.setColor(Color.black);
        } else if (event.getType().isHoliday()) {
            x.setColor(holidayFtColor);
        } else {
            x.setColor(ftColor);
        }
        /* make brighter if event is transparent */
        if (event.getGUI().isTransparent()) {
            x.setColor(ColorUtil.addBrightness(x.getColor(), TRANSPARENT_DIFF));
        }
        
        int relevantCols = 1;
        int startCol = col;
        int startText = indent + 1;
        int iconSize = fontsize - 1;
        if (event.getEndDate() != null) {
            int dayDiffBefore = (int) date.dayDiff(event.getDate());
            int dayDiffAfter = (int) event.getEndDate().dayDiff(date);
            int relevantColsBefore;
            if (numLeftNeighbors > dayDiffBefore) {
                relevantColsBefore = dayDiffBefore;
                startText += 2;
            } else {
                relevantColsBefore = numLeftNeighbors;
            }
            startCol -= relevantColsBefore;
            int relevantColsAfter = dayDiffAfter > numRightNeighbors ? numRightNeighbors : dayDiffAfter;
            
            relevantCols += relevantColsBefore + relevantColsAfter;
        }
        
        int textSpace = space.width * relevantCols - startText - 1;
        startText += gap.x + space.width * startCol;
        int startY = gap.y + row * space.height + headerHeight;
        
        // if there is a category icon to paint -> reduce the space
        if (event.getCategory() != null) {
            textSpace -= iconSize;
        }
        
        String cuttedName = cut(event.getName(), textSpace, x);
        if (cuttedName.equals(event.getName())) {
            int neededSpace = GraphicUtils.getStringWidth(x, cuttedName);
            startText += ((textSpace - neededSpace) / 2);
        } else {
            startText += 2;
        }
        
        // paint the category icon
        if (event.getCategory() != null) {
            ImageIcon icon = (ImageIcon) EventCategories.getScaledIcon(event.getCategory(), iconSize);
            x.drawImage(icon.getImage(), ++startText, startY + index * fontsize + 3, canvas);
            startText += iconSize;
        }
        
        x.drawString(cuttedName, startText, startY + (index + 1) * fontsize + 1);
    }
    
    /**
     * Cuts a string to fit a specific width. If the string needs to be cut,
     * '...' will be appended.
     * @param orig - Original string
     * @param width - Available width
     * @param g - Graphics object (contains the font to apply)
     * @return Original string if enough space available, cutted
     * string otherwise.
     */
    public static String cut(String orig, int width, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        String tmp = orig;
        int len = orig.length();
        while (fm.stringWidth(tmp) > width - 2 && len > 0) {
            tmp = orig.substring(0, --len) + "...";
        }
        return tmp.equals("...") ? orig.substring(0, 1) : tmp;
    }
}