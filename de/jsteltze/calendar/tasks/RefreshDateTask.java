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

package de.jsteltze.calendar.tasks;

import javax.swing.JLabel;

import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.calendar.Date.PrintFormat;

/**
 * Thread for refreshing a date and time label each minute. 
 * @author Johannes Steltzer
 *
 */
public class RefreshDateTask 
    extends Thread {
    
    /** Label for showing date and time. */
    private JLabel label;
    
    /** Set to false to stop this thread. */
    private boolean running;

    /**
     * Construct a new task to display accurate date and
     * time in a label. Format is "DD.MM.YYYY HH:MM". 
     * @param label - Label to display date and time
     */
    public RefreshDateTask(JLabel label) {
        this.label = label;
        this.running = true;
    }
    
    /**
     * Stop this thread.
     */
    public void stopit() {
        this.running = false;
        this.interrupt();
    }
    
    @Override
    public void run() {
        /*
         * Fill the running minute
         */
        Date date = new Date();
        try {
            Thread.sleep(Date.MIN_1 - date.get(Date.SECOND) * Date.SEC_1);
        } catch (InterruptedException e) {
            return;
        }
        date = new Date();
        label.setText(date.print(PrintFormat.DDMMYYYY_HHmm));

        /*
         * Refresh date and time in the label each minute
         */
        while (running) {
            try {
                Thread.sleep(Date.MIN_1);
            } catch (InterruptedException e) {
                return;
            }
            date = new Date();
            label.setText(date.print(PrintFormat.DDMMYYYY_HHmm));
        }
    }
}