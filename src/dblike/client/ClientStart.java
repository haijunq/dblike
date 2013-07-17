/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.client;

import dblike.api.ClientAPI;
import dblike.client.service.ClientConfig;
import dblike.client.service.FileSyncClientService;
import dblike.server.service.FileSyncServerService;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wenhanwu
 */
public class ClientStart {

    private static Registry registry;
    private static String clientID = "001";
    private static String deviceID = "iphone";
    private static String clientIP = "127.0.0.1";
    private static int clientPort = 7860;
    //private static String serverIP = "23.23.129.199";
    private static String serverIP = "127.0.0.1";
    private static int serverPort = 1099;

    /**
     * @return the clientID
     */
    public static String getClientID() {
        return clientID;
    }

    /**
     * @param aClientID the clientID to set
     */
    public static void setClientID(String aClientID) {
        clientID = aClientID;
    }

    public static void bindForClient() {
        try {
            System.out.println("Client start at " + ClientConfig.getCurrentClient().getIp() + ":" + ClientConfig.getCurrentClient().getPort());
            System.out.println("Will connect to server " + ClientConfig.getServerList().get(0).getServerIP() + ":" + ClientConfig.getServerList().get(0).getPort());

            ClientImp client = new ClientImp();
            ClientAPI clientStub = (ClientAPI) UnicastRemoteObject.exportObject(client, 0);
            int cPort = Integer.parseInt(ClientConfig.getCurrentClient().getPort());
            registry = LocateRegistry.createRegistry(cPort);
            String clientBind = "clientUtility" + getClientID() + ClientConfig.getCurrentClient().getDeviceID() + ClientConfig.getCurrentClient().getIp() + ClientConfig.getCurrentClient().getPort();
            try {
                registry.bind(clientBind, clientStub);
            } catch (AlreadyBoundException ex) {
                Logger.getLogger(ClientStart.class.getName()).log(Level.SEVERE, null, ex);
            } catch (AccessException ex) {
                Logger.getLogger(ClientStart.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Already bind: " + "[" + clientBind + "]");
            System.out.println("Client ready");
        } catch (RemoteException ex) {
            Logger.getLogger(ClientStart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) throws IOException {
        ClientConfig.loadServerList();
        Client aClient = new Client(ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getServerIP(), ClientConfig.getServerList().get(ClientConfig.getCurrentServerIndex()).getPort());
        clientID = aClient.login();
        aClient.initData();
        bindForClient();
        
        // new thread to synchronize files 
        String directory = "E:\\Dropbox\\Course\\CICS525\\dblike\\test\\";
        FileSyncClientService fileSyncServer = new FileSyncClientService(directory);
        Thread fileSyncServerThread = new Thread(fileSyncServer);
        fileSyncServerThread.start();
    }
}
