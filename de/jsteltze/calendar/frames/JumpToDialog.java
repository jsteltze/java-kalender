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
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;

import de.jsteltze.calendar.config.enums.View;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.JSpinnerForDate;

/**
 * Dialog for jumping to any custom date.
 * @author A352091
 *
 */
public final class JumpToDialog {
    
    /** Selectible spinners. */
    private static JSpinnerForDate spinner1 = null, spinner2 = null;
    
    /** Selectible versions of the date to choose. */
    private static JRadioButton button1 = new JRadioButton("Datum:"),
            button2 = new JRadioButton("Kalenderwoche/Jahr:");
    
    /**
     * Hidden constructor.
     */
    private JumpToDialog() { }
    
    /**
     * Returns the selected JSpinner (if there are more than 1 spinners as choice).
     * @return the selected JPinner.
     */
    private static JSpinnerForDate getSelectedSpinner() {
        if (spinner2 == null) {
            return spinner1;
        } else {
            return button1.isSelected() ? spinner1 : spinner2;
        }
    }
    
    /**
     * Show the dialog.
     * @param parent - Parent component
     * @param view - Currently displayed view
     * @param initDate - Initial date to set
     * @return the selected date to jump to or null (abortion).
     */
    public static Date show(Component parent, View view, Date initDate) {
        JPanel messageBody = new JPanel(new BorderLayout());
        String header = "";
        spinner2 = null;
        switch (view) {
        case year:
            header = "Jahr:";
            spinner1 = new JSpinnerForDate(initDate, "yyyy");
            break;
        case month:
            header = "Monat/Jahr:";
            spinner1 = new JSpinnerForDate(initDate, "MMM yyyy");
            break;
        case day:
            header = "Datum:";
            spinner1 = new JSpinnerForDate(initDate, "EEE, dd.MM.yyyy");
            break;
        default:
            spinner1 = new JSpinnerForDate(initDate, "EEE, dd.MM.yyyy");
            spinner2 = new JSpinnerForDate(initDate, "ww/yyyy");
            ButtonGroup group = new ButtonGroup();
            group.add(button1);
            group.add(button2);
            button1.setSelected(true);
        }
        
        if (spinner2 == null) {
            JPanel flow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            flow1.add(new JLabel(header));
            flow1.add(spinner1);
            messageBody.add(flow1, BorderLayout.CENTER);
        } else {
            JPanel flow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            flow1.add(button1);
            flow1.add(spinner1);
            JPanel flow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            flow2.add(button2);
            flow2.add(spinner2);
            messageBody.add(flow1, BorderLayout.CENTER);
            messageBody.add(flow2, BorderLayout.SOUTH);
        }
        messageBody.setBorder(new EtchedBorder());
        int answer = JOptionPane.showConfirmDialog(parent, messageBody, "Springen nach...", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer == JOptionPane.CANCEL_OPTION) {
            return null;
        } else {
            return getSelectedSpinner().getSelectedDate();
        }
    }
}
