/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import dblike.server.ActiveClient;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author wenhanwu
 */
public class ActiveClientListServer {

    private static ArrayList<ActiveClient> ActiveClientList = new ArrayList<ActiveClient>();

    /**
     * @return the ActiveClientListServer
     */
    public static ArrayList<ActiveClient> getActiveClientList() {
        return ActiveClientList;
    }

    /**
     * @param aActiveClientList the ActiveClientListServer to set
     */
    public static void setActiveClientList(ArrayList<ActiveClient> aActiveClientList) {
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
}
