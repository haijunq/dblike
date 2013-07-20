/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client.service;

import dblike.client.ActiveServer;
import dblike.service.InternetUtil;
import java.util.Vector;

/**
 * This is to maintain the list of active servers, which is a static list. It is
 * used to keep track of all servers Also will be used by other class.
 *
 * @author wenhanwu
 */
public class ActiveServerListClient {

    private static Vector<ActiveServer> ActiveServerList = new Vector<ActiveServer>();

    /**
     * @return the ActiveServerList
     */
    public static Vector<ActiveServer> getActiveServerList() {
        return ActiveServerList;
    }

    /**
     * @param aActiveServerList the ActiveServerList to set
     */
    public static void setActiveServerList(Vector<ActiveServer> aActiveServerList) {
        ActiveServerList = aActiveServerList;
    }

    /**
     * Search the server on the list by its IP and port.
     *
     * @param serverIP
     * @param port
     * @return the server
     */
    public static ActiveServer searchServerByIP_Port(String serverIP, int port) {
        for (int i = 0; i < ActiveServerList.size(); ++i) {
            if (ActiveServerList.get(i).getServerIP().equals(serverIP) && ActiveServerList.get(i).getPort() == port) {
                return ActiveServerList.get(i);
            }
        }
        return null;
    }

    /**
     * Check if there is a server has the given IP and port. If there is, return
     * the index, if not, return -1.
     *
     * @param serverIP
     * @param port
     * @return
     */
    public static int checkServerByIP_Port(String serverIP, int port) {
        for (int i = 0; i < ActiveServerList.size(); ++i) {
            if (ActiveServerList.get(i).getServerIP().equals(serverIP) && ActiveServerList.get(i).getPort() == port) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Remove the server from the list.
     *
     * @param serverIP
     * @param port
     * @return true for success, false for cannot find
     */
    public static boolean removeServer(String serverIP, int port) {
        if (searchServerByIP_Port(serverIP, port) == null) {
            return false;
        } else {
            ActiveServerList.remove(searchServerByIP_Port(serverIP, port));
            return true;
        }
    }

    /**
     * Add an active server object to the list.
     *
     * @param serverIP
     * @param port
     * @return true if successfully added, false if already exist
     */
    public static boolean addServer(String serverIP, int port) {
        if (searchServerByIP_Port(serverIP, port) == null) {
            String serverID = serverIP;
            ActiveServerList.add(new ActiveServer(serverID, serverIP, port));
            System.out.println("Server [" + " " + serverIP + ":" + port + "] Added~!!!");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Do the heart beat for the given server. Will be called by the server.
     *
     * @param serverIP
     * @param port
     * @return
     */
    public static boolean beatCurrentServer(String serverIP, int port) {
        int position = checkServerByIP_Port(serverIP, port);
//         System.out.println("position=" + position);
        if (position == -1) {
            return false;
        } else {
            if (position != ClientConfig.getCurrentServerIndex()) {
                System.out.println("Error, the server will be beat is not the current server!!!");
            }
            ActiveServerList.get(position).setStatus(InternetUtil.getOK());
            return true;
        }
    }
}
