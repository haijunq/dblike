/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client.service;

import dblike.api.ServerAPI;
import dblike.client.ActiveServer;
import dblike.client.Client;
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
 * This is the listener thread to keep track on the ActiveServerList. Will be
 * used to check the current server's heartbeat.
 *
 * @author wenhanwu
 */
public class ServerListenerClient implements Runnable {

    private boolean runningFlag = true;
    private Vector<ActiveServer> ActiveServerList;

    /**
     * This is to stop the thread.
     *
     * @param flag
     */
    public void setRunningFlag(boolean flag) {
        this.runningFlag = flag;
    }

    /**
     * To get the ActiveServerList
     */
    public ServerListenerClient() {
        this.ActiveServerList = ActiveServerListClient.getActiveServerList();
    }

    /**
     * Keep checking the server's heartbeat to see if it is alive
     *
     * @return
     */
    public boolean checkCurrentServer() {
        boolean flag = true;
        int currentIndex = ClientConfig.getCurrentServerIndex();
        flag = true;
        ActiveServer aServer = ActiveServerList.get(currentIndex);
        if (aServer.getStatus() == InternetUtil.getOK()) {
            aServer.setStatus(aServer.getStatus() - 1);
        } else {
            aServer.setStatus(aServer.getStatus() - 1);
            if (aServer.getStatus() == 0) {
                //ActiveServerListClient.removeServer(aServer.getServerIP(), aServer.getPort());
                System.out.println("Server down!!!-- " + aServer.getServerIP() + ":" + aServer.getPort());
                ClientStart.aClient.pickupNewServer();
                flag = false;
            } else {
//                System.out.println("Slow connection, still connected to ["+ aServer.getServerIP() + ":" + aServer.getPort() +"]");
                System.out.print(".");
            }
        }
        return flag;
    }

    /**
     * Wait for a while
     *
     * @param timeOut
     */
    public void waitForAWhile(int timeOut) {
        try {
            Thread.sleep(timeOut * 1000);


        } catch (InterruptedException ex) {
            Logger.getLogger(ServerListenerClient.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Put the checking into a loop to keep track of the server
     */
    public void run() {
        while (runningFlag) {
            checkCurrentServer();
            waitForAWhile(InternetUtil.getTIMEOUT());
        }

    }
}
