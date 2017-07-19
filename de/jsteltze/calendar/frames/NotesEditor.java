package de.jsteltze.calendar.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.Event;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.common.ui.Button;
import de.jsteltze.common.ui.JTextAreaContextMenu;

/**
 * Frame for showing / editing events notes.
 * @author Johannes Steltzer
 *
 */
public class NotesEditor 
    extends JDialog 
    implements DocumentListener, ActionListener {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Default window size when opened. */
    private static final Dimension DEFAULT_SIZE = new Dimension(350, 250);
    
    /** Button for saving changes. */
    private Button saveButton = new Button("NotesEditorSaveButton", "/media/save16.png", this);
    
    /** Event with notes to be displayed. */
    private Event event;
    
    /** Parent calendar object. */
    private Calendar parent;
    
    /** Text area displaying the event notes. */
    private JTextArea notesArea;

    /**
     * Open a editor dialog showing the notes of an event.
     * @param parent - Parent calendar object
     * @param event - Event of subject
     */
    public NotesEditor(Calendar parent, Event event) {
        
        super(parent.getGUI().getFrame(), "Notizen zu \"" + event.getName() + "\"");
        this.event = event;
        this.parent = parent;
        String notes = event.getNotes(parent.getWorkspace());
        
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel notesIconLabel = new JLabel(
                new ImageIcon(Notification.class.getResource("/media/notes20.png")));
        northPanel.add(notesIconLabel);
        northPanel.add(new JLabel("Notizen zu \"" + event.getName() + "\":"));
        
        notesArea = new JTextArea(notes);
//        notesArea.setEditable(false);
        notesArea.setFont(Const.FONT_EVENT_NOTES);
//        notesArea.setForeground(Color.gray);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setComponentPopupMenu(new JTextAreaContextMenu(notesArea));
        notesArea.setText(notes);
        notesArea.setCaretPosition(0);
        notesArea.getDocument().addDocumentListener(this);
        JScrollPane pScroll = new JScrollPane(notesArea);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        JLabel pathLabel = new JLabel(
                Const.EVENT_DIR + File.separator + event.getID() + File.separator + Const.NOTES_FILE);
        pathLabel.setForeground(Color.GRAY);
        saveButton.setEnabled(false);
        southPanel.add(pathLabel, BorderLayout.WEST);
        southPanel.add(saveButton, BorderLayout.EAST);
                
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(northPanel, BorderLayout.NORTH);
        wrapper.add(pScroll, BorderLayout.CENTER);
        wrapper.add(southPanel, BorderLayout.SOUTH);
        wrapper.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        setLayout(new BorderLayout());
        add(wrapper, BorderLayout.CENTER);
        setSize(DEFAULT_SIZE);
        setLocationRelativeTo(parent.getGUI().getFrame());
        setAlwaysOnTop(true);
        setVisible(true);
    }
    
    @Override
    public void removeUpdate(DocumentEvent e) {
        saveButton.setEnabled(true);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        saveButton.setEnabled(true);
    }

    @Override
    public void changedUpdate(DocumentEvent arg0) {
        saveButton.setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        saveButton.setEnabled(false);
        event.writeNotes(notesArea.getText(), parent);
    }
}
