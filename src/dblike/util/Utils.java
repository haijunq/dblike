/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author JingboYu
 */
public class Utils {
    
    /**
     * 
     * @param dateString
     * @return 
     */
    public static Date convertTimeFromString(String dateString)
    {
        DateFormat format = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy");
        return Utils.convertTimeFromString(dateString, format);
    }
    
    /**
     * 
     * @param dateString
     * @param format
     * @return 
     */
    public static Date convertTimeFromString(String dateString, DateFormat format)
    {
        try
        {
            java.util.Date date = format.parse(dateString);
            return date;
        }
        catch(ParseException pe)
        {
            System.out.println("ERROR: could not parse date in string \"" + dateString + "\"");
            return null;
        }
    }
}
