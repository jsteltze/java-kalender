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

package de.jsteltze.calendar;

import java.io.File;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import de.jsteltze.calendar.config.Const;
import de.jsteltze.common.Download;
import de.jsteltze.common.Log;
import de.jsteltze.common.ProgressBar;

/**
 * Calendar update. Connects to the Internet and looks for new version.
 * @author Johannes Steltzer
 *
 */
public class Update 
    implements Runnable {
    
    /** Different update steps. */
    private static enum Step {
        /** Download the release file. */
        GET_RELEASE,
        /** Download the new calendar version. */
        GET_NEW_VERSION,
        /** Download the updater program. */
        GET_UPDATER,
        /** Finish (restart). */
        FINISH
    }

    /** Progress bar. */
    private ProgressBar pbar;
    
    /** Downloader. */
    private Download down;
    
    /** Download timeout: 1 minute. */
    private static final int TIMEOUT_MSEC = 60000;
    
    /** Update interval: half a second. */
    private static final int UPDATE_MSEC = 500;
    
    /** Calling parent calendar object to update. */
    private Calendar caller; 
    
    /** The current update step. */
    private Step step;
    
    /** Quite mode? */
    private boolean quiet;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(Update.class);

    /**
     * Construct a new update. This will connect to the Internet and
     * look for new version.
     * @param c - Parent calendar object
     * @param quiet - True for quiet mode (no messages), to be used for auto update
     */
    public Update(Calendar c, boolean quiet) {
        this.caller = c;
        this.quiet = quiet;
        step = Step.GET_RELEASE;
        try {
            if (!quiet) {
                pbar = new ProgressBar(caller.getGUI().getFrame(), "Suche neue Version...", false);
            } else {
                pbar = null;
            }
            down = new Download(new URL(Const.DOWNLOAD_URL + Const.RELEASE_FILE), Const.RELEASE_FILE);
            new Thread(this).start();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            
            caller.errorOccurred("<html>Der Download ist fehlgeschlagen.<br>"
                    + "Eventuell konnte keine Verbindung zum Internet hergestellt werden!</html>",
                    "Update fehlgeschlagen", e);
        }
    }
    
    /**
     * Parse remove version file and check if update is available.
     * If yes, start downloading new version.
     */
    private void getNewVersion() {
        try {
            LOG.fine("Download status=" + down.getStatus());
            
            /*
             * Throw error if previous download was incomplete
             */
            if (down.getStatus() != Download.Status.COMPLETE) {
                if (!quiet) {
                    error("<html>Der Download ist fehlgeschlagen!<br>Download-Status: " + down.getStatus().name()
                            + "</html>", null);
                }
                return;
            }
            
            /*
             * Try parsing remote release version
             */
            File release = new File(Const.RELEASE_FILE);
            Scanner sc = new Scanner(release);
            String versionLine = sc.nextLine();
            String description = "";
            while (sc.hasNext()) {
                description += sc.nextLine();
            }
            sc.close();
            release.delete();
            LOG.fine("READ version: " + versionLine);
            boolean updateNeeded = false;
            try {
                updateNeeded = compareVersion(versionLine, Const.VERSION) < 0;
            } catch (Exception e) {
                if (!quiet) {
                    error("Die heruntergeladene Versionsdatei kann nicht gelesen werden!", e);
                }
                return;
            }
            
            /*
             * Proceed if remote version was readable and new release
             * is available
             */
            if (updateNeeded) {
                if (quiet) {
                    caller.getGUI().getFrame().updateAvailable(versionLine);
                    return;
                }
                
                /*
                 * Notify user and wait for OK
                 */
                if (JOptionPane.showConfirmDialog(caller.getGUI().getFrame(), 
                        "<html><b>Es ist ein Update verfügbar: Version " + versionLine + "</b><br><br>"
                        + (description.isEmpty() ? "" : "<p style=\"font-family:monospace; background-color:white; "
                                + "border-width:1px; border-style:solid; border-color:gray; padding:2.5em\">"
                                + description + "</p>")
                        + "<br>Soll die neue Version jetzt heruntergeladen und installiert werden?</html>",
                        "Update verfügbar", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                    return;
                }
                
                /*
                 * Download new version
                 */
                pbar = new ProgressBar(caller.getGUI().getFrame(), "Neue Version herunterladen...", false);
                down = new Download(new URL(Const.DOWNLOAD_URL + Const.FILENAME), Const.NEW_FILENAME);
                new Thread(this).start();
            
            } else {
                if (quiet) {
                    LOG.fine("no update needed");
                } else {
                    JOptionPane.showMessageDialog(caller.getGUI().getFrame(), 
                            "<html>Der Kalender ist auf dem aktuellen Stand!<br><br><table>"
                            + "<tr><td align=\"right\">Eigene Version:</td><td>" + Const.VERSION + "</td></tr>"
                            + "<tr><td align=\"right\">Aktuelle Version:</td><td>" + versionLine + "</td></tr>"
                                    + "</table></html>", "Update...", JOptionPane.PLAIN_MESSAGE);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            error("<html>Während des Update-Vorgangs ist ein Fehler aufgetreten!"
                    + "<br>Das Update wird abgebrochen.</html>", e);
        }        
    }
    
    /**
     * Compare two calendar version strings of the form "X.Y_svnZ".
     * @param v1 - Version string 1
     * @param v2 - Version string 2
     * @return -1 if v1 is greater than, 0 if equal to or 1 if less than v2.
     */
    private int compareVersion(String v1, String v2) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] v2Main = v2.split("\\.");
        String[] v1Main = v1.split("\\.");
        String[] v2Sub = v2Main[1].split("_");
        String[] v1Sub = v1Main[1].split("_");
        
        /* remove the "svn" */
        v2Sub[1] = v2Sub[1].substring(3);
        v1Sub[1] = v1Sub[1].substring(3);
        
        int compareResult;
        compareResult = new Integer(v2Main[0]).compareTo(new Integer(v1Main[0]));
        if (compareResult != 0) {
            return compareResult;
        }
        compareResult = new Integer(v2Sub[0]).compareTo(new Integer(v1Sub[0]));
        if (compareResult != 0) {
            return compareResult;
        }
        compareResult = new Integer(v2Sub[1]).compareTo(new Integer(v1Sub[1]));
        return compareResult;
    }
    
    /**
     * Start downloading updater program.
     */
    private void getUpdater() {
        try {
            LOG.fine("Download status=" + down.getStatus());
            
            /*
             * Throw error if previous download was incomplete
             */
            if (down.getStatus() != Download.Status.COMPLETE) {
                error("<html>Der Download ist fehlgeschlagen!<br>Download-Status: " + down.getStatus().name()
                        + "</html>", null);
                return;
            }
            
            /*
             * Download updater program
             */
            pbar = new ProgressBar(caller.getGUI().getFrame(), "Updater herunterladen...", false);
            down = new Download(new URL(Const.DOWNLOAD_URL + Const.UPDATER));
            new Thread(this).start();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "cannot download updater...", e);
            error("<html>Während des Update-Vorgangs ist ein Fehler aufgetreten!<br>Das Update wird abgebrochen."
                    + "</html>", e);
        }
    }
    
    /**
     * Finalize update by calling updater program. This will
     * exit current program, delete old version, rename new version
     * and start new version. 
     */
    private void finishUpdate() {
        try {
            LOG.fine("Download status=" + down.getStatus());
            if (down.getStatus() != Download.Status.COMPLETE) {
                error("<html>Der Download ist fehlgeschlagen!<br>Download-Status: " + down.getStatus().name()
                        + "</html>", null);
                return;
            }
            JOptionPane.showMessageDialog(caller.getGUI().getFrame(), "Um das Update abzuschließen,"
                    + "\nmuss der Kalender neu gestartet werden.", "Update...", JOptionPane.PLAIN_MESSAGE);
            
            String ownJarName = Const.FILENAME;
            try {
                ownJarName = new File(Update.class.getProtectionDomain()
                        .getCodeSource().getLocation().toURI()).getName();
            } catch (Exception e1) {
                LOG.log(Level.WARNING, "cannot obtain own jar name...", e1);
            }
            
            /*
             * Execute updater
             */
            String cmd = "java -jar " + Const.UPDATER + " \""
                    + ownJarName + "\" \"" + Const.NEW_FILENAME + "\"";
            String[] args = caller.getArgs();
            for (String arg : args) {
                cmd += " " + arg;
            }
            Runtime.getRuntime().exec(cmd);
            
            /* call shutdown hook */
            System.exit(0);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            new File(Const.NEW_FILENAME).delete();
            new File(Const.UPDATER).delete();
            error("<html>Während des Update-Vorgangs ist ein Fehler aufgetreten!"
                    + "<br>Das Update wird abgebrochen.</html>", e);
        }
    }

    /**
     * Proceed with a next step.
     */
    private void next() {
        switch (step) {
        case GET_NEW_VERSION:
            getNewVersion();
            break;
        case GET_UPDATER:
            getUpdater();
            break;
        case FINISH:
            finishUpdate();
            break;
        default:
            break;
        }
    }

    @Override
    public void run() {
        int i = 0;
        
        /*
         * While downloading, update progressbar each UPDATE_MSEC
         * milliseconds
         */
        while (down.getStatus() == Download.Status.DOWNLOADING
                || i++ > TIMEOUT_MSEC / UPDATE_MSEC) {
            if (pbar != null) {
                pbar.setValue((int) down.getProgress());
                if (pbar.isCancelled()) {
                    down.cancel();
                    new File(Const.NEW_FILENAME).delete();
                    new File(Const.RELEASE_FILE).delete();
                    new File(Const.UPDATER).delete();
                    pbar.close();
                    return;
                }
            }
            try {
                Thread.sleep(UPDATE_MSEC);
            } catch (InterruptedException e) {
                LOG.log(Level.WARNING, "sleep interrupted", e);
            }
        }
        
        /* Close progressbar and proceed with next step */
        if (pbar != null) {
            pbar.close();
        }
        
        /* Increment step */
        step = Step.values()[step.ordinal() + 1];
        next();
    }

    /**
     * Display error message.
     * @param msg - Error message to display
     * @param e - Catched exception (can be null)
     */
    private void error(String msg, Exception e) {
        caller.errorOccurred(msg, "Update fehlgeschlagen", e);
        
        if (pbar != null) {
            pbar.close();
        }
        
        // clean up downloaded files
        new File(Const.RELEASE_FILE).delete();
        new File(Const.NEW_FILENAME).delete();
        new File(Const.UPDATER).delete();
    }
}
