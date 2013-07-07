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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wenhanwu
 */
public class SyncActionClient  implements Runnable{

    private static ArrayList<ActiveServer> ActiveServerList;
    private Registry registry;
    private ServerAPI server = null;

    public SyncActionClient() {
        SyncActionClient.ActiveServerList = ActiveServerListClient.getActiveServerList();
    }

    public boolean lookupServer(ActiveServer target) throws RemoteException {
        try {
            registry = LocateRegistry.getRegistry(target.getServerIP(), target.getPort());
            server = (ServerAPI) registry.lookup("serverUtility");
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
            server.beatFromClient(ClientStart.getClientID(), ClientStart.getDeviceID());
        } catch (RemoteException ex) {
            System.out.println("Someting wrong with this server..." + serverLabel);
        }
        return flag;
    }

    public void run() {
        int timeout = InternetUtil.getBEATINTERVAL() * 1000;
        ActiveServer target = ActiveServerListClient.searchServerByIP_Port(ClientStart.getServerIP(), ClientStart.getServerPort());
            System.out.println("Will look up for--->"+target.getServerIP()+":"+target.getPort());
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
