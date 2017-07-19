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

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.logging.Logger;

import de.jsteltze.calendar.Calendar;
import de.jsteltze.calendar.config.Const;
import de.jsteltze.common.Log;
import de.jsteltze.common.calendar.Date;
import de.jsteltze.common.io.Copy;

/**
 * Rolling backup for the calendar XML file. The calendar XML file contains all events and
 * the configuration. This small piece of code makes sure that there is a bunch of backups
 * with different ages to be able to recover data in case of losses or corruption of the
 * original file.
 * A new backup will be created if the youngest backup file has reached a certain age.
 * Old backup files will be removed if the number of backup files exceeds a certain limit.
 * @author Johannes Steltzer
 *
 */
public final class RollingBackup {
    
    /** File filter for files with file name format "Kalender-20140120.xml". */
    private static final FilenameFilter BACKUP_FILE_FILTER = new FilenameFilter() {
        
        @Override
        public boolean accept(File dir, String name) {
            return name.matches("Kalender-\\d{8}\\.xml");
        }
    };
    
    /** Date format for the backup file names. */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    
    /** 
     * Number of backup files to keep. If the number of backup files exceed this
     * limit the oldest backup files will be removed.
     */
    private static int maxBackup = 4;
    
    /**
     * The minimum age (in days) the youngest backup file may have. If the youngest backup
     * file is equal or older than this a new backup will be created. 
     */
    private static int backupAge = 7;
    
    /** Logger. */
    private static final Logger LOG = Log.getLogger(RollingBackup.class);
    
    /**
     * Hidden constructor.
     */
    private RollingBackup() { }
    
    /**
     * Create a new backup of the calendar XML file.
     * @param workingDir - Working directory that contains the calendar XML file
     */
    private static void createNewBackup(String workingDir) {
        java.util.Date now = new java.util.Date();
        String formattedDate = DATE_FORMAT.format(now);
        String fileName = "Kalender-" + formattedDate + ".xml";
        LOG.info("create a new backup (" + fileName + ")");
        File orig = new File(workingDir + File.separator + Const.XMLFILE);
        File copy = new File(workingDir + File.separator + fileName);
        new Copy(null, orig, copy).start();
    }
    
    /**
     * Returns the age (in days) of a backup file. The age will be determined by the
     * file name (format "YYMMDD") and NOT by the real file date.
     * @param backupFile - Backup file with the compulsory file name format
     * @return the age in days.
     */
    private static long getAge(File backupFile) {
        final int dateIndexStart = 9, dateIndexEnd = 17;
        String dateString = backupFile.getName().substring(dateIndexStart, dateIndexEnd);
        Date backupDate;
        try {
            backupDate = new Date(dateString);
        } catch (ParseException e) {
            /*
             *  In case the date format cannot be parsed, we assume that the age
             *  is greater than the maximum age allowed (-> new backup will be created)
             */
            return backupAge + 1;
        }
        return new Date().dayDiff(backupDate);
    }
    
    /**
     * Removes all old backup files. The number of file to remove is determined by
     * the maxBackup variable.
     * @param backupFiles - All backup files
     * @param newlyCreated - Number of newly created backup files which are not 
     * contained in the backupFiles array
     */
    private static void removeOldBackups(File[] backupFiles, int newlyCreated) {
        int filesToRemove = backupFiles.length - maxBackup + newlyCreated;
        for (int i = 0; i < filesToRemove; i++) {
            LOG.fine("remove old backup file (" + backupFiles[i].getName() + ")");
            backupFiles[i].delete();
        }
    }
    
    /**
     * Start the rolling backup mechanism. This will create a new backup if necessary
     * and remove old backup files if necessary.
     * @param parent - Parent calendar object
     */
    public static void start(Calendar parent) {
        String workingDir = parent.getWorkspace();
        File[] backupFiles = new File(workingDir).listFiles(BACKUP_FILE_FILTER);
        if (backupFiles.length == 0) {
            createNewBackup(workingDir);
            return;
        }
        Arrays.sort(backupFiles);
        File youngest = backupFiles[backupFiles.length - 1];
        int newlyCreated = 0;
        if (getAge(youngest) >= backupAge) {
            createNewBackup(workingDir);
            newlyCreated = 1;
        }
        
        removeOldBackups(backupFiles, newlyCreated);
    }
}
