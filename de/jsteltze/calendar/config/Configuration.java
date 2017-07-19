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

package de.jsteltze.calendar.config;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.jsteltze.calendar.config.enums.HolidayConstants;
import de.jsteltze.calendar.config.enums.OnClickDayAction;
import de.jsteltze.calendar.config.enums.OnClickEventAction;
import de.jsteltze.calendar.config.enums.OnCloseAction;
import de.jsteltze.calendar.config.enums.RemindOption;
import de.jsteltze.calendar.config.enums.Style;
import de.jsteltze.calendar.config.enums.View;
import de.jsteltze.common.Msg;

/**
 * Settings for calendar.
 * @author Johannes Steltzer
 *
 */
public class Configuration {
    
    /** All colors. */
    private Color[] colors;
    
    /** Own theme file path or null for default. */
    private String theme;
    
    /** Map of boolean properties. */
    private Map<IntProperty, Integer> intProperties;
    
    /** Map of boolean properties. */
    private Map<BoolProperty, Boolean> boolProperties;
    
    /** Map of enum properties. */
    private Map<EnumProperty, Enum<?>> enumProperties;
    
    /** Integer configuration properties. */
    public enum IntProperty {
        /** Holiday identifier. */
        HolidayID("SettingsTabHolidaysHolidays"),
        /** Special days identifier. */
        SpecialDaysID("SettingsTabHolidaysSpecialDays"),
        /** Action days identifier part 1. */
        ActionDays1ID("SettingsTabHolidaysActionDays"),
        /** Action days identifier part 1. */
        ActionDays2ID("SettingsTabHolidaysActionDays"),
        /** First day of weeks (Monday or Sunday?). */
        FirstDayOfWeek("SettingsTabGeneralWeekstart");
        
        /** The message key (in locale file) for the property description. */
        private String msgKey;
        
        /**
         * Construct a new integer property.
         * @param msgKey - The message key (in locale file) for the property description
         */
        private IntProperty(String msgKey) {
            this.msgKey = msgKey;
        }
        
        /**
         * Returns the description of the integer property (according to the message key).
         * @return the description of the integer property (according to the message key).
         */
        public String getDescription() {
            return Msg.getMessage(msgKey);
        }
        
        /**
         * Returns the integer property according to a given name.        
         * @param x - Name of the property to search
         * @return integer property with the requested name.
         */
        public static IntProperty getByName(String x) {
            for (IntProperty prop : values()) {
                if (prop.name().equals(x)) {
                    return prop;
                }
            }
            return null;
        }
    };
    
    /** Boolean configuration properties. */
    public enum BoolProperty {
        /** Auto update program on startup. */
        AutoUpdate("SettingsTabGeneralAutoUpdateBox"),
        /** Notify about / show time shift events. */
        NotifyTimeShift("SettingsTabGeneralTimeShiftBox"),
        /** Notify about / show season begin events. */
        NotifySeason("SettingsTabGeneralSeasonBox"),
        /** Show the moon. */
        ShowMoon("SettingsTabGeneralMoonBox"),
        /** Show the moon. */
        ShowZodiac("SettingsTabGeneralZodiacBox"),
        /** Display button texts. */
        ButtonTexts("SettingsTabGeneralButtonTextBox"),
        /** Start the program minimized in systray. */
        SystrayStart("SettingsTabGeneralSystrayBox"),
        /** Play a theme for notifications. */
        PlayTheme("SettingsTabGeneralPlayTheme");
        
        /** The message key (in locale file) for the property description. */
        private String msgKey;
        
        /**
         * Construct a new boolean property.
         * @param msgKey - The message key (in locale file) for the property description
         */
        private BoolProperty(String msgKey) {
            this.msgKey = msgKey;
        }
        
        /**
         * Returns the description of the boolean property (according to the message key).
         * @return the description of the boolean property (according to the message key).
         */
        public String getDescription() {
            return Msg.getMessage(msgKey);
        }
        
        /**
         * Returns the boolean property according to a given name.        
         * @param x - Name of the property to search
         * @return boolean property with the requested name.
         */
        public static BoolProperty getByName(String x) {
            for (BoolProperty prop : values()) {
                if (prop.name().equals(x)) {
                    return prop;
                }
            }
            return null;
        }
    };
    
    /** Enum configuration properties. */
    public enum EnumProperty {
        /** Default view. */
        DefaultView("SettingsTabGeneralView", View.class),
        /** Global reminder setting. */
        Remind("SettingsTabGeneralRemindOption", RemindOption.class),
        /** Action at closing the frame. */
        AtClose("SettingsTabGeneralOnCloseAction", OnCloseAction.class),
        /** Action at clicking on a day. */
        AtClickDay("SettingsTabGeneralOnClickDayAction", OnClickDayAction.class),
        /** Action at clicking on an event. */
        AtClickEvent("SettingsTabGeneralOnClickEventAction", OnClickEventAction.class),
        /** Look & Feel. */
        Style("SettingsTabGeneralStyle", Style.class);
        
        /** The message key (in locale file) for the property description. */
        private String msgKey;
        
        /** The enum class. */
        private Class<? extends Enum<?>> enumClass;
        
        /**
         * Construct a new enum property.
         * @param msgKey - The message key (in locale file) for the property description
         * @param enumClass - The enum class
         * @param <E> - Extension of abstract Enum.class
         */
        private <E extends Enum<?>> EnumProperty(String msgKey, Class<E> enumClass) {
            this.msgKey    = msgKey;
            this.enumClass = enumClass;
        }
        
        /**
         * Returns the description of the enum property (according to the message key).
         * @return the description of the enum property (according to the message key).
         */
        public String getDescription() {
            return Msg.getMessage(msgKey);
        }
        
        /**
         * Returns the enum property according to a given name.
         * @param x - Name of the property to search
         * @return enum property with the requested name.
         */
        public static EnumProperty getByName(String x) {
            for (EnumProperty prop : values()) {
                if (prop.name().equals(x)) {
                    return prop;
                }
            }
            return null;
        }
    };

    /** Default configuration. */
    public static final Configuration DEFAULT_CONFIG = new Configuration();

    /**
     * Construct a new configuration with the default settings.
     */
    public Configuration() {
        // set default general settings
        this.colors = ColorSet.DEFAULT.clone();
        this.theme  = null;
        
        // set default boolean properties
        this.intProperties = new HashMap<IntProperty, Integer>();
        this.intProperties.put(IntProperty.HolidayID, HolidayConstants.getDefaultByLaw());
        this.intProperties.put(IntProperty.SpecialDaysID, HolidayConstants.getDefaultSpecial());
        this.intProperties.put(IntProperty.ActionDays1ID, HolidayConstants.getDefaultAction1());
        this.intProperties.put(IntProperty.ActionDays2ID, HolidayConstants.getDefaultAction2());
        this.intProperties.put(IntProperty.FirstDayOfWeek, Calendar.MONDAY);
        
        // set default boolean properties
        this.boolProperties = new HashMap<BoolProperty, Boolean>();
        this.boolProperties.put(BoolProperty.AutoUpdate, true);
        this.boolProperties.put(BoolProperty.NotifyTimeShift, true);
        this.boolProperties.put(BoolProperty.NotifySeason, true);
        this.boolProperties.put(BoolProperty.ShowMoon, true);
        this.boolProperties.put(BoolProperty.ShowZodiac, true);
        this.boolProperties.put(BoolProperty.ButtonTexts, true);
        this.boolProperties.put(BoolProperty.SystrayStart, false);
        this.boolProperties.put(BoolProperty.PlayTheme, true);
        
        // set default enum properties
        this.enumProperties = new HashMap<EnumProperty, Enum<?>>();
        this.enumProperties.put(EnumProperty.DefaultView, View.month);
        this.enumProperties.put(EnumProperty.Remind, RemindOption.before1d);
        this.enumProperties.put(EnumProperty.AtClose, OnCloseAction.moveToSystray);
        this.enumProperties.put(EnumProperty.AtClickDay, OnClickDayAction.overview);
        this.enumProperties.put(EnumProperty.AtClickEvent, OnClickEventAction.open);
        this.enumProperties.put(EnumProperty.Style, Style.system);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Configuration)) {
            return false;
        }
        Configuration conf = (Configuration) o;
        
        /* Compare each single integer property. */
        for (IntProperty prop : IntProperty.values()) {
            if (this.getProperty(prop) != conf.getProperty(prop)) {
                return false;
            }
        }
        
        /* Compare each single boolean property. */
        for (BoolProperty prop : BoolProperty.values()) {
            if (this.getProperty(prop) != conf.getProperty(prop)) {
                return false;
            }
        }
        
        /* Compare each single enum property. */
        for (EnumProperty prop : EnumProperty.values()) {
            if (this.getProperty(prop) != conf.getProperty(prop)) {
                return false;
            }
        }
        
        /* Compare the remaining properties. */
        return this.colors.equals(conf.getColors())
                && (this.theme == null ? conf.getTheme() == null : this.theme.equals(conf.getTheme()));
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public Configuration clone() {
       Configuration conf = new Configuration();
       
       /* Clear integer properties. */
       conf.intProperties.clear();
       /* Copy all current properties. */
       for (IntProperty prop : this.intProperties.keySet()) {
           conf.intProperties.put(prop, this.intProperties.get(prop));
       }
       
       /* Clear boolean properties. */
       conf.boolProperties.clear();
       /* Copy all current properties. */
       for (BoolProperty prop : this.boolProperties.keySet()) {
           conf.boolProperties.put(prop, this.boolProperties.get(prop));
       }
       
       /* Clear enum properties. */
       conf.enumProperties.clear();
       /* Copy all current properties. */
       for (EnumProperty prop : this.enumProperties.keySet()) {
           conf.enumProperties.put(prop, this.enumProperties.get(prop));
       }
       
       conf.setColors(this.colors.clone());
       conf.setTheme(this.theme);
       return conf;
    }

    /**
     * Write XML configuration.
     * @param b - Stream to write
     * @throws IOException if an IO error occurred
     */
    public void write(BufferedWriter b) throws IOException {
        if (this.equals(DEFAULT_CONFIG)) {
            return;
        }
        b.write("  <Config>\n");
        
        // write colors
        for (byte i = 0x00; i < ColorSet.MAXCOLORS; i++) {
            if (!this.colors[i].equals(ColorSet.DEFAULT[i])) {
                b.write("    <Color r=\"" + this.colors[i].getRed() + "\" g=\""
                        + this.colors[i].getGreen() + "\" b=\""
                        + this.colors[i].getBlue() + "\">" + i + "</Color>\n");
            }
        }
        
        // write integer properties
        for (IntProperty prop : this.intProperties.keySet()) {
            // check if the value differs from the default config
            int value = this.intProperties.get(prop);
            if (value != DEFAULT_CONFIG.getProperty(prop)) {
                b.write("    <" + prop.name() + ">" + value + "</" + prop.name() + ">\n");
            }
        }
        
        // write boolean properties
        for (BoolProperty prop : this.boolProperties.keySet()) {
            // check if the value differs from the default config
            boolean value = this.boolProperties.get(prop);
            if (value != DEFAULT_CONFIG.getProperty(prop)) {
                b.write("    <" + prop.name() + ">" + value + "</" + prop.name() + ">\n");
            }
        }
        
        // write enum properties by their ordinals
        for (EnumProperty prop : Arrays.asList(EnumProperty.DefaultView, 
                EnumProperty.AtClose, EnumProperty.AtClickDay, EnumProperty.AtClickEvent, EnumProperty.Style)) {
            // check if the value differs from the default config
            Enum<?> value = getProperty(prop);
            if (value != DEFAULT_CONFIG.getProperty(prop)) {
                b.write("    <" + prop.name() + ">" + value.ordinal() + "</" + prop.name() + ">\n");
            }
        }
        
        // Remind is a special case of enum properties, because it is written by a specific short name
        // and not by its ordinal :-(
        RemindOption remind = (RemindOption) getProperty(EnumProperty.Remind);
        if (remind != DEFAULT_CONFIG.getProperty(EnumProperty.Remind)) {
            b.write("    <Remind>" + remind.getName(true) + "</Remind>\n");
        }
        
        // write custom theme
        if (this.theme != DEFAULT_CONFIG.theme) {
            b.write("    <Theme>" + this.theme + "</Theme>\n");
        }
        
        b.write("  </Config>\n");
    }

    /**
     * Returns the currently displayed colors.
     * @return ColorSet.
     */
    public Color[] getColors() {
        return this.colors;
    }
    
    /**
     * Set a new colors.
     * @param x - new colors to apply
     */
    public void setColors(Color[] x) {
        this.colors = x;
    }
    
    /**
     * Set a new color.
     * @param x - new color to set
     * @param index - Index of the color to change
     */
    public void setColor(Color x, int index) {
        if (index < colors.length) {
            this.colors[index] = x;
        }
    }
    
    /**
     * Returns the value of an integer property.
     * @param prop - Property to query
     * @return Integer value of the requested property.
     */
    public int getProperty(IntProperty prop) {
        return this.intProperties.get(prop);
    }
    
    /**
     * Returns the value of a boolean property.
     * @param prop - Property to query
     * @return Boolean value of the requested property. If the property does not exist,
     * false will be returned.
     */
    public boolean getProperty(BoolProperty prop) {
        Boolean ret = this.boolProperties.get(prop);
        return ret == null ? false : ret;
    }
    
    /**
     * Returns the value of an enum property.
     * @param prop - Property to query
     * @return Enum value of the requested property.
     */
    public Enum<?> getProperty(EnumProperty prop) {
        return this.enumProperties.get(prop);
    }
    
    /**
     * Set a new value for a boolean property. If the property already exists,
     * the old value will be replaced. Otherwise the property will be added to the
     * list of properties.
     * @param prop - Property which to set
     * @param value - Boolean value of the property to set
     */
    public void setProperty(BoolProperty prop, boolean value) {
        this.boolProperties.put(prop, value);
    }
    
    /**
     * Set a new value for a enum property. If the property already exists,
     * the old value will be replaced. Otherwise the property will be added to the
     * list of properties.
     * @param prop - Property which to set
     * @param value - Enum value of the property to set
     */
    public void setProperty(EnumProperty prop, Enum<?> value) {
        this.enumProperties.put(prop, value);
    }
    
    /**
     * Set a new value for a enum property. If the property already exists,
     * the old value will be replaced. Otherwise the property will be added to the
     * list of properties.
     * @param prop - Property which to set
     * @param ordinal - Ordinal of the enum value to set
     */
    public void setProperty(EnumProperty prop, int ordinal) {
        // get the generic enum constants (only possible if enumClass is of Enum<?> type, but this should be the case)
        Enum<?>[] values = prop.enumClass.getEnumConstants();
        
        // check if values array exists and contains the ordinal index
        if (values != null && values.length > ordinal && ordinal >= 0) {
            enumProperties.put(prop, values[ordinal]);
        }
    }
    
    /**
     * Set a new value for an integer property. If the property already exists,
     * the old value will be replaced. Otherwise the property will be added to the
     * list of properties.
     * @param prop - Property which to set
     * @param value - Integer value of the property to set
     */
    public void setProperty(IntProperty prop, int value) {
        this.intProperties.put(prop, value);
    }
    
    /**
     * Returns the notification theme file path.
     * @return the theme file path or null for default.
     */
    public String getTheme() {
        return this.theme;
    }
    
    /**
     * Set the theme file path or null for default.
     * @param x - new notification theme (file path) to set
     */
    public void setTheme(String x) {
        this.theme = x;
    }
}
