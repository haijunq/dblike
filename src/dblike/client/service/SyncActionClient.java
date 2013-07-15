/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client.service;

import dblike.api.ServerAPI;
import dblike.client.ActiveServer;
import dblike.client.ClientStart;
import dblike.service.InternetUtil;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wenhanwu
 */
public class SyncActionClient implements Runnable {

    private static Vector<ActiveServer> ActiveServerList; 
    private String clientID;
    private String deviceID;
    private String serverIP;
    private int serverPort;

    /**
     * @return the serverIP
     */
    public String getServerIP() {
        return serverIP;
    }

    /**
     * @param serverIP the serverIP to set
     */
    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    /**
     * @return the serverPort
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * @param serverPort the serverPort to set
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * @return the clientID
     */
    public String getClientID() {
        return clientID;
    }

    /**
     * @param clientID the clientID to set
     */
    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    /**
     * @return the deviceID
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * @param deviceID the deviceID to set
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public SyncActionClient() {
        SyncActionClient.ActiveServerList = ActiveServerListClient.getActiveServerList();
    }

    public boolean lookupServer(ActiveServer target) throws RemoteException {
        try {
            target.setRegistry(LocateRegistry.getRegistry(target.getServerIP(), target.getPort()));
            target.setServerAPI((ServerAPI) (target.getRegistry()).lookup("serverUtility"));
        } catch (NotBoundException ex) {
            Logger.getLogger(SyncActionClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (AccessException ex) {
            Logger.getLogger(SyncActionClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public boolean beatForServer(ActiveServer target) {
        boolean flag = true;
        String serverLabel = target.getServerIP() + target.getPort();
        try {
            target.getServerAPI().beatFromClient(clientID, deviceID);
        } catch (RemoteException ex) {
            System.out.println("Someting wrong with this server..." + serverLabel);
        }
        return flag;
    }

    public void run() {
        int timeout = InternetUtil.getBEATINTERVAL() * 1000;
        ActiveServer target = ActiveServerListClient.searchServerByIP_Port(serverIP, serverPort);
        System.out.println("Will look up for--->" + target.getServerIP() + ":" + target.getPort());
        try {
            lookupServer(target);
        } catch (RemoteException ex) {
            Logger.getLogger(SyncActionClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            beatForServer(target);
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {
                Logger.getLogger(SyncActionClient.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
