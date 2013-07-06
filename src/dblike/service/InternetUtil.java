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

    public static String getIPList() {
        try {
            InetAddress[] ipInfo;
            InetAddress ipChecker;
            String displayIP = "";
            ipChecker = InetAddress.getLocalHost();
            ipInfo = InetAddress.getLocalHost().getAllByName(ipChecker.getHostName());
            for (int i = 0; i < ipInfo.length; i++) {
                displayIP += (ipInfo[i].getHostName() + ": " + ipInfo[i].getHostAddress());
                if(i!=ipInfo.length-1){
                    displayIP +="\n";
                }
            }
            return displayIP;
        } catch (UnknownHostException ex) {
            Logger.getLogger(InternetUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Failed to get ip";
    }

    public static String getMyIPInfo( ) {
        try {
            InetAddress ipInfo = InetAddress.getLocalHost();
            return (ipInfo.getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(InternetUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Failed";
    }
}