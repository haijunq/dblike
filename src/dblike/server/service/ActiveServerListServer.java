/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import dblike.server.ActiveServer;
import java.util.ArrayList;

/**
 *
 * @author wenhanwu
 */
public class ActiveServerListServer {

    private static ArrayList<ActiveServer> ActiveServerList = new ArrayList<ActiveServer>();

    /**
     * @return the ActiveServerList
     */
    public static ArrayList<ActiveServer> getActiveServerList() {
        return ActiveServerList;
    }

    /**
     * @param aActiveServerList the ActiveServerList to set
     */
    public static void setActiveServerList(ArrayList<ActiveServer> aActiveServerList) {
        ActiveServerList = aActiveServerList;
    }

    public static ActiveServer searchServerByIP_Port(String serverIP, int port) {
        for (int i = 0; i < ActiveServerList.size(); ++i) {
            if (ActiveServerList.get(i).getServerIP().equals(serverIP) && ActiveServerList.get(i).getPort() == port) {
                return ActiveServerList.get(i);
            }
        }
        return null;
    }

    public static int checkServerByIP_Port(String serverIP, int port) {
        for (int i = 0; i < ActiveServerList.size(); ++i) {
            if (ActiveServerList.get(i).getServerIP().equals(serverIP) && ActiveServerList.get(i).getPort() == port) {
                return i;
            }
        }
        return -1;
    }

    public static boolean removeServer(String serverIP, int port) {
        if (searchServerByIP_Port(serverIP, port) == null) {
            return false;
        } else {
            ActiveServerList.remove(searchServerByIP_Port(serverIP, port));
            return true;
        }
    }

    public static boolean addServer(String serverIP, int port) {
        if (searchServerByIP_Port(serverIP, port) == null) {
            String serverID=serverIP;
            ActiveServerList.add(new ActiveServer(serverID, serverIP, port));
            System.out.println("Server [" + " " + serverIP + ":" + port + "] Added~!!!");
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean beatTheServer(String serverIP, int port) {
        int position =checkServerByIP_Port(serverIP, port);
        if (position == -1) {
            return false;
        } else {
            ActiveServerList.get(position).setStatus(1);
            return true;
        }
    }
}
