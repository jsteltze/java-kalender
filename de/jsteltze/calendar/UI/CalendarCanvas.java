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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.config.Configuration.IntProperty;
import de.jsteltze.calendar.config.enums.View;
import de.jsteltze.calendar.frames.TabelOfEventsMulitDay;
import de.jsteltze.common.Log;
import de.jsteltze.common.calendar.Date;

/**
 * Canvas on which to paint calendar contents. This class cannot be instanciated.
 * The concrete extensions for daily, weekly, monthly or yearly view must be used.
 * @author Johannes Steltzer
 *
 */
public abstract class CalendarCanvas 
    extends JComponent 
    implements MouseListener, MouseMotionListener, KeyListener, PopupMenuListener {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** In case of dragged mouse movement: starting position. */
    private Point mouseSrc;
    
    /** In case of dragged mouse movement: reverse movement. */
    protected boolean reverse;
    
    /** Is ctrl pressed? */
    private boolean strgPressed;
    
    /** Selected dates (cells). */
    protected List<Date> markedDates;
    
    /** Selected dates (cells) by the use of STRG multi-selection. */
    private List<Date> strgDates;
    
    /**
     * All cells of this current view. Each view can be
     * divided into cells, arranged as a table. Each
     * cell may represent a date. This makes it easier
     * to address a single date or a set of dates.
     */
    protected Cell[][] matrix;
    
    /**
     * The cell the mouse is currently over This is just
     * to prevent the program from repainting all the
     * time, when the mouse moves but not leaves a cell.
     */
    private Cell mouseHover;
    
    /** Year, month, week or day. */
    protected View view;
    
    /** Dimension for 'matrix' depending on selected view. */
    protected int rows, cols;
    
    /** Clearance to upper and left window border. */
    protected int clear_up, clear_left;
    
    /** Dimension of a single cell. */
    protected int width, height;
    
    /** Parent calendar object. */
    protected Calendar calendar;
    
    /** Currently selected event. */
    private Event selectedEvent;
    
    /** Currently highlighted headline. */
    protected int highlightedHeadline, highlightedHeadlineEnd;
    
    /** Length of the two month names in px. */
    protected int[] lenMonthName = new int[2];
    
    /** Font size for yearly view. */
    protected int fontsizeYear;
    
    /** The ordered list of days of week. */
    protected int[] daysOfWeek = Date.weekDayList(java.util.Calendar.SUNDAY);
    
    /** Popup menu. */
    private static CalendarPopupMenu popup;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(CalendarCanvas.class);

    /**
     * Set the owner of the canvas.
     * @return Parent calendar object.
     */
    public Calendar getOwner() {
        return this.calendar;
    }

    /**
     * Returns the currently painted view.
     * @return Current kind of view (year, month, week, day).
     */
    public View getView() {
        return this.view;
    }

    /**
     * Construct new calendar canvas.
     * @param c - Parent calendar object
     * @param view - View to start with (Year, month, week or day)
     */
    protected CalendarCanvas(Calendar c, View view) {
//        super();
//        this.setIgnoreRepaint(false);
//        this.setOpaque(true);
        calendar = c;
        this.view = view;
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        // TODO: STRG is a tricky thing... implementation later
        this.addKeyListener(this);
        
        this.mouseSrc = null;
        this.reverse = false;
        this.strgPressed = false;
        this.markedDates = new ArrayList<Date>();
        this.strgDates = new ArrayList<Date>();
        this.matrix = null;
        this.highlightedHeadline = -1;
        this.selectedEvent = null;
    }
    
    /**
     * Construct a new CalendarCanvas depending on the desired view.
     * @param calendar - Parent calendar object 
     * @param view - Desired view
     * @return CalendarCanvas object.
     */
    public static CalendarCanvas create(Calendar calendar, View view) {
        switch (view) {
        case year:
            return new CalendarCanvasYear(calendar);
        case week:
            return new CalendarCanvasWeek(calendar);
        case day:
            return new CalendarCanvasDay(calendar);
        case month:
        default:
            return new CalendarCanvasMonth(calendar);
        }
    }

    /**
     * Calculates the dimension of a single cell, pending on the 
     * current view and the window size. Sets the global variables 
     * width and height.
     * @return Dimension calculated for a single cell.
     */
    public abstract Dimension calcDim();
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        LOG.fine("paintComponent");
        daysOfWeek = Date.weekDayList(calendar.getConfig().getProperty(IntProperty.FirstDayOfWeek));
        Cell.initColors(this);
        drawCalendar(g);
        fillCalendar(g);
        
        if (popup != null) {
            popup.repaint();
        }
    }
    
    /**
     * Returns the number of week of a certain date.
     * @param date - Date to get number of week
     * @return the number of week for this date. In the US a week starts with Sunday.
     * So there are special cases to pay attention.
     */
    protected int getWeekNo(Date date) {
        int weekNo = date.get(java.util.Calendar.WEEK_OF_YEAR);
        
        // special case for US
        if (daysOfWeek[0] == java.util.Calendar.SUNDAY) {
            if (date.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SUNDAY) {
                weekNo++;
            
            } else if (weekNo == 1 && date.get(java.util.Calendar.MONTH) == java.util.Calendar.DECEMBER) {
                Date test = date.clone();
                do {
                    test.add(java.util.Calendar.DAY_OF_MONTH, -1);
                    weekNo = test.get(java.util.Calendar.WEEK_OF_YEAR);
                } while (weekNo == 1);
                weekNo++;
            }
        }
        return weekNo;
    }
    
    /**
     * Draw the basic structure of the calendar (grid cells, content independent paintings).
     * @param g - Graphics to paint on
     */
    protected abstract void drawCalendarGrid(Graphics g);

    /**
     * Draw the basic structure of the calendar, depending on the 
     * current view, but independent from specific day or month.
     * @param g - Graphics to paint on
     */
    private void drawCalendar(Graphics g) {
        calcDim();
        drawCalendarGrid(g);
    }
    
    /**
     * Fill the drawn calendar in with dates and events, depending on the currently viewed date.
     * @param g - Graphics to paint on
     * @param events - Events to paint
     * @param date - Currently viewed date 
     */
    protected abstract void fillCalendarContent(Graphics g, List<Event> events, Date date);

    /**
     * Fill the drawn calendar with dates and events, depending on 
     * the currently viewed date.
     * @param g - Graphics to paint on
     */
    private void fillCalendar(Graphics g) {
        List<Event> events = new ArrayList<Event>(calendar.getAllEvents());
        Date date = calendar.getViewedDate().clone();
        date.setHasTime(false);

        matrix = new Cell[cols][rows];

        fillCalendarContent(g, events, date);
    }
    
    /**
     * Mark a set of cells between two points to be marked as selected.
     * @param start - Index of starting cell within the matrix
     * @param end - Index of ending cell within the matrix
     */
    protected abstract void markIt(Point start, Point end);
    
    /**
     * Have a column (days of the same week day) to be marked as selected.
     * @param col - Column (week day) to select
     */
    protected abstract void markColumn(int col);
    
    /**
     * Is the current cursor position on the calendar week label (depending
     * on the currently used view).
     * @param xPos - x Position of the cursor
     * @param yPos - y Position of the cursor
     * @param colIndex - Index of the column (already pre-calculated)
     * @param rowIndex - Index of the row (already pre-calculated)
     * @return True if the cursor is on the calendar week label. If the view
     * does not have such a label -> always false.
     */
    protected abstract boolean mouseOnCalendarWeekLabel(int xPos, int yPos, int colIndex, int rowIndex);
    
    /**
     * Have a set of cells in a row to be marked as selected in
     * yearly or weekly view.
     * @param start - Index of starting cell within the matrix
     * @param end - Index of ending cell within the matrix
     */
    protected void markWeekYear(Point start, Point end) {
        int col = start.x;
        int row = start.y;
        
        List<Cell> toMark = new ArrayList<Cell>();
        
        if (start.x == -1 || start.x == cols || end.x == -1 || end.x == cols) {
            return;
        }
        if (end.y > start.y || (end.y == start.y && end.x > start.x)) {
            reverse = false;
        } else {
            reverse = true;
        }

        while (true) {
            if (!reverse) {
                if (col == cols) { 
                    /* Line break (downwards) */
                    col = 0;
                    row++;
                }
            } 
            else {
                if (col == -1) { 
                    /* Line break (upwards) */
                    col = cols - 1;
                    row--;
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

    /**
     * Mark a set of cells in a row to be marked as selected.
     * @param start - Index of starting cell within the matrix
     * @param end - Index of ending cell within the matrix
     */
    protected void mark(Point start, Point end) {
        if (start == null || end == null) {
            return;
        }
        
        mouseHover = matrix[end.x][end.y];

        markIt(start, end);
    }

    /**
     * Mark a set of cells as selected.
     * @param x - Set of cells to mark
     */
    protected void mark(List<Cell> x) {
        for (int a = 0; a < cols; a++) {
            for (int b = 0; b < rows; b++) {
                if (matrix[a][b] != null) {
                    if (x.contains(matrix[a][b])) {
                        if (!matrix[a][b].isSelected()) {
                            matrix[a][b].setSelected(true);
                        }
                    }
                    else if (matrix[a][b].isSelected() && !strgDates.contains(matrix[a][b].getDate())) {
                        matrix[a][b].setSelected(false);
                    }
                }
            }
        }
        markedDates.clear();
        for (Cell c : x) {
            markedDates.add(c.getDate());
        }
    }
    
    /**
     * In case of a previously highlighted headline - reset the
     * highlighting again.
     * @param g - Graphics to paint on
     */
    protected abstract void resetHeadline(Graphics g);
    
    /**
     * Highlight a header label.
     * @param g - Graphics object to paint on
     * @param index -
     * <br>in case of monthly view:
     *         <li>0..14: index of week day (Mo..So..) on the top
     *         <li>20..25: index of week number on the left site
     *         <li>30..35: index of week number on the right site
     *         <li>50: big month label on the left site
     *         <li>60: big month label on the right site
     *         
     * <br><br>in case of yearly view:
     *         <li>0..11: index of month (Jan..Dec) on the left site
     *         <li>12..23: index of calendar weeks on the right site
     *         <li>30..71: index of column (Mo..So..) on the top
     *         <li>100: big year label
     * @param endindex - for calendar weeks only: where dragging
     *         of whole weeks ends (index numbers same as 'index')
     */
    protected abstract void highlightHeadline(Graphics g, int index, int endindex);

    /**
     * Unmark all selected cells of the current view.
     * In case of STRG key pressed, DO NOT unmark these selected cells.
     */
    public void unmarkAll() {
        LOG.fine("[unmarkAll]");
        for (int a = 0; a < cols; a++) {
            for (int b = 0; b < rows; b++) {
                if (matrix[a][b] != null) {
                    if (matrix[a][b].isSelected()) {
                        if (!strgPressed || !strgDates.contains(matrix[a][b].getDate())) {
                            matrix[a][b].setSelected(false);
                        }
                    }
                }
            }
        }
        markedDates.clear();
//        strgPressed = false;
        
        resetHeadline(getGraphics());
    }
    
    /**
     * Mark or unmark a previously selected event.
     * @param mark - True for selecting, false for de-selecting 
     */
    private void markSelectedEvent(boolean mark) {
        if (selectedEvent != null) {
            selectedEvent.getGUI().setSelected(mark);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (matrix[j][i] != null) {
                        if (matrix[j][i].containsEvent(selectedEvent)) {
                            matrix[j][i].paint(this.getGraphics(), new Dimension(width, height));
                        }
                    }
                }
            }
            if (!mark) {
                selectedEvent = null;
                setToolTipText(null);
            } else {
                setToolTipText(selectedEvent.getGUI().getToolTip());
            }
        }
    }

    /**
     * Repaint the calendar canvas.
     */
    public void update() {
        mouseHover = null;
        repaint();
    }

    /**
     * Mark a single cell as selected.
     * @param x - Cell to select
     */
    private void mark(Cell x) {
        LOG.fine("[mark] " + x);
        
        unmarkAll();
        if (x != null) {
            x.setSelected(true);
            markedDates.add(x.getDate());
        }
        
        mouseHover = x;
    }
    
    /**
     * In case of a clicked event: Show a popup menu for the selected event.
     * @param m - Mouse event (needed to determine the event selected)
     */
    private void displayEventPopupMenu(MouseEvent m) {
        /*
         * Remove all mouse listeners in order to avoid selections
         * while a popup menu is active on a certain event.
         */
        this.removeMouseListener(this);
        this.removeMouseMotionListener(this);
        this.setToolTipText(null);
        
        int col_index = (m.getX() - clear_left) / width;
        int row_index = (m.getY() - clear_up) / height;
        Date selectedDate = matrix[col_index][row_index].getDate();
        
        popup = new CalendarPopupMenu(selectedEvent, selectedDate, calendar, m.getLocationOnScreen(), this, true);
        popup.setVisible(true);
    }
    
    /**
     * In case of a clicked date: Show a popup menu for the selected date.
     * @param m - Mouse event (needed to determine the date selected)
     */
    private void displayCellPopupMenu(MouseEvent m) {
        /*
         * Remove all mouse listeners in order to avoid selections
         * while a popup menu is active on a certain cell (date).
         */
        this.removeMouseListener(this);
        this.removeMouseMotionListener(this);
        this.setToolTipText(null);
        
        popup = new CalendarPopupMenu(markedDates.get(0), calendar, m.getLocationOnScreen(), this);
    }

    @Override
    public void mouseExited(MouseEvent m) {
    }

    @Override
    public void mouseEntered(MouseEvent m) {
        requestFocus();
    }

    /*
     * In case this is introduces mouse dragging, keep track of the cell 
     * where the dragging has started.
     */
    @Override
    public void mousePressed(MouseEvent m) {
        mouseSrc = null;
        
        // Check for popup menu on certain event
        if (selectedEvent != null) {
            if (m.isPopupTrigger()) {
                displayEventPopupMenu(m);
            }
            return;
        }
        
        // Check for popup menu on certain cell (date)
        if (m.isPopupTrigger()) {
            displayCellPopupMenu(m);
            return;
        }
        
        // Set 'mouseSrc' properly (store for the starting point of mouse events)
        
        int colIndex = (m.getX() - clear_left) / width;
        int rowIndex = (m.getY() - clear_up) / height;
        
        if (rowIndex < 0 || rowIndex >= rows) {
            return;
        }
        
        if (mouseOnCalendarWeekLabel(m.getX(), m.getY(), colIndex, rowIndex)) {
            // Keep source point in case of dragging over multiple calendar weeks
            mouseSrc = new Point(colIndex, rowIndex);
            return;
        }
        
        if (view == View.month && (colIndex == 7 || colIndex == 15)) {
            return;
        } else if (m.getX() < clear_left || m.getY() < clear_up || colIndex >= cols || rowIndex >= rows) {
            return;
        }
        
        mouseSrc = new Point(colIndex, rowIndex);
    }

    /*
     * Open the events overview based on the cells (dates) selected.
     */
    @Override
    public void mouseReleased(MouseEvent m) {
        if (popup != null) {
            return;
        }
        
        // Invoke mouse event on a selected event
        if (selectedEvent != null) {
            if (strgPressed) {
                return;
            } else if (m.isPopupTrigger()) {
                displayEventPopupMenu(m);
            } else {
                calendar.newSelection(selectedEvent);
            }
            return;
        }
        
        // Store all selected cells in 'markedDates'
        for (int a = 0; a < cols; a++) {
            for (int b = 0; b < rows; b++) {
                if (matrix[a][b] != null) {
                    if (matrix[a][b].isSelected()) {
                        if (!markedDates.contains(matrix[a][b].getDate())) {
                            markedDates.add(matrix[a][b].getDate());
                        }
                        if (strgPressed && !strgDates.contains(matrix[a][b].getDate())) {
                            strgDates.add(matrix[a][b].getDate());
                        }
                    }
                }
            }
        }
        
        if (strgPressed) {
            // If STRG key still pressed -> do nothing
            return;
        }
        
        if (!markedDates.isEmpty()) {
            
            // If only one cell selected -> invoke click action
            if (markedDates.size() == 1) {
                if (m.isPopupTrigger()) {
                    displayCellPopupMenu(m);
                } else {
                    calendar.newSelection(markedDates, true);
                }
                return;
            }
            
            // Add all selected cells to list 'cells'
            List<Cell> cells = new ArrayList<Cell>();
            for (int b = 0; b < rows; b++) {
                for (int a = 0; a < cols; a++) {
                    if (matrix[a][b] != null) {
                        if (matrix[a][b].isSelected()) {
                            cells.add(matrix[a][b]);
                        }
                    }
                }
            }
            
            // Open table of events window for selected range of dates
            boolean weeklySelection = highlightedHeadline != -1 && 
                    ((view == View.month && highlightedHeadline < 15) 
                            || (view == View.year && highlightedHeadline >= 30
                            && highlightedHeadline <= 71));
            
            new TabelOfEventsMulitDay(cells, calendar, markedDates, weeklySelection);
        }
    }

    /*
     * All we need to evaluate clicks is already implemented in 
     * mousePressed() and mouseReleased().
     */
    @Override
    public void mouseClicked(MouseEvent m) {
    }

    /*
     * If the mouse key is pressed and the mouse is moved, this indicates 
     * that a set of cells, starting from the beginning of the pressing 
     * and ending at this current position is to be marked.
     */
    @Override
    public void mouseDragged(MouseEvent m) {
        if (this.view == View.day) {
            return;
        }
        
        if (m.getX() < clear_left || m.getY() < clear_up || mouseSrc == null) {
            return;
        }
        
        int col_index = (m.getX() - clear_left) / width;
        int row_index = (m.getY() - clear_up) / height;
        
        if (view == View.month && mouseSrc != null 
                && (mouseSrc.x == 7 || mouseSrc.x == 15) && row_index < rows) {
            
            /* mark whole calendar week (left side) */
            if (mouseSrc.x == 7) {
                int start_x = 0, start_y = mouseSrc.y, end_x = 6, end_y = row_index;
                if (row_index < mouseSrc.y) {
                    start_y = row_index;
                    end_y = mouseSrc.y;
                }
                if (matrix[end_x][end_y] == null) {
                    end_x = 14;
                    end_y = 0;
                }
                mark(new Point(start_x, start_y), new Point(end_x, end_y));
                highlightHeadline(getGraphics(), 
                        CalendarCanvasMonth.HIGHLIGHT_WEEK_LEFT + mouseSrc.y, 
                        CalendarCanvasMonth.HIGHLIGHT_WEEK_LEFT + row_index);
                return;
            }
            
            /* mark whole calendar week (right side) */
            else if (mouseSrc.x == 15) {
                int start_x = 8, start_y = mouseSrc.y, end_x = 14, end_y = row_index;
                if (row_index < mouseSrc.y) {
                    start_y = row_index;
                    end_y = mouseSrc.y;
                }
                if (matrix[start_x][start_y] == null) {
                    start_x = 0;
                    if (matrix[0][5] == null) {
                        start_y = 4;
                    } else {
                        start_y = 5;
                    }
                }
                mark(new Point(start_x, start_y), new Point(end_x, end_y));
                highlightHeadline(getGraphics(), 
                        CalendarCanvasMonth.HIGHLIGHT_WEEK_RIGHT + mouseSrc.y, 
                        CalendarCanvasMonth.HIGHLIGHT_WEEK_RIGHT + row_index);
                return;
            }
        }
        else if (view == View.week && mouseSrc != null && mouseSrc.x == cols &&
                row_index < rows) {
            mark(new Point(0, mouseSrc.y), new Point(6, row_index));
            return;
        }
        else if (col_index >= cols || row_index >= rows) {
            return;
        }
        
        if (matrix[col_index][row_index] != mouseHover) {
            mark(mouseSrc, new Point(col_index, row_index));
        }
    }

    /*
     * This method gets called when the mouse is moved without a key 
     * being pressed. In this case, just mark the cell situated 
     * under the current cursor position.
     */
    @Override
    public void mouseMoved(MouseEvent m) {
        if (matrix == null) {
            return;
        }
        
        int col_index = (m.getX() - clear_left) / width;
        int row_index = (m.getY() - clear_up) / height;

        /*
         * Mouse moved beyond left border
         */
        if (m.getX() < clear_left) {
            mouseHover = null;
            markSelectedEvent(false);
            
            if (view == View.year && row_index >= 0 && row_index < Date.MONTHS_OF_YEAR) {
                // For yearly view -> mark the selected month
                highlightHeadline(getGraphics(), row_index, 0);
                mark(new Point(0, row_index), new Point(cols - 1, row_index));
            } else {
                // Clear all selections
                unmarkAll();
            }
        }
        
        /*
         * Mouse moved beyond top border
         */
        else if (m.getY() < clear_up) {
            mouseHover = null;
            
            if (view == View.month && clear_up - m.getY() < 18) {
                // For monthly view in the area between calendar matrix and the month names
                
                if (col_index == 7 || col_index > 14) {
                    // No day of week (Mo ... So) selected (between or next to the two pages)
                    unmarkAll();
                    markSelectedEvent(false);
                }
                else {
                    // Day of week (Mo ... So) selected -> mark that column
                    markColumn(col_index);
                }
            }
            
            else if (view == View.month && 
                    clear_up - m.getY() > 20 && clear_up - m.getY() < 40) {
                // Monthly view -> Name of month selected
                
                if (m.getX() - clear_left > 5 && m.getX() - clear_left < lenMonthName[0] + 5) {
                    // Mark whole left month
                    highlightHeadline(getGraphics(), CalendarCanvasMonth.HIGHLIGHT_MONTH_LEFT, -1);
                    markIt(new Point(0, 0), new Point(6, 5));
                }
                else if (m.getX() - clear_left > 5 + 8 * width && m.getX() - clear_left 
                        < lenMonthName[1] + 5 + 8 * width) {
                    // Mark whole right month
                    highlightHeadline(getGraphics(), CalendarCanvasMonth.HIGHLIGHT_MONTH_RIGHT, -1);
                    markIt(new Point(8, 0), new Point(14, 5));
                }
                else {
                    // Area where no month name is -> clear selections
                    unmarkAll();
                    markSelectedEvent(false);
                }
            }
            
            else if (view == View.year && clear_up - m.getY() < fontsizeYear + 5 
                    && col_index >= 0 && col_index < cols) {
                /* mark weekday in whole year */
                markColumn(col_index);
            }
            else if (view == View.year && m.getX() >= this.getWidth() / 2 - 50 && m.getX() 
                    <= this.getWidth() / 2 + 16
                    && m.getY() <= clear_up - 20 && m.getY() >= 2) {
                /* mark weekday in whole year */
                highlightHeadline(getGraphics(), CalendarCanvasYear.HIGHLIGHT_YEAR, 0);
                mark(new Point(0, 0), new Point(cols - 1, rows - 1));
            }
            else if (view == View.day && m.getX() > clear_left && m.getX() < clear_left + 290 && m.getY() > 2) {
                markSelectedEvent(false);
                if (!matrix[0][0].isSelected()) {
                    matrix[0][0].setSelected(true);
                }
            }
            else if (view == View.day && m.getX() > clear_left + width && m.getX() 
                    < clear_left + width + 290 && m.getY() > 2) {
                markSelectedEvent(false);
                if (!matrix[1][0].isSelected()) {
                    matrix[1][0].setSelected(true);
                }
            }
            else {
                unmarkAll();
                markSelectedEvent(false);
            }
        }
        
        /*
         * Mouse position within the matrix
         */
        else if (col_index < cols && row_index < rows && matrix[col_index][row_index] != null) {
            Event newSelected = matrix[col_index][row_index].getEventUnderCursor(
                    m.getPoint(), new Dimension(width, height));
            
            if (newSelected == null) {
                if (selectedEvent != null) {
                    markSelectedEvent(false);
                }
            }
            else if (!newSelected.equals(selectedEvent)) {
                markSelectedEvent(false);
                selectedEvent = newSelected;
                markSelectedEvent(true);
                mark((Cell) null);
                return;
            }
            else if (newSelected.equals(selectedEvent)) {
                mark((Cell) null);
                return;
            }

            if (matrix[col_index][row_index].equals(mouseHover)) {
                return;
            }
            mark(matrix[col_index][row_index]);
//            markedDates.add(matrix[col_index][row_index].getDate());
        }
        
        /*
         * In monthly view: mouse position on a calendar week label
         */
        else if (view == View.month && mouseOnCalendarWeekLabel(m.getX(), m.getY(), col_index, row_index)) {
            
            markSelectedEvent(false);
            mouseSrc = new Point(col_index, row_index);
            
            if (col_index == 7) {
                /* mark a week in month on left site */
                if (matrix[6][row_index] == null) {
                    if (matrix[0][row_index] == null) {
                        unmarkAll();
                        markSelectedEvent(false);
                    }
                    else {
                        highlightHeadline(getGraphics(), CalendarCanvasMonth.HIGHLIGHT_WEEK_LEFT + row_index, -1);
                        mark(new Point(0, row_index), new Point(14, 0));
                    }
                }
                else {
                    highlightHeadline(getGraphics(), CalendarCanvasMonth.HIGHLIGHT_WEEK_LEFT + row_index, -1);
                    mark(new Point(0, row_index), new Point(6, row_index));
                }
            }

            else {
                /* mark a week in month on right site */
                if (matrix[8][row_index] == null) {
                    if (row_index == 0) {
                        highlightHeadline(getGraphics(), CalendarCanvasMonth.HIGHLIGHT_WEEK_RIGHT + row_index, -1);
                        if (matrix[0][5] == null) {
                            mark(new Point(0, 4), new Point(14, row_index));
                        } else {
                            mark(new Point(0, 5), new Point(14, row_index));
                        }
                    }
                    else {
                        unmarkAll();
                        markSelectedEvent(false);
                    }
                }
                else {
                    highlightHeadline(getGraphics(), CalendarCanvasMonth.HIGHLIGHT_WEEK_RIGHT + row_index, -1);
                    mark(new Point(8, row_index), new Point(14, row_index));
                }
            }
            mouseHover = null;
        }
        
        /*
         * In weekly view: mouse position on column 7
         * -> mark entire week (= entire row)
         */
        else if (view == View.week && col_index == 7 && row_index < 2) {
            if (m.getX() >= clear_left + 5 + cols * width && m.getX() <= clear_left + 10 + cols * width &&
                    m.getY() >= clear_up + 6 + row_index * height && m.getY() <= clear_up + 16 + row_index * height) {
                mark(new Point(0, row_index), new Point(6, row_index));
            } else {
                mark((Cell) null);
            }
            markSelectedEvent(false);
            mouseHover = null;
        }
        
        /*
         * In yearly view: mouse position beyond right border
         * -> mark certain set of calendar weeks
         */
        else if (view == View.year && col_index >= cols && row_index < rows) {
            markSelectedEvent(false);
            mouseHover = null;
            int start_x = 0, start_y = row_index, end_x = cols - 1, end_y = row_index;
            if (row_index > 0) {
                int tmp_x = start_x;
                while (matrix[tmp_x][start_y] == null) {
                    tmp_x++;
                }
                if (tmp_x % 7 != 0) {
                    start_y--;
                    tmp_x = end_x;
                    while (matrix[tmp_x][start_y] == null) {
                        tmp_x--;
                    }
                    start_x = tmp_x - (tmp_x % 7);
                }
            }
            if (row_index < 11) {
                int tmp_x = end_x;
                while (matrix[tmp_x][end_y] == null) {
                    tmp_x--;
                }
                if (++tmp_x % 7 != 0) {
                    end_y++;
                    end_x = 6;
                }
            }
            highlightHeadline(getGraphics(), CalendarCanvasYear.HIGHLIGHT_CALENDAR_WEEK + row_index, 0);
            mark(new Point(start_x, start_y), new Point(end_x, end_y));
        }
        
        /*
         * Any other case -> clear all selections
         */
        else {
            markSelectedEvent(false);
            mark((Cell) null);
            if (markedDates.size() != 0) {
                unmarkAll();
            }
        }
    }

    /*
     * In case STRG is released, open a new create-event-frame, based
     * on the cells (dates) selected.
     */
    @Override
    public void keyReleased(KeyEvent k) {
        strgPressed = false;
        if (k.getKeyCode() == KeyEvent.VK_CONTROL && !strgDates.isEmpty()) {
            calendar.newSelection(strgDates, false);
            strgDates.clear();
            unmarkAll();
        }
        else if (k.getKeyCode() == KeyEvent.VK_ESCAPE) {
            unmarkAll();
            markSelectedEvent(false);
        }
    }

    /*
     * In case STRG is pressed, enable selecting cells independently.
     */
    @Override
    public void keyPressed(KeyEvent k) {
        if (this.view != View.day && k.getKeyCode() == KeyEvent.VK_CONTROL) {
            strgPressed = true;
        }
    }

    @Override
    public void keyTyped(KeyEvent k) {
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        markSelectedEvent(false);
        unmarkAll();
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        /*
         * Add mouse listeners again after popup menu has vanished.
         */
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        popup = null;
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }
}