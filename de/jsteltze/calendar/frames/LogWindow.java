package de.jsteltze.calendar.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.jsteltze.calendar.config.Const;
import de.jsteltze.common.Log;
import de.jsteltze.common.Msg;
import de.jsteltze.common.SimpleTextPane;
import de.jsteltze.common.ui.Button;

/**
 * Frame for showing rechne.dll messages.
 * @author Johannes Steltzer
 *
 */
public final class LogWindow 
    extends JFrame 
    implements ActionListener, ItemListener {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Default window size when opened. */
    private static final Dimension DEFAULT_SIZE = new Dimension(500, 400);
    
    /** Text area displaying the event notes. */
    private SimpleTextPane logArea = new SimpleTextPane(Const.FONT_LOGGING, false,
            LogWindow.class.getResourceAsStream("/media/logo-light.png"),
            SwingConstants.CENTER);
    
    /** Button for clearing the outputs. */
    private Button clearButton = new Button("LogDialogClearButton", "/media/clear.png", this);
    
    /** Drop down menu for selecting log level. */
    private JComboBox<String> logLevelList = new JComboBox<>();
    
    /** Log window singleton instance. */
    private static LogWindow singleton;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(LogWindow.class);
    
    /**
     * Returns the instance of this log frame. This is a singleton frame.
     * If an instance already exists -> return this one. Else create a new one.
     * @return singleton instance of this frame.
     */
    public static LogWindow getInstance() {
        if (singleton == null) {
            LOG.fine("creating a new LogWindow instance");
            singleton = new LogWindow();
        }
        
        return singleton;
    }

    /**
     * Private constructor for log window. This is a singleton instance.
     * Use {@link #getInstance()} to get an instance of this window.
     */
    private LogWindow() {
        super(Msg.getMessage("LogDialogTitle"));
        setIconImage(new ImageIcon(LogWindow.class.getResource("/media/console32.png")).getImage());
        setLayout(new BorderLayout());
        
        logArea.setBackground(Color.white);
        logArea.setCaretPosition(0);
        logArea.addStyle(Const.FONT_LOGGING, Color.red, Level.WARNING.getName());
        logArea.addStyle(Const.FONT_LOGGING.deriveFont(Font.BOLD), Color.red, Level.SEVERE.getName());
        logArea.addStyle(Const.FONT_LOGGING.deriveFont(Font.BOLD), Color.black, Level.INFO.getName());
        logArea.addStyle(Const.FONT_LOGGING, Color.darkGray, Level.FINE.getName());
        JScrollPane scrollPane = logArea.getScrollableComponent();
        scrollPane.setBackground(Color.white);
        
        // list of supported log levels
        logLevelList.addItem(Level.ALL.getName());
        logLevelList.addItem(Level.FINE.getName());
        logLevelList.addItem(Level.INFO.getName());
        logLevelList.addItem(Level.WARNING.getName());
        logLevelList.addItem(Level.SEVERE.getName());
        logLevelList.setSelectedItem(Log.getLevel().getName());
        logLevelList.addItemListener(this);
        
        JPanel logLevelPanel = new JPanel();
        logLevelPanel.add(new JLabel(Msg.getMessage("LogDialogLevelLabel")));
        logLevelPanel.add(logLevelList);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(clearButton.getPanel(), BorderLayout.EAST);
        southPanel.add(logLevelPanel, BorderLayout.WEST);
                
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.add(southPanel, BorderLayout.SOUTH);
        wrapper.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        add(wrapper, BorderLayout.CENTER);
        setSize(DEFAULT_SIZE);
        setLocationRelativeTo(null);
    }
    
    /**
     * Returns the text area of the log window.
     * @return the text area of the log window.
     */
    public SimpleTextPane getTextArea() {
        return logArea;
    }

    @Override
    public void actionPerformed(ActionEvent a) {
        if (a.getSource().equals(clearButton)) {
            logArea.clear();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent ie) {
        if (ie.getStateChange() == ItemEvent.SELECTED) {
            // apply new log level (globally)
            LOG.info("new log level: " + logLevelList.getSelectedItem().toString());
            Log.setLevel(logLevelList.getSelectedItem().toString());
        }
    }
}
