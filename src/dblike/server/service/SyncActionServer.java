/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import dblike.api.ClientAPI;
import dblike.api.ServerAPI;
import dblike.server.ActiveClient;
import dblike.server.ActiveServer;
import dblike.server.ServerStart;
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
public class SyncActionServer implements Runnable {

    private static ArrayList<ActiveClient> ActiveClientList;
    private static ArrayList<ActiveServer> ActiveServerList;
    private Registry registry;
    private ClientAPI client = null;
    private ServerAPI server = null;

    public SyncActionServer() {
        SyncActionServer.ActiveClientList = ActiveClientListServer.getActiveClientList();
        SyncActionServer.ActiveServerList = ActiveServerListServer.getActiveServerList();
    }

    public boolean lookupClient(ActiveClient target) throws RemoteException {
        try {
            registry = LocateRegistry.getRegistry(target.getClientIP(), target.getPort());
            String lookupClient = "clientUtility" + target.getClientID() + target.getDeviceID() + target.getClientIP() + target.getPort();
            client = (ClientAPI) registry.lookup(lookupClient);
        } catch (NotBoundException ex) {
            Logger.getLogger(SyncActionServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (AccessException ex) {
            Logger.getLogger(SyncActionServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public boolean lookupServer(ActiveServer target) throws RemoteException {
        try {
            registry = LocateRegistry.getRegistry(target.getServerIP(), target.getPort());
            server = (ServerAPI) registry.lookup("serverUtility");
        } catch (NotBoundException ex) {
            Logger.getLogger(SyncActionServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (AccessException ex) {
            Logger.getLogger(SyncActionServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public boolean beatForAllClient() {
        boolean flag = true;
        for (int i = 0; i < ActiveClientList.size(); i++) {
            ActiveClient aClient = ActiveClientList.get(i);
            String clientLabel = aClient.getClientID() + aClient.getDeviceID();
            try {
                if (lookupClient(aClient) == false) {
                    System.out.println("Failed to look up " + clientLabel);
                } else {
                    client.beatFromServer(ServerStart.getServerIP(), ServerStart.getPORT());
                }
            } catch (RemoteException ex) {
                System.out.println("Someting wrong with this client..." + clientLabel);
            }

        }
        return flag;
    }

//    public boolean beatForAllServer() {
//        boolean flag = true;
//        for (int i = 0; i < ActiveServerList.size() ; i++) {
//            try {
//                ActiveServer aServer = ActiveServerList.get(i);
//                if (lookupServer(aServer) == false) {
//                    String serverLabel = aServer.getServerIP() + aServer.getPort();
//                    ActiveServerListServer.removeServer(aServer.getServerIP(), aServer.getPort());
//                    System.out.println(serverLabel + " not available");
//                } else {
//                    server.beatFromServer(ServerStart.getServerIP(), ServerStart.getPORT());
//                }
//            } catch (RemoteException ex) {
//                Logger.getLogger(SyncActionServer.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        }
//        return flag;
//    }
    public boolean beatForAllServer() {
        boolean flag = true;
        for (int i = 0; i < ActiveServerList.size(); i++) {
            ActiveServer aServer = ActiveServerList.get(i);
            String serverLabel = aServer.getServerIP() + aServer.getPort();
            try {
                if (lookupServer(aServer) == false) {
                    System.out.println("Failed to look up " + serverLabel);
                } else {
                    server.beatFromServer(ServerStart.getServerIP(), ServerStart.getPORT());
                }
            } catch (RemoteException ex) {
                System.out.println("Someting wrong with this server..." + serverLabel);
            }

        }
        return flag;
    }

    public void run() {
        int timeout = InternetUtil.getBEATINTERVAL() * 1000;
        while (true) {
            beatForAllClient();
            beatForAllServer();
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClientListenerServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}