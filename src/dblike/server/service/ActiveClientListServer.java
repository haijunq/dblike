/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import dblike.server.ActiveClient;
import dblike.service.InternetUtil;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 *
 * @author wenhanwu
 */
public class ActiveClientListServer {

    private static Vector<ActiveClient> ActiveClientList = new Vector<ActiveClient>();

    /**
     * @return the ActiveClientListServer
     */
    public static Vector<ActiveClient> getActiveClientList() {
        return ActiveClientList;
    }

    /**
     * @param aActiveClientList the ActiveClientListServer to set
     */
    public static void setActiveClientList(Vector<ActiveClient> aActiveClientList) {
        ActiveClientList = aActiveClientList;
    }

    public static ActiveClient searchClientbyID(String clientID, String deviceID) {
        for (int i = 0; i < ActiveClientList.size(); ++i) {
            if (ActiveClientList.get(i).getClientID().equals(clientID) && ActiveClientList.get(i).getDeviceID().equals(deviceID)) {
                return ActiveClientList.get(i);
            }
        }
        return null;
    }

    public static int checkClientbyID(String clientID, String deviceID) {
        for (int i = 0; i < ActiveClientList.size(); ++i) {
            if (ActiveClientList.get(i).getClientID().equals(clientID) && ActiveClientList.get(i).getDeviceID().equals(deviceID)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean removeClient(String clientID, String deviceID) {
        if (searchClientbyID(clientID, deviceID) == null) {
            return false;
        } else {
            ActiveClientList.remove(searchClientbyID(clientID, deviceID));
            return true;
        }
    }

    public static boolean addClient(String clientID, String deviceID, String clientIP, int clientPort) {
        if (searchClientbyID(clientID, clientIP) == null) {
            ActiveClientList.add(new ActiveClient(clientID, deviceID, clientIP, clientPort));
            System.out.println("[" + clientID + " " + deviceID + " " + clientIP + "] Login~!!!");
            return true;
        } else {
            return false;
        }
    }

    public static boolean beatTheClient(String clientID, String deviceID) {
        int position =checkClientbyID(clientID, deviceID);
        if (position == -1) {
            return false;
        } else {
            ActiveClientList.get(position).setStatus(InternetUtil.getOK());
            return true;
        }
    }
}
