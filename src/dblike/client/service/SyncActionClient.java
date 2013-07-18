/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client.service;

import dblike.api.ServerAPI;
import dblike.client.ActiveServer; 
import dblike.service.InternetUtil;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry; 
import java.util.Vector;

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

    private boolean runningFlag = true;

    public void setRunningFlag(boolean flag) {
        this.runningFlag = flag;
    }
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
            System.out.println(ex);
            return false;
        } catch (AccessException ex) {
            System.out.println(ex);
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
            //System.out.println("Someting wrong with this server..." + serverLabel);
        }
        return flag;
    }

    public void run() {
        int timeout = InternetUtil.getBEATINTERVAL() * 1000;
        ActiveServer target = ActiveServerListClient.searchServerByIP_Port(serverIP, serverPort); 
        try {
            lookupServer(target);
        } catch (RemoteException ex) {
            System.out.println(ex);
        }
        while (runningFlag) {
            beatForServer(target);
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {
            System.out.println(ex);
            }

        }
    }
}
