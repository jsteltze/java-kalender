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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.UI.GUIUtils;
import de.jsteltze.calendar.config.Configuration;
import de.jsteltze.calendar.config.Configuration.IntProperty;
import de.jsteltze.calendar.config.enums.HolidayConstants;
import de.jsteltze.common.Log;
import de.jsteltze.common.VerticalFlowPanel;
import de.jsteltze.common.ui.Button;
import de.jsteltze.common.ui.SearchPanel;
import de.jsteltze.common.ui.SearchPanelListener;
import de.jsteltze.common.ui.SelectAllCheckbox;

/**
 * Settings frame for calendar configuration: tab for holiday settings.
 * @author Johannes Steltzer
 *
 */
public class SettingsTabHolidays 
    extends JPanel 
    implements ActionListener, SearchPanelListener {
    
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Checkboxes: one for each holiday. */
    private JCheckBox[] holidayBoxes, specialDayBoxes, actionDayBoxes1, actionDayBoxes2;
    
    /** Checkbox for select all world/action days. */
    private SelectAllCheckbox selectAllActionDays = new SelectAllCheckbox();
    
    /** Panels containing the checkboxes. */
    private JPanel pByLaw, pSpecial, pAction, selectAllPanel;
    
    /** Panel containing all panels. */
    private VerticalFlowPanel pMain = new VerticalFlowPanel();
    
    /** Panel containing the search result. */
    private JPanel searchResultPanel;
    
    /** Button for resetting the checkboxes to their default states. */
    private Button defaultButton = new Button("SettingsDefaultButton", this);
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(SettingsTabHolidays.class);

    /**
     * Arrange settings tab 2: Holidays.
     * @param caller - Parent calendar object (to receive currently configured colors from)
     */
    public SettingsTabHolidays(Calendar caller) {
        super(new BorderLayout());
        
        initCheckboxes(caller.getConfig());
        
        selectAllPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        selectAllPanel.add(new JLabel("          "));
        selectAllPanel.add(selectAllActionDays);

        resetCheckboxPanels();

        LOG.fine("HOLIDAY ID=" + caller.getConfig().getProperty(IntProperty.HolidayID));
        
        JPanel pC = new JPanel(new GridLayout(1, 1));
        
        SearchPanel searchPanel = new SearchPanel("Name oder Datum", "Nach Feiertagen oder Aktions-/Welttagen suchen");
        searchPanel.addSearchListener(this);
        
        pMain.add(searchPanel);
        pMain.add(pByLaw);
        pMain.add(pSpecial);
        pMain.add(pAction);

        pC.add(new JScrollPane(pMain));
        pC.setBorder(new EmptyBorder(5, 5, 5, 5));
        pC.setOpaque(false);
        
        add(pC, BorderLayout.CENTER);
        add(defaultButton.getPanel(new FlowLayout(FlowLayout.RIGHT)), BorderLayout.SOUTH);
        setOpaque(false);
    }
    
    /**
     * Initialize the panels and checkboxes.
     * @param config - Current calendar configuration to receive holiday settings from
     */
    private void initCheckboxes(Configuration config) {
        List<HolidayConstants> allByLaw = HolidayConstants.getAllByLaw();
        List<HolidayConstants> allSpecial = HolidayConstants.getAllSpecial();
        List<HolidayConstants> allAction1 = HolidayConstants.getAllAction1();
        List<HolidayConstants> allAction2 = HolidayConstants.getAllAction2();
        
        pByLaw = new JPanel(new GridLayout((allByLaw.size() + 1) / 2, 2));
        pByLaw.setBorder(GUIUtils.getTiteledBorder(IntProperty.HolidayID.getDescription()));
        pSpecial = new JPanel(new GridLayout((allSpecial.size() + 1) / 2, 2));
        pSpecial.setBorder(GUIUtils.getTiteledBorder(IntProperty.SpecialDaysID.getDescription()));
        pAction = new JPanel(new GridLayout((allAction1.size() + allAction2.size() + 1) / 2 + 1, 2));
        pAction.setBorder(GUIUtils.getTiteledBorder(IntProperty.ActionDays1ID.getDescription()));
        
        holidayBoxes = new JCheckBox[allByLaw.size()];
        for (int i = 0; i < allByLaw.size(); i++) {
            holidayBoxes[i] = new JCheckBox(allByLaw.get(i).getTitleWithDate());
            holidayBoxes[i].setSelected(allByLaw.get(i).isActive(config.getProperty(IntProperty.HolidayID)));
        }
        
        specialDayBoxes = new JCheckBox[allSpecial.size()];
        for (int i = 0; i < allSpecial.size(); i++) {
            specialDayBoxes[i] = new JCheckBox(allSpecial.get(i).getTitleWithDate());
            specialDayBoxes[i].setSelected(allSpecial.get(i).isActive(config.getProperty(IntProperty.SpecialDaysID)));
        }
        
        actionDayBoxes1 = new JCheckBox[allAction1.size()];
        int actionDayCount = 0;
        for (int i = 0; i < allAction1.size(); i++) {
            actionDayBoxes1[i] = new JCheckBox(allAction1.get(i).getTitleWithDate());
            if (allAction1.get(i).isActive(config.getProperty(IntProperty.ActionDays1ID))) {
                actionDayBoxes1[i].setSelected(true);
                actionDayCount++;
            }
        }
        actionDayBoxes2 = new JCheckBox[allAction2.size()];
        for (int i = 0; i < allAction2.size(); i++) {
            actionDayBoxes2[i] = new JCheckBox(allAction2.get(i).getTitleWithDate());
            if (allAction2.get(i).isActive(config.getProperty(IntProperty.ActionDays2ID))) {
                actionDayBoxes2[i].setSelected(true);
                actionDayCount++;
            }
        }
        
        selectAllActionDays.addToGroup(actionDayBoxes1);
        selectAllActionDays.addToGroup(actionDayBoxes2);
        if (actionDayCount == allAction1.size() + allAction2.size()) {
            // all selected
            selectAllActionDays.setState(true);
        }
    }
    
    /**
     * First clear all holiday panels and then fill the panels with the checkboxes.
     */
    private void resetCheckboxPanels() {
        // reset holidays by law
        pByLaw.removeAll();
        for (int i = 0; i < holidayBoxes.length; i++) {
            pByLaw.add(holidayBoxes[i]);
        }
        
        // reset special holidays
        pSpecial.removeAll();
        for (int i = 0; i < specialDayBoxes.length; i++) {
            pSpecial.add(specialDayBoxes[i]);
        }
        
        // reset world/action days
        pAction.removeAll();
        pAction.add(selectAllPanel);
        pAction.add(new JLabel());
        for (int i = 0; i < actionDayBoxes1.length; i++) {
            pAction.add(actionDayBoxes1[i]);
        }
        for (int i = 0; i < actionDayBoxes2.length; i++) {
            pAction.add(actionDayBoxes2[i]);
        }
    }
    
    /**
     * Returns holiday settings depending on the checkboxes selected.
     * The integer array contains the following codes:<br>
     * <li>0. holidays by law
     * <li>1. special holidays
     * <li>2. action days (part 1)
     * <li>3. action days (part 2)
     * @return holiday codes.
     */
    public int[] getHolidayCodes() {
        int[] ret = new int[4];
        ret[0] = 0;
        ret[1] = 0;
        ret[2] = 0;
        ret[3] = 0;
        
        /* encode holidays and special days */
        for (int i = 0; i < holidayBoxes.length; i++) {
            if (holidayBoxes[i].isSelected()) {
                ret[0] |= (1 << i);
            }
        }
        for (int i = 0; i < specialDayBoxes.length; i++) {
            if (specialDayBoxes[i].isSelected()) {
                ret[1] |= (1 << i);
            }
        }
        for (int i = 0; i < actionDayBoxes1.length; i++) {
            if (actionDayBoxes1[i].isSelected()) {
                ret[2] |= (1 << i);
            }
        }
        for (int i = 0; i < actionDayBoxes2.length; i++) {
            if (actionDayBoxes2[i].isSelected()) {
                ret[3] |= (1 << i);
            }
        }
        
        return ret;
    }

    @Override
    public void actionPerformed(ActionEvent a) {
        
        /*
         * Use defaults button clicked
         */
        if (a.getSource().equals(defaultButton)) {
            LOG.fine("TAB HOLIDAYS");
            int defaultByLaw = HolidayConstants.getDefaultByLaw();
            int defaultSpecial = HolidayConstants.getDefaultSpecial();
            int defaultAction1 = HolidayConstants.getDefaultAction1();
            int defaultAction2 = HolidayConstants.getDefaultAction2();
            List<HolidayConstants> allByLaw = HolidayConstants.getAllByLaw();
            List<HolidayConstants> allSpecial = HolidayConstants.getAllSpecial();
            List<HolidayConstants> allAction1 = HolidayConstants.getAllAction1();
            List<HolidayConstants> allAction2 = HolidayConstants.getAllAction2();
            
            for (int i = 0; i < allByLaw.size(); i++) {
                holidayBoxes[i].setSelected(allByLaw.get(i).isActive(defaultByLaw));
            }
            for (int i = 0; i < allSpecial.size(); i++) {
                specialDayBoxes[i].setSelected(allSpecial.get(i).isActive(defaultSpecial));
            }
            for (int i = 0; i < allAction1.size(); i++) {
                actionDayBoxes1[i].setSelected(allAction1.get(i).isActive(defaultAction1));
            }
            for (int i = 0; i < allAction2.size(); i++) {
                actionDayBoxes2[i].setSelected(allAction2.get(i).isActive(defaultAction2));
            }
        }
    }

    @Override
    public void searchRequested(String searchText) {
        /* Always: remove old search result panel (if exists). */
        if (searchResultPanel != null) {
            pMain.removeComponent(searchResultPanel);
        }
        
        if (searchText.isEmpty()) {
            /* Reset search: remove search result panel and add holiday panels again */
            resetCheckboxPanels();
            pMain.add(pByLaw);
            pMain.add(pSpecial);
            pMain.add(pAction);
        
        } else {
            /* Perform search: remove holiday panels and add the search result panel */
            searchText = searchText.toLowerCase();
            List<JCheckBox> matchedBoxes = new ArrayList<JCheckBox>();
            
            for (JCheckBox[] array : Arrays.asList(holidayBoxes, specialDayBoxes, actionDayBoxes1, actionDayBoxes2)) {
                for (JCheckBox jc : array) {
                    if (jc.getText().toLowerCase().contains(searchText)) {
                        matchedBoxes.add(jc);
                    }
                }
            }
            
            pMain.removeComponent(pByLaw);
            pMain.removeComponent(pSpecial);
            pMain.removeComponent(pAction);
            
            searchResultPanel = new JPanel(new GridLayout((matchedBoxes.size() + 2) / 2, 2));
            searchResultPanel.setBorder(GUIUtils.getTiteledBorder("Suchergebnisse mit \"" + searchText + "\""));
            
            if (matchedBoxes.isEmpty()) {
                searchResultPanel.add(new JLabel("<html><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + "keine entsprechenden Feier-/Aktionstage gefunden<br><br></html>"));
            } else {
                for (JCheckBox jc : matchedBoxes) {
                    searchResultPanel.add(jc);
                }
            }
            
            pMain.add(searchResultPanel);
        }
        
        pMain.revalidate();
    }
}
