/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wenhanwu
 */
public class InternetUtil {

    private static int TIMEOUT = 3;
    private static int BEATINTERVAL = 1;
    private static int OK = 5;
    private static int CHANGESERVERINTERVAL = 5;

    /**
     * @return the CHANGESERVERINTERVAL
     */
    public static int getCHANGESERVERINTERVAL() {
        return CHANGESERVERINTERVAL;
    }

    /**
     * @param aCHANGESERVERINTERVAL the CHANGESERVERINTERVAL to set
     */
    public static void setCHANGESERVERINTERVAL(int aCHANGESERVERINTERVAL) {
        CHANGESERVERINTERVAL = aCHANGESERVERINTERVAL;
    }

    /**
     * @return the OK
     */
    public static int getOK() {
        return OK;
    }

    /**
     * @param aOK the OK to set
     */
    public static void setOK(int aOK) {
        OK = aOK;
    }

    /**
     * @return the BEATINTERVAL
     */
    public static int getBEATINTERVAL() {
        return BEATINTERVAL;
    }

    /**
     * @return the TIMEOUT
     */
    public static int getTIMEOUT() {
        return TIMEOUT;
    }

    public static String getIPList() {
        try {
            InetAddress[] ipInfo;
            InetAddress ipChecker;
            String displayIP = "Network Devices List:\n";
            ipChecker = InetAddress.getLocalHost();
            ipInfo = InetAddress.getLocalHost().getAllByName(ipChecker.getHostName());
            for (int i = 0; i < ipInfo.length; i++) {
                displayIP += (ipInfo[i].getHostName() + ": " + ipInfo[i].getHostAddress());
                if (i != ipInfo.length - 1) {
                    displayIP += "\n";
                }
            }
            return displayIP;
        } catch (UnknownHostException ex) {
            Logger.getLogger(InternetUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Failed to get ip";
    }

    public static String getMyIPInfo() {
        try {
            InetAddress ipInfo = InetAddress.getLocalHost();
            return (ipInfo.getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(InternetUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Failed";
    }
}