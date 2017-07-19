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

import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Possible categories for events.
 * @author Johannes Steltzer
 *
 */
public final class EventCategories {

    /** List of event categories and their icons. */
    private static Map<String, Icon> iconMap = null;
    
    /** 
     * Buffered map of all scaled icons. 
     * Key of this hash map is string: 'category name' + 'size in pixels'. 
     */
    private static Map<String, Icon> bufferMap = new HashMap<String, Icon>();
    
    /** 
     * Map of all icons with their URL.  
     */
    private static Map<String, URL> urlMap = new LinkedHashMap<String, URL>();
    
    /** Prefix for all internal non-user categories (for holidays). */
    public static final String SYS_PREFIX = "SYS_";
    
    /** Special category names for holidays. */
    public static final String 
            HOLIDAY       = "Feiertag",
            HALLOWEEN     = "Halloween",
            WALPURGIS     = "Walpurgisnacht",
            CARNIVAL      = "Karneval",
            EASTER        = "Ostern",
            VALENTINE     = "Valentinstag",
            CHRISTMAS     = "Weihnachten",
            CALENDAR_DATE = "Termin";
    
    static {
        // hidden categories (for holiday events) start internal with SYS_ 
        urlMap.put(SYS_PREFIX + HOLIDAY, EventCategories.class.getResource("/media/categories/balloons20.png"));
        urlMap.put(SYS_PREFIX + HALLOWEEN, EventCategories.class.getResource("/media/categories/pumpkin20.png"));
        urlMap.put(SYS_PREFIX + WALPURGIS, EventCategories.class.getResource("/media/categories/witch20.png"));
        urlMap.put(SYS_PREFIX + CARNIVAL, EventCategories.class.getResource("/media/categories/carnival20.png"));
        urlMap.put(SYS_PREFIX + EASTER, EventCategories.class.getResource("/media/categories/easter20.png"));
        urlMap.put(SYS_PREFIX + VALENTINE, EventCategories.class.getResource("/media/categories/flower20.png"));
        urlMap.put(SYS_PREFIX + CHRISTMAS, EventCategories.class.getResource("/media/categories/candle20.png"));
        
        // public categories (for user events)
        urlMap.put(CALENDAR_DATE, EventCategories.class.getResource("/media/categories/date20.png"));
        urlMap.put("Arbeit", EventCategories.class.getResource("/media/categories/work20.png"));
        urlMap.put("Geburtstag", EventCategories.class.getResource("/media/categories/cookie20.png"));
        urlMap.put("Feier", EventCategories.class.getResource("/media/categories/balloons20.png"));
        urlMap.put("privat", EventCategories.class.getResource("/media/categories/private20.png"));
        urlMap.put("Reise", EventCategories.class.getResource("/media/categories/travel20.png"));
        urlMap.put("Spiel", EventCategories.class.getResource("/media/categories/chess20.png"));
        urlMap.put("Sport", EventCategories.class.getResource("/media/categories/sport20.png"));
        urlMap.put("Essen", EventCategories.class.getResource("/media/categories/pizza20.png"));
        urlMap.put("Pflanze", EventCategories.class.getResource("/media/categories/plant20.png"));
        urlMap.put("Musik", EventCategories.class.getResource("/media/categories/music20.png"));
        urlMap.put("Geld", EventCategories.class.getResource("/media/categories/money20.png"));
        urlMap.put("Film", EventCategories.class.getResource("/media/categories/movie20.png"));
        urlMap.put("Technik", EventCategories.class.getResource("/media/categories/tools20.png"));
        urlMap.put("wichtig", EventCategories.class.getResource("/media/categories/important20.png"));
        urlMap.put("Grün", EventCategories.class.getResource("/media/categories/green20.png"));
        urlMap.put("Blau", EventCategories.class.getResource("/media/categories/blue20.png"));
        urlMap.put("Gelb", EventCategories.class.getResource("/media/categories/yellow20.png"));
        urlMap.put("Rot", EventCategories.class.getResource("/media/categories/red20.png"));
        urlMap.put("Orange", EventCategories.class.getResource("/media/categories/orange20.png"));
        urlMap.put("Lila", EventCategories.class.getResource("/media/categories/purple20.png"));
    }
    
    /**
     * Hidden constructor.
     */
    private EventCategories() { }
    
    /**
     * Initialize the category list with default categories.
     */
    private static void init() {
        // initialize the original sized (20px) icons
        iconMap = new LinkedHashMap<String, Icon>();
        for (String cat : urlMap.keySet()) {
            iconMap.put(cat, new ImageIcon(urlMap.get(cat)));
        }
        
        // Initialize the buffer for resized icons
        for (String cat : iconMap.keySet()) {
            bufferMap.put(cat.replaceFirst("SYS_", "") + "20", iconMap.get(cat));
        }
    }
    
    /**
     * Returns the original icon (20px) of a requested category name. If the category does not exist
     * null will be returned.
     * @param category - Name of the category
     * @return Corresponding icon of the category or null.
     */
    private static Icon getIcon(String category) {
        if (iconMap == null) {
            init();
        }
        
        return iconMap.containsKey("SYS_" + category) ? iconMap.get("SYS_" + category) : iconMap.get(category);
    }
    
    /**
     * Returns the icon of a requested category name as JLabel. 
     * If the category does not exist null will be returned.
     * @param category - Name of the category
     * @param size - Destination size (icons are always squares)
     * @return JLabel with the corresponding icon of the category or null.
     */
    public static JLabel getIconAsLabel(String category, int size) {
        Icon icon = getScaledIcon(category, size);
        if (icon == null) {
            return null;
        } else {
            JLabel ret = new JLabel(icon);
            ret.setToolTipText("Kategorie: " + category);
            return ret;
        }
    }
    
    /**
     * Returns the icon of a requested category name as HTML IMG tag. 
     * If the category does not exist an empty string will be returned.
     * @param category - Name of the category
     * @param size - Destination size (icons are always squares)
     * @return &lt;img src='...' height='...' width='...'&gt;
     */
    public static String getIconAsHTML(String category, int size) {
        URL url = urlMap.containsKey(SYS_PREFIX + category) ? urlMap.get(SYS_PREFIX + category) : urlMap.get(category);
        if (url == null) {
            return "";
        } else {
            return "<img src='" + url.toString() + "' height='" + size + "' width='" + size + "'>";
        }
    }
    
    /**
     * Returns the complete list of all categories and their icons.
     * @return the complete list of all categories and their icons.
     */
    public static Map<String, Icon> getMap() {
        if (iconMap == null) {
            init();
        }
        
        return iconMap;
    }
    
    /**
     * Returns a scaled version of the category icon.
     * @param cat - Category
     * @param size - Destination size (icons are always squares)
     * @return scaled icon.
     */
    public static Icon getScaledIcon(String cat, int size) {
        if (iconMap == null) {
            init();
        }
        
        // Check if scaled icon is in buffer...
        Icon buffered = bufferMap.get(cat + size);
        if (buffered != null) {
            return buffered;
        }
        
        Icon img = getIcon(cat);
        if (img == null) {
            // no such category found...
            return null;
        }
        Image newimg = ((ImageIcon) img).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        Icon scaled = new ImageIcon(newimg);
        
        // Add to buffer...
        bufferMap.put(cat + size, scaled);
        
        return scaled;
    }
    
    /**
     * Count the number of categories in a list of events.
     * @param events - List of events from where to count the categories
     * @return a sorted hash map of categories where the key is the category
     * name and value the counter. First value is top 1 category.
     */
    public static LinkedHashMap<String, Integer> count(List<Event> events) {
        // add all categories and their count to unsorted hash map
        Map<String, Integer> map = new HashMap<>();
        for (Event e : events) {
            String cat = e.getCategory();
            if (cat != null) {
                if (map.containsKey(cat)) {
                    map.put(cat, map.get(cat) + 1);
                } else {
                    map.put(cat, 1);
                }
            }
        }
        
        // add all categories to one string list where first four characters
        // are the count as string
        List<String> tmpList = new ArrayList<>();
        for (String cat : map.keySet()) {
            tmpList.add(String.format("%4d", map.get(cat)) + cat);
        }
        // sort this list by count
        Collections.sort(tmpList, Collections.reverseOrder());
        
        // build a sorted hash map from the sorted list where the category
        // name is from the sorted list without first four characters
        LinkedHashMap<String, Integer> sortedList = new LinkedHashMap<>();
        for (String catWithCount : tmpList) {
            String cat = catWithCount.substring(4);
            sortedList.put(cat, map.get(cat));
        }
        
        return sortedList;
    }
}

