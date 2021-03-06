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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * This class is to keep sending heartbeat to the servers and clients.
 *
 * @author wenhanwu
 */
public class SyncActionServer implements Runnable {

    private static Vector<ActiveClient> ActiveClientList;
    private static Vector<ActiveServer> ActiveServerList;
    private boolean runningFlag = true;

    /**
     * This is to stop the thread.
     *
     * @param flag
     */
    public void setRunningFlag(boolean flag) {
        this.runningFlag = flag;
    }

    /**
     * To get the ActiveClientList and the ActiveServerList.
     */
    public SyncActionServer() {
        SyncActionServer.ActiveClientList = ActiveClientListServer.getActiveClientList();
        SyncActionServer.ActiveServerList = ActiveServerListServer.getActiveServerList();
    }

    /**
     * Look up the client's API to do the heartbeat for the client.
     *
     * @param target
     * @return
     * @throws RemoteException
     */
    public boolean lookupClient(ActiveClient target) throws RemoteException {
        try {
            target.setRegistry(LocateRegistry.getRegistry(target.getClientIP(), target.getPort()));
            String lookupClient = "clientUtility" + target.getClientID() + target.getDeviceID() + target.getClientIP() + target.getPort();
            target.setClientAPI((ClientAPI) (target.getRegistry()).lookup(lookupClient));
//            target.getClientAPI().printMsg();
        } catch (NotBoundException ex) {
            System.out.println(ex);
            return false;
        } catch (AccessException ex) {
            System.out.println(ex);
            return false;
        }
        return true;
    }

    /**
     * Look up the server's API to do the heartbeat for the server.
     *
     * @param target
     * @return
     * @throws RemoteException
     */
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

    /**
     * Beat for all clients in the list.
     *
     * @return
     */
    public boolean beatForAllClient() {
        boolean flag = true;
//        System.out.println("in total "+ActiveClientList.size());
        for (int i = 0; i < ActiveClientList.size(); i++) {
            ActiveClient aClient = ActiveClientList.get(i);
            String clientLabel = aClient.getClientID() + aClient.getDeviceID();
//            System.out.println(clientLabel);
            try {
                if (lookupClient(aClient) == false) {
                    System.out.println("Failed to look up " + clientLabel);
                } else {
                    aClient.getClientAPI().beatFromServer(ServerStart.getServerIP(), ServerStart.getPORT());
                }
            } catch (RemoteException ex) {
                //System.out.println("Someting wrong with this client..." + clientLabel);
            }

        }
        return flag;
    }

    /**
     * Beat for all servers in the list.
     *
     * @return
     */
    public boolean beatForAllServer() {
        boolean flag = true;
        for (int i = 0; i < ActiveServerList.size(); i++) {
            ActiveServer aServer = ActiveServerList.get(i);
            String serverLabel = aServer.getServerIP() + aServer.getPort();
            try {
                if (lookupServer(aServer) == false) {
                    System.out.println("Failed to look up " + serverLabel);
                } else {
                    aServer.getServerAPI().beatFromServer(ServerStart.getServerIP(), ServerStart.getPORT());
                }
            } catch (RemoteException ex) {
                //System.out.println("Someting wrong with this server..." + serverLabel);
            }

        }
        return flag;
    }

    /**
     * Put the operation in a loop to keep beating the servers and clients.
     */
    public void run() {
        int timeout = InternetUtil.getBEATINTERVAL() * 1000;
        while (runningFlag) {

            beatForAllClient();
            beatForAllServer();
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }

    }
}
