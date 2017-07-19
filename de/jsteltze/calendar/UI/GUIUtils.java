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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.jsteltze.calendar.config.Const;
import de.jsteltze.common.ui.JTextAreaContextMenu;

/**
 * General purpose GUI utilities.
 * @author Johannes Steltzer
 *
 */
public final class GUIUtils {
    
    /** GUI parent component. */
    private static Component parent = null;
    
    /**
     * Hidden constructor.
     */
    private GUIUtils() { }
    
    /**
     * Set a frame or window as parent component.
     * @param c - Component to set as parent
     */
    public static void setParentComponent(Component c) {
        parent = c;
    }
    
    /**
     * Create a titled border for a panel.
     * @param text - Label of the border
     * @param highlight - Highlight the border line?
     * @param gapLeft - Left padding for the border
     * @param gapRight - Right padding for the border
     * @param gapTop - Top padding for the border
     * @param gapBottom - Bottom padding for the border
     * @return titled border.
     */
    public static Border getTiteledBorder(String text, boolean highlight, 
            int gapLeft, int gapRight, int gapTop, int gapBottom) {
        
        Border titeledBorder = BorderFactory.createTitledBorder(
                highlight 
                    ? BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.white, Const.COLOR_BORDER_TITLE)
                    : BorderFactory.createEtchedBorder(),
                text,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                Const.FONT_BORDER_TEXT, 
                Const.COLOR_BORDER_TITLE);
        
        if (gapLeft == 0 && gapRight == 0 && gapTop == 0 && gapBottom == 0) {
            // plain titeled border without gap 
            return titeledBorder;
        } else {
            // inner titeled border within an empty border as gap on the left side
            return BorderFactory.createCompoundBorder(
                    new EmptyBorder(gapTop, gapLeft, gapBottom, gapRight), titeledBorder);
        }
    }
    
    /**
     * Create a titled border for a panel (default: no highlight).
     * @param text - Label of the border
     * @return titled border.
     */
    public static Border getTiteledBorder(String text) {
        return getTiteledBorder(text, false, 0, 0, 0, 0);
    }
    
    /**
     * Show a save-file-dialog to ask the user to choose a file for writing.
     * @param title - Text to appear in the title of the dialog
     * @param filters - File name extension filters. The first element will be the default filter
     * @param initDirectory - Initial directory
     * @param name - Suggested name for the file to save (can be null or "") for no suggestion
     * @return selected file or null.
     */
    public static File saveFileDialog(String title, FileNameExtensionFilter[] filters, 
            File initDirectory, String name) {
        // setup JFileChooser
        JFileChooser chooser = new JFileChooser();
        if (filters != null && filters.length > 0) {
            for (FileFilter filter : filters) {
                chooser.addChoosableFileFilter(filter);
            }
            chooser.setFileFilter(filters[0]);
        }
        chooser.setCurrentDirectory(initDirectory);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setDialogTitle(title);
        if (name != null && !name.isEmpty()) {
            chooser.setSelectedFile(new File(name));
        }
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        
        // get the selected file
        File dstFile = chooser.getSelectedFile();
        FileNameExtensionFilter selectedFilter = (FileNameExtensionFilter) chooser.getFileFilter();
        if (dstFile == null) {
            return null;
        }
        
        // Check if the selected file ends with one of the extensions allowed
        String[] allowedExtensions = selectedFilter.getExtensions();
        boolean match = false;
        for (String ext : allowedExtensions) {
            if (dstFile.getName().toLowerCase().endsWith(ext.toLowerCase())) {
                match = true;
                break;
            }
        }
        
        // Append the extension if necessary depending on the filter selected
        if (!match) {
            dstFile = new File(dstFile.getPath() + "." + allowedExtensions[0]);
        }

        // Check if a file selected already exists -> warning message
        if (dstFile.exists()
                && JOptionPane.showConfirmDialog(parent, "Die Datei \"" + dstFile.getName()
                        + "\" existiert bereits.\nSoll die Datei überschrieben werden?",
                        "Datei überschreiben?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
            return saveFileDialog(title, filters, initDirectory, name);
        }
        
        return dstFile;
    }
    
    /**
     * Show an error message.
     * @param intro - Description of the error (text of the error message). HTML formatting can be used!
     * @param title - Title of the error message
     * @param e - Catched exception (can be null)
     */
    public static void showErrorMessage(String intro, String title, Exception e) {
        final int textAreaHeight = 2, textAreaWidth = 80;
        final JPanel messageBody = new JPanel(new BorderLayout(20, 20));
        JLabel introLabel = new JLabel(intro);
        introLabel.setFont(Const.FONT_ERROR_INTRO);
        messageBody.add(introLabel, BorderLayout.NORTH);
        
        if (e != null) {
            JPanel exceptionBody = new JPanel(new BorderLayout());
            JTextArea exceptionText = new JTextArea(e.toString(), textAreaHeight, textAreaWidth);
            exceptionText.setLineWrap(true);
            exceptionText.setWrapStyleWord(true);
            exceptionText.setEditable(false);
            exceptionText.setFont(Const.FONT_LOGGING);
            exceptionText.setForeground(Color.darkGray);
            exceptionText.setBackground(Color.white);
            exceptionText.setComponentPopupMenu(new JTextAreaContextMenu(exceptionText));
            JLabel headerLabel = new JLabel("technische Fehlermeldung:");
            headerLabel.setForeground(Color.DARK_GRAY);
            JLabel arrowsLabel = new JLabel(">>");
            arrowsLabel.setForeground(Color.DARK_GRAY);
            exceptionBody.add(headerLabel, BorderLayout.NORTH);
            exceptionBody.add(arrowsLabel, BorderLayout.WEST);
            exceptionBody.add(new JScrollPane(exceptionText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
            messageBody.add(exceptionBody, BorderLayout.CENTER);
            messageBody.addHierarchyListener(new HierarchyListener() {
                
                @Override
                public void hierarchyChanged(HierarchyEvent e) {
                    Window window = SwingUtilities.getWindowAncestor(messageBody);
                    if (window instanceof Dialog) {
                        Dialog dialog = (Dialog) window;
                        if (!dialog.isResizable()) {
                            dialog.setResizable(true);
                        }
                    }
                }
            });
        }
        
//        JOptionPane.showMessageDialog(parent, "<html>" + intro
//                + (e == null ? "" : "<br><br>technische Fehlermeldung:<br>"
//                    + "<p style=\"font-family:monospace; background-color:white; "
//                    + "border-width:1px; border-style:solid; border-color:gray; padding:2.5em\">"
//                    + e.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")
//                        .replaceAll("\\n", "<br>") + "</p>") 
//                    + "</html>", title, JOptionPane.ERROR_MESSAGE);
        JOptionPane.showMessageDialog(parent, messageBody, title, JOptionPane.ERROR_MESSAGE);
    }
}

