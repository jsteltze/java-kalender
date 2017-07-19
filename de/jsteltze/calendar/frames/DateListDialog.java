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

package de.jsteltze.calendar.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner.DateEditor;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import de.jsteltze.common.VerticalFlowPanel;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.JSpinnerForDate;
import de.jsteltze.common.ui.Button;

/**
 * Dialog for displaying and editing a list of dates.
 * @author Johannes Steltzer
 *
 */
public class DateListDialog
    extends JDialog
    implements ActionListener {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Listener to be notified of the currently set dates after the OK button was clicked. */
    private DateListListener listener;
    
    /** Buttons for adding a new date, removing all dates, save (OK) and cancel. */
    private Button addButton = new Button("DatesDialogAdd", "/media/add12.png", this),
            removeAllButton = new Button("DatesDialogRemoveAll", "/media/delete12.png", this),
            okButton = new Button("DatesDialogOK", "/media/save16.png", Button.ICON_SIZE_S, this),
            cancelButton = new Button("DatesDialogCancel", this);
    
    /** Container for the list of dates (vertically arranged). */
    private VerticalFlowPanel listPanel = new VerticalFlowPanel();
    
    /** Panel for the add button (for adding a new date) and removing all dates entries button. */
    private JPanel addRemoveButtonPanel = new JPanel(new BorderLayout());
    
    /** List of all date entry panels contained in the 'listPanel'. */
    private List<JPanel> datePanels = new ArrayList<JPanel>();
    
    /** List of all remove buttons for removing a single date entry. */
    private List<Button> removeButtons = new ArrayList<Button>();
    
    /** List of all JSpinners for setting the dates. */
    private List<JSpinnerForDate> dateSpinners = new ArrayList<JSpinnerForDate>();
    
    /** Scroll pane to contain the 'listPanel'. */
    private JScrollPane scrollPane;
    
    /** Initial window size. */
    private static final Dimension DEFAULT_SIZE = new Dimension(300, 300);
    
    /**
     * Construct a new date list dialog.
     * @param parent - Parent dialog
     * @param dates - Initial list of dates to be displayed
     * @param title - Title of the dialog
     * @param intro - Introduction text (can be HTML formatted)
     * @param listener - Listener to be notified of the selected dates when the OK
     * button gets clicked
     */
    public DateListDialog(JDialog parent, List<Date> dates, String title, String intro, DateListListener listener) { 
        super(parent, title);
        this.listener = listener;
        
        setLayout(new BorderLayout(5, 5));
        removeAllButton.setEnabled(false);
        
        for (Date d : dates) {
            addDate(d);
        }
        addRemoveButtonPanel.add(addButton, BorderLayout.WEST);
        addRemoveButtonPanel.add(removeAllButton, BorderLayout.EAST);
        addRemoveButtonPanel.setBorder(new EmptyBorder(10, 5, 5, 5));
        listPanel.add(addRemoveButtonPanel);
        
        scrollPane = new JScrollPane(listPanel);
        
        JPanel buttonPanel = okButton.getPanel();
        buttonPanel.add(cancelButton);
        
        JPanel wrapper = new JPanel(new BorderLayout(5, 5));
        
        wrapper.add(new JLabel(intro), BorderLayout.NORTH);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);
        wrapper.setBorder(new EmptyBorder(5, 5, 5, 5));
        wrapper.setBackground(Color.white);
        
        add(wrapper, BorderLayout.CENTER);
        
        setSize(DEFAULT_SIZE);
        setLocationRelativeTo(parent);
    }
    
    /**
     * Add a new date to the list of dates to be displayed.
     * @param d - Date to add
     */
    private void addDate(Date d) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JSpinnerForDate jsp = new JSpinnerForDate(d);
        Button removeButton = new Button("DatesDialogRemove", "/media/delete12.png", this);
        panel.add(jsp);
        panel.add(removeButton);
        
        datePanels.add(panel);
        listPanel.add(panel);
        removeButtons.add(removeButton);
        dateSpinners.add(jsp);
        
        removeAllButton.setEnabled(true);
        
        // request focus (cursor placed in freshly added JSpinner)
        ((DateEditor) jsp.getEditor()).getTextField().requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(addButton)) {
            // add new entry with current date
            
            listPanel.removeComponent(addRemoveButtonPanel);
            addDate(new Date());
            listPanel.add(addRemoveButtonPanel);
//            
//            // scroll to bottom
//            if (scrollPane != null) {
//                JScrollBar vertical = scrollPane.getVerticalScrollBar();
//                vertical.setValue(vertical.getMaximum());
//            }
        } else if (e.getSource().equals(removeAllButton)) {
            // remove everything
            
            listPanel.removeAll();
            datePanels.clear();
            removeButtons.clear();
            dateSpinners.clear();
            
            listPanel.add(addRemoveButtonPanel);
            
            removeAllButton.setEnabled(false);
        } else if (e.getSource().equals(okButton)) {
            // save it
            
            List<Date> dates = new ArrayList<Date>();
            for (JSpinnerForDate jsp : dateSpinners) {
                dates.add(jsp.getSelectedDate());
            }
            
            listener.dateListChanged(dates);
            
            this.setVisible(false);
            this.dispose();
            return;
        } else if (e.getSource().equals(cancelButton)) {
            // cancel
            this.setVisible(false);
            this.dispose();
            return;
        } else {
            // remove single entry
            
            Button removeButton = (Button) e.getSource();
            
            // determine index of selected panel
            int index = 0;
            for ( ; index < removeButtons.size(); index++) {
                if (removeButtons.get(index).equals(removeButton)) {
                    break;
                }
            }
            
            // determine panel and remove it from the container
            JPanel datePanel = datePanels.get(index);
            listPanel.removeComponent(datePanel);
            
            // remove from local variable lists
            removeButtons.remove(index);
            dateSpinners.remove(index);
            datePanels.remove(index);
        }
        
        // update window
        SwingUtilities.updateComponentTreeUI(listPanel);
        this.invalidate();
        this.revalidate();
    }
}
